package com.chromamorph.maxtranpatsjava;

import java.util.TreeSet;


public class PointFreq implements Comparable<PointFreq> {

	private Point point;
	private int freq;
	private boolean isMaxPoint = false;
	private TreeSet<Transformation> transformations = new TreeSet<Transformation>();

	public PointFreq(Point point, int freq) {
		super();
		setPoint(point);
		setFreq(freq);
	}

	public PointFreq(Point point, int freq, Transformation transformation) {
		super();
		setPoint(point);
		setFreq(freq);
		transformations.add(transformation);
	}

	public boolean isMaxPoint() {
		return isMaxPoint;
	}
	
	public void setMaxPoint(boolean isMaxPoint) {
		this.isMaxPoint = isMaxPoint;
	}
	
	public TreeSet<Transformation> getTransformations() {
		return transformations;
	}
	
	public void addTransformation(Transformation transformation) {
		transformations.add(transformation);
	}
	
	public int compareTo(PointFreq pf) {
		return getPoint().compareTo(pf.getPoint());
	};

	public boolean equals(Object obj) {
		if (!(obj instanceof PointFreq)) return false;
		return compareTo((PointFreq) obj) == 0;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public void incFreq() {
		this.freq++;
	}

	@Override
	public String toString() {
		return "PointFreq("+point+","+freq+")";
	}
	
//	public String getLatexString() {
//		if (transformations.isEmpty())
//			return "\\langle"+getFreq()+","+getPoint().getLatexString()+"\\rangle";
//		ArrayList<Transformation> transformationArray = new ArrayList<Transformation>();
//		transformationArray.addAll(transformations);
//		StringBuilder sb = new StringBuilder("\\lbrace"+transformationArray.get(0).getLatexString());
//		for(int i=1;i < transformationArray.size(); i++)
//			sb.append(","+transformationArray.get(i).getLatexString());
//		sb.append("\\rbrace");
//		
//		return "\\langle"+getPoint().getLatexString()+","+sb.toString()+"\\rangle";
//	}
}

