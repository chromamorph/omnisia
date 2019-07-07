package com.chromamorph.points022;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.IOUtils;

public class RecurSIARRTCheckRecurSIAEffect {
	
	public static void findFilesForWhichRecurSIAChangesMIREXOutput() {
		ArrayList<String> nonRecurSIAPathNames = new ArrayList<String>();
		ArrayList<String> recurSIAPathNames = new ArrayList<String>();
		String outputRootDirectory = "RecurSIA-RRT-experiment/JKU-PDD/JKUPDD-noAudio-Aug2013/matlab/pattDiscOut";
		String[] algorithmDirs = new File(outputRootDirectory).list();
		ArrayList<String> recurSIAAlgorithmDirs = new ArrayList<String>();
		ArrayList<String> nonRecurSIAAlgorithmDirs = new ArrayList<String>();
		for (String algoDir : algorithmDirs) {
			if (algoDir.startsWith("Re")) recurSIAAlgorithmDirs.add(algoDir);
			else if (!algoDir.startsWith(".")) nonRecurSIAAlgorithmDirs.add(algoDir);
		}
		Collections.sort(recurSIAAlgorithmDirs);
		Collections.sort(nonRecurSIAAlgorithmDirs);
		FilenameFilter mirexOutputFileFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
			
		};
		for(String algDir : recurSIAAlgorithmDirs) {
			String[] mirexOutputFiles = new File(outputRootDirectory+"/"+algDir).list(mirexOutputFileFilter);
			for(String mirexOutputFile : mirexOutputFiles)
				recurSIAPathNames.add(outputRootDirectory+"/"+algDir+"/"+mirexOutputFile);
		}
		
		for(String algDir : nonRecurSIAAlgorithmDirs) {
			String[] mirexOutputFiles = new File(outputRootDirectory+"/"+algDir).list(mirexOutputFileFilter);
			for(String mirexOutputFile : mirexOutputFiles)
				nonRecurSIAPathNames.add(outputRootDirectory+"/"+algDir+"/"+mirexOutputFile);
		}

		for(int i = 0; i < nonRecurSIAPathNames.size();i++) {
			try {
				InputStream recFile = new FileInputStream(recurSIAPathNames.get(i));
				InputStream nonRecFile = new FileInputStream(nonRecurSIAPathNames.get(i));
				if (!IOUtils.contentEquals(recFile, nonRecFile))
					System.out.println(recurSIAPathNames.get(i)+"\t\t"+nonRecurSIAPathNames.get(i));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

	
	public static void main(String[] args) {
		findFilesForWhichRecurSIAChangesMIREXOutput();
	}
}
