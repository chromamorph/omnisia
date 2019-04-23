package com.chromamorph.notes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.midi.InvalidMidiDataException;

public class SpellChopinWaltzes {

	public static void main(String[] args) {
		String inputDirectory = "/Users/dave/Documents/Work/Research/Data/Chopin/Waltzes/chopin_walzer";
		String[] inputFileNames = new File(inputDirectory).list();
		Path parentDirectory = Paths.get(inputDirectory).getParent();
		String opdvDir = parentDirectory.toString()+"/chopin-waltzer-opnd";
		for(String inputFileName : inputFileNames) {
			if (inputFileName.endsWith(".mid")) {
				try {
					Notes notes = Notes.fromMIDI(inputDirectory+"/"+inputFileName,true);
					String fileNameWithoutMid = inputFileName.substring(0, inputFileName.length()-4);
					String opdvFileName = opdvDir+"/"+fileNameWithoutMid+".opnd";
					notes.toOPDVFile(opdvFileName);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
