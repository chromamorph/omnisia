package com.chromamorph.points022;

import com.chromamorph.maths.Rational;

public class SFListItem implements Comparable<SFListItem> {
	Rational timeScaleFactor;
	int pitchScaleFactor;
	Point r1,r2,p1,p2;
	
	@Override
	public int compareTo(SFListItem o) {
		if (timeScaleFactor.greaterThan(o.timeScaleFactor)) return 1;
		if (timeScaleFactor.lessThan(o.timeScaleFactor)) return -1;
		int d = pitchScaleFactor - o.pitchScaleFactor;
		if (d != 0) return d;
		if ((d = r1.compareTo(o.r1)) != 0) return d;
		if ((d = r2.compareTo(o.r2)) != 0) return d;
		if ((d = p1.compareTo(o.p1)) != 0) return d;
		if ((d = p2.compareTo(o.p2)) != 0) return d;
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof SFListItem)) return false;
		return compareTo((SFListItem)obj) == 0;
	}
	
	public SFListItem(Rational st, int sp, Point r1, Point r2, Point p1, Point p2) {
		this.timeScaleFactor = st;
		this.pitchScaleFactor = sp;
		this.r1 = r1;
		this.r2 = r2;
		this.p1 = p1;
		this.p2 = p2;
	}
}
