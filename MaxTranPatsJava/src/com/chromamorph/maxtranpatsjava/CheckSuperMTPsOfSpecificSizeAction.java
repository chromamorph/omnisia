package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class CheckSuperMTPsOfSpecificSizeAction extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PointSet pointSet;
	private int mtpSizesIndex;
	private OccurrenceSet mtp;
	
	public CheckSuperMTPsOfSpecificSizeAction(
			PointSet pointSet,
			int mtpSizesIndex,
			OccurrenceSet mtp) {
		this.pointSet = pointSet;
		this.mtpSizesIndex = mtpSizesIndex;
		this.mtp = mtp;
	}
	
	@Override
	protected void compute() {
		ArrayList<OccurrenceSet> largerMTPs = pointSet.getMTPOccurrenceSets()[pointSet.getMTPSizes().get(mtpSizesIndex)];
		ArrayList<CheckLargerMTPAction> actions = new ArrayList<CheckLargerMTPAction>();
		for(OccurrenceSet largerMTP : largerMTPs)
			actions.add(new CheckLargerMTPAction(mtp,largerMTP));
		invokeAll(actions);
		

	}

}
