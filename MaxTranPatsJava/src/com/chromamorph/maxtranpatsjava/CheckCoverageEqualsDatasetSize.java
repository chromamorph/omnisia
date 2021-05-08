package com.chromamorph.maxtranpatsjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

public class CheckCoverageEqualsDatasetSize {
	
	public static int getValue(String line) {
		String[] a = line.trim().split(" ");
		return Integer.parseInt(a[a.length-1]);
	}
	
	public static void main(String[] args) {
//		String rootDirStr = "output/nlb-20210504/pair-files-F2T";// OK
//		String rootDirStr = "output/nlb-20210504/single-files"; // Some STR files
//		String rootDirStr = "output/nlb-20210504/pair-files-F2TR"; // OK
//		String rootDirStr = "output/nlb-20210504/pair-files-F2STR"; // Two STR files.
		String rootDirStr = "output/nlb-20210504/single-files-with-quantization"; // Some STR files

		File rootDir = new File(rootDirStr);
		System.out.println("rootDir is " + rootDir);
		String[] outputFileDirs = rootDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (new File(dir, name).isDirectory() && !name.startsWith("FAILED"));
			}
			
		});
		System.out.println("outputFileDirs has "+outputFileDirs.length+" elements");
		
		int numFilesChecked = 0;
		
		for(String outputFileDirStr : outputFileDirs) {
			File outputFileDir = new File(rootDir, outputFileDirStr);
			String[] outputFileNames = outputFileDir.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".enc");
				}
				
			});
			
			File outputFile = new File(outputFileDir, outputFileNames[0]);
//			System.out.println(outputFile.getAbsolutePath());
			
			try {
				BufferedReader br;
				br = new BufferedReader(new FileReader(outputFile));
				String l;
				int coverage = 0, numPoints = 0;
				while((l = br.readLine()) != null) {
					if (l.startsWith("Coverage"))
						coverage = getValue(l);
					if (l.startsWith("Number of points"))
						numPoints = getValue(l);
				}
				if (coverage != numPoints)
					System.out.println(outputFileDirStr+": Coverage = "+coverage+", Number of points = "+numPoints);
				br.close();
				numFilesChecked++;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}
		System.out.println(numFilesChecked + " files checked");
	}
}
