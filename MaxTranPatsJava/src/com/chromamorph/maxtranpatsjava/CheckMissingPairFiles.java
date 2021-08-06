package com.chromamorph.maxtranpatsjava;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

public class CheckMissingPairFiles {
	
	public static String ROOT_FOLDER = "/Users/susanne/Repos/nlb20210504/";

	public static String INPUT_DIR = ROOT_FOLDER + "data/nlb/nlb_datasets/annmidi";
	
	public static String[] OUTPUT_FOLDERS = new String[]{
			"output/pair-files-F2STR-with-scalexia",
			"output/pair-files-F2STR-with-scalexia-corsair",
			"output/pair-files-F2STR-with-scalexia-mac2010",
			"output/pair-files-F2STR-with-scalexia-new-mac",
			"output/pair-files-F2STR-with-scalexia-p50"
	};

	
	public static boolean[] PAIR_FILE_PRESENT = new boolean[64620];

	public static ArrayList<String> NLB_FILE_NAMES = new ArrayList<String>();

	
	static class NameFreqPair implements Comparable<NameFreqPair> {

		private String name;
		private int freq;
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setFreq(int freq) {
			this.freq = freq;
		}
		
		public String getName() {return name;}
		public int getFreq() {return freq;}
		
		public NameFreqPair(String name, int freq) {
			setName(name);
			setFreq(freq);
		}
		
		@Override
		public int compareTo(NameFreqPair o) {
			if (o == null) return 1;
			if (getFreq() < o.getFreq()) return -1;
			if (getFreq() > o.getFreq()) return 1;
			return getName().compareTo(o.getName());
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj instanceof NameFreqPair)) return false;
			return compareTo((NameFreqPair)obj)==0;
		}
		
		@Override
		public String toString() {
			return getName()+"\t"+getFreq(); 
		}
		
	}
	
	public static void main(String[] args) {


		for(boolean b : PAIR_FILE_PRESENT)
			b = false;

		

		for(String outputFolder : OUTPUT_FOLDERS) {
			File dir = new File(ROOT_FOLDER + outputFolder);
			String[] fileList = dir.list();
			for(String fileName : fileList) {
				int i = Integer.parseInt(fileName.substring(0,5));
				PAIR_FILE_PRESENT[i] = true;
			}
		}


		String[] nlbFileNamesArray = Utility.getInputFileNames(INPUT_DIR);
		for (String nlbFileName : nlbFileNamesArray)
			NLB_FILE_NAMES.add(nlbFileName);
		NLB_FILE_NAMES.sort(null);

		TreeSet<String> missingPairFiles = new TreeSet<String>();
		TreeSet<String> filesWithMissingComparisons = new TreeSet<String>();
		
		int[] numMissingFiles = new int[NLB_FILE_NAMES.size()];
		for(int i : numMissingFiles) numMissingFiles[i] = 0;
		
		int count = 0;
		for(int i = 0; i < NLB_FILE_NAMES.size() - 1; i++)
			for(int j = i + 1; j < NLB_FILE_NAMES.size(); j++) {
				if (!PAIR_FILE_PRESENT[count]) {
					numMissingFiles[i]++;
					numMissingFiles[j]++;
					String fn1 = NLB_FILE_NAMES.get(i);
					filesWithMissingComparisons.add(fn1);
					fn1 = fn1.replace(".", "-");
					String fn2 = NLB_FILE_NAMES.get(j);
					filesWithMissingComparisons.add(fn2);
					fn2 = fn2.replace(".","-");
					String countStr = String.format("%05d", count);
					String outputFilePrefix = countStr+"-"+fn1+"-"+fn2;
					missingPairFiles.add(outputFilePrefix);
				}
				count++;
			}

		TreeSet<NameFreqPair> nameFreqPairs = new TreeSet<NameFreqPair>();
		
		for(int i = 0; i < NLB_FILE_NAMES.size(); i++)
			nameFreqPairs.add(new NameFreqPair(NLB_FILE_NAMES.get(i),numMissingFiles[i]));
		
		System.out.println("Number of missing pair files: "+missingPairFiles.size());
		System.out.println("Number of files with missing comparisons: " + filesWithMissingComparisons.size());

		for(NameFreqPair nfp : nameFreqPairs)
			System.out.println(nfp);
		

	};
}
