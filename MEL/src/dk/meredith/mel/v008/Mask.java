package dk.meredith.mel.v008;

import java.util.ArrayList;

public class Mask implements Comparable<Mask> {
	private int offset;
	private ArrayList<Integer> maskStructure = new ArrayList<Integer>();
	
	public Mask(int offset, int... intervals) {
		setOffset(offset);
		setMaskStructure(intervals);
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public ArrayList<Integer> getMaskStructure() {
		return maskStructure;
	}

	public void setMaskStructure(ArrayList<Integer> intervals) {
		this.maskStructure = intervals;
	}
	
	public void setMaskStructure(int... intervals) {
		for(int i = 0; i < intervals.length; i++)
			this.maskStructure.add(intervals[i]);
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Mask)) return false;
		return compareTo((Mask)obj) == 0;
	}
	
	public int compareTo(Mask mask) {
		int d = getOffset() - mask.getOffset();
		if (d != 0) return d;
		d = getMaskStructure().size() - mask.getMaskStructure().size();
		if (d != 0) return d;
		for (int i = 0; i < getMaskStructure().size(); i++) {
			d = getMaskStructure().get(i) - mask.getMaskStructure().get(i);
			if (d != 0) return d;
		}
		return 0;
	}
	
	public String toString() {
		return "[" + getOffset() + "," + getMaskStructure() + "]";
	}
	
	public Integer getMaskedValue(Integer i) {
		if (i == null) return null;
		int u = offset, m = 0;
		if (i >= offset) {
			while (u < i) {
				m++;
				u += maskStructure.get(Maths.mod(m-1,maskStructure.size()));
			}
			if (u == i) return m;
			return null;
		} else {
			while (u > i) {
				m--;
				u -= maskStructure.get(Maths.mod(m,maskStructure.size()));
			}
			if (u == i) return m;
			return null;
		}
	}
	
	public Integer getUnmaskedValue(Integer i) {
		if (i == null) return null;
		int u = offset, m = 0;
		if (i >= 0) {
			while (m < i) {
				m++;
				u += maskStructure.get(Maths.mod(m-1,maskStructure.size()));
			}
		} else {
			while (m > i) {
				m--;
				u -= maskStructure.get(Maths.mod(m,maskStructure.size()));
			}
		}
		return u;
	}

}
