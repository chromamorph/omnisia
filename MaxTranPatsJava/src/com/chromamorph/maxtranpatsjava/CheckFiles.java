package com.chromamorph.maxtranpatsjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class CheckFiles {

	public static TreeMap<String, Long> methodTimings = new TreeMap<String, Long>();
	public static String[] methodSignatures = new String[] {
			"computeMaximalTransformablePatterns",
			"computeSizeMTPSetArray",
			"computePatternTransformationSetPairs",
			"computeSuperMTPs",
			"computeHeterogeneousOccurrenceSets",
			"removeDuplicateOccurrenceSets",
			"removeRedundantTransformations",
			"removeOccurrenceSetsWithEmptyTransformationSets",
			"computeSortedOccurrenceSets",
			"computeEncoding",
	};


	public static int getValue(String line) {
		String[] a = line.trim().split(" ");
		return Integer.parseInt(a[a.length-1]);
	}

	public static int getValue(String line, int index) {
		String[] a = line.trim().split("\s+");
		//		for(String s : a) System.out.print("\""+s+"\", ");
		//		System.out.println();
		return Integer.parseInt(a[index]);
	}

	public static void initializeMethodTimings() {
		for(String methodSig : methodSignatures) {
			methodTimings.put(methodSig,0l);
		}

	}

	public static void checkCoveredSetEqualsPairFileDatasets(File outputFile, String datasetFileDirectory) throws Exception {
		int suffixStart = outputFile.getName().indexOf(".");
		String inputFilePath1 = datasetFileDirectory+"/"+outputFile.getName().substring(0,12)+".mid";
		String inputFilePath2 = datasetFileDirectory+"/"+outputFile.getName().substring(13,suffixStart)+".mid";
		//		System.out.println("  Input file path: "+inputFilePath);
		try {

			PointSet ps1 = new PointSet(
					new File(inputFilePath1), 
					true, 
					true,
					"1100");
			PointSet ps2 = new PointSet(
					new File(inputFilePath2), 
					true, 
					true,
					"1100");

			//			We're going to form the union of ps1 with ps2 translated by twice the maximum x-value of ps1
			Transformation tran = new Transformation(new F_2T(), Utility.makeSigma(ps1.getMax(0) * 2, 0));
			PointSet translatedPS2 = tran.phi(ps2);
			PointSet dataset = new PointSet();
			dataset.addAll(ps1);
			dataset.addAll(translatedPS2);

			//			PointSet dataset = new PointSet(new File(inputFilePath),true,true,"1100");
			Encoding encoding = new Encoding(outputFile, dataset);
			PointSet EMinusD = encoding.getCoveredSet().setMinus(dataset);
			PointSet DMinusE = dataset.setMinus(encoding.getCoveredSet());

			if (!EMinusD.isEmpty() || !DMinusE.isEmpty()) {
				System.out.println("\n"+outputFile.getName());
				System.out.println("\nDataset:\n" + dataset);
				System.out.println("Encoding covered set \\ dataset = "+EMinusD);
				System.out.println("Dataset \\ Encoding covered set = "+DMinusE);
				if (!EMinusD.isEmpty())
					for(OccurrenceSet os : encoding.getOccurrenceSets()) {
						PointSet osDiffSet = os.getCoveredSet().setMinus(dataset);
						if (!osDiffSet.isEmpty())
							System.out.println("OS: "+os+"\n  contains following points not in dataset: "+ osDiffSet);
					}
			}
		} catch (IOException | DimensionalityException e) {
			e.printStackTrace();
		}
	}

	public static void checkCoveredSetEqualsDataset(File outputFile, String datasetFileDirectory) throws Exception {
		int suffixStart = outputFile.getName().indexOf(".");
		String inputFilePath = datasetFileDirectory+"/"+outputFile.getName().substring(0,suffixStart)+".mid";
		//		System.out.println("  Input file path: "+inputFilePath);
		try {
			PointSet dataset = new PointSet(new File(inputFilePath),true,true,"1100");
			Encoding encoding = new Encoding(outputFile, dataset);
			PointSet EMinusD = encoding.getCoveredSet().setMinus(dataset);
			PointSet DMinusE = dataset.setMinus(encoding.getCoveredSet());

			if (!EMinusD.isEmpty() || !DMinusE.isEmpty()) {
				System.out.println("\n"+outputFile.getName());
				System.out.println("\nDataset:\n" + dataset);
				System.out.println("Encoding covered set \\ dataset = "+EMinusD);
				System.out.println("Dataset \\ Encoding covered set = "+DMinusE);
				if (!EMinusD.isEmpty())
					for(OccurrenceSet os : encoding.getOccurrenceSets()) {
						PointSet osDiffSet = os.getCoveredSet().setMinus(dataset);
						if (!osDiffSet.isEmpty())
							System.out.println("OS: "+os+"\n  contains following points not in dataset: "+ osDiffSet);
					}
			}
		} catch (IOException | DimensionalityException e) {
			e.printStackTrace();
		}
	}

	public static void printTransformationsAndExtraDatasetPoints(File outputFile, String datasetFileDirectory) throws Exception {
		int suffixStart = outputFile.getName().indexOf(".");
		String inputFilePath = datasetFileDirectory+"/"+outputFile.getName().substring(0,suffixStart)+".mid";
		System.out.println("  Input file path: "+inputFilePath);
		try {
			PointSet dataset = new PointSet(new File(inputFilePath),true,true,"1100");
			System.out.println("\nDataset:\n" + dataset);
			Encoding encoding = new Encoding(outputFile, dataset);
			PointSet diffSet = encoding.getCoveredSet().setMinus(dataset);
			System.out.println("Encoding covered set \\ dataset = "+diffSet);
			diffSet = dataset.setMinus(encoding.getCoveredSet());
			System.out.println("Dataset \\ Encoding covered set = "+diffSet);


			for(OccurrenceSet os : encoding.getOccurrenceSets()) {
				PointSet osDiffSet = os.getCoveredSet().setMinus(dataset);
				if (!osDiffSet.isEmpty())
					System.out.println("OS: "+os+"\n  contains following points not in dataset: "+ osDiffSet);
			}
		} catch (IOException | DimensionalityException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		initializeMethodTimings();
		//		String rootDirStr = "output/nlb-20210504/pair-files-F2T";// OK
		//		String rootDirStr = "output/nlb-20210504/single-files"; // Some STR files
		//		String rootDirStr = "output/nlb-20210504/pair-files-F2TR"; // OK
		//		String rootDirStr = "output/nlb-20210504/pair-files-F2STR"; // Two STR files.
		//		String rootDirStr = "output/nlb-20210504/single-files-with-quantization"; // Some STR files
		//		String rootDirStr = "output/nlb-20210504/pair-files-F2STR-with-quantization";
		//		String rootDirStr = "output/nlb-20210504/single-files-with-scalexia"; // Some STR files
		String rootDirStr = "output/nlb-20210504/pair-files-F2STR-with-scalexia-p50";
		String datasetFileDirectory = "data/nlb/nlb_datasets/annmidi";
		boolean pairFiles = true;


		File rootDir = new File(rootDirStr);
		System.out.println("rootDir is " + rootDir);
		String[] outputFileDirs = rootDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (new File(dir, name).isDirectory() && !name.startsWith("FAILED"));
			}

		});
		System.out.println("outputFileDirs has "+outputFileDirs.length+" elements");

		int numFilesChecked = 0;

		for(String outputFileDirStr : outputFileDirs) {
			File outputFileDir = new File(rootDir, outputFileDirStr);
			String[] outputFileNames = outputFileDir.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".enc");
				}

			});

			File outputFile = new File(outputFileDir, outputFileNames[0]);
			//			System.out.println(outputFile.getAbsolutePath());
			try {
				BufferedReader br;
				br = new BufferedReader(new FileReader(outputFile));
				int coverage = 0, numPoints = 0;
				String l = br.readLine();
				if (l == null || l.trim().isBlank())
					System.out.println("ERROR! File is empty: "+outputFile);
				else {
					while(l != null) {
						//						if (l.startsWith("Coverage"))
						//							coverage = getValue(l);
						//						if (l.startsWith("Number of points"))
						//							numPoints = getValue(l);
						for(String methodSig : methodSignatures) {
							if (l.contains(methodSig)) {
								//							System.out.println(l);
								Long newTime = methodTimings.get(methodSig) + getValue(l, 2);
								methodTimings.put(methodSig, newTime);			
								break;
							}
						}
						l = br.readLine();
					}
					//					if (coverage != numPoints) {
					//						System.out.println("ERROR! Coverage not equal to number of points in " + outputFileDirStr+": Coverage = "+coverage+", Number of points = "+numPoints);
					//						printTransformationsAndExtraDatasetPoints(outputFile,datasetFileDirectory);
					//					}
					if (pairFiles)
						checkCoveredSetEqualsPairFileDatasets(outputFile, datasetFileDirectory);
					else
						checkCoveredSetEqualsDataset(outputFile, datasetFileDirectory);
				}
				br.close();
				numFilesChecked++;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} 

		}
		System.out.println(numFilesChecked + " files checked");
		for(String methodSig : methodSignatures)
			System.out.println(String.format("%20d  %s", methodTimings.get(methodSig),methodSig));

		//	Find duplicate outputFileDirs
		ArrayList<String> ofds = new ArrayList<String>();
		for(String outputFileDirStr : outputFileDirs)
			ofds.add(outputFileDirStr);
		ofds.sort(null);
		ArrayList<String> dupes = new ArrayList<String>();
		int startTran = ofds.get(0).indexOf("F_");
		for(int i = ofds.size()-1; i >= 0; i--)
			for(int j = i - 1; j >= 0; j--) {
				if (ofds.get(j).substring(0,startTran-1).equals(ofds.get(i).substring(0,startTran-1))) {
					dupes.add(ofds.get(j));
				}
			}
		
		System.out.println("\n "+dupes.size()+" duplicate files:\n");
		for(String dupe : dupes) System.out.println(dupe);

	}
}
