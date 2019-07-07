package com.chromamorph.points022;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class MML2019GARenameOutputFiles {
	
	public static String getNewPieceName(String path) {
		int endIndex = path.lastIndexOf('.');
		int beginIndex = path.lastIndexOf("\\")+1;
		String oldPieceName = path.substring(beginIndex,endIndex);
		switch (oldPieceName) {
		case "mazurka24-4" : return "chop_mazurka24-4";
		case "silverswan" : return "gbns_silverswan";
		case "sonata01-3" : return "beet_sonata01-3";
		case "sonata04-2" : return "mzrt_sonata04-2";
		case "wtc2f20" : return "bach_wtc2f20";
		}
		return null;
	}
	
	public static String getAlgorithmName(String path) {
		int endIndex = path.lastIndexOf("\\");
		int beginIndex = path.lastIndexOf("\\", endIndex -1)+1;
		return path.substring(beginIndex,endIndex);
	}
	
	public static String getPathFolder(String path) {
		int endIndex = path.lastIndexOf("\\");
		return path.substring(0,endIndex+1);
	}	
	
	static String mirexRootFolderString = "D:\\Dropbox\\Work\\Research\\Papers in preparation\\2019-06-28-MML-2019-OPTISIA\\optisia\\output\\JKU-PDD-m";
	static String encodingRootFolderString = "D:\\Dropbox\\Work\\Research\\Papers in preparation\\2019-06-28-MML-2019-OPTISIA\\optisia\\output_old\\JKU-PDD";
	
	public static void renameMIREXFiles() {
		File mirexRootFolder = new File(mirexRootFolderString);
		FilenameFilter fileFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				File file = new File(dir.getAbsoluteFile()+"/"+name);
				return (!name.startsWith(".") || file.isDirectory());
			}
			
		};
		File[] algFolders = mirexRootFolder.listFiles(fileFilter);
		for (File algFolder : algFolders) {
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".cos") || name.endsWith(".Forth") || name.endsWith(".SIATECCompress");
				}
				
			};
			File[] cosFiles = algFolder.listFiles(filter);
			for (File cosFile : cosFiles) {
				String oldFileName = cosFile.getAbsolutePath();
				String algorithmName = getAlgorithmName(oldFileName);
				String pathFolder = getPathFolder(oldFileName);
				String newPieceName = getNewPieceName(oldFileName);
				cosFile.renameTo(new File(pathFolder + newPieceName + "_" + algorithmName + ".txt"));
			}
			
		}
	}

	public static void renameEncodingFiles() {
		File encodingRootFolder = new File(encodingRootFolderString);
		FilenameFilter fileFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				File file = new File(dir.getAbsoluteFile()+"/"+name);
				return (!name.startsWith(".") || file.isDirectory());
			}
			
		};
		File[] algFolders = encodingRootFolder.listFiles(fileFilter);
		for (File algFolder : algFolders) {
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".cos") || name.endsWith(".Forth") || name.endsWith(".SIATECCompress");
				}
				
			};
			File[] cosFiles = algFolder.listFiles(filter);
			for (File cosFile : cosFiles) {
				String oldFileName = cosFile.getAbsolutePath();
				String algorithmName = getAlgorithmName(oldFileName);
				String newPieceName = getNewPieceName(oldFileName);
				cosFile.renameTo(new File(mirexRootFolderString + "\\" + algorithmName + "\\" + newPieceName + "_" + algorithmName + ".out"));
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		renameMIREXFiles();
//		renameEncodingFiles();
	}
}
