package com.chromamorph.maxtranpatsjava;

import java.util.Collection;
import java.util.TreeSet;

public class PointSetPointSetTransformationSetTriple implements Comparable<PointSetPointSetTransformationSetTriple> {
	private PointSet objectPointSet, imagePointSet;
	private TreeSet<Transformation> transformationSet;
	
	public PointSetPointSetTransformationSetTriple(PointSet objectPointSet, PointSet imagePointSet, Collection<Transformation> transformations) {
		setObjectPointSet(objectPointSet);
		setImagePointSet(imagePointSet);
		setTransformationSet(transformations);
	}
	
	@Override
	public int compareTo(PointSetPointSetTransformationSetTriple o) {
		if (o == null) return 1;
		
		if (getObjectPointSet() == null) return -1;
		if (o.getObjectPointSet() == null) return 1;
		int d = getObjectPointSet().compareTo(o.getObjectPointSet());
		if (d != 0) return d;
		
		if (getImagePointSet() == null) return -1;
		if (o.getImagePointSet() == null) return 1;
		d = getImagePointSet().compareTo(o.getImagePointSet());
		if (d != 0) return d;

		System.out.println(">>>>>>>ERROR!!!! PointSetPointSetTransformationSetTriple.compareTo() given two triples with same image and object PointSets<<<<<<<");
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof PointSetPointSetTransformationSetTriple)) return false;
		return compareTo((PointSetPointSetTransformationSetTriple)obj)==0;
	}
	
	public PointSet getObjectPointSet() {
		return objectPointSet;
	}
	
	public void setObjectPointSet(PointSet objectPointSet) {
		this.objectPointSet = objectPointSet;
	}
	
	public PointSet getImagePointSet() {
		return imagePointSet;
	}
	
	public void setImagePointSet(PointSet imagePointSet) {
		this.imagePointSet = imagePointSet;
	}
	
	public TreeSet<Transformation> getTransformationSet() {
		return transformationSet;
	}
	
	public void setTransformationSet(Collection<Transformation> transformations) {
		this.transformationSet = new TreeSet<Transformation>(transformations);
	}
	
}
