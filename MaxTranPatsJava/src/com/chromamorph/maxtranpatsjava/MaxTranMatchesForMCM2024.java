package com.chromamorph.maxtranpatsjava;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;
import com.chromamorph.points022.Encoding;
import com.chromamorph.points022.EvaluateMIREX2013;
import com.chromamorph.points022.OMNISIA;
import com.chromamorph.points022.PointSet;

public class MaxTranMatchesForMCM2024 {
	public static void main(String[] args) throws MissingTieStartNoteException, FileNotFoundException, SuperMTPsNotNullException {
		String datasetFileName = "data/MCM2024/Ravel/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN.OPND";
		File datasetFile = new File(datasetFileName);
		String patternFileName = "data/MCM2024/Ravel/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN-QUERY-1.OPND";
		String groundTruthFileName = "data/MCM2024/Ravel/RAVEL-HAYDN-OCCURRENCES.OPNDS";
		File groundTruthFile = new File(groundTruthFileName);

		TransformationClass[] transformationClasses = new TransformationClass[] {new F_2STR()};
		boolean pitchSpell = true;
		boolean midTimePoint = true;
		String dimensionMask = "1100";
		String outputDir = "output/MCM2024/Ravel";
		int minSize = 4;
		boolean draw = true;
		double minCompactness = 0;
		double minOccurrenceCompactness = 0.5;
		
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
				minOccurrenceCompactness
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
		groundTruthEncoding.readOccurrenceSets(groundTruthFile);
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
		
		System.out.println("Ground truth occurrences");
		int i = 0;
		for(PointSet occ: newGroundTruthOccurrenceSet)
			System.out.println(String.format("%3d. ", ++i)+occ);
		System.out.println();
		System.out.println("Computed occurrences");
		i = 0;
		for(PointSet occ: computedOccurrenceSet)
			System.out.println(String.format("%3d. ", ++i)+occ);
		System.out.println();
		System.out.println("TLP = "+P3);
		System.out.println("TLR = "+R3);
		System.out.println("TLF1 = "+TLF1);
		
		
		
		//		Draw ground truth for Ravel
		String[] omnisiaArgs = new String[] {
				"-i", datasetFileName,
				"-occsets", groundTruthFileName,
				"-o", "data/MCM2024/Ravel",
				"-d"
		};
		OMNISIA.main(omnisiaArgs);
		

	}
}
