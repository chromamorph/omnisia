package com.chromamorph.points022;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MML2019ConvertJKUPDDOutputToMIREXFormat {
	
	public static ArrayList<String> getInputFilePathStringArray(String rootFolderPathString) {
		ArrayList<String> inputFilePathStringArray = new ArrayList<String>();
		File rootFolder = new File(rootFolderPathString);
		File[] rootFolderFiles = rootFolder.listFiles();
		for(File folder : rootFolderFiles) {
			if (folder.isDirectory()) {
				File[] algFolderFiles = folder.listFiles();
				for (File algFolderFile : algFolderFiles) {
					String name = algFolderFile.getName();
					if (name.endsWith(".cos") || name.endsWith(".Forth") || name.endsWith(".SIATECCompress")) {
						String inputFilePathString = algFolderFile.getAbsolutePath();
						inputFilePathStringArray.add(inputFilePathString);						
					}
				}
			}
		}
		return inputFilePathStringArray;
	}
	
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
	
	public static ArrayList<String> getOutputFilePathStringArray(ArrayList<String> inputFilePathStringArray) {
		ArrayList<String> outputFilePathStringArray = new ArrayList<String>();
		for(String path : inputFilePathStringArray) {
			String newPieceName = getNewPieceName(path);
			String algorithmName = getAlgorithmName(path);
			String pathFolder = getPathFolder(path);
			String outputFilePathString = pathFolder + newPieceName + "_" + algorithmName + ".txt";
			outputFilePathStringArray.add(outputFilePathString);
		}
		return outputFilePathStringArray;
	}
	
	public static void convert(String inputPath, String outputPath) {
		COSIATECEncoding encoding = new COSIATECEncoding(inputPath);
		String mirexString = encoding.toMIREXString();
		try {
			PrintWriter ps = new PrintWriter(outputPath);
			ps.println(mirexString);
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String rootFolderPathString = "D:\\Dropbox\\Work\\Research\\Papers in preparation\\2019-06-28-MML-2019-OPTISIA\\optisia\\output\\JKU-PDD";
		ArrayList<String> inputFilePathStringArray = getInputFilePathStringArray(rootFolderPathString);
//		for(String path : inputFilePathStringArray) System.out.println(path);
		ArrayList<String> outputFilePathStringArray = getOutputFilePathStringArray(inputFilePathStringArray);
		for(String path : outputFilePathStringArray) System.out.println(path);
		for(int i = 0; i < outputFilePathStringArray.size(); i++) 
			convert(inputFilePathStringArray.get(i),outputFilePathStringArray.get(i));

	}
}
