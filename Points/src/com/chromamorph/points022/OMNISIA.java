package com.chromamorph.points022;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class OMNISIA {

	private static PrintWriter LOG = null;
	private static File LOG_FILE = null;
	private static String[] ENCODING_FILE_EXTENSIONS = {".cos", ".siateccompress", ".sia", ".siatec", ".alltecs"};
	
	public static ArrayList<String> OUTPUT_FILE_PATH_STRINGS = new ArrayList<String>();


	////////////////////
	//	Inner classes

	static class DavesIntFormatException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6688143546204831763L;

		public DavesIntFormatException() {
			super();
		}

		public DavesIntFormatException(String cmdSwitch, int currentValue, NumberFormatException e) {
			super("Integer parsing format exception on switch, "+cmdSwitch+". Value of this parameter will be "+currentValue+"\nOriginal message: "+e.getMessage());
		}

	}

	static class DavesDoubleFormatException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1634393033796916557L;

		public DavesDoubleFormatException() {
			super();
		}

		public DavesDoubleFormatException(String cmdSwitch, double currentValue, NumberFormatException e) {
			super("Double parsing format exception on switch, "+cmdSwitch+". Value of this parameter will be "+currentValue+"\nOriginal message: "+e.getMessage());
		}

	}

	private static String COMMAND_LINE = "";

	////////////////////
	//	Parameter values

	private static BasicAlgorithm BASIC_ALGORITHM 	= BasicAlgorithm.COSIATEC;
	private static File INPUT_FILE 					= null;
	private static File INPUT_DIR					= null;
	private static File[] INPUT_FILE_LIST			= null;
	private static File OUTPUT_DIR 					= null;
	private static boolean DIATONIC_PITCH 			= false;
	private static boolean HELP 					= false;
	private static boolean MIREX					= false;
	private static boolean COMPACTNESS_TRAWLER		= false;
	private static double CTA						= 0.67;
	private static int CTB							= 3;
	private static boolean R_SUPERDIAGONALS			= false;
	private static int R							= 1;
	private static boolean RRT						= false;
	private static double MIN_TEC_COMPACTNESS		= 0.0;
	private static int MIN_PATTERN_SIZE				= 0;
	private static int MAX_PATTERN_SIZE				= 0; //Allows patterns of all sizes.
	private static boolean MERGE_TECS				= false;
	private static int MIN_MATCH_SIZE				= 5;
	private static int NUM_ITERATIONS				= 10;
	private static boolean WITHOUT_CHANNEL_10		= false;
	private static boolean DRAW						= false;
	private static double CR_LOW					= 0.2;
	private static double CR_HIGH					= 1.0;
	private static double COMP_V_LOW				= 0.2;
	private static double COMP_V_HIGH				= 1.0;
	private static int C_MIN						= 15;
	private static double SIGMA_MIN					= 0.5;
	private static boolean BB_COMPACTNESS			= false; //Uses BB compactness in Forth's algorithm 
	private static boolean NO_DATE					= false; //If true, then does not append date to output file directories
	private static boolean BB_MODE					= false; //Use BB mode in MIREX output
	private static boolean SEGMENT_MODE				= false; //Use Segment mode in MIREX output
	private static File OUTPUT_FILE					= null;  //To be used to send output encoding file only to given place.
	private static int TOP_N_PATTERNS				= 0; //Limits output to top n patterns (if 0, then all patterns returned)
	private static Algorithm RECURSIA_ALGORITHM		= Algorithm.COSIATEC;
	private static boolean SORT_BY_PATTERN_SIZE		= false;
	private static boolean GPU_ACCEL				= false;
	private static boolean DRAW_POINT_SET			= false;
	public static boolean RHYTHM_ONLY				= false;
	public static String TEC_PRIORITY_STRING		= TECQualityComparator.DEFAULT_PRIORITY_STRING;
	public static String DUAL_TEC_PRIORITY_STRING	= TECQualityComparator.DEFAULT_PRIORITY_STRING;
	public static boolean NUM_MTPS_ONLY				= false;
	public static CompactnessType COMPACTNESS_TYPE	= CompactnessType.BOUNDING_BOX;
	private static File OCCURRENCE_SETS_FILE		= null;

	////////////////////
	//	Switches

	private static String ALGORITHM_SWITCH 			= "a";
	private static String INPUT_FILE_SWITCH 		= "i";
	private static String INPUT_DIR_SWITCH			= "id";
	private static String OUTPUT_DIR_SWITCH 		= "o";
	private static String DIATONIC_PITCH_SWITCH 	= "d";
	private static String HELP_SWITCH 				= "h";
	private static String MIREX_SWITCH				= "m";
	private static String COMPACTNESS_TRAWLER_SWITCH= "ct";
	private static String CTA_SWITCH				= "cta";
	private static String CTB_SWITCH				= "ctb";
	private static String R_SUPERDIAGONALS_SWITCH	= "rsd";
	private static String R_SWITCH					= "r";
	private static String RRT_SWITCH				= "rrt";
	private static String MIN_TEC_COMPACTNESS_SWITCH= "minc";
	private static String MIN_PATTERN_SIZE_SWITCH	= "min";
	private static String MAX_PATTERN_SIZE_SWITCH	= "max";
	private static String MERGE_TECS_SWITCH			= "merge";
	private static String MIN_MATCH_SIZE_SWITCH		= "minm";
	private static String NUM_ITERATIONS_SWITCH		= "spins";
	private static String WITHOUT_CHANNEL_TEN_SWITCH= "no10";
	private static String DRAW_SWITCH				= "draw";
	private static String CR_LOW_SWITCH				= "crlow";
	private static String CR_HIGH_SWITCH			= "crhi";
	private static String COMP_V_LOW_SWITCH			= "comlow";
	private static String COMP_V_HIGH_SWITCH		= "comhi";
	private static String C_MIN_SWITCH				= "cmin";
	private static String SIGMA_MIN_SWITCH			= "sigmin";
	private static String BB_COMPACTNESS_SWITCH		= "bbcomp";
	private static String NO_DATE_SWITCH			= "nodate";
	private static String BB_MODE_SWITCH			= "bbmode";
	private static String SEGMENT_MODE_SWITCH		= "segmode";
	private static String OUTPUT_FILE_SWITCH		= "out";
	private static String TOP_N_PATTERNS_SWITCH		= "top";
	private static String RECURSIA_ALGORITHM_SWITCH	= "recalg";
	private static String SORT_BY_PATTERN_SIZE_SWITCH = "sortpat";
	private static String GPU_ACCEL_SWITCH			= "gpu";
	private static String DRAW_POINT_SET_SWITCH		= "drawps";
	private static String RHYTHM_ONLY_SWITCH		= "rhythm";
	private static String TEC_PRIORITY_SWITCH		= "tecqual";
	private static String DUAL_TEC_PRIORITY_SWITCH	= "dualtecqual";
	private static String NUM_MTPS_ONLY_SWITCH		= "nummtpsonly";
	private static String COMPACTNESS_TYPE_SWITCH	= "comptype";
	private static String OCCURRENCE_SETS_FILE_SWITCH= "occsets";


	////////////////////
	//	Static methods for parsing program arguments and setting parameter values

	private static void getBasicAlgorithm(String[] args) {
		if (inputFileIsEncodingFile())
			BASIC_ALGORITHM = BasicAlgorithm.NONE;
		String algStr = getValue(ALGORITHM_SWITCH, args);
		if (algStr != null)
			BASIC_ALGORITHM = BasicAlgorithm.valueOf(algStr);
	}

	private static void getCompactnessType(String[] args) {
		String compType = getValue(COMPACTNESS_TYPE_SWITCH, args);
		if (compType != null && compType.toLowerCase().equals("segment"))
			COMPACTNESS_TYPE = CompactnessType.SEGMENT;
	}

	private static void getTECPriority(String[] args) {
		String tecqual = getValue(TEC_PRIORITY_SWITCH, args);
		if (tecqual != null)
			TEC_PRIORITY_STRING = tecqual.trim().toLowerCase();
	}

	private static void getDualTECPriority(String[] args) {
		String dualtecqual = getValue(DUAL_TEC_PRIORITY_SWITCH, args);
		if (dualtecqual != null)
			DUAL_TEC_PRIORITY_STRING = dualtecqual.trim().toLowerCase();
		else
			DUAL_TEC_PRIORITY_STRING = TEC_PRIORITY_STRING;
	}


	private static void getOccurrenceSetFile(String[] args) {
		String filePathStr = getValue(OCCURRENCE_SETS_FILE_SWITCH, args);
		if (filePathStr != null && new File(filePathStr).exists()) {
			OCCURRENCE_SETS_FILE = new File(new File(filePathStr).getAbsolutePath());
		}
	}
	
	private static void getInputFile(String[] args) {
		String filePathStr = getValue(INPUT_FILE_SWITCH, args);
		if (filePathStr != null && new File(filePathStr).exists()) {
			INPUT_FILE = new File(new File(filePathStr).getAbsolutePath());
			//			println(INPUT_FILE.toString());
			OUTPUT_DIR = INPUT_FILE.getParentFile();
			//			println(OUTPUT_DIR.toString());
		} else if (filePathStr==null)
			println("ERROR: No input file provided. Use -"+HELP_SWITCH+" to get help.");
		else
			println("ERROR: Input file does not exist. Use -\"+HELP_SWITCH+\" to get help.");
	}

	private static void getInputDir(String[] args) {
		String inputDirStr = getValue(INPUT_DIR_SWITCH, args);
		if (inputDirStr == null && INPUT_FILE == null) {
			println("ERROR: You need to provide either an input file or an input directory. Use the -"+HELP_SWITCH+" to get help.");
			return;
		}
		if (inputDirStr == null) return;
		File inputDir = new File(inputDirStr).getAbsoluteFile();
		if (inputDir.exists() && inputDir.isDirectory()) {
			File[] inputFileList = inputDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					for(String ext : PointSet.INPUT_FILE_EXTENSIONS) {
						if (name.endsWith(ext))
							return true;
					}
					return false;
				}
			});
			if (inputFileList.length > 0) {
				INPUT_DIR = inputDir;
				INPUT_FILE_LIST = inputFileList;
			}
		} else
			println("ERROR: Input file directory (-id) either does not exist, is not a directory, or does not contain any input files in a format that can be accepted by the program.");
	}

