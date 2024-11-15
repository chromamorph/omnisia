package com.chromamorph.points022;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;


public class MTECSEncoding extends Encoding {

//	private int minMtpSize = 0;

	public MTECSEncoding(
			String inputFilePath,
			String outputDirectoryPath,
			boolean forRSuperdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(null,
				inputFilePath,
				outputDirectoryPath,
				0,
				PitchRepresentation.MORPHETIC_PITCH,
				false, //drawOutput
				false, //verbose
				forRSuperdiagonals, r,
				withCompactnessTrawler, a, b);
	}

	public MTECSEncoding(String inputFilePathName, String outputDirectoryPathName, Integer minPatternSize, PitchRepresentation pitchRepresentation, boolean drawOutput) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(null, 
				inputFilePathName, 
				outputDirectoryPathName, 
				minPatternSize, 
				pitchRepresentation, 
				drawOutput, 
				false, //verbose
				false, 0,
				false, 0.0, 0);
	}

	public MTECSEncoding(PointSet pointSet, 
			String inputFilePathNameOrOutputFileName, 
			String outputDirectoryPathName, 
			Integer minPatternSize,
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean verbose,
			boolean forRSuperdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(pointSet, 
				inputFilePathNameOrOutputFileName, 
				outputDirectoryPathName, 
				minPatternSize,
				0,
				pitchRepresentation, 
				drawOutput, 
				verbose,
				forRSuperdiagonals, r,
				withCompactnessTrawler, a, b);
	}

	public MTECSEncoding(PointSet pointSet, 
			String inputFilePathNameOrOutputFileName, 
			String outputDirectoryPathName, 
			Integer minPatternSize,
			Integer maxPatternSize,
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean verbose,
			boolean forRSuperdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(pointSet, 
				inputFilePathNameOrOutputFileName, 
				outputDirectoryPathName, 
				minPatternSize,
				maxPatternSize,
				pitchRepresentation, 
				drawOutput, 
				verbose,
				forRSuperdiagonals, r,
				withCompactnessTrawler, a, b,
				false, //Not called from OMNISIA
				false, false, false,
				null,
				0,
				true, //withoutChannel10
				false //removeRedundantTranslators
				);

	}

	public MTECSEncoding(PointSet pointSet, 
			String inputFilePathNameOrOutputFileName, 
			String outputDirectoryPathName, 
			Integer minPatternSize,
			Integer maxPatternSize,
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean verbose,
			boolean forRSuperdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b,
			boolean fromOMNISIA,
			boolean mirex, boolean segmentMode, boolean bbMode,
			String omnisiaOutputFilePath,
			int topNPatterns,
			boolean withoutChannel10,
			boolean removeRedundantTranslators) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(pointSet, 
			inputFilePathNameOrOutputFileName, 
			outputDirectoryPathName, 
			minPatternSize,
			maxPatternSize,
			pitchRepresentation, 
			drawOutput, 
			verbose,
			forRSuperdiagonals, r,
			withCompactnessTrawler, a, b,
			fromOMNISIA,
			mirex, segmentMode, bbMode,
			omnisiaOutputFilePath,
			topNPatterns,
			withoutChannel10,
			removeRedundantTranslators,
			false // calledFromForthEncoding
				);
	}
	
	public MTECSEncoding(PointSet pointSet, 
			String inputFilePathNameOrOutputFileName, 
			String outputDirectoryPathName, 
			Integer minPatternSize,
			Integer maxPatternSize,
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean verbose,
			boolean forRSuperdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b,
			boolean fromOMNISIA,
			boolean mirex, boolean segmentMode, boolean bbMode,
			String omnisiaOutputFilePath,
			int topNPatterns,
			boolean withoutChannel10,
			boolean removeRedundantTranslators,
			boolean calledFromForthEncoding) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		super(
				pointSet,
				inputFilePathNameOrOutputFileName,
				outputDirectoryPathName,
				pitchRepresentation.equals(PitchRepresentation.MORPHETIC_PITCH)?true:false,
				withoutChannel10,
				"SIATEC",
				topNPatterns,
				mirex,
				segmentMode,
				bbMode,
				omnisiaOutputFilePath
				);
		if (pointSet.isEmpty())
			System.out.println(">>>>>pointSet is empty in MTECSEncoding constructor<<<<<");
		if (pointSet.size()==1)
			System.out.println(">>>> pointSet has size 1 in MTECSEncoding constructor <<<<<<");
		long startTime = System.currentTimeMillis();
		VectorPointPair[][] vectorTable = SIA.computeVectorTable(dataset, false);
		
