package com.chromamorph.notes;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public class PlayNotes {
	public static void main(String[] args) throws InvalidMidiDataException, MidiUnavailableException {
		Notes notes = new Notes(args[0]);
		int tatumsPerBeat = Integer.parseInt(args[1]);
		float beatsPerMinute = Float.parseFloat(args[2]);
		notes.play(tatumsPerBeat, beatsPerMinute);
	}
}
