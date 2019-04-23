package com.chromamorph.points022;

import java.io.File;

public class SegmentationFromTECAnalysis02 {

	static Encoding encoding;
	
	
	public static void main(String[] args) {
//		Read in TEC analysis
		readTECAnalysis(args[0]);
//		Compute the segmentation
		computeSegmentation();
//		Store the segmentation in a file
		writeSegmentationToFile();
//		Display the segmentation and store image in a file
		displaySegmentationAndWriteImageToFile();
	}

	private static void displaySegmentationAndWriteImageToFile() {
		// TODO Auto-generated method stub
		
	}

	private static void writeSegmentationToFile() {
		// TODO Auto-generated method stub
		
	}

	private static void computeSegmentation() {
		/*
		 * Create pattern distribution array (PDA).
		 * Array has an entry for each tatum at which
		 * there is a change in the pattern distribution.
		 * There is a change in the pattern distribution
		 * each time a pattern occurrence starts or ends.
		 * Suppose the TEC analysis contains N TECs 
		 * (i.e., N patterns). Each entry in the PDA is 
		 * an array of size N. Suppose PDA[i] = A, then
		 * |A| = N. A[0] is the salience of pattern 0, the 
		 * first pattern in the analysis, generated first by
		 * SIATECCompress or COSIATEC. 
		 */
	}

	private static void readTECAnalysis(String fileName) {
		encoding = new COSIATECEncoding(fileName);
	}
}
