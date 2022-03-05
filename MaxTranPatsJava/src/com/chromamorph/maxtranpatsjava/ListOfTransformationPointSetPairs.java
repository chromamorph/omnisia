package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class ListOfTransformationPointSetPairs {

	ArrayList<TransformationPointSetPair> pairs = new ArrayList<TransformationPointSetPair>();
	
	public ListOfTransformationPointSetPairs() {}
	
	public void add(Transformation transformation, PointSequence pointSequence) {
		for(TransformationPointSetPair pair : pairs) {
			if (pair.getTransformation().equals(transformation)) {
				pair.getPointSet().addAll(pointSequence);
				return;
			}
		}
//		If we get to here, then this is a new transformation, so we need to make
//		a new TransformationPointSetPair and add to the list
		pairs.add(new TransformationPointSetPair(transformation, pointSequence.toPointSet()));
	}
	
}
