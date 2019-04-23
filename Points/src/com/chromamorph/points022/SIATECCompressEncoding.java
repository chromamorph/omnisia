package com.chromamorph.points022;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JFrame;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

import processing.core.PApplet;

public class SIATECCompressEncoding extends Encoding {
	private PointSet points = new PointSet();

	public int getNumberOfNotes() {return dataset.size(); }
	public int getMaxPitch() {return dataset.getMaxY(); }
	public int getMinPitch() {return dataset.getMinY(); }
	public long getMaxTime() {return dataset.getMaxX(); }
	public long getMinTime() {return dataset.getMinX(); }
	public int getNumberOfEncodingTECs() {return getTECs().size(); }

	private int totalNumberOfMTPs;
	public void setTotalNumberOfMTPs(int numberOfMTPs) {totalNumberOfMTPs = numberOfMTPs;}
	public int getTotalNumberOfMTPs() {return totalNumberOfMTPs;}

	private int numberOfTranDistMTPs;
	public void setNumberOfTranDistMTPs(int numberOfMTPs) {this.numberOfTranDistMTPs = numberOfMTPs;}
	public int getNumberOfTranDistMTPs() {return numberOfTranDistMTPs;}
	private boolean outputFilePathDefinedByOMNISIA = false;

	public SIATECCompressEncoding(String encodingFileName) {
		//First read in each line of the COS file.

		ArrayList<String> tecStrings = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(encodingFileName));
			for(String l = br.readLine(); l != null; l = br.readLine())
				if ((l.trim().length() > 0) && (l.startsWith("T(P(p(")))
					tecStrings.add(l.trim());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Now parse each tecString in tecStrings to get a TEC and
		//store these TECs in a new list.

		for(int i = 0; i < tecStrings.size(); i++)
			getTECs().add(new TEC(tecStrings.get(i)));

		//Now we need to find the complete set of points covered 
		//by the set of TECs that we've just read in.

		PointSet pointSet = new PointSet();
		for(TEC tec : getTECs()) {
			pointSet.addAll(tec.getCoveredPoints());
		}

		dataset = pointSet;

		for(TEC tec : getTECs())
			tec.setDataset(pointSet);

