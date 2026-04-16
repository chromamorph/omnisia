package dk.meredith.mel.v007;

public class Mask implements Comparable<Mask> {

	private int offset;
	private MaskStructure maskStructure;
	
	public Mask(int offset, int... maskIntervals) {
		this.offset = offset;
		maskStructure = new MaskStructure(maskIntervals);
	}
	
	public Mask(int offset, MaskStructure maskStructure) {
		this.offset = offset;
		this.maskStructure = maskStructure;
	}
	
	public static Mask basicMask() {
		return new Mask(0,1);
	}
	
	public int getOffset() {
		return offset;
	}
	public MaskStructure getMaskStructure() {
		return maskStructure;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Mask) {
			return compareTo((Mask)obj) == 0;
		}
		return false;
	}
	
	public int getMaskStructureSize() {
		return maskStructure.size();
	}
	
	public int getMaskValue(int i) {
		return maskStructure.get(i);
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
	
	public int compareTo(Mask m) {
		int d = getOffset() - m.getOffset();
		if (d != 0) return d;
		d = getMaskStructureSize() - m.getMaskStructureSize();
		if (d != 0) return d;
		for(int i = 0; i < getMaskStructureSize(); i++) {
			d = getMaskValue(i) - m.getMaskValue(i);
			if (d != 0) return d;
		}
		return 0;
	}
	
	public String toString() {
		return "["+offset+","+maskStructure+"]";
	}
	
	public static void main(String[] args) {
		Mask mask = new Mask(3,1,2,3);
		for (int i = -12; i < 11; i++) {
			System.out.print(mask.getMaskedValue(i) + " ");
		}
		System.out.println();
		for(int i = -7; i < 4; i++)
			System.out.print(mask.getUnmaskedValue(i)+" ");
		System.out.println();
		System.out.println();
		mask = new Mask(-4,3,2,1);
		for(int i = -7; i < 4; i++)
			System.out.print("("+i+","+mask.getMaskedValue(i)+") ");
		System.out.println();		
		for(int i = -2; i < 4; i++)
			System.out.print("("+i+","+mask.getUnmaskedValue(i)+") ");
		System.out.println();
		System.out.println();
		mask = new Mask(-1,2);
		for(int i = -2; i < 4; i++)
			System.out.print("("+i+","+mask.getMaskedValue(i)+") ");
		System.out.println();		
		for(int i = 0; i < 3; i++)
			System.out.print("("+i+","+mask.getUnmaskedValue(i)+") ");
		
	}
	
}
