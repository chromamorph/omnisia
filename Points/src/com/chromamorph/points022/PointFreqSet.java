package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import com.chromamorph.maths.Maths;

public class PointFreqSet {

	private TreeSet<PointFreq> pointFreqs = new TreeSet<PointFreq>();
	private TreeSet<PointFreq> multiPoints = new TreeSet<PointFreq>();
	
	public TreeSet<PointFreq> getMultiPoints() {
		return multiPoints;
	}

	public PointFreq addPoint(Point point) {
		PointFreq pf = new PointFreq(point,1);
		PointFreq floorPf = pointFreqs.floor(pf);
		PointFreq ceilPf = pointFreqs.ceiling(pf);
		if (ceilPf != null && ceilPf.equals(floorPf)) {
			ceilPf.incFreq();
			multiPoints.add(ceilPf);
			return ceilPf;
		} else {
			pointFreqs.add(pf);
			return pf; 
		}
	}

	public PointFreq addPoint(Point point, Vector vector) {
		PointFreq pf = new PointFreq(point,1,vector);
		PointFreq floorPf = pointFreqs.floor(pf);
		PointFreq ceilPf = pointFreqs.ceiling(pf);
		if (ceilPf != null && ceilPf.equals(floorPf)) {
			ceilPf.incFreq();
			multiPoints.add(ceilPf);
			ceilPf.addTranslator(vector);
			return ceilPf;
		} else {
			pointFreqs.add(pf);
			return pf;
		}
	}

	public PointFreq get(Point point) {
		PointFreq pointFreq = new PointFreq(point, 1);
		if (pointFreqs.contains(pointFreq))
			return pointFreqs.floor(pointFreq);
		return null;
	}
	
	@Override
	public String toString() {
		return pointFreqs.toString();
	}
	
	public int getFreq(Point p) {
		if (multiPoints.contains(new PointFreq(p,1)))
			return multiPoints.floor(new PointFreq(p,1)).getFreq();
		else if (pointFreqs.contains(new PointFreq(p,1)))
			return 1;
		else
			return 0;
	}
	
	public String getLatexString() {
		ArrayList<PointFreq> pfa = new ArrayList<PointFreq>();
		pfa.addAll(pointFreqs);
		Comparator<PointFreq> comparator = new Comparator<PointFreq>() {

			@Override
			public int compare(PointFreq pf1, PointFreq pf2) {
				if (pf1 == null && pf2 == null) return 0;
				if (pf1 == null) return -1;
				if (pf2 == null) return 1;
				int d = pf1.getFreq() - pf2.getFreq();
				if (d != 0) return d;
				d = pf1.getPoint().compareTo(pf2.getPoint());
				if (d != 0) return d;
				VectorSet pf1Translators = new VectorSet(pf1.getTranslators());
				VectorSet pf2Translators = new VectorSet(pf2.getTranslators());
				return pf1Translators.compareTo(pf2Translators);
			}
			
		};
		Collections.sort(pfa, comparator);
		StringBuilder sb = new StringBuilder();
		sb.append("\\langle&"); // Open list
		//Add first element
		sb.append(pfa.get(0).getLatexString());
		for(int i = 1; i < pfa.size(); i++) {
			sb.append(",");
			if (Maths.mod(i, 4)==0)
				sb.append("\\\\\n&");
			sb.append(pfa.get(i).getLatexString());
		}
		sb.append("\\rangle"); // Close list
		return sb.toString();
	}
}


