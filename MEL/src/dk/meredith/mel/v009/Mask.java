package dk.meredith.mel.v009;

public class Mask {

	public static final Mask BASIC = new Mask(0,1);
	private int offset;
	private MaskStructure maskStructure = new MaskStructure();
	
	public Mask(int offset, int... maskStructure) {
		this.offset = offset;
		this.maskStructure = new MaskStructure(maskStructure);
	}

	public Mask(int offset, MaskStructure maskStructure) {
		this.offset = offset;
		this.maskStructure = maskStructure;
	}

	public Integer mask(Integer i) {
		if (i == null) return null;
		int j = offset, k = 0;
		if (i >= offset) {
			while (j < i) {
				k++;
				j += maskStructure.get(Maths.mod(k-1,maskStructure.size()));
			}
			if (j == i) return k;
			return null;
		} else {
			while (j > i) {
				k--;
				j -= maskStructure.get(Maths.mod(k,maskStructure.size()));
			}
			if (j == i) return k;
			return null;
		}
	}
	
	public Integer unmask(Integer i) {
		if (i == null) return null;
		int j = offset, k = 0;
		if (i >= 0) {
			while (k < i) {
				k++;
				j += maskStructure.get(Maths.mod(k-1,maskStructure.size()));
			}
		} else {
			while (k > i) {
				k--;
				j -= maskStructure.get(Maths.mod(k,maskStructure.size()));
			}
		}
		return j;
	}

	public String toString() {
		return "["+offset+","+maskStructure+"]";
	}
}
