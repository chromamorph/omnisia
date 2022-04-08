package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class ComputeSuperMTPsForSingleMTPAction extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PointSet pointSet;
	private int mtpSizesIndex;
	private OccurrenceSet mtp;
	
	public ComputeSuperMTPsForSingleMTPAction(
			PointSet pointSet, 
			int mtpSizesIndex, 
			OccurrenceSet mtp) {
		this.pointSet = pointSet;
		this.mtpSizesIndex = mtpSizesIndex;
		this.mtp = mtp;
	}
	
	@Override
	protected void compute() {
		ArrayList<CheckSuperMTPsOfSpecificSizeAction> actions = new ArrayList<CheckSuperMTPsOfSpecificSizeAction>();
		for(int j = mtpSizesIndex + 1; j < pointSet.getMTPSizes().size(); j++)
			actions.add(new CheckSuperMTPsOfSpecificSizeAction(pointSet,j,mtp));
		invokeAll(actions);
	}

}