//		System.out.println("vectorTable");
//		for(int i = 0; i < vectorTable.length; i++) {
//			System.out.println(" ");
//			for(int j = 0; j < vectorTable[0].length; j++) {
//				System.out.print(String.format("%30s", vectorTable[i][j]));
//			}
//			System.out.println();
//		}
//		System.out.println();
		
		ArrayList<MtpCisPair> mtpCisPairs = SIA.run(
				dataset,
				vectorTable,
				forRSuperdiagonals, r,
				withCompactnessTrawler, a, b,
				null, //logPrintStream
				false, //remove tran equiv mtps
				false, //merge vectors
				minPatternSize, //
				maxPatternSize,
				true // Includes negative vector MTPs
				);
		
//		System.out.println("mtpCisPairs");
//		for(MtpCisPair mcp : mtpCisPairs) {
//			System.out.println(" "+mcp);
//		}
//		
		ArrayList<TEC> mtpTecs = new ArrayList<TEC>();
		for(MtpCisPair mcp : mtpCisPairs) {
			mtpTecs.add(new TEC(mcp.getMtp(),mcp.getVectorSet(),dataset));
		}
		ArrayList<ArrayList<TEC>> mtecs = new ArrayList<ArrayList<TEC>>();
		mtecs.add(mtpTecs);
		ArrayList<TEC> prevMtecs = mtpTecs;
		for(int numVecs = 2; numVecs <= mtpTecs.size(); numVecs++) {
			ArrayList<TEC> nextMtecs = new ArrayList<TEC>();
			for (int i = 0; i < mtpTecs.size(); i++) {
				for (int j = 0; j < prevMtecs.size(); j++) {
					PointSet newPattern = mtpTecs.get(i).getPattern().intersection(prevMtecs.get(j).getPattern());
					VectorSet newTranslators = mtpTecs.get(i).getTranslators().copy();
					newTranslators.addAll(prevMtecs.get(j).getTranslators());
					if (newPattern.size() <= maxPatternSize && newPattern.size() >= minPatternSize) {
						nextMtecs.add(new TEC(newPattern,newTranslators,dataset));
					}
				}
			}
			if (nextMtecs.isEmpty())
				break;
			mtecs.add(nextMtecs);
			prevMtecs = nextMtecs;
		}

		for(int nv = 0; nv < mtecs.size(); nv++) {
			System.out.println(mtecs.get(nv).size()+" mtecs for nv = "+(nv+1));
			for(TEC mtec : mtecs.get(nv)) {
				System.out.println("  "+mtec+" "+mtec.getMaxVecs());
			}
		}
		
		
//		What are the distinct MTECs and for which maxVec sets are they maximal?
//		TreeSet<TEC> distinctMtecs = new TreeSet<TEC>();
//		for(ArrayList<TEC> mtecsForThisNv : mtecs) {
//			for(TEC mtec : mtecsForThisNv) {
//				TEC foundTec = null;
//				if (distinctMtecs.contains(mtec)) 
//					foundTec = distinctMtecs.floor(mtec);
//				if (foundTec == null) {
//					distinctMtecs.add(mtec);
//				} else { // Found the same TEC already in distinctMtecs, so add this one's maxVecSet to the one already in distinctMtecs
//					foundTec.addMaxVecSet(mtec.getMaxVecs());
//				}
//			}
//		}
		
		
		
		ArrayList<TEC> mtecList = new ArrayList<TEC>();
		for(ArrayList<TEC> mtecsForThisNumVecs : mtecs) {
			mtecList.addAll(mtecsForThisNumVecs);
		}
		setTECs(mtecList);
//		PointSet coveredSet = new PointSet();
//		for(TEC tec : getTECs())
//			coveredSet.addAll(tec.getCoveredPoints());
//		PointSet residualPointSet = dataset.diff(coveredSet);
//		if (!residualPointSet.isEmpty())
//			getTECs().add(new TEC(residualPointSet,dataset));
		if (removeRedundantTranslators)
			removeRedundantTranslators();
		long endTime = System.currentTimeMillis();
		setRunningTime(endTime-startTime);
		if (!calledFromForthEncoding)
			writeToFile();
		
