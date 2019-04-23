package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * 
 * @author David Meredith
 * 
 * Running this file generates all result tables for 
 * JNMR paper on COSIATEC.
 *  
 */
public class JNMR2014Evaluation {

	private static String[] filter = {};
	private static String filterString = getFilterString();
	private static String nlbRootDirectoryPath = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB";
	private static String nlbGroundTruthFilePath = "/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/nlb_datasets/ann_labels.txt";
	private static String nlbResultsTableTexFilePath = "/Users/dave/Documents/Work/Research/Papers in preparation/2014/2014-03-31-ACM-MM-Orlando-3-7-November/2014-03-19-nlbresults"+filterString+".tex";

	private static String getFilterString() {
		if (filter.length==0) return "";
		StringBuilder sb = new StringBuilder(filter[0]);
		for(int i = 1; i < filter.length; i++)
			sb.append("-"+filter[i]);
		return sb.toString(); 
	}
	
	static class DistanceNode implements Comparable<DistanceNode> {
		String tune1, tune2, distanceString;
		double distance;

		DistanceNode(String distanceNodeString) {
			String[] array = distanceNodeString.split("\t");
			tune1 = array[0];
			tune2 = array[1];
			distanceString = array[2];
			distance = Double.parseDouble(distanceString);
		}

		DistanceNode(String tune1, String tune2, double distance) {
			this.tune1 = tune1;
			this.tune2 = tune2;
			this.distance = distance;
			this.distanceString = new Double(distance).toString();
		}

