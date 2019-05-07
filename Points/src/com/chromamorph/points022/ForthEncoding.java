package com.chromamorph.points022;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JFrame;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

import processing.core.PApplet;

public class ForthEncoding extends Encoding {

	private double minCr, maxCr, minCompV, maxCompV, sigmaMin;
	private double crLow, crHigh, compVLow, compVHigh;
	private int cMin;

	class TECWeightPair {
		TEC tec;
		double weight;

		TECWeightPair(TEC tec, double weight) {
			this.tec = tec;
			this.weight = weight;
		}
	}

	private ArrayList<TECWeightPair> tecWeights = null;
	private ArrayList<ArrayList<TEC>> S = null; //The cover computed
	private double[] wCrArray, wCompVArray;

	//	private ArrayList<TEC> allTecs = null; 

	public ForthEncoding(String encodingFileName, PointSet pointSet) {
		dataset = pointSet;

		//First read in each line of the alltecs encoding file.

		ArrayList<String> tecStrings = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(encodingFileName));
			for(String l = br.readLine(); l != null; l = br.readLine())
				if ((l.trim().length() > 0) && (l.startsWith("T(P(p(")))
					tecStrings.add(l.trim());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Now parse each tecString in tecStrings to get a TEC and
		//store these TECs in a new list.

		//		if (allTecs == null)
		//			allTecs = new ArrayList<TEC>();
		for(int i = 0; i < tecStrings.size(); i++) {
			//			System.out.println("tecString: "+tecStrings.get(i));
			TEC tec = new TEC(tecStrings.get(i));
			//			System.out.println("TEC "+i+": "+tec);
			//			System.out.println("getTECs() = "+getTECs());
			getTECs().add(tec);
		}