		//		TEC lastTEC = getTECs().get(getTECs().size()-1); 
		//		if (lastTEC.getTranslatorSetSize() == 1 || lastTEC.getPatternSize() == 1)
		//			residualPointSet = lastTEC.getCoveredPoints();

	}

	public SIATECCompressEncoding(String inputFilePathName, String outputDirectoryPathName, int minPatternSize, PitchRepresentation pitchRepresentation, boolean drawOutput) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(inputFilePathName,outputDirectoryPathName,minPatternSize,pitchRepresentation,drawOutput,false,0.0,0);
	}

	public SIATECCompressEncoding(
			String inputFilePath,
			String outputDirectoryPath,
			boolean forRSuperdiagonals, int r,
			boolean withCompactnessTrawler, double a, int b) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(inputFilePath,
				outputDirectoryPath,
				0,
				PitchRepresentation.MORPHETIC_PITCH,
				false, //drawOutput
				withCompactnessTrawler, a, b,
				forRSuperdiagonals, r);
	}

	public SIATECCompressEncoding(String inputFilePathName, 
			String outputDirectoryPathName, 
			int minPatternSize, 
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean withCompactnessTrawler, 
			double a, 
			int b) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(inputFilePathName, 
				outputDirectoryPathName, 
				minPatternSize, 
				pitchRepresentation, 
				drawOutput, 
				withCompactnessTrawler, 
				a, 
				b,
				false,
				0);
	}


	public SIATECCompressEncoding(String inputFilePathName, 
			String outputDirectoryPathName, 
			int minPatternSize, 
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean withCompactnessTrawler, 
			double a, 
			int b,
			boolean forRSuperdiagonals,
			int r) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(inputFilePathName, 
				outputDirectoryPathName, 
				minPatternSize, 
				0,
				pitchRepresentation, 
				drawOutput, 
				withCompactnessTrawler, 
				a, 
				b,
				forRSuperdiagonals,
				r);
	}

	public SIATECCompressEncoding(String inputFilePathName, 
			String outputDirectoryPathName, 
			int minPatternSize, 
			int maxPatternSize,
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean withCompactnessTrawler, 
			double a, 
			int b,
			boolean forRSuperdiagonals,
			int r) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(inputFilePathName, 
				outputDirectoryPathName, 
				minPatternSize, 
				maxPatternSize,
				pitchRepresentation, 
				drawOutput, 
				withCompactnessTrawler, 
				a, 
				b,
				forRSuperdiagonals,
				r,
				true,
				false, //mirex
				false, //segmentMode
				false, //bbMode
				null, //omnisiaOutputFilePath
				0, // topNPatterns
				false //withoutChannel10
				);
	}


	public SIATECCompressEncoding(String inputFilePathName, 
			String outputDirectoryPathName, 
			int minPatternSize, 
			int maxPatternSize,
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean withCompactnessTrawler, 
			double a, 
			int b,
			boolean forRSuperdiagonals,
			int r,
			boolean removeRedundantTranslators,
			boolean mirex,
			boolean segmentMode,
			boolean bbMode,
			String omnisiaOutputFilePath,
			int topNPatterns,
			boolean withoutChannel10
			) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		super(null,
				inputFilePathName,
				outputDirectoryPathName,
				pitchRepresentation.equals(PitchRepresentation.MORPHETIC_PITCH)?true:false,
				withoutChannel10,
				"SIATECCompress",
				topNPatterns,
				mirex,
				segmentMode,
				bbMode,
				omnisiaOutputFilePath
				);
		SIATECCompress siatecCompress = new SIATECCompress();
		SIATECCompressEncoding encoding = (SIATECCompressEncoding)(siatecCompress.encode(dataset,
				withCompactnessTrawler,
				a,
				b,
				forRSuperdiagonals,
				r,
				minPatternSize,
				maxPatternSize,
				0,
				removeRedundantTranslators));

		setTotalNumberOfMTPs(SIA.TOTAL_NUMBER_OF_MTPs);
		setNumberOfTranDistMTPs(siatecCompress.getNumberOfTranDistMTPs());
		setRunningTime(encoding.getRunningTime());
		setTECs(encoding.getTECs());
		
		writeToFile();
		
	}

	public SIATECCompressEncoding(PointSet points, String inputFileName, String outputDirectoryPathName, int minPatternSize, PitchRepresentation pitchRepresentation, boolean drawOutput) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException {
		this(points, inputFileName, outputDirectoryPathName, minPatternSize, pitchRepresentation, drawOutput, false, 0.0, 0);
	}

	public SIATECCompressEncoding(PointSet points, 
			String inputFileName, 
			String outputDirectoryPathName, 
			int minPatternSize, 
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean withCompactnessTrawler, 
			double a, 
			int b) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException {
		this(	points, 
				inputFileName, 
				outputDirectoryPathName, 
				minPatternSize, 
				pitchRepresentation, 
				drawOutput, 
				withCompactnessTrawler, 
				a, 
				b,
				false, //forRSuperdiagonals
				0);
	}


	public SIATECCompressEncoding(PointSet points, 
			String inputFileName, 
			String outputDirectoryPathName, 
			int minPatternSize, 
			PitchRepresentation pitchRepresentation, 
			boolean drawOutput, 
			boolean withCompactnessTrawler, 
			double a, 
			int b,
			boolean forRSuperdiagonals,
			int r) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException {
		dataset = points;
		SIATECCompress siatecCompress = new SIATECCompress();
		SIATECCompressEncoding encoding = (SIATECCompressEncoding)(siatecCompress.encode(dataset,withCompactnessTrawler,a,b,forRSuperdiagonals,r)); 
		setTotalNumberOfMTPs(SIA.TOTAL_NUMBER_OF_MTPs);
		setNumberOfTranDistMTPs(siatecCompress.getNumberOfTranDistMTPs());
		setRunningTime(encoding.getRunningTime());
		setTECs(encoding.getTECs());
		if (!outputDirectoryPathName.endsWith("/"))
			outputDirectoryPathName = outputDirectoryPathName + "/";
		String aString = String.format("%.2f",a);
		String bString = String.format("%d",b);
		String outputFilePathName = outputDirectoryPathName + inputFileName + ".SIA"+(forRSuperdiagonals?"-R"+r+"-":"")+(withCompactnessTrawler?"-CT-"+aString+"-"+bString+"-":"")+"TECCompress";
		writeToFile(outputFilePathName);

		if (drawOutput)
			draw();
	}

	public void draw() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,getTECs());
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public SIATECCompressEncoding(PointSet dataset) {
		this(dataset, 0.0, 0, 0);
	}

	public SIATECCompressEncoding(PointSet dataset, double a, int b, int r) {
		SIATECCompress siatecCompress = new SIATECCompress();

		SIATECCompressEncoding encoding = (SIATECCompressEncoding)(siatecCompress.encode(dataset, a, b, r)); 
		setTotalNumberOfMTPs(SIA.TOTAL_NUMBER_OF_MTPs);
		setNumberOfTranDistMTPs(siatecCompress.getNumberOfTranDistMTPs());
		setTECs(encoding.getTECs());
		this.dataset = dataset;
	}

	public SIATECCompressEncoding(PointSet points, PointSet dataset) {
		this.dataset = dataset;
		addTEC(new TEC(points, dataset));
		this.points = points;
		this.points.bbArea = this.points.maxX = this.points.minX = null; 
		this.points.maxY = this.points.minY = null;
		this.points.temporalDensity = null;
	}

	public SIATECCompressEncoding(ArrayList<TEC> tecs, PointSet residualPointSet, PointSet dataset) {
		this.dataset = dataset;
		setTECs(tecs);
		if (!residualPointSet.isEmpty())
			addTEC(new TEC(residualPointSet,dataset));
		for(TEC tec : getTECs())
			this.points.getPoints().addAll(tec.getCoveredPoints().getPoints());
		this.points.bbArea = this.points.maxX = this.points.minX = null; 
		this.points.maxY = this.points.minY = null;
		this.points.temporalDensity = null;
	}

	public void writeToFile(String fileName) {
		try {
			PrintStream ps = new PrintStream(fileName);
			ps.print(toString());
			ps.close();
			StringBuilder sb = new StringBuilder();
			sb.append("\n\nNumber of notes: "+getNumberOfNotes()+"\n");
			sb.append("Number of MTPs: "+getTotalNumberOfMTPs()+"\n");
			sb.append("Number of translationally distinct MTPs: " + getNumberOfTranDistMTPs()+"\n");
			sb.append("Running time in ms: "+getRunningTime()+"\n");
			sb.append("Number of encoding TECs: "+getNumberOfEncodingTECs()+"\n");
			sb.append("Encoding length: "+getEncodingLength()+"\n");
			sb.append("Encoding length without residual point set: "+getEncodingLengthWithoutResidualPointSet()+"\n");
			sb.append("Number and proportion of residual points: "+getNumberOfResidualPoints()+", "+String.format("%.2f",getPercentageOfResidualPoints())+"%\n");
			sb.append("Compression ratio: "+getCompressionRatio() +"\n");
			sb.append("Compression ratio excluding residual point set: "+String.format("%.2f",getCompressionRatioWithoutResidualPointSet())+"\n");
			sb.append("Maximum time (in tatums): "+getMaxTime()+"\n");
			sb.append("Minimum time (in tatums): "+getMinTime() +"\n");
			sb.append("Maximum pitch: " + getMaxPitch() + "\n");
			sb.append("Minimum pitch: " + getMinPitch() + "\n");
			sb.append("Running time: "+getRunningTime()+" milliseconds\n");

			if (!outputFilePathDefinedByOMNISIA) {
				String logFilePathName = fileName.substring(0,fileName.lastIndexOf(".")) + ".log";
				ps = new PrintStream(logFilePathName);
				ps.print(sb.toString());
				ps.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		//		String inputFilePath = "/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv851b-done.opnd";
		//		String inputFilePath = "/Users/dave/Documents/Work/Research/Data/JKU-PDD/JKUPDD-noAudio-Aug2013/groundTruth/bachBWV889Fg/polyphonic/lisp/wtc2f20.opnd";
		//		String inputFilePath = "/Users/dave/Documents/Work/Research/Data/Francois/Releve-308.mid";
		//		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/output/points018";
		//		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/Data/Francois/SIATECCompress";
		//		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/Data/Francois/Releve-308/05SIATECCompress-DIAT";
		//		String outputFileDirectoryPath = "/Users/dave/Documents/Work/Research/Data/Francois/Releve-308/06SIARCTTECCompress-DIAT";
		//		String inputFilePath = "/Users/dave/Documents/Work/Research/Data/Francois/07-map-GM.mid";
		String inputFilePath = "data/WTCI-FUGUES-FOR-JNMR-2014/bwv847b-done.opnd";
		String outputFileDirectoryPath = "output/2015-11-13c";

		int r = 3;
		boolean withCompactnessTrawler = false;
		double a = 0.6;
		int b = 3;
		int minPatternSize = 3;
		PitchRepresentation pitchRepresentation = PitchRepresentation.MORPHETIC_PITCH;
		boolean drawOutput = false;
		boolean forRSuperdiagonals = false;
		SIATECCompressEncoding encoding;
		try {
			encoding = new SIATECCompressEncoding(
					//					inputFilePath,
					//					outputFileDirectoryPath,
					//					forRSubdiagonals, 
					//					r,
					//					withCompactnessTrawler, 
					//					a, 
					//					b

					inputFilePath, 
					outputFileDirectoryPath, 
					minPatternSize, 
					pitchRepresentation, 
					drawOutput, 
					withCompactnessTrawler, 
					a, 
					b,
					forRSuperdiagonals,
					r
					);
			//			encoding.drawOccurrenceSets(true);
			encoding.drawOccurrenceSets(outputFileDirectoryPath+"/"+"bwv847b-done.opnd",true);
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
	}

}
