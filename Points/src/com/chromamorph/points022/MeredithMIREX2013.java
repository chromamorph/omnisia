package com.chromamorph.points022;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.chromamorph.notes.Notes;

/**
 * MIREX 2013 Entry
 * ================
 * 
 * Runs COSIATEC on pieces in database given by user.
 */
public class MeredithMIREX2013 {

	private static String groundTruthFolder = "/Users/dave/Documents/Work/Research/workspace/Points/data/JKUPDD-noAudio-Jul2013/groundTruth/";

	public static void main(String[] args) {

		//Make lists of file paths for input files

		ArrayList<String> inputFilesMonophonicLisp = new ArrayList<String>();
		ArrayList<String> inputFilesPolyphonicLisp = new ArrayList<String>();

		String bachFolder = groundTruthFolder + "bachBWV889Fg/";
		String beethovenFolder = groundTruthFolder + "beethovenOp2No1Mvt3/";
		String chopinFolder = groundTruthFolder + "chopinOp24No4/";
		String gibbonsFolder = groundTruthFolder + "gibbonsSilverSwan1612/";
		String mozartFolder = groundTruthFolder + "mozartK282Mvt2/";

		ArrayList<String> composerFolderList = new ArrayList<String>();
		composerFolderList.add(bachFolder);
		composerFolderList.add(beethovenFolder);
		composerFolderList.add(chopinFolder);
		composerFolderList.add(gibbonsFolder);
		composerFolderList.add(mozartFolder);

		String lispFolder = "lisp/";

		String monophonicFolder = "monophonic/";
		String polyphonicFolder = "polyphonic/";

		String bachName = "wtc2f20";
		String beethovenName = "sonata01-3";
		String chopinName = "mazurka24-4";
		String gibbonsName = "silverswan";
		String mozartName = "sonata04-2";

		ArrayList<String> nameList = new ArrayList<String>();
		nameList.add(bachName);
		nameList.add(beethovenName);
		nameList.add(chopinName);
		nameList.add(gibbonsName);
		nameList.add(mozartName);

		String lispSuffix = ".opnd";

		//inputFilesMonophonicLisp

		inputFilesMonophonicLisp.add(bachFolder+     monophonicFolder+lispFolder+bachName+     lispSuffix);
		inputFilesMonophonicLisp.add(beethovenFolder+monophonicFolder+lispFolder+beethovenName+lispSuffix);
		inputFilesMonophonicLisp.add(chopinFolder+   monophonicFolder+lispFolder+chopinName+   lispSuffix);
		inputFilesMonophonicLisp.add(gibbonsFolder+  monophonicFolder+lispFolder+gibbonsName+  lispSuffix);
		inputFilesMonophonicLisp.add(mozartFolder+   monophonicFolder+lispFolder+mozartName+   lispSuffix);

		System.out.println("Monophonic lisp input files:");
		for(String fileName : inputFilesMonophonicLisp) 
			System.out.println(fileName + (new File(fileName).exists()?" exists":" does not exist"));

		//inputFilesPolyphonicLisp

		inputFilesPolyphonicLisp.add(bachFolder+     polyphonicFolder+lispFolder+bachName+     lispSuffix);
		inputFilesPolyphonicLisp.add(beethovenFolder+polyphonicFolder+lispFolder+beethovenName+lispSuffix);
		inputFilesPolyphonicLisp.add(chopinFolder+   polyphonicFolder+lispFolder+chopinName+   lispSuffix);
		inputFilesPolyphonicLisp.add(gibbonsFolder+  polyphonicFolder+lispFolder+gibbonsName+  lispSuffix);
		inputFilesPolyphonicLisp.add(mozartFolder+   polyphonicFolder+lispFolder+mozartName+   lispSuffix);

		System.out.println("Polyphonic lisp input files:");
		for(String fileName : inputFilesPolyphonicLisp) 
			System.out.println(fileName + (new File(fileName).exists()?" exists":" does not exist"));

		/*
		 * Run COSIATEC on the JKU PDD (July 2013, no audio version)
		 * downloaded from
		 * 
		 * <a href="https://dl.dropbox.com/u/11997856/JKU/JKUPDD-noAudio-Mar2013.zip">https://dl.dropbox.com/u/11997856/JKU/JKUPDD-noAudio-Jul2013.zip</a>
		 * 
		 * on 10 July 2013.
		 * 
		 * Evaluate the output using the methodology described at the MIREX
		 * 2013 WIKI page for the "Discovery of Repeated Themes & Sections"
		 * competition:
		 * 
		 * <a href="http://www.music-ir.org/mirex/wiki/2013:Discovery_of_Repeated_Themes_%26_Sections">http://www.music-ir.org/mirex/wiki/2013:Discovery_of_Repeated_Themes_%26_Sections</a>
		 * 
		 * COSIATEC is run on both the monophonic and polyphonic Lisp test files with chromatic and morphetic pitch. The Lisp test files
		 * are assumed to have been converted to OPND format before running COSIATEC on them.
		 * 
		 */

		boolean[] booleanValues = {true,false};
		String[] polyMonoFolders = {polyphonicFolder, monophonicFolder};
		
		//Run COSIATEC on polyphonic and monophonic Lisp encodings using chromatic and morphetic pitch

		for(int i = 0; i < 5; i++) {
			for(boolean diatonicPitch : booleanValues) {
				for(String polyMonoFolder : polyMonoFolders) {
					try {
						String fileName = nameList.get(i) + lispSuffix;
						String outputDirectoryName = composerFolderList.get(i)+polyMonoFolder+lispFolder;
						String fullFileName = outputDirectoryName+fileName;
						System.out.println(fullFileName);
						fileName = nameList.get(i);
						PointSet points = null;
						Notes notes = Notes.fromOPND(fullFileName);
						points = new PointSet(notes,diatonicPitch);
						new COSIATECEncoding(points,fileName,outputDirectoryName.substring(0,outputDirectoryName.length()-1),diatonicPitch,fullFileName);
					} catch (NoMorpheticPitchException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
