package com.chromamorph.midi;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

public class PrintAllMIDIEventsInFile {

//	public static String fileName = "data/2010-10-28-Dr-Gradus-Ad-Parnassum.mid";
	public static String fileName = "midiFile.mid";
	
	public static Sequencer sequencer;
	public static File midiFile;
	public static Sequence sequence;
	
	public static void main(String[] args) {
		readMIDIFileIntoSequencer();
		printAllMIDIEventsInSequencer();
	}

	private static void printAllMIDIEventsInSequencer() {
		try {
			sequencer.open();
			Track[] tracks = sequencer.getSequence().getTracks();
			for(int trackIndex = 0; trackIndex < tracks.length; trackIndex ++) {
				Track track = tracks[trackIndex];
				for(int eventIndex = 0; eventIndex < track.size(); eventIndex++) {
					MidiEvent event = track.get(eventIndex);
					Long tick = event.getTick();
					MidiMessage message = event.getMessage();
					if (message instanceof ShortMessage) 
						System.out.println("TRACK:"+trackIndex+", TICK:"+tick+", "+getShortMessageString((ShortMessage)message));
					else if (message instanceof MetaMessage)
						System.out.println("TRACK:"+trackIndex+", TICK:"+tick+", "+getMetaMessageString((MetaMessage)message));
					else if (message instanceof SysexMessage)
						System.out.println("TRACK:"+trackIndex+", TICK:"+tick+", "+getSysexMessageString((SysexMessage)message));
				}
			}
			sequencer.close();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	private static String getShortMessageString(ShortMessage shortMessage) {
		int channel = shortMessage.getChannel();
		int command = shortMessage.getCommand();
		int data1 = shortMessage.getData1();
		int data2 = shortMessage.getData2();
		int status = shortMessage.getStatus();
		String 	statusString = "UNKNOWN", 
				commandString = "UNKNOWN", 
				data1String = "UNKNOWN", 
				data2String = "UNKNOWN";
		if (status == ShortMessage.ACTIVE_SENSING) statusString = "ACTIVE_SENSING";
		if (command == ShortMessage.CHANNEL_PRESSURE) commandString = "CHANNEL_PRESSURE";
		if (status == ShortMessage.CONTINUE) statusString = "CONTINUE";
		if (command == ShortMessage.CONTROL_CHANGE) commandString = "CONTROL_CHANGE";
		if (status == ShortMessage.END_OF_EXCLUSIVE) statusString = "END_OF_EXCLUSIVE";
		if (status == ShortMessage.MIDI_TIME_CODE) statusString = "MIDI_TIME_CODE";
		if (command == ShortMessage.NOTE_OFF) {
			commandString = "NOTE_OFF";
			data1String = "PITCH="+data1;
			data2String = "VEL="+data2;
		}
		if (command == ShortMessage.NOTE_ON) {
			commandString = "NOTE_ON";
			data1String = "PITCH="+data1;
			data2String = "VEL="+data2;
		}
		if (command == ShortMessage.PITCH_BEND) commandString = "PITCH_BEND";
		if (command == ShortMessage.POLY_PRESSURE) commandString = "POLY_PRESSURE";
		if (command == ShortMessage.PROGRAM_CHANGE) commandString = "PROGRAM_CHANGE";
		if (status == ShortMessage.SONG_POSITION_POINTER) statusString = "SONG_POSITION_POINTER";
		if (status == ShortMessage.SONG_SELECT) statusString = "SONG_SELECT";
		if (status == ShortMessage.START) statusString = "START";
		if (status == ShortMessage.STOP) statusString = "STOP";
		if (status == ShortMessage.SYSTEM_RESET) statusString = "SYSTEM_RESET";
		if (status == ShortMessage.TIMING_CLOCK) statusString = "TIMING_CLOCK";
		if (status == ShortMessage.TUNE_REQUEST) statusString = "TUNE_REQUEST";
		return "ShortMessage, "+
		"channel: "+channel+", "+
		"command: "+commandString+" ("+command+"), "+
		"status: "+statusString+" ("+status+"), "+
		"data1: "+data1String+" ("+data1+"), " +
		"data2: "+data2String+" ("+data2+")";
	}
	
	private static String getMetaMessageString(MetaMessage metaMessage) {
		byte[] data = metaMessage.getData();
		String dataString = new String(data);
		return "MetaMessage, "+
		"status: "+metaMessage.getStatus()+", "+
		"type: "+metaMessage.getType()+", "+
		"data: "+dataString+" ("+data+")";
	}
	
	private static String getSysexMessageString(SysexMessage sysexMessage) {
		int status = sysexMessage.getStatus();
		String statusString = "UNKNOWN";
		if (status == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE)
			statusString = "SPECIAL_SYSTEM_EXCLUSIVE";
		if (status == SysexMessage.SYSTEM_EXCLUSIVE)
			statusString = "SYSTEM_EXCLUSIVE";
		byte[] data = sysexMessage.getData();
		String dataString = new String(data);
		return "SysexMessage, "+
		"status: "+statusString+" ("+status+")"+
		"data: "+dataString+" ("+data+")";
	}
	
	private static void readMIDIFileIntoSequencer() {
		try {
			sequencer = MidiSystem.getSequencer();
			midiFile = new File(fileName);
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
	
}
