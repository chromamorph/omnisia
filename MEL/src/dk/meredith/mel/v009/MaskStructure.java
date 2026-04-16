package dk.meredith.mel.v009;

import java.util.ArrayList;

public class MaskStructure {

	private ArrayList<Integer> intervals = new ArrayList<Integer>();
	
	public MaskStructure(int... intervals) {
		for(int i = 0; i < intervals.length; i++)
			this.intervals.add(intervals[i]);
	}
	
	public int size() {
		return intervals.size();
	}
	
	public Integer get(int i) {
		return intervals.get(i);
	}

	public String toString() {
		return intervals.toString();
	}
}
