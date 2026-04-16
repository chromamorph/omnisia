package dk.meredith.mel.v009;

import java.util.ArrayList;

public class VectorSum {
	
	private ArrayList<Vector> vectors = new ArrayList<Vector>();

	public VectorSum() {}
	
	public VectorSum(Vector... vectors) {
		for(int i = 0; i < vectors.length; i++)
			this.vectors.add(vectors[i]);
	}
	
	public VectorSum(ArrayList<Vector>... vectorLists) {
		for(ArrayList<Vector> vectorList : vectorLists)
			for(Vector vector : vectorList)
				this.vectors.add(vector);
	}
	
	public void add(Vector vector) {
		vectors.add(vector);
	}
	
	@SuppressWarnings("unchecked")
	public VectorSum add(VectorSum vectorSum) {
		return new VectorSum(vectors,vectorSum.vectors);
	}

	public ArrayList<Vector> getVectors() {
		return vectors;
	}
	
	public String toString() {
		return vectors.toString();
	}
}
