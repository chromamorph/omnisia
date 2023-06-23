package com.chromamorph.points022;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class DrawOccurrenceSet {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Syntax: java -jar DrawOccurrenceSet.jar <DATASET-OPND-FILE> <OCCURRENCE-SET-OPND-FILE> [MORPHETIC-PITCH]");
			boolean morpheticPitch = false;
			if (args.length == 3)
				morpheticPitch = true;
			try {
				PointSet dataset = new PointSet(args[0],morpheticPitch);
				
			} catch (MissingTieStartNoteException e) {
				e.printStackTrace();
			}
		}
	}

}
