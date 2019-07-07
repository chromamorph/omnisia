package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MML2019OptisiaEvaluateJKUPDDOutput {
	private static String JKU_PDD_OUTPUT_DIRECTORY_PATH = "D:\\Dropbox\\Work\\Research\\Papers in preparation\\2019-06-28-MML-2019-OPTISIA\\optisia\\output\\JKU-PDD-m";
	private static String RESULTS_OUTPUT_DIRECTORY_PATH = "D:\\Dropbox\\Work\\Research\\Papers in preparation\\2019-06-28-MML-2019-OPTISIA\\optisia\\result-tables\\JKU-PDD";
	private static String GROUND_TRUTH_DIRECTORY_PATH = "RecurSIA-RRT-experiment/JKU-PDD/JKUPDD-noAudio-Aug2013/groundTruth";
	private static String[] INPUT_FILE_PREFIXES = {"wtc2f20","sonata01-3","mazurka24-4","silverswan","sonata04-2"};
	private static String[] OUTPUT_FILE_PREFIXES = {"bach_wtc2f20","beet_sonata01-3","chop_mazurka24-4","gbns_silverswan","mzrt_sonata04-2"};
	private static String[] GROUND_TRUTH_DIRECTORY_NAMES = {"bachBWV889Fg","beethovenOp2No1Mvt3","chopinOp24No4","gibbonsSilverSwan1612","mozartK282Mvt2"};
	private static String[] COMPOSERS = {"Bach", "Beethoven", "Chopin", "Gibbons", "Mozart"};
//	private static double[] OCCURRENCE_THRESHOLDS = {0.5,0.75};

	static class Result {
		int nP, nQ;
		double P, R, F1, PEst, REst, F1Est, P3, R3, TLF1, FFP, FFTPEst, cf;
		double[] POcc, ROcc, F1Occ;
		long runtime, frt;

		public String toString() {
			StringBuilder sb = new StringBuilder();
//			sb.append(String.format("%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%d,%d",nP,nQ,P,R,F1,PEst,REst,F1Est,P3,R3,TLF1,FFP,FFTPEst,frt,runtime));
			sb.append(String.format("%d,%d,%.4f,%.4f,%.4f,%.4f,%d,%.4f",nP,nQ,P3,R3,TLF1,cf,runtime,FFP));
//			for(int i = 0; i < POcc.length; i++) {
//				sb.append(String.format(",%.4f,%.4f,%.4f",POcc[i],ROcc[i],F1Occ[i]));
//			}
			return sb.toString();
		}
		
		public static String getHeaderString() {
			return "nP,nQ,P3,R3,TLF1,cf,runtime,FFP";
		}
	}

	public static String getValueFromEncodingFile(String encodingFilePath, String keyString) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(encodingFilePath));
			String l = br.readLine();
			while (!l.startsWith(keyString)) l = br.readLine();
			String[] a = l.split(" ");
			br.close();
			return a[1];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void run() {

		//First make a list of all the full paths of all the MIREX output files to be evaluated 
		ArrayList<String> outputFilePaths = new ArrayList<String>();
		ArrayList<String> encodingFilePaths = new ArrayList<String>();
		String[] outputAlgorithmDirectoryNames = new File(JKU_PDD_OUTPUT_DIRECTORY_PATH).list();

		for(String outputAlgorithmDirectoryName : outputAlgorithmDirectoryNames) {
			String outputAlgorithmDirectoryPath = JKU_PDD_OUTPUT_DIRECTORY_PATH+"/"+outputAlgorithmDirectoryName;
			System.out.println(outputAlgorithmDirectoryPath);
			if (!outputAlgorithmDirectoryPath.startsWith(".") && new File(outputAlgorithmDirectoryPath).isDirectory()) {
				String[] outputFileNames = new File(outputAlgorithmDirectoryPath).list();
				for(String outputFileName : outputFileNames) {
					for(String prefix : OUTPUT_FILE_PREFIXES) {
						if (outputFileName.startsWith(prefix) && outputFileName.endsWith(".txt")) {
							outputFilePaths.add(outputAlgorithmDirectoryPath+"/"+outputFileName);
							String encodingFileName = outputFileName.substring(0,outputFileName.lastIndexOf("."))+".out";
							encodingFilePaths.add(outputAlgorithmDirectoryPath+"/"+encodingFileName);
							break;
						}
					}
				}
			}
		}

		System.out.println("OUTPUT FILE PATHS:");
		for(String outputFilePath : outputFilePaths)
			System.out.println(outputFilePath);
		
		System.out.println("ENCODING FILE PATHS:");
		for(String encodingFilePath : encodingFilePaths)
			System.out.println(encodingFilePath);
		
		//Now make a list of the full paths of all the corresponding Lisp input files

		ArrayList<String> lispInputFilePaths = new ArrayList<String>();
		for(String outputFilePath : outputFilePaths) {
			String groundTruthFolderName = null;
			String inputFilePrefix = null;
			for(int i = 0; i < OUTPUT_FILE_PREFIXES.length; i++) {
				String outputFilePrefix = OUTPUT_FILE_PREFIXES[i];
				if (outputFilePath.contains(outputFilePrefix)) {
					groundTruthFolderName = GROUND_TRUTH_DIRECTORY_NAMES[i];
					inputFilePrefix = INPUT_FILE_PREFIXES[i];
				}
			}
			String lispInputFilePath = GROUND_TRUTH_DIRECTORY_PATH+"/"+groundTruthFolderName+"/polyphonic/lisp/"+inputFilePrefix+".txt";
			lispInputFilePaths.add(lispInputFilePath);
		}

		for(int i = 0; i < lispInputFilePaths.size(); i++) {
			System.out.println(outputFilePaths.get(i));
			System.out.println(lispInputFilePaths.get(i));
			System.out.println(encodingFilePaths.get(i));
			System.out.println();
		}

		//Calculate measures for all output files and store in a list
		ArrayList<Result> results = new ArrayList<Result>();
		for(int i = 0; i < lispInputFilePaths.size(); i++) {
			String mirexCosFilePath = outputFilePaths.get(i);
			System.out.println("mirexCosFilePath = "+mirexCosFilePath);System.out.flush();
			String encodingFilePath = encodingFilePaths.get(i);
			String lispInputFilePath = lispInputFilePaths.get(i);
			System.out.println("lispInputFilePath = "+lispInputFilePath);System.out.flush();
			ArrayList<ArrayList<PointSet>> computedOccurrenceSets = MIREX2013Entries.readMIREXOutputFile(mirexCosFilePath,lispInputFilePath);
			System.out.println("computedOccurrenceSets contains "+computedOccurrenceSets.size()+" occurrence sets");System.out.flush();
			ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets = EvaluateMIREX2013.readGroundTruthOccurrenceSets(lispInputFilePath);
			System.out.println("groundTruthOccurrenceSets contains "+groundTruthOccurrenceSets.size()+" occurrence sets");System.out.flush();
			Result result = new Result();
			result.nP = groundTruthOccurrenceSets.size();
			result.nQ = computedOccurrenceSets.size();

			//Calculate simple P, R and F1
//			result.P = EvaluateMIREX2013.getBasicP(groundTruthOccurrenceSets,computedOccurrenceSets);
//			result.R = EvaluateMIREX2013.getBasicR(groundTruthOccurrenceSets, computedOccurrenceSets);
//			result.F1 = EvaluateMIREX2013.calculateF1Score(result.P,result.R);
//
//			double[][] establishmentMatrix = new double[result.nP][result.nQ];
//
//			for(int emCol = 0; emCol < computedOccurrenceSets.size(); emCol++) {
//				ArrayList<PointSet> computedOccurrenceSet = computedOccurrenceSets.get(emCol);
//				for(int emRow = 0; emRow < groundTruthOccurrenceSets.size(); emRow++) {
//					ArrayList<PointSet> groundTruthOccurrenceSet = groundTruthOccurrenceSets.get(emRow);
//					double[][] scoreMatrix = EvaluateMIREX2013.calculateScoreMatrix(groundTruthOccurrenceSet,computedOccurrenceSet);
//					//Now find the max entry in the score matrix and store this in the establishment matrix.
//					establishmentMatrix[emRow][emCol] = EvaluateMIREX2013.getMaxValue(scoreMatrix);
//				}	
//			}
//
//			result.PEst = EvaluateMIREX2013.calculatePrecision(establishmentMatrix);
//			result.REst = EvaluateMIREX2013.calculateRecall(establishmentMatrix);
//			result.F1Est = EvaluateMIREX2013.calculateF1Score(result.PEst,result.REst);
//			result.FFTPEst = EvaluateMIREX2013.getFFTPEst(establishmentMatrix);
//			result.runtime = EvaluateMIREX2013.readRunTime(timingFilePath);
//			result.frt = result.runtime;
//
//			result.F1Occ = new double[OCCURRENCE_THRESHOLDS.length];
//			result.POcc = new double[OCCURRENCE_THRESHOLDS.length];
//			result.ROcc = new double[OCCURRENCE_THRESHOLDS.length];
//			for(int j = 0; j < OCCURRENCE_THRESHOLDS.length; j++) {
//				double occurrence_threshold = OCCURRENCE_THRESHOLDS[j];
//				double[][] precisionOccurrenceMatrix = new double[result.nP][result.nQ]; //All entries automatically initialized to 0.0
//				double[][] recallOccurrenceMatrix = new double[result.nP][result.nQ];
//				double[][] f1ScoreOccurrenceMatrix = new double[result.nP][result.nQ];
//
//				for(int emRow = 0; emRow < result.nP; emRow++)
//					for(int emCol = 0; emCol < result.nQ; emCol++) {
//						if (establishmentMatrix[emRow][emCol] >= occurrence_threshold) {
//							double[][] scoreMatrix = EvaluateMIREX2013.calculateScoreMatrix(groundTruthOccurrenceSets.get(emRow),computedOccurrenceSets.get(emCol));
//							precisionOccurrenceMatrix[emRow][emCol] = EvaluateMIREX2013.calculatePrecision(scoreMatrix);
//							recallOccurrenceMatrix[emRow][emCol] = EvaluateMIREX2013.calculateRecall(scoreMatrix);
//							f1ScoreOccurrenceMatrix[emRow][emCol] = EvaluateMIREX2013.calculateF1Score(precisionOccurrenceMatrix[emRow][emCol],recallOccurrenceMatrix[emRow][emCol]);
//						}
//					}
//
//				result.POcc[j] = EvaluateMIREX2013.calculatePrecision(precisionOccurrenceMatrix, true);
//				result.ROcc[j] = EvaluateMIREX2013.calculateRecall(recallOccurrenceMatrix,true);
//				result.F1Occ[j] = EvaluateMIREX2013.calculateF1Score(result.POcc[j],result.ROcc[j]);
//			}

			System.out.print("Calling getP3 on "+groundTruthOccurrenceSets.size()+" ground-truth occurrence sets and "+computedOccurrenceSets.size()+" computed occurrence sets: ");
			System.out.flush();
			result.P3 = EvaluateMIREX2013.getP3(groundTruthOccurrenceSets,computedOccurrenceSets);
			System.out.println(result.P3);
			System.out.flush();
			System.out.print("Calling getR3 on "+groundTruthOccurrenceSets.size()+" ground-truth occurrence sets and "+computedOccurrenceSets.size()+" computed occurrence sets: ");
			System.out.flush();
			result.R3 = EvaluateMIREX2013.getR3(groundTruthOccurrenceSets,computedOccurrenceSets);
			System.out.println(result.R3);
			System.out.flush();
			System.out.print("Calculating F1 for "+groundTruthOccurrenceSets.size()+" ground-truth occurrence sets and "+computedOccurrenceSets.size()+" computed occurrence sets: ");
			System.out.flush();
			result.TLF1 = (2*result.P3*result.R3)/(result.P3+result.R3);
			System.out.println(result.TLF1);
			System.out.flush();
			System.out.print("Calculating FFP for "+groundTruthOccurrenceSets.size()+" ground-truth occurrence sets and "+computedOccurrenceSets.size()+" computed occurrence sets: ");
			System.out.flush();
			result.FFP = EvaluateMIREX2013.getP3(groundTruthOccurrenceSets,computedOccurrenceSets,5);
			System.out.println(result.FFP);
			System.out.flush();
			
			result.cf = Double.parseDouble(getValueFromEncodingFile(encodingFilePath, "compressionRatio"));
			result.runtime = Long.parseLong(getValueFromEncodingFile(encodingFilePath, "runningTime"));
			results.add(result);
		}

		System.out.println("FINISHED CALCULATING RESULTS"); System.out.flush();
		
		//Store results in a CSV file that can be loaded into Excel
		Calendar cal = Calendar.getInstance();
		System.out.println("GOT HERE"); System.out.flush();		
		String dateString=String.format("%4d%02d%02d%02d%02d",cal.get(Calendar.YEAR),1+cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
		try {
			PrintStream csvFile = new PrintStream(RESULTS_OUTPUT_DIRECTORY_PATH+"/"+dateString+"-results.csv");
			StringBuilder sb = new StringBuilder();
//			sb.append("file,nP,nQ,P,R,F1,PEst,REst,F1Est,P3,R3,TLF1,FFP,FFTPEst,frt,runtime");
			sb.append("algorithm,file,");
			sb.append(Result.getHeaderString());
//			for(int i = 0; i < OCCURRENCE_THRESHOLDS.length; i++)
//				sb.append(String.format(",POcc(%.2f),ROcc(%.2f),F1Occ(%.2f)",OCCURRENCE_THRESHOLDS[i],OCCURRENCE_THRESHOLDS[i],OCCURRENCE_THRESHOLDS[i]));
			String columnHeaderString = sb.toString();
			csvFile.println(columnHeaderString);
			for(int i = 0; i < outputFilePaths.size(); i++) {
				String outputFilePath = outputFilePaths.get(i);
				int start = JKU_PDD_OUTPUT_DIRECTORY_PATH.length()+1;
				int end = outputFilePath.lastIndexOf("/");
				String algorithm = outputFilePath.substring(start,end);
				start = outputFilePath.lastIndexOf("/")+1;
				end = outputFilePath.lastIndexOf(".");
				String outputFileName = outputFilePath.substring(start,end);
				csvFile.println(algorithm+","+outputFileName+","+results.get(i));
			}
			csvFile.close();
			
			//Now create piece x algorithm tables for each of the results that show all values separately
			
			String[] measures = {"P3","R3","TLF1","cf","runtime","FFP"};
			
			for(String measure : measures) {
				csvFile = new PrintStream(RESULTS_OUTPUT_DIRECTORY_PATH+"/"+dateString+"-"+measure+"-table.csv");
				csvFile.print("Algorithm, Basic algorithm, Mode, RecurSIA, RRT, CT, R");
				for(String composer : COMPOSERS) csvFile.print(","+composer);
				for(int i = 0; i < outputFilePaths.size(); i++) {
					String outputFilePath = outputFilePaths.get(i);
					int start = JKU_PDD_OUTPUT_DIRECTORY_PATH.length()+1;
					int end = outputFilePath.lastIndexOf("/");
					String algorithm = outputFilePath.substring(start,end);
					if (i%5==0) csvFile.print("\n"+parseAlgorithm(algorithm));
					switch (measure) {
					case "P3": 		csvFile.print(","+String.format("%.4f", results.get(i).P3)); break;
					case "R3": 		csvFile.print(","+String.format("%.4f", results.get(i).R3)); break;
					case "TLF1": 	csvFile.print(","+String.format("%.4f", results.get(i).TLF1)); break;
					case "cf": 		csvFile.print(","+String.format("%.4f", results.get(i).cf)); break;
					case "runtime": csvFile.print(","+String.format("%d", results.get(i).runtime)); break;
					case "FFP": 	csvFile.print(","+String.format("%.4f", results.get(i).FFP)); break;
					}
				}
				csvFile.close();				
			}
			
			
//			//Now create a third results file to produce a piece x algorithm table that just shows TLP values
//			
//			csvFile = new PrintStream(RESULTS_OUTPUT_DIRECTORY_PATH+"/"+dateString+"-tlp-table.csv");
//			csvFile.print("Algorithm");
//			for(String composer : COMPOSERS) csvFile.print(","+composer);
//			for(int i = 0; i < outputFilePaths.size(); i++) {
//				String outputFilePath = outputFilePaths.get(i);
//				int start = JKU_PDD_OUTPUT_DIRECTORY_PATH.length()+1;
//				int end = outputFilePath.lastIndexOf("/");
//				String algorithm = outputFilePath.substring(start,end);
//				if (i%5==0) csvFile.print("\n"+algorithm);
//				csvFile.print(","+String.format("%.2f", results.get(i).P3));
//			}
//			csvFile.close();
//
//			//Now create a fourth results file to produce a piece x algorithm table that just shows TLR values
//			
//			csvFile = new PrintStream(RESULTS_OUTPUT_DIRECTORY_PATH+"/"+dateString+"-tlr-table.csv");
//			csvFile.print("Algorithm");
//			for(String composer : COMPOSERS) csvFile.print(","+composer);
//			for(int i = 0; i < outputFilePaths.size(); i++) {
//				String outputFilePath = outputFilePaths.get(i);
//				int start = JKU_PDD_OUTPUT_DIRECTORY_PATH.length()+1;
//				int end = outputFilePath.lastIndexOf("/");
//				String algorithm = outputFilePath.substring(start,end);
//				if (i%5==0) csvFile.print("\n"+algorithm);
//				csvFile.print(","+String.format("%.2f", results.get(i).R3));
//			}
//			csvFile.close();

			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String getBasicAlgorithm(String algorithm) {
		if (algorithm.contains("COSIA"))
			return "COSIATEC";
		if (algorithm.contains("TECCompress"))
			return "SIATECCompress";
		if (algorithm.contains("Forth"))
			return "Forth";
		return "Unknown";
	}
	
	public static String getMode(String algorithm) {
		if (algorithm.contains("BB") || algorithm.contains("-bbmode"))
			return "BB";
		if (algorithm.contains("Segment") || algorithm.contains("-segmode"))
			return "Segment";
		return "Z";
	}
	
	public static String getRecurSIA(String algorithm) {
		if (algorithm.startsWith("Re"))
			return "RecurSIA";
		return "Z";
	}
	
	public static String getRRT(String algorithm) {
		if (algorithm.contains("-RRT"))
			return "RRT";
		return "Z";
	}
	
	public static String getCT(String algorithm) {
		if (algorithm.contains("CT"))
			return "CT";
		return "Z";
	}
	
	public static String getR(String algorithm) {
		if (algorithm.contains("ForthR")||algorithm.contains("SIAR"))
			return "R";
		return "Z";
	}
	
	public static String parseAlgorithm(String algorithm) {
//		Algorithm,Mode,RecurSIA,RRT,CT,R
		String alg = getBasicAlgorithm(algorithm);
		String mode = getMode(algorithm);
		String recursia = getRecurSIA(algorithm);
		String rrt = getRRT(algorithm);
		String ct = getCT(algorithm);
		String r = getR(algorithm);
		return algorithm+","+alg + ","+mode+","+recursia+","+rrt+","+ct+","+r;
	}
	
	public static void main(String[] args) {
		run();
	}
}
