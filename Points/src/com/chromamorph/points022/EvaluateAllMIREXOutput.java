package com.chromamorph.points022;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;

public class EvaluateAllMIREXOutput {
//	private static String MIREX_OUTPUT_FOLDER_PATH=    "/Users/dave/Documents/Work/Research/MIREX2013/mirexDave-2013-07-02/pattDiscOut";
//	private static String RESULTS_OUTPUT_FOLDER_PATH=  "/Users/dave/Documents/Work/Research/MIREX2013/mirexDave-2013-07-02/results";
//	private static String MIREX_OUTPUT_FOLDER_PATH =   "/Users/dave/Documents/Work/Research/MIREX2013/mirexDave-2013-07-29/examples/MIREX2013/pattDiscOut";
//	private static String RESULTS_OUTPUT_FOLDER_PATH = "/Users/dave/Documents/Work/Research/MIREX2013/mirexDave-2013-07-29/examples/MIREX2013/results";
//	private static String GROUND_TRUTH_FOLDER_PATH="/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Jul2013/groundTruth";
	private static String MIREX_OUTPUT_FOLDER_PATH		= "/Users/dave/Documents/Work/Research/Data/MIREX2016/mirex-output";
	private static String RESULTS_OUTPUT_FOLDER_PATH	= "/Users/dave/Documents/Work/Research/Data/MIREX2016/mirex-results-java";
	private static String GROUND_TRUTH_FOLDER_PATH		= "/Users/dave/Documents/Work/Research/Data/MIREX2016/JKUPDD-noAudio-Aug2013/groundTruth";
	private static String[] OUTPUT_FILE_PREFIXES = {"bach_wtc2f20_","beet_sonata01-3_","chop_mazurka24-4_","gbns_silverswan_","mzrt_sonata04-2_"};
	private static String[] INPUT_FILE_PREFIXES = {"wtc2f20","sonata01-3","mazurka24-4","silverswan","sonata04-2"};
	private static String[] GROUND_TRUTH_FOLDER_NAMES = {"bachBWV889Fg","beethovenOp2No1Mvt3","chopinOp24No4","gibbonsSilverSwan1612","mozartK282Mvt2"};
	private static double[] OCCURRENCE_THRESHOLDS = {0.5,0.75};

