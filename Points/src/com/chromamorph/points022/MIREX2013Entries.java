package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.JFileChooser;

import com.chromamorph.maths.Maths;
import com.chromamorph.points022.CollinsLispToOpnd.CollinsNote;

/**
 * 
 * @author David Meredith
 * @date 11 July 2013
 * 
 * This is the main class for the COSIATEC entry for
 * MIREX 2013.
 * 
 * The class needs to run from the command line and take two arguments:
 * 
 * CosiatecMirex2013 <inputFile> <outputFile>
 * 
 * If outputFile is a directory name, then the name of
 * the outputFile is the same as that of the inputFile
 * but with the suffix changed to ".cos"
 * 
 * If no outputFile is specified, the outputFile is 
 * stored in the same directory as the inputFile and
 * has the same name as the input file but with the
 * suffix changed to ".cos".
 * 
 * The format of the output file is as specified on
 * the MIREX competition page:
 * 
 * http://www.music-ir.org/mirex/wiki/2013:Discovery_of_Repeated_Themes_%26_Sections
 * 
 * The format is as follows:
 * 
pattern1 
occurrence1 
7.00000, 45.00000 
7.00000, 48.00000 
... 
11.00000, 60.00000 
occurrence2 
31.00000, 57.00000 
31.00000, 60.00000 
... 
35.00000, 72.00000 
occurrence3 
59.00000, 57.00000 
59.00000, 60.00000 
... 
63.00000, 72.00000 
pattern2 
occurrence1 
7.00000, 45.00000 
7.00000, 48.00000 
... 
11.00000, 57.00000 
occurrence2 
27.00000, 48.00000 
27.00000, 52.00000 
... 
59.00000, 60.00000 
... 
patternM 
occurrence1 
9.00000, 58.00000 
9.50000, 52.00000 
... 
12.00000, 60.0000 
...
occurrencem 
100.00000, 62.00000 
100.50000, 55.00000 
...
103.00000, 61.00000
 *
 * The patterns are given in the order that they are
 * computed by the COSIATEC algorithm, on the hypothesis
 * that the first pattern is the perceptually most salient
 * or "best" pattern.
 * 
 * Each occurrence is the contents of the bounding box of the
 * the actual pattern discovered by COSIATEC. (Though in some
 * cases, this may give poorer precision, e.g., with fugue
 * entries.)
 *
 */
public class MIREX2013Entries {

	public static String PARAMETER_STRING = null;
	public static String ROOT_INPUT_DIRECTORY_PATH = "/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth";
	public static String ROOT_OUTPUT_DIRECTORY_PATH = "/Users/dave/Documents/Work/Research/MIREX2013/mirexDave-2013-07-02";
	public static String[] OUTPUT_PREFIXES = {"bach_wtc2f20_","beet_sonata01-3_","chop_mazurka24-4_","gbns_silverswan_","mzrt_sonata04-2_"};
	public static String[] INPUT_PREFIXES = {"wtc2f20","sonata01-3","mazurka24-4","silverswan","sonata04-2"};
	public static double MIN_PATTERN_SIZE = 0.0;
	public static double BOUNDING_BOX = 0.0;
	public static double SEGMENT = 0.0;
	public static double READ_ENCODING_FILE = 0.0;
	public static double SIATEC_COMPRESS = 0.0;

	public static String INPUT_FILE_NAME= null;
	public static String COS_INPUT_FILE_PATH = null;
	public static String OUTPUT_FILE_NAME = null;
	public static String TIMING_FILE_NAME = null;
	public static PointSet DATASET = null;
	public static Integer GCD, PRODUCT_OF_DENOMINATORS;
	public static Encoding ENCODING = null;
	public static ArrayList<TomDavePoint> TOM_DAVE_POINTS = null;

	public static long START_TIME, END_TIME;

	static class TomDavePoint {
		int tomsNumerator, tomsDenominator, davesOnset, tomsPitch, davesPitch;

		public String toString() {
			return "tdp(tn("+tomsNumerator+"),td("+tomsDenominator+"),tp("+tomsPitch+"),do("+davesOnset+"),dp("+davesPitch+"))";
		}
	}

	public static void main(String[] args) {
		run(args);
	}

