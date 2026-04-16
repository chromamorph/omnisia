package dk.meredith.mel.v009;

import java.util.ArrayList;

public class Note implements Comparable<Note> {

	private int time, pitch;
	
	public Note(int time, int pitch) {
		this.time = time;
		this.pitch = pitch;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Note)) return false;
		return compareTo((Note)obj) == 0;
	}
	
	public int compareTo(Note n) {
		int d = time - n.time;
		if (d != 0) return d;
		return pitch - n.pitch;
	}
	
	public String toString() {
		return "["+time+","+pitch+"]";
	}

	public Note translate(VectorSum vectorSum) {
		ArrayList<Vector> vectors = vectorSum.getVectors();
		
		int newTime = time;
		int newMaskedTime;
		for(Vector vector : vectors) {
			newMaskedTime = vector.getTimeMaskSequence().mask(newTime);
			newMaskedTime += vector.getTime();
			newTime = vector.getTimeMaskSequence().unmask(newMaskedTime);
		}

		int newPitch = pitch;
		int newMaskedPitch;
		for(Vector vector : vectors) {
			newMaskedPitch = vector.getPitchMaskSequence().mask(newPitch);
			newMaskedPitch += vector.getPitch();
			newPitch = vector.getPitchMaskSequence().unmask(newMaskedPitch);
		}

		return new Note(newTime, newPitch);

	}
	
}
