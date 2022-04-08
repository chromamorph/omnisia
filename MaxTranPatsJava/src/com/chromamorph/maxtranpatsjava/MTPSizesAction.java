package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class MTPSizesAction extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PointSet pointSet;
	private int mtpSizesIndex;
	
	public void setPointSet(PointSet pointSet) {
		this.pointSet = pointSet;
	}
	public PointSet getPointSet() {
		return pointSet;
	}
	public void setMTPSizesIndex(int mtpSizesIndex) {
		this.mtpSizesIndex = mtpSizesIndex;
	}
	public int getMTPSizesIndex() {
		return mtpSizesIndex;
	}
	
	public MTPSizesAction(PointSet pointSet, int mtpSizesIndex) {
		setPointSet(pointSet);
		setMTPSizesIndex(mtpSizesIndex);
	}
	
	@Override
	protected void compute() {
		int size = pointSet.getMTPSizes().get(mtpSizesIndex);
		ArrayList<OccurrenceSet> mtpsOfThisSize = pointSet.getMTPOccurrenceSets()[size];
		ArrayList<ComputeSuperMTPsForSingleMTPAction> actions = new ArrayList<ComputeSuperMTPsForSingleMTPAction>();
		for(OccurrenceSet mtp : mtpsOfThisSize) {
			actions.add(new ComputeSuperMTPsForSingleMTPAction(pointSet, getMTPSizesIndex(), mtp));
		}
		invokeAll(actions);
	}

}
