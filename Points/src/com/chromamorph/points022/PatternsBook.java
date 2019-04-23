package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;

import org.apache.commons.io.IOUtils;

import com.chromamorph.notes.Notes;
import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class PatternsBook {

	static Calendar cal = null;

	static String outputRootFolder = "output/PATTERNS-BOOK";

	static PrintWriter logFile = null; 

	static String JKUPDDOutputRootFolder = outputRootFolder + "/JKU-PDD";
	static String targetJKUPDDOutputFolder = JKUPDDOutputRootFolder+"/mirex-output";
	static String sourceJKUPDDOutputFolder = JKUPDDOutputRootFolder+"/output";
	static String JKUPDDMatlabResultsFolder = JKUPDDOutputRootFolder+"/matlab-results";

	static String nlbInputRootFolder = "data/PATTERNS-BOOK/NLB/nlb_datasets";
	static String nlbInputKernFolder = nlbInputRootFolder + "/annkrn";
	static String nlbInputMidiFolder = nlbInputRootFolder + "/annmidi";
	static String nlbOutputRootFolder = outputRootFolder+"/NLB";
	static String nlbInputMIDIPairFileFolder = nlbOutputRootFolder+"/NLB-PAIR-FILES-MIDI";
	static String nlbInputKernPairFileFolder = nlbOutputRootFolder+"/NLB-PAIR-FILES-KERN";

	static ArrayList<String> nlbMIDIFilePathStrings = null;
	static ArrayList<String> nlbKernFilePathStrings = null;

	static ArrayList<String> nlbInputMIDIPairFilePathStrings = null;
	static ArrayList<String> nlbInputKernPairFilePathStrings = null;

	static ArrayList<String> omnisiaArgStrings = null;

	static TreeMap<String, String> algorithmFileExtensionMap = new TreeMap<String, String>();

	static int count = 0, maxCount = 2;


	public static String getAlgorithmOutputFolder(String argString) {
		return argString.replace("-", "").trim().replace(" ", "-");
	}

	public static void compressSingleNLBFiles() throws MissingTieStartNoteException {
		for(String argString : omnisiaArgStrings) {
			//			if (count++ == maxCount) break;
			String algorithmOutputDirectoryPath = nlbOutputRootFolder + "/" + getAlgorithmOutputFolder(argString);
			for(String nlbFilePathString : nlbMIDIFilePathStrings) {
				String inputFileString = "-i "+nlbFilePathString;
				String outputDirectoryString = "-o " + algorithmOutputDirectoryPath+"/midi/singleFiles";
				String completeArgString = inputFileString + " " +outputDirectoryString + " "+ argString;
				OMNISIA.main(completeArgString.split(" "));
			}
			for(String nlbFilePathString : nlbKernFilePathStrings) {
				String inputFileString = "-i "+nlbFilePathString;
				String outputDirectoryString = "-o " + algorithmOutputDirectoryPath+"/kern/singleFiles";
				String completeArgString = inputFileString + " "+outputDirectoryString + " "+ argString;
				OMNISIA.main(completeArgString.split(" "));
			}
		}
	}

	public static void generateNLBKernFilePathStrings() {
		String[] nameList = new File(nlbInputKernFolder).list();
		nlbKernFilePathStrings = new ArrayList<String>();
		for(String name : nameList) {
			if (name.endsWith(".krn"))
				nlbKernFilePathStrings.add(nlbInputKernFolder+"/"+name);
		}
		logln(nlbKernFilePathStrings.size() + " NLB kern file path strings");
	}

	public static void generateNLBMIDIFilePathStrings() {
		String[] nameList = new File(nlbInputMidiFolder).list();
		nlbMIDIFilePathStrings = new ArrayList<String>();
		for(String name : nameList) {
			if (name.endsWith(".mid"))
				nlbMIDIFilePathStrings.add(nlbInputMidiFolder+"/"+name);
		}
		logln(nlbMIDIFilePathStrings.size() + " NLB MIDI file path strings");
	}

	public static void generateNLBPairFilesFromMIDI() throws InvalidMidiDataException, IOException {
		for(int i = 0; i < nlbMIDIFilePathStrings.size()-1; i++)
			for(int j = i+1; j < nlbMIDIFilePathStrings.size(); j++) {
				Notes notes1 = Notes.fromMIDI(nlbMIDIFilePathStrings.get(i),true);
				Notes notes2 = Notes.fromMIDI(nlbMIDIFilePathStrings.get(j),true);
				notes2.translateInTime(notes1.getMaxTimePoint()*2);
				Notes notes = new Notes();
				notes.addAll(notes1);
				notes.addAll(notes2);
				String fileName1 = Paths.get(nlbMIDIFilePathStrings.get(i)).getFileName().toString();
				String fileNameWithoutExtension1 = fileName1.substring(0,fileName1.lastIndexOf("."));
				String fileName2 = Paths.get(nlbMIDIFilePathStrings.get(j)).getFileName().toString();				
				String fileNameWithoutExtension2 = fileName2.substring(0,fileName2.lastIndexOf("."));
				String pairFileName = fileNameWithoutExtension1+"-"+fileNameWithoutExtension2+".opnd";
				String opndFilePathString = nlbInputMIDIPairFileFolder+"/"+pairFileName;
				notes.toOPNDFile(opndFilePathString);
			}
	}

	public static void generateNLBPairFilesFromKern() throws IOException, MissingTieStartNoteException {
		for(int i = 0; i < nlbKernFilePathStrings.size()-1; i++)
			for(int j = i+1; j < nlbKernFilePathStrings.size(); j++) {
				Notes notes1 = Notes.fromKern(nlbKernFilePathStrings.get(i));
				Notes notes2 = Notes.fromKern(nlbKernFilePathStrings.get(j));
				notes2.translateInTime(notes1.getMaxTimePoint()*2);
				Notes notes = new Notes();
				notes.addAll(notes1);
				notes.addAll(notes2);
				String fileName1 = Paths.get(nlbKernFilePathStrings.get(i)).getFileName().toString();
				String fileNameWithoutExtension1 = fileName1.substring(0,fileName1.lastIndexOf("."));
				String fileName2 = Paths.get(nlbKernFilePathStrings.get(j)).getFileName().toString();				
				String fileNameWithoutExtension2 = fileName2.substring(0,fileName2.lastIndexOf("."));
				String pairFileName = fileNameWithoutExtension1+"-"+fileNameWithoutExtension2+".opnd";
				String opndFilePathString = nlbInputKernPairFileFolder+"/"+pairFileName;
				notes.toOPNDFile(opndFilePathString);
			}
	}

	public static void generateNLBPairFiles() throws InvalidMidiDataException, IOException, MissingTieStartNoteException {
		logln("Generating input NLB pair files from MIDI");
		generateNLBPairFilesFromMIDI();
		logln("Generating input NLB pair files from Kern");
		generateNLBPairFilesFromKern();
	}

	public static void generateNLBPairFilePathStrings() {
		String[] kernFileNames = new File(nlbInputKernPairFileFolder).list();
		nlbInputKernPairFilePathStrings = new ArrayList<String>();
		for(String fileName : kernFileNames)
			if (fileName.endsWith(".opnd")) {
				String pathString = nlbInputKernPairFileFolder + "/"+fileName;
				nlbInputKernPairFilePathStrings.add(pathString);
//				log("\rKern pair file path string added: "+pathString);
			}
		logln(nlbInputKernPairFilePathStrings.size()+" NLB input kern pair file path strings");

		nlbInputMIDIPairFilePathStrings = new ArrayList<String>();
		String[] midiFileNames = new File(nlbInputMIDIPairFileFolder).list();
		for(String fileName : midiFileNames)
			if (fileName.endsWith(".opnd")) {
				String pathString = nlbInputMIDIPairFileFolder + "/"+fileName;
				nlbInputMIDIPairFilePathStrings.add(pathString);
//				log("\rMIDI pair file path string added: "+pathString);
			}
		logln(nlbInputMIDIPairFilePathStrings.size()+" NLB input MIDI pair file path strings");
	}

	public static void compressNLBPairFiles() throws MissingTieStartNoteException {
		for(String argString : omnisiaArgStrings) {
			String algorithmOutputDirectoryPath = nlbOutputRootFolder + "/" + getAlgorithmOutputFolder(argString);
			for(String nlbFilePathString : nlbInputMIDIPairFilePathStrings) {
				String inputFileString = "-i "+nlbFilePathString;
				String outputDirectoryPath = algorithmOutputDirectoryPath+"/midi/pairFiles";
				String outputDirectoryString = "-o " + outputDirectoryPath;
				String completeArgString = inputFileString + " " +outputDirectoryString + " "+ argString;
				OMNISIA.main(completeArgString.split(" "));
			}
			for(String nlbFilePathString : nlbInputKernPairFilePathStrings) {
				String inputFileString = "-i "+nlbFilePathString;
				String outputDirectoryPath = algorithmOutputDirectoryPath+"/kern/pairFiles";				
				String outputDirectoryString = "-o " + outputDirectoryPath;
				String completeArgString = inputFileString + " "+outputDirectoryString + " "+ argString;
				OMNISIA.main(completeArgString.split(" "));
			}
		}
	}

	static class NLBDistance implements Comparable<NLBDistance> {
		private String piece1, piece2;
		private double distance;

		NLBDistance(String piece1, String piece2, double distance) {
			setPiece1(piece1);
			setPiece2(piece2);
			setDistance(distance);
		}

		public String getPiece1() {
			return piece1;
		}

		public void setPiece1(String piece1) {
			this.piece1 = piece1;
		}

		public String getPiece2() {
			return piece2;
		}

		public void setPiece2(String piece2) {
			this.piece2 = piece2;
		}

		public double getDistance() {
			return distance;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}

		@Override
		public int compareTo(NLBDistance o) {
			if (o == null) return 1;
			int d = (int)Math.signum(o.getDistance()-getDistance());
			if (d != 0) return d;
			d = getPiece1().compareTo(o.getPiece1());
			if (d != 0) return d;
			return getPiece2().compareTo(o.getPiece2());
		}

		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof NLBDistance)) return false;
			return compareTo((NLBDistance)obj)==0;
		}

	}

	static public class EncodingLengthNotFoundException extends Exception {

		private static final long serialVersionUID = -6422578318559497161L;

		public EncodingLengthNotFoundException(String filePathString) {
			super("Encoding length not found in file "+filePathString);
		}

		public EncodingLengthNotFoundException(String filePathString, boolean entryFound) {
			super("Encoding length not found in file "+filePathString+", though encodingLength entry was found!!!");

		}

	}

	private static int getZFromFile(String filePathString) throws IOException, EncodingLengthNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(new File(filePathString)));
		String l = br.readLine();
		while (l != null && !l.split(" ")[0].equals("encodingLength"))
			l = br.readLine();
		br.close();
		if (l == null)
			throw new EncodingLengthNotFoundException(filePathString);
		return Integer.parseInt(l.split(" ")[1]);
	}

	public static void computeNLBDistances() throws IOException, EncodingLengthNotFoundException {
		for(String format : new String[]{"kern","midi"})
			for(String algorithm : omnisiaArgStrings) {
				TreeSet<NLBDistance> distances = new TreeSet<NLBDistance>();
				for(String inputPairFilePathString : nlbInputMIDIPairFilePathStrings) {
					String inputPairFileName = Paths.get(inputPairFilePathString).getFileName().toString(); 
					String piece1 = inputPairFileName.substring(0,12);
					String piece2 = inputPairFileName.substring(13,25);
					String pair = inputPairFileName.substring(0,25);
					String algorithmName = algorithm.substring(2,algorithm.indexOf("-",2)).trim();
					String encodingFileExtension = algorithmFileExtensionMap.get(algorithmName);
					if (encodingFileExtension == null)
						System.out.println("Encoding file extension is null when algorithm is " + algorithmName);
					String formatSuffix = format.equals("kern")?"krn":"mid";
					String algorithmOutputFolderName = getAlgorithmOutputFolder(algorithm);
					//					Get file path for file for piece 1 containing encoding length
					String pieceFilePath1 = nlbOutputRootFolder+"/"+algorithmOutputFolderName+"/"+format+"/singleFiles/"+piece1+"-"+formatSuffix+"/"+piece1+"-diat."+encodingFileExtension;
					String pieceFilePath2 = nlbOutputRootFolder+"/"+algorithmOutputFolderName+"/"+format+"/singleFiles/"+piece2+"-"+formatSuffix+"/"+piece2+"-diat."+encodingFileExtension;
					String pairFilePath = nlbOutputRootFolder+"/"+algorithmOutputFolderName+"/"+format+"/pairFiles/"+pair+"-opnd"+"/"+pair+"-diat."+encodingFileExtension;
					//					Z is compressed encoding length
					int zX = getZFromFile(pieceFilePath1);
					int zY = getZFromFile(pieceFilePath2);
					int zXY = getZFromFile(pairFilePath);
					double eZXY = (1.0 * (zXY - Math.min(zX,zY)))/(1.0 * Math.max(zX,zY));
					distances.add(new NLBDistance(piece1, piece2, eZXY));
				}
			}
	}

	public static void runNLB() throws MissingTieStartNoteException, InvalidMidiDataException, IOException, EncodingLengthNotFoundException {
		logln("\nRunning NLB experiment");
		generateNLBKernFilePathStrings();

		generateNLBMIDIFilePathStrings();

//		compressSingleNLBFiles();

//		generateNLBPairFiles();

		generateNLBPairFilePathStrings();

		compressNLBPairFiles();
		//		Find distances between all pairs of nlb files
//		computeNLBDistances();
		//		Classify nlb files using 1-nn and leave-one-out cross validation
	}

	public static void moveJKUOutputFilesForMatlabEvalScript() throws IOException {
		String[] sourceOutputFolderFileList = new File(sourceJKUPDDOutputFolder).list();
		for(String s : sourceOutputFolderFileList) System.out.println(s);
		for(String algFolder : sourceOutputFolderFileList) {
			if (algFolder.startsWith(".")) continue;
			System.out.println(algFolder);
			String sourceAlgFolderPath = sourceJKUPDDOutputFolder+"/"+algFolder;
			File sourceAlgFolder = new File(sourceAlgFolderPath);
			String targetAlgFolderPath = targetJKUPDDOutputFolder+"/"+algFolder;
			File targetAlgFolder = new File(targetAlgFolderPath);
			targetAlgFolder.mkdirs();
			String[] pieceFolders = sourceAlgFolder.list();
			for(String pieceFolder : pieceFolders) {
				System.out.println("  "+pieceFolder);
				String sourcePieceFolderPath = sourceAlgFolderPath+"/"+pieceFolder;
				String sourcePieceFileName = null;
				if (algFolder.startsWith("COSIATEC")) {
					sourcePieceFileName = pieceFolder.substring(0,pieceFolder.length()-4)+"-diat.cos";
					FileReader sourcePieceFile = new FileReader(sourcePieceFolderPath+"/"+sourcePieceFileName);
					FileWriter targetPieceFile = new FileWriter(targetAlgFolderPath+"/"+getTargetPieceFileName(sourcePieceFileName));
					IOUtils.copy(sourcePieceFile, targetPieceFile);
					sourcePieceFile.close();
					targetPieceFile.close();
					//					Runtime file
					String sourceLogFileName = pieceFolder.substring(0,pieceFolder.length()-4)+"-diat.log";
					BufferedReader logFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourceLogFileName));
					PrintWriter runtimeFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetRuntimeFileName(sourcePieceFileName)));
					String l = null;
					while ((l = logFile.readLine()) != null)
						if (l.startsWith("Running time: ")) {
							String[] a = l.split(" ");
							runtimeFile.println("runtime "+a[2]);
							runtimeFile.println("FRT 0");
							break;
						}
					logFile.close();
					runtimeFile.close();
				} else if (algFolder.startsWith("Forth")) {
					String[] algFolderFileList = new File(sourcePieceFolderPath).list();
					for(String algFolderFile : algFolderFileList)
						if (algFolderFile.endsWith(".Forth")) {
							sourcePieceFileName = algFolderFile;
							break;
						}	
					FileReader sourcePieceFile = new FileReader(sourcePieceFolderPath+"/"+sourcePieceFileName);
					FileWriter targetPieceFile = new FileWriter(targetAlgFolderPath+"/"+getTargetPieceFileName(sourcePieceFileName));
					IOUtils.copy(sourcePieceFile, targetPieceFile);
					sourcePieceFile.close();
					targetPieceFile.close();
					//					Runtime file
					String sourceLogFileName = pieceFolder.substring(0,pieceFolder.length()-4)+".alltecs";
					BufferedReader logFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourceLogFileName));
					PrintWriter runtimeFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetRuntimeFileName(sourcePieceFileName)));
					String l = null;
					while ((l = logFile.readLine()) != null)
						if (l.startsWith("Running time (ms): ")) {
							String[] a = l.split(" ");
							runtimeFile.println("runtime "+a[3]);
							runtimeFile.println("FRT 0");
							break;
						}
					logFile.close();
					runtimeFile.close();
				} else if (algFolder.startsWith("SIATECCompress")) {
					sourcePieceFileName = pieceFolder.substring(0,pieceFolder.length()-4)+".SIATECCompress";
					BufferedReader sourcePieceFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourcePieceFileName));
					PrintWriter targetPieceFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetPieceFileName(sourcePieceFileName)));
					String l = null;
					while ((l = sourcePieceFile.readLine())!= null && !l.isEmpty())
						targetPieceFile.println(l);
					sourcePieceFile.close();
					targetPieceFile.close();
					//					Runtime file
					String sourceLogFileName = pieceFolder.substring(0,pieceFolder.length()-4)+".log";
					BufferedReader logFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourceLogFileName));
					PrintWriter runtimeFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetRuntimeFileName(sourcePieceFileName)));
					l = null;
					while ((l = logFile.readLine()) != null)
						if (l.startsWith("Running time: ")) {
							String[] a = l.split(" ");
							runtimeFile.println("runtime "+a[2]);
							runtimeFile.println("FRT 0");
							break;
						}
					logFile.close();
					runtimeFile.close();
				} else if (algFolder.startsWith("RecurSIA")) {
					sourcePieceFileName = pieceFolder.substring(0,pieceFolder.length()-4)+"-diat.RecurSIA";
					BufferedReader sourcePieceFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourcePieceFileName));
					PrintWriter targetPieceFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetPieceFileName(sourcePieceFileName)));
					String l = null;
					while ((l = sourcePieceFile.readLine())!= null && !l.isEmpty())
						targetPieceFile.println(l);
					sourcePieceFile.close();
					targetPieceFile.close();
					//					Runtime file
					String sourceLogFileName = pieceFolder.substring(0,pieceFolder.length()-4)+"-diat.log";
					BufferedReader logFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourceLogFileName));
					PrintWriter runtimeFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetRuntimeFileName(sourcePieceFileName)));
					l = null;
					while ((l = logFile.readLine()) != null)
						if (l.startsWith("Running time: ")) {
							String[] a = l.split(" ");
							runtimeFile.println("runtime "+a[2]);
							runtimeFile.println("FRT 0");
							break;
						}
					logFile.close();
					runtimeFile.close();
				}
			}
		}

	}

	public static String getTargetPieceFileName(String sourcePieceFileName) {
		if (sourcePieceFileName.startsWith("mazurka")) return "chop_mazurka24-4.txt";
		if (sourcePieceFileName.startsWith("silverswan")) return "gbns_silverswan.txt";
		if (sourcePieceFileName.startsWith("sonata01-3")) return "beet_sonata01-3.txt";
		if (sourcePieceFileName.startsWith("sonata04-2")) return "mzrt_sonata04-2.txt";
		if (sourcePieceFileName.startsWith("wtc2f20")) return "bach_wtc2f20.txt";
		return null;
	}

	public static String getTargetRuntimeFileName(String sourcePieceFileName) {
		if (sourcePieceFileName.startsWith("mazurka")) return "chop_mazurka24-4_runtime.txt";
		if (sourcePieceFileName.startsWith("silverswan")) return "gbns_silverswan_runtime.txt";
		if (sourcePieceFileName.startsWith("sonata01-3")) return "beet_sonata01-3_runtime.txt";
		if (sourcePieceFileName.startsWith("sonata04-2")) return "mzrt_sonata04-2_runtime.txt";
		if (sourcePieceFileName.startsWith("wtc2f20")) return "bach_wtc2f20_runtime.txt";
		return null;
	}

	public static void getFolderListForJKUPDDMatlabScript() {
		String targetOutputFolder = targetJKUPDDOutputFolder;
		String[] folderList = new File(targetOutputFolder).list();
		for(String s : folderList)
			System.out.println("fullfile(outputDir, \'"+s+"\'),...");
	}

	public static void makeMatlabScriptResultsTable() throws IOException {
		String resultsFolder = JKUPDDMatlabResultsFolder;
		BufferedReader input = new BufferedReader(new FileReader(resultsFolder+"/results.csv"));
		PrintWriter fFile = new PrintWriter(new File(resultsFolder+"/tlf1.csv"));
		PrintWriter rFile = new PrintWriter(new File(resultsFolder+"/tlr.csv"));
		PrintWriter pFile = new PrintWriter(new File(resultsFolder+"/tlp.csv"));
		PrintWriter rtFile = new PrintWriter(new File(resultsFolder+"/rt.csv"));
		String headers = "Algorithm, Bach, Beet, Chop, Gbns, Mzrt";
		fFile.println(headers);
		rFile.println(headers);
		pFile.println(headers);
		rtFile.println(headers);
		String l = input.readLine();
		for(int alg = 0; alg < 3; alg++) {
			for(int piece = 0; piece < 5; piece++) {
				l = input.readLine(); 
				String[] a = l.split(",");				
				if (piece==0) {
					fFile.print(a[0].trim());
					rFile.print(a[0].trim());
					pFile.print(a[0].trim());
					rtFile.print(a[0].trim());
				}
				fFile.print(", "+a[12].trim());
				rFile.print(", "+a[11].trim());
				pFile.print(", "+a[10].trim());
				rtFile.print(", "+a[13].trim());

			}
			fFile.println();
			rFile.println();
			pFile.println();
			rtFile.println();
		}
		input.close();
		fFile.close();
		rFile.close();
		pFile.close();
		rtFile.close();
	}

	public static void generateMatlabResults() {
		//		moveOutputFiles();
		//		getFolderListForJKUPDDMatlabScript();
		//		Then run Matlab script
		//		Change number of algorithms before running next method.
		//		makeMatlabScriptResultsTable();
	}

	public static void generateJKUPDDResults() throws IOException {
		//		generateMatlabResults();


	}

	/**
	 * Following generates the OMNISIA arg strings that should replicate
	 * the results on the NLB published in FMA14 paper.
	 */
