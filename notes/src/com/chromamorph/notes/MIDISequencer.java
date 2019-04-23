package com.chromamorph.notes;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.SwingUtilities;

public class MIDISequencer {

	private Notes notes = null;
	private int beatsPerMinute = 120;
	private long tatumsPerBeat = 60l;

	public MIDISequencer(String midiFileName) {
		if (midiFileName != null) {
			try {
				notes = Notes.fromMIDI(midiFileName);
				notes.play(tatumsPerBeat, beatsPerMinute);
			} catch (InvalidMidiDataException | IOException e) {
				e.printStackTrace();
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		String fileName = null;
		if (args.length > 0)
			fileName = args[0];
		final String actualFileName = fileName;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MIDISequencer(actualFileName);
			}
		});
	}
}
