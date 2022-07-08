package com.chromamorph.points022.segmentation01;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;
import com.chromamorph.points022.COSIATECEncoding;
import com.chromamorph.points022.Encoding;
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
	private StructuralSegmentation segmentation;
	
	public SegWithTECs() {
//		String pointSetFilePath = "/Users/dave/Documents/Work/Research/Data/Sony/Lounge music segmentation/FilesWithGroundTruthDM/LC0103/LC0103.mid";
//		String encodingFilePathName = "/Users/dave/Documents/Work/Research/Data/Sony/Lounge music segmentation/FilesWithGroundTruthDM/LC0103/LC0103-diat.cos";
		
//		String pointSetFilePath = "D:\\Dropbox\\Work\\Research\\AI Song Contest\\Geerdes\\20191206geerdes\\Arcade - Duncan Laurence\\42366.mid";
//		String encodingFilePathName = "D:\\Dropbox\\Work\\Research\\AI Song Contest\\Geerdes\\20191206geerdes\\Arcade - Duncan Laurence\\42366-mid-2019-12-08-19-40-23-410\\42366-diat.cos";

		String pointSetFilePath = "/Users/susanne/Repos/omnisia/Points/output/Begg/Begg3_0-1729-76-1-xml-2022-07-01-21-11-25-324/Begg3_0-1729-76-1.pts";
//		String encodingFilePathName = "/Users/susanne/Repos/omnisia/Points/output/Begg/Begg3_0-1729-76-1-xml-2022-07-01-21-11-25-324/Begg3_0-1729-76-1-diat.cos";
		String encodingFilePathName = "/Users/susanne/Repos/omnisia/Points/data/Beate Kutschke/MusicXMLs London/Begg/Begg3_0-1729-76-1.xml";
		try {
			pointSet = new PointSet(pointSetFilePath,true);
		} catch (MissingTieStartNoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		encoding = new COSIATECEncoding(encodingFilePathName);
//		pointSet.draw(new File(pointSetFilePath).getName(), true);
//		encoding.drawOccurrenceSets(true);
		segmentation = new StructuralSegmentation(encoding,pointSet,encodingFilePathName.contains("diat"));
		segmentation.draw();
	}
	
	@Override
	public String toString() {
		return segmentation.toString();
	}
	
	public static void main(String[] args) {
		try {
//			PrintWriter out = new PrintWriter("D:\\Dropbox\\Work\\Research\\AI Song Contest\\Geerdes\\20191206geerdes\\Arcade - Duncan Laurence\\42366-mid-2019-12-08-19-40-23-410\\42366-diat.seg");
			PrintWriter out = new PrintWriter("/Users/susanne/Repos/omnisia/Points/output/Segmentation/Begg3_0-1729-76-1.seg");
			out.println(new SegWithTECs());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
