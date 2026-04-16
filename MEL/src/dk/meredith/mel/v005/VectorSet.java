package dk.meredith.mel.v005;

import java.util.TreeSet;

public class VectorSet {
	private TreeSet<Vector> vectors = new TreeSet<Vector>();
	
	public VectorSet() {}
	
	public VectorSet(Vector... vecs) {
		for(Vector v : vectors)
			vectors.add(v);
	}
	
	public VectorSet add(Vector vector) {
		vectors.add(vector);
		return this;
	}
	
	public VectorSet add(VectorSet vectorSet) {
		for(Vector v : vectorSet.vectors)
			vectors.add(v);
		return this;
	}
	
	public VectorSet x(VectorSet vectorSet) {
		VectorSet vs = new VectorSet();
		vs.add(this);
		vs.add(vectorSet);
		for(Vector v1 : vectors)
			for(Vector v2 : vectorSet.vectors)
				vs.add(v1.plus(v2));
		return vs;
	}
	
	public VectorSet x(VectorSequence seq) {
		return x(seq.toSet());
	}
	
	public TreeSet<Vector> getVectors() {
		return vectors;
	}
	
	public String toString() {
		return vectors.toString();
	}
}
