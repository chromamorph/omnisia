package com.chromamorph.points022;

import java.io.File;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class MeredithTLRMIREX2016 {
	public static void main(String[] args) throws MissingTieStartNoteException {
		File parent = null;
		if (args.length < 2)
			System.out.println("Syntax: java -jar MeredithTLRMIREX2016 <input file path> <output file path>");
		else if (!((new File(args[0])).exists()))
			System.out.println("Input file does not exist!");
		else if ((parent = new File(args[1]).getParentFile()) != null && !parent.exists() && !parent.mkdirs())
			System.out.println("Cannot create output file!");
		else {
			String[] omnisiaArgs = {
					"-a", "SIATECCompress",
					"-i", args[0],
					"-out", args[1],
					"-d",
					"-m",
					"-minc", "0.25",
					"-min", "4",
					"-segmode"
			};
			OMNISIA.main(omnisiaArgs);
		}
	}
}
