package com.chromamorph.points022;

import com.chromamorph.maths.Rational;

public class MSP implements Comparable<MSP>{

	PointSet objectPattern, imagePattern;
	Rational timeScaleFactor;
	int pitchScaleFactor;
	Point r1,r2;

	public MSP(Rational timeScaleFactor, int pitchScaleFactor, Point r1, Point r2, Point objectPoint, Point imagePoint) {
		this.timeScaleFactor = timeScaleFactor;
		this.pitchScaleFactor = pitchScaleFactor;
		this.r1 = r1;
		this.r2 = r2;
		objectPattern = new PointSet(objectPoint);
		imagePattern = new PointSet(imagePoint);
	}
	
	public void add(Point objectPoint, Point imagePoint) {
		objectPattern.add(objectPoint);
		imagePattern.add(imagePoint);
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof MSP)) return false;
		return compareTo((MSP)obj)==0;
	}
	
	@Override
	public int compareTo(MSP o) {
		int d;
		if ((d = objectPattern.compareTo(o.objectPattern))!=0) return d;
		if ((d = imagePattern.compareTo(o.imagePattern))!=0) return d;
		if ((d = r1.compareTo(o.r1))!=0) return d;
		if ((d = r2.compareTo(o.r2))!=0) return d;
		if ((d = timeScaleFactor.compareTo(o.timeScaleFactor))!=0) return d;
		if ((d = pitchScaleFactor - o.pitchScaleFactor)!=0) return d;
		return 0;
	}

}
