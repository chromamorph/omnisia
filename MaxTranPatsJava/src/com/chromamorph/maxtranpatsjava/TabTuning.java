package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

import com.chromamorph.pitch.Pitch;

public class TabTuning {

	ArrayList<Pitch> coursePitches = null;
	
	public TabTuning() {
		coursePitches = new ArrayList<Pitch>();
	}
	
	public void addCoursePitch(String courseElement) {
		int start = courseElement.indexOf("pname=")+7;
		int end = start + 1;
		String letterName = courseElement.substring(start, end);
		start = courseElement.indexOf("oct=")+5;
		end = courseElement.indexOf('"', start);
		String oct = courseElement.substring(start, end);
		String pitchName = letterName + oct;
		Pitch p = new Pitch();
		p.setPitchName(pitchName);
		addCoursePitch(p);
	}
	
	public void addCoursePitch(Pitch pitch) {
		coursePitches.add(pitch);
	}
	
	public Pitch getCoursePitch(int courseNumber) {
		return coursePitches.get(courseNumber-1);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Tuning("+coursePitches.get(0));
		for(Pitch p : coursePitches.subList(1, coursePitches.size()))
			sb.append(","+p);
		return sb.toString();
	}
	
}
