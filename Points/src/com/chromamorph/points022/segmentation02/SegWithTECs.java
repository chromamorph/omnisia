package com.chromamorph.points022.segmentation02;

import com.chromamorph.points022.COSIATECEncoding;
import com.chromamorph.points022.Encoding;
import com.chromamorph.points022.MIREX2013Entries;
import com.chromamorph.points022.PointSet;


/**
 * 
 * @author David Meredith
 * @date 7 February 2016
 * Takes a TEC analysis and a point set as input and computes a structural segmentation.
 */
public class SegWithTECs {
	
	private PointSet pointSet;
	private Encoding encoding;
	private StructuralSegmentation segmentation, groundTruth;
	private String pointSetFilePath, encodingFilePath, groundTruthFilePath, outputDirectoryPath;
	
	public SegWithTECs() {
		outputDirectoryPath = MIREX2013Entries.getFileName("Select output directory", "~/", true);
		pointSetFilePath = MIREX2013Entries.getFileName("Select input point set file", outputDirectoryPath);
		encodingFilePath = MIREX2013Entries.getFileName("Select TEC encoding file", outputDirectoryPath);
		groundTruthFilePath = MIREX2013Entries.getFileName("Select ground truth file", outputDirectoryPath);
		
		pointSet = new PointSet(pointSetFilePath,true);
		encoding = new COSIATECEncoding(encodingFilePath);
		
		segmentation = new StructuralSegmentation(
				encoding,
				pointSet,
				encodingFilePath.contains("diat"),
				groundTruthFilePath);
		System.out.println("\nGround truth:"+segmentation.getGroundTruth());
		System.out.println("\nSegmentation:\n"+segmentation);
//		segmentation.draw();
		int start = pointSetFilePath.lastIndexOf("/")+1;
		int end = pointSetFilePath.lastIndexOf(".");
		String baseFileName = pointSetFilePath.substring(start,end);
		String outputFilePath = outputDirectoryPath+"/"+baseFileName+".seg";
//		segmentation.writeToFile(outputFilePath);
	}
	
	@Override
	public String toString() {
		return segmentation.toString();
	}
	
	public static void main(String[] args) {
		new SegWithTECs();
	}
}
