package com.chromamorph.points022;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;


public class SIAEncoding extends Encoding {

	private TreeSet<PatternVectorSetPair> mtpVectorSetPairs = new TreeSet<PatternVectorSetPair>();	
	/**
	 * Constructor used for generating results for JNMR 2014 paper
	 * @param inputFilePathName
	 * @param outputDirectoryPathName
	 * @param forRSubdiagonals
	 * @param withCompactnessTrawler
	 * @param rsb
	 * @throws InvalidMidiDataException 
	 * @throws UnimplementedInputFileFormatException 
	 * @throws IOException 
	 * @throws NoMorpheticPitchException 
	 * @throws MissingTieStartNoteException 
	 */
	public SIAEncoding(String inputFilePathName,
			String outputDirectoryPathName,
			boolean forRSubdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(inputFilePathName,
				outputDirectoryPathName,
				0,
				PitchRepresentation.MORPHETIC_PITCH,
				false,
				forRSubdiagonals, r,
				withCompactnessTrawler, a, b);		
	}

	public SIAEncoding(String inputFilePathName, 
			String outputDirectoryPathName, 
			Integer minPatternSize, 
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(inputFilePathName, 
				outputDirectoryPathName, 
				minPatternSize, 
				pitchRepresentation, 
				drawOutput,
				false, 0,
				false, 0.0, 0);
	}

	public SIAEncoding(String inputFilePathName, 
			String outputDirectoryPathName, 
			Integer minPatternSize,
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput,
			boolean forRSubdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(inputFilePathName, 
				outputDirectoryPathName, 
				minPatternSize,
				0, //maxPatternSize
				pitchRepresentation, 
				drawOutput,
				forRSubdiagonals, r,
				withCompactnessTrawler, a, b,
				false, //mirex
				false, //segmentMode
				false, //bbMode
				null, //omnisiaOutputFilePath
				0, //topNPatterns
				false //withoutChannel10
				);
	}

	public SIAEncoding(String inputFilePathName, 
			String outputDirectoryPathName, 
			Integer minPatternSize,
			Integer maxPatternSize,
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput,
			boolean forRSubdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b,
			boolean mirex, boolean segmentMode, boolean bbMode,
			String omnisiaOutputFilePath,
			int topNPatterns,
			boolean withoutChannel10) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		super(null,
				inputFilePathName,
				outputDirectoryPathName,
				pitchRepresentation.equals(PitchRepresentation.MORPHETIC_PITCH)?true:false,
				withoutChannel10,
				"SIA",
				topNPatterns,
				mirex,
				segmentMode,
				bbMode,
				omnisiaOutputFilePath
				);
		long startTime = System.currentTimeMillis();
		VectorPointPair[][] vectorTable = SIA.computeVectorTable(dataset);

		ArrayList<MtpCisPair> mtpCisPairs = SIA.run(
				dataset, 
				vectorTable, 
				forRSubdiagonals, r, 
				withCompactnessTrawler, a, b, 
				null, //logPrintStream
				false, //removeTranslationallyEquivalentMtps
				true, //mergeVectors
				minPatternSize,
				maxPatternSize
				);

		//Convert mtpCisPairs to mtpVectorSetPairs
		for(MtpCisPair mtpCisPair : mtpCisPairs) {
			PointSet mtp = mtpCisPair.getMtp();
			VectorSet vectorSet = mtpCisPair.getVectorSet();
			mtpVectorSetPairs.add(new PatternVectorSetPair(mtp,vectorSet));
		}

		long endTime = System.currentTimeMillis();
		setRunningTime(endTime-startTime);
		
		setTECs(new ArrayList<TEC>());
		for(PatternVectorSetPair patternVectorSetPair : mtpVectorSetPairs) {
			PointSet pattern = patternVectorSetPair.getMtp();
			VectorSet vectorSet = patternVectorSetPair.getVectorSet();
			vectorSet.add(new Vector(0,0));
			addTEC(new TEC(pattern,vectorSet,dataset));
		}
		
		writeToFile();

	}	


	public static void main(String[] args) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException {
		//		String inputFilePath = "/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/Lartillot/ClosedSubstrings001.opnd";
		//		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/Lartillot";
		String inputFilePath = "/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv847b-done.opnd";
		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/output/points018";
		boolean forRSubdiagonals = false;
		int r = 0;
		boolean withCompactnessTrawler = true;
		double a = 1.0;
		int b = 2;
		SIAEncoding encoding;
		try {
			encoding = new SIAEncoding(
					inputFilePath,
					outputFileDirectoryPath,
					forRSubdiagonals, 
					r,
					withCompactnessTrawler, 
					a, 
					b);
			encoding.draw();
		} catch (MissingTieStartNoteException e) {
			e.printStackTrace();
		}
	}
}
