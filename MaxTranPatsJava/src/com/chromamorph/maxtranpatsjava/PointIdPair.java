package com.chromamorph.maxtranpatsjava;

public class PointIdPair implements Comparable<PointIdPair> {
	private Point point;
	private static int numPointIdPairs = 0;
	private int id;
	
	public PointIdPair(Point point) {
		setPoint(point);
		setId(numPointIdPairs++);
	}
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public int getId() {
		return id;
	}
	
	private void setId(int id) {
		this.id = id;
	}
	@Override
	public int compareTo(PointIdPair o) {
		if (o == null) return 1;
		int d = getPoint().compareTo(o.getPoint());
		if (d != 0) return d;
		return d = getId() - o.getId();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof PointIdPair)) return false;
		return compareTo((PointIdPair)obj)==0;
	}
	
	@Override
	public String toString() {
		return "<"+getPoint()+", "+getId()+">";
	}
}
