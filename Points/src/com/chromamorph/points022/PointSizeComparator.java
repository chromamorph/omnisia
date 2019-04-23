package com.chromamorph.points022;

import java.util.Comparator;

class PointSetSizeComparator implements Comparator<MtpCisPair> {

	@Override
	public int compare(MtpCisPair pc1, MtpCisPair pc2) {
		PointSet s1 = pc1.getMtp();
		PointSet s2 = pc2.getMtp();
		if (s1 == null && s2 == null) return 0;
		if (s2 == null) return -1;
		if (s1 == null) return 1;
		int d = s2.size() - s1.size();
		return d;
	}

}

