package com.chromamorph.points022;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class RecurSIARRTExperimentRun {

	static String[] pieceLabels =      	{"chop_mazurka24-4",    "gbns_silverswan",          "beet_sonata01-3",          "mzrt_sonata04-2",      "bach_wtc2f20"};
	static String[] groundTruthDirs = 	{"chopinOp24No4",       "gibbonsSilverSwan1612",    "beethovenOp2No1Mvt3",      "mozartK282Mvt2",       "bachBWV889Fg"};
	static String[] lispFileNames =   	{"mazurka24-4.txt",     "silverswan.txt",           "sonata01-3.txt",           "sonata04-2.txt",       "wtc2f20.txt"};
	static String[][] algorithms =      {
			/*
			 * Algorithms tested in
			 * Meredith, D. (2015). Music analysis and point-set compression. Journal of New Music Research, 44(3):245-270.
			 * https://doi.org/10.1080/09298215.2015.1045003 
			 */
//			{"SIATECCompressSegment",  "-a SIATECCompress  -segmode"},
//			{"SIARTECCompressSegment", "-a SIATECCompress  -segmode                            -rsd -r 3"},
//			{"COSIARTECSegment",       "-a COSIATEC        -segmode                            -rsd -r 3"},
//			{"COSIATECSegment",        "-a COSIATEC        -segmode"},
//			{"ForthCTSegment",         "-a Forth           -segmode    -ct -cta 0.66 -ctb 3"},
//			{"ForthRCTSegment",        "-a Forth           -segmode    -ct -cta 0.66 -ctb 3    -rsd -r 3"},
//			{"ForthCT",                "-a Forth                       -ct -cta 0.66 -ctb 3"},
//			{"ForthRCT",               "-a Forth                       -ct -cta 0.66 -ctb 3    -rsd -r 3"},
//			{"COSIACTTECSegment",      "-a COSIATEC        -segmode    -ct -cta 0.66 -ctb 3"},
//			{"COSIARCTTECSegment",     "-a COSIATEC        -segmode    -ct -cta 0.66 -ctb 3    -rsd -r 3"},
//			{"Forth",                  "-a Forth"},
//			{"COSIACTTEC",             "-a COSIATEC                    -ct -cta 0.66 -ctb 3"},
//			{"COSIARCTTEC",            "-a COSIATEC                    -ct -cta 0.66 -ctb 3    -rsd -r 3"},
//			{"SIATECCompress",         "-a SIATECCompress"},
//			{"COSIATEC",               "-a COSIATEC"},
			{"COSIATEC-MIN-4",         "-a COSIATEC 														-min 4"}, //Fails on Gibbons
	};

	public static void runJKUPDDExperiment() {
		for(String[] algorithm : algorithms)
			for (int i = 0; i < pieceLabels.length; i++) {
				if (i > 3) {
					String cmd = "java -jar OMNISIA.jar -m -d -draw " + algorithm[1]; 
					cmd += " -out RecurSIA-RRT-experiment/JKU-PDD/JKUPDD-noAudio-Aug2013/matlab/pattDiscOut/" + algorithm[0] + "/" + pieceLabels[i] + "_" + algorithm[0] + ".txt";
					cmd += " -i RecurSIA-RRT-experiment/JKU-PDD/JKUPDD-noAudio-Aug2013/groundTruth/" + groundTruthDirs[i] + "/polyphonic/lisp/" + lispFileNames[i];
					try {
						String[] cmdArray = cmd.split("\\s+");
//						for(String s : cmdArray) {
//							System.out.println(s + " ");
//						}
//						System.out.println();
						OMNISIA.main(cmdArray);
						System.gc();
					} catch (MissingTieStartNoteException e) {
						e.printStackTrace();
					}
				}
			}
	}

	public static void main(String[] args) {
		//runJKUPDDExperiment();
		EvaluateJKUPDDOutputForRecurSIARRTPaper.main(new String[0]);
		//		runFuguesExperiment();
		//		runNLBExperiment();
	}

}
