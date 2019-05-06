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
			//			{"COSIATEC-MIN-4",         "-a COSIATEC 														-min 4"}, //Fails on Gibbons

//			////////////////////////////////////////////////////////////////////////////////////
			{"COSIATEC",				"-a COSIATEC"},
			{"COSIATECSegment",			"-a COSIATEC		-segmode"},
//			{"COSIATECBB",				"-a COSIATEC		-bbmode"},
//
//			{"COSIACTTEC",				"-a COSIATEC					-ct -cta 0.67 -ctb 3"},
//			{"COSIACTTECSegment",		"-a COSIATEC		-segmode	-ct -cta 0.67 -ctb 3"},
//			{"COSIACTTECBB",			"-a COSIATEC		-bbmode		-ct -cta 0.67 -ctb 3"},
//
//			{"COSIARTEC",				"-a COSIATEC											-rsd -r 3"},
//			{"COSIARTECSegment",		"-a COSIATEC		-segmode							-rsd -r 3"},
//			{"COSIARTECBB",				"-a COSIATEC		-bbmode								-rsd -r 3"},
//
//			{"COSIARCTTEC",				"-a COSIATEC					-ct -cta 0.67 -ctb 3	-rsd -r 3"},
//			{"COSIARCTTECSegment",		"-a COSIATEC		-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3"},
//			{"COSIARCTTECBB",			"-a COSIATEC		-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3"},
//
//			//			with RecurSIA
//
//			{"ReCOSIATEC",				"-recalg COSIATEC													-a RecurSIA"},
//			{"ReCOSIATECSegment",		"-recalg COSIATEC	-segmode										-a RecurSIA"},
//			{"ReCOSIATECBB",			"-recalg COSIATEC	-bbmode											-a RecurSIA"},
//
//			{"ReCOSIACTTEC",			"-recalg COSIATEC				-ct -cta 0.67 -ctb 3				-a RecurSIA"},
//			{"ReCOSIACTTECSegment",		"-recalg COSIATEC	-segmode	-ct -cta 0.67 -ctb 3				-a RecurSIA"},
//			{"ReCOSIACTTECBB",			"-recalg COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3				-a RecurSIA"},
//
//			{"ReCOSIARTEC",				"-recalg COSIATEC										-rsd -r 3	-a RecurSIA"},
//			{"ReCOSIARTECSegment",		"-recalg COSIATEC	-segmode							-rsd -r 3	-a RecurSIA"},
//			{"ReCOSIARTECBB",			"-recalg COSIATEC	-bbmode								-rsd -r 3	-a RecurSIA"},
//
//			{"ReCOSIARCTTEC",			"-recalg COSIATEC				-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
//			{"ReCOSIARCTTECSegment",	"-recalg COSIATEC	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
//			{"ReCOSIARCTTECBB",			"-recalg COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
//
//			//			with -rrt
//
//			{"COSIATEC-RRT",				"-a COSIATEC																	-rrt"},
//			{"COSIATECSegment-RRT",			"-a COSIATEC	-segmode														-rrt"},
//			{"COSIATECBB-RRT",				"-a COSIATEC	-bbmode															-rrt"},
//
//			{"COSIACTTEC-RRT",				"-a COSIATEC				-ct -cta 0.67 -ctb 3								-rrt"},
//			{"COSIACTTECSegment-RRT",		"-a COSIATEC	-segmode	-ct -cta 0.67 -ctb 3								-rrt"},
//			{"COSIACTTECBB-RRT",			"-a COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3								-rrt"},
//
//			{"COSIARTEC-RRT",				"-a COSIATEC										-rsd -r 3					-rrt"},
//			{"COSIARTECSegment-RRT",		"-a COSIATEC	-segmode							-rsd -r 3					-rrt"},
//			{"COSIARTECBB-RRT",				"-a COSIATEC	-bbmode								-rsd -r 3					-rrt"},
//
//			{"COSIARCTTEC-RRT",				"-a COSIATEC				-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
//			{"COSIARCTTECSegment-RRT",		"-a COSIATEC	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
//			{"COSIARCTTECBB-RRT",			"-a COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
//
//			//			with RecurSIA
//
//			{"ReCOSIATEC-RRT",			"-recalg COSIATEC													-a RecurSIA		-rrt"},
//			{"ReCOSIATECSegment-RRT",	"-recalg COSIATEC	-segmode										-a RecurSIA		-rrt"},
//			{"ReCOSIATECBB-RRT",		"-recalg COSIATEC	-bbmode											-a RecurSIA		-rrt"},
//
//			{"ReCOSIACTTEC-RRT",		"-recalg COSIATEC				-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
//			{"ReCOSIACTTECSegment-RRT",	"-recalg COSIATEC	-segmode	-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
//			{"ReCOSIACTTECBB-RRT",		"-recalg COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
//
//			{"ReCOSIARTEC-RRT",			"-recalg COSIATEC										-rsd -r 3	-a RecurSIA		-rrt"},
//			{"ReCOSIARTECSegment-RRT",	"-recalg COSIATEC	-segmode							-rsd -r 3	-a RecurSIA		-rrt"},
//			{"ReCOSIARTECBB-RRT",		"-recalg COSIATEC	-bbmode								-rsd -r 3	-a RecurSIA		-rrt"},
//
//			{"ReCOSIARCTTEC-RRT",		"-recalg COSIATEC				-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
//			{"ReCOSIARCTTECSegment-RRT","-recalg COSIATEC	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
//			{"ReCOSIARCTTECBB-RRT",		"-recalg COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},


	};

	public static void runJKUPDDExperiment() {
		for(String[] algorithm : algorithms)
			for (int i = 0; i < pieceLabels.length; i++) {
				if (i == 1 || i == 4) {
					String cmd = "-m -d -draw " + algorithm[1]; 
					cmd += " -out RecurSIA-RRT-experiment/JKU-PDD/JKUPDD-noAudio-Aug2013/matlab/pattDiscOut/" + algorithm[0] + "/" + pieceLabels[i] + "_" + algorithm[0] + ".out";
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
		runJKUPDDExperiment();
		//RecurSIARRTEvaluateJKUPDDOutput.main(new String[0]);
		//		runFuguesExperiment();
		//		runNLBExperiment();
	}

}
