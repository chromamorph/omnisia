package com.chromamorph.points022;

import java.io.File;

public class RunCOSIATECOnWTCIRandomPointSets {
	
	public static void main(String[] args) {
		File inputDir = new File("/Users/dave/Documents/Work/Research/workspace/Points/output/points018/WTCIRandomPointSets");
		String[] inputFileNameList = inputDir.list();
		String outputDir = "/Users/dave/Documents/Work/Research/workspace/Points/output/points018/COSIATECOnWTCIRandomPointSets-new";
//		int counter = 0;
		for(String inputFileName : inputFileNameList) {
			if (inputFileName.endsWith("-random.pts")) {
				System.out.println("\n"+inputFileName);
				PointSet points = new PointSet(inputDir+"/"+inputFileName);
				new COSIATECEncoding(points,inputFileName,outputDir,true,inputDir+"/"+inputFileName);
			}
//			counter++;
//			if (counter == 2) break;
		}
	}
}
