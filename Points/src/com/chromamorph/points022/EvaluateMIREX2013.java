package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import com.chromamorph.points022.MIREX2013Entries.TomDavePoint;

/**
 * 
 * @author David Meredith
 * @date 13 July 2013
 *
 * <p>Run this program to evaluate the analysis produced by
 * COSIATEC or SIATECCompress using the methodology 
 * described on the following page 
 * <a href="http://www.music-ir.org/mirex/wiki/2013:Discovery_of_Repeated_Themes_%26_Sections#Evaluation_Procedure">here</a>.</p>
 * 
 * <p>The input must be in the format described <a href="http://www.music-ir.org/mirex/wiki/2013:Discovery_of_Repeated_Themes_%26_Sections#Example_Algorithm_Output_for_a_Ground-Truth_Piece">here</a>.</p>
 */
public class EvaluateMIREX2013 {

	private static double[] OCCURRENCE_THRESHOLDS = {0.0,0.25,0.5,0.75,0.9};
	private static String computedOccurrenceSetFilesRootDirectory = "/Users/dave/Documents/Work/Research/MIREX2013/mirexDave-2013-07-02";
	private static String inputFileRootDirectory = "/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Jul2013/groundTruth";

	public static void main(String[] args) {

		//Read in a collection of computed occurrence sets

		String mirexCosFileName = MIREX2013Entries.getMIREXOutputFilePath(computedOccurrenceSetFilesRootDirectory);

		System.out.println("MIREX output file path: "+mirexCosFileName);

		String lispInputFileName = MIREX2013Entries.getLispInputFilePath(inputFileRootDirectory);

		System.out.println("JKU PDD Lisp input file path: "+lispInputFileName);

		ArrayList<ArrayList<PointSet>> computedOccurrenceSets = MIREX2013Entries.readMIREXOutputFile(mirexCosFileName,lispInputFileName);

		System.out.println("\nComputed occurrence sets:");
		for(int i = 0; i < computedOccurrenceSets.size(); i++)
			System.out.println("QQ"+String.format("%-5d",i+1)+" "+computedOccurrenceSets.get(i));

		//Read in a collection of ground-truth occurrence sets

		ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets = readGroundTruthOccurrenceSets(lispInputFileName);

		System.out.println("\nGround truth occurrence sets:");
		for(int i = 0; i < groundTruthOccurrenceSets.size(); i++)
			System.out.println("PP"+String.format("%-5d",i+1)+groundTruthOccurrenceSets.get(i));

		//Calculate establishment precision, establishment recall and establishment F1 score

		int nP = groundTruthOccurrenceSets.size();

		System.out.println("\nNumber of ground truth patterns (occurrence sets), nP = "+nP);

		int nQ = computedOccurrenceSets.size();

		System.out.println("Number of computed patterns (occurrence sets),       nQ = "+nQ);


		double[][] establishmentMatrix = new double[nP][nQ];


		for(int emCol = 0; emCol < computedOccurrenceSets.size(); emCol++) {
			ArrayList<PointSet> computedOccurrenceSet = computedOccurrenceSets.get(emCol);
			for(int emRow = 0; emRow < groundTruthOccurrenceSets.size(); emRow++) {
				ArrayList<PointSet> groundTruthOccurrenceSet = groundTruthOccurrenceSets.get(emRow);
				double[][] scoreMatrix = calculateScoreMatrix(groundTruthOccurrenceSet,computedOccurrenceSet);
				//Now find the max entry in the score matrix and store this in the establishment matrix.
				establishmentMatrix[emRow][emCol] = getMaxValue(scoreMatrix);
			}	
		}

		//Print out establishment matrix

		System.out.println("\nEstablishment matrix:");

		System.out.print("        ");
		for(int col = 0; col < nQ; col++)
			System.out.print("QQ"+String.format("%-6d",(col+1)));
		for(int row = 0; row < nP; row++) {
			System.out.print("\nPP"+String.format("%-4d",(row + 1)));
			for(int col = 0; col < nQ; col++) {
				double val = establishmentMatrix[row][col];
				if (val > 0.0)
					System.out.print(String.format("%8.4f",val));
			}
		}
		System.out.println("\n\nNotes:");
		System.out.println("  PPi is the ith ground truth occurrence set; QQj is the jth computed occurrence set.");
		System.out.println("  Empty entries in the matrix are zero.");


		/*
		 * Calculate establishment precision, using following formula
		 * 
		 * P_est = (1/n_Q) SUM_{j=1}^{n_Q}(max{S(PP_i,QQ_j)|i = 1,...n_P})
		 */

		double establishmentPrecision = calculatePrecision(establishmentMatrix);

		/*
		 * Calculate establishment recall, using following formula
		 * 
		 * R_est = (1/n_P) SUM_{i=1}^{n_P}(max{S(PP_i,QQ_j)|j = 1,...n_Q})
		 */

		double establishmentRecall = calculateRecall(establishmentMatrix);


		double P = establishmentPrecision;
		double R = establishmentRecall;
		double establishmentF1 = calculateF1Score(P,R);

		System.out.println("\n\nEstablishment measures:");
		System.out.println("  precision  "+String.format("%.4f",establishmentPrecision));
		System.out.println("  recall     "+String.format("%.4f",establishmentRecall));
		System.out.println("  F1         "+String.format("%.4f",establishmentF1));

		//Now we calculate occurrence precision, occurrence recall and occurrence F1 score

		/*
		 * For each entry, e, in the establishment matrix greater than the occurrence threshold
		 * calculate the precision of the score matrix, s(PP_i,QQ_j)
		 * 
		 * precision( s(PP_i,QQ_j) ) = (1/mQ) SUM_{l = 1}^{mQ}(max(s(P_k,Q_l) | k = 1,..mP))
		 * 
		 * where PP_i is ground truth occurrence set i, QQ_j is computed occurrence set j and
		 * s(P_k,Q_l) is the entry in the score matrix, s(PP_i,QQ_j), at position (k,l), giving the
		 * similarity between ground truth occurrence P_k and computed occurrence Q_l.
		 * 
		 * The entry at position(i,j) in the occurrence matrix is set to equal precision(s(PP_i,QQ_j)).
		 * 
		 * The occurrence precision is then the precision of the precision occurrence matrix.
		 * 
		 * All other entries in the occurrence matrix are zero.
		 * 
		 * Analogous definitions for occurrence recall and occurrence F1.
		 * 
		 */

		/*
		 * We're going to print out a table showing the occurrence
		 * precision, recall and F1 score for all the different
		 * occurrence thresholds stored in the array, 
		 * OCCURRENCE_THRESHOLDS.
		 */

		System.out.println("\n\nOccurrence measures");
		System.out.println("c      P_occ  R_occ  F1_occ");

		for(double occurrence_threshold : OCCURRENCE_THRESHOLDS) {

			double[][] precisionOccurrenceMatrix = new double[nP][nQ]; //All entries automatically initialized to 0.0
			double[][] recallOccurrenceMatrix = new double[nP][nQ];
			double[][] f1ScoreOccurrenceMatrix = new double[nP][nQ];

			for(int emRow = 0; emRow < nP; emRow++)
				for(int emCol = 0; emCol < nQ; emCol++) {
					if (establishmentMatrix[emRow][emCol] >= occurrence_threshold) {
						double[][] scoreMatrix = calculateScoreMatrix(groundTruthOccurrenceSets.get(emRow),computedOccurrenceSets.get(emCol));
						precisionOccurrenceMatrix[emRow][emCol] = calculatePrecision(scoreMatrix);
						recallOccurrenceMatrix[emRow][emCol] = calculateRecall(scoreMatrix);
						f1ScoreOccurrenceMatrix[emRow][emCol] = calculateF1Score(precisionOccurrenceMatrix[emRow][emCol],recallOccurrenceMatrix[emRow][emCol]);
					}
				}

			double precision = calculatePrecision(precisionOccurrenceMatrix, true);
			double recall = calculateRecall(recallOccurrenceMatrix,true);
			double f1Score = calculateF1Score(precision,recall);
			System.out.println(String.format("%-6.2f %6.4f %6.4f %6.4f",occurrence_threshold,precision,recall,f1Score));
		}

		//Now let's calculate the "Three-layer F1" value

		System.out.println("\n\nOverall precision (P3): "+String.format("%.4f", getP3(groundTruthOccurrenceSets,computedOccurrenceSets)));
		System.out.println("Overall recall (R3):    "+String.format("%.4f", getR3(groundTruthOccurrenceSets,computedOccurrenceSets)));
		System.out.println("Three-layer F1 score:   "+String.format("%.4f", getThreeLayerF1(groundTruthOccurrenceSets,computedOccurrenceSets)));

		//First five precision

		/*
		 * First five precision is the proportion of the first five patterns returned that
		 * are ground truth occurrence sets. In general, we can't expect computed occurrence
		 * sets to be exactly equal to ground-truth occurrence sets, so I define first-five precision
		 * as mean P3(groundTruthOccurrenceSets,firstFiveComputedOccurrenceSets).
		 */
		System.out.println("\n\nFirst five precision (using P3) : "+String.format("%.4f",getP3(groundTruthOccurrenceSets,computedOccurrenceSets,5)));


	}
	/**
	 * As defined here
	 * http://www.music-ir.org/mirex/wiki/2013:Discovery_of_Repeated_Themes_%26_Sections#Precision.2C_Recall.2C_and_F1_Score
	 * 
	 * At present it is implemented as follows: take the prototypical version of the pattern as specified in the 
	 * ground truth (this is the version specified in bach_wtc2f20 -> polyphonic -> repeatedPatterns -> bruhn -> A, 
	 * for instance, whereas all occurrences including the prototype are specified in the occurrences subfolder); 
	 * take the version defined as occurrence1 in the algorithm output; if these are translationally equivalent 
	 * then this results in a contribution to precision, recall, F1.
	 * @param groundTruthOccurrenceSets
	 * @param computedOccurrenceSets
	 * @return
	 */
	public static double getBasicP(ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets, ArrayList<ArrayList<PointSet>> computedOccurrenceSets) {
		double k = 0.0;
		for(ArrayList<PointSet> groundTruthOccurrenceSet : groundTruthOccurrenceSets) {
			for(ArrayList<PointSet> computedOccurrenceSet : computedOccurrenceSets) {
				PointSet groundTruthPrototype = groundTruthOccurrenceSet.get(0);
				PointSet computedPrototype = computedOccurrenceSet.get(0);
				if(groundTruthPrototype.translationallyEquivalentTo(computedPrototype)) {
					k++;
					break;
				}
			}
		}
		double nQ = computedOccurrenceSets.size();
		return k/nQ;
	}

