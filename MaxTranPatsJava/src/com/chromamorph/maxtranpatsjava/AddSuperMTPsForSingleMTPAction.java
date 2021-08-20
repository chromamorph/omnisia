package com.chromamorph.maxtranpatsjava;

import java.util.concurrent.RecursiveAction;

public class AddSuperMTPsForSingleMTPAction extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private OccurrenceSet mtp;
	
	public AddSuperMTPsForSingleMTPAction(OccurrenceSet mtp) {
		this.mtp = mtp;
	}
	
	@Override
	protected void compute() {
		try {
			mtp.addAllTransformations(mtp.getSuperMTPTransformations());
		} catch (TimeOutException e) {
			e.printStackTrace();
		}
		mtp.setSuperMTPs(null);
	}

}
