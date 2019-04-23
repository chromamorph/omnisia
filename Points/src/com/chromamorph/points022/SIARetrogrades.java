package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.TreeSet;

public class SIARetrogrades implements Encoder {

	private final static double MIN_COMPACTNESS = 0.9;
	private final static int MIN_PATTERN_SIZE = 4;

	class VectorPointPair implements Comparable<VectorPointPair> {
		private Vector vector;
		private Point point;

		VectorPointPair(Vector vector, Point point) {
			this.vector = vector;
			this.point = point;
		}

		public Vector getVector() {
			return vector;
		}

		public Point getPoint() {
			return point;
		}

		@Override
		public int compareTo(VectorPointPair vpp) {
			int d = vector.compareTo(vpp.getVector());
			if (d != 0) return d;
			return point.compareTo(vpp.getPoint());
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof VectorPointPair)) return false;
			return compareTo((VectorPointPair)obj) == 0;
		}
	}

	@Override
	public Encoding encode(PointSet points) {

		//Find retrograde of points
		PointSet retrogradePointSet = points.getRetrograde();
		TreeSet<Point> retrograde = retrogradePointSet.getPoints();

		//Find maximal matches from points to retrograde

		TreeSet<VectorPointPair> vectorPointPairs = new TreeSet<VectorPointPair>();

		TreeSet<Point> prime = points.getPoints();
		for(Point primePoint : prime)
			for(Point retrogradePoint : retrograde)
				vectorPointPairs.add(new VectorPointPair(new Vector(primePoint,retrogradePoint),primePoint));

		TreeSet<PatternPair> maxRetPatternPairs = new TreeSet<PatternPair>();
		PointSet pattern = new PointSet();
		PointSet pattern2 = null;
		ArrayList<VectorPointPair> vppArrayList = new ArrayList<VectorPointPair>(vectorPointPairs);
		Vector previousVector = vppArrayList.get(0).getVector();
		for(int i = 0; i < vppArrayList.size(); i++) {
			if (pattern.isEmpty() || vppArrayList.get(i).getVector().equals(previousVector))
				pattern.add(vppArrayList.get(i).getPoint());
			else {
				pattern2 = pattern.translate(previousVector).getRetrograde();
				if (pattern.getCompactness(points) >= MIN_COMPACTNESS && 
						pattern2.getCompactness(points) >= MIN_COMPACTNESS &&
						pattern.getBBArea() != 0 &&
						pattern.size() >= MIN_PATTERN_SIZE) {
					maxRetPatternPairs.add(new PatternPair(pattern,pattern2));
				}
				pattern = new PointSet();
				pattern.add(vppArrayList.get(i).getPoint());
				previousVector = vppArrayList.get(i).getVector();
			}
		}
		pattern2 = pattern.translate(previousVector).getInversion();
		if (pattern.getCompactness(points) >= MIN_COMPACTNESS && 
				pattern2.getCompactness(points) >= MIN_COMPACTNESS &&
				pattern.getBBArea() != 0 &&
				pattern.size() >= MIN_PATTERN_SIZE) {
			maxRetPatternPairs.add(new PatternPair(pattern,pattern2));
		}
		System.out.println(maxRetPatternPairs.size()+"non-trivial, maximal reversible patterns found");
		points.draw(maxRetPatternPairs);
		
		return new SIARetrogradesEncoding();
	}
}