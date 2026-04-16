package dk.meredith.mel.v007;

import java.util.ArrayList;

public class Space implements Comparable<Space> {
	/**
	 * The ith MaskStack is for the ith dimension.
	 */
	private ArrayList<MaskStack> maskStacks = new ArrayList<MaskStack>();
	
	public Space(int dimension) {
		for(int i = 0; i < dimension; i++)
			maskStacks.add(new MaskStack(new Mask(0,1)));
	}
	
	/**
	 * MaskStacks must be in order of dimensions.
	 * @param maskStacks
	 */
	public Space(MaskStack... maskStacks) {
		for(MaskStack maskStack : maskStacks)
			this.maskStacks.add(maskStack);
	}
	
	public MaskStack getMaskStack(int dimension) {
		if (dimension < 0 || dimension >= maskStacks.size()) return null;
		return maskStacks.get(dimension);
	}

	public int getDimension() {
		return maskStacks.size();
	}
	
	public ArrayList<Integer> getMaskedCoords(ArrayList<Integer> unmaskedCoords) {
		ArrayList<Integer> maskedCoords = new ArrayList<Integer>();
		for(int i = 0; i < unmaskedCoords.size(); i++) {
			MaskStack ms = getMaskStack(i);
			if (ms != null)
				maskedCoords.add(ms.getMaskedValue(unmaskedCoords.get(i)));
			else
				maskedCoords.add(null);
		}
		return maskedCoords;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Space)) return false;
		return compareTo(((Space)obj)) == 0;
	}
	
	@Override
	public int compareTo(Space s) {
		int maxDimension = getDimension();
		if (s.getDimension() > maxDimension) maxDimension = s.getDimension();
		for(int i = 0; i < maxDimension; i++) {
			int d = getMaskStack(i).compareTo(s.getMaskStack(i));
			if (d != 0) return d;
		}
		return 0;
	}
		
	public boolean isBasicSpace() {
		return equals(new Space(getDimension()));
	}
	
	public String toString() {
		return maskStacks.toString();
	}

	public Point getMaskedPoint(Point point) {
		Point unmaskedPoint = point.getUnmaskedPoint();
		ArrayList<Integer> coords = getMaskedCoords(unmaskedPoint.getCoords());
		return new Point(this,coords);
	}
}
