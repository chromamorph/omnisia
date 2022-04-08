package com.chromamorph.maxtranpatsjava;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class CheckMissingPairFiles {

	
	public static String ROOT_FOLDER = "../../nlb20210504/";

	public static String INPUT_DIR = ROOT_FOLDER + "data/nlb/nlb_datasets/annmidi";

	public static String[] OUTPUT_FOLDERS = new String[]{
			"output/pair-files-F2STR-with-scalexia",
			"output/pair-files-F2STR-with-scalexia-corsair",
			"output/pair-files-F2STR-with-scalexia-mac2010",
			"output/pair-files-F2STR-with-scalexia-new-mac",
			"output/pair-files-F2STR-with-scalexia-p50",
			"output/pair-files-F2STR-with-scalexia-missing"
	};

	public static boolean[] PAIR_FILE_PRESENT = new boolean[64620];

	public static ArrayList<String> NLB_FILE_NAMES = new ArrayList<String>();
	
	public static int NUM_PAIR_FILES_MISSING = 0;

	public static void computePairFilePresentArray() {
		for(boolean b : PAIR_FILE_PRESENT)
			b = false;
		for(String outputFolder : OUTPUT_FOLDERS) {
			File dir = new File(ROOT_FOLDER + outputFolder);
			String[] fileList = dir.list(
					new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							if (name.startsWith("."))
								return false;
							File f = new File(dir, name);
							if (!(f.isDirectory()))
								return false;
							String[] d = f.list();
							if (d.length != 1)
								return false;
							if (!d[0].endsWith(".enc"))
								return false;
							for(int i = 0; i < 5; i++)
								if (!Character.isDigit(name.charAt(i)))
									return false;
							return true;
						}
						
					});
			for(String fileName : fileList) {
				int i = Integer.parseInt(fileName.substring(0,5));
				PAIR_FILE_PRESENT[i] = true;
			}
		}
		for(boolean b : PAIR_FILE_PRESENT)
			if (!b) NUM_PAIR_FILES_MISSING++;
	}
	
	public static void getNLBFileNamesList() {
		String[] nlbFileNamesArray = Utility.getInputFileNames(INPUT_DIR);
		for (String nlbFileName : nlbFileNamesArray)
			NLB_FILE_NAMES.add(nlbFileName);
		NLB_FILE_NAMES.sort(null);		
	}
		
	public static void main(String[] args) {
		computePairFilePresentArray();
		getNLBFileNamesList();
		System.out.println(NUM_PAIR_FILES_MISSING+" pair files missing");
	};
	
}
