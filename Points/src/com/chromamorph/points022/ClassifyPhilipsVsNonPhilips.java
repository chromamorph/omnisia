package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;

public class ClassifyPhilipsVsNonPhilips {

	private static String algorithm = "COSIATEC04";
	private static String inputFileDirectoryPath = "/Users/dave/Documents/Work/Research/Data/vanKranenburg/Diminutions/diminuties/krn-known";
	private static String filePairPTSFileDirectoryPath = "/Users/dave/Documents/Work/Research/Data/vanKranenburg/Diminutions/filepairs";
	private static String algorithmDirectoryPath = "/Users/dave/Documents/Work/Research/Data/vanKranenburg/Diminutions/output/"+algorithm;
	private static String outputDirectoryPathForSingleFiles = algorithmDirectoryPath+"/single-files";
	private static String outputDirectoryPathForFilePairs = algorithmDirectoryPath+"/file-pairs";
	private static String distancesFilePath = algorithmDirectoryPath+"/distances.txt";
	private static String[] inputFileNames = null;
	private static double[][] distanceMatrix = null;
	private static int numberOfCorrectClassifications = 0;

	private static void runCOSIATEC01(String inputFilePath, String outputDirectoryPath) {
		boolean 	diatonicPitch = true;
		boolean 	mirex = false;
		boolean 	withCompactnessTrawler = false;
		double 		a = 0.5;
		int 		b = 3;
		boolean 	forRSuperdiagonals = false;
		int 		r = 1;
		boolean 	removeRedundantTranslators = false;
		double		minTECCompactness = 0.0;
		int			minPatternSize = 0;
		boolean		mergeTECs = false;
		int 		minMatchSize = 5;
		int			numIterations = 10;
		String 		inputFilePathString = inputFilePath;
		String		outputFileDirectoryPathString = outputDirectoryPath;
		boolean		withoutChannel10 = true;


		try {
			new COSIATECEncoding(
					inputFilePathString, 
					outputFileDirectoryPathString, 
					diatonicPitch, 
					mirex,
					withCompactnessTrawler,
					a,
					b,
					forRSuperdiagonals,
					r,
					removeRedundantTranslators,
					minTECCompactness,
					minPatternSize,
					mergeTECs,
					minMatchSize,
					numIterations,
					withoutChannel10);
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnimplementedInputFileFormatException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}

	private static void runCOSIATEC02(String inputFilePath, String outputDirectoryPath) {
		boolean 	diatonicPitch = true;
		boolean 	mirex = false;
		boolean 	withCompactnessTrawler = true;
		double 		a = 0.5;
		int 		b = 3;
		boolean 	forRSuperdiagonals = true;
		int 		r = 1;
		boolean 	removeRedundantTranslators = true;
		double		minTECCompactness = 0.5;
		int			minPatternSize = 3;
		boolean		mergeTECs = false;
		int 		minMatchSize = 5;
		int			numIterations = 10;
		String 		inputFilePathString = inputFilePath;
		String		outputFileDirectoryPathString = outputDirectoryPath;
		boolean		withoutChannel10 = true;


		try {
			new COSIATECEncoding(
					inputFilePathString, 
					outputFileDirectoryPathString, 
					diatonicPitch, 
					mirex,
					withCompactnessTrawler,
					a,
					b,
					forRSuperdiagonals,
					r,
					removeRedundantTranslators,
					minTECCompactness,
					minPatternSize,
					mergeTECs,
					minMatchSize,
					numIterations,
					withoutChannel10);
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnimplementedInputFileFormatException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}

	private static void runCOSIATEC03(String inputFilePath, String outputDirectoryPath) {
		boolean 	diatonicPitch = true;
		boolean 	mirex = false;
		boolean 	withCompactnessTrawler = true;
		double 		a = 0.5;
		int 		b = 3;
		boolean 	forRSuperdiagonals = false;
		int 		r = 1;
		boolean 	removeRedundantTranslators = true;
		double		minTECCompactness = 0.5;
		int			minPatternSize = 3;
		boolean		mergeTECs = false;
		int 		minMatchSize = 5;
		int			numIterations = 10;
		String 		inputFilePathString = inputFilePath;
		String		outputFileDirectoryPathString = outputDirectoryPath;
		boolean		withoutChannel10 = true;


		try {
			new COSIATECEncoding(
					inputFilePathString, 
					outputFileDirectoryPathString, 
					diatonicPitch, 
					mirex,
					withCompactnessTrawler,
					a,
					b,
					forRSuperdiagonals,
					r,
					removeRedundantTranslators,
					minTECCompactness,
					minPatternSize,
					mergeTECs,
					minMatchSize,
					numIterations,
					withoutChannel10);
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnimplementedInputFileFormatException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}

	private static void runCOSIATEC04(String inputFilePath, String outputDirectoryPath) {
		boolean 	diatonicPitch = false;
		boolean 	mirex = false;
		boolean 	withCompactnessTrawler = false;
		double 		a = 0.5;
		int 		b = 3;
		boolean 	forRSuperdiagonals = false;
		int 		r = 1;
		boolean 	removeRedundantTranslators = false;
		double		minTECCompactness = 0.5;
		int			minPatternSize = 3;
		boolean		mergeTECs = false;
		int 		minMatchSize = 5;
		int			numIterations = 10;
		String 		inputFilePathString = inputFilePath;
		String		outputFileDirectoryPathString = outputDirectoryPath;
		boolean		withoutChannel10 = true;


		try {
			new COSIATECEncoding(
					inputFilePathString, 
					outputFileDirectoryPathString, 
					diatonicPitch, 
					mirex,
					withCompactnessTrawler,
					a,
					b,
					forRSuperdiagonals,
					r,
					removeRedundantTranslators,
					minTECCompactness,
					minPatternSize,
					mergeTECs,
					minMatchSize,
					numIterations,
					withoutChannel10);
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnimplementedInputFileFormatException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}

	
	private static void runSIATECCompress(String inputFilePath, String outputFileDirectoryPath) {
		boolean forRSuperdiagonals = false;
		int r = 3;
		boolean withCompactnessTrawler = false;
		double a = 0.6;
		int b = 3;
		int minPatternSize = 3;
		PitchRepresentation pitchRepresentation = PitchRepresentation.MORPHETIC_PITCH;
		boolean drawOutput = false;
		try {
			new SIATECCompressEncoding(
					inputFilePath, 
					outputFileDirectoryPath, 
					minPatternSize, 
					pitchRepresentation, 
					drawOutput, 
					withCompactnessTrawler, 
					a, 
					b,
					forRSuperdiagonals,
					r
					);
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnimplementedInputFileFormatException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}

	private static void compressFiles(String inputDirectoryPath, String outputDirectoryPath) {
		String[] inputFiles = new File(inputDirectoryPath).list();

		ArrayList<String> inputFileArrayList = new ArrayList<String>();
		for(String inputFile : inputFiles)
			if (inputFile.toLowerCase().endsWith(".krn") || inputFile.toLowerCase().endsWith(".pts"))
				inputFileArrayList.add(inputFile);

		for(String inputFile : inputFileArrayList) {
			String inputFilePath = inputDirectoryPath+"/"+inputFile;
			if (algorithm.equals("COSIATEC01"))
				runCOSIATEC01(inputFilePath,outputDirectoryPath);
			else if (algorithm.equals("COSIATEC02"))
				runCOSIATEC02(inputFilePath,outputDirectoryPath);
			else if (algorithm.equals("COSIATEC03"))
				runCOSIATEC03(inputFilePath,outputDirectoryPath);
			else if (algorithm.equals("SIATECCompress"))
				runSIATECCompress(inputFilePath,outputDirectoryPath);
			else if (algorithm.equals("COSIATEC04"))
				runCOSIATEC04(inputFilePath,outputDirectoryPath);
		}
	}

	private static void createFilePairFiles() {
		for(int i = 0; i < inputFileNames.length-1; i++) {
			for(int j = i+1; j < inputFileNames.length; j++) {
				System.out.print("Creating pair file for "+inputFileNames[i]+" and "+inputFileNames[j]+"...");
				PointSet ps1 = new PointSet(inputFileDirectoryPath+"/"+inputFileNames[i]);
				PointSet ps2 = new PointSet(inputFileDirectoryPath+"/"+inputFileNames[j]);
				PointSet ps = new PointSet();
				ps.addAll(ps1);
				ps.addAll(ps2.translate(new Vector(ps1.getMaxX()*2,0)));
				int endIndex = inputFileNames[i].lastIndexOf('.');
				String fileName1 = inputFileNames[i].substring(0, endIndex);
				endIndex = inputFileNames[j].lastIndexOf('.');
				String fileName2 = inputFileNames[j].substring(0, endIndex);
				String filePairPTSFilePath = filePairPTSFileDirectoryPath+"/"+fileName1+"-"+fileName2+".pts";
				new File(filePairPTSFileDirectoryPath).mkdirs();

				try {
					ps.writeToPtsFile(filePairPTSFilePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("DONE!");
			}
		}

	}

	private static Integer getZ(String fileName) {
		try {
			if (algorithm.startsWith("Forth")) {
				Integer encodingLength = getIntegerValueFromStringKey("Encoding length: ", fileName);
				Integer numberOfPointsInDataset = getIntegerValueFromStringKey("Number of points in dataset: ", fileName);
				Integer totalNumberOfPointsCovered = getIntegerValueFromStringKey("Total number of points covered: ", fileName);
				return encodingLength + numberOfPointsInDataset - totalNumberOfPointsCovered;
			} else {
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				String l = br.readLine();
				String s = "Encoding length: ";
				if (algorithm.equals("COSIATEC+BZIP2"))
					s = "BZIP file length in bytes: ";
				while (l != null && !l.startsWith(s))
					l = br.readLine();
				br.close();
				if (l == null)
					throw new NullPointerException("l is null in "+fileName);
				return Integer.parseInt(l.substring(s.length()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Could not find encoding length of file "+fileName);
		return null;
	}

	public static Integer getIntegerValueFromStringKey(String key, String filePath) throws IOException {
		BufferedReader br;
		br = new BufferedReader(new FileReader(filePath));
		String l = br.readLine();
		while (l != null && !l.startsWith(key))
			l = br.readLine();
		br.close();
		if (l == null)
			throw new NullPointerException("ERROR: Key, "+key+", does not occur in file, "+filePath+"!");
		Integer i = Integer.parseInt(l.substring(key.length()));
		//		System.out.println("String, "+l+", gives Integer value, "+i);
		return i;
	}

	public static void computeNCDs() {
		String singleFileOutputDirectory = outputDirectoryPathForSingleFiles;
		String filePairOutputDirectory = outputDirectoryPathForFilePairs;
		String distancesFile = distancesFilePath;

		String[] pairFileList = new File(filePairOutputDirectory).list();
		System.out.println("pairFileList.length="+pairFileList.length);
		String[] singleFileList = new File(singleFileOutputDirectory).list();
		System.out.println("singleFileList.length="+singleFileList.length);

		TreeSet<String> logFileNames = new TreeSet<String>();
		for(String logFileName : singleFileList)
			if (logFileName.endsWith(".log") || (algorithm.startsWith("Forth") && logFileName.endsWith(".alltecs"))
					|| (algorithm.endsWith("TECCompress") && logFileName.endsWith("TECCompress")))
				logFileNames.add(logFileName);

		System.out.println(logFileNames.size()+" log files found for single files");

		TreeSet<String> pairLogFileNames = new TreeSet<String>();
		for(String pairLogFileName : pairFileList)
			if (pairLogFileName.endsWith(".log") || (algorithm.startsWith("Forth") && pairLogFileName.endsWith(".alltecs"))
					|| (algorithm.endsWith("TECCompress") && pairLogFileName.endsWith("TECCompress")))
				pairLogFileNames.add(pairLogFileName);

		System.out.println(pairLogFileNames.size()+" log files found for pair files");

		try {
			PrintStream outputStream = new PrintStream(distancesFile);
			for(String pairLogFileName : pairLogFileNames) {
				String songName1 = pairLogFileName.substring(0,15);
				System.out.println("songName1 = "+songName1);
				String songName2 = pairLogFileName.substring(16,31);
				System.out.println("songName2 = "+songName2);
				String fileName1 = logFileNames.ceiling(songName1);
				System.out.println("fileName1 = "+fileName1);
				String fileName2 = logFileNames.ceiling(songName2);
				System.out.println("fileName2 = "+fileName2);
				String fullFileName1 = singleFileOutputDirectory + "/" + fileName1;
				System.out.println("fullFileName1 = "+fullFileName1);				
				String fullFileName2 = singleFileOutputDirectory + "/" + fileName2;
				System.out.println("fullFileName2 = "+fullFileName2);				
				String fullPairFileName = filePairOutputDirectory + "/" + pairLogFileName;
				System.out.println("fullPairFileName = " + fullPairFileName);
				/*
				 * e_z(x,y) = (Z(xy) - min{Z(x),Z(y)})/(max{Z(x),Z(y)})
				 * 
				 * Vitanyi and Li 2009, p. 664
				 */
				int zX = getZ(fullFileName1);
				int zY = getZ(fullFileName2);
				int zXY = getZ(fullPairFileName);

				double eZXY = (1.0 * (zXY - Math.min(zX,zY)))/(1.0 * Math.max(zX,zY));

				String s = songName1 + "\t" + songName2 + "\t" + String.format("%.4f",eZXY);
				outputStream.println(s);
				System.out.println(s);
			}
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void constructDistanceMatrix() {
		try{
			BufferedReader br = new BufferedReader(new FileReader(distancesFilePath));
			for(int i = 0; i < inputFileNames.length-1; i++) {
				for(int j = i+1; j < inputFileNames.length; j++) {
					String l = br.readLine();
					distanceMatrix[j][i] = distanceMatrix[i][j] = Double.parseDouble(l.split("\t")[2]);

				}
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < inputFileNames.length; i++)
			distanceMatrix[i][i] = 0;
	}

	static class Classification {
		String thisSong;
		int thisSongIndex;
		String mostSimilarOtherSong;
		int mostSimilarOtherSongIndex;
		double distance;

		public Classification(String thisSong, int thisSongIndex, String mostSimilarOtherSong, int mostSimilarOtherSongIndex, double distance) {
			this.thisSong = thisSong;
			this.thisSongIndex = thisSongIndex;
			this.mostSimilarOtherSong = mostSimilarOtherSong;
			this.mostSimilarOtherSongIndex = mostSimilarOtherSongIndex;
			this.distance = distance;
		}

		public String toString() {
			return thisSong + "\t"+mostSimilarOtherSong+"\t"+String.format("%.4f",distance);
		}
	}

	private static ArrayList<Classification> classifications = new ArrayList<Classification>();

	private static void classify() {
		for(int i = 0; i < inputFileNames.length; i++) {
			Double min = null;
			Integer minIndex = null;
			for(int j = 0; j < inputFileNames.length; j++) {
				if ((i != j) && (min == null || distanceMatrix[i][j] < min)) {
					min = distanceMatrix[i][j];
					minIndex = j;
				}
			}
			Classification cl = new Classification(inputFileNames[i],i,inputFileNames[minIndex],minIndex,min);
			classifications.add(cl);
		}
		for(Classification c : classifications) {
			String thisSongPrefix = c.thisSong.substring(0, 2);
			String mostSimilarSongPrefix = c.mostSimilarOtherSong.substring(0, 2);
			if (thisSongPrefix.equals(mostSimilarSongPrefix) || (!thisSongPrefix.equals("PP") && !mostSimilarSongPrefix.equals("PP"))) {
				numberOfCorrectClassifications++;
				System.out.println(numberOfCorrectClassifications+". "+c);
			}
		}
		System.out.println("Number of correct classifications = "+numberOfCorrectClassifications);
		System.out.println("Classification success rate = "+((1.0 * numberOfCorrectClassifications)/inputFileNames.length));
	}

	public static void main(String[] args) {
		String[] inputFileList = new File(inputFileDirectoryPath).list();
		TreeSet<String> inputFileTreeSet = new TreeSet<String>();
		for(String inputFile : inputFileList)
			if (inputFile.toLowerCase().endsWith(".krn"))
				inputFileTreeSet.add(inputFile);
		inputFileNames = new String[inputFileTreeSet.size()];
		inputFileTreeSet.toArray(inputFileNames);

		distanceMatrix = new double[inputFileNames.length][inputFileNames.length];
		new File(outputDirectoryPathForSingleFiles).mkdirs();
		new File(outputDirectoryPathForFilePairs).mkdirs();
		//		Create file pair files
		//		createFilePairFiles();
		//		Run compression algorithm on each file individually
		compressFiles(inputFileDirectoryPath,outputDirectoryPathForSingleFiles);
		//		Run compression algorithm on each distinct unordered pair of files
		compressFiles(filePairPTSFileDirectoryPath,outputDirectoryPathForFilePairs);
		//		Compute NCDs
		computeNCDs();
		//		Construct distance matrix
		constructDistanceMatrix();
		//		Classify using 1 nearest neighbour and leave-one-out cross validation
		classify();
	}
}