	public static double getBasicR(ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets, ArrayList<ArrayList<PointSet>> computedOccurrenceSets) {
		double k = 0.0;
		for(ArrayList<PointSet> groundTruthOccurrenceSet : groundTruthOccurrenceSets) {
			for(ArrayList<PointSet> computedOccurrenceSet : computedOccurrenceSets) {
				PointSet groundTruthPrototype = groundTruthOccurrenceSet.get(0);
				PointSet computedPrototype = computedOccurrenceSet.get(0);
				if(groundTruthPrototype.translationallyEquivalentTo(computedPrototype)) {
					k++;
					break;
				}
			}
		}
		double nP = groundTruthOccurrenceSets.size();
		return k/nP;
	}

	/**
	 * This is the maximum value in the first five columns of the establishment matrix.
	 * @param establishmentMatrix
	 * @return
	 */
	public static double getFFTPEst(double[][] establishmentMatrix) {
		double fftp = 0.0;
		for(int col = 0; col < 5 && col < establishmentMatrix[0].length; col++)
			for(int row = 0; row < establishmentMatrix.length; row++)
				if (establishmentMatrix[row][col] > fftp)
					fftp = establishmentMatrix[row][col];
		return fftp;
	}
	
	public static long readRunTime(String timingFilePath) {
		if (!(new File(timingFilePath).exists())) return 0l;
		long l = 0l;
		try {
			BufferedReader br = new BufferedReader(new FileReader(timingFilePath));
			l = Long.parseLong(br.readLine().trim());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l;
	}
	
	public static double getThreeLayerF1(ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets, ArrayList<ArrayList<PointSet>> computedOccurrenceSets) {
		double P3 = getP3(groundTruthOccurrenceSets,computedOccurrenceSets);
		double R3 = getR3(groundTruthOccurrenceSets,computedOccurrenceSets);
		return (P3+R3==0.0?0.0:(2*P3*R3)/(P3+R3));
	}

	public static double getP3(ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets, ArrayList<ArrayList<PointSet>> computedOccurrenceSets) {
		return getP3(groundTruthOccurrenceSets, computedOccurrenceSets, 0);
	}

	public static double getP3(ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets, ArrayList<ArrayList<PointSet>> computedOccurrenceSets, int firstN) {
		double sum = 0.0;
		int n;
		if (firstN == 0 || firstN >= computedOccurrenceSets.size())
			n = computedOccurrenceSets.size();
		else 
			n = firstN;
		for(int i = 0; i < n; i++) {
			ArrayList<PointSet> computedOccurrenceSet = computedOccurrenceSets.get(i);
			double max = 0.0;
			for(ArrayList<PointSet> groundTruthOccurrenceSet : groundTruthOccurrenceSets) {
				double f1 = getF1(groundTruthOccurrenceSet,computedOccurrenceSet);
				if (f1 > max)
					max = f1;
			}
			sum += max;
		}
		return sum/(firstN==0?computedOccurrenceSets.size():firstN);
	}

	public static double getR3(ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets, ArrayList<ArrayList<PointSet>> computedOccurrenceSets) {
		double sum = 0.0;
		for(ArrayList<PointSet> groundTruthOccurrenceSet : groundTruthOccurrenceSets) {
			double max = 0.0;
			for(ArrayList<PointSet> computedOccurrenceSet : computedOccurrenceSets) {
				double f1 = getF1(groundTruthOccurrenceSet,computedOccurrenceSet);
				if (f1 > max)
					max = f1;
			}
			sum += max;
		}
		return sum/groundTruthOccurrenceSets.size();
	}

	public static double getF1(ArrayList<PointSet> groundTruthOccurrenceSet, ArrayList<PointSet> computedOccurrenceSet) {
		double Pb = getPb(groundTruthOccurrenceSet,computedOccurrenceSet);
		double Rb = getRb(groundTruthOccurrenceSet,computedOccurrenceSet);
		return (2*Pb*Rb)/(Pb+Rb);
	}

	private static double getPb(ArrayList<PointSet> groundTruthOccurrenceSet, ArrayList<PointSet> computedOccurrenceSet) {
		double sum = 0.0;
		for(PointSet computedOccurrence : computedOccurrenceSet) {
			double max = 0.0;
			for(PointSet groundTruthOccurrence : groundTruthOccurrenceSet) {
				double f1 = getF1(groundTruthOccurrence,computedOccurrence);
				if (f1 > max)
					max = f1;
			}
			sum += max;
		}
		return sum/computedOccurrenceSet.size();		
	}

	private static double getRb(ArrayList<PointSet> groundTruthOccurrenceSet, ArrayList<PointSet> computedOccurrenceSet) {
		double sum = 0.0;
		for(PointSet groundTruthOccurrence : groundTruthOccurrenceSet) {
			double max = 0.0;
			for(PointSet computedOccurrence : computedOccurrenceSet) {
				double f1 = getF1(groundTruthOccurrence,computedOccurrence);
				if (f1 > max)
					max = f1;
			}
			sum += max;
		}
		return sum/groundTruthOccurrenceSet.size();		
	}

	private static double getF1(PointSet groundTruthOccurrence, PointSet computedOccurrence) {
		int PcapQ = groundTruthOccurrence.intersection(computedOccurrence).size();
		double P = (1.0*PcapQ)/computedOccurrence.size();
		double R = (1.0*PcapQ)/groundTruthOccurrence.size();;
		return (2*P*R)/(P+R);
	}

	public static double[][] calculateScoreMatrix(ArrayList<PointSet> groundTruthOccurrenceSet, ArrayList<PointSet> computedOccurrenceSet) {
		double[][] scoreMatrix = new double[groundTruthOccurrenceSet.size()][computedOccurrenceSet.size()];
		for(int row = 0; row < groundTruthOccurrenceSet.size(); row++)
			for(int col = 0; col < computedOccurrenceSet.size(); col++) {
				scoreMatrix[row][col] = computeSimilarity(groundTruthOccurrenceSet.get(row),computedOccurrenceSet.get(col));
			}
		return scoreMatrix;
	}

	public static double calculatePrecision(double[][] matrix) {
		return calculatePrecision(matrix, false);
	}

	public static double calculatePrecision(double[][] matrix, boolean nonZeroColumnsOnly) {
		double sum = 0.0;
		int numCols = matrix[0].length;
		int numNonZeroCols = 0;
		for(int j = 0; j < numCols; j++) {
			double maxValue = getMaxValueInColumn(matrix,j);
			if (maxValue > 0.0) numNonZeroCols++;
			sum += maxValue;
		}
		return sum/(nonZeroColumnsOnly?numNonZeroCols:numCols);
	}

	public static double calculateRecall(double[][] matrix) {
		return calculateRecall(matrix, false);
	}

	public static double calculateRecall(double[][] matrix, boolean nonZeroRowsOnly) {
		double sum = 0.0;
		int numRows = matrix.length;
		int numNonZeroRows = 0;
		for(int i = 0; i < numRows; i++) {
			double maxValue = getMaxValueInRow(matrix,i);
			if (maxValue > 0.0) numNonZeroRows++;
			sum += maxValue;
		}
		return sum/(nonZeroRowsOnly?numNonZeroRows:numRows);
	}

	public static double calculateF1Score(double precision, double recall) {
		return (2.0 * precision * recall)/(precision + recall);
	}

	private static double getMaxValueInColumn(double[][] matrix, int columnIndex) {
		double max = 0.0;
		for(int row = 0; row < matrix.length; row++) {
			if (matrix[row][columnIndex] > max)
				max = matrix[row][columnIndex];
		}
		return max;
	}

	private static double getMaxValueInRow(double[][] matrix, int rowIndex) {
		//		displayMatrix(matrix);
		double max = 0.0;
		for(int col = 0; col < matrix[rowIndex].length; col++) {
			if (matrix[rowIndex][col] > max)
				max = matrix[rowIndex][col];
		}
		return max;
	}

	public static double getMaxValue(double[][] scoreMatrix) {
		double max = 0.0;
		for(int i = 0; i < scoreMatrix.length; i++)
			for(int j = 0; j < scoreMatrix[i].length; j++)
				if (scoreMatrix[i][j] > max)
					max = scoreMatrix[i][j];
		return max;
	}

	/**
	 * Computes a double representing the similarity between 
	 * groundTruthOccurrence and computedOccurrence.
	 * 
	 * Uses the cardinality score as a measure of similarity:
	 * s_c(P_i,Q_j) = |P_i cap Q_j|/max{|P_i|,|Q_j|}
	 * 
	 * where P_i is the ground truth occurrence, Q_j is the computed occurrence.
	 * 
	 * @param groundTruthOccurrence
	 * @param computedOccurrence
	 * @return
	 */
	private static double computeSimilarity(PointSet groundTruthOccurrence, PointSet computedOccurrence) {
		return ((groundTruthOccurrence.intersection(computedOccurrence)).size()*1.0)/Math.max(groundTruthOccurrence.size(), computedOccurrence.size());
	}

	public static ArrayList<ArrayList<PointSet>> readGroundTruthOccurrenceSets(String lispInputFileName) {

		ArrayList<TomDavePoint> tomDavePoints = MIREX2013Entries.readLispFileIntoPointSet(lispInputFileName);

		ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets = new ArrayList<ArrayList<PointSet>>();
		String repeatedPatternsDirectoryPath = lispInputFileName.substring(0,lispInputFileName.lastIndexOf("lisp"))+"repeatedPatterns";
		ArrayList<String> occurrenceSetDirectoryPaths = getOccurrenceSetDirectoryPaths(repeatedPatternsDirectoryPath);
		for(String occurrenceSetDirectoryPath : occurrenceSetDirectoryPaths) {
			File occSetDir = new File(occurrenceSetDirectoryPath);
			String[] occFileNames = occSetDir.list(); //Includes .opc files!
			ArrayList<PointSet> thisOccurrenceSet = new ArrayList<PointSet>();
			for(String occFileName : occFileNames) {
				if (occFileName.endsWith(".txt")) {
					String occFilePath = occurrenceSetDirectoryPath+(occurrenceSetDirectoryPath.endsWith("/")?"":"/")+occFileName;
					PointSet occurrence = readLispOccurrenceFile(occFilePath, tomDavePoints);
					thisOccurrenceSet.add(occurrence);
				}
			}
			groundTruthOccurrenceSets.add(thisOccurrenceSet);
		}
		return groundTruthOccurrenceSets;
	}

	public static PointSet readLispOccurrenceFile(String lispOccurrenceFilePath, ArrayList<TomDavePoint> tomDavePoints) {
		try {
			ArrayList<String> opcStrings = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(lispOccurrenceFilePath));
			for(String l = br.readLine(); l != null; l = br.readLine())
				opcStrings.add(l.trim());
			br.close();

			PointSet pointSet = new PointSet();
			for(String opcString : opcStrings) {
				double onsetNumerator, onsetDenominator, mirexOnset, mirexMidi;
				int slashPos;
				if ((slashPos = opcString.indexOf("/")) > 0) {
					onsetNumerator = Double.parseDouble(opcString.substring(1,slashPos).trim());
					onsetDenominator = Double.parseDouble(opcString.substring(slashPos+1,opcString.indexOf(" ")));
					mirexOnset = onsetNumerator/onsetDenominator;
				} else {
					mirexOnset = Double.parseDouble(opcString.substring(1,opcString.indexOf(" ")));
				}
				mirexMidi = Double.parseDouble(opcString.substring(opcString.indexOf(" "),opcString.indexOf(")")).trim());
				pointSet.add(MIREX2013Entries.findPoint(mirexOnset, mirexMidi, tomDavePoints));
			}
			return pointSet;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static ArrayList<String> getOccurrenceSetDirectoryPaths(String repeatedPatternsDirectoryPath) {

		/*
		 * repeatedPatternsDirectoryPath should be something like
		 * /Users/dave/Documents/Work/Research/workspace/Points/data/JKUPDD-noAudio-Jul2013/groundTruth/chopinOp24No4/polyphonic/repeatedPatterns
		 */

		//		System.out.println("repeatedPatternsDirectory = "+repeatedPatternsDirectoryPath);

		
		FilenameFilter filenameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				ArrayList<String> allowedPatternDirNames = new ArrayList<String>();
				allowedPatternDirNames.add("bruhn");
				allowedPatternDirNames.add("barlowAndMorgensternRevised");
				allowedPatternDirNames.add("sectionalRepetitions");
				allowedPatternDirNames.add("schoenberg");
				allowedPatternDirNames.add("tomCollins");
				return allowedPatternDirNames.contains(name);
			}
			
		};
		
		File repPatDir = new File(repeatedPatternsDirectoryPath);
		String[] patternSetDirNames = repPatDir.list(filenameFilter);
		ArrayList<String> patternSetDirectoryPaths = new ArrayList<String>();
		for(String dirName : patternSetDirNames) {
			if (dirName.startsWith(".") || (repeatedPatternsDirectoryPath.contains("polyphonic") && dirName.equals("barlowAndMorgenstern"))) 
				continue;
			patternSetDirectoryPaths.add(repeatedPatternsDirectoryPath+(repeatedPatternsDirectoryPath.endsWith("/")?"":"/")+dirName);
		}
		ArrayList<String> occSetDirPaths = new ArrayList<String>();
		for(String patternSetDirectoryPath : patternSetDirectoryPaths) {
			File patSetDir = new File(patternSetDirectoryPath);
			String[] patternDirNames = patSetDir.list();
			for(String patternDirName : patternDirNames) {
				if (!patternDirName.startsWith(".")) {
					String occSetDirPath = patternSetDirectoryPath+(patternSetDirectoryPath.endsWith("/")?"":"/")+patternDirName+"/occurrences/lisp";
					occSetDirPaths.add(occSetDirPath);
				}
			}
		}

		System.out.println("\nGround truth occurrence set directory paths:");
		for(int i = 0; i < occSetDirPaths.size(); i++)
			System.out.println("PP"+String.format("%-5d",i+1)+occSetDirPaths.get(i));
		return occSetDirPaths;
	}

	@SuppressWarnings("unused")
	private static void displayMatrix(double[][] matrix) {

		System.out.println("\n\nMATRIX ("+matrix.length+"x"+matrix[0].length+") :");
		System.out.print("        ");
		for(int col = 1; col <= matrix[0].length; col++)
			System.out.print(String.format("%-7d",col));
		System.out.println();
		for(int row = 0; row < matrix.length; row++) {
			System.out.print(String.format("%7d",row+1));
			for(int col = 0; col < matrix[row].length; col++) {
				System.out.print(String.format("%7.4f",matrix[row][col]));
			}
			System.out.println();
		}
		System.out.println("\n**************");
	}
	
}
