package com.chromamorph.maxtranpatsjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

/**
 * NLBNCD
 * 
 * Calculates the normalized compression distance for each pair of tunes
 * in the annotated corpus of the Dutch Song Databank.
 * 
 * Stores the distances in a file, distances.txt, which is stored in the algorithm folder alongside
 * the NLB and NLB-PAIRS directories. These directories need to contain the full set of single files
 * and pair files.
 * 
 * 
 * @author David Meredith
 *
 */
public class MaxTranPatsNLBNCD {

//	static String algorithm = "F2STR-scalexia";
//	static String algorithm = "F2T";
	static String algorithm = "F2TR";

	public static void main(String[] args) {
		String rootNLBDirectory = "/Users/susanne/Repos/nlb20210504/20210827-output/";
		String nlbOutputDirectoryName = rootNLBDirectory+algorithm+"/NLB";
		String nlbPairDirectoryName = rootNLBDirectory+algorithm+"/NLB-PAIRS";
		String outputFileName = rootNLBDirectory+algorithm+"/distances.txt";

		String[] nlbPairFileList = new File(nlbPairDirectoryName).list();
		System.out.println("nlbPairFileList.length="+nlbPairFileList.length);
		String[] nlbOutputFileList = new File(nlbOutputDirectoryName).list();
		System.out.println("nlbOutputFileList.length="+nlbOutputFileList.length);

		TreeSet<String> nlbLogFileNames = new TreeSet<String>();
		for(String nlbLogFileName : nlbOutputFileList)
			if (nlbLogFileName.endsWith(".log") || (algorithm.startsWith("Forth") && nlbLogFileName.endsWith(".alltecs"))
					|| (algorithm.endsWith("TECCompress") && nlbLogFileName.endsWith("-TECCompress"))
					|| (algorithm.startsWith("F2") && nlbLogFileName.endsWith(".enc")))
				nlbLogFileNames.add(nlbLogFileName);
		
		System.out.println(nlbLogFileNames.size()+" log files found");

		TreeSet<String> nlbPairLogFileNames = new TreeSet<String>();
		for(String nlbPairLogFileName : nlbPairFileList)
			if (nlbPairLogFileName.endsWith(".log") || (algorithm.startsWith("Forth") && nlbPairLogFileName.endsWith(".alltecs"))
					|| (algorithm.endsWith("TECCompress") && nlbPairLogFileName.endsWith("-TECCompress"))
					|| (algorithm.startsWith("F2") && nlbPairLogFileName.endsWith(".enc")))
				nlbPairLogFileNames.add(nlbPairLogFileName);

		System.out.println(nlbPairLogFileNames.size()+" pair log files found");

		try {
			PrintStream outputStream = new PrintStream(outputFileName);
			for(String nlbPairLogFileName : nlbPairLogFileNames) {
				String songName1 = nlbPairLogFileName.substring(0,12);
				System.out.println("songName1 = "+songName1);
				String songName2 = nlbPairLogFileName.substring(13,25);
				System.out.println("songName2 = "+songName2);
				String fileName1 = nlbLogFileNames.ceiling(songName1);
				System.out.println("fileName1 = "+fileName1);
				String fileName2 = nlbLogFileNames.ceiling(songName2);
				System.out.println("fileName2 = "+fileName2);
				String fullFileName1 = nlbOutputDirectoryName + "/" + fileName1;
				System.out.println("fullFileName1 = "+fullFileName1);				
				String fullFileName2 = nlbOutputDirectoryName + "/" + fileName2;
				System.out.println("fullFileName2 = "+fullFileName2);				
				String fullPairFileName = nlbPairDirectoryName + "/" + nlbPairLogFileName;
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
}
