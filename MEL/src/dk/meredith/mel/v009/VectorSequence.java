package dk.meredith.mel.v009;

import java.util.ArrayList;

public class VectorSequence {

	private ArrayList<Vector> vectors = new ArrayList<Vector>();
	
	public VectorSequence(Vector... vectors) {
		for (Vector vector : vectors)
			this.vectors.add(vector);
	}

	public VectorSequence(ArrayList<Vector> vectors) {
		for (Vector vector : vectors)
			this.vectors.add(vector);
	}
	
	public VectorSequence(int multiplicity, Vector vector) {
		for(int i = 0; i < multiplicity; i++)
			this.vectors.add(vector);
	}

	public VectorSequence(MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence, int... coords) {
		for(int i = 0; i < coords.length; i += 2)
			vectors.add(new Vector(timeMaskSequence, pitchMaskSequence, coords[i], coords[i+1]));
	}

	public VectorSequence(VectorSequence... vectorSequences) {
		for(VectorSequence vectorSequence : vectorSequences)
			vectors.addAll(vectorSequence.vectors);
	}

	public VectorSequence inv() {
		ArrayList<Vector> newVecs = new ArrayList<Vector>();
		for(Vector vector : vectors)
			newVecs.add(vector.inv());
		return new VectorSequence(newVecs);
	}

	public VectorSequence ret() {
		return rev().inv();
	}

	private VectorSequence rev() {
		ArrayList<Vector> newVecs = new ArrayList<Vector>();
		for(int i = vectors.size()-1; i >= 0; i--)
			newVecs.add(vectors.get(i));
		return new VectorSequence(newVecs);
	}
	
	public ArrayList<VectorSum> getVectorSums() {
		ArrayList<VectorSum> vectorSums = new ArrayList<VectorSum>();
		for(int i = 0; i < vectors.size(); i++) {
			VectorSum vectorSum = new VectorSum();
			for(int j = 0; j <= i; j++)
				vectorSum.add(vectors.get(i));
			vectorSums.add(vectorSum);
		}
		return vectorSums;
	}
	
	public String toString() {
		return vectors.toString();
	}
}
