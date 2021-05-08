package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class Point implements Comparable<Point>{
	
	private ArrayList<Double> coords = new ArrayList<Double>();
	
	public Point() {
	}
	
	public Point(String s) {
		String str = s.trim();
		if (str.startsWith("(") && str.endsWith(")"))
			constructPointFromLispFormatString(str);
		else
			constructPointFromPTSFileEntry(str);
	}
	
	private void constructPointFromPTSFileEntry(String str) {
		String[] a = str.split(" ");
		for(String cs : a)
			coords.add(Double.parseDouble(cs));
	}
	
	private void constructPointFromLispFormatString(String str) {
		String[] a = str.substring(1, str.length()-1).split(" ");
		for(String cs : a)
			coords.add(Double.parseDouble(cs));
	}
	
	public Point(Double... coords) {
		for(Double d : coords)
			if (d != null)
				this.coords.add(d);
	}
	
	public int size() {
		return coords.size();
	}
	
	public Double get(int i) {
		return coords.get(i);
	}
	
	public void set(int i, double x) {
		coords.set(i, x);
	}
	
	@Override
	public int compareTo(Point o) {
		if (o == null) return 1;
		return Utility.compareToArrayListOfDoubles(coords, o.coords);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Point)) return false;
		return compareTo((Point)obj) == 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("p(");
		for(int i = 0; i < size(); i++) {
			sb.append(get(i));
			if (i < size()-1)
				sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}
	
}
