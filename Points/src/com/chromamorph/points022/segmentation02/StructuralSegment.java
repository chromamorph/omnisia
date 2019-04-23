package com.chromamorph.points022.segmentation02;

public class StructuralSegment implements Comparable<StructuralSegment>{
	private int label;
	private long start, end;
	private char letterLabel = 0;
	private double startInSeconds, endInSeconds;

	public StructuralSegment(int label, long start, long end) {
		setLabel(label);
		setStart(start);
		setEnd(end);
	}

	public StructuralSegment(
			int label, 
			long start, 
			long end, 
			double startInSeconds,
			double endInSeconds) {
		setLabel(label);
		setStart(start);
		setEnd(end);
		setStartInSeconds(startInSeconds);
		setEndInSeconds(endInSeconds);
	}
	public void setLetterLabel(char letterLabel) {
		this.letterLabel = letterLabel;
	}
	
	public char getLetterLabel() {
		return letterLabel;
	}
	
	public double getStartInSeconds() {
		return startInSeconds;
	}

	public void setStartInSeconds(double startInSeconds) {
		this.startInSeconds = startInSeconds;
	}

	public double getEndInSeconds() {
		return endInSeconds;
	}

	public void setEndInSeconds(double endInSeconds) {
		this.endInSeconds = endInSeconds;
	}

	@Override
	public String toString() {
		return "Segment("+getLabel()+","+getStart()+","+getEnd()+","+getStartInSeconds()+","+getEndInSeconds()+")";
	}

	@Override
	public int compareTo(StructuralSegment seg) {
		if (seg == null) return 1;
		if (getStart() < seg.getStart()) return -1;
		if (getStart() > seg.getStart()) return 1;
		//		Segments start at the same time
		if (getLabel() < seg.getLabel()) return -1;
		if (getLabel() > seg.getLabel()) return 1;
		//		labels are the same
		if (getEnd() < seg.getEnd()) return 1;
		if (getEnd() > seg.getEnd()) return -1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof StructuralSegment)) return false;
		return compareTo((StructuralSegment)obj) == 0;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}
}
