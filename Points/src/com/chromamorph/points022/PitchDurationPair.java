package com.chromamorph.points022;

import com.chromamorph.pitch.Pitch;

public class PitchDurationPair {
	private Pitch pitch = null;
	private long duration = 0l;
	private boolean isEnding = false;
	private boolean isEndingStart = false;
	private int endingNumber = 0;
	private boolean isRepeat = false;
	private boolean isBackward = false;
	private boolean isBackup = false;
	private int backupDuration = 0;
	
	public PitchDurationPair(int backupDuration) {
		setIsBackup(true);
		setBackupDuration(backupDuration);
	}
	
	public PitchDurationPair(Pitch pitch, int duration) {
		setPitch(pitch);
		setDuration(duration);
	}
	
	public PitchDurationPair(boolean isEndingStart, int endingNumber) {
		setDuration(0);
		setPitch(null);
		setIsEnding(true);
		setIsEndingStart(isEndingStart);
		setEndingNumber(endingNumber);
	}
	
	public PitchDurationPair(boolean isBackward) {
		setPitch(null);
		setDuration(0);
		setIsRepeat(true);
		setIsBackward(isBackward);
	}
	
	public void setIsBackup(boolean isBackup) {
		this.isBackup = isBackup;
	}
	
	public void setBackupDuration(int backupDuration) {
		this.backupDuration = backupDuration;
	}
	
	public boolean isBackup() {
		return isBackup;
	}
	
	public int getBackupDuration() {
		return backupDuration;
	}
	
	public void setIsRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}
	
	public void setIsBackward(boolean isBackward) {
		this.isBackward = isBackward;
	}
	
	public boolean isRepeat() {
		return isRepeat;
	}
	
	public boolean isBackward() {
		return isBackward;
	}
	
	public boolean isForward() {
		return !isBackward;
	}
	
	public void setIsEnding(boolean isEnding) {
		this.isEnding = isEnding;
	}
	
	public void setIsEndingStart(boolean isEndingStart) {
		this.isEndingStart = isEndingStart;
	}
	
	public void setEndingNumber(int endingNumber) {
		this.endingNumber = endingNumber;
	}
	
	public boolean isEnding() {return isEnding;}
	public boolean isEndingStart() {return isEndingStart;}
	public boolean isEndingStop() {return !isEndingStart;}
	public int getEndingNumber() {return endingNumber;}
	
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
		if (isBackup)
			return "Backup(" + getBackupDuration() + ")";
		if (getPitch() == null && isRepeat()) 
			return "Repeat(" + (isBackward()?"backward":"forward")+")";
		if (getPitch() == null && isEnding())
			return "Ending(" + getEndingNumber() + "," + (isEndingStart()?"start":"stop")+")";
		return "("+getPitch()+","+getDuration()+")";
	}
}
