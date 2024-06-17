package com.chromamorph.notes;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public class PlayNotes {
	public static void main(String[] args) throws InvalidMidiDataException, MidiUnavailableException {
		
		if (args.length == 0 || args[0].trim().startsWith("-h")) {
			System.out.println("Syntax:\n java -jar playnotes.jar <file-name> [<beats-per-minute> [<tatums-per-beat> [<start-tatum> [<end-tatum>]]]]");
			return;
		}
		
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
	}
}
