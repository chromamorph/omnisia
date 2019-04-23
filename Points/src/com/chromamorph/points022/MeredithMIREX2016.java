package com.chromamorph.points022;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class MeredithMIREX2016 {
	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println("Syntax: java -Xmx32G -jar MeredithMIREX2016.jar <input file path> <output file path>");
			return;
		}

		String[] argsArray = {
//				private static String ALGORITHM_SWITCH 			= "a";
				"-a","Forth",
//				private static String INPUT_FILE_SWITCH 		= "i";
				"-i", args[0],
//				private static String OUTPUT_DIR_SWITCH 		= "o";
//				private static String DIATONIC_PITCH_SWITCH 	= "d";
				"-d",
//				private static String HELP_SWITCH 				= "h";
//				private static String MIREX_SWITCH				= "m";
				"-m",
//				private static String COMPACTNESS_TRAWLER_SWITCH= "ct";
				"-ct",
//				private static String CTA_SWITCH				= "cta";
				"-cta", "0.67",
//				private static String CTB_SWITCH				= "ctb";
				"-ctb", "3",
//				private static String R_SUPERDIAGONALS_SWITCH	= "rsd";
				"-rsd",
//				private static String R_SWITCH					= "r";
				"3",
//				private static String RRT_SWITCH				= "rrt";
				"-rrt",
//				private static String MIN_TEC_COMPACTNESS_SWITCH= "minc";
				"-minc", "0.8",
//				private static String MIN_PATTERN_SIZE_SWITCH	= "min";
				"-min", "4",
//				private static String MAX_PATTERN_SIZE_SWITCH	= "max";
//				private static String MERGE_TECS_SWITCH			= "merge";
//				private static String MIN_MATCH_SIZE_SWITCH		= "minm";
//				private static String NUM_ITERATIONS_SWITCH		= "spins";
//				private static String WITHOUT_CHANNEL_TEN_SWITCH= "no10";
//				private static String DRAW_SWITCH				= "draw";
//				private static String CR_LOW_SWITCH				= "crlow";
//				private static String CR_HIGH_SWITCH			= "crhi";
//				private static String COMP_V_LOW_SWITCH			= "comlow";
//				private static String COMP_V_HIGH_SWITCH		= "comhi";
//				private static String C_MIN_SWITCH				= "cmin";
//				private static String SIGMA_MIN_SWITCH			= "sigmin";
//				private static String BB_COMPACTNESS_SWITCH		= "bbcomp";
//				private static String NO_DATE_SWITCH			= "nodate";
//				private static String BB_MODE_SWITCH			= "bbmode";
//				private static String SEGMENT_MODE_SWITCH		= "segmode";
				"-segmode",
//				private static String OUTPUT_FILE_SWITCH		= "out";
				"-out", args[1]
				
		};
		
		try {
			OMNISIA.main(argsArray);
		} catch (MissingTieStartNoteException e) {
			e.printStackTrace();
		}

	}
}
