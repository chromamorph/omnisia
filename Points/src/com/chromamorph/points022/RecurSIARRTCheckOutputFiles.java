package com.chromamorph.points022;

import java.io.File;

public class RecurSIARRTCheckOutputFiles {
	
	static String outputRootDirectoryStr = "D:\\Source\\Repos\\omnisia\\Points\\RecurSIA-RRT-experiment\\JKU-PDD\\JKUPDD-noAudio-Aug2013\\matlab\\pattDiscOut";
	
	public static void main(String[] args) {
		File outputRootDirectory = new File(outputRootDirectoryStr);
		File[] algorithmDirs = outputRootDirectory.listFiles();
		System.out.println("There are "+algorithmDirs.length+" subdirectories");
		for(File algorithmDir : algorithmDirs) {
			if ((algorithmDir.getName().startsWith("Re") && algorithmDir.list().length == 20) || 
					(!algorithmDir.getName().startsWith("Re") && algorithmDir.list().length == 25)) {
				System.out.println(algorithmDir.getName()+":"+algorithmDir.list().length+" OK");
			} else
				System.out.println(algorithmDir.getName()+":"+algorithmDir.list().length+" ERROR!");
		}
	}
}
