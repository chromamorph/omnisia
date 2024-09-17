package com.chromamorph.maxtranpatsjava;

import java.io.IOException;
import java.util.ArrayList;

public class MaxTranPats {

	public static String INPUT_FILE_PATH 					= null;
	public static String QUERY_FILE_PATH 					= null;
	public static String GROUND_TRUTH_FILE_PATH 			= null;
	public static boolean MID_TIME_POINT					= false;
	public static String OUTPUT_DIR_PATH					= null;
	public static int MIN_PATTERN_SIZE						= 0;
	public static double MIN_COMPACTNESS					= 0.0;
	public static double MIN_OCC_COMPACTNESS				= 0.0;
	public static boolean DIATONIC_PITCH					= false;
	public static TransformationClass[] TRANSFORMATION_CLASSES = null;
	public static String DIMENSION_MASK					= null;
	public static boolean SCALEXIA							= false;
	public static boolean DRAW								= false;
	public static boolean HELP								= false;
	public static boolean DRAW_GROUND_TRUTH					= false;
	public static boolean DRAW_BOUNDING_BOXES				= false;
	
	public static String INPUT_FILE_PATH_SWITCH 			= "i";
	public static String QUERY_FILE_PATH_SWITCH 			= "q";
	public static String GROUND_TRUTH_FILE_PATH_SWITCH 		= "gt";
	public static String MID_TIME_POINT_SWITCH 				= "mt";
	public static String OUTPUT_DIR_PATH_SWITCH 			= "o";
	public static String MIN_PATTERN_SIZE_SWITCH			= "min";
	public static String MIN_COMPACTNESS_SWITCH				= "minc";
	public static String MIN_OCC_COMPACTNESS_SWITCH			= "minoc";
	public static String DIATONIC_PITCH_SWITCH				= "d";
	public static String TRANSFORMATION_CLASSES_SWITCH		= "tc";
	public static String DIMENSION_MASK_SWITCH				= "dm";
	public static String SCALEXIA_SWITCH					= "scal";
	public static String DRAW_SWITCH						= "draw";
	public static String HELP_SWITCH						= "h";
	public static String DRAW_GROUND_TRUTH_SWITCH			= "drawgt";
	public static String DRAW_BOUNDING_BOXES_SWITCH			= "drawbb";
	
	public static String[] ALL_TRANS_CLASS_STRINGS = new String[] {
			"F_2STR_FIXED",
			"F_2STR_Rational",
			"F_2STR",
			"F_2T",
			"F_2TR"};
	public static TransformationClass[] ALL_TRANS_CLASSES = new TransformationClass[] {
			new F_2STR_FIXED(),
			new F_2STR_Rational(),
			new F_2STR(),
			new F_2T(),
			new F_2TR()
	};
	
