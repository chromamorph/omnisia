package dk.meredith.mel.v009;

public class Vector {

	private int time, pitch;
	private MaskSequence timeMaskSequence, pitchMaskSequence;
	
	public static Vector ZERO = new Vector(MaskSequence.BASIC, MaskSequence.BASIC, 0,0);
	
	public Vector(MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence, int time, int pitch) {
		this.time = time;
		this.pitch = pitch;
		this.timeMaskSequence = timeMaskSequence;
		this.pitchMaskSequence = pitchMaskSequence;
	}

	public Vector inv() {
		return new Vector(timeMaskSequence, pitchMaskSequence, time, -pitch);
	}

	public MaskSequence getTimeMaskSequence() {
		return timeMaskSequence;
	}

	public MaskSequence getPitchMaskSequence() {
		return pitchMaskSequence;
	}
	
	public int getTime() {
		return time;
	}
	
	public int getPitch() {
		return pitch;
	}
	
	public String toString() {
		return "["+time+","+pitch+","+timeMaskSequence+","+pitchMaskSequence+"]";
	}
}
