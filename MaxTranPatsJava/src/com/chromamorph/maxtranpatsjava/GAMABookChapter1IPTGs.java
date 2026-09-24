package com.chromamorph.maxtranpatsjava;

import java.io.IOException;
import java.util.Calendar;

public class GAMABookChapter1IPTGs {
	public static void main(String[] args) {
		String outputDirPath, groundTruthFilePath;
		// We assume it is being run in eclipse or another IDE from the source file in the OMNISIA repository and using the GT file in the data subfolder of the MaxTranPatsJava project
		groundTruthFilePath = "data/gama/chapter1/RAVEL-HAYDN-GROUND-TRUTH.gt";
		outputDirPath = "output/GAMABookChapter1IPTGs/"+Calendar.getInstance().getTime().toString().replace(' ', '-').replace(':', '-');
		String[] argStrings = new String[] {
				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Book -o "+outputDirPath,			 						// CPO
				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Book -o "+outputDirPath+" -mt -xsf 2",					// CPM
				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Book -o "+outputDirPath+" -d",	 						// MPO
				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Book -o "+outputDirPath+" -d -mt -xsf 2",					// MPM
				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Mod12_Book -o "+outputDirPath+" -c",	 					// CO
				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Mod12_Book -o "+outputDirPath+" -c -mt -xsf 2",			// CM
				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Mod7_Book -o "+outputDirPath+" -d -m", 					// MO
				"-gt "+groundTruthFilePath+" -iptg -tc F_2STR_Mod7_Book -o "+outputDirPath+" -d -m -mt -xsf 2"			// MM
		};
		
		System.out.println("Output dir: "+outputDirPath);
		System.out.println("Ground-truth file: "+groundTruthFilePath);
		for(String argString : argStrings) {
			String[] argArray = argString.split("\s");
			MaxTranPats.main(argArray);
		}
	}

}