	public static String getTransformationClasses() {
		StringBuilder sb = new StringBuilder("{");
		sb.append(TRANSFORMATION_CLASSES[0]);
		for(int i = 1; i < TRANSFORMATION_CLASSES.length; i++) {
			sb.append(","+TRANSFORMATION_CLASSES[i]);
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static String getParameterSettings() {
		StringBuilder sb = new StringBuilder("\n\nParameter settings:\n===================\n");
		sb.append("Input file path (-"+INPUT_FILE_PATH_SWITCH+"): "+INPUT_FILE_PATH+"\n");
		sb.append("Query file path (-"+QUERY_FILE_PATH_SWITCH+"): "+QUERY_FILE_PATH+"\n");
		sb.append("Ground-truth file path (-"+GROUND_TRUTH_FILE_PATH_SWITCH+"): "+GROUND_TRUTH_FILE_PATH+"\n");
		sb.append("Mid-time point (-"+MID_TIME_POINT_SWITCH+"): "+MID_TIME_POINT+"\n");
		sb.append("Output directory (-"+OUTPUT_DIR_PATH_SWITCH+"): "+OUTPUT_DIR_PATH+"\n");
		sb.append(String.format("%s (-%s): %s\n", "Minimum pattern size", MIN_PATTERN_SIZE_SWITCH, MIN_PATTERN_SIZE));
		sb.append(String.format("%s (-%s): %s\n", "Minimum occurrence set compactness", MIN_COMPACTNESS_SWITCH, MIN_COMPACTNESS));
		sb.append(String.format("%s (-%s): %s\n", "Minimum occurrence compactness", MIN_OCC_COMPACTNESS_SWITCH, MIN_OCC_COMPACTNESS));
		sb.append(String.format("%s (-%s): %s\n", "Morphetic pitch", DIATONIC_PITCH_SWITCH, DIATONIC_PITCH));
		sb.append(String.format("%s (-%s): %s\n", "Transformation classes", TRANSFORMATION_CLASSES_SWITCH, getTransformationClasses()));
		sb.append(String.format("%s (-%s): %s\n", "Dimension mask", DIMENSION_MASK_SWITCH, DIMENSION_MASK));
		sb.append(String.format("%s (-%s): %s\n", "Use ScaleXIA", SCALEXIA_SWITCH, SCALEXIA));
		sb.append(String.format("%s (-%s): %s\n", "Draw patterns", DRAW_SWITCH, DRAW));
		sb.append(String.format("%s (-%s): %s\n", "Draw ground truth", DRAW_GROUND_TRUTH_SWITCH, DRAW_GROUND_TRUTH));
		sb.append(String.format("%s (-%s): %s\n", "Draw bounding boxes around patterns", DRAW_BOUNDING_BOXES_SWITCH, DRAW_BOUNDING_BOXES));
		
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
	
	public static Double getDoubleValue(ArrayList<String> argList, String sw, double defaultValue) {
		Double val = defaultValue;
		int i = argList.lastIndexOf("-"+sw);
		if (i >= 0)
			val = Double.parseDouble(argList.get(i+1));
		return val;
	}

	
	public static TransformationClass[] getTransformationClasses(ArrayList<String> argList) {
		ArrayList<TransformationClass> transformationClassList = new ArrayList<TransformationClass>();
		for(int j = 0; j < ALL_TRANS_CLASS_STRINGS.length; j++) {
			int i = argList.lastIndexOf(ALL_TRANS_CLASS_STRINGS[j]);
			if (i >= 0)	{			
				transformationClassList.add(ALL_TRANS_CLASSES[j]);
			}
		}
		TransformationClass[] transformationClasses = new TransformationClass[transformationClassList.size()];
		transformationClassList.toArray(transformationClasses);
		return transformationClasses;
	}
	
	private static void println(String... s) {
		for(String str : s) {
			System.out.println(str);
		}
	}
	
	
	private static String getListOfAllTransformationClasses() {
		StringBuilder sb = new StringBuilder("{"+ALL_TRANS_CLASS_STRINGS[0]);
		for(int i = 1; i < ALL_TRANS_CLASS_STRINGS.length; i++) {
			sb.append(", "+ALL_TRANS_CLASS_STRINGS[i]);
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static void showHelp() {
		println(
				"MaxTranPats Help",
				"================",
				"-"+INPUT_FILE_PATH_SWITCH+"\tPath to input file (required).",
				"-"+OUTPUT_DIR_PATH_SWITCH+"\tDirectory in which to place output files. Default is same as input file directory.",
				"-"+GROUND_TRUTH_FILE_PATH_SWITCH+"\tFile containing ground truth to be compared with output.",
				"-"+MIN_PATTERN_SIZE_SWITCH+"\tMinimum pattern size (default is 0). -n sets minimum to size of query - n points.",
				"-"+MIN_COMPACTNESS_SWITCH+"\tMinimum compactness of occurrence sets (default is 0.0).",
				"-"+MIN_OCC_COMPACTNESS_SWITCH+"\tMinimum compactness of individual occurrences (default is 0.0).",
				"-"+QUERY_FILE_PATH_SWITCH+"\tPath to file containing query.",
				"-"+TRANSFORMATION_CLASSES_SWITCH+"\tFollowed by list of transformation classes to include. Must be in the following list:",
				"\t"+getListOfAllTransformationClasses(),
				"-"+DIATONIC_PITCH_SWITCH+"\tUse morphetic pitch, not chromatic pitch.",
				"-"+MID_TIME_POINT_SWITCH+"\tUse mid-time points, not onset times (e.g., when looking for retrogrades).",
				"-"+DIMENSION_MASK_SWITCH+"\tA binary string in which a 1 indicates that a dimenion is to be used.",
				"-"+SCALEXIA_SWITCH+"\tUse Scalexia rather than a transformation class.",
				"-"+DRAW_SWITCH+"\tDraw results in a graph.",
				"-"+HELP_SWITCH+"\tDisplay this help.",
				"-"+DRAW_GROUND_TRUTH_SWITCH+"\tDraw ground truth file (.gt file).",
				"-"+DRAW_BOUNDING_BOXES_SWITCH+"\tDraw bounding boxes around patterns."
		);
	}
	
	public static void main(String[] args) {
		ArrayList<String> argArray = new ArrayList<String>();
		for(String arg: args)
			argArray.add(arg);

		INPUT_FILE_PATH = getStringValue(argArray, INPUT_FILE_PATH_SWITCH);
		if (INPUT_FILE_PATH == null || HELP) {
			showHelp();
			return;
		} 
		OUTPUT_DIR_PATH = getStringValue(argArray, OUTPUT_DIR_PATH_SWITCH);
		GROUND_TRUTH_FILE_PATH = getStringValue(argArray, GROUND_TRUTH_FILE_PATH_SWITCH);
		MIN_PATTERN_SIZE = getIntValue(argArray, MIN_PATTERN_SIZE_SWITCH,0);
		MIN_COMPACTNESS = getDoubleValue(argArray, MIN_COMPACTNESS_SWITCH, 0.0);
		MIN_OCC_COMPACTNESS = getDoubleValue(argArray, MIN_OCC_COMPACTNESS_SWITCH, 0.0);
		QUERY_FILE_PATH = getStringValue(argArray, QUERY_FILE_PATH_SWITCH);		
		TRANSFORMATION_CLASSES = getTransformationClasses(argArray);
		DIATONIC_PITCH = getBooleanValue(argArray, DIATONIC_PITCH_SWITCH);
		MID_TIME_POINT = getBooleanValue(argArray,MID_TIME_POINT_SWITCH);
		DIMENSION_MASK = getStringValue(argArray,DIMENSION_MASK_SWITCH);
		SCALEXIA = getBooleanValue(argArray,SCALEXIA_SWITCH);
		DRAW = getBooleanValue(argArray,DRAW_SWITCH);
		HELP = getBooleanValue(argArray,HELP_SWITCH);
		DRAW_GROUND_TRUTH = getBooleanValue(argArray,DRAW_GROUND_TRUTH_SWITCH);
		DRAW_BOUNDING_BOXES = getBooleanValue(argArray,DRAW_BOUNDING_BOXES_SWITCH);

		if (OUTPUT_DIR_PATH == null) {
			int end = INPUT_FILE_PATH.lastIndexOf("/");
			OUTPUT_DIR_PATH = INPUT_FILE_PATH.substring(0, end);
		}
		
		if (DIMENSION_MASK == null)
			DIMENSION_MASK = "1100";
		
		if (DRAW_GROUND_TRUTH)
			try {
				PointSet.drawGroundTruthFile(
						GROUND_TRUTH_FILE_PATH, 
						INPUT_FILE_PATH, 
						DIATONIC_PITCH, 
						MID_TIME_POINT,
						DIMENSION_MASK,
						DRAW_BOUNDING_BOXES);
			} catch (IOException | DimensionalityException e) {
				e.printStackTrace();
			}
		else if (QUERY_FILE_PATH == null) {
			PointSet.encodePointSetFromFile(
					INPUT_FILE_PATH, 
					TRANSFORMATION_CLASSES,
					DIATONIC_PITCH, //pitchSpell
					MID_TIME_POINT, //midTimePoint
					DIMENSION_MASK, //dimensionMask
					OUTPUT_DIR_PATH, //outputDir
					SCALEXIA, //useScalexia
					MIN_PATTERN_SIZE, //minSize
					DRAW, //draw
					MIN_COMPACTNESS,
					MIN_OCC_COMPACTNESS,
					GROUND_TRUTH_FILE_PATH,
					DRAW_BOUNDING_BOXES
					);
		} else {
			PointSet.maximalTransformedMatchesFromFiles(
					QUERY_FILE_PATH,
					INPUT_FILE_PATH,
					TRANSFORMATION_CLASSES,
					DIATONIC_PITCH, //pitchSpell
					MID_TIME_POINT, //midTimePoint
					DIMENSION_MASK, //dimensionMask
					OUTPUT_DIR_PATH, //outputDir
					MIN_PATTERN_SIZE, //minSize
					DRAW, //draw
					MIN_COMPACTNESS,
					MIN_OCC_COMPACTNESS,
					GROUND_TRUTH_FILE_PATH,
					DRAW_BOUNDING_BOXES
					);
		}
	}

	
}
