package com.chromamorph.points022;

import java.io.File;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class RunSIATECCompressOnRandomPointSets {
	
	public static void main(String[] args) {
		File inputDir = new File("/Users/dave/Documents/Work/Research/workspace/Points/data/Random point sets");
		String[] inputFileNameList = inputDir.list();
		String outputDir = "/Users/dave/Documents/Work/Research/workspace/Points/output/points018/SIATECCompressOnRandomPointSets";
//		int counter = 0;
		for(String inputFileName : inputFileNameList) {
			if (inputFileName.endsWith(".pts")) {
					System.out.println("\n"+inputFileName);
					PointSet points;
					try {
						points = new PointSet(inputDir+"/"+inputFileName);
					} catch (MissingTieStartNoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SIATECCompressEncoding encoding = (SIATECCompressEncoding)points.encode(new SIATECCompress());
					String outputFileName = outputDir+"/"+inputFileName.substring(0,inputFileName.lastIndexOf("."))+"-SIATECCompress.txt";
					encoding.writeToFile(outputFileName);
					System.out.println("Written encoding to "+outputFileName);
			}
//			counter++;
//			if (counter == 2) break;
		}
	}
}