private static boolean inputFileIsEncodingFile() {
	String ifs = INPUT_FILE.getName();
	int start = ifs.lastIndexOf('.');
	String extension = ifs.substring(start).toLowerCase();
	for(String ext : ENCODING_FILE_EXTENSIONS)
		if (ext.equals(extension))
			return true;
	return false;
}

private static void getOutputDir(String[] args) {
	String outputDirStr = getValue(OUTPUT_DIR_SWITCH, args);
	File outputDir;
	if (outputDirStr == null) {
		outputDir = INPUT_FILE.getParentFile();
	} else if (new File(outputDirStr).exists())
		outputDir = new File(new File(outputDirStr).getAbsolutePath());
	else {
		boolean dirCreated = new File(outputDirStr).mkdirs();
		if (dirCreated)
			outputDir = new File(new File(outputDirStr).getAbsolutePath());
		else {
			println("ERROR: Provided output directory does not exist. Using parent directory of input file instead.");
			outputDir = INPUT_FILE.getParentFile();
		}
	}

	//		Make a subdirectory in the given output directory to hold the output files for this
	//		run

	Path outputDirPath = outputDir.toPath();
	String subdirString = INPUT_FILE.getName().replace('.', '-');
	if (!NO_DATE) {	
		Calendar cal = Calendar.getInstance();
		String timeString = cal.get(Calendar.YEAR)+"-"+String.format("%02d", 1+cal.get(Calendar.MONTH))+"-"+String.format("%02d", cal.get(Calendar.DATE))+"-"+String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))+"-"+String.format("%02d", cal.get(Calendar.MINUTE))+"-"+String.format("%02d", cal.get(Calendar.SECOND))+"-"+String.format("%03d", cal.get(Calendar.MILLISECOND));
		subdirString = subdirString+"-"+timeString;
	}
	Path fullOutputDirPath = outputDirPath.resolve(subdirString);
	OUTPUT_DIR = fullOutputDirPath.toFile();
}

private static void getRhythmOnly(String[] args) {
	RHYTHM_ONLY = getBooleanValue(RHYTHM_ONLY_SWITCH, args);
}

private static void getNumMtpsOnly(String[] args) {
	NUM_MTPS_ONLY = getBooleanValue(NUM_MTPS_ONLY_SWITCH, args);
}

private static void getDiatonicPitch(String[] args) {
	if (inputFileIsEncodingFile())
		DIATONIC_PITCH = INPUT_FILE.toString().toLowerCase().contains("diat");
	else
		DIATONIC_PITCH = getBooleanValue(DIATONIC_PITCH_SWITCH, args);
}

private static void getSortByPatternSize(String[] args) {
	SORT_BY_PATTERN_SIZE = getBooleanValue(SORT_BY_PATTERN_SIZE_SWITCH, args);
}

private static void getGPUAccel(String[] args) {
	GPU_ACCEL = getBooleanValue(GPU_ACCEL_SWITCH, args);
}

private static void getHelp(String[] args) {
	HELP = getBooleanValue(HELP_SWITCH, args);
}

private static void getDrawPointSet(String[] args) {
	DRAW_POINT_SET = getBooleanValue(DRAW_POINT_SET_SWITCH, args);
}

private static void getMIREX(String[] args) {
	MIREX = getBooleanValue(MIREX_SWITCH, args);
}

private static void getCompactnessTrawler(String[] args) {
	COMPACTNESS_TRAWLER = getBooleanValue(COMPACTNESS_TRAWLER_SWITCH, args);
}

private static void getCTA(String[] args) throws DavesDoubleFormatException {
	String ctaStr = getValue(CTA_SWITCH,args);
	if (ctaStr!=null) {
		try {
			double c = Double.parseDouble(ctaStr);
			if (c <= 1 && c >= 0)
				CTA = c;
			else
				throw new NumberFormatException("Value of -cta switch is out of bounds. Default value, "+CTA+", will be used.");
		} catch (NumberFormatException e) {
			throw new DavesDoubleFormatException(CTA_SWITCH,CTA,e);
		}
	}
}

private static void getCTB(String[] args) throws DavesIntFormatException {
	String ctbStr = getValue(CTB_SWITCH,args);
	if (ctbStr != null) {
		try {
			int ctb = Integer.parseInt(ctbStr);
			if (ctb < 0)
				throw new NumberFormatException("Value of -"+CTB_SWITCH+" must be non-negative. Default value of "+CTB+" will be used.");
			CTB = ctb;
		} catch (NumberFormatException e) {
			throw new DavesIntFormatException(CTB_SWITCH, CTB, e);
		}
	}
}

