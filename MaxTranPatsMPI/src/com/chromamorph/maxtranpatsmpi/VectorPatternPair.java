package com.chromamorph.maxtranpatsmpi;

import com.chromamorph.maxtranpatsjava.PointSet;

public class VectorPatternPair implements Comparable<VectorPatternPair>{

	private Vector vector;
	private PointSet pattern;
	
	public VectorPatternPair(Vector vector, PointSet pattern) {
		this.vector = vector;
		this.pattern = pattern;
	}
	
	public Vector getVector() {return vector;}
	public PointSet getPattern() {return pattern;}

	@Override
	public int compareTo(VectorPatternPair o) {
		if (o == null) return 1;
		int d = getVector().compareTo(o.getVector());
		if (d != 0) return d;
		d = getPattern().compareTo(o.getPattern());
		return d;
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof VectorPatternPair)) return false;
		return compareTo((VectorPatternPair)o) == 0;
	}
	
	public String toString() {
		return "<"+getVector()+","+getPattern()+">";
	}
	
}
