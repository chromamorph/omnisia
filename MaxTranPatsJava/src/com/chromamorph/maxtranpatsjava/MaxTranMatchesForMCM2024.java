package com.chromamorph.maxtranpatsjava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;
import com.chromamorph.points022.Encoding;
import com.chromamorph.points022.EvaluateMIREX2013;
import com.chromamorph.points022.OMNISIA;
import com.chromamorph.points022.PointSet;

public class MaxTranMatchesForMCM2024 {
	public static void main(String[] args) {
		try {
//			processTask(
//					"data/MCM2024/Ravel/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN.OPND",
//					"data/MCM2024/Ravel/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN-QUERY-1.OPND",
//					"data/MCM2024/Ravel/RAVEL-HAYDN-OCCURRENCES.OPNDS",
//					"output/MCM2024/Ravel",
//					0.8,
//					0.5);
//			processTask(
//					"data/MCM2024/Bach/ContrapunctusVI.opnd",
//					"data/MCM2024/Bach/ContrapunctusVISubject.opnd",
//					"data/MCM2024/Bach/ContrapunctusVISubjectEntries.opnds",
//					"output/MCM2024/Bach",
//					10,
//					0.5,
//					false);
			
			int bwv = 851;
			processTask(
						"data/MCM2024/WTCI/opnd/bwv"+bwv+"/bwv"+bwv+"b.opnd",
						"data/MCM2024/WTCI/opnd/bwv"+bwv+"/S/0001.opnd",
						"data/MCM2024/WTCI/opnd/bwv"+bwv+"/S/",
						"output/MCM2024/WTCI/bwv"+bwv,
						0.8,
						0.5,
						false);
//			processTask(
//					"data/MCM2024/WTCI/opnd/bwv847/bwv847b.opnd",
//					"data/MCM2024/WTCI/opnd/bwv847/S/0001.opnd",
//					"data/MCM2024/WTCI/opnd/bwv847/S/",
//					"output/MCM2024/WTCI/bwv847",
//					0.8,
//					0.5);
		} catch (FileNotFoundException | MissingTieStartNoteException | SuperMTPsNotNullException e) {
			e.printStackTrace();
		}
	}
	
