package com.chromamorph.maxtranpatsjava;

import java.io.IOException;
import java.util.Calendar;

import com.chromamorph.points022.DrawPoints;

public class GAMABookChapter1RavelMenuetCO {
	
	public static void main(String[] args) {
		String outputDirPath, 
			groundTruthFilePath, 
			inputFilePath, 
			// Following do not change ground truth - they only change the way some of the points are drawn
			// in order to improve readability
			groundTruthFilePathForCO,
			groundTruthFilePathForMM,
			groundTruthFilePathForMO,
			groundTruthFilePathForCM;
		// We assume it is being run in eclipse from the source file in the OMNISIA repository and using the GT 
		// and OPND files in the data subfolder of the MaxTranPatsJava project
		inputFilePath = "data/gama/chapter1/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN.OPND";
		groundTruthFilePath = "data/gama/chapter1/RAVEL-HAYDN-GROUND-TRUTH.gt";
		groundTruthFilePathForCO = "data/gama/chapter1/RAVEL-HAYDN-GROUND-TRUTH-FOR-CO.gt";
		groundTruthFilePathForMO = "data/gama/chapter1/RAVEL-HAYDN-GROUND-TRUTH-FOR-MO.gt";
		groundTruthFilePathForMM = "data/gama/chapter1/RAVEL-HAYDN-GROUND-TRUTH-FOR-MM.gt";
		groundTruthFilePathForCM = "data/gama/chapter1/RAVEL-HAYDN-GROUND-TRUTH-FOR-CM.gt";
		outputDirPath = "output/GAMABookChapter1/pitchtimereps/"+Calendar.getInstance().getTime().toString().replace(' ', '-').replace(':', '-');
		String[] argStrings = new String[] {
//				"-i "+inputFilePath+" -gt "+groundTruthFilePath+" -drawgt -o "+outputDirPath,						// CPO
//				"-i "+inputFilePath+" -gt "+groundTruthFilePath+" -drawgt -mt -xsf 2 -o "+outputDirPath,			// CPM
//				"-i "+inputFilePath+" -gt "+groundTruthFilePath+" -drawgt -d -o "+outputDirPath,					// MPO
//				"-i "+inputFilePath+" -gt "+groundTruthFilePath+" -drawgt -d -mt -xsf 2 -o "+outputDirPath,			// MPM
				"-i "+inputFilePath+" -gt "+groundTruthFilePathForCO+" -drawgt -c -o "+outputDirPath,				// CO
//				"-i "+inputFilePath+" -gt "+groundTruthFilePathForCM+" -drawgt -c -mt -xsf 2 -o "+outputDirPath,	// CM
//				"-i "+inputFilePath+" -gt "+groundTruthFilePathForMO+" -drawgt -m -o "+outputDirPath,				// MO
//				"-i "+inputFilePath+" -gt "+groundTruthFilePathForMM+" -drawgt -m -mt -xsf 2 -o "+outputDirPath,	// MM
				
//				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Book -o "+outputDirPath,			 						// CPO IPTG
//				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Book -o "+outputDirPath+" -mt -xsf 2",					// CPM IPTG
//				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Book -o "+outputDirPath+" -d",	 						// MPO IPTG
//				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Book -o "+outputDirPath+" -d -mt -xsf 2",					// MPM IPTG
//				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Mod12_Book -o "+outputDirPath+" -c",	 					// CO IPTG
//				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Mod12_Book -o "+outputDirPath+" -c -mt -xsf 2",			// CM IPTG
//				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Mod7_Book -o "+outputDirPath+" -d -m", 					// MO IPTG
//				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Mod7_Book -o "+outputDirPath+" -d -m -mt -xsf 2"			// MM IPTG
		};
		
		System.out.println("Output dir: "+outputDirPath);
		System.out.println("Ground-truth file: "+groundTruthFilePath);
		System.out.println("Input file: "+inputFilePath);
		for(String argString : argStrings) {
			String[] argArray = argString.split("\s");
			MaxTranPats.main(argArray);
		}
	}

}
