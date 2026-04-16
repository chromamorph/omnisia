package dk.meredith.mel.v007;

import java.util.ArrayList;

public class Vector implements Comparable<Vector> {
	
	//coords gives the co-ordinates of this Vector in the Space, space.
	private ArrayList<Integer> coords = new ArrayList<Integer>();
	private Space space;
	
	public Vector(Space space, int... coords) {
		this.space = space;
		for(int i = 0; i < coords.length; i++) {
			this.coords.add(coords[i]);
		}
	}
	
	public Vector(int... coords) {
		this.space = new Space(coords.length);
		for(int i = 0; i < coords.length; i++) {
			this.coords.add(coords[i]);
		}
	}
	
	public Vector(Space space, ArrayList<Integer> coords) {
		this.space = space;
		this.coords = coords;
	}
	
	public Vector(ArrayList<Integer> coords) {
		this.space = new Space(coords.size());
		this.coords = coords;
	}
	
	public int getCoord(int dimension) {
		//System.out.println("Vector.getCoord: coords is " + coords);
		return coords.get(dimension);
	}
	
	public ArrayList<Integer> getCoords() {
		return coords;
	}
	
	public Space getSpace() {
		return space;
	}

	public int getDimension() {
		return coords.size();
	}
	
	/**
	 * Returns true iff unmasked vectors are the same.
	 */
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Vector)
			return compareTo(((Vector)obj)) == 0;
		return false;
	}

	/**
	 * Compares the unmasked vectors for this Vector and Vector p.
	 */
	public int compareTo(Vector v) {
		//System.out.println("Vector passed to Vector.compareTo is "+ v);
		int d = getDimension() - v.getDimension();
		if (d != 0) return d;
		//Same dimension
		d = getSpace().compareTo(v.getSpace());
		if (d != 0) return d;
		//Same spaces
		for(int i = 0; i < v.getDimension(); i++)
			if (getCoord(i) != v.getCoord(i)) return getCoord(i) - v.getCoord(i);
		//Same coords
		return 0;
	}

	public Vector add(Vector v) {
		if (v.getDimension() != getDimension()) return null;
		Vector basicV = v.getUnmaskedVector();
		Vector basicThis = getUnmaskedVector();
		ArrayList<Integer> newCoords = new ArrayList<Integer>();
		for(int i = 0; i < getDimension(); i++)
			newCoords.add(basicV.getCoord(i)+basicThis.getCoord(i));
		if (getSpace().equals(v.getSpace())) {
			ArrayList<Integer> maskedCoords = getSpace().getMaskedCoords(newCoords);
			return new Vector(getSpace(),maskedCoords);
		}
		return new Vector(newCoords);
	}
	
	@Override
	public String toString() {
		return "["+coords+","+space+"]";
	}

}
