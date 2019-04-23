package com.chromamorph.points022;

import java.io.File;
import java.io.IOException;

import com.chromamorph.notes.Notes;

public class RunCOSIATECOnWTCI {
	
	public static void main(String[] args) {
		File inputDir = new File("/Users/dave/Documents/Work/Research/workspace/Points/data/DMRN2012-WTCI");
		String[] inputFileNameList = inputDir.list();
		String outputDir = "/Users/dave/Documents/Work/Research/workspace/Points/output/points018/COSIATECOnWTCI-new";
//		int counter = 0;
		for(String inputFileName : inputFileNameList) {
			if (inputFileName.endsWith("-mel.opnd")) {
				try {
					System.out.println("\n"+inputFileName);
					PointSet points = new PointSet(Notes.fromOPND(inputDir+"/"+inputFileName),true);
					new COSIATECEncoding(points,inputFileName,outputDir,true,inputDir+"/"+inputFileName);
				} catch (NoMorpheticPitchException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//			counter++;
//			if (counter == 2) break;
		}
	}
}
