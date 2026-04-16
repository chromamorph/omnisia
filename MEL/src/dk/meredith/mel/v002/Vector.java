package dk.meredith.mel.v002;

class Vector implements Comparable<Vector>{
	
	public int time, pitch;
	
	public Vector(int time, int pitch) {
		this.time = time;
		this.pitch = pitch;
	}
	
	public Vector add(Vector v) {
		return new Vector(time+v.time,pitch+v.pitch);
	}
	
	public Vector minus(Vector v) {
		return new Vector(time-v.time,pitch-v.pitch);
	}
	
	public int compareTo(Vector n) {
		int d = time - n.time;
		if (d != 0) return d;
		d = pitch - n.pitch;
		if (d != 0) return d;
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Vector)) return false;
		return compareTo((Vector)o) == 0;
	}
	
	public String toString() {
		return "["+time+","+pitch+"]";
	}

	
}

