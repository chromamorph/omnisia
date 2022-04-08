package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class ComputeSuperMTPsAction extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PointSet pointSet;
	
	public PointSet getPointSet() {return pointSet; }
	public void setPointSet(PointSet pointSet) {
		this.pointSet = pointSet;
	}
	
	public ComputeSuperMTPsAction(PointSet pointSet) {
		setPointSet(pointSet);
	}
	
	@Override
	protected void compute() {
		ArrayList<MTPSizesAction> mtpSizesActions = new ArrayList<MTPSizesAction>();
		for(int i = 0; i < pointSet.getMTPSizes().size()-1; i++)
			mtpSizesActions.add(new MTPSizesAction(pointSet,i));
		invokeAll(mtpSizesActions);
	}

}
