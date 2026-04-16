package dk.meredith.mel.v007;

import java.util.TreeSet;

public class VectorSet {
	private TreeSet<Vector> vectors = new TreeSet<Vector>();
	
	public VectorSet() {}
	
	public VectorSet(Vector... vectors) {
		for(Vector vector : vectors) {
			this.vectors.add(vector);
		}
	}
	
	public VectorSet(Space space, Vector... vectors) {
		for(Vector vector : vectors) {
			this.vectors.add(new Vector(space, vector.getCoords()));
		}
	}
	
	public VectorSet(VectorSet... vectorSets) {
		for(VectorSet vectorSet : vectorSets) {
			vectors.addAll(vectorSet.vectors);
		}
	}
	
	public static VectorSet cross(VectorSet... vectorSets) {
		VectorSet vectorSet = new VectorSet(vectorSets[0]);
		for(int i = 1; i < vectorSets.length; i++) {
			VectorSet vs1 = new VectorSet(vectorSet);
			VectorSet vs2 = vectorSets[i];
			for(Vector v1 : vs1.getVectors())
				for(Vector v2 : vs2.getVectors())
					vectorSet.add(v1.add(v2));
		}
		vectorSet.add(vectorSets);
		vectorSet.add(Vector.zeroVector(vectorSet.getDimension()));
		return vectorSet;
	}
	
	public int getDimension() {
		return getVectors().first().getDimension();
	}
	
	public void add(Vector vector) {
		vectors.add(vector);
	}
	
	public void add(VectorSet... vectorSets) {
		for(VectorSet vectorSet : vectorSets)
			for(Vector vector : vectorSet.vectors)
				add(vector);
	}
	
	public TreeSet<Vector> getVectors() {
		return vectors;
	}
	
	public String toString() {
		return vectors.toString();
	}
}
