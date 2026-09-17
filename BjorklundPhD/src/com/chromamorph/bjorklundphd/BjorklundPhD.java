package com.chromamorph.bjorklundphd;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;
import com.chromamorph.points022.PointSet;

public class BjorklundPhD {
	
	public static String[] ALGORITHMS = new String[] {
			"SIATEC-C"
	};
	
	public static TreeSet<String> ALGORITHMS_TREE_SET = new TreeSet<String>();
	
	public static String listAlgorithms() {
		StringBuilder sb = new StringBuilder("{"+ALGORITHMS[0]);
		for(int i = 1; i < ALGORITHMS.length; i++)
			sb.append(","+ALGORITHMS[i]);
		sb.append("}");
		return sb.toString();
	}
	
	public static String INPUT_FILE_PATH 	= null;
	public static String OUTPUT_DIR_PATH 	= null;
	public static int DELTA 				= 0;
	public static boolean DIATONIC_PITCH	= false;
	public static boolean DRAW				= false;
	public static String ALGORITHM			= "SIATEC-C";
	
	public static String INPUT_FILE_PATH_SWITCH = "i";
	public static String OUTPUT_DIR_PATH_SWITCH = "o";
	public static String DELTA_SWITCH			= "delta";
	public static String DIATONIC_PITCH_SWITCH 	= "d";
	public static String DRAW_SWITCH			= "draw";
	public static String ALGORITHM_SWITCH		= "a";
	
	public static String getParameterSettings() {
		StringBuilder sb = new StringBuilder("\n\nParameter settings:\n===================\n");
		sb.append("Input file path (-"+INPUT_FILE_PATH_SWITCH+"): "+INPUT_FILE_PATH+"\n");
		sb.append("Output directory (-"+OUTPUT_DIR_PATH_SWITCH+"): "+OUTPUT_DIR_PATH+"\n");
		sb.append(String.format("%s (-%s): %d\n", "Delta", DELTA_SWITCH, DELTA));
		sb.append(String.format("%s (-%s): %s\n", "Morphetic pitch", DIATONIC_PITCH_SWITCH, DIATONIC_PITCH));
		sb.append(String.format("%s (-%s): %s\n", "Draw patterns", DRAW_SWITCH, DRAW));
		sb.append(String.format("%s (-%s): %s\n", "Algorithm", ALGORITHM_SWITCH, ALGORITHM));
		return sb.toString();
	}

	public static String getStringValue(ArrayList<String> argList, String sw) {
		String str = null;
		int i = argList.lastIndexOf("-"+sw);
		if (i >= 0)
			str = argList.get(i+1);
		return str;
	}
	
	public static boolean getBooleanValue(ArrayList<String> argList, String sw) {
		int i = argList.lastIndexOf("-"+sw);
		return (i >= 0);
	}
	
	public static Integer getIntValue(ArrayList<String> argList, String sw, int defaultValue) {
		Integer val = defaultValue;
		int i = argList.lastIndexOf("-"+sw);
		if (i >= 0)
			val = Integer.parseInt(argList.get(i+1));
		return val;
	}

	private static void println(String... s) {
		for(String str : s) {
			System.out.println(str);
		}
	}
	
	public static void showHelp() {
		println(
				"Björklund PhD Help",
				"==================",
				"-"+INPUT_FILE_PATH_SWITCH+"\tPath to input file (required).",
				"-"+OUTPUT_DIR_PATH_SWITCH+"\tDirectory in which to place output files. Default is same as input file directory.",
				"-"+DELTA_SWITCH+"\tValue of delta parameter in SIATEC-C",
				"-"+DIATONIC_PITCH_SWITCH+"\tUse morphetic pitch, not chromatic pitch.",
				"-"+DRAW_SWITCH+"\tDraw results in a graph.",
				"-"+ALGORITHM_SWITCH+"\tAlgorithm to run. Must be in the set, "+listAlgorithms()
		);
	}

	public static void main(String[] args) {
		ArrayList<String> argArray = new ArrayList<String>();
		for(String arg: args)
			argArray.add(arg);

		OUTPUT_DIR_PATH = getStringValue(argArray, OUTPUT_DIR_PATH_SWITCH);
		DIATONIC_PITCH = getBooleanValue(argArray, DIATONIC_PITCH_SWITCH);
		DRAW = getBooleanValue(argArray,DRAW_SWITCH);
		DELTA = getIntValue(argArray, DELTA_SWITCH, 0);
		INPUT_FILE_PATH = getStringValue(argArray, INPUT_FILE_PATH_SWITCH);
		ALGORITHM = getStringValue(argArray, ALGORITHM_SWITCH);
		
		if (INPUT_FILE_PATH != null) {
			File f = new File(INPUT_FILE_PATH);
			if (!f.exists()) {
				System.out.println("ERROR: INPUT FILE PATH DOES NOT EXIST!");
				return;
			}
		}
				
		if (OUTPUT_DIR_PATH == null) {
			int end = INPUT_FILE_PATH.lastIndexOf("/");
			OUTPUT_DIR_PATH = INPUT_FILE_PATH.substring(0, end);
		}
		
		for(String s:ALGORITHMS)
			ALGORITHMS_TREE_SET.add(s);
		
		if (ALGORITHM == null) {
			ALGORITHM = "SIATEC-C";
		} else if (!ALGORITHMS_TREE_SET.contains(ALGORITHM)) {
			System.out.println("ERROR: UNRECOGNIZED ALGORITHM!");
			return;
		}
					
		if (ALGORITHM.equals("SIATEC-C")) {
			try {
				PointSet D = new PointSet(INPUT_FILE_PATH,DIATONIC_PITCH);
				SIATEC_C(D,delta);
			} catch (MissingTieStartNoteException e) {
				e.printStackTrace();
				return;
			}
		}
		
	}
}
