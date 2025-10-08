package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public abstract class TransformationClass implements Comparable<TransformationClass> {
	
	static private int numClasses = 0;
	
	protected int sigmaLength = 0;
	private final int id;
	private String name = "Unnamed";
	protected int basisSize = 0;
	protected ArrayList<Double> identitySigma = null;
	private long sigmaComplexity = -1;
	int[][] perms;
	
	@Override
	public int compareTo(TransformationClass o) {
		if (o == null) return 1;
		int d = getSigmaLength() - o.getSigmaLength();
		if (d != 0) return d;
		return name.compareTo(o.name);
	}
	
	protected TreeSet<Transformation> transformationInstances = new TreeSet<Transformation>();
	
	public void setPerms() {
		perms = Utility.computePermutationIndexSequences(basisSize);
	}
	
	public int[][] getPerms() {
		return perms;
	}
	
	public int[] getPerm(long i) {
		return perms[(int)i];
	}
	
	public TreeSet<Transformation> getTransformationInstances() {
		return transformationInstances;
	}
	
	public void addTransformationInstance(Transformation transformationInstance) {
		this.transformationInstances.add(transformationInstance);
		sigmaComplexity = -1;
	}
	
	public void setTransformationInstances(Collection<Transformation> transformationInstances) {
		this.transformationInstances.addAll(transformationInstances);
		sigmaComplexity = -1;
	}
	
//	public long getSigmaComplexity() {
//		if (sigmaComplexity != -1) return sigmaComplexity;
//		long sc = 0;
//		for(int sigmaIndex = 0; sigmaIndex < sigmaLength; sigmaIndex++) {
//			TreeSet<Double> valuesForThisSigmaIndex = new TreeSet<Double>();
//			for (Transformation f : transformationInstances) {
//				valuesForThisSigmaIndex.add(f.getSigma().get(sigmaIndex));
//			}
//			ArrayList<Long> intValues = new ArrayList<Long>();
//			for(Double d : valuesForThisSigmaIndex)
//				intValues.add((long)Math.round(d*1000000000));
//			long minValueForThisSigmaIndex = intValues.get(0);
//			for(int i = 0; i < intValues.size(); i++)
//				intValues.set(i, intValues.get(i) - minValueForThisSigmaIndex);
//			long gcd = Utility.gcd(intValues);
//			long maxIndex = intValues.get(intValues.size()-1)/gcd;
//			long complexityForThisSigmaIndex = (long)Math.ceil(Math.log1p(maxIndex)/Math.log(2.0));
//			sc += complexityForThisSigmaIndex;
//		}
//		sigmaComplexity = sc;
//		return sc;
//	}
	
	public TransformationClass() {
		id = ++numClasses;
	}
	
	abstract Point phi(ArrayList<Double> sigma, Point p);
	abstract ArrayList<ArrayList<Double>> getSigmas(PointSequence objectBasis, PointSequence imageBasis);
//	abstract ArrayList<Double> getInverseSigma(ArrayList<Double> sigma);
	
	public ArrayList<Double> getIdentitySigma() {
		return identitySigma;
	};

	public PointSet phi(ArrayList<Double> sigma, PointSet pointSet) {
		PointSet ps = new PointSet();
		for(Point p : pointSet.getPointsArray())
			ps.add(phi(sigma,p));
		return ps;
	}	
	
	public PointSequence phi(ArrayList<Double> sigma, PointSequence pointSequence) {
		PointSequence seq = new PointSequence();
		for(Point p : pointSequence.getPoints())
			seq.add(phi(sigma,p));
		return seq;
	}
	
	protected void setName(String name) {this.name = name;}
	public String getName() {return name;}
	
	public int getSigmaLength() {return sigmaLength;}
	protected void setSigmaLength(int sigmaLength) {this.sigmaLength = sigmaLength;}
	
	public int getBasisSize() {return basisSize;}
	protected void setBasisSize(int basisSize) {this.basisSize = basisSize;}
	
	public int getNumClasses() {return numClasses;}
	public int getId() {return id;}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TransformationClass)) return false;
		return compareTo((TransformationClass)obj)==0;
	}
	
	@Override
	public String toString() {
		return "TC("+getId()+","+getName()+")";
	}
	
	public static String getTransformationClassesString(TransformationClass[] transformationClasses) {
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < transformationClasses.length; i++) {
			TransformationClass tc = transformationClasses[i];
			s.append(tc.getName());
			if (i < transformationClasses.length - 1)
				s.append("-");
		}
		return s.toString().replace("_", "");
	}
	
}
