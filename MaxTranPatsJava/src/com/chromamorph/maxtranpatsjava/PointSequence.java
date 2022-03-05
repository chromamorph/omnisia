package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class PointSequence implements Comparable<PointSequence> {

	private ArrayList<Point> points = new ArrayList<Point>();
	
	public PointSequence() {}
	
	public PointSequence(PointSet pointSet) {
		for(Point p : pointSet.getPointsArray())
			points.add(p);
	}
	
	public PointSequence copy() {
		PointSequence seq = new PointSequence();
		for(Point p : points) seq.add(p);
		return seq;
	}
	
	public int size() {
		return points.size();
	}
	
	public Point get(int i) {return points.get(i);}
	public void set(int i, Point p) {points.set(i, p);}
	
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	public void add(Point p) {points.add(p);}
	
	@Override
	public int compareTo(PointSequence o) {
		if (o == null) return 1;
		int d = size() - o.size();
		if (d != 0) return d;
//		So d == 0 : this point sequence and o are the same size
		for(int i = 0; i < size(); i++) {
			d = get(i).compareTo(o.get(i));
			if (d != 0) return d;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof PointSequence)) return false;
		return compareTo((PointSequence)obj) == 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("S(");
		if (points.size() > 0) sb.append(points.get(0));
		for(int i = 1; i < points.size(); i++)
			sb.append(","+points.get(i));
		sb.append(")");
		return sb.toString();
	}
	
	public PointSet toPointSet() {
		PointSet ps = new PointSet();
		ps.addAll(this);
		return ps;
	}

	
}
