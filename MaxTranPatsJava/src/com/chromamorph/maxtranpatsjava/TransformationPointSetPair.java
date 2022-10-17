package com.chromamorph.maxtranpatsjava;

public class TransformationPointSetPair implements Comparable<TransformationPointSetPair>{
	
	private Transformation transformation;
	private PointSet pointSet;
	
	
	public TransformationPointSetPair(Transformation transformation, PointSet pointSet) {
		Transformation inverseTransformation = transformation.getInverse();
//		if (inverseTransformation.compareTo(transformation) > 0) {
////			Store inverse transformation and its maximal transformable pattern instead
//			setTransformation(inverseTransformation);
//			setPointSet(transformation.phi(pointSet));
//		} else {
			setTransformation(transformation);
			setPointSet(pointSet);
//		}
	}
	
	private void setPointSet(PointSet pointSet) {
		this.pointSet = pointSet;
	}
	
	public PointSet getPointSet() {
		return pointSet;
	}
	
	private void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}
	
	public Transformation getTransformation() {
		return transformation;
	}
	
	@Override
	public int compareTo(TransformationPointSetPair o) {
		if (o == null) return 1;
		int d = transformation.getTransformationClass().compareTo(o.getTransformation().getTransformationClass());
		if (d != 0) return d;
		d = o.getPointSet().size()-pointSet.size();
		if (d != 0) return d;
	    d = transformation.compareTo(o.getTransformation());
	    if (d != 0) return d;
	    PointSet ps = o.getPointSet();
	    return getPointSet().compareTo(ps);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TransformationPointSetPair)) return false;
		return compareTo((TransformationPointSetPair)obj) == 0;
	}
	
	@Override
	public String toString() {
		return "TPSP("+getTransformation()+","+getPointSet()+")";
	}
	
	public void addPoint(Point p) {
		pointSet.add(p);
	}
}