private static void getRSuperdiagonals(String[] args) {
	R_SUPERDIAGONALS = getBooleanValue(R_SUPERDIAGONALS_SWITCH, args);
}

private static void getR(String[] args) throws DavesIntFormatException {
	String rStr = getValue(R_SWITCH,args);
	if (rStr != null) {
		try {
			int r = Integer.parseInt(rStr);
			if (r < 0)
				throw new NumberFormatException("ERROR: Value of -r switch out of bounds. Default value, "+R+", will be used.");
			R = r;
		} catch (NumberFormatException e) {
			throw new DavesIntFormatException(R_SWITCH, R, e);
		}
	}
}

private static void getRRT(String[] args) {
	RRT = getBooleanValue(RRT_SWITCH, args);
}

private static void getMinTECCompactness(String[] args) throws DavesDoubleFormatException {
	String mtcStr = getValue(MIN_TEC_COMPACTNESS_SWITCH,args);
	if (mtcStr != null) {
		try {
			double minct = Double.parseDouble(mtcStr);
			if (minct < 0 || minct > 1)
				throw new NumberFormatException("Value of -"+MIN_TEC_COMPACTNESS_SWITCH+" is out of bounds. Default value of "+MIN_TEC_COMPACTNESS+" will be used.");
			MIN_TEC_COMPACTNESS = minct;
		} catch (NumberFormatException e) {
			throw new DavesDoubleFormatException(MIN_TEC_COMPACTNESS_SWITCH, MIN_TEC_COMPACTNESS, e);
		}
	}
}

private static void getMinPatSize(String[] args) throws DavesIntFormatException {
	String mpsStr = getValue(MIN_PATTERN_SIZE_SWITCH,args);
	if (mpsStr != null) {
		try {
			int mps = Integer.parseInt(mpsStr);
			if (mps < 0)
				throw new NumberFormatException("Value of switch -"+MIN_PATTERN_SIZE_SWITCH+" must be non-negative. Default value of "+MIN_PATTERN_SIZE+" will be used.");
			MIN_PATTERN_SIZE = mps;
		} catch (NumberFormatException e) {
			throw new DavesIntFormatException(MIN_PATTERN_SIZE_SWITCH, MIN_PATTERN_SIZE, e);
		}
	}
}

private static void getMaxPatSize(String[] args) throws DavesIntFormatException {
	String maxpatStr = getValue(MAX_PATTERN_SIZE_SWITCH,args);
	if (maxpatStr != null) {
		try {
			int mps = Integer.parseInt(maxpatStr);
			if (mps < 0)
				throw new NumberFormatException("Value of -"+MAX_PATTERN_SIZE_SWITCH+" must be non-negative.");
			MAX_PATTERN_SIZE = mps; 
		} catch (NumberFormatException e) {
			throw new DavesIntFormatException(MAX_PATTERN_SIZE_SWITCH, MAX_PATTERN_SIZE, e);
		}
	}
}

private static void getMergeTECS(String[] args) {
	MERGE_TECS = getBooleanValue(MERGE_TECS_SWITCH, args);
}

private static void getMinMatchSize(String[] args) throws DavesIntFormatException{
	String minmatchStr = getValue(MIN_MATCH_SIZE_SWITCH,args);
	if (minmatchStr != null) {
		try {
			int mms = Integer.parseInt(minmatchStr);
			if (mms < 0)
				throw new NumberFormatException("Value of -"+MIN_MATCH_SIZE_SWITCH+" must be non-negative. Default value of "+MIN_MATCH_SIZE+ " will be used.");
			MIN_MATCH_SIZE = mms;
		} catch (NumberFormatException e) {
			throw new DavesIntFormatException(MIN_MATCH_SIZE_SWITCH, MIN_MATCH_SIZE, e);
		}
	}
}

private static void getNumIterations(String[] args) throws DavesIntFormatException {
	String itsStr = getValue(NUM_ITERATIONS_SWITCH,args);
	if (itsStr != null) {
		try {
			int ni = Integer.parseInt(itsStr);
			if (ni <= 0)
				throw new NumberFormatException("Value of -"+NUM_ITERATIONS_SWITCH+" must be greater than zero.");
			NUM_ITERATIONS = ni;
		} catch (NumberFormatException e) {
			throw new DavesIntFormatException(NUM_ITERATIONS_SWITCH,NUM_ITERATIONS,e);
		}
	}
}

private static void getWithoutChannel10(String[] args) {
	WITHOUT_CHANNEL_10 = getBooleanValue(WITHOUT_CHANNEL_TEN_SWITCH, args);
}

private static void getDraw(String[] args) {
	if (inputFileIsEncodingFile()) 
		DRAW = true;
	else
		DRAW = getBooleanValue(DRAW_SWITCH, args);
}

//	private static double CR_LOW					= 0.2;
private static void getCRLow(String[] args) throws DavesDoubleFormatException {
	String crLowStr = getValue(CR_LOW_SWITCH,args);
	if (crLowStr!=null) {
		try {
			double crLow = Double.parseDouble(crLowStr);
			CR_LOW = crLow;
		} catch (NumberFormatException e) {
			throw new DavesDoubleFormatException(CR_LOW_SWITCH, CR_LOW, e);
		}
	}
}
//	private static double CR_HIGH					= 1.0;
private static void getCRHigh(String[] args) throws DavesDoubleFormatException {
	String crHighStr = getValue(CR_HIGH_SWITCH,args);
	if (crHighStr!=null) {
		try {
			double crHigh = Double.parseDouble(crHighStr);
			CR_HIGH = crHigh;
		} catch (NumberFormatException e) {
			throw new DavesDoubleFormatException(CR_HIGH_SWITCH, CR_HIGH, e);
		}
	}
}
//	private static double COMP_V_LOW				= 0.2;
private static void getCompVLow(String[] args) throws DavesDoubleFormatException {
	String compVLowStr = getValue(COMP_V_LOW_SWITCH,args);
	if (compVLowStr!=null) {
		try {
			double compVLow = Double.parseDouble(compVLowStr);
			COMP_V_LOW = compVLow;
		} catch (NumberFormatException e) {
			throw new DavesDoubleFormatException(COMP_V_LOW_SWITCH, COMP_V_LOW, e);
		}
	}
}
//	private static double COMP_V_HIGH				= 1.0;
private static void getCompVHigh(String[] args) throws DavesDoubleFormatException {
	String compVHighStr = getValue(COMP_V_HIGH_SWITCH,args);
	if (compVHighStr!=null) {
		try {
			double compVHigh = Double.parseDouble(compVHighStr);
			COMP_V_HIGH = compVHigh;
		} catch (NumberFormatException e) {
			throw new DavesDoubleFormatException(COMP_V_HIGH_SWITCH, COMP_V_HIGH, e);
		}
	}
}
//	private static int C_MIN						= 15;
private static void getCMin(String[] args) throws DavesIntFormatException {
	String cMinStr = getValue(C_MIN_SWITCH,args);
	if (cMinStr!=null) {
		try {
			int cMin = Integer.parseInt(cMinStr);
			C_MIN = cMin;
		} catch (NumberFormatException e) {
			throw new DavesIntFormatException(C_MIN_SWITCH, C_MIN, e);
		}
	}
}
//	private static double SIGMA_MIN					= 0.5;
private static void getSigmaMin(String[] args) throws DavesDoubleFormatException {
	String sigmaMinStr = getValue(SIGMA_MIN_SWITCH,args);
	if (sigmaMinStr!=null) {
		try {
			double sigmaMin = Double.parseDouble(sigmaMinStr);
			SIGMA_MIN = sigmaMin;
		} catch (NumberFormatException e) {
			throw new DavesDoubleFormatException(SIGMA_MIN_SWITCH, SIGMA_MIN, e);
		}
	}
}