		for(TEC tec : getTECs())
			tec.setDataset(dataset);

	}	

	public ForthEncoding(PointSet pointSet,
			String inputFilePathOrOutputFileName, 
			String outputDirectoryPath,
			PitchRepresentation pitchRepresentation,
			int minPatternSize,
			int maxPatternSize,
			double crLow, 
			double crHigh, 
			double compVLow, 
			double compVHigh,
			int cMin,
			double sigmaMin,
			boolean forRSuperdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b,
			boolean useBoundingBoxCompactness,
			boolean removeRedundantTranslators,
			boolean mirex, boolean segmentMode, boolean bbMode,
			String omnisiaOutputFilePath,
			int topNPatterns,
			boolean withoutChannel10,
			boolean fromOMNISIA
			) throws MissingTieStartNoteException, FileNotFoundException {
		super(pointSet,
				inputFilePathOrOutputFileName,
				outputDirectoryPath,
				pitchRepresentation.equals(PitchRepresentation.MORPHETIC_PITCH)?true:false,
						withoutChannel10,
						"Forth",
						topNPatterns,
						mirex,
						segmentMode,
						bbMode,
						omnisiaOutputFilePath);
		if (pointSet.isEmpty())
			System.out.println(">>>>> pointSet argument is empty in ForthEncoding constructor <<<<<<");
		this.compVHigh = compVHigh;
		this.compVLow = compVLow;
		this.crHigh = crHigh;
		this.crLow = crLow;
		this.cMin = cMin;
		this.sigmaMin = sigmaMin;

		try {
			long startTime = System.currentTimeMillis();

			//First computes a set of TECs using SIATEC
			//This also computes the covered set for each TEC
			if (dataset.isEmpty())
				System.out.println(">>>> dataset is empty in ForthEncoding <<<<<");
			SIATECEncoding siatecEncoding = new SIATECEncoding(
					dataset, //inherited from Encoding
					null, //outputFileNameWithoutExtension+"."+outputFileExtension, //inherited from Encoding 
					null, //outputDirectoryPathString, //inherited from Encoding
					minPatternSize,
					maxPatternSize,
					pitchRepresentation, 
					false, //drawOutput
					false, //verbose
					forRSuperdiagonals, r,
					withCompactnessTrawler, a, b,
					fromOMNISIA,
					mirex, segmentMode, bbMode,
					omnisiaOutputFilePath,
					topNPatterns,
					withoutChannel10,
					removeRedundantTranslators,
					true //Called from ForthEncoding
					);
			ArrayList<TEC> tecs = siatecEncoding.getTECs();
			System.out.println("DONE: "+tecs.size()+" TECs found");
			if (tecs.isEmpty()) {
				System.out.println(">>>>>>>> siatecEncoding.getTecs() is empty <<<<<<");
			}

			if (!tecs.isEmpty()) {
				//			Check if tecs contains a TEC with an empty pattern
				for(TEC tec : tecs)
					if (tec.getPattern().isEmpty())
						System.out.println("!!!!!!!!!!!!>>>>>>>>>>>>> tecs contains a TEC with an empty pattern : "+tec+" <<<<<<<<<<<<<<!!!!!!!!!!!!!!");

				//Find all wCr and wCompV for each TEC

				wCrArray = new double[tecs.size()];
				wCompVArray = new double[tecs.size()];
				for(int i = 0; i < tecs.size(); i++) {
					wCrArray[i] = wCr(tecs.get(i));
					if (useBoundingBoxCompactness)
						wCompVArray[i] = tecs.get(i).getCompactness();
					else
						wCompVArray[i] = wCompV(tecs.get(i));
				}

				//Find minimum and maximum values for wCr and wCompV
				minCr = maxCr = wCrArray[0];
				minCompV = maxCompV = wCompVArray[0];
				for(int i = 1; i < tecs.size(); i++) {
					if (wCrArray[i] > maxCr) maxCr = wCrArray[i];
					if (wCrArray[i] < minCr) minCr = wCrArray[i];
					if (wCompVArray[i] > maxCompV) maxCompV = wCompVArray[i];
					if (wCompVArray[i] < minCompV) minCompV = wCompVArray[i];
				}

				//Now we compute a weight for each TEC
				double[] weights = new double[tecs.size()];
				for(int i = 0; i < tecs.size(); i++) {
					weights[i] = wDashCr(i) * wDashCompV(i);
				}

				//Make list of TECWeightPairs
				tecWeights = new ArrayList<TECWeightPair>();
				for(int i = 0; i < tecs.size(); i++) {
					tecWeights.add(new TECWeightPair(tecs.get(i),weights[i]));
				}

				//Now we compute the cover using the pseudocode in the 
				//JNMR 2014 paper
				forthCover();


				for(ArrayList<TEC> tecArray : S) {
					addTEC(tecArray.get(0));
					for(int i = 1; i < tecArray.size(); i++) {
						addTEC(tecArray.get(i));
					}
				}

				//			CHECK IF THERE ARE ANY EMPTY PATTERNS IN THE TEC ARRAY
				for(TEC tec : getTECs())
					if (tec.getPattern().isEmpty())
						System.out.println("!!!!!!>>>>>>>>> FORTH IS GENERATING COVERS CONTAINING EMPTY TECS <<<<<<<<<<<!!!!!!!!!!!!");

				//			**********
			}

			//			Add residual point set
			//			Get covered set of allTecs
			PointSet coveredSetOfAllTecs = new PointSet();
			for(TEC tec : getTECs())
				coveredSetOfAllTecs.addAll(tec.getCoveredPoints());
			//			Find set of remaining, uncovered points
			PointSet uncoveredPoints = dataset.diff(coveredSetOfAllTecs);
			//			Now add uncoveredPoints as a residual point set TEC to alltecs
			if (!uncoveredPoints.isEmpty())
				addTEC(new TEC(uncoveredPoints,dataset));

			if (removeRedundantTranslators)
				SIATECCompress.removeRedundantTranslators(getTECs());

			long endTime = System.currentTimeMillis();

			long runningTime = endTime - startTime;
			setRunningTime(runningTime);

			writeToFile();

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

	//	private double getProportionOfPointsCovered() {
	//		return (1.0 * getTotalNumberOfPointsCovered())/(1.0 * dataset.size()); 
	//	}

	//	private int getTotalNumberOfPointsCovered() {
	//		PointSet points = new PointSet();
	//		for(TEC tec : allTecs)
	//			points.addAll(tec.getCoveredPoints());
	//		return points.size();
	//	}

	//	protected int getEncodingLength() {
	//		int n = 0;
	//		for (TEC tec : allTecs)
	//			n += tec.getEncodingLength();
	//		return n;
	//	}

	private void forthCover() {
		System.out.println("Computing cover...");
		S = new ArrayList<ArrayList<TEC>>();
		PointSet P = new PointSet();
		boolean found = true;
		while (P.size() != dataset.size() && found) {
			found = false;
			double gammaMax = 0.0;
			TECWeightPair bestTEC = null;
			Integer bestTECIndex = null; 
			TreeSet<Integer> tecWeightsToBeRemoved = new TreeSet<Integer>();
			for(int i = 0; i < tecWeights.size(); i++) {
				int c = tecWeights.get(i).tec.getCoveredPoints().diff(P).size();
				if (c < cMin) { 
					tecWeightsToBeRemoved.add(i);
					continue;
				}
				double gamma = c * tecWeights.get(i).weight;
				if (gamma > gammaMax) {
					gammaMax = gamma;
					bestTEC = tecWeights.get(i);
					bestTECIndex = i;
				}
			}
			if (bestTEC != null) {
				tecWeightsToBeRemoved.add(bestTECIndex);
				System.out.println("TEC added to cover: "+bestTEC.tec+", "+bestTEC.weight);
				found = true;

				System.out.println("Checking for primary...");
				int i = 0;
				boolean primaryFound = false;
				while (!primaryFound && i < S.size()) {
					double intersectionSize = 1.0 * S.get(i).get(0).getCoveredPoints().intersection(bestTEC.tec.getCoveredPoints()).size();
					double primaryTECCoveredSetSize = 1.0 * S.get(i).get(0).getCoveredPoints().size();
					if (intersectionSize/primaryTECCoveredSetSize > sigmaMin) {
						S.get(i).add(bestTEC.tec);
						primaryFound = true;
					}
					i++;
				}
				System.out.println("...DONE: "+(primaryFound?"Primary found":"No primary found"));
				if (!primaryFound) {
					ArrayList<TEC> newPrimaryTEC = new ArrayList<TEC>();
					newPrimaryTEC.add(bestTEC.tec);
					S.add(newPrimaryTEC);
				}
				P.addAll(bestTEC.tec.getCoveredPoints());
			}
			//Remove TECs from tecWeights whose indices are in tecWeightsToBeRemoved
			System.out.print("Removing TECs and weights no longer needed...");
			ArrayList<TECWeightPair> newTECWeights = new ArrayList<TECWeightPair>();
			for(Integer i = 0; i < tecWeights.size(); i++)
				if (!tecWeightsToBeRemoved.contains(i))
					newTECWeights.add(tecWeights.get(i));
			tecWeights = newTECWeights;
			newTECWeights = null;
			System.out.println("DONE");
		}
		System.out.println("..DONE Forth Cover");
	}

	private double wDashCr(int i) {
		double wDashCr = (wCrArray[i]-minCr)/(maxCr-minCr);
		if (wDashCr > crHigh || wDashCr < crLow) wDashCr = 0.0;
		return wDashCr;
	}

	private double wCr(TEC tec) {
		return tec.getCompressionRatio();
	}

	private double wDashCompV(int i) {
		double wDashCompV = (wCompVArray[i]-minCompV)/(maxCompV-minCompV);
		if (wDashCompV > compVHigh || wDashCompV < compVLow) wDashCompV = 0.0;
		return wDashCompV;
	}

	private double wCompV(TEC tec) {
		Double maxWCompV = null;
		TreeSet<Vector> vectors = tec.getTranslators().getVectors();
		PointSet pattern = tec.getPattern();
		for(Vector v : vectors) {
			PointSet thisPattern = pattern.translate(v);
			double thisWCompV = (thisPattern.size() * 1.0)/segV(thisPattern).size();
			if (maxWCompV == null || thisWCompV > maxWCompV)
				maxWCompV = thisWCompV;
		}
		return maxWCompV;
	}

	/**
	 * Defined on page 40 of Jamie Forth's PhD thesis.
	 * 
	 * segV returns the intersection of the shortest temporal segment of the dataset
	 * that contains pattern with the union of the voices that contain
	 * notes in the pattern.
	 * 
	 * @param pattern
	 * @return
	 */
	private PointSet segV(PointSet pattern) {
		Long startTime = pattern.getMinX();
		Long endTime = pattern.getMaxX();
		TreeSet<Integer> voices = new TreeSet<Integer>();
		TreeSet<Point> patternPoints = pattern.getPoints();
		for(Point p : patternPoints)
			if (p.getVoice() != null)
				voices.add(p.getVoice());
		PointSet segment = dataset.getSegment(startTime, endTime, true); 
		if (voices.isEmpty()) { 
			//Return contents of bounding segment
			return segment;
		}
		//Voices has some non-null elements
		PointSet segV = new PointSet();
		TreeSet<Point> segmentPoints = segment.getPoints(); 
		for(Point p : segmentPoints)
			if (voices.contains(p.getVoice()))
				segV.add(p);
		return segV;
	}

	@Override
	public void draw() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,getTECs());
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	//	@Override
	//	public ArrayList<TEC> getTECs() {
	//		return allTecs;
	//	}

	//	public String toString() {
	//		StringBuilder sb = new StringBuilder();
	//		for(TEC tec : allTecs) {
	//			sb.append(tec+"\n");
	//		}
	//		return sb.toString();
	//	}

}
