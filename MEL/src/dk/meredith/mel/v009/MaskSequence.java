package dk.meredith.mel.v009;

import java.util.ArrayList;

public class MaskSequence {
	
	public static final MaskSequence BASIC = new MaskSequence(Mask.BASIC);
	private ArrayList<Mask> masks = new ArrayList<Mask>();

	public MaskSequence(Mask... masks) {
		for(Mask mask : masks) {
			this.masks.add(mask);
		}
	}

	@SuppressWarnings("unchecked")
	public MaskSequence(MaskSequence maskSequence, Mask... masks) {
		this.masks = (ArrayList<Mask>)maskSequence.getMasks().clone();
		for(Mask mask : masks)
			this.masks.add(mask);
	}

	private ArrayList<Mask> getMasks() {
		return masks;
	}
	
	public int unmask(int i) {
		int k = i;
		for(int j = masks.size()-1; j >= 0; j--) {
			k = masks.get(j).unmask(k);
		}
		return k;
	}

	public Integer mask(int i) {
		Integer k = i;
		for (int j = 0; j < masks.size(); j++) {
			k = masks.get(j).unmask(k);
		}
		return k;
	}

	public String toString() {
		return masks.toString();
	}
}
