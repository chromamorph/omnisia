package com.chromamorph.points022;

/**
 * 
 * @author David Meredith
 * @date 1 July 2014
 * 
 * Running this file carries out the running-time experiments for the
 * JNMR 2014 paper.
 *
 */
public class JNMR2014RunningTimeExperiment {
	public static void main(String[] args) {

		Encoding encoding;
		String[] algorithms = {"COSIATEC", "Forth", "SIATECCompress"};
		boolean[] trueOrFalse = {true, false};
		RawSegmentBB[] rawSegmentBB = {RawSegmentBB.RAW,RawSegmentBB.SEGMENT,RawSegmentBB.BB};
		String inputFileRootFolderPathName = "/Users/dave/Documents/Work/Research/Data/RunningTimeData/InputFiles";
		String[] inputFilePaths = {
				inputFileRootFolderPathName+"/200.opnd",
				inputFileRootFolderPathName+"/400.opnd",
				inputFileRootFolderPathName+"/600.opnd",
				inputFileRootFolderPathName+"/800.opnd",
				inputFileRootFolderPathName+"/1000.opnd",
				inputFileRootFolderPathName+"/1500.opnd",
				inputFileRootFolderPathName+"/2000.opnd",
				inputFileRootFolderPathName+"/2500.opnd",
				inputFileRootFolderPathName+"/3000.opnd",
				inputFileRootFolderPathName+"/3500.opnd",
				inputFileRootFolderPathName+"/4000.opnd",
};
		String outputDirectoryName = "/Users/dave/Documents/Work/Research/Data/RunningTimeData/OutputFiles";

		int r = 3; // 3 superdiagonals when running SIAR
		double a = 0.66; // threshold for compactness using compactness trawler
		int b = 3; // minimum size of pattern when using compactness trawler
		try {
			int[] start = {0,0,0,0,0};
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
											withCompactnessTrawler, a, b, false,true);
								} else if (algorithm.equals("Forth")) {
									encoding = new ForthEncoding(inputFilePath,
											outputDirectoryName+"/"+algorithmFolderName,
											forRSubdiagonals, r, 
											withCompactnessTrawler, a, b);
								} else if (algorithm.equals("SIATECCompress")) {
									encoding = new SIATECCompressEncoding(inputFilePath,
											outputDirectoryName+"/"+algorithmFolderName,
											forRSubdiagonals, r,
											withCompactnessTrawler, a, b);
								} else
									throw new Exception("Unrecognized algorithm");

								//Write encoding to file
//								String inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf("/"));
//								PointSet dataset = new PointSet(inputFilePath,PitchRepresentation.MORPHETIC_PITCH);
//								String outputFileName = outputDirectoryName+"/"+algorithmFolderName+"/"+inputFileName.substring(0,inputFileName.lastIndexOf("."))+"."+algorithm;
//								MIREX2013Entries.writeEncodingToFile(
//										encoding,
//										outputFileName,
//										0,
//										rsb.equals(RawSegmentBB.BB),
//										rsb.equals(RawSegmentBB.SEGMENT),
//										dataset);
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
