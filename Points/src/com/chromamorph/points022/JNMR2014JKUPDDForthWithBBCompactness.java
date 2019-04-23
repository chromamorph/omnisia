package com.chromamorph.points022;

/**
 * 
 * @author David Meredith
 * @date 21 February 2014
 * 
 * Running this file carries out all experiments on JKU PDD
 * reported in the 2014 JNMR paper on COSIATEC. It also 
 * generates the result table published in that paper.
 *
 */
public class JNMR2014JKUPDDForthWithBBCompactness {
	public static void main(String[] args) {
		//Run all algorithms on the JKU PDD

		Encoding encoding;
		String[] algorithms = {"Forth"};
		int[] start = {0,0,0,0,0};
		boolean[] trueOrFalse = {true, false};
		RawSegmentBB[] rawSegmentBB = {RawSegmentBB.RAW,RawSegmentBB.SEGMENT,RawSegmentBB.BB};
		String[] inputFilePaths = {
				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/bachBWV889Fg/polyphonic/lisp/wtc2f20.txt",
				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/beethovenOp2No1Mvt3/polyphonic/lisp/sonata01-3.txt",
				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/chopinOp24No4/polyphonic/lisp/mazurka24-4.txt",
				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/gibbonsSilverSwan1612/polyphonic/lisp/silverswan.txt",
				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/mozartK282Mvt2/polyphonic/lisp/sonata04-2.txt"
		};
		String outputDirectoryName = "/Users/dave/Documents/Work/Research/Data/JKU-PDD/JNMR2014";

		int r = 3; // 3 superdiagonals when running SIAR
		double a = 0.66; // threshold for compactness using compactness trawler
		int b = 3; // minimum size of pattern when using compactness trawler
		try {
			for(int i = start[0]; i < algorithms.length; i++) {
				String algorithm = algorithms[i];
				for (int j = start[1]; j < trueOrFalse.length; j++) {
					boolean forRSubdiagonals = trueOrFalse[j];
					for(int k = start[2]; k < trueOrFalse.length; k++) {
						boolean withCompactnessTrawler = trueOrFalse[k];
						for(int l = start[3]; l < rawSegmentBB.length; l++) {
							RawSegmentBB rsb = rawSegmentBB[l];
							for(int m = start[4]; m < inputFilePaths.length; m++) {
								String inputFilePath = inputFilePaths[m];
								StringBuilder algFolder = new StringBuilder();
								if (algorithm.equals("Forth")) {
									algFolder.append("Forth");
									if (forRSubdiagonals)
										algFolder.append("R");
									if (withCompactnessTrawler)
										algFolder.append("CT");
								} else {
									if (algorithm.equals("COSIATEC")) 
										algFolder.append("CO");
									algFolder.append("SIA");
									if (forRSubdiagonals)
										algFolder.append("R");
									if (withCompactnessTrawler)
										algFolder.append("CT");
									if (algorithm.equals("COSIATEC") || algorithm.equals("SIATEC"))
										algFolder.append("TEC");
									if (algorithm.equals("SIATECCompress"))
										algFolder.append("TECCompress");
								}
								if (rsb.equals(RawSegmentBB.SEGMENT))
									algFolder.append("Segment");
								if (rsb.equals(RawSegmentBB.BB))
									algFolder.append("BB");
								String algorithmFolderName = algFolder.toString();
								algorithmFolderName = algorithmFolderName+"-BB-compactness";
								if (algorithm.equals("SIA")) {
									encoding = new SIAEncoding(inputFilePath,
											outputDirectoryName+"/"+algorithmFolderName,
											forRSubdiagonals, r,
											withCompactnessTrawler, a, b);
								} else if (algorithm.equals("SIATEC")) {
									encoding = new SIATECEncoding(inputFilePath,
											outputDirectoryName+"/"+algorithmFolderName,
											forRSubdiagonals, r,
											withCompactnessTrawler, a, b);
								} else if (algorithm.equals("COSIATEC")) {
									encoding = new COSIATECEncoding(inputFilePath,
											outputDirectoryName+"/"+algorithmFolderName,
											forRSubdiagonals, r,
											withCompactnessTrawler, a, b);
								} else if (algorithm.equals("Forth")) {
									encoding = new ForthEncoding(inputFilePath,
											outputDirectoryName+"/"+algorithmFolderName,
											forRSubdiagonals, r, 
											withCompactnessTrawler, a, b,
											true // useBoundingBoxCompactness
											);
								} else if (algorithm.equals("SIATECCompress")) {
									encoding = new SIATECCompressEncoding(inputFilePath,
											outputDirectoryName+"/"+algorithmFolderName,
											forRSubdiagonals, r,
											withCompactnessTrawler, a, b);
								} else
									throw new Exception("Unrecognized algorithm");

								//Write encoding to MIREX format file
								String inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf("/"));
								PointSet dataset = new PointSet(inputFilePath,PitchRepresentation.MORPHETIC_PITCH);
								String outputFileName = outputDirectoryName+"/"+algorithmFolderName+"/"+inputFileName.substring(0,inputFileName.lastIndexOf("."))+"."+algorithm;
								MIREX2013Entries.writeEncodingToFile(
										encoding,
										outputFileName,
										0,
										rsb.equals(RawSegmentBB.BB),
										rsb.equals(RawSegmentBB.SEGMENT),
										dataset);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
