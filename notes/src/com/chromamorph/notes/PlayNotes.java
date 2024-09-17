package com.chromamorph.notes;

import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public class PlayNotes {
	
	public static void main(String[] args) throws InvalidMidiDataException, MidiUnavailableException {
		System.out.println("PlayNotes program");
		if (args.length == 0 || args[0].trim().startsWith("-h")) {
			System.out.println("Syntax:\n java -jar playnotes.jar <file-name> [<beats-per-minute> [<tatums-per-beat> [<start-tatum> [<end-tatum>]]]]");
			return;
		}
		
		if (args[0].toLowerCase().endsWith(".opnd") ||
				args[0].toLowerCase().endsWith(".opndv") ||
				args[0].toLowerCase().endsWith(".mid") ||
				args[0].toLowerCase().endsWith(".midi")) {
			int tatumsPerBeat = 8;
			float beatsPerMinute = 120.0f;
			Notes notes = new Notes(args[0]);
			Long startTatum = null;
			Long endTatum = null;
			if (args.length > 1)
				beatsPerMinute = Float.parseFloat(args[1]);
			if (args.length > 2)
				tatumsPerBeat = Integer.parseInt(args[2]);
			if (args.length > 3)
				startTatum = Long.parseLong(args[3]);
			if (args.length > 4)
				endTatum = Long.parseLong(args[4]);
			if (startTatum != null)
				notes = notes.getSegment(startTatum, endTatum, true);
			notes.play(tatumsPerBeat, beatsPerMinute);
		} else if (args[0].toLowerCase().endsWith(".gt")) {
//			then this is a ground truth file and contains one or more lists of patterns
//			where each pattern is a list of opnd points.
//			We want PlayNotes to play each pattern and then prompt the user to continue
			System.out.println("Input file is a ground truth file.");
			int tatumsPerBeat = 8;
			float beatsPerMinute = 120.0f;
			if (args.length > 1)
				beatsPerMinute = Float.parseFloat(args[1]);
			if (args.length > 2)
				tatumsPerBeat = Integer.parseInt(args[2]);
			ArrayList<ArrayList<Notes>> listOfOccurrenceSets = Notes.readGroundTruthPatternsFromFile(args[0]);
			Scanner sc = new Scanner(System.in);
			int occurrenceSetIndex = 0;
			for(ArrayList<Notes> occurrenceSet : listOfOccurrenceSets) {
				System.out.println("Occurrence set number "+(++occurrenceSetIndex));
				int patternIndex = 0;
				for (Notes pattern : occurrenceSet) {
					System.out.println("  Now playing pattern "+(++patternIndex));
					System.out.println(pattern);
					long segmentStart = pattern.getNotes().first().getOnset();
					pattern.play((long)tatumsPerBeat, beatsPerMinute, segmentStart);
					if (patternIndex < occurrenceSet.size()) {
						System.out.println("Press ENTER to proceed to next pattern.");
						sc.nextLine();
					}
				}
			}
			
		}
	}
}