private static void getBBCompactness(String[] args) {
	BB_COMPACTNESS = getBooleanValue(BB_COMPACTNESS_SWITCH,args);
}

private static void getNoDate(String[] args) {
	NO_DATE = getBooleanValue(NO_DATE_SWITCH,args);
}

private static void getBBMode(String[] args) {
	BB_MODE = getBooleanValue(BB_MODE_SWITCH,args);
}

private static void getSegmentMode(String[] args) {
	SEGMENT_MODE = getBooleanValue(SEGMENT_MODE_SWITCH, args);
}

private static void getOutputFile(String[] args) {
	String outputFilePath = getValue(OUTPUT_FILE_SWITCH, args);
	if (outputFilePath != null) {
		OUTPUT_FILE = new File(outputFilePath);
		File outputFileDir = OUTPUT_FILE.getParentFile();
		if (outputFileDir != null && !outputFileDir.mkdirs())
			System.out.println("ERROR: Output file (-"+OUTPUT_FILE_SWITCH+") does not exist!");
	}
	if (OUTPUT_FILE != null)
		System.out.println("OUTPUT_FILE is "+OUTPUT_FILE.getAbsolutePath());
	if (OUTPUT_FILE == null)
		OUTPUT_DIR.mkdirs();
}

private static void getTopNPatterns(String[] args) throws DavesIntFormatException {
	String topNPatterns = getValue(TOP_N_PATTERNS_SWITCH,args);
	if (topNPatterns!=null) {
		try {
			int topNPats = Integer.parseInt(topNPatterns);
			TOP_N_PATTERNS = topNPats;
		} catch (NumberFormatException e) {
			throw new DavesIntFormatException(TOP_N_PATTERNS_SWITCH, TOP_N_PATTERNS, e);
		}
	}
}

private static void getRecurSIAAlgorithm(String[] args) {
	String recursiaAlgorithmString = getValue(RECURSIA_ALGORITHM_SWITCH,args);
	if (recursiaAlgorithmString != null)
		RECURSIA_ALGORITHM = Algorithm.valueOf(recursiaAlgorithmString);
}
////////////////////
//	Helper methods for parsing and displaying program arguments

private static String getValue(String key, String[] args) {
	for(int i = 0; i < args.length-1; i++)
		if (keyEqual(key,args[i]))
			return args[i+1];
	return null;
}

private static boolean getBooleanValue(String key, String[] args) {
	for(int i = 0; i < args.length; i++)
		if (keyEqual(key,args[i]))
			return true;
	return false;
}

private static boolean keyEqual(String key, String arg) {
	if (arg==null || key==null) return false;
	int beginIndex = 0;
	while (beginIndex < arg.length() && arg.charAt(beginIndex)=='-') beginIndex++;
	String newArg = arg.substring(beginIndex);
	return key.toLowerCase().equals(newArg.toLowerCase());
}

////////////////////
//	Console and log file output

