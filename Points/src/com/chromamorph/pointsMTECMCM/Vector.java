package com.chromamorph.pointsMTECMCM;

public class Vector implements Comparable<Vector>{
	int x, y;
	
	public Vector(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public Vector(Point p, Point q) {
		setX(q.getX()-p.getX());
		setY(q.getY()-p.getY());
	}
	
	private void setX(int x) {
		this.x = x;
	}
	
	private void setY(int y) {
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String toString() {
		return "v("+x+","+y+")";
	}
	
	@Override
	public int compareTo(Vector v) {
		if (v == null) return 1;
		int d = getX() - v.getX();
		if (d != 0) return d;
		d = getY() - v.getY();
		if (d != 0) return d;
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Vector)) return false;
		return compareTo((Vector)obj) == 0;
	}
	
	public Vector copy() {
		return new Vector(x,y);
	}
	
}
