package com.chromamorph.points022;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.chromamorph.notes.Notes;

public class DrawGiraudFugueGroundTruthPatterns {

	private static String ROOT_FOLDER_PATH_NAME = "/Users/dave/Documents/Work/Research/Data/GiraudFugueTruthFiles/ground-truth-patterns/opnd";
	private static String SINGLE_FUGUE_FOLDER_PATH_NAME = null;

	public static void main(String[] args) {
		//		Get list of fugue directories
		ArrayList<String> fugueFolderPathNames = new ArrayList<String>();
		if (SINGLE_FUGUE_FOLDER_PATH_NAME == null) {
			File rootFolder = new File(ROOT_FOLDER_PATH_NAME);
			String[] fugueFolderNames = rootFolder.list();
			for(String fugueFolderName : fugueFolderNames) {
				if (fugueFolderName.startsWith("bwv"))
					fugueFolderPathNames.add(rootFolder+"/"+fugueFolderName);
			}
		} else {
			fugueFolderPathNames.add(SINGLE_FUGUE_FOLDER_PATH_NAME);
		}
		//		For each fugue
		for(String fugueFolderPathName : fugueFolderPathNames) {
			System.out.println(fugueFolderPathName);
			String[] patternFolderNames = new File(fugueFolderPathName).list();
			//			Load OPND file for this fugue
			String fugueOPNDFileName = null;
			for(String fileName : patternFolderNames) {
				System.out.println("  "+fileName);
				if (new File(fugueFolderPathName+"/"+fileName).isFile() && fileName.startsWith("bwv") && fileName.endsWith(".opnd")) {
					fugueOPNDFileName = fileName;
					break;
				}
			}
			Notes fugueNotes = null;
			String fugueOPNDFilePathName = fugueFolderPathName+"/"+fugueOPNDFileName;
			try {
				fugueNotes = Notes.fromOPND(fugueOPNDFilePathName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			PointSet fuguePointSet = null;
			try {
				fuguePointSet = new PointSet(fugueNotes,true);
			} catch (NoMorpheticPitchException e1) {
				e1.printStackTrace();
			}
			//			Get list of pattern directories for this fugue
			ArrayList<String> patternFolderPathNames = new ArrayList<String>();
			for(String patternFolderName : patternFolderNames) {
				if (patternFolderName.startsWith("S") || patternFolderName.startsWith("CS"))
					patternFolderPathNames.add(fugueFolderPathName+"/"+patternFolderName);
			}
			//			For each pattern
			for(String patternFolderPathName : patternFolderPathNames) {
				System.out.println("  "+patternFolderPathName);
				//				Get list of occurrence files
				String[] occurrenceFileNames = new File(patternFolderPathName).list();
				ArrayList<String> occurrenceFilePathNames = new ArrayList<String>();
				for(String occurrenceFileName : occurrenceFileNames) {
					if (occurrenceFileName.endsWith(".opnd") && occurrenceFileName.length()==9) 
						occurrenceFilePathNames.add(patternFolderPathName+"/"+occurrenceFileName);
				}
				//				Load occurrence files into an occurrence set
				ArrayList<PointSet> occurrenceSet = new ArrayList<PointSet>();
				for(String occurrenceFilePathName : occurrenceFilePathNames) {
					try {
						Notes occurrenceNotes = Notes.fromOPND(occurrenceFilePathName);
						PointSet occurrencePointSet = new PointSet(occurrenceNotes,true);
						occurrenceSet.add(occurrencePointSet);
						System.out.println("    "+occurrenceFilePathName);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (NoMorpheticPitchException e) {
						e.printStackTrace();
					}
				}
				//				Draw occurrence set for this pattern
				fuguePointSet.draw(occurrenceSet,patternFolderPathName);
			}
		}
	}

}
