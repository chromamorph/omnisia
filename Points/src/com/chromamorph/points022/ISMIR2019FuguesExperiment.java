package com.chromamorph.points022;

import java.io.IOException;

/**
 * 
 * @author David Meredith
 * @date 1 July 2014
 * @UPDATED 2019-04-08
 * 
 * Running this file REPLICATES all experiments on the fugues from the first book of the 48
 * reported in the 2015 jnmr PAPER.
 *
 */
public class ISMIR2019FuguesExperiment {
	public static void main(String[] args) {

		String pathToJarFile = "D:\\Dropbox\\Work\\Research\\Data\\Giraud-fugues\\omnisia3.jar";
		//Encoding encoding;
		String[] algorithms = {"COSIATEC"};//"SIATECCompress", "Forth"};
		boolean[] trueOrFalse = {true, false};
		RawSegmentBB[] rawSegmentBB = {RawSegmentBB.RAW,RawSegmentBB.SEGMENT,RawSegmentBB.BB};
		String inputFileRootFolderPathName = "D:\\Dropbox\\Work\\Research\\Data\\Giraud-fugues\\ground-truth-patterns\\opnd";
		String[] inputFilePaths = new String[24];
		for(int i = 0; i < 24; i++) {
			int fugueBwvNumber = 846+i;
			inputFilePaths[i] = inputFileRootFolderPathName + "\\bwv"+fugueBwvNumber+"\\bwv"+fugueBwvNumber+"b.opnd";
		}
		String outputDirectoryName = "D:\\Dropbox\\Work\\Research\\Data\\Giraud-fugues\\test-computed-patterns";

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
//								if (algorithm.equals("COSIATEC")) {
//									encoding = new COSIATECEncoding(inputFilePath,
//											outputDirectoryName+"/"+algorithmFolderName,
//											forRSubdiagonals, r,
//											withCompactnessTrawler, a, b, false, true);
//								} else if (algorithm.equals("Forth")) {
//									encoding = new ForthEncoding(inputFilePath,
//											outputDirectoryName+"/"+algorithmFolderName,
//											forRSubdiagonals, r, 
//											withCompactnessTrawler, a, b);
//								} else if (algorithm.equals("SIATECCompress")) {
//									encoding = new SIATECCompressEncoding(inputFilePath,
//											outputDirectoryName+"/"+algorithmFolderName,
//											forRSubdiagonals, r,
//											withCompactnessTrawler, a, b);
//								} else
//									throw new Exception("Unrecognized algorithm");

								String cmd = "java -jar " + pathToJarFile + 
										" -i " + inputFilePath +
										" -a " + algorithm +
										(forRSubdiagonals?" -rsd -r " + r:"") +
										(withCompactnessTrawler?" -ct -cta " + a + " -ctb " + b:"") +
										(rsb.equals(RawSegmentBB.SEGMENT)?" -segmode ":(rsb.equals(RawSegmentBB.BB)?" -bbmode ":" ")) +
										"-d -nodate -o " + outputDirectoryName +"\\"+ algorithmFolderName;
								System.out.println(cmd);
								
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
