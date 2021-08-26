package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class AddSuperMTPTransformationsRecursiveAction extends RecursiveAction {

	private ArrayList<OccurrenceSet> mtps;
	
	public AddSuperMTPTransformationsRecursiveAction(ArrayList<OccurrenceSet> mtps) {
		this.mtps = mtps;
	}
	@Override
	protected void compute() {
		
		ArrayList<RecursiveAction> tasks = new ArrayList<RecursiveAction>();
		
		for(OccurrenceSet mtp: mtps) {
			tasks.add(new AddSuperMTPsForSingleMTPAction(mtp));
		}

		invokeAll(tasks);
	}
}
