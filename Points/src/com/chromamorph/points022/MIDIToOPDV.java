package com.chromamorph.points022;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Notes;

public class MIDIToOPDV {
	public static void main(String[] args) {
		if (args.length == 0 || !(args[0].toLowerCase().endsWith(".mid") || args[0].toLowerCase().endsWith(".midi")))
			System.out.println("You need to provide the path to a midi file as a command line argument.\nThe file must have the file extension .mid or .midi.");
		else
			try {
				Notes notes = Notes.fromMIDI(args[0],true,true);
				Path inputFilePath = Paths.get(args[0]);
				String inputFileName = inputFilePath.getFileName().toString();
				String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.'))+".opdv";
				Path outputFilePath = inputFilePath.getParent().resolve(outputFileName);
				notes.toGVFile(outputFilePath.toString());
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
