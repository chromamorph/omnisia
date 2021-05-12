package com.chromamorph.maxtranpatsjava;


public class TransformationPointPair implements Comparable<TransformationPointPair>{
	private Transformation transformation;
	private Point point;
	private Integer index;

//	public TransformationPointPair(Point p1, Point p2, int columnIndex) {
//		transformation = new Transformation(p1,p2);
//		point = p1;
//		this.index = columnIndex;
//	}
//	
//	public TransformationPointPair(Point p1, Point p2) {
//		transformation = new Transformation(p1,p2);
//		point = p1;
//	}
	
	public TransformationPointPair(Transformation f, Point p) {
		this.transformation = f;
		this.point = p;
	}

	@Override
	public int compareTo(TransformationPointPair fp) {
		if (fp == null) return 1;
		if (fp.transformation == null) return 1;
		if (fp.point == null) return 1;
		int d = transformation.compareTo(fp.transformation);
		if (d != 0) return d;
		return (point.compareTo(fp.point));
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof TransformationPointPair)) return false;
		return (compareTo((TransformationPointPair)o) == 0);
	}
	
	public String toString() {
		return "TPP("+transformation+","+point+","+index+")";
	}
	
	public Transformation getTransformation() { return transformation; }
	public Point getPoint() { return point; }
	public Integer getIndex() {return index; }
	
//	public String getLatexString() {
//		return "\\langle"+getTransformation().getLatexString()+","+getPoint().getLatexString()+"\\rangle";
//	}
}