	public static void run(String[] args) {
		parseParameterString();
		findInputAndOutputFileNames(args);
		readLispFileIntoPointSet(INPUT_FILE_NAME);
		getStartTime();
		encodeDataset();

		writeEncodingToFile(
				ENCODING,
				OUTPUT_FILE_NAME,
				(int)MIN_PATTERN_SIZE,
				BOUNDING_BOX > 0.0,
				SEGMENT > 0.0,
				DATASET);
		getEndTime();
		writeTimingFile();
	}

	private static void getStartTime() {
		START_TIME = System.currentTimeMillis();
	}

	private static void getEndTime() {
		END_TIME = System.currentTimeMillis();
	}

	private static void writeTimingFile() {
		try {
			PrintStream ps = new PrintStream(TIMING_FILE_NAME);
			ps.print(END_TIME-START_TIME);
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void parseParameterString() {
		MIN_PATTERN_SIZE = getParameterStringValue("minPatternSize");
		System.out.println("MIN_PATTERN_SIZE = " + String.format("%.2f",MIN_PATTERN_SIZE));
		BOUNDING_BOX = getParameterStringValue("boundingBox");
		System.out.println("BOUNDING_BOX = " + String.format("%.2f",BOUNDING_BOX));
		SEGMENT = getParameterStringValue("segment");
		System.out.println("SEGMENT = " + String.format("%.2f",SEGMENT));
		READ_ENCODING_FILE = getParameterStringValue("readEncodingFile");
		System.out.println("READ_ENCODING_FILE = " + String.format("%.2f",READ_ENCODING_FILE));
		SIATEC_COMPRESS = getParameterStringValue("siatecCompress");
		System.out.println("SIATEC_COMPRESS = "+ String.format("%.2f",SIATEC_COMPRESS));
	}

	public static void findInputAndOutputFileNames(String[] args) {

		System.out.print("Determining input and output file names...");
		String outputFileDirectory = null;
		if (READ_ENCODING_FILE != 0.0) {
			outputFileDirectory = getOutputFileDirectoryName(ROOT_OUTPUT_DIRECTORY_PATH);
			COS_INPUT_FILE_PATH = getMelCosFileName(outputFileDirectory);
		}
		if (args.length < 1) {
			INPUT_FILE_NAME = getLispInputFilePath(ROOT_INPUT_DIRECTORY_PATH);
			if (outputFileDirectory == null)
				outputFileDirectory = getOutputFileDirectoryName(ROOT_OUTPUT_DIRECTORY_PATH);
			int start = INPUT_FILE_NAME.lastIndexOf("/")+1;
			int end = INPUT_FILE_NAME.lastIndexOf(".");
			String inputFileNameWithoutPath = INPUT_FILE_NAME.substring(start,end);
			OUTPUT_FILE_NAME = outputFileDirectory + (outputFileDirectory.endsWith("/")?"":"/")+ getOutputFileName(inputFileNameWithoutPath);
			TIMING_FILE_NAME = outputFileDirectory + (outputFileDirectory.endsWith("/")?"":"/")+ getTimingFileName(inputFileNameWithoutPath);
		} else {
			String i = args[0];
			if (new File(i).exists()) 
				INPUT_FILE_NAME = i;
			else {
				System.out.println("Input file does not exist: "+args[0]);
				return;
			}

			if (args.length > 1) { // Either output file name or output file folder provided
				String o = args[1];
				File f = new File(o);
				int start = INPUT_FILE_NAME.lastIndexOf("/")+1;
				int end = INPUT_FILE_NAME.lastIndexOf(".");
				String inputFileNameWithoutPath = INPUT_FILE_NAME.substring(start,end);
				if(f.isDirectory()) {
					OUTPUT_FILE_NAME = o + (o.endsWith("/")?"":"/")+ getOutputFileName(inputFileNameWithoutPath);
					TIMING_FILE_NAME = o + (o.endsWith("/")?"":"/")+ getTimingFileName(inputFileNameWithoutPath);
				} else {
					OUTPUT_FILE_NAME = o;
					String od = o.substring(0,o.lastIndexOf("/"));
					TIMING_FILE_NAME = od + "/" + getTimingFileName(inputFileNameWithoutPath);
				}
			} else {
				int start = INPUT_FILE_NAME.lastIndexOf("/")+1;
				int end = INPUT_FILE_NAME.lastIndexOf(".");
				String inputFilePath = INPUT_FILE_NAME.substring(0,start);
				String inputFileNameWithoutPath = INPUT_FILE_NAME.substring(start,end);
				OUTPUT_FILE_NAME = inputFilePath+getOutputFileName(inputFileNameWithoutPath);
				TIMING_FILE_NAME = inputFilePath+getTimingFileName(inputFileNameWithoutPath);
			}
		}

		System.out.println("DONE!");
		System.out.println("INPUT FILE:  "+INPUT_FILE_NAME);
		System.out.println("OUTPUT FILE: "+OUTPUT_FILE_NAME);
		System.out.println("TIMING FILE: "+TIMING_FILE_NAME);
	}

	public static String getOutputFileName(String inputFileNameWithoutPath) {
		return getOutputOrTimingFileName(inputFileNameWithoutPath, false);
	}

	public static String getTimingFileName(String inputFileNameWithoutPath) {
		return getOutputOrTimingFileName(inputFileNameWithoutPath, true);
	}

	private static String getOutputOrTimingFileName(String inputFileNameWithoutPath, boolean timingFile) {
		String algorithm = (SIATEC_COMPRESS > 0?"SIATECCompress":"COSIATEC");
		String suffix = (timingFile?"rt":"txt");
		for(int i = 0; i < INPUT_PREFIXES.length; i++) {
			String inputPrefix = INPUT_PREFIXES[i];
			if (inputFileNameWithoutPath.startsWith(inputPrefix)) {
				return OUTPUT_PREFIXES[i]+algorithm+(PARAMETER_STRING.length()>0?"_"+PARAMETER_STRING.trim().replaceAll("_", "").replaceAll(",", "_"):"")+"."+suffix;
			}
		}
		return inputFileNameWithoutPath+"_"+algorithm+(PARAMETER_STRING.length()>0?"_"+PARAMETER_STRING.trim().replaceAll("_", "").replaceAll(",", "_"):"")+"."+suffix;
	}

	public static ArrayList<TomDavePoint> readLispFileIntoPointSet(String lispFileName) {
		return readLispFileIntoPointSet(lispFileName,true);
	}
	
	/**
	 * Assumes the file at inputFileName is in Collins' "lisp" format.
	 * 
	 * Converts this into OPND format internally (without creating a
	 * file).
	 * 
	 * Stores the gcd and product of denominators so that the onsets
	 * can be restored to Collins' format for the output.
	 */
	public static ArrayList<TomDavePoint> readLispFileIntoPointSet(String lispFileName, boolean diatonicPitch) {

		//Read Collins lisp file into a StringBuilder
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(lispFileName));
			for (String l = br.readLine(); l != null; l = br.readLine()) sb.append(l);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Convert Stringbuilder to a String
		String text = sb.toString();

		//Parse the string into an array of note strings
		String[] sa = text.split("[()]");

		//Make list of CollinsNote objects
		ArrayList<CollinsNote> collinsNotes = new ArrayList<CollinsNote>();
		for(int i = 0; i < sa.length;i++) {
			String s = sa[i].trim();
			if (s.length() > 0) {
				CollinsNote collinsNote = new CollinsNote(s);
				collinsNotes.add(collinsNote);
			}
		}

		//Find the distinct denominators used in the onsets and durations
		//in the input lisp format file.
		TreeSet<Integer> distinctDenominators = new TreeSet<Integer>();
		for(CollinsNote c : collinsNotes) {
			Integer durationDenominator = c.getDurationDenominator(); 
			if (durationDenominator != null) 
				distinctDenominators.add(durationDenominator);
			distinctDenominators.add(c.getOnsetDenominator());
		}

		PRODUCT_OF_DENOMINATORS = 1;
		for(Integer d : distinctDenominators)
			PRODUCT_OF_DENOMINATORS *= d;

		//Multiply all the onsets and durations by productOfDenominators
		//Make a list of all the distinct results

		TreeSet<Integer> distinctMultipliedOnsetsAndDurations = new TreeSet<Integer>();
		for(CollinsNote c : collinsNotes) {
			if (c.getDurationNumerator() != null)
				distinctMultipliedOnsetsAndDurations.add(c.getDurationNumerator() * PRODUCT_OF_DENOMINATORS / c.getDurationDenominator());
			Integer onset = c.getOnsetNumerator() * PRODUCT_OF_DENOMINATORS / c.getOnsetDenominator();
			distinctMultipliedOnsetsAndDurations.add(onset);
		}

		//Find gcd of all multiplied onsets and durations

		GCD = Maths.gcd(distinctMultipliedOnsetsAndDurations.toArray(new Integer[1]));

		DATASET = new PointSet();
		TOM_DAVE_POINTS = new ArrayList<TomDavePoint>();
		for(CollinsNote c : collinsNotes) {
			int onset = (c.getOnsetNumerator() * PRODUCT_OF_DENOMINATORS) / (GCD * c.getOnsetDenominator());
			int morpheticPitch = c.getCollinsMorpheticPitch() - 37;
			int chromaticPitch = c.getMidiNoteNumber() - 21;
			DATASET.add(new Point(onset,diatonicPitch?morpheticPitch:chromaticPitch));
			TomDavePoint tomDavePoint = new TomDavePoint();
			tomDavePoint.davesOnset = onset;
			tomDavePoint.davesPitch = diatonicPitch?morpheticPitch:chromaticPitch;
			tomDavePoint.tomsDenominator = c.getOnsetDenominator();
			tomDavePoint.tomsNumerator = c.getOnsetNumerator();
			tomDavePoint.tomsPitch = c.getMidiNoteNumber();
			TOM_DAVE_POINTS.add(tomDavePoint);
		}

		return TOM_DAVE_POINTS;
	}

	public static void encodeDataset() {
		String algorithm;
		if (SIATEC_COMPRESS > 0) { // Run SIATECCompress
			algorithm = "sia";
			ENCODING = new SIATECCompressEncoding(DATASET);
		} else { // Run COSIATEC
			algorithm = "cos";
			if (READ_ENCODING_FILE > 0.0)
				ENCODING = new COSIATECEncoding(COS_INPUT_FILE_PATH);
//			else 
//				ENCODING = new COSIATECEncoding(DATASET);
		}

		//Make output file name
		String outputCOSFilePath = null;
		for(int i = 0; i < OUTPUT_PREFIXES.length; i++) {
			int start;
			if ((start = OUTPUT_FILE_NAME.indexOf(OUTPUT_PREFIXES[i])) >= 0) {
				outputCOSFilePath = OUTPUT_FILE_NAME.substring(0,start+OUTPUT_PREFIXES[i].length()-1)+"."+algorithm;
				break;
			}
		}

		//Print encoding to output file
		PrintStream ps;
		try {
			ps = new PrintStream(outputCOSFilePath);
			ps.println(ENCODING);
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void writeEncodingToFile(
			Encoding encoding, 
			String outputFileName, 
			int minPatternSize,
			boolean boundingBox,
			boolean segment,
			PointSet dataset) {
		PrintStream outputStream;
		try {
			outputStream = new PrintStream(outputFileName);
			ArrayList<TEC> tecs = encoding.getTECs();
			for(int i = 0; i < tecs.size(); i++) {
				TEC tec = tecs.get(i);
				if (tec.getPattern().size() >= minPatternSize)
					outputStream.print(getMIREXString(tec,i+1,boundingBox,segment,dataset));
			}
			outputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot write to output file: "+outputFileName);
		}
	}

	public static double getParameterStringValue(String key) {
		int i = PARAMETER_STRING.indexOf(key);
		if (i >= 0) {
			int commaPos = PARAMETER_STRING.indexOf(",",i);
			if (commaPos < 0) commaPos = PARAMETER_STRING.length();
			return Double.parseDouble(PARAMETER_STRING.substring(i+key.length()+1,commaPos));
		}
		return 0.0;
	}

	public static String getMIREXString(TEC tec, int index, boolean boundingBox, boolean segment, PointSet dataset) {
		StringBuilder sb = new StringBuilder();
		sb.append("pattern"+index+"\n");
		TreeSet<Vector> translators = tec.getTranslators().getVectors();

		ArrayList<PointSet> occurrences = new ArrayList<PointSet>();
		for(Vector v : translators) {
			PointSet occurrence = tec.getPattern().translate(v);
			if (boundingBox) {
				occurrence = dataset.getBBSubset(occurrence.getTopLeft(), occurrence.getBottomRight());
			} else if (segment) {
				occurrence = dataset.getSegment(occurrence.getMinX(), occurrence.getMaxX(),true);
			}

			occurrences.add(occurrence);
		}

		int occIndex = 0;
		for(PointSet pointSet : occurrences) {
			sb.append("occurrence"+ ++occIndex+"\n");
			TreeSet<Point> points = pointSet.getPoints();
			for(Point thisPoint : points) {
				TomDavePoint tomDavePoint = findTomDavePoint(thisPoint);
				double outputOnset = (tomDavePoint.tomsNumerator * 1.0)/(tomDavePoint.tomsDenominator);
				double outputPitch = tomDavePoint.tomsPitch * 1.0;
				String pointString = String.format("%.5f",outputOnset)+", "+String.format("%.5f",outputPitch)+"\n";
				sb.append(pointString);
			}
		}
		return sb.toString();
	}

	public static TomDavePoint findTomDavePoint(Point p) {
		if (TOM_DAVE_POINTS == null) System.out.println("TOM_DAVE_POINTS is null");
		if (TOM_DAVE_POINTS.isEmpty()) System.out.println("TOM_DAVE_POINTS is empty");
		for(TomDavePoint tdp : TOM_DAVE_POINTS) {
			if (p.getX() == tdp.davesOnset && p.getY() == tdp.davesPitch) {
				return tdp;
			}
		}
		//		System.out.println("ERROR!!! Corresponding tomDavePoint not found for point "+p);
		throw new IllegalArgumentException("Corresponding tomDavePoint not found for point "+p+"\nTOM_DAVE_POINTS:\n"+TOM_DAVE_POINTS);
	}

	public static ArrayList<ArrayList<PointSet>> readMirexCosFile() {
		return readMIREXOutputFile(null,null);
	}

	/**
	 * Reads in a MIREX 2013 style COSIATEC output file (mirexCosFileName)
	 * and a MIREX 2013 style lisp format input file (lispInputFileName) and
	 * a boolean flag, convertToMorpheticPitch.
	 * 
	 * Returns a PointSet in which each point gives the onset time and
	 * morphetic pitch of the note in the lisp input file that corresponds
	 * to the note in the COSIATEC output file.
	 * 
	 * @param mirexCosFileName
	 * @param lispInputFileName
	 * @return
	 */
	public static ArrayList<ArrayList<PointSet>> readMIREXOutputFile(String mirexCosFileName, String lispInputFileName) {

		ArrayList<TomDavePoint> localTomDavePoints = readLispFileIntoPointSet(lispInputFileName==null?getLispInputFilePath(null):lispInputFileName);
		int extraPointVal = 1000000000;

		ArrayList<ArrayList<PointSet>> mirexOccurrenceSets = new ArrayList<ArrayList<PointSet>>();
		BufferedReader br;
		try {
			//			br = new BufferedReader(new FileReader(mirexCosFileName==null?getMIREXOutputFilePath(null):mirexCosFileName));
			//			int i = 0;
			//			for(String l = br.readLine(); l != null; l = br.readLine()) {
			//				i++;
			//				if (l.trim().length() > 0)
			//					mirexLines.add(l);
			//				if (i%25000==0) {
			//					System.out.println(i);
			//					System.out.flush();
			//				}
			//				if (i%500==0) {
			//					System.out.print(".");
			//					System.out.flush();
			//				}
			//			}
			//			br.close();
			//			if (lispInputFileName.equals("/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth//chopinOp24No4/polyphonic/lisp/mazurka24-4.txt")) {
			//				System.out.println("mirexCosFileName contains "+mirexLines.size()+" lines");
			//				System.out.flush();
			//			}

			br = new BufferedReader(new FileReader(mirexCosFileName==null?getMIREXOutputFilePath(null):mirexCosFileName));

			ArrayList<PointSet> occurrenceSet = null;
			PointSet occurrence = null;
			int i = 0;
			for(String line = br.readLine(); line != null; line = br.readLine()) {
				if (line.trim().length() > 0) {
					i++;
					if (i%25000==0) {
						System.out.println(i);
						System.out.flush();
					}
					if (i%500==0) {
						System.out.print("x");
						System.out.flush();
					}
					if (line.startsWith("pattern")) {
						if (occurrenceSet != null && occurrence != null) {
							occurrenceSet.add(occurrence);
							mirexOccurrenceSets.add(occurrenceSet);
						}
						occurrenceSet = new ArrayList<PointSet>();
						occurrence = null;
					} else if (line.startsWith("occurrence")) {
						if (occurrence != null)
							occurrenceSet.add(occurrence);
						occurrence = new PointSet();
					} else {
						int commaPos = line.indexOf(",");
						double mirexOnset = Double.parseDouble(line.substring(0,commaPos));
						double mirexMidi = Double.parseDouble(line.substring(commaPos+2));
						Point point = findPoint(mirexOnset,mirexMidi,localTomDavePoints);
						if (point != null)
							occurrence.add(point);
						else {
							occurrence.add(new Point(extraPointVal--,extraPointVal--));
						}
					}
				}
			}
			br.close();

			occurrenceSet.add(occurrence);
			mirexOccurrenceSets.add(occurrenceSet);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mirexOccurrenceSets;
	}

	public static String getMIREXOutputFilePath(String startDirectoryPath) {
		return getFileName("Choose MIREX output file", startDirectoryPath);
	}

	public static String getLispInputFilePath(String startDirectoryPath) {
		return getFileName("Choose JKU PDD lisp input file", startDirectoryPath);
	}

	public static String getMelCosFileName(String startDirectoryPath) {
		return getFileName("Choose MEL COSIATEC encoding file", startDirectoryPath);
	}

	public static String getOutputFileDirectoryName(String startDirectoryPath) {
		System.out.println("\ngetOutputFileDirectoryName called with startDirectoryPath = "+startDirectoryPath);
		return getFileName("Choose output file directory", startDirectoryPath, true);
	}

	public static String getFileName(String dialogTitle, String startDirectoryPath) {
		return getFileName(dialogTitle, startDirectoryPath, false);
	}

	public static String getFileName(String dialogTitle, String startDirectoryPath, boolean directoriesOnly) {
		JFileChooser chooser = new JFileChooser(startDirectoryPath == null?"./":startDirectoryPath);
		chooser.setDialogTitle(dialogTitle);
		if (directoriesOnly)
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) return null;
		return chooser.getSelectedFile().getAbsolutePath();	
	}

	public static Point findPoint(double mirexOnset, double mirexMidi, ArrayList<TomDavePoint> tomDavePoints) throws IllegalArgumentException {
		for(TomDavePoint tdp : tomDavePoints) {
			if (Math.abs((tdp.tomsNumerator * 1.0)/tdp.tomsDenominator - mirexOnset) <= 0.00001 &&
					(tdp.tomsPitch * 1.0 == mirexMidi))
				return new Point(tdp.davesOnset,tdp.davesPitch);
		}
		System.out.println("findPoint cannot find a corresponding tomDavePoint for ("+mirexOnset+","+mirexMidi+")");
		return null;
	}

	public static ArrayList<ArrayList<PointSet>> readMelCosiatecEncoding() {
		return readMelCosiatecEncoding(null);
	}

	public static ArrayList<ArrayList<PointSet>> readMelCosiatecEncoding(String melCosFileName) {

		ArrayList<String> tecStrings = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(melCosFileName==null?getMelCosFileName(null):melCosFileName));
			for(String l = br.readLine(); l != null; l = br.readLine())
				if (l.trim().length() > 0)
					tecStrings.add(l.trim());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Now make an array of occurrence sets for the tecs

		ArrayList<ArrayList<PointSet>> melOccurrenceSets = new ArrayList<ArrayList<PointSet>>();
		for(String tecString : tecStrings) {
			TEC tec = new TEC(tecString);
			ArrayList<PointSet> occurrenceSet = new ArrayList<PointSet>();
			for(Vector v : tec.getTranslators().getVectors())
				occurrenceSet.add(tec.getPattern().translate(v));
			melOccurrenceSets.add(occurrenceSet);
		}

		return melOccurrenceSets;

	}
}
