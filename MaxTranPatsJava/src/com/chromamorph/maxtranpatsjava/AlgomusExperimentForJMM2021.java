package com.chromamorph.maxtranpatsjava;

import java.io.File;
import java.util.ArrayList;

public class AlgomusExperimentForJMM2021 {
	
	static private ArrayList<String> getInputFileNames() {
		String shostakovichDirName = "data/algomus/Shostakovich/";
		String bachDirName = "data/algomus/Bach/";
		
		ArrayList<String> inputFileNames = new ArrayList<String>();
		File shostakovichDir = new File(shostakovichDirName);
		File bachDir = new File(bachDirName);
		
		for(String fn : shostakovichDir.list()) {
			if (fn.endsWith(".mid"))
				inputFileNames.add(shostakovichDirName + fn);
		}
		
		for (String fn : bachDir.list()) {
			if (fn.endsWith(".opnd"))
				inputFileNames.add(bachDirName + fn);
		}
		
		return inputFileNames;
	}
	
	static private void analysePieces() {
		ArrayList<String> inputFileNames = getInputFileNames();
		TransformationClass[] transformationClasses = new TransformationClass[] {new F_2STR()};
		for (String fileName : inputFileNames)
			PointSet.run(fileName, transformationClasses);
	}
	
	public static void main(String[] args) {
//		Generate analyses of all pieces
		analysePieces();
//		Compare generated analyses with ground truth report results
	}
}
