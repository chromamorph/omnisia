package com.chromamorph.points022;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;

import com.chromamorph.notes.Notes;

/**
 * 
 * @author David Meredith
 * @date 1 July 2014
 * 
 * Computes results of fugues experiment for JNMR 2014 paper.
 * 
 * This is a table of fugue against algorithm where each entry gives the three-layer F1 measure.
 * The program generates a csv file that can be loaded directly into Excel.
 *
 */
public class JNMR2014FuguesComputeResults {

//	private static String algorithmOutputRootFolderPathName = "/Users/dave/Documents/Work/Research/Data/GiraudFugueTruthFiles/JNMR2014";
//	private static String groundTruthFolderPathName = "/Users/dave/Documents/Work/Research/Data/GiraudFugueTruthFiles/ground-truth-patterns";
//	private static String resultTablesFolderPathName = "/Users/dave/Documents/Work/Research/Data/GiraudFugueTruthFiles/JNMR2014";

	private static String algorithmOutputRootFolderPathName = "D:\\Dropbox\\Work\\Research\\Data\\Giraud-fugues\\test-computed-patterns";
	private static String groundTruthFolderPathName = "D:\\Dropbox\\Work\\Research\\Data\\Giraud-fugues\\ground-truth-patterns";
	private static String resultTablesFolderPathName = "D:\\Dropbox\\Work\\Research\\Data\\Giraud-fugues\\test-results";

	
	public static void main(String[] args) {
		String[] algStrArray = new File(algorithmOutputRootFolderPathName).list();
		ArrayList<String> algorithmStrings = new ArrayList<String>();
		for(String alg: algStrArray) {
			if (!alg.startsWith(".") && new File(algorithmOutputRootFolderPathName+"/"+alg).isDirectory())
				algorithmStrings.add(alg);
		}

		StringBuilder tlf1TableString = new StringBuilder();
		StringBuilder tlpTableString = new StringBuilder();
		StringBuilder tlrTableString = new StringBuilder();
		//Append first line of table headers - number of fugue
		for(int i = 1; i < 25; i++) {
			tlf1TableString.append(","+i);
			tlpTableString.append(","+i);
			tlrTableString.append(","+i);
		}
		tlf1TableString.append("\n");
		tlpTableString.append("\n");
		tlrTableString.append("\n");

		//Append second line of table header - bwv number
		for(int i = 846; i < 870; i++) {
			tlf1TableString.append(","+i);
			tlpTableString.append(","+i);
			tlrTableString.append(","+i);
		}
		tlf1TableString.append("\n");
		tlpTableString.append("\n");
		tlrTableString.append("\n");

		//Load groundTruthOccurrenceSets
		ArrayList<ArrayList<ArrayList<PointSet>>> groundTruthOccurrenceSetsArray = new ArrayList<ArrayList<ArrayList<PointSet>>>();
		for(int i = 0; i < 24; i++) {
			ArrayList<ArrayList<PointSet>> groundTruthOccurrenceSets = new ArrayList<ArrayList<PointSet>>();
			String fugueFolderPathName = groundTruthFolderPathName+"/opnd/bwv"+(i+846);
			String[] patternFolderNames = new File(fugueFolderPathName).list();
			System.out.println(fugueFolderPathName+" contains:");
			for(String patternFolderName : patternFolderNames) System.out.println("  "+patternFolderName);
			for(String patternFolderName : patternFolderNames) {
				String patternFolderPathName = fugueFolderPathName + "/" + patternFolderName;
				if (!patternFolderPathName.startsWith(".") && new File(patternFolderPathName).isDirectory())
					groundTruthOccurrenceSets.add(getGroundTruthOccurrenceSetForThisPattern(patternFolderPathName));
			}
			groundTruthOccurrenceSetsArray.add(groundTruthOccurrenceSets);
		}

		System.out.println("Ground-truth occurrence sets loaded");

		for(String algorithm : algorithmStrings) {
			//Append row header - algorithm name
			tlf1TableString.append(algorithm);
			tlpTableString.append(algorithm);
			tlrTableString.append(algorithm);

			ArrayList<ArrayList<ArrayList<PointSet>>> computedOccurrenceSetsArray = getComputedOccurrenceSetsArrayForThisAlgorithm(algorithm);
			System.out.println("Obtained computed occurrence sets for algorithm "+algorithm);
			for(int i = 0; i < 24; i++) {
				double tlf1 = EvaluateMIREX2013.getThreeLayerF1(groundTruthOccurrenceSetsArray.get(i), computedOccurrenceSetsArray.get(i));
				double tlp = EvaluateMIREX2013.getP3(groundTruthOccurrenceSetsArray.get(i), computedOccurrenceSetsArray.get(i));
				double tlr = EvaluateMIREX2013.getR3(groundTruthOccurrenceSetsArray.get(i), computedOccurrenceSetsArray.get(i));
				tlf1TableString.append(","+String.format("%.2f",tlf1));
				tlpTableString.append(","+String.format("%.2f",tlp));
				tlrTableString.append(","+String.format("%.2f",tlr));
			}
			System.out.println("Computed TLF1, P3 and R3 values for algorithm "+algorithm);
			tlf1TableString.append("\n");
			tlpTableString.append("\n");
			tlrTableString.append("\n");
		}
		
		//Print tableString to a file
		Calendar cal = Calendar.getInstance();
		String dateString=String.format("%4d%02d%02d%02d%02d",cal.get(Calendar.YEAR),1+cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));

