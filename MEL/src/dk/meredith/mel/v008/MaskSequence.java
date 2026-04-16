package dk.meredith.mel.v008;

import java.util.ArrayList;

public class MaskSequence implements Comparable<MaskSequence>{
	private ArrayList<Mask> masks = new ArrayList<Mask>();
	
	public MaskSequence(Mask... masks) {
		for(Mask mask : masks) {
			this.masks.add(mask);
		}
	}
	
	public ArrayList<Mask> getMasks() {
		return masks;
	}
	
	public Mask get(int i) {
		return masks.get(i);
	}
	
	public int size() {
		return masks.size();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof MaskSequence)) return false;
		return compareTo((MaskSequence)obj) == 0;
	}
	
	public int compareTo(MaskSequence m) {
		int d = size() - m.size();
		if (d != 0) return d;
		for (int i = 0; i < size(); i++) {
			d = get(i).compareTo(m.get(i));
			if (d != 0) return d;
		}
		return 0;
	}
	
	public String toString() {
		return getMasks().toString();
	}
	
	/**
	 * Masks are applied in reverse order of addition to the masks ArrayList.
	 * @param maskedValue
	 * @return
	 */
	public int getUnmaskedValue(int maskedValue) {
		int mv = maskedValue;
		for(int i = masks.size()-1; i >= 0; i--) {
			mv = masks.get(i).getUnmaskedValue(mv);
		}
		return mv;
	}

	public Integer getMaskedValue(int unmaskedValue) {
		Integer u = unmaskedValue;
		for (int i = 0; i < masks.size(); i++) {
			u = masks.get(i).getMaskedValue(u);
		}
		return u;
	}
}
