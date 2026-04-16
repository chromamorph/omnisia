package dk.meredith.mel.v007;

import java.util.ArrayList;

public class MaskStack implements Comparable<MaskStack> {
	
	private ArrayList<Mask> masks = new ArrayList<Mask>();
	
	public MaskStack(Mask...masks) {
		for(Mask mask : masks) {
			this.masks.add(mask);
		}
	}
	
	public static MaskStack basicMaskStack() {
		return new MaskStack(Mask.basicMask());
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
	
	public boolean equals(Object o) {
		if (o instanceof MaskStack)
			return  compareTo((MaskStack)o) == 0;
		return false;
	}
	
	public int getSize() {
		return masks.size();
	}
	
	public Mask getMask(int i) {
		if (i < 0 || i > getSize()) return null;
		return masks.get(i);
	}
	
	@Override
	public int compareTo(MaskStack ms) {
		int d = getSize() - ms.getSize();
		if (d != 0) return d;
		for(int i = 0; i < getSize(); i++) {
			d = getMask(i).compareTo(ms.getMask(i));
			if (d != 0) return d;
		}
		return 0;
	}
	
	public String toString() {
		return masks.toString();
	}
	
	public static void main(String[] args) {
		MaskStack ms = new MaskStack(new Mask(3, 1,2,3), 
									 new Mask(-4, 3,2,1),
									 new Mask(-1, 2));
		for(int i = -12; i < 11; i++)
			System.out.print(ms.getMaskedValue(i)+"  ");
		System.out.println();
		for(int i = 0; i < 3; i++)
			System.out.print(ms.getUnmaskedValue(i)+" ");
	}
	
}
