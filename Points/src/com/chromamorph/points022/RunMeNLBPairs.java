package com.chromamorph.points022;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Notes;

/**
 * Create MIDI files that are concatenations of every pair of files in the annotated NLB dataset.
 * 
 * @author David Meredith
 * @date Tuesday 21 May 2013
 *
 */
public class RunMeNLBPairs {

	static String algorithm = "ForthRCT";
	
	public static void main(String[] args) {
		File inputFileDirectory = new File("/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/nlb_datasets/annmidi");
		String[] inputFileList = inputFileDirectory.list();
		String outputDirectoryName = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB/"+algorithm+"/NLB-PAIRS";

		int iStart = 0;

		//		for(int i = 0; i < inputFileList.length; i++)
		//			if (inputFileList[i].equals("NLB075325_02.mid")) {
		//				iStart = i;
		//				break;
		//			}

		for(int i = iStart; i < inputFileList.length; i++) {
			String fileName1 = inputFileList[i];
			if (!fileName1.endsWith("mid")) continue;
			String fullFileName1 = inputFileDirectory+"/"+fileName1;
			if (new File(fullFileName1).isDirectory()) continue;
			for(int j = i+1; j < inputFileList.length; j++) {
				String fileName2 = inputFileList[j];
				if (!fileName2.endsWith("mid")) continue;
				String fullFileName2 = inputFileDirectory+"/"+fileName2;
				if (new File(fullFileName2).isDirectory()) continue;
				try {
					Notes notes1 = Notes.fromMIDI(fullFileName1,true);
					Notes notes2 = Notes.fromMIDI(fullFileName2,true);
					PointSet points1 = new PointSet(notes1,true);
					PointSet points2 = new PointSet(notes2,true);
					long timeShift = points1.getMaxX()*2;
					points1.addAll(points2.translate(new Vector(timeShift,0)));
					String inputFileName = fileName1.substring(0,fileName1.indexOf('.'))+"+"+fileName2;
					
					//Forth
//					PointSet dataset = points1;
//					String outputFileName = inputFileName;
//					String outputDirectoryPath = outputDirectoryName;
//					new ForthEncoding(dataset,
//							outputFileName,
//							outputDirectoryPath,
//							false, //forRSubdiagonals, 
//							0, //r, 
//							false, //withCompactnessTrawler, 
//							0.0, //a, 
//							0 //b
//							);

					//ForthCT
//					PointSet dataset = points1;
//					String outputFileName = inputFileName;
//					String outputDirectoryPath = outputDirectoryName;
//					new ForthEncoding(dataset,
//							outputFileName,
//							outputDirectoryPath,
//							false, //forRSubdiagonals, 
//							0, //r, 
//							true, //withCompactnessTrawler, 
//							0.66, //a, 
//							3 //b
//							);

					//ForthR
//					PointSet dataset = points1;
//					String outputFileName = inputFileName;
//					String outputDirectoryPath = outputDirectoryName;
//					new ForthEncoding(dataset,
//							outputFileName,
//							outputDirectoryPath,
//							true, //forRSubdiagonals, 
//							3, //r, 
//							false, //withCompactnessTrawler, 
//							0.0, //a, 
//							0 //b
//							);

//					ForthRCT
					PointSet dataset = points1;
					String outputFileName = inputFileName;
					String outputDirectoryPath = outputDirectoryName;
					new ForthEncoding(dataset,
							outputFileName,
							outputDirectoryPath,
							true, //forRSubdiagonals, 
							3, //r, 
							true, //withCompactnessTrawler, 
							0.66, //a, 
							3 //b
							);

					//					String fullFileName = fullFileName1+" + "+fullFileName2;
					//					String outputFilePath = outputDirectoryName+"/"+inputFileName; 

					//					new ForthEncoding(points1,
					//							inputFileName,
					//							outputDirectoryName,
					//							PitchRepresentation.MORPHETIC_PITCH,
					//							0.0, 
					//							1.0, 
					//							0.0, 
					//							1.0,
					//							15,
					//							0.5);

					//SIACTTECCompress
					//					new SIATECCompressEncoding(points1, 
					//							inputFileName, 
					//							outputDirectoryName, 
					//							0, 
					//							PitchRepresentation.MORPHETIC_PITCH, 
					//							false, 
					//							true, 
					//							.66, 
					//							3);

					//					new BZIP2Encoding(inputFileName, outputDirectoryName, points1);
					//					BZIP2Encoding.CreateLogFile(inputFileName, outputDirectoryName);
					//					new SIATECCompressEncoding(points1, inputFileName, outputDirectoryName, 0, PitchRepresentation.MORPHETIC_PITCH, false);		

					//					COSIACTTEC
					//					new COSIATECEncoding(points1,inputFileName,outputDirectoryName,true,fullFileName, false, true, 0.66, 3);

					//SIARCTTECCompress
					//					new SIATECCompressEncoding(
					//							points1, 
					//							inputFileName, 
					//							outputDirectoryName, 
					//							0, 
					//							PitchRepresentation.MORPHETIC_PITCH, 
					//							false,//don't draw 
					//							true,//compactness trawling
					//							0.66, 
					//							3,
					//							true, //forRSuperdiagonals
					//							3);   //r = 3

					//SIARTECCompress
					//					new SIATECCompressEncoding(
					//							points1, 
					//							inputFileName, 
					//							outputDirectoryName, 
					//							0, 
					//							PitchRepresentation.MORPHETIC_PITCH, 
					//							false,//don't draw 
					//							false,//no compactness trawling
					//							0.0, 
					//							0,
					//							true, //forRSuperdiagonals
					//							3);   //r = 3


					//					COSIARCTTEC
					//					new COSIATECEncoding(
					//							points1,
					//							inputFileName,
					//							outputDirectoryName,
					//							true,
					//							fullFileName,
					//							false,
					//							true,
					//							0.66,
					//							3,
					//							true,
					//							3);


					//					new COSIATECEncoding(points1,inputFileName,outputDirectoryName,true,fullFileName);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NoMorpheticPitchException e) {
					e.printStackTrace();
				} catch (UnimplementedInputFileFormatException e) {
					e.printStackTrace();
				} 
			}
		}
	}
}