//		if (fromOMNISIA && outputDirectoryPathName != null && inputFilePathNameOrOutputFileName != null) {
//			String outputFileName = Paths.get(inputFilePathNameOrOutputFileName).getFileName().toString();
//			outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf('.'))+".SIATEC";
//			Path outputFilePath = Paths.get(outputDirectoryPathName).resolve(outputFileName);
//			File outputFile = outputFilePath.toFile();
//			PrintStream ps = null;
//			if (omnisiaOutputFilePath == null)
//				ps = new PrintStream(outputFile);
//			else
//				ps = new PrintStream(omnisiaOutputFilePath);
//			ps.println(this);
//			ps.close();
//		} else {
//			String outputFileName = null;
//			String minPatSizeString = "";
//			if (minPatternSize > 0)
//				minPatSizeString = minPatternSize.toString();
//			String pitchRepresentationString = (pitchRepresentation.equals(PitchRepresentation.CHROMATIC_PITCH)?"CP":"MP");
//			if (pointSet == null) {
//				int start = inputFilePathNameOrOutputFileName.lastIndexOf("/") + 1;
//				int end = inputFilePathNameOrOutputFileName.lastIndexOf(".");
//				String inputFileName = inputFilePathNameOrOutputFileName.substring(start,end);
//				if (!outputDirectoryPathName.endsWith("/"))
//					outputDirectoryPathName = outputDirectoryPathName + "/";
//				outputFileName = outputDirectoryPathName+inputFileName+(minPatternSize>0?"-":"")+minPatSizeString+"-"+pitchRepresentationString+".SIATEC";
//			} else if (outputDirectoryPathName != null && inputFilePathNameOrOutputFileName != null) {
//				outputFileName = outputDirectoryPathName+"/"+inputFilePathNameOrOutputFileName+(minPatternSize>0?"-":"")+minPatSizeString+"-"+pitchRepresentationString+".SIATEC";
//			}
//
//			if (outputFileName != null) {
//				File outputDirectory = new File(outputDirectoryPathName);
//				if (!outputDirectory.exists()) outputDirectory.mkdir();
//				PrintStream ps = new PrintStream(outputFileName);
//				ps.println(this);
//				ps.close();
//			}
//			if (verbose) System.out.println(this);
//		}
//		if (drawOutput)
//			draw();
	}

	//	private ArrayList<MtpCisPair> computeMtpCisPairs(VectorPointPair[][] vectorTable) {
	//		System.out.print("computeMtpCisPairs...");
	//		TreeSet<VectorPointPair> sortedSIAVectorTable = new TreeSet<VectorPointPair>();
	//
	//		for(int i = 0; i < vectorTable.length; i++)
	//			for(int j = i+1; j < vectorTable.length; j++)
	//				sortedSIAVectorTable.add(vectorTable[i][j]);
	//
	//		ArrayList<PointSet> MTPs = new ArrayList<PointSet>();
	//		ArrayList<ArrayList<Integer>> CISs = new ArrayList<ArrayList<Integer>>();
	//		VectorPointPair firstVP = sortedSIAVectorTable.first(); 
	//		Vector v = firstVP.getVector();
	//		PointSet mtp = new PointSet();
	//		ArrayList<Integer> cis = new ArrayList<Integer>();
	//		mtp.add(firstVP.getPoint());
	//		cis.add(firstVP.getIndex());
	//		NavigableSet<VectorPointPair> rest = sortedSIAVectorTable.tailSet(firstVP, false); 
	//		for(VectorPointPair vp : rest) {
	//			if (vp.getVector().equals(v)) {
	//				mtp.add(vp.getPoint());
	//				cis.add(vp.getIndex());
	//			} else {
	//				if (mtp.size() >= minMtpSize) {
	//					MTPs.add(mtp);
	//					CISs.add(cis);
	//				}
	//				mtp = new PointSet();
	//				cis = new ArrayList<Integer>();
	//				v = vp.getVector();
	//				mtp.add(vp.getPoint());
	//				cis.add(vp.getIndex());
	//			}
	//		}		
	//
	//		if (mtp.size() >= minMtpSize) {
	//			MTPs.add(mtp);
	//			CISs.add(cis);
	//		}
	//
	//		ArrayList<MtpCisPair> mtpCisPairs = new ArrayList<MtpCisPair>();
	//
	//		for(int i = 0; i < MTPs.size(); i++) {
	//			mtpCisPairs.add(new MtpCisPair(MTPs.get(i),CISs.get(i)));
	//		}
	//
	//		System.out.println("completed: "+MTPs.size()+" MTPs found");
	//		return mtpCisPairs;
	//
	//	}

	//	private ArrayList<MtpCisPair> removeTranslationallyEquivalentMtps(ArrayList<MtpCisPair> mtpCisPairs) {
	//		System.out.print("removeTranslationallyEquivalentMtps");
	//		//Put the MTPs into order of size - then only have to compare each MTP 
	//		//with other MTPs of the same size when removing translationally 
	//		//equivalent MTPs.
	//
	//		PointSetSizeComparator pointSetSizeComparator = new PointSetSizeComparator();
	//		Collections.sort(mtpCisPairs, pointSetSizeComparator);
	//
	//		//Remove MTPs that are translationally equivalent to other MTPs
	//		//so that we don't have to compute the same TEC more than once.
	//
	//		ArrayList<MtpCisPair> newMtpCisPairList = new ArrayList<MtpCisPair>();
	//
	//		MtpCisPair s1, s2;
	//		int n = mtpCisPairs.size();
	//		for(int i = 0; i < n; i++) {
	//			if (i%500 == 0 && i != 0) {
	//				System.out.print(".");
	//				System.out.flush();
	//			}
	//			if (i%25000 == 0) {
	//				System.out.println();
	//				System.out.flush();
	//			}
	//			s1 = mtpCisPairs.get(i);
	//			int x = s1.getMtp().size();
	//			boolean found = false;
	//			for(int j = i + 1; !found && j < n && (s2 = mtpCisPairs.get(j)).getMtp().size() == x; j++) {
	//				if (s1.getMtp().translationallyEquivalentTo(s2.getMtp()))
	//					found = true;
	//			}
	//			if (!found)
	//				newMtpCisPairList.add(s1);
	//		}
	//
	//		System.out.println("\ncompleted: "+newMtpCisPairList.size()+" MTPs after removing translational equivalents");
	//		return newMtpCisPairList;
	//	}


