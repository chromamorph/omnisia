package com.chromamorph.notes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeSet;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class KernToOPNDV {

	static TreeSet<Path> getKernFilePaths(Path inputFileDirPath) {
		TreeSet<Path> kernFilePaths = new TreeSet<Path>();
		String[] fileList = inputFileDirPath.toFile().list();
		for(String fileName : fileList) {
			Path filePath = inputFileDirPath.resolve(fileName);
			if (filePath.toFile().isDirectory())
				kernFilePaths.addAll(getKernFilePaths(filePath));
			else if (fileName.endsWith(".krn"))
				kernFilePaths.add(filePath);
		}
		return kernFilePaths;
	}

	static void convert(Path inputFilePath, Path outputDirPath) throws IOException, MissingTieStartNoteException {
		String kernFilePathString = inputFilePath.toString();
		Notes notes = Notes.fromKern(kernFilePathString);
		
//		Find opndFilePathString
//		If input file is /A/B/C/D/E/in.krn and outputDirPath is /A/B/F/, then
//		opndFilePathString is /A/B/F/D/E/in.opndv
		String opndFileName = inputFilePath.getFileName().toString().substring(0, inputFilePath.getFileName().toString().indexOf(".")) + ".opndv";
		Path opndFilePath = outputDirPath.resolve(opndFileName);
		String opndFilePathString = opndFilePath.toString();
		notes.toOPNDFile(opndFilePathString);
		System.out.println(inputFilePath.toString()+" --> "+opndFilePathString);
	}
	
	public static void main(String[] args) throws IOException, MissingTieStartNoteException {
		//		If args[0] is a directory, then convert all kern files under this directory to opndv files
		//		and store in the output directory given as args[1].

		if (args.length == 0) {
			System.out.println("Syntax: java -jar KernToOPNDV <input file or directory> [<output directory>]");
			return;
		}
		
		File inputFile = new File(args[0]);
		Path inputFilePath = inputFile.toPath();
		File outputDir = (args.length==2?new File(args[1]):inputFilePath.getParent().toFile());
		Path outputDirPath = outputDir.toPath();

		//		Check that args[0] exists
		if (!(inputFile.exists())) {
			System.out.println("Input file does not exist.");
			return;
		}
		//		Input file exists
		if (!(inputFile.isDirectory())) {
			//			Input file is a single file to be converted
			//			Check that output directory exists or can be made
			if (outputDir.exists() || outputDir.mkdirs())
				convert(inputFilePath, outputDirPath);
		} else {
			//			Input file is a directory - convert all kern files under this directory
			//			and store in the output directory.
			TreeSet<Path> kernFilePaths = getKernFilePaths(inputFilePath);

			for(Path kernFilePath : kernFilePaths) 
				convert(kernFilePath,outputDirPath);
		}



	}
}
