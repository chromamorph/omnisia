package com.chromamorph.points022;

import com.chromamorph.pitch.Pitch;

public class PitchDurationPair {
	private Pitch pitch;
	private long duration;
	
	public PitchDurationPair(Pitch pitch, int duration) {
		setPitch(pitch);
		setDuration(duration);
	}
	
	public Pitch getPitch() {
		return pitch;
	}
	public void setPitch(Pitch pitch) {
		this.pitch = pitch;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	@Override
	public String toString() {
		return "("+getPitch()+","+getDuration()+")";
	}
}
