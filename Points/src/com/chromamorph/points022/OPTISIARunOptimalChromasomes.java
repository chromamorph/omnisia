package com.chromamorph.points022;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class OPTISIARunOptimalChromasomes {
	
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

			//////////////////////////////////////////////////////////////////////////////////////
			// COSIATEC
			
			{"COSIATEC",				"-a COSIATEC"},
			{"COSIATECSegment",			"-a COSIATEC		-segmode"},
			{"COSIATECBB",				"-a COSIATEC		-bbmode"},

			{"COSIACTTEC",				"-a COSIATEC					-ct -cta 0.67 -ctb 3"},
			{"COSIACTTECSegment",		"-a COSIATEC		-segmode	-ct -cta 0.67 -ctb 3"},
			{"COSIACTTECBB",			"-a COSIATEC		-bbmode		-ct -cta 0.67 -ctb 3"},

			{"COSIARTEC",				"-a COSIATEC											-rsd -r 3"},
			{"COSIARTECSegment",		"-a COSIATEC		-segmode							-rsd -r 3"},
			{"COSIARTECBB",				"-a COSIATEC		-bbmode								-rsd -r 3"},

			{"COSIARCTTEC",				"-a COSIATEC					-ct -cta 0.67 -ctb 3	-rsd -r 3"},
			{"COSIARCTTECSegment",		"-a COSIATEC		-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3"},
			{"COSIARCTTECBB",			"-a COSIATEC		-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3"},

			//			with RecurSIA

			{"ReCOSIATEC",				"-recalg COSIATEC													-a RecurSIA"},
			{"ReCOSIATECSegment",		"-recalg COSIATEC	-segmode										-a RecurSIA"},
			{"ReCOSIATECBB",			"-recalg COSIATEC	-bbmode											-a RecurSIA"},

			{"ReCOSIACTTEC",			"-recalg COSIATEC				-ct -cta 0.67 -ctb 3				-a RecurSIA"},
			{"ReCOSIACTTECSegment",		"-recalg COSIATEC	-segmode	-ct -cta 0.67 -ctb 3				-a RecurSIA"},
			{"ReCOSIACTTECBB",			"-recalg COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3				-a RecurSIA"},

			{"ReCOSIARTEC",				"-recalg COSIATEC										-rsd -r 3	-a RecurSIA"},
			{"ReCOSIARTECSegment",		"-recalg COSIATEC	-segmode							-rsd -r 3	-a RecurSIA"},
			{"ReCOSIARTECBB",			"-recalg COSIATEC	-bbmode								-rsd -r 3	-a RecurSIA"},

			{"ReCOSIARCTTEC",			"-recalg COSIATEC				-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
			{"ReCOSIARCTTECSegment",	"-recalg COSIATEC	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
			{"ReCOSIARCTTECBB",			"-recalg COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},

			//			with -rrt

			{"COSIATEC-RRT",				"-a COSIATEC																	-rrt"},
			{"COSIATECSegment-RRT",			"-a COSIATEC	-segmode														-rrt"},
			{"COSIATECBB-RRT",				"-a COSIATEC	-bbmode															-rrt"},

			{"COSIACTTEC-RRT",				"-a COSIATEC				-ct -cta 0.67 -ctb 3								-rrt"},
			{"COSIACTTECSegment-RRT",		"-a COSIATEC	-segmode	-ct -cta 0.67 -ctb 3								-rrt"},
			{"COSIACTTECBB-RRT",			"-a COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3								-rrt"},

			{"COSIARTEC-RRT",				"-a COSIATEC										-rsd -r 3					-rrt"},
			{"COSIARTECSegment-RRT",		"-a COSIATEC	-segmode							-rsd -r 3					-rrt"},
			{"COSIARTECBB-RRT",				"-a COSIATEC	-bbmode								-rsd -r 3					-rrt"},

			{"COSIARCTTEC-RRT",				"-a COSIATEC				-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
			{"COSIARCTTECSegment-RRT",		"-a COSIATEC	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
			{"COSIARCTTECBB-RRT",			"-a COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},

			//			with RecurSIA and RRT

			{"ReCOSIATEC-RRT",			"-recalg COSIATEC													-a RecurSIA		-rrt"},
			{"ReCOSIATECSegment-RRT",	"-recalg COSIATEC	-segmode										-a RecurSIA		-rrt"},
			{"ReCOSIATECBB-RRT",		"-recalg COSIATEC	-bbmode											-a RecurSIA		-rrt"},

			{"ReCOSIACTTEC-RRT",		"-recalg COSIATEC				-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
			{"ReCOSIACTTECSegment-RRT",	"-recalg COSIATEC	-segmode	-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
			{"ReCOSIACTTECBB-RRT",		"-recalg COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},

			{"ReCOSIARTEC-RRT",			"-recalg COSIATEC										-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReCOSIARTECSegment-RRT",	"-recalg COSIATEC	-segmode							-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReCOSIARTECBB-RRT",		"-recalg COSIATEC	-bbmode								-rsd -r 3	-a RecurSIA		-rrt"},

			{"ReCOSIARCTTEC-RRT",		"-recalg COSIATEC				-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReCOSIARCTTECSegment-RRT","-recalg COSIATEC	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReCOSIARCTTECBB-RRT",		"-recalg COSIATEC	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//Forth's algorithm
			
			{"Forth",				"-a Forth"},
			{"ForthSegment",		"-a Forth		-segmode"},
			{"ForthBB",				"-a Forth		-bbmode"},

			{"ForthCT",				"-a Forth					-ct -cta 0.67 -ctb 3"},
			{"ForthCTSegment",		"-a Forth		-segmode	-ct -cta 0.67 -ctb 3"},
			{"ForthCTBB",			"-a Forth		-bbmode		-ct -cta 0.67 -ctb 3"},

			{"ForthR",				"-a Forth											-rsd -r 3"},
			{"ForthRSegment",		"-a Forth		-segmode							-rsd -r 3"},
			{"ForthRBB",			"-a Forth		-bbmode								-rsd -r 3"},

			{"ForthRCT",			"-a Forth					-ct -cta 0.67 -ctb 3	-rsd -r 3"},
			{"ForthRCTSegment",		"-a Forth		-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3"},
			{"ForthRCTBB",			"-a Forth		-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3"},

			//			with RecurSIA

			{"ReForth",				"-recalg Forth													-a RecurSIA"},
			{"ReForthSegment",		"-recalg Forth	-segmode										-a RecurSIA"},
			{"ReForthBB",			"-recalg Forth	-bbmode											-a RecurSIA"},

			{"ReForthCT",			"-recalg Forth				-ct -cta 0.67 -ctb 3				-a RecurSIA"},
			{"ReForthCTSegment",	"-recalg Forth	-segmode	-ct -cta 0.67 -ctb 3				-a RecurSIA"},
			{"ReForthCTBB",			"-recalg Forth	-bbmode		-ct -cta 0.67 -ctb 3				-a RecurSIA"},

			{"ReForthR",			"-recalg Forth										-rsd -r 3	-a RecurSIA"},
			{"ReForthRSegment",		"-recalg Forth	-segmode							-rsd -r 3	-a RecurSIA"},
			{"ReForthRBB",			"-recalg Forth	-bbmode								-rsd -r 3	-a RecurSIA"},

			{"ReForthRCT",			"-recalg Forth				-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
			{"ReForthRCTSegment",	"-recalg Forth	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
			{"ReForthRCTBB",		"-recalg Forth	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},

			//			with -rrt

			{"Forth-RRT",			"-a Forth																	-rrt"},
			{"ForthSegment-RRT",	"-a Forth	-segmode														-rrt"},
			{"ForthBB-RRT",			"-a Forth	-bbmode															-rrt"},

			{"ForthCT-RRT",			"-a Forth				-ct -cta 0.67 -ctb 3								-rrt"},
			{"ForthCTSegment-RRT",	"-a Forth	-segmode	-ct -cta 0.67 -ctb 3								-rrt"},
			{"ForthCTBB-RRT",		"-a Forth	-bbmode		-ct -cta 0.67 -ctb 3								-rrt"},

			{"ForthR-RRT",			"-a Forth										-rsd -r 3					-rrt"},
			{"ForthRSegment-RRT",	"-a Forth	-segmode							-rsd -r 3					-rrt"},
			{"ForthRBB-RRT",		"-a Forth	-bbmode								-rsd -r 3					-rrt"},

			{"ForthRCT-RRT",		"-a Forth				-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
			{"ForthRCTSegment-RRT",	"-a Forth	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
			{"ForthRCTBB-RRT",		"-a Forth	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},

			//			with RecurSIA and RRT

			{"ReForth-RRT",			"-recalg Forth													-a RecurSIA		-rrt"},
			{"ReForthSegment-RRT",	"-recalg Forth	-segmode										-a RecurSIA		-rrt"},
			{"ReForthBB-RRT",		"-recalg Forth	-bbmode											-a RecurSIA		-rrt"},

			{"ReForthCT-RRT",		"-recalg Forth				-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
			{"ReForthCTSegment-RRT","-recalg Forth	-segmode	-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
			{"ReForthCTBB-RRT",		"-recalg Forth	-bbmode		-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},

			{"ReForthR-RRT",		"-recalg Forth										-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReForthRSegment-RRT",	"-recalg Forth	-segmode							-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReForthRBB-RRT",		"-recalg Forth	-bbmode								-rsd -r 3	-a RecurSIA		-rrt"},

			{"ReForthRCT-RRT",		"-recalg Forth				-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReForthRCTSegment-RRT","-recalg Forth	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReForthRCTBB-RRT",	"-recalg Forth	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//SIATECCompress
			
			{"SIATECCompress",					"-a SIATECCompress"},
			{"SIATECCompressSegment",			"-a SIATECCompress		-segmode"},
			{"SIATECCompressBB",				"-a SIATECCompress		-bbmode"},

			{"SIACTTECCompress",				"-a SIATECCompress					-ct -cta 0.67 -ctb 3"},
			{"SIACTTECCompressSegment",			"-a SIATECCompress		-segmode	-ct -cta 0.67 -ctb 3"},
			{"SIACTTECCompressBB",				"-a SIATECCompress		-bbmode		-ct -cta 0.67 -ctb 3"},

			{"SIARTECCompress",					"-a SIATECCompress											-rsd -r 3"},
			{"SIARTECCompressSegment",			"-a SIATECCompress		-segmode							-rsd -r 3"},
			{"SIARTECCompressBB",				"-a SIATECCompress		-bbmode								-rsd -r 3"},

			{"SIARCTTECCompress",				"-a SIATECCompress					-ct -cta 0.67 -ctb 3	-rsd -r 3"},
			{"SIARCTTECCompressSegment",		"-a SIATECCompress		-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3"},
			{"SIARCTTECCompressBB",				"-a SIATECCompress		-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3"},

			//			with RecurSIA

			{"ReSIATECCompress",				"-recalg SIATECCompress													-a RecurSIA"},
			{"ReSIATECCompressSegment",			"-recalg SIATECCompress	-segmode										-a RecurSIA"},
			{"ReSIATECCompressBB",				"-recalg SIATECCompress	-bbmode											-a RecurSIA"},

			{"ReSIACTTECCompress",				"-recalg SIATECCompress				-ct -cta 0.67 -ctb 3				-a RecurSIA"},
			{"ReSIACTTECCompressSegment",		"-recalg SIATECCompress	-segmode	-ct -cta 0.67 -ctb 3				-a RecurSIA"},
			{"ReSIACTTECCompressBB",			"-recalg SIATECCompress	-bbmode		-ct -cta 0.67 -ctb 3				-a RecurSIA"},

			{"ReSIARTECCompress",				"-recalg SIATECCompress										-rsd -r 3	-a RecurSIA"},
			{"ReSIARTECCompressSegment",		"-recalg SIATECCompress	-segmode							-rsd -r 3	-a RecurSIA"},
			{"ReSIARTECCompressBB",				"-recalg SIATECCompress	-bbmode								-rsd -r 3	-a RecurSIA"},

			{"ReSIARCTTECCompress",				"-recalg SIATECCompress				-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
			{"ReSIARCTTECCompressSegment",		"-recalg SIATECCompress	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},
			{"ReSIARCTTECCompressBB",			"-recalg SIATECCompress	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA"},

			//			with -rrt

			{"SIATECCompress-RRT",				"-a SIATECCompress																		-rrt"},
			{"SIATECCompressSegment-RRT",		"-a SIATECCompress		-segmode														-rrt"},
			{"SIATECCompressBB-RRT",			"-a SIATECCompress		-bbmode															-rrt"},

			{"SIACTTECCompress-RRT",			"-a SIATECCompress					-ct -cta 0.67 -ctb 3								-rrt"},
			{"SIACTTECCompressSegment-RRT",		"-a SIATECCompress		-segmode	-ct -cta 0.67 -ctb 3								-rrt"},
			{"SIACTTECCompressBB-RRT",			"-a SIATECCompress		-bbmode		-ct -cta 0.67 -ctb 3								-rrt"},

			{"SIARTECCompress-RRT",				"-a SIATECCompress											-rsd -r 3					-rrt"},
			{"SIARTECCompressSegment-RRT",		"-a SIATECCompress		-segmode							-rsd -r 3					-rrt"},
			{"SIARTECCompressBB-RRT",			"-a SIATECCompress		-bbmode								-rsd -r 3					-rrt"},

			{"SIARCTTECCompress-RRT",			"-a SIATECCompress					-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
			{"SIARCTTECCompressSegment-RRT",	"-a SIATECCompress		-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},
			{"SIARCTTECCompressBB-RRT",			"-a SIATECCompress		-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3					-rrt"},

			//			with RecurSIA and RRT

			{"ReSIATECCompress-RRT",			"-recalg SIATECCompress													-a RecurSIA		-rrt"},
			{"ReSIATECCompressSegment-RRT",		"-recalg SIATECCompress	-segmode										-a RecurSIA		-rrt"},
			{"ReSIATECCompressBB-RRT",			"-recalg SIATECCompress	-bbmode											-a RecurSIA		-rrt"},

			{"ReSIACTTECCompress-RRT",			"-recalg SIATECCompress				-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
			{"ReSIACTTECCompressSegment-RRT",	"-recalg SIATECCompress	-segmode	-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},
			{"ReSIACTTECCompressBB-RRT",		"-recalg SIATECCompress	-bbmode		-ct -cta 0.67 -ctb 3				-a RecurSIA		-rrt"},

			{"ReSIARTECCompress-RRT",			"-recalg SIATECCompress										-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReSIARTECCompressSegment-RRT",	"-recalg SIATECCompress	-segmode							-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReSIARTECCompressBB-RRT",			"-recalg SIATECCompress	-bbmode								-rsd -r 3	-a RecurSIA		-rrt"},

			{"ReSIARCTTECCompress-RRT",			"-recalg SIATECCompress				-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReSIARCTTECCompressSegment-RRT",	"-recalg SIATECCompress	-segmode	-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
			{"ReSIARCTTECCompressBB-RRT",		"-recalg SIATECCompress	-bbmode		-ct -cta 0.67 -ctb 3	-rsd -r 3	-a RecurSIA		-rrt"},
			
			{"COSIATEC",		""},

			
	};

	
	public static void runJKUPDDExperiment() {
		for(String[] algorithm : algorithms)
			for (int i = 0; i < pieceLabels.length; i++) {
				String cmd = "-m -d -draw " + algorithm[1]; 
				cmd += " -out RecurSIA-RRT-experiment/JKU-PDD/JKUPDD-noAudio-Aug2013/matlab/pattDiscOut/" + algorithm[0] + "/" + pieceLabels[i] + "_" + algorithm[0] + ".out";
				cmd += " -i RecurSIA-RRT-experiment/JKU-PDD/JKUPDD-noAudio-Aug2013/groundTruth/" + groundTruthDirs[i] + "/polyphonic/lisp/" + lispFileNames[i];
				try {
					String[] cmdArray = cmd.split("\\s+");
					OMNISIA.main(cmdArray);
					System.gc();
				} catch (MissingTieStartNoteException e) {
					e.printStackTrace();
				}
			}
	}

	public static void main(String[] args) {
		runJKUPDDExperiment();
//		RecurSIARRTEvaluateJKUPDDOutput.run();
		//		runFuguesExperiment();
		//		runNLBExperiment();
	}
}
