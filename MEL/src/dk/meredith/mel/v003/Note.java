package dk.meredith.mel.v003;

public class Note implements Comparable<Note> {
	
	public int onset, pitch, duration, generation;
	
	public Note(int onset, int pitch, int duration) {
		this.onset = onset;
		this.pitch = pitch;
		this.duration = duration;
	}
	
	public int compareTo(Note n) {
		int d = generation - n.generation;
		if (d != 0) return d;
		d = onset - n.onset;
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
	
	public Note translate(Vector v) {
		return new Note(onset+v.time, pitch+v.pitch, duration);
	}
	
	public Note t(Vector v) {
		return translate(v);
	}
	
	public Note translate(int t, int p) {
		return new Note(onset+t,pitch+p,duration);
	}
	
	public Note t(int t, int p) {
		return translate(t,p);
	}
	
	public NoteSet translate(VectorSet vectorSet) {
		NoteSet ns = new NoteSet();
		for(Vector v : vectorSet.getVectors())
			ns.add(translate(v));
		return ns;
	}
	
	public NoteSet t(VectorSet vectorSet) {
		return translate(vectorSet);
	}

	public String toString() {
		return "["+onset+","+pitch+","+duration+","+generation+"]";
	}
	
}