	static class Result {
		int nP, nQ;
		double P, R, F1, PEst, REst, F1Est, P3, R3, TLF1, FFP, FFTPEst;
		double[] POcc, ROcc, F1Occ;
		long runtime, frt;
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%d,%d",nP,nQ,P,R,F1,PEst,REst,F1Est,P3,R3,TLF1,FFP,FFTPEst,frt,runtime));
			for(int i = 0; i < POcc.length; i++) {
				sb.append(String.format(",%.4f,%.4f,%.4f",POcc[i],ROcc[i],F1Occ[i]));
			}
			return sb.toString();
		}
	}
	
	public static void main(String[] args) {
		
//		RESULTS_OUTPUT_FOLDER_PATH = MIREX2013Entries.getFileName("Choose results folder","/Users/dave/Documents/Work/Research/MIREX2013/mirexDave-2013-07-02",true);
//		MIREX_OUTPUT_FOLDER_PATH = MIREX2013Entries.getFileName("Choose algorithm output folder","/Users/dave/Documents/Work/Research/MIREX2013/mirexDave-2013-07-02",true);
		
		//First make a list of all the full paths of all the MIREX output files to be evaluated 
		ArrayList<String> mirexOutputFilePaths = new ArrayList<String>();
		ArrayList<String> timingFilePaths = new ArrayList<String>();
		String[] mirexOutputFileNames = new File(MIREX_OUTPUT_FOLDER_PATH).list();
		
		for(String mirexOutputFileName : mirexOutputFileNames) {
			for(String prefix : OUTPUT_FILE_PREFIXES)
				if (mirexOutputFileName.startsWith(prefix) && mirexOutputFileName.endsWith(".txt")) {
					mirexOutputFilePaths.add(MIREX_OUTPUT_FOLDER_PATH+"/"+mirexOutputFileName);
					String timingFileName = mirexOutputFileName.substring(0,mirexOutputFileName.lastIndexOf("."))+".rt";
					timingFilePaths.add(MIREX_OUTPUT_FOLDER_PATH+"/"+timingFileName);
					break;
				}
		}
		
		//Now make a list of the full paths of all the corresponding Lisp input files
		
		ArrayList<String> lispInputFilePaths = new ArrayList<String>();
		for(String mirexOutputFilePath : mirexOutputFilePaths) {
			String groundTruthFolderName = null;
			String inputFilePrefix = null;
			for(int i = 0; i < OUTPUT_FILE_PREFIXES.length; i++) {
				String outputFilePrefix = OUTPUT_FILE_PREFIXES[i];
				if (mirexOutputFilePath.contains(outputFilePrefix)) {
					groundTruthFolderName = GROUND_TRUTH_FOLDER_NAMES[i];
					inputFilePrefix = INPUT_FILE_PREFIXES[i];
				}
			}
			String lispInputFilePath = GROUND_TRUTH_FOLDER_PATH+"/"+groundTruthFolderName+"/polyphonic/lisp/"+inputFilePrefix+".txt";
			lispInputFilePaths.add(lispInputFilePath);
		}
		
		for(int i = 0; i < lispInputFilePaths.size(); i++) {
			System.out.println(mirexOutputFilePaths.get(i));
			System.out.println(lispInputFilePaths.get(i));
			System.out.println(timingFilePaths.get(i));
			System.out.println();
		}
		
		//Calculate measures for all output files and store in a list
		ArrayList<Result> results = new ArrayList<Result>();
		for(int i = 0; i < lispInputFilePaths.size(); i++) {
			String mirexCosFilePath = mirexOutputFilePaths.get(i);
			String timingFilePath = timingFilePaths.get(i);
			String lispInputFilePath = lispInputFilePaths.get(i);
			ArrayList<ArrayList<PointSet>> computedOccurrenceSets = MIREX2013Entries.readMIREXOutputFile(mirexCosFilePath,lispInputFilePath);
			ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets = EvaluateMIREX2013.readGroundTruthOccurrenceSets(lispInputFilePath);
			Result result = new Result();
			result.nP = groundTruthOccurrenceSets.size();
			result.nQ = computedOccurrenceSets.size();

			//Calculate simple P, R and F1
			result.P = EvaluateMIREX2013.getBasicP(groundTruthOccurrenceSets,computedOccurrenceSets);
			result.R = EvaluateMIREX2013.getBasicR(groundTruthOccurrenceSets, computedOccurrenceSets);
			result.F1 = EvaluateMIREX2013.calculateF1Score(result.P,result.R);
			
			double[][] establishmentMatrix = new double[result.nP][result.nQ];

			for(int emCol = 0; emCol < computedOccurrenceSets.size(); emCol++) {
				ArrayList<PointSet> computedOccurrenceSet = computedOccurrenceSets.get(emCol);
				for(int emRow = 0; emRow < groundTruthOccurrenceSets.size(); emRow++) {
					ArrayList<PointSet> groundTruthOccurrenceSet = groundTruthOccurrenceSets.get(emRow);
					double[][] scoreMatrix = EvaluateMIREX2013.calculateScoreMatrix(groundTruthOccurrenceSet,computedOccurrenceSet);
					//Now find the max entry in the score matrix and store this in the establishment matrix.
					establishmentMatrix[emRow][emCol] = EvaluateMIREX2013.getMaxValue(scoreMatrix);
				}	
			}
			
			result.PEst = EvaluateMIREX2013.calculatePrecision(establishmentMatrix);
			result.REst = EvaluateMIREX2013.calculateRecall(establishmentMatrix);
			result.F1Est = EvaluateMIREX2013.calculateF1Score(result.PEst,result.REst);
			result.FFTPEst = EvaluateMIREX2013.getFFTPEst(establishmentMatrix);
			result.runtime = EvaluateMIREX2013.readRunTime(timingFilePath);
			result.frt = result.runtime;
			
			result.F1Occ = new double[OCCURRENCE_THRESHOLDS.length];
			result.POcc = new double[OCCURRENCE_THRESHOLDS.length];
			result.ROcc = new double[OCCURRENCE_THRESHOLDS.length];
			for(int j = 0; j < OCCURRENCE_THRESHOLDS.length; j++) {
				double occurrence_threshold = OCCURRENCE_THRESHOLDS[j];
				double[][] precisionOccurrenceMatrix = new double[result.nP][result.nQ]; //All entries automatically initialized to 0.0
				double[][] recallOccurrenceMatrix = new double[result.nP][result.nQ];
				double[][] f1ScoreOccurrenceMatrix = new double[result.nP][result.nQ];

				for(int emRow = 0; emRow < result.nP; emRow++)
					for(int emCol = 0; emCol < result.nQ; emCol++) {
						if (establishmentMatrix[emRow][emCol] >= occurrence_threshold) {
							double[][] scoreMatrix = EvaluateMIREX2013.calculateScoreMatrix(groundTruthOccurrenceSets.get(emRow),computedOccurrenceSets.get(emCol));
							precisionOccurrenceMatrix[emRow][emCol] = EvaluateMIREX2013.calculatePrecision(scoreMatrix);
							recallOccurrenceMatrix[emRow][emCol] = EvaluateMIREX2013.calculateRecall(scoreMatrix);
							f1ScoreOccurrenceMatrix[emRow][emCol] = EvaluateMIREX2013.calculateF1Score(precisionOccurrenceMatrix[emRow][emCol],recallOccurrenceMatrix[emRow][emCol]);
						}
					}

				result.POcc[j] = EvaluateMIREX2013.calculatePrecision(precisionOccurrenceMatrix, true);
				result.ROcc[j] = EvaluateMIREX2013.calculateRecall(recallOccurrenceMatrix,true);
				result.F1Occ[j] = EvaluateMIREX2013.calculateF1Score(result.POcc[j],result.ROcc[j]);
			}
			
			result.P3 = EvaluateMIREX2013.getP3(groundTruthOccurrenceSets,computedOccurrenceSets);
			result.R3 = EvaluateMIREX2013.getR3(groundTruthOccurrenceSets,computedOccurrenceSets);
			result.TLF1 = EvaluateMIREX2013.getThreeLayerF1(groundTruthOccurrenceSets, computedOccurrenceSets);
			result.FFP = EvaluateMIREX2013.getP3(groundTruthOccurrenceSets,computedOccurrenceSets,5);
			results.add(result);
		}
		
		//Store results in a CSV file that can be loaded into Excel
		Calendar cal = Calendar.getInstance();
		String dateString=String.format("%4d%02d%02d%02d%02d",cal.get(Calendar.YEAR),1+cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
		try {
			PrintStream csvFile = new PrintStream(RESULTS_OUTPUT_FOLDER_PATH+"/"+dateString+"-results.csv");
			StringBuilder sb = new StringBuilder();
			sb.append("file,nP,nQ,P,R,F1,PEst,REst,F1Est,P3,R3,TLF1,FFP,FFTPEst,frt,runtime");
			for(int i = 0; i < OCCURRENCE_THRESHOLDS.length; i++)
				sb.append(String.format(",POcc(%.2f),ROcc(%.2f),F1Occ(%.2f)",OCCURRENCE_THRESHOLDS[i],OCCURRENCE_THRESHOLDS[i],OCCURRENCE_THRESHOLDS[i]));
			String columnHeaderString = sb.toString();
			csvFile.println(columnHeaderString);
			for(int i = 0; i < mirexOutputFilePaths.size(); i++) {
				String mirexOutputFilePath = mirexOutputFilePaths.get(i);
				int start = mirexOutputFilePath.lastIndexOf("/")+1;
				int end = mirexOutputFilePath.lastIndexOf(".");
				String mirexOutputFileName = mirexOutputFilePath.substring(start,end);
				csvFile.println(mirexOutputFileName+","+results.get(i));
			}
			csvFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
