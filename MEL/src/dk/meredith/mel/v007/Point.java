package dk.meredith.mel.v007;

import java.util.ArrayList;

public class Point implements Comparable<Point> {
	
	//coords gives the co-ordinates of this Point in the Space, space.
	private ArrayList<Integer> coords = new ArrayList<Integer>();
	private Space space;
	
	public Point(Space space, int... coords) {
		this.space = space;
		for(int i = 0; i < coords.length; i++) {
			this.coords.add(coords[i]);
		}
	}
	
	public Point(int... coords) {
		this.space = new Space(coords.length);
		for(int i = 0; i < coords.length; i++) {
			this.coords.add(coords[i]);
		}
	}
	
	public Point(Space space, ArrayList<Integer> coords) {
		this.space = space;
		this.coords = coords;
	}
	
	public Point(ArrayList<Integer> coords) {
		this.space = new Space(coords.size());
		this.coords = coords;
	}
	
	public int getCoord(int dimension) {
		return coords.get(dimension);
	}
	
	public int getUnmaskedCoord(int dimension) {
		return getSpace().getMaskStack(dimension).getUnmaskedValue(getCoord(dimension));
	}

	public Space getSpace() {
		return space;
	}

	public Point getUnmaskedPoint() {
		ArrayList<Integer> basicCoords = new ArrayList<Integer>();
		for(int i = 0; i < coords.size(); i++) {
			basicCoords.add(getUnmaskedCoord(i));
		}
		return new Point(basicCoords);
	}
	
	public int getDimension() {
		return coords.size();
	}
	
	/**
	 * Returns true iff unmasked points are the same.
	 */
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Point)
			return compareTo(((Point)obj)) == 0;
		return false;
	}

	/**
	 * Compares the unmasked points for this Point and Point p.
	 */
	public int compareTo(Point p) {
		int d = getDimension() - p.getDimension();
		if (d != 0) return d;
		//Same dimension
		Point basicP = p.getUnmaskedPoint();
		Point basicThis = getUnmaskedPoint();
		for(int i = 0; i < getDimension(); i++) {
			d = basicThis.getCoord(i) - basicP.getCoord(i);
			if (d != 0) return d;
		}
		return 0;
	}

	public Point translate(Vector v) {
		Point p = v.getSpace().getMaskedPoint(this);
		
	}
		
	@Override
	public String toString() {
		if (space.isBasicSpace())
			return "["+coords+"]";
		return "["+coords+","+space+"]";
	}

	public ArrayList<Integer> getCoords() {
		return coords;
	}

}
