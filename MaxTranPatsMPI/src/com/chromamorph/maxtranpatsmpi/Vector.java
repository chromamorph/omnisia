package com.chromamorph.maxtranpatsmpi;

import com.chromamorph.maxtranpatsjava.Point;
import com.chromamorph.maxtranpatsjava.Utility;

public class Vector extends Point {
	
	public Vector(String s) {
		super(s);
	}
	
	public Vector(Double... coords) {
		super(coords);
	}
	
	public Vector(Point objectPoint, Point imagePoint) {
		for()
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("v(");
		boolean onsetPresent = false;
		if (getOnset() != null && (double)getOnset() != get(0)) {
			sb.append(getOnset());
			onsetPresent = true;
			if (size() > 1) sb.append(",");
		}
		for(int i = onsetPresent?1:0; i < size(); i++) {
			double l = Math.floor(get(i));
			String numString = "";
			if (Utility.equalWithTolerance(l, get(i)))
				numString = String.format("%d", (long)l);
			else
				numString = new Double(get(i)).toString();
			sb.append(numString);
			if (i < size()-1)
				sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}

	
}
