package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.TreeSet;

public class SIAInversions implements Encoder {

	private final static double MIN_COMPACTNESS = 0.7;
	private final static int MIN_PATTERN_SIZE = 4;

	@Override
	public Encoding encode(PointSet points) {

		//Find inversion of points
		PointSet inversionPointSet = points.getInversion();
		TreeSet<Point> inversion = inversionPointSet.getPoints();

		//Find maximal matches from points to inversion

		TreeSet<VectorPointPair> vectorPointPairs = new TreeSet<VectorPointPair>();

		TreeSet<Point> prime = points.getPoints();
		for(Point primePoint : prime)
			for(Point inversionPoint : inversion)
				vectorPointPairs.add(new VectorPointPair(new Vector(primePoint,inversionPoint),primePoint));

		TreeSet<PatternPair> maxInvPatternPairs = new TreeSet<PatternPair>();
		PointSet pattern = new PointSet();
		PointSet pattern2 = null;
		ArrayList<VectorPointPair> vppArrayList = new ArrayList<VectorPointPair>(vectorPointPairs);
		Vector previousVector = vppArrayList.get(0).getVector();
		for(int i = 0; i < vppArrayList.size(); i++) {
			if (pattern.isEmpty() || vppArrayList.get(i).getVector().equals(previousVector))
				pattern.add(vppArrayList.get(i).getPoint());
			else {
				pattern2 = pattern.translate(previousVector).getInversion();
				if (pattern.getCompactness(points) >= MIN_COMPACTNESS && 
						pattern2.getCompactness(points) >= MIN_COMPACTNESS &&
						pattern.getBBArea() != 0 &&
						pattern.size() >= MIN_PATTERN_SIZE) {
					maxInvPatternPairs.add(new PatternPair(pattern,pattern2));
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
			maxInvPatternPairs.add(new PatternPair(pattern,pattern2));
		}
		System.out.println(maxInvPatternPairs.size()+"non-trivial, maximal invertible patterns found");
		points.draw(maxInvPatternPairs);
		
		return new SIAInversionsEncoding();
	}
}