	public static void processTask(
			String datasetFileName,
			String patternFileName,
			String groundTruthFileName,
			String outputDir,
			double minSizeRatio,
			double minOccurrenceCompactness,
			boolean drawBoundingBoxes) throws MissingTieStartNoteException, FileNotFoundException, SuperMTPsNotNullException {
		File datasetFile = new File(datasetFileName);
		File groundTruthFile = new File(groundTruthFileName);

		TransformationClass[] transformationClasses = new TransformationClass[] {new F_2STR()};
		boolean pitchSpell = true;
		boolean midTimePoint = true;
		String dimensionMask = "1100";
		boolean draw = true;
		double minCompactness = 0;
		
		int patternSize = new PointSet(patternFileName).size();
		int minSize = (int)Math.floor(minSizeRatio*patternSize);
		
		com.chromamorph.maxtranpatsjava.PointSet outputPointSet = com.chromamorph.maxtranpatsjava.PointSet.maximalTransformedMatchesFromFiles(
				patternFileName, 
				datasetFileName, 
				transformationClasses, 
				pitchSpell, 
				midTimePoint, 
				dimensionMask, 
				outputDir, 
				minSize, 
				draw, 
				minCompactness, 
				minOccurrenceCompactness,
				groundTruthFileName,
				drawBoundingBoxes,
				false,
				false
				);
		
//		Evaluate output
		Encoding 		groundTruthEncoding = new Encoding(
				//					PointSet dataset,
				new com.chromamorph.points022.PointSet(
						datasetFile.getAbsolutePath(),
						pitchSpell,
						true),
				//					String inputFilePathString,
				datasetFile.getAbsolutePath(),
				//					String outputDirectoryPathString,
				new File(outputDir).getAbsolutePath(),				
						//					boolean isDiatonic,
						pitchSpell,
						//					boolean withoutChannel10,
						true,
						//					String outputFileExtension,
						"pts",
						//					int topNPatterns,
						0,
						//					boolean forMirex,
						false,
						//					boolean segmentMode,
						false,
						//					boolean bbMode,
						false,
						//					String omnisiaOutputFilePathString					
						null);
		if (!groundTruthFile.isDirectory())
			groundTruthEncoding.readOccurrenceSets(groundTruthFile);
		else 
			groundTruthEncoding.readOccurrenceSetsFromDirectory(groundTruthFile);
		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> groundTruthOccurrenceSets = groundTruthEncoding.getOccurrenceSets();
		System.out.println("Duration of first point is " + groundTruthOccurrenceSets.get(0).get(0).getPoints().first().getDuration());
		System.out.println("Voice of first point is " + groundTruthOccurrenceSets.get(0).get(0).getPoints().first().getVoice());
//		Merge ground truth occurrence sets into a single set of occurrences
		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> newGroundTruthOccurrenceSets = new ArrayList<ArrayList<com.chromamorph.points022.PointSet>>();
		ArrayList<com.chromamorph.points022.PointSet> newGroundTruthOccurrenceSet = new ArrayList<com.chromamorph.points022.PointSet>();
		for (ArrayList<com.chromamorph.points022.PointSet> occSet : groundTruthOccurrenceSets) {
			for(com.chromamorph.points022.PointSet occ : occSet)
				newGroundTruthOccurrenceSet.add(occ);
		}
		
		if (midTimePoint) {
//			We need to change all onsets in newGroundTruthOccurrenceSet to mid time points. If this leads to onsets that are non-integer, then we have to double the values
//			First we have to find out if we're going to have to double the values
			com.chromamorph.points022.PointSet dataset = new com.chromamorph.points022.PointSet(datasetFileName,pitchSpell,true);
			double multiplier = 1;
			for(com.chromamorph.points022.Point p : dataset.getPoints()) {
				double midTime = p.getX() + p.getDuration()/2.0;
				if (midTime - Math.floor(midTime) == 0.5)
					multiplier = 2;
			}
			
			for(com.chromamorph.points022.PointSet pointSet : newGroundTruthOccurrenceSet) {
				TreeSet<com.chromamorph.points022.Point> points = pointSet.getPoints();
				for(com.chromamorph.points022.Point point : points) {
					point.setX((long)((point.getX()+0.5*point.getDuration())*multiplier));
				}
			}
		}
		
		Collections.sort(newGroundTruthOccurrenceSet);
		
		newGroundTruthOccurrenceSets.add(newGroundTruthOccurrenceSet);
		
//		
		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> computedOccurrenceSets = new ArrayList<ArrayList<com.chromamorph.points022.PointSet>>();
		ArrayList<com.chromamorph.points022.PointSet> computedOccurrenceSet = new ArrayList<com.chromamorph.points022.PointSet>();
		ArrayList<OccurrenceSet>[] mtpOccSetsArrayListArray = outputPointSet.getMTPOccurrenceSets();
		for(ArrayList<OccurrenceSet> occSetArrayList : mtpOccSetsArrayListArray) {
			if (occSetArrayList != null) {
				for(OccurrenceSet occSet : occSetArrayList) {
					TreeSet<com.chromamorph.maxtranpatsjava.PointSet> mtpOccs = occSet.getOccurrences();
					for(com.chromamorph.maxtranpatsjava.PointSet mtpOcc : mtpOccs) {
						com.chromamorph.points022.PointSet ps = mtpOcc.getPoints022PointSet();
						computedOccurrenceSet.add(ps);
					}
				}
			}
		}
		
		Collections.sort(computedOccurrenceSet);
		computedOccurrenceSets.add(computedOccurrenceSet);
//		computedOccurrenceSets = groundTruthOccurrenceSets; //To check that this give TLP, TLR and TLF1 values of 1.
		double P3 = EvaluateMIREX2013.getP3(groundTruthOccurrenceSets,computedOccurrenceSets);
		double R3 = EvaluateMIREX2013.getR3(groundTruthOccurrenceSets,computedOccurrenceSets);
		double TLF1 = (2*P3*R3)/(P3+R3);
		
		String resultsFileName = outputDir+"/"+datasetFile.getName().replace('.', '-') + "results.txt";
		File resultsFile = new File(resultsFileName);
		PrintWriter resultsPW = new PrintWriter(resultsFile);
		
		resultsPW.println("Ground truth occurrences");
		int i = 0;
		for(PointSet occ: newGroundTruthOccurrenceSet)
			resultsPW.println(String.format("%3d. ", ++i)+occ);
		resultsPW.println();
		resultsPW.println("Computed occurrences");
		i = 0;
		for(PointSet occ: computedOccurrenceSet)
			resultsPW.println(String.format("%3d. ", ++i)+occ);
		resultsPW.println();
		resultsPW.println("TLP = "+P3);
		resultsPW.println("TLR = "+R3);
		resultsPW.println("TLF1 = "+TLF1);
		resultsPW.close();
		
		
		//		Draw ground truth for Ravel
		String[] omnisiaArgs = new String[] {
				"-i", datasetFileName,
				"-occsets", groundTruthFileName,
				"-o", outputDir,
				"-d"
		};
		OMNISIA.main(omnisiaArgs);
		

	}
}
