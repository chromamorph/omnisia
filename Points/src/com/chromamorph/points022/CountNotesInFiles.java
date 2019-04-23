package com.chromamorph.points022;

public class CountNotesInFiles {
	public static void main(String[] args) {
		String startDirectoryPath = "/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth";
		System.out.println(MIREX2013Entries.readLispFileIntoPointSet(MIREX2013Entries.getLispInputFilePath(startDirectoryPath)).size());
	}
}
