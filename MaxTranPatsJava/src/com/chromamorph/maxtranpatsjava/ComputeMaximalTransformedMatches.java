package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class ComputeMaximalTransformedMatches extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int minSize, startIndex, endIndex, numObjectBases, numImageBases;
	ListOfTransformationPointSetPairs[] mtmArray;
	PointSet pattern;
	PointSet dataset;
	TransformationClass tc;
	
	public ComputeMaximalTransformedMatches(
			PointSet pattern,
			PointSet dataset, 
			TransformationClass tc,
			ListOfTransformationPointSetPairs[] mtmArray,
			int minSize,
			int startIndex,
			int endIndex,
			int numObjectBases,
			int numImageBases) {
		this.pattern = pattern;
		this.minSize = minSize;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.mtmArray = mtmArray;
		this.dataset = dataset;
		this.tc = tc;
		this.numImageBases = numImageBases;
		this.numObjectBases = numObjectBases;
	}
	
	@Override
	protected void compute() {
		if (endIndex - startIndex == 1)
			computeDirectly();
		else {
			int split = (endIndex + startIndex)/2;
			invokeAll(
					new ComputeMaximalTransformedMatches(pattern, dataset, tc, mtmArray, minSize, startIndex, split, numObjectBases, numImageBases),
					new ComputeMaximalTransformedMatches(pattern, dataset, tc, mtmArray, minSize, split, endIndex, numObjectBases, numImageBases));
		}
	}
	
	protected void computeDirectly() {
		int C = startIndex, N = numObjectBases, p = tc.getPerms().length;
		int imgIndex = C/(N*p);
		int objIndex = (C % (N * p)) / p;
		if (imgIndex < objIndex)
			return;
		int[] perm = tc.getPerm(C % p);
		PointSequence objectBasis = pattern.computeBasis(tc.getBasisSize(), objIndex);
		PointSequence imageBasis = dataset.computeBasis(tc.getBasisSize(), imgIndex);
		PointSequence imgBasisPerm = new PointSequence();
		for(int i = 0; i < tc.getBasisSize(); i++)
			imgBasisPerm.add(imageBasis.get(perm[i]));
		ArrayList<Transformation> transformations = Transformation.getTransformations(tc, objectBasis, imgBasisPerm);
		for(Transformation transformation : transformations) {

			int i = transformation.hash(PointSet.HASH_TABLE_SIZE);
			synchronized (mtmArray[i]) {
				mtmArray[i].add(transformation,objectBasis);
			}
			
//			i = transformation.getInverse().hash(PointSet.HASH_TABLE_SIZE);
//			synchronized (mtmArray[i]) {
//				mtmArray[i].add(transformation.getInverse(),imageBasis);
//			}
			synchronized (tc) {
				tc.addTransformationInstance(transformation);
//				tc.addTransformationInstance(transformation.getInverse());
			}
		}

	}

}
