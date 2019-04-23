package com.chromamorph.points022;

import java.util.Comparator;

class RecurSIAMtpComparator implements Comparator<MtpCisPair> {

	@Override
	public int compare(MtpCisPair o1, MtpCisPair o2) {
		//				Prefer pair where union of MTP and MTP+v is larger
		PointSet ps1 = new PointSet();
		PointSet ps2 = new PointSet();
		ps1.addAll(o1.getMtp());
		ps1.addAll(o1.getMtp().translate(o1.getVectorSet().get(0)));
		ps2.addAll(o2.getMtp());
		ps2.addAll(o2.getMtp().translate(o2.getVectorSet().get(0)));
		int d = ps2.size() - ps1.size();
		if (d != 0) return d;
		//				Prefer larger MTP
		d = o2.getMtp().size() - o1.getMtp().size();
		if (d != 0) return d;
		//				Prefer lexicographically earlier pattern
		return o1.getMtp().compareTo(o2.getMtp());
	}

};

