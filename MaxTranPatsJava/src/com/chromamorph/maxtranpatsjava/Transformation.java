package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class Transformation implements Comparable<Transformation>{
	private TransformationClass transformationClass;
	private ArrayList<Double> sigma;
	
	public Transformation(TransformationClass transformationClass, ArrayList<Double> sigma) {
		setTransformationClass(transformationClass);
		setSigma(sigma);
	}
	
	public Transformation getInverse() {
		return new Transformation(transformationClass, transformationClass.getInverseSigma(sigma));
	}
	
	public static ArrayList<Transformation> getTransformations(TransformationClass transformationClass, PointSequence objectBasis, PointSequence imageBasis) {
		ArrayList<ArrayList<Double>> sigmas = transformationClass.getSigmas(objectBasis,imageBasis);
		ArrayList<Transformation> transformations = new ArrayList<Transformation>();
		for(ArrayList<Double> sigma : sigmas)
			transformations.add(new Transformation(transformationClass, sigma));
		return transformations;
	}
	
	public TransformationClass getTransformationClass() {
		return transformationClass;
	}
	public void setTransformationClass(TransformationClass transformationClass) {
		this.transformationClass = transformationClass;
	}
	public ArrayList<Double> getSigma() {
		return sigma;
	}
	public void setSigma(ArrayList<Double> sigma) {
		if (sigma == null) {
			this.sigma = null;
			return;
		}
		this.sigma = new ArrayList<Double>();
		for(Double d : sigma)
			if (d == -0.0)
				this.sigma.add(0.0);
			else
				this.sigma.add(d);
	}
	
	@Override
	public int compareTo(Transformation o) {
		if (o == null) return 1;
		int d = getTransformationClass().compareTo(o.getTransformationClass());
		if (d != 0) return d;
		return Utility.compareToArrayListOfDoubles(getSigma(), o.getSigma());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Transformation)) return false; 
		return compareTo((Transformation)obj)==0;
	}
	
	public PointSet phi(PointSet pointSet) {
		return getTransformationClass().phi(getSigma(), pointSet);
	}
	
	public Point phi(Point point) {
		return getTransformationClass().phi(getSigma(), point);
	}
	
	@Override
	public String toString() {
		return "T("+getTransformationClass().getName()+","+getSigma()+")";
	}
	
	public int getSigmaLength() {
		return getTransformationClass().getSigmaLength();
	}
	
//	public long getSigmaComplexity() {
//		return getTransformationClass().getSigmaComplexity();
//	}
	
}
