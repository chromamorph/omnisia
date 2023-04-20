package com.chromamorph.notes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConvertNotesToOPDVLispFormat {
	public static void main(String[] args) throws IOException {
		Notes notes = new Notes(args[0]);
		Path inputFilePath = Paths.get(args[0]);
		String outputFileParentFolder = inputFilePath.getParent().toString();
		String outputFileName = inputFilePath.getFileName().toString();
		int end = outputFileName.indexOf('.');
		String outputFileNameWithoutSuffix = outputFileName.substring(0,end);
		String outputFilePathString = outputFileParentFolder+"/"+outputFileNameWithoutSuffix+".opd";
		notes.toOPDVFile(outputFilePathString,false,true,false);
		outputFilePathString = outputFileParentFolder+"/"+outputFileNameWithoutSuffix+".opnd";
		notes.toOPNDFile(outputFilePathString, false, true, false);
		outputFilePathString = outputFileParentFolder+"/"+outputFileNameWithoutSuffix+".opcd";
		notes.toOPCDFile(outputFilePathString, false, true, false);
	}
}
