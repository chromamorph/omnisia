package com.chromamorph.points022;

public class SIAMIREXTest {
	public static void main(String[] args) {
		//Run SIA on chosen file

		Encoding encoding;
		String[] algorithms = {"SIA"};
		boolean[] trueOrFalse = {true, false};
		RawSegmentBB[] rawSegmentBB = {RawSegmentBB.RAW,RawSegmentBB.SEGMENT,RawSegmentBB.BB};
		String[] inputFilePaths = {
//				"/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/simple/ThreeSquares.pts"
				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/bachBWV889Fg/polyphonic/lisp/wtc2f20.txt"
//				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/beethovenOp2No1Mvt3/polyphonic/lisp/sonata01-3.txt",
//				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/chopinOp24No4/polyphonic/lisp/mazurka24-4.txt",
//				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/gibbonsSilverSwan1612/polyphonic/lisp/silverswan.txt",
//				"/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth/mozartK282Mvt2/polyphonic/lisp/sonata04-2.txt"
		};
		String outputDirectoryName = "/Users/dave/Documents/Work/Research/Data/SIAMIREXTest/";

		int r = 3; // 3 superdiagonals when running SIAR
		double a = 0.66; // threshold for compactness using compactness trawler
		int b = 3; // minimum size of pattern when using compactness trawler
		try {
			for(String algorithm : algorithms)
				for (boolean forRSubdiagonals : trueOrFalse)
					for(boolean withCompactnessTrawler : trueOrFalse)
						for(RawSegmentBB rsb : rawSegmentBB)
							for(String inputFilePath : inputFilePaths) {
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
								encoding = new SIAEncoding(inputFilePath,
										outputDirectoryName+"/"+algorithmFolderName,
										forRSubdiagonals, r,
										withCompactnessTrawler, a, b);
								//	
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
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
