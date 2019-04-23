package com.chromamorph.notes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PitchSpellWTC1 {

	static class Result {
		double noteAccuracy = 0.0;
		int noteErrorCount = 0;
		int numberOfNotes = 0;

		public Result(double noteAccuracy, int noteErrorCount, int numberOfNotes) {
			this.noteAccuracy = noteAccuracy;
			this.noteErrorCount = noteErrorCount;
			this.numberOfNotes = numberOfNotes;
		}
	}

	static Result bestResult = null;
	static int bestPreContext = 0;
	static int bestPostContext = 0;

	public static void main(String[] args) {
		String wtc1DirectoryPathName = "/Users/dave/Documents/Work/Research/2015-06-17-workspace/Points/data/WTCI-FUGUES-FOR-JNMR-2014";
		String[] inputFileNames = new File(wtc1DirectoryPathName).list();
		for(int preContext = 0; preContext <= 50; preContext++) {
			for(int postContext = 0; postContext <= 50; postContext++) {
				ArrayList<Result> results = new ArrayList<Result>();
				for(String inputFileName : inputFileNames) {
					if (inputFileName.endsWith(".opnd")) {
						try {
							Notes notes = Notes.fromOPND(wtc1DirectoryPathName+"/"+inputFileName);
							notes.pitchSpell(preContext,postContext);
							double noteAccuracy = notes.getPitchSpellingNoteAccuracy();
							int noteErrorCount = notes.getPitchSpellingNoteErrorCount();
							int numberOfNotes = notes.getNumberOfNotes();
							results.add(new Result(noteAccuracy,noteErrorCount,numberOfNotes));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				int totalNumberOfNotes = 0;
				int totalNumberOfErrors = 0;
				for(Result result : results) {
					totalNumberOfNotes += result.numberOfNotes;
					totalNumberOfErrors += result.noteErrorCount;
				}
				double totalNoteAccuracy = (1.0 - (1.0 * totalNumberOfErrors)/totalNumberOfNotes);
				System.out.println(preContext + "\t"+postContext+"\t"+totalNumberOfErrors+"\t"+totalNoteAccuracy);
				if (bestResult == null || totalNumberOfErrors < bestResult.noteErrorCount) {
					bestResult = new Result(totalNoteAccuracy,totalNumberOfErrors,totalNumberOfNotes);
					bestPreContext = preContext;
					bestPostContext = postContext;
				}
			}
		}
		System.out.println("Best precontext = "+bestPreContext);
		System.out.println("Best postcontext = "+bestPostContext);
		System.out.println("Best note accuracy = "+bestResult.noteAccuracy);
		System.out.println("Best note error count = "+bestResult.noteErrorCount);
	}

}
