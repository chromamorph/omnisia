package dk.meredith.mel.v007;

import java.util.TreeSet;

public class PointSet {
	private TreeSet<Point> points = new TreeSet<Point>();

	public PointSet(Point refPoint, VectorSet vectorSet) {
		points.add(refPoint);
		for(Vector vector : vectorSet.getVectors())
			points.add(refPoint.translate(vector));
	}
	
	public PointSet(PointSet...pointSets) {
		for(PointSet pointSet : pointSets) 
			points.addAll(pointSet.points);
	}
	
	public void add(Point point) {
		points.add(point);
	}
	
	public void add(PointSet... pointSets) {
		for(PointSet ps : pointSets)
			points.addAll(ps.points);
	}
	
	public int getMax(int dimension) {
		int max = points.first().getCoord(dimension);
		for(Point point : points)
			if (point.getCoord(dimension) > max)
				max = point.getCoord(dimension);
		return max;
	}
	
	public int getMin(int dimension) {
		int min = points.first().getCoord(dimension);
		for(Point point : points)
			if (point.getCoord(dimension) > min)
				min = point.getCoord(dimension);
		return min;
	}
	
	public TreeSet<Point> getPoints() {
		return points;
	}
	
	public String toString() {
		return points.toString();
	}
}
