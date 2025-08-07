package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class ComputeMaximalTransformablePatternsRunnable implements Runnable {

	int minSize, startIndex, endIndex, numObjectBases;
	ListOfTransformationPointSetPairs[] mtpArray;
	PointSet pointSet;
	TransformationClass tc;

	public ComputeMaximalTransformablePatternsRunnable(
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
	public void run() {
		int N = numObjectBases, p = tc.getPerms().length;
		for (int C = startIndex; C < endIndex; C++ ) {
			int imgIndex = C/(N*p);
			int objIndex = (C % (N * p)) / p;
//			if (imgIndex < objIndex)
//				return;
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
					synchronized (tc) {
						tc.addTransformationInstance(transformation);
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
