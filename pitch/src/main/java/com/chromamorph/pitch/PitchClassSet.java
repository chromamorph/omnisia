package com.chromamorph.pitch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import com.chromamorph.maths.Maths;

public class PitchClassSet implements Comparable<PitchClassSet> {
	
	private TreeSet<Integer> pitchClasses = new TreeSet<Integer>();
	
	public PitchClassSet() {
	}
	
	public PitchClassSet(Integer... pitchClasses) {
		add(pitchClasses);
	}
	
	public PitchClassSet(Collection<Integer> pitchClasses) {
		setPitchClasses(pitchClasses);
	}
	
	public TreeSet<Integer> getPitchClasses() {
		return pitchClasses;
	}
	
	public Integer size() {
		return getPitchClasses().size();
	}
	
	public void setPitchClasses(Collection<Integer> pitchClasses) {
		this.pitchClasses.addAll(pitchClasses);
	}
	
	public void add(Integer... pitchClasses) throws IllegalArgumentException {
		for(Integer pc : pitchClasses) {
			if (pc < 12 && pc >= 0) 
				this.pitchClasses.add(pc);
			else
				throw new IllegalArgumentException("Pitch class out of range ("+pc+")");
		}
	}
	
	public void add(PitchClassSet pitchClassSet) {
		for(Integer pc : pitchClassSet.getPitchClasses()) add(pc);
	}
	
	public Integer getSetNumber() {
		double setNumber = 0;
		for (Integer pc : getPitchClasses())
			setNumber += Math.pow(2, pc);
		return (int)setNumber;
	}
	
@Override
	public boolean equals(Object obj) {
		if (obj instanceof PitchClassSet) {
			PitchClassSet pcs = (PitchClassSet) obj;
			ArrayList<Integer> a1 = new ArrayList<Integer>(getPitchClasses());
			ArrayList<Integer> a2 = new ArrayList<Integer>(pcs.getPitchClasses());
			for (int i = 0; i < a1.size(); i++)
				if (a1.get(i) != a2.get(i)) return false;
			return true;
		} else
			return false;
	}

	public int compareTo(PitchClassSet pcs) {
		if (equals(pcs)) return 0;
		if (getSetNumber() < pcs.getSetNumber()) return -1;
		return 1;
	}
	
	public String toString() {
		return getPitchClasses().toString();
	}
	
	public PitchClassSet transpose(Integer interval) throws IllegalArgumentException {
		if (interval >= 12 || interval < 0)
			throw new IllegalArgumentException("Interval out of range ("+interval+")");
		PitchClassSet pcs = new PitchClassSet();
		for (Integer pc : getPitchClasses())
			pcs.add(Maths.mod(pc+interval, 12));
		return pcs;
	}
	
	public PitchClassSet getTranEqClass() {
		PitchClassSet tec = this;
		for (int i = 1; i < 12; i++) {
			PitchClassSet pcs = transpose(i);
			if (compareTo(pcs) > 0)
				tec = new PitchClassSet(pcs.getPitchClasses());
		}
		return tec;
	}
	
	public PitchClassSet getIntervalSet(PitchClassSet pitchClassSet) {
		PitchClassSet intervalSet = new PitchClassSet();
		for(int i = 0; i < 12; i++)
			if (transpose(i).equals(pitchClassSet))
				intervalSet.add(i);
		return intervalSet;
	}
	
	public boolean contains(int pc) {
		return getPitchClasses().contains(pc);
	}
	
	public PitchClassSet getIntersection(PitchClassSet otherSet) {
		PitchClassSet outputSet = new PitchClassSet();
		for(int pc : getPitchClasses())
			if (otherSet.contains(pc))
				outputSet.add(pc);
		return outputSet;
	}
	
	/**
	 * Uses F1 score to measure similarity between two pitch class sets.
	 * @param otherSet
	 * @return
	 */
	public double getSimilarity(PitchClassSet otherSet) {
		return (2.0*getIntersection(otherSet).size())/(size()+otherSet.size());
	}
	
	public double getDissimilarity(PitchClassSet otherSet) {
		return 1.0-getSimilarity(otherSet);
	}
}
