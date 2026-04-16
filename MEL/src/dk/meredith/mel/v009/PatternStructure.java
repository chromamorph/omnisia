package dk.meredith.mel.v009;

import java.util.ArrayList;

public class PatternStructure {

	private ArrayList<VectorSum> vectorSums = new ArrayList<VectorSum>();
	
	public PatternStructure(VectorSequence... vectorSequences) {
		this.vectorSums = vectorSequences[0].getVectorSums();
		for(int i = 1; i < vectorSequences.length; i++) {
			@SuppressWarnings("unchecked")
			ArrayList<VectorSum> vsl1 = (ArrayList<VectorSum>)this.vectorSums.clone();
			ArrayList<VectorSum> vsl2 = vectorSequences[i].getVectorSums();
			for(VectorSum vs1 : vsl1)
				for(VectorSum vs2 : vsl2)
					this.vectorSums.add(vs1.add(vs2));
		}
		vectorSums.add(new VectorSum(Vector.ZERO));
		for(int i = 1; i < vectorSequences.length; i++)
			vectorSums.addAll(vectorSequences[i].getVectorSums());
	}

	public ArrayList<VectorSum> getVectorSums() {
		return vectorSums;
	}

	public String toString() {
		return vectorSums.toString();
	}
}
