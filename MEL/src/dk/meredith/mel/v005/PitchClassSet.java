package dk.meredith.mel.v005;

import java.util.ArrayList;
import java.util.TreeSet;

public class PitchClassSet {
	private ArrayList<Integer> pcs = new ArrayList<Integer>();
	
	public PitchClassSet(int...pitchClasses) {
		TreeSet<Integer> pcts = new TreeSet<Integer>();
		for(int pc : pitchClasses) pcts.add(pc);
		pcs = new ArrayList<Integer>(pcts);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for(int i = 0; i < 12; i++) {
			if (pcs.get(i) == 1)
				sb.append(i);
			if (i == 11)
				sb.append("}");
			else
				sb.append(",");
		}
		return sb.toString();
	}
	
	public int getPitch(int pitch, int interval) {
		int pc = Maths.mod(pitch,12);
		int indexOfOldPC = pcs.indexOf(pc);
		int mum = pcs.size();
		int oldMP = mum*(pitch/12) + indexOfOldPC;
		int newMP = oldMP + interval;
		int newCP = 12 * (newMP/mum) + pcs.get(Maths.mod(newMP,mum));
		return newCP;
	}
}
