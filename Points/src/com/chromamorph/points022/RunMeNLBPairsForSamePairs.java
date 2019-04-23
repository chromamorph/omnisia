package com.chromamorph.points022;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Notes;

/**
 * Compresses two copies of each melody in the annotated corpus to get a measure of the length
 * of the encoding and the NCD between identical files.
 * 
 * @author David Meredith
 * @date Tuesday 21 May 2013
 *
 */
public class RunMeNLBPairsForSamePairs {

	public static void main(String[] args) {
		File inputFileDirectory = new File("/Users/dave/Documents/Work/Research/workspace/Points/data/nlb_datasets/annmidi");
		String[] inputFileList = inputFileDirectory.list();
		String outputDirectoryName = "/Users/dave/Documents/Work/Research/workspace/Points/output/points017/nlb-pairs";

		for(int i = 0; i < inputFileList.length; i++) {
			String fileName1 = inputFileList[i];
			if (!fileName1.endsWith("mid")) continue;
			String fullFileName1 = inputFileDirectory+"/"+fileName1;
			if (new File(fullFileName1).isDirectory()) continue;
			String fileName2 = inputFileList[i];
			String fullFileName2 = inputFileDirectory+"/"+fileName2;
			try {
				Notes notes1 = Notes.fromMIDI(fullFileName1,true);
				Notes notes2 = Notes.fromMIDI(fullFileName2,true);
				PointSet points1 = new PointSet(notes1,true);
				PointSet points2 = new PointSet(notes2,true);
				long timeShift = points1.getMaxX()*2;
				points1.addAll(points2.translate(new Vector(timeShift,0)));
				String inputFileName = fileName1.substring(0,fileName1.indexOf('.'))+"+"+fileName2;
				String fullFileName = fullFileName1+" + "+fullFileName2;
				new COSIATECEncoding(points1,inputFileName,outputDirectoryName,true,fullFileName);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoMorpheticPitchException e) {
				e.printStackTrace();
			}
		}
	}
}
