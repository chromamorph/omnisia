package com.chromamorph.maxtranpatsjava;

public class TransformationPointSequencePair implements Comparable<TransformationPointSequencePair>{
	private Transformation transformation;
	private PointSequence pointSequence;
	
	public TransformationPointSequencePair(Transformation transformation, PointSequence pointSequence) {
		setTransformation(transformation);
		setPointSequence(pointSequence);
	}
	
	public Transformation getTransformation() {
		return transformation;
	}
	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}
	public PointSequence getPointSequence() {
		return pointSequence;
	}
	public void setPointSequence(PointSequence pointSequence) {
		this.pointSequence = pointSequence;
	}
	
	public int compareTo(TransformationPointSequencePair o) {
		if (o == null) return 1;
		int d = getTransformation().compareTo(o.getTransformation());
		if (d != 0) return d;
		return getPointSequence().compareTo(o.getPointSequence());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TransformationPointSequencePair)) return false;
		return compareTo((TransformationPointSequencePair)obj)==0;
	}
	
}
