package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class ComputeMaximalTransformablePatterns extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int minSize, startIndex, endIndex, numObjectBases;
	ListOfTransformationPointSetPairs[] mtpArray;
	PointSet pointSet;
	TransformationClass tc;

	public ComputeMaximalTransformablePatterns(
			PointSet pointSet, 
			TransformationClass tc,
			ListOfTransformationPointSetPairs[] mtpArray,
			int minSize,
			int startIndex,
			int endIndex,
			int numObjectBases) {
		this.minSize = minSize;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.mtpArray = mtpArray;
		this.pointSet = pointSet;
		this.tc = tc;
		this.numObjectBases = numObjectBases;
	}

	@Override
	protected void compute() {
		if (endIndex - startIndex == 1)
			computeDirectly();
		else {
			int split = (endIndex + startIndex)/2;
			invokeAll(
					new ComputeMaximalTransformablePatterns(pointSet, tc, mtpArray, minSize, startIndex, split, numObjectBases),
					new ComputeMaximalTransformablePatterns(pointSet, tc, mtpArray, minSize, split, endIndex, numObjectBases));
		}
	}

	protected void computeDirectly() {
		int C = startIndex, N = numObjectBases, p = tc.getPerms().length;
		int imgIndex = C/(N*p);
		int objIndex = (C % (N * p)) / p;
		if (imgIndex < objIndex)
			return;
		int[] perm = tc.getPerm(C % p);
		try {
			PointSequence objectBasis = pointSet.computeBasis(tc.getBasisSize(), objIndex);
			PointSequence imageBasis = pointSet.computeBasis(tc.getBasisSize(), imgIndex);
			PointSequence imgBasisPerm = new PointSequence();
			for(int i = 0; i < tc.getBasisSize(); i++)
				imgBasisPerm.add(imageBasis.get(perm[i]));
			ArrayList<Transformation> transformations = Transformation.getTransformations(tc, objectBasis, imgBasisPerm);
			for(Transformation transformation : transformations) {

				int i = transformation.hash(PointSet.HASH_TABLE_SIZE);
				synchronized (mtpArray[i]) {
					mtpArray[i].add(transformation,objectBasis);
				}

				i = transformation.getInverse().hash(PointSet.HASH_TABLE_SIZE);
				synchronized (mtpArray[i]) {
					mtpArray[i].add(transformation.getInverse(),imageBasis);
				}
				synchronized (tc) {
					tc.addTransformationInstance(transformation);
					tc.addTransformationInstance(transformation.getInverse());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
