package com.chromamorph.points022;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;


public class ZPAQCompressor {

	public ZPAQCompressor(String inputFileName, String outputDirectoryPath, PointSet dataset) {
		//Save dataset to a file
		String pointSetFileName = outputDirectoryPath + "/" + inputFileName + ".PointSet";
		System.out.println("pointSetFileName: "+pointSetFileName);
		File output = new File(pointSetFileName);
		if (! output.exists()) {
			try {
				output.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				PrintStream ps = new PrintStream(pointSetFileName);
				ps.print(dataset);
				ps.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (! new File(pointSetFileName+".zpaq").exists()) {
			//Compress dataset file and store to outputDirectory
			String command = "zpaq nsic "+pointSetFileName+".zpaq "+pointSetFileName;
			try {
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void CreateLogFile(String inputFileName, String outputDirectoryPath) {
		//Create a log file giving statistics about file
		String pointSetFileName = outputDirectoryPath + "/" + inputFileName + ".PointSet";
		System.out.println("\npointSetFileName : "+pointSetFileName);
		System.out.println("outputDirectoryPath : "+outputDirectoryPath);
		System.out.println("inputFileName: "+inputFileName);
		long sizeOfPointsFile = new File(pointSetFileName).length();
		long sizeOfCompressedFile = new File(pointSetFileName+".zpaq").length();
		String logFileName = outputDirectoryPath + "/" + inputFileName + ".log";
		File file = new File(logFileName);
		if (! file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			PrintStream ps = new PrintStream(logFileName);
			ps.println("Uncompressed length: "+sizeOfPointsFile);
			System.out.println("Uncompressed length: "+sizeOfPointsFile);
			ps.println("Encoding length: "+sizeOfCompressedFile);
			System.out.println("Encoding length: "+sizeOfCompressedFile);
			ps.println("Compression ratio: "+(1.0 * sizeOfPointsFile)/sizeOfCompressedFile);
			System.out.println("Compression ratio: "+(1.0 * sizeOfPointsFile)/sizeOfCompressedFile);
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}