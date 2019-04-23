package com.chromamorph.pointsMTECMCM;

/**
 * 
 * @author David Meredith
 * @date 22 August 2012
 *
 * Defines a Point object. This represents a 2d point in an
 * integer lattice where both X and Y are non-negative.
 *
 */
public class Point implements Comparable<Point>{
	int x, y;
	
	public Point(int x, int y) {
		setX(x);
		setY(y);
	}

	public Point(long x, int y) {
		setX((int)x);
		setY(y);
	}
	
	public Point(String l) {
		String[] array = l.split(" ");
		x = Integer.parseInt(array[0]);
		y = Integer.parseInt(array[1]);
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
		return "p("+x+","+y+")";
	}
	
	@Override
	public int compareTo(Point p) {
		if (p == null) return 1;
		int d = getX() - p.getX();
		if (d != 0) return d;
		d = getY() - p.getY();
		if (d != 0) return d;
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Point)) return false;
		return compareTo((Point)obj) == 0;
	}
	
	public Point translate(Vector vector) {
		return new Point(getX()+vector.getX(),getY()+vector.getY());
	}
	
	public Point copy() {
		return new Point(getX(),getY());
	}
}
