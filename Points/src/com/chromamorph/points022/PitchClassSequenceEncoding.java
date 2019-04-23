package com.chromamorph.points022;

public class PitchClassSequenceEncoding extends Encoding {

	private PointSet points;
	
	public PitchClassSequenceEncoding(PointSet points) {
		PointSet newPoints = new PointSet();
		int i = 0;
		for (Point point : points.getPoints()) {
			newPoints.add(new Point(i,point.getY() % 12));
			i++;
		}
		this.points = newPoints;
		newPoints = null;
	}
	
	public PointSet getPoints() {
		return points;
	}
	
	public void draw() {}

}
