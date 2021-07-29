package com.chromamorph.maxtranpatsjava;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

public class CheckMissingPairFiles {
	public static void main(String[] args) {

		boolean[] present = new boolean[64620];

		for(boolean b : present)
			b = false;

		String[] outputFolders = new String[]{
				"output/nlb-20210504/pair-files-F2STR-with-scalexia",
				"output/nlb-20210504/pair-files-F2STR-with-scalexia-corsair",
				"output/nlb-20210504/pair-files-F2STR-with-scalexia-mac2010",
				"output/nlb-20210504/pair-files-F2STR-with-scalexia-new-mac",
				"output/nlb-20210504/pair-files-F2STR-with-scalexia-p50"
		};

		for(String outputFolder : outputFolders) {
			File dir = new File(outputFolder);
			String[] fileList = dir.list();
			for(String fileName : fileList) {
				int i = Integer.parseInt(fileName.substring(0,5));
				present[i] = true;
			}
		}

		String inputDir = "data/nlb/nlb_datasets/annmidi";

		String[] nlbFileNamesArray = Utility.getInputFileNames(inputDir);
		ArrayList<String> nlbFileNames = new ArrayList<String>();
		for (String nlbFileName : nlbFileNamesArray)
			nlbFileNames.add(nlbFileName);
		nlbFileNames.sort(null);

		TreeSet<String> missingPairFiles = new TreeSet<String>();
		TreeSet<String> filesWithMissingComparisons = new TreeSet<String>();
		
		
		int count = 0;
		for(int i = 0; i < nlbFileNames.size() - 1; i++)
			for(int j = i + 1; j < nlbFileNames.size(); j++) {
				if (!present[count]) {
					String fn1 = nlbFileNames.get(i);
					filesWithMissingComparisons.add(fn1);
					fn1 = fn1.replace(".", "-");
					String fn2 = nlbFileNames.get(j);
					filesWithMissingComparisons.add(fn2);
					fn2 = fn2.replace(".","-");
					String countStr = String.format("%05d", count);
					String outputFilePrefix = countStr+"-"+fn1+"-"+fn2;
					missingPairFiles.add(outputFilePrefix);
				}
				count++;
			}

		System.out.println("Number of missing pair files: "+missingPairFiles.size());
		System.out.println("Number of files with missing comparisons: " + filesWithMissingComparisons.size());


	};
}
