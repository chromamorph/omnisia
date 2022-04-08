package com.chromamorph.maxtranpatsjava;

import java.util.concurrent.RecursiveAction;

public class CheckLargerMTPAction extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OccurrenceSet mtp, largerMTP;
	
	public CheckLargerMTPAction(OccurrenceSet mtp, OccurrenceSet largerMTP) {
		this.mtp = mtp;
		this.largerMTP = largerMTP;
	}
	
	@Override
	protected void compute() {
		if (largerMTP.getPattern().contains(mtp.getPattern())) {
			synchronized (mtp) {
				mtp.addSuperMTP(largerMTP);
			}
		}
	}

}
