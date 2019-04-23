package com.chromamorph.points022;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

//import com.chromamorph.notes.Notes;

//import com.chromamorph.notes.Notes;

public class RunMeNLB {

	public static String fullFileName;

	/**
	 * Name of subdirectory of root directory for output data that contains results and data
	 * for this algorithm.
	 */
	public static String algorithm = "ForthRCT";


	public static void main(String[] args) throws InvalidMidiDataException, IOException, NoMorpheticPitchException {
		File inputFileDirectory = new File("/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/nlb_datasets/annmidi");
		String[] inputFileList = inputFileDirectory.list();
		String outputDirectoryName = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB/"+algorithm+"/NLB";
		for(String fileName : inputFileList) {
			if (!fileName.endsWith("mid")) continue;
			fullFileName = inputFileDirectory+"/"+fileName;
			fileName = fileName.substring(0,fileName.indexOf('.'));
			//			PointSet points = null;
			//			boolean diatonicPitch = true;
			//			Notes notes = Notes.fromMIDI(fullFileName,diatonicPitch);
			//			points = new PointSet(notes,diatonicPitch);
			//			new ForthEncoding(fullFileName, 
			//					outputDirectoryName,
			//					PitchRepresentation.MORPHETIC_PITCH,
			//					0.0, 
			//					1.0, 
			//					0.0, 
			//					1.0,
			//					15,
			//					0.5);
			//						new BZIP2Encoding(fileName, outputDirectoryName, points);
			//						BZIP2Encoding.CreateLogFile(fileName, outputDirectoryName);
			
//			Forth
//			try {
//				new ForthEncoding(fullFileName,
//						outputDirectoryName,
//						false, //forRSubdiagonals, 
//						0, //r, 
//						false, //withCompactnessTrawler, 
//						0.0, //a, 
//						0 //b
//						);
//			} catch (UnimplementedInputFileFormatException e1) {
//				e1.printStackTrace();
//			}

//			ForthCT
//			try {
//				new ForthEncoding(fullFileName,
//						outputDirectoryName,
//						false, //forRSubdiagonals, 
//						0, //r, 
//						true, //withCompactnessTrawler, 
//						0.66, //a, 
//						3 //b
//						);
//			} catch (UnimplementedInputFileFormatException e1) {
//				e1.printStackTrace();
//			}

//			ForthR
//			try {
//				new ForthEncoding(fullFileName,
//						outputDirectoryName,
//						true, //forRSubdiagonals, 
//						3, //r, 
//						false, //withCompactnessTrawler, 
//						0.0, //a, 
//						0 //b
//						);
//			} catch (UnimplementedInputFileFormatException e1) {
//				e1.printStackTrace();
//			}

//			ForthRCT
			try {
				new ForthEncoding(fullFileName,
						outputDirectoryName,
						true, //forRSubdiagonals, 
						3, //r, 
						true, //withCompactnessTrawler, 
						0.66, //a, 
						3 //b
						);
			} catch (UnimplementedInputFileFormatException e1) {
				e1.printStackTrace();
			}

			//SIACTTECCompress
			//			try {
			//				new SIATECCompressEncoding(fullFileName, 
			//													   outputDirectoryName, 0, PitchRepresentation.MORPHETIC_PITCH, false, true, .66, 3);
			//			} catch (NoMorpheticPitchException e) {
			//				e.printStackTrace();
			//			} catch (IOException e) {
			//				e.printStackTrace();
			//			} catch (UnimplementedInputFileFormatException e) {
			//				e.printStackTrace();
			//			} catch (InvalidMidiDataException e) {
			//				e.printStackTrace();
			//			}

			//			COSIARTEC
			//			new COSIATECEncoding(
			//					points,
			//					fileName,
			//					outputDirectoryName,
			//					diatonicPitch,
			//					fullFileName,
			//					false,
			//					false,
			//					0.0,
			//					0,
			//					true,
			//					3);

			//			COSIARCTTEC
			//			new COSIATECEncoding(
			//					points,
			//					fileName,
			//					outputDirectoryName,
			//					diatonicPitch,
			//					fullFileName,
			//					false,
			//					true,
			//					0.66,
			//					3,
			//					true,
			//					3);

			//			SIARTECCompress
			//						try {
			//							new SIATECCompressEncoding(fullFileName, 
			//													   outputDirectoryName, 
			//													   0, 
			//													   PitchRepresentation.MORPHETIC_PITCH, 
			//													   false, 
			//													   false, 
			//													   0.0, 
			//													   0,
			//													   true,
			//													   3);
			//						} catch (NoMorpheticPitchException e) {
			//							e.printStackTrace();
			//						} catch (IOException e) {
			//							e.printStackTrace();
			//						} catch (UnimplementedInputFileFormatException e) {
			//							e.printStackTrace();
			//						} catch (InvalidMidiDataException e) {
			//							e.printStackTrace();
			//						}

			//			SIARCTTECCompress
//			try {
//				new SIATECCompressEncoding(fullFileName, 
//						outputDirectoryName, 
//						0, 
//						PitchRepresentation.MORPHETIC_PITCH, 
//						false, 
//						true, 
//						0.66, 
//						3,
//						true,
//						3);
//			} catch (NoMorpheticPitchException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (UnimplementedInputFileFormatException e) {
//				e.printStackTrace();
//			} catch (InvalidMidiDataException e) {
//				e.printStackTrace();
//			}


			//new SIATECCompressEncoding(fullFileName, outputDirectoryName, 0, PitchRepresentation.MORPHETIC_PITCH, false);

			//COSIACTTEC

			/*
			 * PointSet dataset, 
							 String inputFileName, 
							 String outputFolderName, 
							 boolean diatonicPitch, 
							 String fullFileName, 
							 boolean mirex,
							 boolean withCompactnessTrawler,
							 double a,
							 int b
			 */
			//			new COSIATECEncoding(points,fileName,outputDirectoryName,diatonicPitch,fullFileName, false, true, 0.66, 3);

			//COSIATEC
			//			new COSIATECEncoding(points,fileName,outputDirectoryName,diatonicPitch,fullFileName);
		}
	}
}