		String tlf1ResultsFilePathName = resultTablesFolderPathName+"/"+dateString+"tlf1-results.csv";
		String tlpResultsFilePathName = resultTablesFolderPathName+"/"+dateString+"tlp-results.csv";
		String tlrResultsFilePathName = resultTablesFolderPathName+"/"+dateString+"tlr-results.csv";
		PrintStream tlf1Ps, tlpPs, tlrPs;
		try {
			tlf1Ps = new PrintStream(tlf1ResultsFilePathName);
			tlf1Ps.print(tlf1TableString.toString());
			tlf1Ps.close();
			tlpPs = new PrintStream(tlpResultsFilePathName);
			tlpPs.print(tlpTableString.toString());
			tlpPs.close();
			tlrPs = new PrintStream(tlrResultsFilePathName);
			tlrPs.print(tlrTableString.toString());
			tlrPs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private static ArrayList<ArrayList<ArrayList<PointSet>>> getComputedOccurrenceSetsArrayForThisAlgorithm(String algorithm) {
		boolean isBoundingBox = (algorithm.contains("BB")?true:false);
		boolean isSegment = (algorithm.contains("Segment")?true:false);
		String outputFolderPathNameForThisAlgorithm = algorithmOutputRootFolderPathName + "/" + algorithm;
		ArrayList<ArrayList<ArrayList<PointSet>>> computedOccurrenceSetsArray = new ArrayList<ArrayList<ArrayList<PointSet>>>();
		for(int bwv = 846; bwv < 870; bwv++) {
			Notes notes = null;
			try {
				notes = Notes.fromOPND(groundTruthFolderPathName+"/opnd/bwv"+bwv+"/bwv"+bwv+"b.opnd");
			} catch (IOException e) {
				e.printStackTrace();
			}
			PointSet dataset = null;
			try {
				dataset = new PointSet(notes,true);
			} catch (NoMorpheticPitchException e) {
				e.printStackTrace();
			}
			String fileSuffix = null;
			if (algorithm.startsWith("CO")) {
				fileSuffix = "opnd-diat.cos";
			} else if (algorithm.startsWith("Forth")) {
				fileSuffix = "alltecs";
			} else if (algorithm.startsWith("LZ77")) {
				fileSuffix = "LZ77";
			} else {//algorithm is SIATECCompress
				fileSuffix = "SIATECCompress";
			}
			ArrayList<TEC> computedTECsForThisFugue = ViewCOSEncoding.readCOSIATECEncoding(outputFolderPathNameForThisAlgorithm+"/bwv"+bwv+"b."+fileSuffix);
			ArrayList<ArrayList<PointSet>> computedOccurrenceSetsForThisFugue = new ArrayList<ArrayList<PointSet>>();
			for(TEC tec : computedTECsForThisFugue) {
				ArrayList<PointSet> occurrenceSet = new ArrayList<PointSet>();
				for(Vector v : tec.getTranslators().getVectors()) {
					PointSet occurrence = tec.getPattern().translate(v);
					if (isBoundingBox)
						occurrenceSet.add(dataset.getBBSubset(occurrence.getTopLeft(), occurrence.getBottomRight()));
					else if (isSegment)
						occurrenceSet.add(dataset.getSegment(occurrence.getMinX(),occurrence.getMaxX(),true));
					else
						occurrenceSet.add(occurrence);
				}
				computedOccurrenceSetsForThisFugue.add(occurrenceSet);
			}
			computedOccurrenceSetsArray.add(computedOccurrenceSetsForThisFugue);
		}
		return computedOccurrenceSetsArray;
	}

	private static ArrayList<PointSet> getGroundTruthOccurrenceSetForThisPattern(String patternFolderPathName) {
		String[] occurrenceFileNames = new File(patternFolderPathName).list();
		ArrayList<PointSet> occurrenceSet = new ArrayList<PointSet>();
		for(String occurrenceFileName : occurrenceFileNames) {
			if (occurrenceFileName.endsWith(".opnd")) {
				try {
					Notes occurrenceNotes = Notes.fromOPND(patternFolderPathName+"/"+occurrenceFileName);
					PointSet occurrence = new PointSet(occurrenceNotes,true);
					occurrenceSet.add(occurrence);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NoMorpheticPitchException e) {
					e.printStackTrace();
				}
			}
		}
		return occurrenceSet;
	}
}