		@Override
		public int compareTo(DistanceNode n) {
			if (n == null) return 1;
			double d = distance-n.distance;
			if (d > 0) return 1;
			if (d < 0) return -1;
			int e = tune1.compareTo(n.tune1);
			if (e > 0) return 1;
			if (e < 0) return -1;
			return tune2.compareTo(n.tune2);
		}

		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof DistanceNode)) return false;
			return compareTo((DistanceNode)o) == 0;
		}

		public String toString() {
			return "DistanceNode("+tune1+", "+tune2+", "+distanceString+", "+distance+")";
		}
	}

	static class LabelledSong implements Comparable<LabelledSong>{
		String song, family;

		LabelledSong(String s) {
			String[] array = s.split("\t");
			song = array[0];
			family = array[1];
		}

		LabelledSong(String song, String family) {
			this.song = song;
			this.family = family;
		}

		@Override
		public String toString() {
			return "LabelledSong("+song+", "+family+")";
		}

		public int compareTo(LabelledSong s) {
			if (s == null) return 1;
			int d = song.compareTo(s.song);
			if (d != 0) return d;
			return family.compareTo(s.family);
		}

		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof LabelledSong)) return false;
			return compareTo((LabelledSong)o) == 0;
		}
	}

	//	private static double getCR(String nlbOutputLogFile, Algorithm algorithm) {
	//		double cr = 0.0;
	//		try {
	//			BufferedReader br;
	//			br = new BufferedReader(new FileReader(nlbOutputDirectory+"/"+nlbOutputLogFile));
	//			String s = "Compression ratio: ";
	//			String l = br.readLine();
	//			while (!l.startsWith(s))
	//				l = br.readLine();
	//			cr = Double.parseDouble(l.substring(s.length()));
	//			br.close();
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//		return cr;
	//	}

	private static double computeLeaveOneOutSuccessRate(String algorithm) {
		try {
			TreeSet<DistanceNode> distanceNodes = new TreeSet<DistanceNode>();
			String distanceFile = nlbRootDirectoryPath+"/"+algorithm+"/distances.txt";
			BufferedReader br = new BufferedReader(new FileReader(distanceFile));
			String l = br.readLine().trim();
			while(l != null) {
				distanceNodes.add(new DistanceNode(l));
				l = br.readLine();
			}
			br.close();

			//Read all songs in annotated corpus into a list
			br = new BufferedReader(new FileReader(nlbGroundTruthFilePath));
			l = br.readLine();
			TreeSet<LabelledSong> songList = new TreeSet<LabelledSong>();
			while (l != null) {
				songList.add(new LabelledSong(l));
				l = br.readLine();
			}
			br.close();

			//For each song in the annotated corpus, descend this sorted distanceNodes list
			//until we get a pair of different songs that contains the query song.
			int tp = 0;
			for(LabelledSong s : songList) {
				String tune1 = s.song;
				String tune2 = null;
				for(DistanceNode n: distanceNodes) {
					if (tune1.equals(n.tune1) && !tune1.equals(n.tune2)) {
						tune2 = n.tune2;
						break;
					}
					if (tune1.equals(n.tune2) && !tune1.equals(n.tune1)) {
						tune2 = n.tune1;
						break;
					}
				}
				LabelledSong nearestNeighbourSong = songList.ceiling(new LabelledSong(tune2,""));
				if (nearestNeighbourSong.family.equals(s.family))
					tp++;
			}
			return (1.0*tp)/songList.size();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("ERROR in computeLeaveOneOutSuccessRate");
		return 0.0;
	}

	private static double computeMeanCROnAC(String algorithm) throws IOException {
		//Get list of compressed file names
		String[] nlbFileNames = new File(nlbRootDirectoryPath+"/"+algorithm+"/NLB").list();
		ArrayList<String> compressedFileNames = new ArrayList<String>();
		for(String nlbFileName : nlbFileNames) {
			if (algorithm.equals("SIACTTECCompress") || (algorithm.equals("SIARTECCompress")) || (algorithm.equals("SIARCTTECCompress"))) {
				if (nlbFileName.endsWith("-TECCompress"))
					compressedFileNames.add(nlbRootDirectoryPath+"/"+algorithm+"/NLB/"+nlbFileName);
			} else if (algorithm.startsWith("Forth")) {
				if (nlbFileName.endsWith(".alltecs"))
					compressedFileNames.add(nlbRootDirectoryPath+"/"+algorithm+"/NLB/"+nlbFileName);
			} else if (algorithm.equals("SIATECCompress")) {
				if (nlbFileName.endsWith(".SIATECCompress"))
					compressedFileNames.add(nlbRootDirectoryPath+"/"+algorithm+"/NLB/"+nlbFileName);
			} else if (nlbFileName.endsWith(".log"))
				compressedFileNames.add(nlbRootDirectoryPath+"/"+algorithm+"/NLB/"+nlbFileName);
		}
		if (compressedFileNames.size() != 360)
			System.out.println("ERROR: compressedFileNames is the wrong size (should be 360 actually "+compressedFileNames.size()+")");
		if (algorithm.startsWith("Forth"))
			return computeMeanCRForForth(compressedFileNames);
		String logFileKey = "Compression ratio: ";
		if (algorithm.equals("COSIATEC+BZIP2"))
			logFileKey = "Compression ratio from PointSet to BZIP (in bytes): ";
		return computeMeanCR(compressedFileNames, logFileKey);
	}

	private static double computeMeanCRForForth(ArrayList<String> filePaths) throws IOException {
		double sum = 0.0;
		for(String filePath : filePaths) {
			Integer encodingLength = NLBNCD.getIntegerValueFromStringKey("Encoding length: ", filePath);
			Integer numberOfPointsInDataset = NLBNCD.getIntegerValueFromStringKey("Number of points in dataset: ", filePath);
			Integer totalNumberOfPointsCovered = NLBNCD.getIntegerValueFromStringKey("Total number of points covered: ", filePath);
			double cr = (1.0*numberOfPointsInDataset)/(1.0*encodingLength+numberOfPointsInDataset-totalNumberOfPointsCovered);
			sum += cr;
		}
		return sum/filePaths.size();
	}

	private static double computeMeanCROnPairFiles(String algorithm) throws IOException {
		//Get list of compressed file names
		String[] nlbPairFiles = new File(nlbRootDirectoryPath+"/"+algorithm+"/NLB-PAIRS").list();
		ArrayList<String> compressedFileNames = new ArrayList<String>();
		for(String pairFile : nlbPairFiles) {
			if (algorithm.equals("SIACTTECCompress") || algorithm.equals("SIARTECCompress") || algorithm.equals("SIARCTTECCompress")) {
				if (pairFile.endsWith("-TECCompress"))
					compressedFileNames.add(nlbRootDirectoryPath+"/"+algorithm+"/NLB-PAIRS/"+pairFile);
			} else if (algorithm.startsWith("Forth")) {
				if (pairFile.endsWith(".alltecs"))
					compressedFileNames.add(nlbRootDirectoryPath+"/"+algorithm+"/NLB-PAIRS/"+pairFile);
			} else if (algorithm.equals("SIATECCompress")) {
				if (pairFile.endsWith(".SIATECCompress")) 
					compressedFileNames.add(nlbRootDirectoryPath+"/"+algorithm+"/NLB-PAIRS/"+pairFile);
			} else if (pairFile.endsWith(".log"))
				compressedFileNames.add(nlbRootDirectoryPath+"/"+algorithm+"/NLB-PAIRS/"+pairFile);
		}
		int n = 360 * 359 / 2;
		if (compressedFileNames.size() != n)
			System.out.println("ERROR: compressedFileNames is the wrong size (should be "+n+" actually "+compressedFileNames.size()+")");
		if (algorithm.startsWith("Forth"))
			return computeMeanCRForForth(compressedFileNames);
		String logFileKey = "Compression ratio: ";
		if (algorithm.equals("COSIATEC+BZIP2"))
			logFileKey = "Compression ratio from PointSet to BZIP (in bytes): ";
		return computeMeanCR(compressedFileNames, logFileKey);
	}

	private static double computeMeanCR(ArrayList<String> filePaths, String s) {
		double sumOfCRs = 0.0;
		int n = 0;
		for(String fullFilePathName : filePaths) {
			if (n > 0 && n%1000==0) System.out.print(".");
			if ((n==10000 || ((n-10000)%50000==0))) System.out.println();
			try {
				BufferedReader br = new BufferedReader(new FileReader(fullFilePathName));
				String l = br.readLine();
				while (l != null && !l.startsWith(s))
					l = br.readLine();
				if (l == null)
					System.out.println("ERROR: Could not find Compression ratio for file "+ fullFilePathName);
				else {
					double thisCR = Double.parseDouble(l.trim().substring(s.length()));
					sumOfCRs += thisCR;
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			n++;
		}
		return (sumOfCRs/filePaths.size());
	}

	private static void writeCSVFile(ArrayList<String> algorithms, 
			ArrayList<Double> leaveOneOutSuccessRates, 
			ArrayList<Double> meanCRsOnAC,
			ArrayList<Double> meanCRsOnPairFiles) {
		try {
			String[] headers = {"Algorithm","1NNLOO", "MeanCROnAC", "MeanCROnPairFiles"};
			String csvFilePathName = nlbRootDirectoryPath+"/Results"+filterString+".csv";
			PrintStream ps = new PrintStream(csvFilePathName);
			ps.print(headers[0]);
			for(int i = 1; i < headers.length; i++)
				ps.print(","+headers[i]);
			ps.println();
			for(int row = 0; row < algorithms.size(); row++) {
				ps.println(algorithms.get(row)+","
						+String.format("%.2f",leaveOneOutSuccessRates.get(row))+","
						+String.format("%.2f",meanCRsOnAC.get(row))+","
						+String.format("%.2f",meanCRsOnPairFiles.get(row)));
			}
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void writeNlbResultsTexFile(ArrayList<String> algorithms, 
			ArrayList<Double> leaveOneOutSuccessRates, 
			ArrayList<Double> meanCRsOnAC,
			ArrayList<Double> meanCRsOnPairFiles) {
		try {
			String[] headers = {"Algorithm","1-NN Leave-one-out SR", "Mean CR on AC", "Mean CR on pair files"};

			PrintStream ps = new PrintStream(nlbResultsTableTexFilePath);
			ps.print("\\begin{table}\\centering\\begin{tabular}");

			//Write column alignment string
			StringBuilder colAlStr = new StringBuilder("{l");
			for(int i = 0; i < headers.length-1; i++)
				colAlStr.append("|c");
			colAlStr.append("}");
			ps.println(colAlStr.toString());

			//Write header row
			StringBuilder sb = new StringBuilder("{\\em "+headers[0]+"} ");
			for(int i = 1; i < headers.length; i++)
				sb.append("& {\\em "+headers[i]+"}");
			sb.append("\\\\ \\hline");
			ps.println(sb.toString());

			//Write body of table
			for(int row = 0; row < algorithms.size(); row++) {
				ps.println(algorithms.get(row)+" & "
						+String.format("%.4f",leaveOneOutSuccessRates.get(row))+" & "
						+String.format("%.4f",meanCRsOnAC.get(row))+" & "
						+String.format("%.4f",meanCRsOnPairFiles.get(row)) + "\\\\");
			}
			ps.print("\\hline \\end{tabular}\\caption{Results on NLB.}\\label{nlbresults}\\end{table}");
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		ArrayList<String> filterList = new ArrayList<String>();
		for(String alg : filter) filterList.add(alg);
		String[] nlbRootDirItems = new File(nlbRootDirectoryPath).list();

		System.out.print("Finding algorithms...");
		ArrayList<String> algorithms = new ArrayList<String>();
		for(String nlbRootDirItem : nlbRootDirItems)
			if (!nlbRootDirItem.startsWith(".") 
					&& new File(nlbRootDirectoryPath+"/"+nlbRootDirItem).isDirectory()
					&& (filter.length==0 || filterList.contains(nlbRootDirItem)))
				algorithms.add(nlbRootDirItem);
		System.out.println("DONE\n\nAlgorithms:");
		for(String algorithm : algorithms)
			System.out.println(" "+algorithm);
		System.out.println();

		System.out.print("Finding leave-one-out success rates...");
		ArrayList<Double> leaveOneOutSuccessRates = new ArrayList<Double>();
		for(String algorithm : algorithms) {
			System.out.println("  for "+algorithm);
			leaveOneOutSuccessRates.add(computeLeaveOneOutSuccessRate(algorithm));
		}
		System.out.println("DONE");		

		System.out.print("Finding mean CRs on annotated corpus...");
		ArrayList<Double> meanCRsOnAC = new ArrayList<Double>();
		for(String algorithm : algorithms){
			System.out.println("  for "+algorithm);
			meanCRsOnAC.add(computeMeanCROnAC(algorithm));
		}
		System.out.println("DONE");		

		System.out.print("Finding mean CRs on pair files...");
		ArrayList<Double> meanCRsOnPairFiles = new ArrayList<Double>();
		for(String algorithm : algorithms) {
			System.out.println("for "+algorithm);
			meanCRsOnPairFiles.add(computeMeanCROnPairFiles(algorithm));
		}
		System.out.println("DONE");		

		writeCSVFile(algorithms,leaveOneOutSuccessRates,meanCRsOnAC,meanCRsOnPairFiles);
		writeNlbResultsTexFile(algorithms,leaveOneOutSuccessRates,meanCRsOnAC,meanCRsOnPairFiles);

		//		for(String distanceFile : distanceFiles) {
		//
		//
		//			//Find average compression ratio in NLB directory
		//			String[] nlbOutputFiles = new File(nlbOutputDirectory).list();
		//			
		//			ArrayList<String> nlbOutputLogFiles = new ArrayList<String>();
		//			for(String nlbOutputFile : nlbOutputFiles)
		//				if (nlbOutputFile.endsWith(".log"))
		//					nlbOutputLogFiles.add(nlbOutputFile);
		//			
		//			double sumOfCRs = 0.0;
		//			double sumOfCrsWithoutRPS = 0.0;
		//			for(String nlbOutputLogFile : nlbOutputLogFiles) {
		//				sumOfCRs += getCR(nlbOutputLogFile);
		//				sumOfCrsWithoutRPS += getCRWithoutRPS(nlbOutputLogFile);
		//			}
		//			
		//			System.out.println("Mean CR = "+sumOfCRs/nlbOutputLogFiles.size());
		//			System.out.println("Mean CR excluding residual point set = "+sumOfCrsWithoutRPS/nlbOutputLogFiles.size());
		//		}
	}	
}