private static void openLogFile() {

	File outputDir;
	String logFilePathString;
	if (OUTPUT_FILE == null) {
		outputDir = OUTPUT_DIR;
		logFilePathString = outputDir.toPath().resolve(INPUT_FILE.toPath().getFileName()).toAbsolutePath().toString();			
	}
	else {
		outputDir = OUTPUT_FILE.getParentFile();			
		logFilePathString = outputDir.toPath().resolve(OUTPUT_FILE.toPath().getFileName()).toAbsolutePath().toString();			
	}
	int endIndex = logFilePathString.lastIndexOf(".");
	logFilePathString = logFilePathString.substring(0, endIndex) + ".log";

	LOG_FILE = new File(logFilePathString);
	try {
		LOG = new PrintWriter(LOG_FILE);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

private static void closeLogFile() {
	if (LOG != null)
		LOG.close();
}

private static void println(String... s) {
	for(String str : s) {
		System.out.println(str);
		if (LOG!=null)
			LOG.println(str);
	}
}

public static String getParameterValuesString() {
	String[] parameterStrings = new String[]{
			"Parameter values",
			"================",
			"Basic algorithm: (-"+ALGORITHM_SWITCH+"): "+BASIC_ALGORITHM,
			"Input file (-"+INPUT_FILE_SWITCH+"): "+ ((INPUT_FILE==null)?null:INPUT_FILE.getAbsolutePath()),
			"Output directory (-"+OUTPUT_DIR_SWITCH+"): "+((OUTPUT_DIR==null)?null:OUTPUT_DIR.getAbsolutePath()),
			"Morphetic (diatonic) pitch (-"+DIATONIC_PITCH_SWITCH+"): "+DIATONIC_PITCH,
			"MIREX (-"+MIREX_SWITCH+"): "+MIREX,
			"Compactness trawler (-"+COMPACTNESS_TRAWLER_SWITCH+"): "+COMPACTNESS_TRAWLER,
			"Minimum compactness of trawled patterns (-"+CTA_SWITCH+"): "+CTA,
			"Minimum size of trawled patterns (-"+CTB_SWITCH+"): "+CTB,
			"For r superdiagonals (-"+R_SUPERDIAGONALS_SWITCH+"): "+R_SUPERDIAGONALS,
			"r (-"+R_SWITCH+"): "+R,
			"Remove redundant translators (-"+RRT_SWITCH+"): "+RRT,
			"Minimum TEC compactness (-"+MIN_TEC_COMPACTNESS_SWITCH+"): "+MIN_TEC_COMPACTNESS,
			"Minimum pattern size (-"+MIN_PATTERN_SIZE_SWITCH+"): "+MIN_PATTERN_SIZE,
			"Maximum pattern size (-"+MAX_PATTERN_SIZE_SWITCH+"): "+(MAX_PATTERN_SIZE==0?"Unlimited":MAX_PATTERN_SIZE),
			"Merge TECS (-"+MERGE_TECS_SWITCH+"): "+MERGE_TECS,
			"Minimum match size if TECs are merged (-"+MIN_MATCH_SIZE_SWITCH+"): "+MIN_MATCH_SIZE,
			"Number of iterations if TECs are merged (-"+NUM_ITERATIONS_SWITCH+"): "+NUM_ITERATIONS,
			"Remove channel 10 (drum channel) in MIDI (-"+WITHOUT_CHANNEL_TEN_SWITCH+"): "+WITHOUT_CHANNEL_10,
			"Help requested (-"+HELP_SWITCH+"): "+HELP,
			"Draw analysis (-"+DRAW_SWITCH+"): "+DRAW,
			"Minimum compression ratio in Forth's algorithm (-"+CR_LOW_SWITCH+"): "+CR_LOW,
			"Maximum compression ratio in Forth's algorithm (-"+CR_HIGH_SWITCH+"): "+CR_HIGH,
			"Minimum compactness threshold in Forth's algorithm (-"+COMP_V_LOW_SWITCH+"): "+COMP_V_LOW,
			"Maximum compactness threshold in Forth's algorithm (-"+COMP_V_HIGH_SWITCH+"): "+COMP_V_HIGH,
			"c_min threshold in Forth's algorithm (-"+C_MIN_SWITCH+"): "+C_MIN,
			"sigma_min threshold in Forth's algorithm (-"+SIGMA_MIN_SWITCH+"): "+SIGMA_MIN,
			"Using bounding-box compactness in Forth's algorithm (-"+BB_COMPACTNESS_SWITCH+"): "+BB_COMPACTNESS,
			"Appending date to output directories (-"+NO_DATE_SWITCH+") (N.B.: if true, then NO DATE APPENDED): "+NO_DATE,
			"Using BB mode in MIREX output (-"+BB_MODE_SWITCH+"): "+BB_MODE,
			"Using Segment mode in MIREX output (-"+SEGMENT_MODE_SWITCH+"): "+SEGMENT_MODE,
			"Output file (-"+OUTPUT_FILE_SWITCH+"): " + (OUTPUT_FILE!=null?OUTPUT_FILE.getAbsolutePath():null),
			"Top N Patterns (-"+TOP_N_PATTERNS_SWITCH+"): " + TOP_N_PATTERNS,
			"Basic algorithm used by RecurSIA (-"+RECURSIA_ALGORITHM_SWITCH+"): " + RECURSIA_ALGORITHM,
			"Sort TECs by decreasing pattern size (-"+SORT_BY_PATTERN_SIZE_SWITCH+"): " + SORT_BY_PATTERN_SIZE,
			"Use GPU acceleration (-"+GPU_ACCEL_SWITCH+"): " + GPU_ACCEL,
			"Draw input point set (-"+DRAW_POINT_SET_SWITCH+"): " + DRAW_POINT_SET,
			"Rhythm only (-"+RHYTHM_ONLY_SWITCH+"): " + RHYTHM_ONLY,
			"TEC quality priority string (-"+TEC_PRIORITY_SWITCH+"): " + TEC_PRIORITY_STRING,
			"Dual TEC quality priority string (-"+DUAL_TEC_PRIORITY_SWITCH+"): " + DUAL_TEC_PRIORITY_STRING,
			"Return number of MTPs only (-"+NUM_MTPS_ONLY_SWITCH+"): " + NUM_MTPS_ONLY,
			"Compactness type (-"+COMPACTNESS_TYPE_SWITCH+"): " + COMPACTNESS_TYPE,
			"Input file directory (-"+INPUT_DIR_SWITCH+"): " + INPUT_DIR,
			"Occurrence set file (-"+OCCURRENCE_SETS_FILE_SWITCH+"): " + ((OCCURRENCE_SETS_FILE==null)?null:OCCURRENCE_SETS_FILE.getAbsolutePath()),
			""
	};
	StringBuilder sb = new StringBuilder();
	for(String s : parameterStrings)
		sb.append(s+"\n");
	return sb.toString();
}

private static void printParsedParameterValues() {
	println(getParameterValuesString());		
}

private static String getBasicAlgorithms() {
	BasicAlgorithm[] basicAlgorithms = BasicAlgorithm.values();
	String output = basicAlgorithms[0].toString();
	for(int i = 1; i < basicAlgorithms.length-1; i++)
		output += ", "+basicAlgorithms[i];
	return output;
}

private static void showHelp() {
	println("",
			"OMNISIA HELP",
			"============",
			"",
			"Switches",
			"========",
			"",
			"-"+ALGORITHM_SWITCH+"\tBasic algorithm to use. Possible values are: ",
			"\t"+getBasicAlgorithms()+".",
			"\tDefault is COSIATEC.",
			"",
			"-"+INPUT_FILE_SWITCH+"\tPath to input file (REQUIRED unless -"+INPUT_DIR_SWITCH+" used).",
			"",
			"-"+INPUT_DIR_SWITCH+"\tPath to input directory containing input files to be batch processed",
			"\t(REQUIRED if -"+INPUT_FILE_SWITCH+" not used).",
			"",
			"-"+OUTPUT_DIR_SWITCH+"\tPath to output directory. Default is same",
			"\tdirectory as input file.",
			"",
			"-"+DIATONIC_PITCH_SWITCH+"\tIf present, then use morphetic (diatonic)",
			"\tpitch instead of chromatic pitch. If morphetic",
			"\tpitch is not available in the input data (e.g.,",
			"\tMIDI format), then input data is pitch-spelt using",
			"\tthe PS13s1 algorithm.",
			"",
			"-"+HELP_SWITCH+"\tHelp. If present, then this help screen to be printed.",
			"\tThis happens if the program is called with no arguments",
			"\tor if it is unable to determine the values of all",
			"\tnecessary parameters from the arguments provided.",
			"",
			"-"+MIREX_SWITCH+"\tIf present, generates output in MIREX format.",
			"",
			"-"+COMPACTNESS_TRAWLER_SWITCH+"\tIf present, uses Collins' compactness trawler,",
			"\tas used in his SIACT and SIARCT-CFP algorithms.",
			"",
			"-"+CTA_SWITCH+"\tThe variable which Collins et al call 'a'. It is the minimum",
			"\tcompactness permitted in the trawled patterns.",
			"",
			"-"+CTB_SWITCH+"\tThe variable which Collins et al call 'b'. It is the minimum",
			"\tsize of the patterns trawled by the compactness trawler.",
			"",
			"-"+R_SUPERDIAGONALS_SWITCH+"\tIf present, limits SIA to r superdiagonals, as used in Collins'",
			"\tSIAR algorithm. Number of superdiagonals determined by the -"+R_SWITCH+"switch.",
			"",
			"-"+R_SWITCH+"\tNumber of superdiagonals to analyse if limited with -"+R_SUPERDIAGONALS_SWITCH+" switch.",
			"\tDefault value is 1.",
			"",
			"-"+RRT_SWITCH+"\tIf present, redundant translators are removed.",
			"",
			"-"+MIN_TEC_COMPACTNESS_SWITCH+"\tThreshold value for minimum TEC compactness (default is 0.0).",
			"",
			"-"+MIN_PATTERN_SIZE_SWITCH+"\tMinimum allowed pattern size. Default is 0.",
			"",
			"-"+MAX_PATTERN_SIZE_SWITCH+"\tMaximum allowed pattern size. Default is 0, which means that",
			"\tpatterns of all sizes are allowed.",
			"",
			"-"+MERGE_TECS_SWITCH+"\tIf present, TECs are merged.",
			"",
			"-"+MIN_MATCH_SIZE_SWITCH+"\tMinimum match size if TECs are merged. Default value is 5.",
			"",
			"-"+NUM_ITERATIONS_SWITCH+"\tNumber of iterations if TECs are merged. Default value is 10.",
			"",
			"-"+WITHOUT_CHANNEL_TEN_SWITCH+"\tIf present, channel 10 (drum channel) is removed if input",
			"\tis in MIDI format.",
			"",
			"-"+DRAW_SWITCH+"\tIf present, generates an image file containing a visualization",
			"\tof the analysis.",
			"",
			"-"+CR_LOW_SWITCH+"\tMinimum compression ratio in Forth's algorithm. Default is 0.2.",
			"",
			"-"+CR_HIGH_SWITCH+"\tMaximum compression ratio in Forth's algorithm. Default is 1.0.",
			"",
			"-"+COMP_V_LOW_SWITCH+"\tMinimum compactness threshold in Forth's algorithm. Default is 0.2",
			"",
			"-"+COMP_V_HIGH_SWITCH+"\tMaximum compactness threshold in Forth's algorithm. Default is 1,0",
			"",
			"-"+C_MIN_SWITCH+"\tc_min threshold in Forth's algorithm. Default is 15",
			"",
			"-"+SIGMA_MIN_SWITCH+"\tsigma_min threshold in Forth's algorithm. Default is 0.5.",
			"",
			"-"+BB_COMPACTNESS_SWITCH+"\tIf present, use bounding-box compactness in Forth's algorithm",
			"\tinstead of within-voice segment compactness.",
			"",
			"-"+NO_DATE_SWITCH+"\tIf present, then does not append date to output directory names.",
			"",
			"-"+BB_MODE_SWITCH+"\tIf present, then uses BB mode when generating output in MIREX format.",
			"",
			"-"+SEGMENT_MODE_SWITCH+"\tIf present, then uses Segment mode when generating output in MIREX format.",
			"",
			"-"+OUTPUT_FILE_SWITCH+"\tIf present, overrides -"+OUTPUT_DIR_SWITCH+" and prints a single output",
			"\tencoding to the given path.",
			"",
			"-"+TOP_N_PATTERNS_SWITCH+"\tIf present, limits output to top N patterns.",
			"",
			"-"+RECURSIA_ALGORITHM_SWITCH+"\tIf RecurSIA is main algorithm used, then value of this switch",
			"\tdetermines which basic algorithm is used on each pattern. Possible values are COSIATEC,",
			"\tSIATECCompress or Forth.",
			"",
			"-"+SORT_BY_PATTERN_SIZE_SWITCH+"\tWhen using COSIATEC, getBestTEC sorts TECs",
			"\twith preference given to TECs with larger patterns.",
			"",
			"-"+GPU_ACCEL_SWITCH+"\tUse GPU acceleration.",
			"",
			"-"+DRAW_POINT_SET_SWITCH+"\tGenerates a PNG file and a PTS file of the input point set.",
			"\tIf the -"+DIATONIC_PITCH_SWITCH+" switch is selected, then the output point set uses",
			"\tmorphetic pitch.",
			"",
			"-"+RHYTHM_ONLY_SWITCH+"\tRuns the selected analysis algorithm on a rhythmic projection",
			"\tof the input dataset. That is, it only considers the first co-ordinate of each point,",
			"\tthe pitch co-ordinate is set to zero for every point.",
			"",
			"-"+TEC_PRIORITY_SWITCH+"\t Determines the priority with which heuristics are applied",
			"\twhen computing the quality of a TEC. The string should be a permutation of cmvswa.",
			"\tc = compression factor, m = compactness, v = coverage, s = pattern size, w = pattern width,",
			"\ta = pattern bounding-box area. The heuristics are applied as successive tie-breakers, in",
			"\tin the order in which they appear in this string. This switch determines only the way these",
			"\theuristics are applied when comparing two TECs that are not a conjugate pair. The default",
			"\tvalue for this string is "+TECQualityComparator.DEFAULT_PRIORITY_STRING+".",
			"",
			"-"+DUAL_TEC_PRIORITY_SWITCH+"\t Determines the priority with which heuristics are applied",
			"\twhen computing the quality of a TEC when comparing two TECs that form a conjugate pair.",
			"\tSee entry in this help for the switch, -"+TEC_PRIORITY_SWITCH+", for details regarding",
			"\thow to construct the string value for this switch. If neither this switch nor -"+TEC_PRIORITY_SWITCH,
			"\tare set, then the value of this string is "+TECQualityComparator.DEFAULT_PRIORITY_STRING+".",
			"\tIf -"+TEC_PRIORITY_SWITCH+" is set, but this switch is not, then the value of this string is",
			"\t the same as that set for -"+TEC_PRIORITY_SWITCH+".",
			"",
			"-"+NUM_MTPS_ONLY_SWITCH+"\t If true, then returns running time for computing vector table, sorting it",
			"\tand counting number of MTPs. This is in order to generate output that can be compared with Antti",
			"\t Laaksonen's parallel implementations of this process of 13 June 2021.",
			"",
			"-"+COMPACTNESS_TYPE_SWITCH+"\t Determines type of compactness used to compare quality of TECs in",
			"\tCOSIATEC algorithm. Legal values are SEGMENT and BB. Default is BB.",
			"",
			"-"+OCCURRENCE_SETS_FILE_SWITCH+"\t Allows the user to specify a file containing a set of pattern",
			"\toccurrences in LISP OPND format. The file should consist of a sequence of Lisp lists, delimited",
			"\tby parentheses. Each of these lists should contain a list of notes, where each note is a Lisp",
			"\tlist with the format (onset pitch-name duration [voice]). In addition to the occurrence set",
			"\tfile, the user must supply an input dataset file using the -"+INPUT_FILE_SWITCH+" switch.",
			""
			);
}

private static void setCommandLine(String[] args) {
	COMMAND_LINE = "";
	for(String s : args) 
		COMMAND_LINE = COMMAND_LINE + s.trim() + " ";
}


////////////////////
//	Analyse

private static void writeSwitchesToFile(String[] args) {
	try {
		File outputDir;
		String switchesFilePath;
		if (OUTPUT_FILE == null) {
			outputDir = OUTPUT_DIR;
			switchesFilePath = outputDir.toPath().resolve(INPUT_FILE.toPath().getFileName()).toAbsolutePath().toString();			
		}
		else {
			outputDir = OUTPUT_FILE.getParentFile();			
			switchesFilePath = outputDir.toPath().resolve(OUTPUT_FILE.toPath().getFileName()).toAbsolutePath().toString();			
		}
		int endIndex = switchesFilePath.lastIndexOf(".");
		switchesFilePath = switchesFilePath.substring(0, endIndex) + ".switches";
		File switchesFile = new File(switchesFilePath);
		PrintWriter pw = new PrintWriter(switchesFile);
		for(String s : args)
			pw.print(s+" ");
		pw.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

private static void analyse(String[] args) throws MissingTieStartNoteException, FileNotFoundException, IncompatibleRecurSIAAlgorithmException {
	if (OUTPUT_FILE == null)
		writeSwitchesToFile(args);
	Encoding encoding = null;
	if (!DRAW_POINT_SET && OCCURRENCE_SETS_FILE == null) {
		switch (BASIC_ALGORITHM) {
		case COSIATEC: encoding = runCOSIATEC(); break;
		case SIATECCompress: encoding = runSIATECCompress(); break;
		case SIA: encoding = runSIA(); break;    
		case SIATEC: encoding = runSIATEC(); break;
		case Forth: encoding = runForth(); break;
		case RecurSIA: encoding = runRecurSIA(); break;
		case TTWM: encoding = runTTWM(); break;
		case NONE: encoding = new COSIATECEncoding(INPUT_FILE.getAbsolutePath());
		}
		encoding.setTitle(COMMAND_LINE);
	} else //DRAW_POINT_SET is true or OCCURRENCE_SET_FILE is non-null
		encoding = new Encoding(
				//					PointSet dataset,
				new PointSet(
						INPUT_FILE.getAbsolutePath(),
						DIATONIC_PITCH,
						WITHOUT_CHANNEL_10),
				//					String inputFilePathString,
				INPUT_FILE.getAbsolutePath(),
				//					String outputDirectoryPathString,
				OUTPUT_DIR==null?null:OUTPUT_DIR.getAbsolutePath(),				
						//					boolean isDiatonic,
						DIATONIC_PITCH,
						//					boolean withoutChannel10,
						WITHOUT_CHANNEL_10,
						//					String outputFileExtension,
						"pts",
						//					int topNPatterns,
						TOP_N_PATTERNS,
						//					boolean forMirex,
						MIREX,
						//					boolean segmentMode,
						SEGMENT_MODE,
						//					boolean bbMode,
						BB_MODE,
						//					String omnisiaOutputFilePathString					
						(OUTPUT_FILE!=null?OUTPUT_FILE.getAbsolutePath():null)
				);

	//		switch (BASIC_ALGORITHM) {
	//		case COSIATEC: encoding = runCOSIATEC(); break;
	//		case SIATECCompress: encoding = runSIATECCompress(); break;
	//		case SIA: encoding = runSIA(); break;    
	//		case SIATEC: encoding = runSIATEC(); break;
	//		case Forth: encoding = runForth(); break;
	//		case RecurSIA: encoding = runRecurSIA(); break;
	//		case NONE: encoding = new COSIATECEncoding(INPUT_FILE.getAbsolutePath());
	//		}
	//		encoding.setTitle(COMMAND_LINE);

	//		Print dataset used for analysis to file
	File outputDir;
	String outputDatasetFilePath;
	if (OUTPUT_FILE == null) {
		outputDir = OUTPUT_DIR;
		outputDatasetFilePath = outputDir.toPath().resolve(INPUT_FILE.toPath().getFileName()).toAbsolutePath().toString();			
	}
	else {
		outputDir = OUTPUT_FILE.getParentFile();			
		outputDatasetFilePath = outputDir.toPath().resolve(OUTPUT_FILE.toPath().getFileName()).toAbsolutePath().toString();			
	}
	int endIndex = outputDatasetFilePath.lastIndexOf(".");
	outputDatasetFilePath = outputDatasetFilePath.substring(0, endIndex) + ".pts";
	try {
		encoding.getDataset().writeToPtsFile(outputDatasetFilePath);
		//			PrintWriter pw = new PrintWriter(new File(outputDatasetFilePath));
		//			pw.println();
		//			pw.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	if ((DRAW || DRAW_POINT_SET || OCCURRENCE_SETS_FILE != null) && encoding != null && INPUT_DIR == null) {
		if (OCCURRENCE_SETS_FILE != null) {
			encoding.readOccurrenceSets(OCCURRENCE_SETS_FILE);
		}
		//if (DRAW && encoding != null) {
		String outputImageFilePath;
		if (OUTPUT_FILE == null) {
			outputDir = OUTPUT_DIR;
			outputImageFilePath = outputDir.toPath().resolve(INPUT_FILE.toPath().getFileName()).toAbsolutePath().toString();			
		}
		else {
			outputDir = OUTPUT_FILE.getParentFile();			
			outputImageFilePath = outputDir.toPath().resolve(OUTPUT_FILE.toPath().getFileName()).toAbsolutePath().toString();			
		}
		if (!(BASIC_ALGORITHM == BasicAlgorithm.RecurSIA))
			encoding.drawOccurrenceSetsToFile(outputImageFilePath,DIATONIC_PITCH);
		//			else
		//				encoding.drawRecursiveTecsToFile(outputImageFilePath,DIATONIC_PITCH);
	}
	if ((DRAW || DRAW_POINT_SET) && encoding != null && INPUT_DIR != null) {
		String outputFilePathString = encoding.getOutputFilePathString();
		OUTPUT_FILE_PATH_STRINGS.add(outputFilePathString);
	}
}

private static COSIATECEncoding runCOSIATEC() throws MissingTieStartNoteException, FileNotFoundException {
	return new COSIATECEncoding(
			null, 
			OUTPUT_DIR==null?null:OUTPUT_DIR.getAbsolutePath(), 
					DIATONIC_PITCH, 
					INPUT_FILE.getAbsolutePath(), 
					MIREX,
					COMPACTNESS_TRAWLER,
					CTA,
					CTB,
					R_SUPERDIAGONALS,
					R,
					RRT,
					MIN_TEC_COMPACTNESS,
					MIN_PATTERN_SIZE,
					MAX_PATTERN_SIZE,
					MERGE_TECS,
					MIN_MATCH_SIZE,
					NUM_ITERATIONS,
					false, //Do not draw
					SEGMENT_MODE,
					BB_MODE,
					(OUTPUT_FILE!=null?OUTPUT_FILE.getAbsolutePath():null),
					TOP_N_PATTERNS,
					WITHOUT_CHANNEL_10,
					SORT_BY_PATTERN_SIZE,
					TEC_PRIORITY_STRING,
					DUAL_TEC_PRIORITY_STRING,
					COMPACTNESS_TYPE
			);
}

private static SIATECCompressEncoding runSIATECCompress() {
	try {
		return new SIATECCompressEncoding(
				INPUT_FILE.getAbsolutePath(), 
				OUTPUT_DIR.getAbsolutePath(), 
				MIN_PATTERN_SIZE,
				MAX_PATTERN_SIZE,
				(DIATONIC_PITCH?PitchRepresentation.MORPHETIC_PITCH:PitchRepresentation.CHROMATIC_PITCH), 
				false, //draw output
				COMPACTNESS_TRAWLER, 
				CTA, 
				CTB,
				R_SUPERDIAGONALS,
				R,
				RRT,
				MIREX,
				SEGMENT_MODE,
				BB_MODE,
				(OUTPUT_FILE!=null?OUTPUT_FILE.getAbsolutePath():null),
				TOP_N_PATTERNS,
				WITHOUT_CHANNEL_10);
	} catch (NoMorpheticPitchException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (UnimplementedInputFileFormatException e) {
		e.printStackTrace();
	} catch (InvalidMidiDataException e) {
		e.printStackTrace();
	} catch (MissingTieStartNoteException e) {
		e.printStackTrace();
	}
	return null;
}

private static SIAEncoding runSIA() throws MissingTieStartNoteException {
	try {
		return new SIAEncoding(
				INPUT_FILE.getAbsolutePath(), 
				OUTPUT_DIR.getAbsolutePath(), 
				MIN_PATTERN_SIZE,
				MAX_PATTERN_SIZE,
				(DIATONIC_PITCH?PitchRepresentation.MORPHETIC_PITCH:PitchRepresentation.CHROMATIC_PITCH), 
				false, //Draw output
				R_SUPERDIAGONALS, R,
				COMPACTNESS_TRAWLER, CTA, CTB,
				MIREX, SEGMENT_MODE, BB_MODE,
				(OUTPUT_FILE!=null?OUTPUT_FILE.getAbsolutePath():null),
				TOP_N_PATTERNS,
				WITHOUT_CHANNEL_10,
				GPU_ACCEL);
	} catch (NoMorpheticPitchException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (UnimplementedInputFileFormatException e) {
		e.printStackTrace();
	} catch (InvalidMidiDataException e) {
		e.printStackTrace();
	}
	return null;
}

private static TTWMEncoding runTTWM() throws MissingTieStartNoteException, FileNotFoundException {
	return new TTWMEncoding(
			INPUT_FILE.getAbsolutePath(),
			OUTPUT_DIR.getAbsolutePath(),
			DIATONIC_PITCH);
}

private static SIATECEncoding runSIATEC() throws MissingTieStartNoteException {
	try {
		return new SIATECEncoding(
				new PointSet(INPUT_FILE.getAbsolutePath(),DIATONIC_PITCH,WITHOUT_CHANNEL_10),
				INPUT_FILE.getAbsolutePath(), 
				OUTPUT_DIR.getAbsolutePath(), 
				MIN_PATTERN_SIZE, 
				MAX_PATTERN_SIZE,
				(DIATONIC_PITCH?PitchRepresentation.MORPHETIC_PITCH:PitchRepresentation.CHROMATIC_PITCH), 
				false, //drawOutput
				true, //verbose
				R_SUPERDIAGONALS, R,
				COMPACTNESS_TRAWLER, CTA, CTB,
				true, //Indicates that it is being called from OMNISIA
				MIREX, SEGMENT_MODE, BB_MODE,
				(OUTPUT_FILE!=null?OUTPUT_FILE.getAbsolutePath():null),
				TOP_N_PATTERNS,
				WITHOUT_CHANNEL_10,
				RRT
				);
	} catch (NoMorpheticPitchException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (UnimplementedInputFileFormatException e) {
		e.printStackTrace();
	} catch (InvalidMidiDataException e) {
		e.printStackTrace();
	}
	return null;
}

private static ForthEncoding runForth() throws MissingTieStartNoteException, FileNotFoundException {
	PointSet pointSet = new PointSet(INPUT_FILE.getAbsolutePath(),DIATONIC_PITCH,WITHOUT_CHANNEL_10);
	return new ForthEncoding(
			pointSet,
			INPUT_FILE.getAbsolutePath(), 
			OUTPUT_DIR.getAbsolutePath(),
			(DIATONIC_PITCH?PitchRepresentation.MORPHETIC_PITCH:PitchRepresentation.CHROMATIC_PITCH),
			MIN_PATTERN_SIZE,
			MAX_PATTERN_SIZE,
			CR_LOW, 
			CR_HIGH, 
			COMP_V_LOW, 
			COMP_V_HIGH,
			C_MIN,
			SIGMA_MIN,
			R_SUPERDIAGONALS, R,
			COMPACTNESS_TRAWLER, CTA, CTB,
			BB_COMPACTNESS,
			RRT,
			MIREX,
			SEGMENT_MODE,
			BB_MODE,
			(OUTPUT_FILE!=null?OUTPUT_FILE.getAbsolutePath():null),
			TOP_N_PATTERNS,
			WITHOUT_CHANNEL_10,
			true //fromOMNISA
			);
}

private static RecurSIAEncoding runRecurSIA() throws FileNotFoundException, MissingTieStartNoteException, IncompatibleRecurSIAAlgorithmException {
	return new RecurSIAEncoding(
			//				private static File INPUT_FILE 					= null;
			INPUT_FILE.getAbsolutePath(),
			//				private static File OUTPUT_DIR 					= null;
			OUTPUT_DIR==null?null:OUTPUT_DIR.getAbsolutePath(),
					//				private static boolean DIATONIC_PITCH 			= false;
					DIATONIC_PITCH,
					//				private static boolean WITHOUT_CHANNEL_10		= false;
					WITHOUT_CHANNEL_10,
					//				private static int TOP_N_PATTERNS				= 0; //Limits output to top n patterns (if 0, then all patterns returned)
					TOP_N_PATTERNS,
					//				private static boolean MIREX					= false;
					MIREX,
					//				private static boolean SEGMENT_MODE				= false; //Use Segment mode in MIREX output
					SEGMENT_MODE,
					//				private static boolean BB_MODE					= false; //Use BB mode in MIREX output
					BB_MODE,
					//				private static File OUTPUT_FILE					= null;  //To be used to send output encoding file only to given place.
					OUTPUT_FILE==null?null:OUTPUT_FILE.getAbsolutePath(),
							//				private static boolean COMPACTNESS_TRAWLER		= false;
							COMPACTNESS_TRAWLER,
							//				private static double CTA						= 0.67;
							CTA,
							//				private static int CTB							= 3;
							CTB,
							//				private static boolean R_SUPERDIAGONALS			= false;
							R_SUPERDIAGONALS,
							//				private static int R							= 1;
							R,
							//				private static boolean RRT						= false;
							RRT,
							//				private static double MIN_TEC_COMPACTNESS		= 0.0;
							MIN_TEC_COMPACTNESS,
							//				private static int MIN_PATTERN_SIZE				= 0;
							MIN_PATTERN_SIZE,
							//				private static int MAX_PATTERN_SIZE				= 0; //Allows patterns of all sizes.
							MAX_PATTERN_SIZE,
							//				private static boolean MERGE_TECS				= false;
							MERGE_TECS,
							//				private static int MIN_MATCH_SIZE				= 5;
							MIN_MATCH_SIZE,
							//				private static int NUM_ITERATIONS				= 10;
							NUM_ITERATIONS,
							RECURSIA_ALGORITHM,
							CR_LOW, CR_HIGH, COMP_V_LOW, COMP_V_HIGH, C_MIN, SIGMA_MIN, BB_COMPACTNESS,
							true, //fromOMNISIA
							SORT_BY_PATTERN_SIZE,
							TEC_PRIORITY_STRING,
							DUAL_TEC_PRIORITY_STRING,
							COMPACTNESS_TYPE
			);
}

////////////////////
//	Main method
public static void main(String[] args) throws MissingTieStartNoteException {
	setCommandLine(args);
	getHelp(args);
	if (HELP) {
		showHelp();
		printParsedParameterValues();
		closeLogFile();
		return;
	}
	getInputFile(args);
	getOccurrenceSetFile(args);
//	if (INPUT_FILE == null) {
//		showHelp();
//		printParsedParameterValues();
//		return;
//	}
	getInputDir(args);
	if (INPUT_FILE == null && INPUT_DIR == null) {
		println("ERROR: No input file or input directory provided. Use the -"+HELP_SWITCH+" to get help.");
		return;
	}
	getNoDate(args);


	getRecurSIAAlgorithm(args);
	getMIREX(args);
	getCompactnessTrawler(args);
	getRSuperdiagonals(args);
	getRRT(args);
	getMergeTECS(args);
	getWithoutChannel10(args);
	getBBCompactness(args);
	getSegmentMode(args);
	getBBMode(args);
	getSortByPatternSize(args);
	getGPUAccel(args);
	getDrawPointSet(args);
	try {
		getCTB(args);
		getR(args);
		getMinPatSize(args);
		getMaxPatSize(args);
		getMinMatchSize(args);
		getNumIterations(args);
		getCMin(args);
		getTopNPatterns(args);
	} catch (DavesIntFormatException e) {
		println(e.getMessage());
		printParsedParameterValues();
		closeLogFile();
		return;
	}
	getRhythmOnly(args);
	getNumMtpsOnly(args);
	getTECPriority(args);
	getDualTECPriority(args);
	getCompactnessType(args);
	try {
		getCTA(args);
		getMinTECCompactness(args);
		getCompVHigh(args);
		getCompVLow(args);
		getCRHigh(args);
		getCRLow(args);
		getSigmaMin(args);
	} catch (DavesDoubleFormatException e) {
		println(e.getMessage());
		printParsedParameterValues();
		closeLogFile();
		return;
	}
	printParsedParameterValues();
	try {
		if (INPUT_DIR != null && INPUT_FILE_LIST != null) {
			int i = 0;
			for(File inputFile : INPUT_FILE_LIST)
				println(++i + " " + inputFile.getAbsolutePath());
			for(File inputFile : INPUT_FILE_LIST) {
				INPUT_FILE = inputFile;
				getOutputDir(args);
				getOutputFile(args);
				openLogFile();
				getBasicAlgorithm(args);
				getDiatonicPitch(args);
				getDraw(args);
				analyse(args);
				closeLogFile();
			}
			if (DRAW) {
				Encoding.drawEncodingFiles();
			}
		} else {
			getOutputDir(args);
			getOutputFile(args);
			openLogFile();
			getBasicAlgorithm(args);
			getDiatonicPitch(args);
			getDraw(args);
			analyse(args);		
			closeLogFile();
		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IncompatibleRecurSIAAlgorithmException e) {
		e.printStackTrace();
	}
	closeLogFile();
}
}
