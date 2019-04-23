package com.chromamorph.points022;

import com.chromamorph.notes.Note;
import com.chromamorph.pitch.Pitch;

public class OPNDV implements Comparable<OPNDV>{
	private Long onset = null;
	private Pitch pitch = null;
	private Long duration = null;
	private Integer voice = null;
	
	public OPNDV(Note note) {
		this(note.getOnset(),note.getPitchName(),note.getDuration(),note.getVoice());
	}
	
	public OPNDV(Long onset, String pitchName, Long duration, Integer voice) {
		setOnset(onset);
		setPitch(pitchName);
		setDuration(duration);
		setVoice(voice);
	}
	
	public void setOnset(Long onset) {
		this.onset = onset;
	}
	
	public void setPitch(String pitchName) {
		this.pitch = new Pitch();
		this.pitch.setPitchName(pitchName);
	}
	
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
	public void setVoice(Integer voice) {
		this.voice = voice;
	}
	
	public Long getOnset() {return onset;}
	public Pitch getPitch() {return pitch;}
	public Long getDuration() {return duration;}
	public Integer getVoice() {return voice;}
	
	public String toString() {
		return "("+onset+" "+pitch.getPitchName()+" "+duration+" "+voice+")";
	}
	
	public int compareTo(OPNDV o) {
		if (o == null) return 1;
		if (getOnset().compareTo(o.getOnset()) < 0) return -1;
		if (getOnset().compareTo(o.getOnset()) > 0) return 1;
		if (getPitch().compareTo(o.getPitch()) < 0) return -1;
		if (getPitch().compareTo(o.getPitch()) > 0) return 1;
		if (getDuration().compareTo(o.getDuration()) < 0) return -1;
		if (getDuration().compareTo(o.getDuration()) > 0) return 1;
		return getVoice().compareTo(o.getVoice());
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof OPNDV)) return false;
		return compareTo((OPNDV)o) == 0;
	}
}

