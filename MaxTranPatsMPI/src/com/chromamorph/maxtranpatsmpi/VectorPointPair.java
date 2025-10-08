package com.chromamorph.maxtranpatsmpi;

import com.chromamorph.maxtranpatsjava.Point;

public class VectorPointPair implements Comparable<VectorPointPair> {
	private Vector vector;
	private Point point;
	
	public VectorPointPair(Vector vector, Point point) {
		this.vector = vector;
		this.point = point;
	}
	
	public Vector getVector() {return vector;}
	public Point getPoint() {return point;}

	@Override
	public int compareTo(VectorPointPair o) {
		if (o == null) return 1;
		int d = getVector().compareTo(o.getVector());
		if (d != 0) return d;
		d = getPoint().compareTo(o.getPoint());
		return d;
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof VectorPointPair)) return false;
		return compareTo((VectorPointPair)o) == 0;
	}
	
	public String toString() {
		return "<"+getVector()+","+getPoint()+">";
	}

}
