package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

public class GisselMHCalculateNCDs {

	static class Pair implements Comparable<Pair> {
		private int piece1, piece2, length, length1, length2;
		private double cf, cf1, cf2;

		public Pair(int piece1, int piece2, int length, int length1, int length2, double cf, double cf1, double cf2) {
			setPiece1(piece1);
			setPiece2(piece2);
			setLength(length);
			setLength1(length1);
			setLength2(length2);
			setCf(cf);
			setCf1(cf1);
			setCf2(cf2);
		}

		@Override
		public int compareTo(Pair o) {
			if (o == null) return 1;
			if (getNCD() < o.getNCD()) return -1;
			if (getNCD() > o.getNCD()) return 1;
			int d = getPiece1() - o.getPiece1();
			if (d != 0) return d;
			d = getPiece2() - o.getPiece2();
			if (d != 0) return d;
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Pair)) return false;
			return compareTo((Pair)obj)==0;
		}

		public double getNCD() {
			return (1.0 * (length - Math.min(length1,length2)))/(1.0 * Math.max(length1,length2));
		}

		public int getPiece1() {
			return piece1;
		}
		public void setPiece1(int piece1) {
			this.piece1 = piece1;
		}
		public int getPiece2() {
			return piece2;
		}
		public void setPiece2(int piece2) {
			this.piece2 = piece2;
		}
		public int getLength() {
			return length;
		}
		public void setLength(int length) {
			this.length = length;
		}
		public int getLength1() {
			return length1;
		}
		public void setLength1(int length1) {
			this.length1 = length1;
		}
		public int getLength2() {
			return length2;
		}
		public void setLength2(int length2) {
			this.length2 = length2;
		}
		public double getCf() {
			return cf;
		}
		public void setCf(double cf) {
			this.cf = cf;
		}
		public double getCf1() {
			return cf1;
		}
		public void setCf1(double cf1) {
			this.cf1 = cf1;
		}
		public double getCf2() {
			return cf2;
		}
		public void setCf2(double cf2) {
			this.cf2 = cf2;
		}

		public String toString() {
			return 
					getPiece1()+"\t"+
					getPiece2()+"\t"+
					getLength1()+"\t"+
					getLength2()+"\t"+
					getCf1()+"\t"+
					getCf2()+"\t"+
					getLength()+"\t"+
					getCf()+"\t"+
					getNCD();
		}
	}

	private static String inputPNGFileDirectoryPathName = "png";
	private static String inputTXTFileDirectoryPathName = "txt";
	private static String outputPTSFileDirectoryForPNGs = "ptsfrompng";
	private static String outputPTSFileDirectoryForTXTs = "ptsfromtxt";

	private static String inputFileDirectoryPathName = "ptsfrompng";
	private static String inputPairFileDirectoryPathName = "inputPairFiles";
	private static String singleFileOutputDirectoryPathName = "output/single";
	private static String pairFileOutputDirectoryPathName = "output/pairs";
	private static String argumentString = "-rrt -a COSIATEC";
	private static String distancesFilePathNameSortedByDistance = "output/distancesSortedByDistance-"+argumentString.replaceAll(" ", "");
	private static String distancesFilePathNameSortedByName = "output/distancesSortedByName-"+argumentString.replaceAll(" ", "");
	private static TreeSet<Pair> pairsSortedByDistance = new TreeSet<Pair>();
	private static ArrayList<Pair> pairsSortedByName = new ArrayList<Pair>();
	private static String groundTruthClassificationFilePathName = "labels.txt";
	private static int numPieces = 107;
	private static String loocvKnnClassificationFilePathName = "output/loocvknn.txt";
	private static int maxK = 31; //must be odd


	private static void convertPNGToPTS() {
		new File(outputPTSFileDirectoryForPNGs).mkdirs();
		String[] inputPNGFileNames = new File(inputPNGFileDirectoryPathName).list();
		for(String fn : inputPNGFileNames) {
			PointSet ps = new PointSet(inputPNGFileDirectoryPathName+"/"+fn);
			String outputFileName = fn.substring(0,fn.lastIndexOf('.')) + ".pts";
			try {
				ps.writeToPtsFile(outputPTSFileDirectoryForPNGs+"/"+outputFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void convertTXTToPTS() {
		new File(outputPTSFileDirectoryForTXTs).mkdirs();
		String[] inputTxtFileNames = new File(inputTXTFileDirectoryPathName).list();
		for(String fn : inputTxtFileNames) {
			PointSet ps = new PointSet(inputTXTFileDirectoryPathName+"/"+fn);
			String outputFileName = fn.substring(0,fn.lastIndexOf('.')) + ".pts";
			try {
				ps.writeToPtsFile(outputPTSFileDirectoryForTXTs+"/"+outputFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void comparePTSFiles() {
		String[] fileNames = new File(outputPTSFileDirectoryForPNGs).list();
		for(String s : fileNames) {
			PointSet ps1 = new PointSet(outputPTSFileDirectoryForPNGs+"/"+s);
			PointSet ps2 = new PointSet(outputPTSFileDirectoryForTXTs+"/"+s);
			if (!ps1.equals(ps2))
				System.out.println(s+" not equal");
		}
	}

	private static void compressIndividualFiles() {
		new File(singleFileOutputDirectoryPathName).mkdirs();
		String[] fileNames = new File(inputFileDirectoryPathName).list();
		for(String fn : fileNames) {
			String inputFilePathString = inputFileDirectoryPathName+"/"+fn;
			String[] args = (argumentString+" -i "+inputFilePathString+" -o "+singleFileOutputDirectoryPathName).split(" ");
			if (fn.endsWith(".pts"))
				OMNISIA.main(args);
		}
	}

	private static void compressPairFiles() {
		new File(pairFileOutputDirectoryPathName).mkdirs();
		String[] fileNames = new File(inputPairFileDirectoryPathName).list();
		Arrays.sort(fileNames);
		System.out.println("Number of pair files: "+fileNames.length);
		for(String fn : fileNames) {
			String inputFilePathString = inputPairFileDirectoryPathName+"/"+fn;
			String[] args = (argumentString+" -i "+inputFilePathString+" -o "+pairFileOutputDirectoryPathName).split(" ");
			if (fn.endsWith(".pts"))
				OMNISIA.main(args);
		}
	}

	private static void createPairFiles() {
		new File(inputPairFileDirectoryPathName).mkdirs();
		for(int i = 1; i < numPieces; i++)
			for(int j = i+1; j < numPieces+1; j++) {
				String pairFileName = i+"-"+j+".pts";
				String pairFilePathName = inputPairFileDirectoryPathName+"/"+pairFileName;
				PointSet ps1 = new PointSet(inputFileDirectoryPathName+"/"+i+".pts");
				PointSet ps2 = new PointSet(inputFileDirectoryPathName+"/"+j+".pts");
				long maxX = ps1.getMaxX();
				Vector vector = new Vector(2*maxX,0);
				ps1.addAll(ps2.translate(vector));
				try {
					ps1.writeToPtsFile(pairFilePathName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}

	public static void computePairsAndNCDs() {
		System.out.print("Finding output directory names...");
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return dir.toPath().resolve(name).toFile().isDirectory();
			}
		};
		String[] pairOutputDirectoryNames = new File(pairFileOutputDirectoryPathName).list(filter);
		String[] singleFileOutputDirectoryNames = new File(singleFileOutputDirectoryPathName).list(filter);
		System.out.println("DONE");
		System.out.println("Computing pairs:");
		for(String pairOutputDirectoryName : pairOutputDirectoryNames) {
			System.out.println("  "+pairOutputDirectoryName);
			String[] a = pairOutputDirectoryName.split("-");
			int piece1 = Integer.parseInt(a[0]);
			int piece2 = Integer.parseInt(a[1]);
			int length = 0, length1 = 0, length2 = 0;
			double cf = 0, cf1 = 0, cf2 = 0;
			String cosLogFilePathName = pairFileOutputDirectoryPathName+"/"+pairOutputDirectoryName+"/"+piece1+"-"+piece2+"-chrom.log";
			try {
				BufferedReader br = new BufferedReader(new FileReader(cosLogFilePathName));
				String l;
				while ((l = br.readLine()) != null) {
					if (l.startsWith("Encoding length: "))
						length = Integer.parseInt(l.substring("Encoding length: ".length()));
					else if (l.startsWith("Compression ratio: "))
						cf = Double.parseDouble(l.substring("Compression ratio: ".length()));
				}
				br.close();
				//				Find length1, length2, cf1 and cf2
				for(int piece : new int[]{piece1,piece2}) {
					String singleFileOutputDirName = null;
					for(String s : singleFileOutputDirectoryNames)
						if (s.startsWith(String.format("%d-", piece))) {
							singleFileOutputDirName = s;
							break;
						}
					String pieceCosLogFilePathName = singleFileOutputDirectoryPathName+"/"+singleFileOutputDirName+"/"+piece+"-chrom.log";
					br = new BufferedReader(new FileReader(pieceCosLogFilePathName));
					int len;
					double c;
					while ((l = br.readLine()) != null) {
						if (l.startsWith("Encoding length: ")) {
							len = Integer.parseInt(l.substring("Encoding length: ".length()));
							if (piece==piece1)
								length1 = len;
							else
								length2 = len;
						} else if (l.startsWith("Compression ratio: ")) {
							c = Double.parseDouble(l.substring("Compression ratio: ".length()));
							if (piece==piece1)
								cf1 = c;
							else
								cf2 = c;
						}
					}
					br.close();
				}
				pairsSortedByDistance.add(new Pair(piece1,piece2,length,length1,length2,cf,cf1,cf2));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Completed computing pairs");

		//		Make list of pairs sorted by name
		System.out.print("Making list of pairs sorted by name...");
		for(Pair pair : pairsSortedByDistance)
			pairsSortedByName.add(pair);
		Comparator<Pair> nameSortComparator = new Comparator<Pair>() {

			@Override
			public int compare(Pair o1, Pair o2) {
				if (o1==o2) return 0;
				if (o2 == null) return 1;
				if (o1 == null) return -1;
				int d = o1.getPiece1()-o2.getPiece1();
				if (d != 0) return d;
				return o1.getPiece2()-o2.getPiece2();
			}

		};
		Collections.sort(pairsSortedByName,nameSortComparator);
		System.out.println("DONE");

		try {
			//		Print out list of pairs sorted by distance

			System.out.print("Creating file containing list of pairs, sorted by distance...");
			PrintWriter pw;
			pw = new PrintWriter(new File(distancesFilePathNameSortedByDistance));
			for(Pair pair : pairsSortedByDistance)
				pw.println(pair);
			pw.close();
			System.out.println("DONE");

			//		Print out list of pairs sorted by name

			System.out.print("Creating file containing list of pairs, sorted by name...");
			pw = new PrintWriter(new File(distancesFilePathNameSortedByName));
			for(Pair pair : pairsSortedByName)
				pw.println(pair);
			pw.close();
			System.out.println("DONE");			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void computeLOOCVWithKNN(String maxKString) {
		maxK = Integer.parseInt(maxKString);
		System.out.println("maxK set to "+maxK);
		//		Get ground-truth classifications
		System.out.print("Loading ground-truth classifications...");
		int[] groundTruthClassifications = new int[numPieces+1];
		BufferedReader br;
		String l;
		try {
			br = new BufferedReader(new FileReader(groundTruthClassificationFilePathName));
			int i = 1;
			while((l = br.readLine()) != null)
				groundTruthClassifications[i++] = Integer.parseInt(l);
			br.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println("DONE");
		//		for(int i = 0; i < numPieces; i++)
		//			System.out.println(i+"\t"+groundTruthClassifications[i]);

		//		Read in pairs from distances file
		pairsSortedByDistance = new TreeSet<Pair>();
		try {
			System.out.print("Reading in distances from distances file...");
			br = new BufferedReader(new FileReader(distancesFilePathNameSortedByDistance));
			while((l = br.readLine()) != null) {
				String[] a = l.split("\t");
				Pair pair = new Pair(
						Integer.parseInt(a[0]), //piece1
						Integer.parseInt(a[1]), //piece2
						Integer.parseInt(a[6]), //length
						Integer.parseInt(a[2]), //length1
						Integer.parseInt(a[3]), //length2
						Double.parseDouble(a[7]), //cf
						Double.parseDouble(a[4]), //cf1
						Double.parseDouble(a[5])  //cf2
						);
				pairsSortedByDistance.add(pair);
			}
			br.close();
			//			for(Pair pair : pairsSortedByDistance)
			//				System.out.println(pair);
			System.out.println("DONE");
			
			
			Pair[] pairsArray = new Pair[pairsSortedByDistance.size()];
			pairsSortedByDistance.toArray(pairsArray);

//			Compute the knn classifications
			System.out.print("Computing knn classifications...");
			int[][] classification = new int[maxK+1][numPieces+1];
			for(int k = 1; k <= maxK; k += 2) {
				System.out.print("for k="+k+"...");
				for(int piece = 1; piece <= numPieces; piece++) {
					int[] knns = new int[k];
					int pos = -1; //position in the list of distances
					for(int i = 0; i < k; i++) { //position in knns array, the list of k classifications for this piece
						pos++;
						while (pos < pairsArray.length) {
							Pair pair = pairsArray[pos];
							if (pair.piece1==piece) {
								knns[i] = groundTruthClassifications[pair.piece2];
								break;
							} else if (pair.piece2==piece) {
								knns[i] = groundTruthClassifications[pair.piece1];
								break;
							}
							pos++;
						}
					}
//					Calculate knn classification for this piece
//					Each class label is either a 1 or a -1
//					So the computer label will be 1 iff the sum of the k labels is greater than 0.
					int sum = 0;
					for(int knn : knns)
						sum += knn;
					classification[k][piece] = sum<0?-1:1;
				}
			}
			System.out.println("DONE");
			
//			Print classifications to a file
			System.out.print("Printing classifications to a file...");
			PrintWriter pw = new PrintWriter(loocvKnnClassificationFilePathName);
			for(int piece = 1; piece <= numPieces; piece++) {
				String classString = "";
				for(int k = 1; k <= maxK; k += 2) {
					classString += (classification[k][piece] + ((k==maxK)?"":"\t"));
				}
				pw.println(classString);
			}
			System.out.println("DONE");
			
//			Print classification success rate to file
			System.out.print("Calculating classification success rates and numbers of classification errors...");
			int[] numCorrectClassifications = new int[maxK+1];
			for(int k = 1; k <= maxK; k += 2) {
				numCorrectClassifications[k] = 0;
				for(int piece = 1; piece <= numPieces; piece++)
					if (classification[k][piece]==groundTruthClassifications[piece])
						numCorrectClassifications[k]++;
				pw.print(numCorrectClassifications[k]);
				if (k != maxK) 
					pw.print("\t");
				else
					pw.println();
			}
			for(int k = 1; k <= maxK; k += 2) {
				pw.print(1.0*numCorrectClassifications[k]/numPieces);
				if (k != maxK) 
					pw.print("\t");
				else
					pw.println();
			}
			pw.close();
			System.out.println("DONE");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {
		//		convertPNGToPTS();
		//		convertTXTToPTS();
		//		comparePTSFiles();
		//		compressIndividualFiles();
		//		createPairFiles();
		//		compressPairFiles();
		//		computePairsAndNCDs();
		computeLOOCVWithKNN(args[0]);
	}
}
