package com.chromamorph.maxtranpatsjava;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.chromamorph.pitch.Pitch;

public class Point implements Comparable<Point>{
	
	private ArrayList<Double> coords = new ArrayList<Double>();
	
	private Long onset = null;
	private Pitch pitch = null;
	private Long duration = null;
	private Integer voice = null;
	
	public Long getOnset() {
		return onset;
	}

	public void setOnset(Long onset) {
		this.onset = onset;
	}

	public Pitch getPitch() {
		return pitch;
	}

	public void setPitch(Pitch pitch) {
		this.pitch = pitch;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Integer getVoice() {
		return voice;
	}

	public void setVoice(Integer voice) {
		this.voice = voice;
	}

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
		String[] a = str.split("\\h");
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
	
	public void add(double x) {
		coords.add(x);
	}
	
	public ArrayList<Double> getCoords() {
		return coords;
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
	
	@SuppressWarnings("deprecation")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("p(");
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
	
	public com.chromamorph.points022.Point toOmnisiaPoint() {
		long x = (long)Math.floor( get(0));
		int y = (int)Math.floor(get(1));
		com.chromamorph.points022.Point omnisiaPoint = new com.chromamorph.points022.Point(x,y);
		return omnisiaPoint;
	}
	
	public static void main(String[] args) {
		Point p = new Point(3.0, 2.1, 4.0, 5.0);
		p.setOnset(3l);
		System.out.println(p);
	}
	
}
