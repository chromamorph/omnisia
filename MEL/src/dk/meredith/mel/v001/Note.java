package dk.meredith.mel.v001;

public class Note implements Comparable<Note> {
	
	public int onset, pitch, duration;
	
	public Note(int onset, int pitch, int duration) {
		this.onset = onset;
		this.pitch = pitch;
		this.duration = duration;
	}
	
	public int compareTo(Note n) {
		int d = onset - n.onset;
		if (d != 0) return d;
		d = pitch - n.pitch;
		if (d != 0) return d;
		d = duration - n.duration;
		if (d != 0) return d;
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Note)) return false;
		return compareTo((Note)o) == 0;
	}
	
	public Note t(Vector v) {
		return new Note(onset+v.time, pitch+v.pitch, duration);
	}
	
	public Note t(int t, int p) {
		return new Note(onset+t,pitch+p,duration);
	}

	public String toString() {
		return "["+onset+","+pitch+","+duration+"]";
	}
	
}
