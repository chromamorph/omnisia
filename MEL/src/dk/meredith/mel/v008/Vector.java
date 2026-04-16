package dk.meredith.mel.v008;

public class Vector implements Comparable<Vector> {
	private int time, pitch;
	private MaskSequence timeMaskSequence, pitchMaskSequence;
	
	public Vector(int time, int pitch, MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence) {
		setTime(time);
		setPitch(pitch);
		setTimeMaskSequence(timeMaskSequence);
		setPitchMaskSequence(pitchMaskSequence);
	}
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getPitch() {
		return pitch;
	}
	public void setPitch(int pitch) {
		this.pitch = pitch;
	}
	public MaskSequence getTimeMaskSequence() {
		return timeMaskSequence;
	}
	public void setTimeMaskSequence(MaskSequence timeMaskSequence) {
		this.timeMaskSequence = timeMaskSequence;
	}
	public MaskSequence getPitchMaskSequence() {
		return pitchMaskSequence;
	}
	public void setPitchMaskSequence(MaskSequence pitchMaskSequence) {
		this.pitchMaskSequence = pitchMaskSequence;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Vector)) return false;
		return compareTo((Vector)obj) == 0;
	}
	
	public int compareTo(Vector n) {
		int d = getTime() - n.getTime();
		if (d != 0) return d;
		d = getPitch() - n.getPitch();
		if (d != 0) return d;
		d = getTimeMaskSequence().compareTo(n.getTimeMaskSequence());
		if (d != 0) return d;
		d = getPitchMaskSequence().compareTo(n.getPitchMaskSequence());
		if (d != 0) return d;
		return 0;
	}
	
	public String toString() {
		return "["+getTime()+","+getPitch()+","+getTimeMaskSequence()+","+getPitchMaskSequence()+"]";
	}
	
	public Note translate(Note note) {
		int maskedOldTime = getTimeMaskSequence().getMaskedValue(note.getTime());
		int maskedNewTime = maskedOldTime + getTime();
		int newTime = getTimeMaskSequence().getUnmaskedValue(maskedNewTime);
		int maskedOldPitch = getPitchMaskSequence().getMaskedValue(note.getPitch());
		int maskedNewPitch = maskedOldPitch + getPitch();
		int newPitch = getPitchMaskSequence().getUnmaskedValue(maskedNewPitch);
		return new Note(newTime, newPitch);
	}
	
	public static void main(String[] args) {
		Mask timeMask1 = new Mask(0,2);
		Mask timeMask2 = new Mask(0,3);
		Mask pitchMask1 = new Mask(0,2);
		Mask pitchMask2	= new Mask(0,3);
		MaskSequence timeMaskSequence = new MaskSequence(timeMask1,timeMask2);
		MaskSequence pitchMaskSequence = new MaskSequence(pitchMask1,pitchMask2);
		Vector v = new Vector(1,1,timeMaskSequence,pitchMaskSequence);
		Note n = new Note(0,0);
		System.out.println(v.translate(n));
		System.out.println(n.translate(v));
	}
}
