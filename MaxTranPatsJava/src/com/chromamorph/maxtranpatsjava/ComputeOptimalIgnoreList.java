package com.chromamorph.maxtranpatsjava;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class ComputeOptimalIgnoreList {

	public static int[] IGNORE_LIST_ARRAY = new int[] {303,164,163,165,162,125,166,315,143,32,359,6,350,154,115,123,2,31,35,222,28,110,118,121,344,346,112,242,323,192,308,336,358,147,13,20,33,4,161,293,17,66,256,285,339,67,23,36,52,86,180,277,312,355,16,26,61,65,69,119,120,183,206,207,212,232,309,318,319,320,324,328,330,349,356,348};
	public static ArrayList<Integer> MINIMAL_IGNORE_LIST = new ArrayList<Integer>();
	public static TreeSet<NameFreqPair> NAME_FREQ_PAIRS;
	public static TreeSet<String> FILES_WITH_MISSING_COMPARISONS;
	public static TreeSet<String> MISSING_PAIR_FILES = new TreeSet<String>();
	public static int[] MISSED_COMPARISONS_FOR_EACH_SONG;


	
	public static boolean[][] FAIL_TABLE = new boolean[360][360];

	
	public static String ROOT_FOLDER = "D:/Repos/nlb20210504/";

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
		private int index;
		private int freq;

		public void setIndex(int index) {
			this.index = index;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setFreq(int freq) {
			this.freq = freq;
		}

		public String getName() {return name;}
		public int getFreq() {return freq;}
		public int getIndex() {return index;}

		public NameFreqPair(String name, int index, int freq) {
			setName(name);
			setFreq(freq);
			setIndex(index);
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
			return getIndex() + "\t" + getName()+"\t"+getFreq(); 
		}

	}

	public static void computePairFilePresentArray() {
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
	}
	
	public static void initializeFailTable() {
		for(int i = 0; i < 360; i++)
			for(int j = 0; j < 360; j++)
				FAIL_TABLE[i][j] = false;		
	}
	
	public static void getNLBFileNamesList() {
		String[] nlbFileNamesArray = Utility.getInputFileNames(INPUT_DIR);
		for (String nlbFileName : nlbFileNamesArray)
			NLB_FILE_NAMES.add(nlbFileName);
		NLB_FILE_NAMES.sort(null);		
	}
	
	public static void computeSmallestIgnoreList() {		
		int lastNumMissingPairFiles = 100000;
		int iteration = 1;
		do {
			FILES_WITH_MISSING_COMPARISONS = new TreeSet<String>();

			MISSED_COMPARISONS_FOR_EACH_SONG = new int[NLB_FILE_NAMES.size()];
			for(int i = 0; i < NLB_FILE_NAMES.size(); i++) MISSED_COMPARISONS_FOR_EACH_SONG[i] = 0;

			int count = 0;
			for(int i = 0; i < NLB_FILE_NAMES.size() - 1; i++)
				for(int j = i + 1; j < NLB_FILE_NAMES.size(); j++) {
					if (!PAIR_FILE_PRESENT[count]) {
						if (!MINIMAL_IGNORE_LIST.contains(i) && !MINIMAL_IGNORE_LIST.contains(j)) {
							FAIL_TABLE[i][j] = true;
							MISSED_COMPARISONS_FOR_EACH_SONG[i]++;
							MISSED_COMPARISONS_FOR_EACH_SONG[j]++;
							String fn1 = NLB_FILE_NAMES.get(i);
							FILES_WITH_MISSING_COMPARISONS.add(fn1);
							fn1 = fn1.replace(".", "-");
							String fn2 = NLB_FILE_NAMES.get(j);
							FILES_WITH_MISSING_COMPARISONS.add(fn2);
							fn2 = fn2.replace(".","-");
							String countStr = String.format("%05d", count);
							String outputFilePrefix = countStr+"-"+fn1+"-"+fn2;
							MISSING_PAIR_FILES.add(outputFilePrefix);
						}
					}
					count++;
				}
			NAME_FREQ_PAIRS = new TreeSet<NameFreqPair>();

			for(int i = 0; i < NLB_FILE_NAMES.size(); i++)
				NAME_FREQ_PAIRS.add(new NameFreqPair(NLB_FILE_NAMES.get(i),i,MISSED_COMPARISONS_FOR_EACH_SONG[i]));
			if (MISSING_PAIR_FILES.size() == lastNumMissingPairFiles && MISSING_PAIR_FILES.size() > 0) {
				MINIMAL_IGNORE_LIST.remove(MINIMAL_IGNORE_LIST.size()-1);
			} else
				lastNumMissingPairFiles = MISSING_PAIR_FILES.size();
			if (MISSING_PAIR_FILES.size() > 0)
				MINIMAL_IGNORE_LIST.add(NAME_FREQ_PAIRS.last().getIndex());
			System.out.println("Iteration "+ (iteration++));
			System.out.println(MINIMAL_IGNORE_LIST);
			System.out.println();
		} while (MISSING_PAIR_FILES.size() > 0);
		
		System.out.println("Ignore list has length "+MINIMAL_IGNORE_LIST.size());
		System.out.println("Ignore list: "+ MINIMAL_IGNORE_LIST);

	}
	
	public static void printFailTable() {
		for(int i = 0; i < 360; i++) {
			System.out.print(String.format("%4d   ", i));
			for(int j = 0; j < 360; j++) {
				if (FAIL_TABLE[i][j] || FAIL_TABLE[j][i])
					System.out.print("x");
				else
					System.out.print(" ");
			}
			System.out.println();
		}		
	}
	
	public static void main(String[] args) {
		Arrays.sort(IGNORE_LIST_ARRAY);
		initializeFailTable();
		computePairFilePresentArray();
		getNLBFileNamesList();
		computeSmallestIgnoreList();
//		printFailTable();
	};
	
}
