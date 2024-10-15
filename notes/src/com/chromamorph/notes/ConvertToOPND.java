package com.chromamorph.notes;

public class ConvertToOPND {
	public static void main(String[] args) {
		Notes notes = new Notes(args[0]);
		int endIndex = args[0].lastIndexOf(".");
		String outputFilePath = args[0].substring(0, endIndex)+".OPND";
		notes.toOPNDFile(outputFilePath,true,true,false);
	}

}
