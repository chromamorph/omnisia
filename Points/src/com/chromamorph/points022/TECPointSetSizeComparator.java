package com.chromamorph.points022;

import java.util.Comparator;

class TECPointSetSizeComparator extends TECQualityComparator implements Comparator<TEC> {

	TECPointSetSizeComparator() {
		super();
	}
	
	@Override
	public int compare(TEC tec1, TEC tec2) {
		if (tec1 == null && tec2 == null) return 0;
		if (tec1 == null) return 1;
		if (tec2 == null) return -1;
		double d;
		int aInt, bInt;
		d = (aInt = tec2.getPatternSize()) - (bInt = tec1.getPatternSize());
		if (Math.abs(d*1.0)/(Math.max(aInt, bInt) * 1.0) > PATTERN_SIZE_TOLERANCE) {
			PATTERN_SIZE_USED_FREQ++;
			return (int)Math.signum(d);
		}
		return super.compare(tec1, tec2);
	}

}

