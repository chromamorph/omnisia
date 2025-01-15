package com.chromamorph.maxtranpatsjava;

import com.chromamorph.maths.Rational;

public class TabNote implements Comparable<TabNote> {

	private Rational rationalOnset, rationalDuration;
	private Long onset, duration;
	private Integer course, fret;
	private Integer chromaticPitch;

	public Rational getRationalOnset() {
		return rationalOnset;
	}



	public void setRationalOnset(Rational rationalOnset) {
		this.rationalOnset = rationalOnset;
	}



	public Rational getRationalDuration() {
		return rationalDuration;
	}



	public void setRationalDuration(Rational rationalDuration) {
		this.rationalDuration = rationalDuration;
	}



	public Long getOnset() {
		return onset;
	}



	public void setOnset(long onset) {
		this.onset = onset;
	}



	public Long getDuration() {
		return duration;
	}



	public void setDuration(long duration) {
		this.duration = duration;
	}



	public Integer getCourse() {
		return course;
	}



	public void setCourse(int course) {
		this.course = course;
	}



	public Integer getFret() {
		return fret;
	}



	public void setFret(int fret) {
		this.fret = fret;
	}



	public Integer getChromaticPitch() {
		return chromaticPitch;
	}



	public void setChromaticPitch(int chromaticPitch) {
		this.chromaticPitch = chromaticPitch;
	}

	public int getCourseFromNoteElt(String noteElt) {
		int start = noteElt.indexOf("tab.course=")+12;
		int end = noteElt.indexOf('"',start);
		return Integer.parseInt(noteElt.substring(start, end));
	}
	
	public int getFretFromNoteElt(String noteElt) {
		int start = noteElt.indexOf("tab.fret=")+10;
		int end = noteElt.indexOf('"',start);
		return Integer.parseInt(noteElt.substring(start, end));
	}
	
	public void computeChromaticPitch(TabTuning tuning) {
		setChromaticPitch(tuning.getCoursePitch(getCourse()).getChromaticPitch()+getFret());
	}
	
	public TabNote(Rational onset, Rational duration, String noteElt, TabTuning tuning) {
		setRationalOnset(onset);
		setRationalDuration(duration);
		setCourse(getCourseFromNoteElt(noteElt));
		setFret(getFretFromNoteElt(noteElt));
		computeChromaticPitch(tuning);
	}
	
	

	@Override
	public int compareTo(TabNote o) {
		if (o == null) return 1;
		if (getRationalOnset() != null && o.getRationalOnset() != null) {
			int d = getRationalOnset().compareTo(o.getRationalOnset());
			if (d != 0) return d;
		}
		//		Rational onsets are the same
		if (getOnset() != null && o.getOnset() != null) {
			long d = getOnset() - o.getOnset();
			if (d != 0l) return (int)(Math.signum(d));
		}
		//		Onsets are the same
		if (getChromaticPitch() != null && o.getChromaticPitch() != null) {
			int d = getChromaticPitch() - o.getChromaticPitch();
			if (d != 0) return d;
		}
		//		Chromatic pitches are the same
		if (getRationalDuration() != null && o.getRationalDuration() != null) {
			int d = getRationalDuration().compareTo(o.getRationalDuration());
			if (d != 0) return d;
		}
		//		Rational durations are the same
		if (getDuration() != null && o.getDuration() != null) {
			long d = getDuration() - o.getDuration();
			if (d != 0l) return (int)(Math.signum(d));
		}
		//		Durations are the same
		if (getCourse() != null && o.getCourse() != null) {
			int d = getCourse() - o.getCourse();
			if (d != 0) return d;
		}
		//		Courses are the same
		if (getFret() != null && o.getFret() != null) {
			int d = getFret() - o.getFret();
			if (d != 0) return d;
		}
		//		Frets are the same
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TabNote)) return false;
		return compareTo((TabNote)obj) == 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TabNote(");
		sb.append(getOnset()+","+getChromaticPitch()+","+getDuration()+","+getCourse()+","+getFret()+","+getRationalOnset()+","+getRationalDuration());
		sb.append(")");
		return sb.toString();
	}

}
