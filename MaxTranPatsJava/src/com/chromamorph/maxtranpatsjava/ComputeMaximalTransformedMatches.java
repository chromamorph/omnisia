package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class ComputeMaximalTransformedMatches extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int numObjectBases, numImageBases;
	long startIndex, endIndex;
	ListOfTransformationPointSetPairs[] mtmArray;
	PointSet pattern;
	PointSet dataset;
	TransformationClass tc;

	public ComputeMaximalTransformedMatches(
			PointSet pattern,
			PointSet dataset, 
			TransformationClass tc,
			ListOfTransformationPointSetPairs[] mtmArray,
			long startIndex,
			long endIndex,
			int numObjectBases,
			int numImageBases) {
		this.pattern = pattern;
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
			long split = (endIndex + startIndex)/2;
			invokeAll(
					new ComputeMaximalTransformedMatches(pattern, dataset, tc, mtmArray, startIndex, split, numObjectBases, numImageBases),
					new ComputeMaximalTransformedMatches(pattern, dataset, tc, mtmArray, split, endIndex, numObjectBases, numImageBases));
		}
	}

	protected void computeDirectly() {
		long C = startIndex;
		int N = numObjectBases, p = tc.getPerms().length;
		long imgIndex = C/(N*p);
		long objIndex = (C % (N * p)) / p;
		if (!dataset.isMTM() && imgIndex < objIndex)
			return;
		int[] perm = tc.getPerm(C % p);
		PointSequence objectBasis, imageBasis;
		try {
			objectBasis = pattern.computeBasis(tc.getBasisSize(), objIndex);
			imageBasis = dataset.computeBasis(tc.getBasisSize(), imgIndex);
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
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
