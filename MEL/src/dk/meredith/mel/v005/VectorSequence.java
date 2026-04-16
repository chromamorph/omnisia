package dk.meredith.mel.v005;

import java.util.ArrayList;

public class VectorSequence {

	private ArrayList<Vector> vectors = new ArrayList<Vector>();
	
	public VectorSequence() {}
	
	public VectorSequence(Vector...vectors) {
		for(Vector v : vectors)
			this.vectors.add(v);
	}
	
	public VectorSequence(Vector vector, int multiplicity) {
		add(vector, multiplicity);
	}
	
	public VectorSequence add(Vector...vectors) {
		for(Vector v : vectors)
			this.vectors.add(v);
		return this;
	}

	public VectorSequence add(VectorSequence vectorSequence) {
		for(Vector v : vectorSequence.getVectors())
			vectors.add(v);
		return this;
	}
	
	public VectorSequence add(Vector vector, int multiplicity) {
		for(int i = 0; i < multiplicity; i++)
			add(vector);
		return this;
	}
	
	public ArrayList<Vector> getVectors() {
		return vectors;
	}
	
	public VectorSet toSet() {
		VectorSet set = new VectorSet();
		Vector vec = new Vector(0,0);
		for(Vector v : vectors) {
			vec = vec.plus(v);
			set.add(vec);
		}
		return set;
	}
	
	public VectorSet x(VectorSequence seq) {
		return toSet().x(seq.toSet());
	}
	
	public VectorSet x(VectorSet set) {
		return toSet().x(set);
	}
	
	public String toString() {
		return vectors.toString();
	}
	
}