//	public static void generateOMNISIAArgStrings() {
//		omnisiaArgStrings = new ArrayList<String>();
//		for(String basicAlgorithm : new String[]{"COSIATEC", "SIATECCompress", "Forth", "RecurSIA"}) {
//			for(boolean compactnessTrawler : new boolean[]{true, false}) {
//				for(boolean rSuperdiagonals : new boolean[]{true, false}) {
//					for(String recAlg : basicAlgorithm.equals("RecurSIA")?new String[]{"COSIATEC", "SIATECCompress", "Forth"}:new String[]{"COSIATEC"}) {
//						String argString = 
//								"-a "+basicAlgorithm
//								+ " -d"
//								+ (compactnessTrawler?" -ct -cta 0.66 -ctb 3":"")
//								+ (rSuperdiagonals?" -rsd -r 3":"")
//								+ (basicAlgorithm.equals("RecurSIA")?" -recalg "+recAlg:"")
//								+ " -no10 -nodate";
//						omnisiaArgStrings.add(argString);
//					}				
//				}
//			}
//		}
//		logln(omnisiaArgStrings.size()+" OMNISIA arg strings generated");
//	}
	
	public static void generateOMNISIAArgStrings() {
		omnisiaArgStrings = new ArrayList<String>();
		for(String basicAlgorithm : new String[]{
				"COSIATEC"
//				"SIATECCompress", 
//				"Forth", 
//				"RecurSIA"
				}) {
//			for(boolean compactnessTrawler : new boolean[]{true, false}) {
//				for(boolean rSuperdiagonals : new boolean[]{true, false}) {
					for(String recAlg : basicAlgorithm.equals("RecurSIA")?new String[]{"COSIATEC", "SIATECCompress", "Forth"}:new String[]{"COSIATEC"}) {
						String argString = 
								"-a "+basicAlgorithm
								+ " -d"
//								+ (compactnessTrawler?" -ct -cta 0.66 -ctb 3":"")
//								+ (rSuperdiagonals?" -rsd -r 3":"")
								+ (basicAlgorithm.equals("RecurSIA")?" -recalg "+recAlg:"")
								+ " -no10 -nodate";
						omnisiaArgStrings.add(argString);
					}				
//				}
//			}
		}
		logln(omnisiaArgStrings.size()+" OMNISIA arg strings generated");		
	}

	//	public static void generateOMNISIAArgStrings() {
	//		omnisiaArgStrings = new ArrayList<String>();
	//		for(String basicAlgorithm : new String[]{"COSIATEC", "SIATECCompress", "Forth", "RecurSIA"})
	//			for(boolean diatonicPitch : new boolean[]{true,false})
	//				for(boolean compactnessTrawler : new boolean[]{true, false}) 
	//					for (boolean rSuperdiagonals : new boolean[]{true, false}) {
	//						for(int r : rSuperdiagonals?new int[]{1,3}:(new int[]{1}))
	//							for (boolean rrt : new boolean[]{true,false})
	//								for(double minTecCompactness : new double[]{0,0.5,0.9})
	//									for(int minPatternSize : new int[]{0,3}) {
	//										for(String recAlg : basicAlgorithm.equals("RecurSIA")?new String[]{"COSIATEC", "SIATECCompress", "Forth"}:new String[]{"COSIATEC"}) {
	//											String argString = 
	//													"-a "+basicAlgorithm
	//													+ (diatonicPitch?" -d":"")
	//													+ (compactnessTrawler?" -ct":"")
	//													+ (rSuperdiagonals?" -rsd -r "+r:"")
	//													+ (rrt?" -rrt":"")
	//													+ " -minc "+minTecCompactness
	//													+ " -min "+minPatternSize
	//													+ (basicAlgorithm.equals("RecurSIA")?" -recalg "+recAlg:"")
	//													+ " -no10 -nodate";
	//											omnisiaArgStrings.add(argString);
	//										}
	//									}
	//					}
	//		logln(omnisiaArgStrings.size()+" OMNISIA parameter value combinations to be tested");
	//	}

	public static void runExperiments() throws MissingTieStartNoteException, InvalidMidiDataException, IOException, EncodingLengthNotFoundException {
		generateOMNISIAArgStrings();
		runNLB();
	}

	public static void generateResults() throws IOException {
		generateJKUPDDResults();
	}

	public static String getTimeString() {
		cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR)+"-"+String.format("%02d", 1+cal.get(Calendar.MONTH))+"-"+String.format("%02d", cal.get(Calendar.DATE))+"-"+String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))+"-"+String.format("%02d", cal.get(Calendar.MINUTE))+"-"+String.format("%02d", cal.get(Calendar.SECOND))+"-"+String.format("%03d", cal.get(Calendar.MILLISECOND));
	}

	public static void makeLogFile() throws FileNotFoundException {
		String logFolder = outputRootFolder+"/logs";
		new File(logFolder).mkdirs();
		logFile = new PrintWriter(logFolder+"/"+getTimeString()+"-PATTERNS-BOOK.log");
		logln("Log file created at "+getTimeString());
	}

	public static void closeLogFile() {
		logFile.close();
	}

	public static void logln(Object obj) {
		System.out.println(obj);
		if (logFile != null) logFile.println(obj);
	}

	public static void log(Object obj) {
		System.out.print(obj);
		if (logFile != null) logFile.print(obj);
	}

	public static void createAlgorithmFileExtensionMap() {
		algorithmFileExtensionMap.put("COSIATEC", "cos");
		algorithmFileExtensionMap.put("Forth", "Forth");
		algorithmFileExtensionMap.put("SIATECCompress", "SIATECCompress");
		algorithmFileExtensionMap.put("RecurSIA", "RecurSIA");
	}

	public static void main(String[] args) throws IOException, EncodingLengthNotFoundException {
		try {
			makeLogFile();
			createAlgorithmFileExtensionMap();
			runExperiments();
			//		generateResults();
			closeLogFile();
		} catch (MissingTieStartNoteException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
}
