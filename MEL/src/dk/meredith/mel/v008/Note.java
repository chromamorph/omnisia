package dk.meredith.mel.v008;

public class Note implements Comparable<Note> {
	private int time, pitch;
	
	public Note(int time, int pitch) {
		super();
		this.time = time;
		this.pitch = pitch;
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
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Note)) return false;
		return compareTo((Note)obj) == 0;
	}
	
	public int compareTo(Note p) {
		int d = getTime() - p.getTime();
		if (d != 0) return d;
		d = getPitch() - p.getPitch();
		if (d != 0) return d;
		return 0;
	}
	
	public String toString() {
		return "["+time+","+pitch+"]";
	}
	
	public Note translate(Vector v) {
		return v.translate(this);
	}
	
}
