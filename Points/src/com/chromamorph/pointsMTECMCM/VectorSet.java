package com.chromamorph.pointsMTECMCM;

import java.util.ArrayList;
import java.util.TreeSet;

public class VectorSet {
	private TreeSet<Vector> vectors = new TreeSet<Vector>();
	
	public VectorSet(Vector... vectors) {
		for(Vector vector : vectors)
			this.vectors.add(vector);
	}
	
	public void add(Vector vector) {
		vectors.add(vector);
	}
	
	public VectorSet copy() {
		VectorSet newVectorSet = new VectorSet();
		for(Vector vector : vectors)
			newVectorSet.add(vector.copy());
		return newVectorSet;
	}
	
	public TreeSet<Vector> getVectors() {
		return vectors;
	}
	
	public boolean contains(Vector vector) {
		return vectors.contains(vector);
	}
	
	public boolean remove(Vector vector) {
		return vectors.remove(vector);
	}
	
	public int size() {
		return getVectors().size();
	}
	
	public boolean isEmpty() {
		return vectors.isEmpty();
	}
	
	public String toString() {
		if (isEmpty()) return "V()";
		if (size() == 1) return vectors.first().toString();
		StringBuilder sb = new StringBuilder("V("+vectors.first());
		for(Vector vector : vectors.tailSet(vectors.first(), false))
			sb.append(","+vector);
		sb.append(")");
		return sb.toString();
	}
	
	public Vector get(int i) {
		ArrayList<Vector> array = new ArrayList<Vector>(vectors);
		return array.get(i);
	}

	public VectorSet remove(VectorSet removableSubset) {
		for(Vector vector : removableSubset.getVectors())
			vectors.remove(vector);
		return this.copy();
	}
	
	public static void main(String[] args) {
		VectorSet V1 = new VectorSet(new Vector(1,0), new Vector(2,0), new Vector(3,0));
		VectorSet V2 = new VectorSet(new Vector(1,0), new Vector(2,0));
		V1.remove(V2);
		System.out.println(V1);
	}
}
