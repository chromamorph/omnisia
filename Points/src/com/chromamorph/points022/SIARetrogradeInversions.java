package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.TreeSet;

public class SIARetrogradeInversions implements Encoder {

	private final static double MIN_COMPACTNESS = 1.0;
	private final static int MIN_PATTERN_SIZE = 6;

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

		//Find retrograde inversion of points
		PointSet retrogradeInversionPointSet = points.getRetrograde().getInversion();
		TreeSet<Point> retrograde = retrogradeInversionPointSet.getPoints();

		//Find maximal matches from points to retrograde inversion

		TreeSet<VectorPointPair> vectorPointPairs = new TreeSet<VectorPointPair>();

		TreeSet<Point> prime = points.getPoints();
		for(Point primePoint : prime)
			for(Point retrogradeInversionPoint : retrograde)
				vectorPointPairs.add(new VectorPointPair(new Vector(primePoint,retrogradeInversionPoint),primePoint));

		TreeSet<PatternPair> maxRetInvPatternPairs = new TreeSet<PatternPair>();
		PointSet pattern = new PointSet();
		PointSet pattern2 = null;
		ArrayList<VectorPointPair> vppArrayList = new ArrayList<VectorPointPair>(vectorPointPairs);
		Vector previousVector = vppArrayList.get(0).getVector();
		for(int i = 0; i < vppArrayList.size(); i++) {
			if (pattern.isEmpty() || vppArrayList.get(i).getVector().equals(previousVector))
				pattern.add(vppArrayList.get(i).getPoint());
			else {
				pattern2 = pattern.translate(previousVector).getRetrograde().getInversion();
				if (pattern.getCompactness(points) >= MIN_COMPACTNESS && 
						pattern2.getCompactness(points) >= MIN_COMPACTNESS &&
						pattern.getBBArea() != 0 &&
						pattern.size() >= MIN_PATTERN_SIZE) {
					maxRetInvPatternPairs.add(new PatternPair(pattern,pattern2));
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
			maxRetInvPatternPairs.add(new PatternPair(pattern,pattern2));
		}
		System.out.println(maxRetInvPatternPairs.size()+"non-trivial, maximal reversible patterns found");
		points.draw(maxRetInvPatternPairs);
		
		return new SIARetrogradeInversionsEncoding();
	}
}