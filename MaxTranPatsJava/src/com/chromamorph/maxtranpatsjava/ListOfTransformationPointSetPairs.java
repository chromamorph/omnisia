package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class ListOfTransformationPointSetPairs {

	private ArrayList<TransformationPointSetPair> pairs = new ArrayList<TransformationPointSetPair>();
	
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
	
	public int size() {
		return pairs.size();
	}
	
	public ArrayList<TransformationPointSetPair> getPairs() {
		return pairs;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("\nListOfTranformationPointSetPairs:\n\t");
		if (pairs.size() > 0)
			sb.append(pairs.get(0).toString());
		for (int i = 1; i < pairs.size(); i++)
			sb.append("\n\t"+pairs.get(i));
		sb.append("\n");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		ArrayList<Double> sigma = Utility.makeSigma(-1.0, 7.0, -5.0, -1.0);
		ArrayList<Double> sigma2 = Utility.makeSigma(-1.0, 10.0, -5.0, -1.0);		
		Transformation transformation = new Transformation(new F_2STR(), sigma);
		Transformation transformation2 = new Transformation(new F_2STR(), sigma2);
		PointSequence ps1 = new PointSequence(new PointSet(new Point(3.0,1.0), new Point(3.0,2.0), new Point(4.0,3.0), new Point(4.0,4.0)));
		PointSequence ps2 = new PointSequence(new PointSet(new Point(3.0,1.0), new Point(3.0,2.0), new Point(5.0,3.0), new Point(5.0,4.0)));
		PointSequence ps3 = new PointSequence(new PointSet(new Point(1.0,1.0), new Point(1.0,2.0), new Point(5.0,3.0), new Point(5.0,4.0)));
		ListOfTransformationPointSetPairs list = new ListOfTransformationPointSetPairs();
		list.add(transformation, ps1);
		System.out.println(list);
		list.add(transformation, ps2);
		System.out.println(list);
		list.add(transformation2, ps3);
	}
	
}
