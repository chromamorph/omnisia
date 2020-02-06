package com.chromamorph.points022;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.chromamorph.notes.Notes;
import com.chromamorph.notes.Notes.MissingTieStartNoteException;

import processing.core.PApplet;

public class COSIATECEncoding extends Encoding {

	@SuppressWarnings("unused")
	private static int ITERATION = 0;


	public COSIATECEncoding() {
		super();
	}

	public COSIATECEncoding(String encodingFileName) {
		//First read in each line of the COS file.
		super();
		ArrayList<String> tecStrings = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(encodingFileName));
			for(String l = br.readLine(); l != null; l = br.readLine())
				if (l.trim().length() > 0) {
					if (l.trim().startsWith("tatumsPerBar")) {
						String valStr = l.split(" ")[1];
						Long val = valStr.equals("null")?null:Long.parseLong(valStr);
						setTatumsPerBar(val);						
					}
					else if (l.trim().startsWith("barOneStartsAt")) {
						String valStr = l.split(" ")[1];
						Long val = valStr.equals("null")?null:Long.parseLong(valStr);						
						setBarOneStartsAt(val);						
					}
					else if (l.trim().startsWith("T("))
						tecStrings.add(l.trim());
				}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Now parse each tecString in tecStrings to get a TEC and
		//store these TECs in a new list.

		for(int i = 0; i < tecStrings.size(); i++)
			getTECs().add(new TEC(tecStrings.get(i)));

		//Now we need to find the complete set of points covered 
		//by the set of TECs that we've just read in.

		PointSet pointSet = new PointSet();
		for(TEC tec : getTECs()) {
			pointSet.addAll(tec.getCoveredPoints());
		}

		dataset = pointSet;

