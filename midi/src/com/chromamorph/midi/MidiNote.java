package com.chromamorph.midi;

public class MidiNote implements Comparable<MidiNote>{
	public Long onset = null;
	public Integer midiNoteNumber = null;
	public Integer velocity = null;
	public Long duration = null;
	public Integer channel = null;
	public Integer track = null;
	public String pitchName = null;
	
	public static Long ONSET = null;
	public static Long DURATION = null;
	public static Long INTER_ONSET = null;
	public static boolean CHORD = false;
	public static String PITCH_NAME = null;

	public MidiNote(Long onset, Integer midiNoteNumber, Integer velocity, Long duration, Integer channel, Integer track) {
		this.onset = onset;
		this.midiNoteNumber = midiNoteNumber;
		this.velocity = velocity;
		this.duration = duration;
		this.channel = channel;
		this.track = track;
	}
	
	public MidiNote(String l) {
		//Assume l has form: Staff PitchName Onset Offset Voice
		String[] array = l.split(" ");
		pitchName = array[1];
		onset = Long.parseLong(array[2]);
		duration = Long.parseLong(array[3])-onset;
	}
	
	public MidiNote(Long onset, Integer midiNoteNumber, Integer velocity, Long duration, Integer channel, Integer track, String pitchName) {
		this.onset = onset;
		this.midiNoteNumber = midiNoteNumber;
		this.velocity = velocity;
		this.duration = duration;
		this.channel = channel;
		this.track = track;
		this.pitchName = pitchName;
	}
	
	public MidiNote() {
		onset = ONSET;
		duration = DURATION;
		if (INTER_ONSET != null && CHORD == false)
			ONSET += INTER_ONSET;
		else if (CHORD == false)
			ONSET += DURATION;
		pitchName = PITCH_NAME;
	}
	
	public static String noteTableHeader() {
		return "ONSET\tMIDI\tVEL\tDUR\tCHAN\tTRACK\tPN";
	}
	public String toString() {
		return onset+"\t"+midiNoteNumber+"\t"+velocity+"\t"+duration+"\t"+channel+"\t"+track+"\t"+pitchName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MidiNote) return compareTo((MidiNote)obj) == 0;
		return false;
	}
	
	@Override
	public int compareTo(MidiNote n) {
		if (track != null || n.track != null) {
			if (track == null) return -1;
			if (n.track == null) return 1;
			if (track < n.track) return -1;
			if (track > n.track) return 1;
		}
		if (channel != null || n.channel != null) {
			if (channel == null) return -1;
			if (n.channel == null) return 1;
			if (channel < n.channel) return -1;
			if (channel > n.channel) return 1;
		}
		if (onset != null || n.onset != null) {
			if (onset == null) return -1;
			if (n.onset == null) return 1;
			if (onset < n.onset) return -1;
			if (onset > n.onset) return 1;
		}
		if (midiNoteNumber != null || n.midiNoteNumber != null) {
			if (midiNoteNumber == null) return -1;
			if (n.midiNoteNumber == null) return 1;
			if (midiNoteNumber < n.midiNoteNumber) return -1;
			if (midiNoteNumber > n.midiNoteNumber) return 1;
		}
		if (pitchName != null || n.pitchName != null) {
			if (pitchName == null) return -1;
			if (n.pitchName == null) return 1;
			return pitchName.compareTo(n.pitchName);
		}
		if (velocity != null || n.velocity != null) {
			if (velocity == null) return -1;
			if (n.velocity == null) return 1;
			if (velocity < n.velocity) return -1;
			if (velocity > n.velocity) return 1;
		}
		if (duration != null || n.duration != null) {
			if (duration == null) return -1;
			if (n.duration == null) return 1;
			if (duration < n.duration) return -1;
			if (duration > n.duration) return 1;
		}
		return 0;
	}
}
