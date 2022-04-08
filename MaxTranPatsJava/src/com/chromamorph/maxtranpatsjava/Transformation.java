package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.TreeSet;

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
	
	public static TreeSet<Transformation> readTransformationsFromString(String transformationsString) throws InvalidArgumentException {
		if (transformationsString.equals("[]"))
			return new TreeSet<Transformation>();
		if (!transformationsString.startsWith("[T(") || !transformationsString.endsWith("])]"))
			throw new InvalidArgumentException("Transformation.readTransformationsFromString(String) called with invalid argument:\n"+transformationsString);
		String tranListStr = transformationsString.substring(1,transformationsString.length()-1);
		tranListStr = tranListStr.replace("), T(", ");T(");
		String[] tranArray = tranListStr.split(";");
		TreeSet<Transformation> transformations = new TreeSet<Transformation>();
		for(String tran : tranArray)
			transformations.add(new Transformation(tran));
		return transformations;
	}
	
	public Transformation(String transformationString) throws InvalidArgumentException {
		if (!transformationString.startsWith("T(") || !transformationString.endsWith("])"))
			throw new InvalidArgumentException("Transformation(String) constructor called with invalid argument String: "+transformationString);
		
		TransformationClass tc = null;
		if (transformationString.contains("F_2STR"))
			tc = new F_2STR();
		else if (transformationString.contains("F_2TR"))
			tc = new F_2TR();
		else if (transformationString.contains("F_2T"))
			tc = new F_2T();
		setTransformationClass(tc);
		
		ArrayList<Double> sigma = new ArrayList<Double>();
		int sigmaStart = transformationString.indexOf("[")+1;
		int sigmaEnd = transformationString.indexOf("]");
		String[] sigmaArray = transformationString.substring(sigmaStart,sigmaEnd).split(", ");
		for(String s : sigmaArray)
			sigma.add(Double.parseDouble(s));
		setSigma(sigma);
	}
	
	public int hash(int hashTableSize) {
		int m = hashTableSize;
		double A = 1.6180339887;
		double h = 1.0;
		int transformationClassId = getTransformationClass().getId();
		h = transformationClassId * A;
		for(double s : getSigma()) h *= Math.abs(s==0.0?0.000001:s) * A;
		h %= 1.0;
		return (int)Math.floor(h*m);
	}
}
