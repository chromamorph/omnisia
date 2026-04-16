package dk.meredith.mel.v007;

import java.util.TreeSet;

public class MEL {
	private PointSet pointSet = new PointSet();
	
	/////MaskStructure
	
	public MaskStructure maskStructure(int... intervals) {
		return new MaskStructure(intervals);
	}
	
	/////Mask
	
	public Mask mask(int offset, int... intervals) {
		return new Mask(offset,intervals);
	}
	
	public Mask mask(int offset, MaskStructure maskStructure) {
		return new Mask(offset, maskStructure);
	}
	
	/////MaskStack

	public MaskStack maskStack(Mask... masks) {
		return new MaskStack(masks);
	}
	
	/////Space
	
	public Space space(MaskStack... maskStacks) {
		return new Space(maskStacks);
	}
	
	/////Point
	
	public Point point(int... coords) {
		return new Point(coords);
	}
	
	/////Vector
	
	public Vector vector(int... coords) {
		return new Vector(coords);
	}
	
	/////Vector set
	
	public VectorSet vectorSet(Space space, Vector...vectors) {
		return new VectorSet(space,vectors);
	}
	
	public VectorSet cross(VectorSet... vectorSets) {
		System.out.println("VectorSets passed to cross:");
		for(VectorSet vectorSet : vectorSets)
			System.out.println(vectorSet);
		System.out.println();
		return VectorSet.cross(vectorSets);
	}
	
	/////Point set
	
	public PointSet pointSet(Point refPoint, VectorSet vectorSet) {
		return new PointSet(refPoint, vectorSet);
	}
	
	public PointSet union(PointSet... pointSets) {
		return new PointSet(pointSets);
	}
	
	/////add
	
	public void add(PointSet...pointSets) {
		pointSet.add(pointSets);
	}
	
	/////Printing
	
	public String toString() {
		return this.getClass() + "\n" + pointSet;
	}
	
	/////For DrawNoteSet
	
	public int getMaxPitch() {
		return pointSet.getMax(1);
	}
	
	public int getMinPitch() {
		return pointSet.getMin(1);
	}

	public int getMaxTime() {
		return pointSet.getMax(0);
	}
	
	public int getMinTime() {
		return pointSet.getMin(0);
	}

	public TreeSet<Point> getPoints() {
		return pointSet.getPoints();
	}
	
}
