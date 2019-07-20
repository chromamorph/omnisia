package com.chromamorph.midi;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class ReadAndPlayMIDIFile {
	
//	private static String fileName = "data/Pathetique.mid";
//	private static String fileName = "midiFile2.mid";
	private static FileInputStream midiFile;
	private static Sequencer sequencer;
	private static Sequence sequence;
	private static Scanner sc;
	
	private static void readMIDIFileIntoSequencer() {
		try {
			System.out.println("Please enter the name of the MIDI file to play: ");
			String fileName = sc.nextLine();
			sequencer = MidiSystem.getSequencer();
			midiFile = new FileInputStream(fileName);
			sequence = MidiSystem.getSequence(midiFile);
			sequencer.setSequence(sequence);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void playSequenceInSequencer() {
		try {
			sequencer.open();
			sequencer.start();
			System.out.println("Press ENTER when finished playing.");
			System.in.read();
			sequencer.stop();
			sequencer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		sc = new Scanner(System.in);
		readMIDIFileIntoSequencer();
		playSequenceInSequencer();
		try {
			midiFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sc.close();
	}
}