//	@Override
//	public void draw() {
//		javax.swing.SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				JFrame frame = new JFrame();
//				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
//				frame.setResizable(false);
//				PApplet embed = new DrawPoints(dataset,getTECs());
//				frame.add(embed);
//				embed.init();
//				frame.pack();
//				frame.setVisible(true);
//			}
//		});
//	}

//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		for(TEC tec: tecs)
//			sb.append(tec.toString()+"\n");
//		return sb.toString();
//	}

//	public static void main(String[] args) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException {
		//		String inputFilePath = "/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/Lartillot/ClosedSubstrings001.opnd";
		//		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/Lartillot";
		//		String inputFilePath = "/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv847b-done.opnd";
		//		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/output/points018";
		//		String inputFilePath = "/Users/dave/Documents/Work/Research/Data/Mozart-Haydn/Themefinder/Haydn/op74n1-04.mid";
//		String inputFilePath = "/Users/dave/Documents/Work/Research/Data/Sony/From Emmanuel/to David Meredith/SC045 - Buddha Bar - Bestof by Ravin 2013 - Alfida - Allaya Lee.mid";
//		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/2015-02-15-workspace/Points/output/points018";
//		boolean forRSubdiagonals = false;
//		int r = 0;
//		boolean withCompactnessTrawler = false;
//		double a = 1.0;
//		int b = 2;
//		int minPatternSize = 5;
//		PointSet pointSet = null;
//		PitchRepresentation pitchRepresentation = PitchRepresentation.CHROMATIC_PITCH;
//		boolean drawOutput = true;
//		boolean verbose = false;
//		SIATECEncoding encoding = new SIATECEncoding(
//				inputFilePath,
//				outputFileDirectoryPath,
//				forRSubdiagonals, 
//				r,
//				withCompactnessTrawler, 
//				a, 
//				b);

		//		SIATECEncoding encoding = new SIATECEncoding(
		//				pointSet, 
		//				inputFilePath, 
		//				outputFileDirectoryPath, 
		//				minPatternSize, 
		//				pitchRepresentation, 
		//				drawOutput, 
		//				verbose,
		//				forRSubdiagonals, 
		//				r,
		//				withCompactnessTrawler, 
		//				a, 
		//				b);
//		encoding.draw();
//	}

}