		for(TEC tec : getTECs())
			tec.setDataset(pointSet);

	}

	public COSIATECEncoding( 
			PointSet dataset,
			String outputDirectoryPathString, 
			boolean diatonicPitch, 
			String inputFilePathString, 
			boolean mirex,
			boolean withCompactnessTrawler,
			double a,
			int b,
			boolean forRSuperdiagonals,
			int r,
			boolean removeRedundantTranslators,
			double minTECCompactness,
			int minPatternSize,
			int maxPatternSize,
			boolean mergeTECs,
			int minMatchSize,
			int numIterations,
			boolean draw,
			boolean segmentMode,
			boolean bbMode,
			String omnisiaOutputFilePathString,
			int topNPatterns,
			boolean withoutChannel10,
			boolean sortPatternsBySize) throws MissingTieStartNoteException, FileNotFoundException {
		super(dataset,inputFilePathString,
				outputDirectoryPathString,
				diatonicPitch,
				withoutChannel10,
				"cos",
				topNPatterns,
				mirex,
				segmentMode,
				bbMode,
				omnisiaOutputFilePathString);

		try {

			long startTime = System.currentTimeMillis();

			PointSet points = getDataset().copy();
			TEC bestTEC = null;

			while (!points.isEmpty()) {
				ITERATION++;
				bestTEC = getBestTEC(
						points, 
						withCompactnessTrawler, a, b, 
						forRSuperdiagonals, r, 
						removeRedundantTranslators, 
						minTECCompactness, 
						minPatternSize, 
						maxPatternSize,
						sortPatternsBySize);
				getTECs().add(bestTEC);
				points.remove(bestTEC.getCoveredPoints());
			}

			if (mergeTECs) mergeTECs(minMatchSize, minTECCompactness, numIterations);
			long endTime = System.currentTimeMillis();

			setRunningTime(endTime-startTime);

			writeToFile();

			if (draw && omnisiaOutputFilePathString == null && inputFileName != null && outputDirectoryPathString != null) 
				writeToImageFile(inputFileName,outputDirectoryPathString, diatonicPitch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TEC getBestTEC(PointSet points, 
			boolean withCompactnessTrawler, double a, int b, 
			boolean forRSuperdiagonals, int r,
			boolean removeRedundantTranslators,
			double minTECCompactness,
			int minPatternSize,
			int maxPatternSize,
			boolean sortPatternsBySize) {
		if (points.isEmpty())
			throw new IllegalArgumentException("getBestTEC called with empty point set!");
		System.out.println("getBestTEC:");
		if (points.size() == 1) {
			TEC tec = new TEC(points.copy(), //NOTE THAT points HAS TO BE COPIED BECAUSE THE POINT IS REMOVED LATER!!! 
					new VectorSet(new Vector(0,0)), points);
			System.out.println("\n\n____________\ngetBestTEC given a point set of size 1: " + points+"\nReturning TEC: "+tec+"\n______________\n\n");
			return tec;
		}

		VectorPointPair[][] vectorTable = SIA.computeVectorTable(points,logPrintStream, false);
		ArrayList<MtpCisPair> mtpCisPairs = SIA.run(
				points, 
				vectorTable, 
				forRSuperdiagonals, r, 
				withCompactnessTrawler, a, b, 
				logPrintStream,
				true, //remove tran equiv mtps
				false, //no merge vectors
				minPatternSize,
				maxPatternSize
				);

		LogPrintStream.println(logPrintStream, "...DONE: "+mtpCisPairs.size()+" translationally distinct MTPs");

		if (mtpCisPairs.isEmpty()) {
			LogPrintStream.println(logPrintStream, "No remaining patterns after trawling, so remainder of dataset returned as TEC!");
			TEC bestTEC = new TEC(points.copy(), new VectorSet(new Vector(0,0)), dataset);
			LogPrintStream.println(logPrintStream, "\nBest TEC: ("+String.format("%.2f",bestTEC.getCompressionRatio())+","+ String.format("%.2f", bestTEC.getCompactness())+ ") "+bestTEC.toString());
			return bestTEC;
		}


		TECQualityComparator tecQualityComparator = new TECQualityComparator();
		if (sortPatternsBySize)
			tecQualityComparator = new TECPointSetSizeComparator();

		MtpCisPair mtpCisPair;
		TEC bestTEC = null;
		LogPrintStream.println(logPrintStream, "Finding best TEC");
		for(int i = 0; i < mtpCisPairs.size(); i++) {

			if (i%500==0 && i != 0) {
				System.out.print(".");
				System.out.flush();
			} else if (i%25000==0 && i != 0) {
				System.out.println();
				System.out.flush();
			}

			mtpCisPair = mtpCisPairs.get(i);

			//Find the TEC and its dual for this mtpCisPair
			TEC thisTEC = getTECForMTP(mtpCisPair, vectorTable, points);

			TEC dualTEC = thisTEC.getDual();
			if ((maxPatternSize > 0 && dualTEC.getPatternSize() > maxPatternSize) || dualTEC.getPatternSize() < minPatternSize)
				dualTEC = null;

			//Remove redundant translators from thisTEC and dualTEC
			if (removeRedundantTranslators) {
				thisTEC.removeRedundantTranslators();
				if (dualTEC != null)
					dualTEC.removeRedundantTranslators();
			}

			//Check to see if thisTEC is better than bestTEC. If it is, then set bestTEC to point at thisTEC.
			if (dualTEC != null && tecQualityComparator.compare(dualTEC,bestTEC) < 0) {
				if (dualTEC.getCompactness() >= minTECCompactness && dualTEC.getPatternSize() >= minPatternSize)
					bestTEC = dualTEC;
			}
			if (bestTEC == null || tecQualityComparator.compare(thisTEC, bestTEC) < 0) {
				if (thisTEC.getCompactness() >= minTECCompactness && thisTEC.getPatternSize() >= minPatternSize)
					bestTEC = thisTEC;
			}

			//			If bestTEC has a pattern of size 1 and a translator set of size greater than 1, then set bestTEC to
			//			be the dual of bestTEC.

			if (bestTEC != null && bestTEC.getPattern().size() == 1 && bestTEC.getTranslatorSetSize() > 1)
				bestTEC = bestTEC.getDual();
		}

		if (bestTEC == null)
			bestTEC = new TEC(points.copy(),new VectorSet(new Vector(0,0)),dataset);

		LogPrintStream.println(logPrintStream, "\nBest TEC: ("+String.format("%.2f",bestTEC.getCompressionRatio())+","+ String.format("%.2f", bestTEC.getCompactness())+ ") "+bestTEC.toString());
		return bestTEC;
	}


	private TEC getTECForMTP(MtpCisPair mtpCisPair, VectorPointPair[][] vectorTable, PointSet points) {
		VectorSet translators = new VectorSet();
		Integer[] cols = new Integer[mtpCisPair.getCis().size()];
		mtpCisPair.getCis().toArray(cols);
		int patSize = mtpCisPair.getCis().size();
		int[] rows = new int[patSize];
		rows[0] = 0;
		while(rows[0] <= points.size() - patSize) { //For each vector in the first pattern point column
			for(int j = 1; j < patSize; j++) rows[j] = rows[0]+j; //Initialize the indices for the other pattern point columns
			Vector v0 = vectorTable[cols[0]][rows[0]].getVector();
			boolean found = false;
			for(int col = 1; col < patSize; col++) { //For each pattern point
				while(rows[col] < points.size() && vectorTable[cols[col]][rows[col]].getVector().compareTo(v0) < 0) {
					rows[col]++; //Increment CI for this pattern point until >= v0
				}
				if (rows[col] >= points.size() || !v0.equals(vectorTable[cols[col]][rows[col]].getVector())) break; //If not equal then break
				if (col == patSize-1) found = true;
			}
			if (found || patSize == 1) translators.add(v0);
			rows[0]++;
		}

		return new TEC(mtpCisPair.getMtp(),translators,this.dataset);
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


	public boolean compareWithPointSet(PointSet pointSet) {
		return dataset.equals(pointSet);
	}

	public static boolean compareEncodingWithPointSet() throws NoMorpheticPitchException, IOException {
		//Get encoding
		String encodingFileName = MIREX2013Entries.getFileName("Choose encoding file", "./");
		String pointSetFileName = MIREX2013Entries.getFileName("Choose OPND file", "./");
		COSIATECEncoding encoding = new COSIATECEncoding(encodingFileName);
		boolean diatonic = false;
		if (encodingFileName.contains("diat"))
			diatonic = true;
		PointSet pointSet = new PointSet(Notes.fromOPND(pointSetFileName),diatonic);
		return encoding.compareWithPointSet(pointSet);	
	}

	public static boolean compareEncodingWithPointSet(COSIATECEncoding encoding, PointSet pointSet) {
		return encoding.compareWithPointSet(pointSet);
	}

	public static void main(String[] args) {
		Encoding encoding = new COSIATECEncoding("/Users/dave/Documents/Work/Research/Grants awarded/LRN2CRE8 Third attempt/2016-09-27-London-meeting/output/2016-09-26/score-midi-2016-09-27-02-16-55-817/score-diat.cos");
		encoding.drawOccurrenceSets();
	}

}
