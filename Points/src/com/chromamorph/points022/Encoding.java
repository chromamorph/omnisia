package com.chromamorph.points022;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import javax.swing.JFrame;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;
import com.chromamorph.points022.MIREX2013Entries.TomDavePoint;

import processing.core.PApplet;

public class Encoding {

	private String title = "";
	protected PointSet dataset = null;
	protected ArrayList<ArrayList<PointSet>> occurrenceSets = null;

	private ArrayList<TEC> tecs = new ArrayList<TEC>();
	public void setTECs(ArrayList<TEC> tecs) {this.tecs = tecs;}

	/**
	 * Returns an ArrayList containing the TECs in this Encoding.
	 *
	 * @return An ArrayList<TEC>. Never returns null - if there are no
	 * TECs and the internal ArrayList variable is null, then this method
	 * creates a new empty internal ArrayList.
	 */
	public ArrayList<TEC> getTECs() {
		if (tecs== null)
			tecs = new ArrayList<TEC>();
		return tecs;
	}
	public void addTEC(TEC tec) {
		getTECs().add(tec);
	}

	private Long tatumsPerBar = null; 
	private Long barOneStartsAt = null;

	protected String inputFilePathString = null;
	protected Path inputFilePath = null;
	protected String inputFileName = null;
	protected String inputFileNameWithoutExtension = null;

	protected String outputDirectoryPathString = null;
	protected Path outputDirectoryPath = null;

	protected String outputFileNameWithoutExtension = null;

	protected String logFilePathString = null;
	protected Path logFilePath = null;
	protected PrintStream logPrintStream = null;

	protected String outputFilePathString = null;
	protected Path outputFilePath = null;

	protected String outputFileExtension = null;

	protected String omnisiaOutputFilePathString = null;
	protected Path omnisiaOutputFilePath = null;

	protected boolean isDiatonic = false;
	protected boolean withoutChannel10 = false;
	protected String encoderName = null;
	protected boolean forMirex = false;
	protected boolean segmentMode = false;
	protected boolean bbMode = false;
	protected int topNPatterns = 0;

	//	protected PointSet residualPointSet = null;
	protected Long runningTime = null;

	public Encoding() {}

	public Encoding(
			PointSet dataset,
			String inputFilePathString,
			String outputDirectoryPathString,
			boolean isDiatonic,
			boolean withoutChannel10,
			String outputFileExtension,
			int topNPatterns,
			boolean forMirex,
			boolean segmentMode,
			boolean bbMode,
			String omnisiaOutputFilePathString) throws MissingTieStartNoteException, FileNotFoundException {


		//		protected String omnisiaOutputFilePathString = null;
		this.omnisiaOutputFilePathString = omnisiaOutputFilePathString;
		this.bbMode = bbMode;
		this.segmentMode = segmentMode;
		this.forMirex = forMirex;
		this.topNPatterns = topNPatterns;
		this.isDiatonic = isDiatonic;
		this.withoutChannel10 = withoutChannel10;

		//		protected String outputFileExtension = null;
		this.outputFileExtension = outputFileExtension;

		//		protected String inputFilePathString = null;
		this.inputFilePathString = inputFilePathString;
		//		protected Path inputFilePath = null;
		if (inputFilePathString != null)
			this.inputFilePath = Paths.get(inputFilePathString);
		//		protected String inputFileName = null;
		if (inputFilePath != null)
			this.inputFileName = this.inputFilePath.getFileName().toString();
		//		protected String inputFileNameWithoutExtension = null;
		if (inputFileName != null)
			this.inputFileNameWithoutExtension = this.inputFileName.substring(0, this.inputFileName.indexOf("."));

		//		protected String outputDirectoryPathString = null;
		this.outputDirectoryPathString = outputDirectoryPathString;
		//		protected Path outputDirectoryPath = null;
		if (omnisiaOutputFilePathString == null && outputDirectoryPathString != null) {
			this.outputDirectoryPath = Paths.get(outputDirectoryPathString);
		}

		//		protected String outputFileNameWithoutExtension = null;
		if (omnisiaOutputFilePathString == null && inputFileNameWithoutExtension != null) {
			this.outputFileNameWithoutExtension = inputFileNameWithoutExtension + (isDiatonic?"-diat":"-chrom");
		}

		if (omnisiaOutputFilePathString == null && outputDirectoryPath != null && outputFileNameWithoutExtension != null) {
			//			protected Path logFilePath = null;
			this.logFilePath = this.outputDirectoryPath.resolve(outputFileNameWithoutExtension+".log");
			//			protected String logFilePathString = null;
			this.logFilePathString = logFilePath.toString();
			//			protected PrintStream logPrintStream = null;
			this.logPrintStream = new PrintStream(logFilePathString);
		}

		//		protected Path omnisiaOutputFilePath = null;
		if (omnisiaOutputFilePathString != null)
			this.omnisiaOutputFilePath = Paths.get(omnisiaOutputFilePathString);


		//		protected String outputFilePathString = null;
		if (omnisiaOutputFilePathString != null)
			this.outputFilePathString = omnisiaOutputFilePathString;
		else if (this.outputDirectoryPath != null && this.outputFileNameWithoutExtension != null)
			this.outputFilePathString = this.outputDirectoryPath.resolve(this.outputFileNameWithoutExtension+"."+this.outputFileExtension).toString();
		//		protected Path outputFilePath = null;
		if (this.outputFilePathString != null)
			this.outputFilePath = Paths.get(this.outputFilePathString);


		if (this.inputFilePathString != null) {
			setTatumsPerBarAndBarOneStartsAt(this.inputFilePathString);
			setTitle(Paths.get(inputFilePathString).getFileName().toString());
		}
		if (dataset != null)
			this.dataset = dataset;
		else if (this.inputFilePathString != null)
			this.dataset = new PointSet(this.inputFilePathString, isDiatonic, withoutChannel10);

		this.encoderName = this.getClass().getName();
	}

	public PointSet getDataset() {
		return dataset;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public Long getTatumsPerBar() {
		return tatumsPerBar;
	}
	public void setTatumsPerBar(Long tatumsPerBar) {
		this.tatumsPerBar = tatumsPerBar;
	}
	public Long getBarOneStartsAt() {
		return barOneStartsAt;
	}
	public void setBarOneStartsAt(Long barOneStartsAt) {
		this.barOneStartsAt = barOneStartsAt;
	}

	public ArrayList<ArrayList<PointSet>> getOccurrenceSets() {
		if (occurrenceSets == null) {
			occurrenceSets = new ArrayList<ArrayList<PointSet>>();
			for(TEC tec: getTECs())
				occurrenceSets.add(tec.getPointSets());
		}
		return occurrenceSets;
	}

	public ArrayList<ArrayList<PointSet>> getOccurrenceSets(int numberOfPatterns) {
		if (occurrenceSets == null) {
			occurrenceSets = new ArrayList<ArrayList<PointSet>>();
			ArrayList<TEC> tecs = getTECs();
			for(int i = 0; i < tecs.size() && i < numberOfPatterns; i++) {
				TEC tec = tecs.get(i);
				occurrenceSets.add(tec.getPointSets());
			}
		}
		return occurrenceSets;
	}

	public void draw() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,occurrenceSets,true,isDiatonic,tatumsPerBar,barOneStartsAt,getTitle(),outputFilePath.toString(),false);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void drawOccurrenceSets() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,getOccurrenceSets(),true);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void drawOccurrenceSets(final boolean diatonicPitch) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,getOccurrenceSets(),true,diatonicPitch,getTatumsPerBar(),getBarOneStartsAt(),getTitle());
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void drawOccurrenceSets(final String outputFilePath, final boolean diatonicPitch) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,getOccurrenceSets(),true,diatonicPitch,getTatumsPerBar(),getBarOneStartsAt(),getTitle(),outputFilePath, false);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void drawOccurrenceSetsToFile(final String outputFilePath, final boolean diatonicPitch) {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JFrame frame = new JFrame();
					frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
					frame.setResizable(false);
					PApplet embed = new DrawPoints(dataset,getOccurrenceSets(),true,diatonicPitch,getTatumsPerBar(),getBarOneStartsAt(),getTitle(),outputFilePath, false, true);
					frame.add(embed);
					embed.init();
					frame.pack();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void drawRecursiveTecsToFile(final String outputFilePath, final boolean diatonicPitch) {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JFrame frame = new JFrame();
					frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
					frame.setResizable(false);
					PApplet embed = new DrawPoints(dataset,getTECs(),diatonicPitch,getTatumsPerBar(),getBarOneStartsAt(),getTitle(),outputFilePath, false, true);
					frame.add(embed);
					embed.init();
					frame.pack();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void drawSegmentation(final String outputFilePath, final boolean diatonicPitch) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,getOccurrenceSets(),true,diatonicPitch,getTatumsPerBar(),getBarOneStartsAt(),getTitle(),outputFilePath, true);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}


	public void drawOccurrenceSets(final boolean diatonicPitch, final boolean printToPDF, final String pdfFilePath) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(
						dataset,
						getOccurrenceSets(),
						true,
						diatonicPitch,
						getTatumsPerBar(),
						getBarOneStartsAt(),
						getTitle(),
						printToPDF,
						pdfFilePath);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void drawOccurrenceSets(final int numberOfPatterns) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,getOccurrenceSets(numberOfPatterns),true);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void drawOccurrenceSets(final String infoString) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,getOccurrenceSets(),true,infoString);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void setTatumsPerBarAndBarOneStartsAt(String inputFilePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
			String l;
			while((l = br.readLine()) != null) {
				if (l.trim().startsWith("%tatumsPerBar"))
					setTatumsPerBar(Long.parseLong(l.trim().split(" ")[1]));
				else if (l.trim().startsWith("%barOneStartsAt"))
					setBarOneStartsAt(Long.parseLong(l.trim().split(" ")[1]));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a new set of TECs by repeated pairwise merging of the old set of TECs. 
	 */
	public void mergeTECs(int minMatchSize, double minCompactness, int numIterations) {
		occurrenceSets = null;
		for(int k = 0; k < numIterations; k++) {
			TreeSet<TEC> newTECs = new TreeSet<TEC>();
			for(int i = 0; i < getTECs().size()-1; i++)
				for(int j = i+1; j < getTECs().size(); j++) {
					TEC tec1 = getTECs().get(i);
					TEC tec2 = getTECs().get(j);
					ArrayList<TEC> newTECsForThisPairOfOldTECs = mergeTECs(tec1,tec2,minMatchSize, minCompactness);
					if (newTECsForThisPairOfOldTECs.size() > 2)
						newTECs.add(newTECsForThisPairOfOldTECs.get(2));
					else {
						newTECs.add(tec1);
						newTECs.add(tec2);
					}
				}
			setTECs(new ArrayList<TEC>(newTECs)); 
			Collections.sort(getTECs(),new TECQualityComparator());
		}
	}

	private static class TECMatchPair extends TEC {
		private TreeSet<VectorVectorPair> match;
		public TreeSet<VectorVectorPair> getMatch() {return match; }
		public void setMatch(TreeSet<VectorVectorPair> match) {this.match = match;}

		public TECMatchPair(PointSet mergedPattern, VectorSet mergedTranslators, PointSet dataset, TreeSet<VectorVectorPair> maxMatch) {
			super(mergedPattern,mergedTranslators,dataset);
			setMatch(maxMatch);
		}

		public VectorSet getOriginVectors() {
			VectorSet vSet = new VectorSet();
			for(VectorVectorPair vvp : getMatch()) {
				vSet.add(vvp.getOriginVector());
			}
			return vSet;
		}

		public Vector getDifferenceVector() {
			return getMatch().first().getDifferenceVector();
		}
	}

	/**
	 * Merges tec1 and tec2 to produce a new merged tec, if 1. the size of the maximal match between 
	 * the vector sets of tec1 and tec2 is greater than or equal to minMatchSize; and 2. the
	 * compactness of the merged TEC is at least minCompactness. 
	 * 
	 * @param tec1 The first TEC to merge
	 * @param tec2 The second TEC to merge
	 * @param minMatchSize The minimum size of a match required to generate a new merged TEC
	 * @param minCompactness The minimum compactness tolerated in the merged TEC
	 * @return An ArrayList containing either two or three TECs. If it contains only two TECs, then the
	 * first TEC is the same as tec1 given as input and the second is the same as tec2 given as input.
	 * If the ArrayList contains three TECs, then the first TEC is the new replacement for tec1, the
	 * second is the new replacement for tec2 and the third is the new merged TEC.
	 */
	public ArrayList<TEC> mergeTECs(TEC tec1, TEC tec2, int minMatchSize, double minCompactness) {

		System.out.println("tec1: "+tec1);
		System.out.println("tec2: "+tec2);
		System.out.println();

		//		Find maximal match between translator sets for tec1 and tec2 using SIAM
		Vector[] tranArray1 = new Vector[tec1.getTranslatorSetSize()];
		Vector[] tranArray2 = new Vector[tec2.getTranslatorSetSize()];
		tec1.getTranslators().getVectors().toArray(tranArray1);
		tec2.getTranslators().getVectors().toArray(tranArray2);

		TreeSet<VectorVectorPair> vectorTable = new TreeSet<VectorVectorPair>();

		for(int i = 0; i < tranArray1.length; i++)
			for(int j = 0; j < tranArray2.length; j++)
				vectorTable.add(new VectorVectorPair(tranArray2[j].minus(tranArray1[i]),tranArray1[i]));

		//		Find maximal translatable vector patterns (i.e., maximal matches between vector sets)
		VectorVectorPair[] vvps = new VectorVectorPair[vectorTable.size()];
		vectorTable.toArray(vvps);
		ArrayList<TreeSet<VectorVectorPair>> maximalMatches = new ArrayList<TreeSet<VectorVectorPair>>();
		Vector thisDiffVector = vvps[0].getDifferenceVector();
		TreeSet<VectorVectorPair> thisMaxMatch = new TreeSet<VectorVectorPair>();
		thisMaxMatch.add(vvps[0]);
		for(int i = 1; i < vvps.length; i++) {
			VectorVectorPair thisVVP = vvps[i];
			if (thisVVP.getDifferenceVector().equals(thisDiffVector)) {
				thisMaxMatch.add(thisVVP);
			} else {
				maximalMatches.add(thisMaxMatch);
				thisDiffVector = thisVVP.getDifferenceVector();
				thisMaxMatch = new TreeSet<VectorVectorPair>();
				thisMaxMatch.add(thisVVP);
			}
		}
		maximalMatches.add(thisMaxMatch);

		//		Compute the merged TECs that correspond to the maximal matches
		ArrayList<TECMatchPair> mergedTECMatchPairs = new ArrayList<TECMatchPair>();
		for(TreeSet<VectorVectorPair> maxMatch : maximalMatches) {
			Vector firstOriginVector = maxMatch.first().getOriginVector();
			Vector diffVector = maxMatch.first().getDifferenceVector();
			VectorSet mergedTranslators = new VectorSet();
			for(VectorVectorPair vvp : maxMatch) {
				mergedTranslators.add(vvp.getOriginVector().minus(firstOriginVector));
			}
			PointSet pattern1 = tec1.getPattern().translate(firstOriginVector);
			PointSet pattern2 = tec2.getPattern().translate(firstOriginVector.add(diffVector));
			PointSet mergedPattern = new PointSet();
			mergedPattern.addAll(pattern1);
			mergedPattern.addAll(pattern2);
			TECMatchPair mergedTEC = new TECMatchPair(mergedPattern,mergedTranslators,dataset,maxMatch);
			mergedTECMatchPairs.add(mergedTEC);
		}

		//		Find the best merged TEC
		Collections.sort(mergedTECMatchPairs, new TECQualityComparator());
		TECMatchPair bestMergedTECMatchPair = mergedTECMatchPairs.get(0);

		//		Compute the new versions of tec1 and tec2
		//		Remove translators corresponding to occurrences of patterns in the merged TEC
		//		First find new version of tec1. This is just original translator set minus
		//		set of origin vectors in best TECMatchPair:

		VectorSet originVectors = bestMergedTECMatchPair.getOriginVectors();
		TEC newTEC1;
		if (originVectors.isEmpty())
			newTEC1 = new TEC(tec1.getPattern().copy(),tec1.getTranslators().copy(),dataset);
		else
			newTEC1 = new TEC(tec1.getPattern().copy(),tec1.getTranslators().copy().remove(originVectors),dataset);
		if (newTEC1.getTranslatorSetSize()==0)
			newTEC1 = null;

		//		... now find new version of tec2.
		Vector differenceVector = bestMergedTECMatchPair.getDifferenceVector();
		TEC newTEC2;
		if (originVectors.isEmpty())
			newTEC2 = new TEC(tec2.getPattern().copy(),tec2.getTranslators().copy(),dataset);
		else
			newTEC2 = new TEC(tec2.getPattern().copy(),tec2.getTranslators().copy().remove(originVectors.translate(differenceVector)),dataset);
		if (newTEC2.getTranslatorSetSize()==0)
			newTEC2 = null;

		ArrayList<TEC> outputList = new ArrayList<TEC>();
		outputList.add(tec1);
		outputList.add(tec2);

		//		If the best merged TEC has sufficient size and compactness then
		//		return an array consisting of the new tec1, the new tec2 and the best merged TEC, 
		//		(return null in place of tec1 or tec2 when one of them is empty)...

		if (bestMergedTECMatchPair.getTranslatorSetSize() >= minMatchSize && bestMergedTECMatchPair.getCompactness() >= minCompactness)
			outputList.add(bestMergedTECMatchPair);

		//		... otherwise return an array containing just tec1 and tec2.
		return outputList;
	}

	protected void writeToImageFile(String inputFilePathString, String outputDirectoryPathString, boolean diatonicPitch) {
		new DrawPoints(dataset,getOccurrenceSets(),true,diatonicPitch,getTatumsPerBar(),getBarOneStartsAt(),getTitle(),inputFilePathString, false, true//Write image file without displaying image
				);
	}

	static int mirexPatternNumber = 0;
	
	public static String getMIREXStringForTEC(TEC tec, boolean boundingBox, boolean segment, PointSet dataset) {
		StringBuilder sb = new StringBuilder();
		sb.append("pattern"+(++mirexPatternNumber)+"\n");
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
				TomDavePoint tomDavePoint = MIREX2013Entries.findTomDavePoint(thisPoint);
				double outputOnset = (tomDavePoint.tomsNumerator * 1.0)/(tomDavePoint.tomsDenominator);
				double outputPitch = tomDavePoint.tomsPitch * 1.0;
				String pointString = String.format("%.5f",outputOnset)+", "+String.format("%.5f",outputPitch)+"\n";
				sb.append(pointString);
			}
		}
		if (tec.getPatternTECs() != null) {
			for(TEC patternTec : tec.getPatternTECs()) {
				sb.append(getMIREXStringForTEC(patternTec, boundingBox, segment, dataset));
			}
		}
		return sb.toString();
	}

	
	public String toMIREXString() {
		mirexPatternNumber = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getTECs().size() && (topNPatterns == 0 || i < topNPatterns); i++) {
			TEC tec = getTECs().get(i);
			sb.append(getMIREXStringForTEC(tec, bbMode, segmentMode, dataset));
		}
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!getTECs().isEmpty())
			sb.append(getTECs().get(0));
		for (int i = 1; i < getTECs().size() && (topNPatterns == 0 || i < topNPatterns); i++) {
			TEC tec = getTECs().get(i);
			sb.append("\n"+tec);
		}
		sb.append("\n");
		sb.append("\nnumberOfNotes "+ getDataset().size());
		sb.append("\ntatumsPerBar "+getTatumsPerBar());
		sb.append("\nbarOneStartsAt "+getBarOneStartsAt());
		sb.append("\ncompressionRatio "+getCompressionRatio());
		sb.append("\nrunningTime "+getRunningTime());
		sb.append("\nencodingLength "+getEncodingLength());
		sb.append("\nencodingLengthWithoutResidualPointSet "+getEncodingLengthWithoutResidualPointSet());
		sb.append("\nnumberOfResidualPoints "+getNumberOfResidualPoints());
		sb.append("\npercentageOfResidualPoints "+getPercentageOfResidualPoints());
		sb.append("\ncompressionRatioWithoutResidualPointSet "+getCompressionRatioWithoutResidualPointSet());
		sb.append("\ntitle " + getTitle());
		sb.append("\nnumberOfTECs "+getNumberOfTECs());
		sb.append("\ninputFilePath "+getInputFilePathString());
		sb.append("\noutputFilePath "+getOutputFilePathString());
		sb.append("\nlogFilePath "+getLogFilePathString());
		sb.append("\noutputFileExtension "+getOutputFileExtension());
		sb.append("\nisDiatonic "+isDiatonic());
		sb.append("\nwithoutChannel10 "+isWithoutChannel10());
		sb.append("\nencoderName "+getEncoderName());
		sb.append("\nforMirex "+isForMirex());
		sb.append("\nmode "+getMode());
		sb.append("\ntopNPatterns "+topNPatterns);

		return sb.toString();
	}

	protected PatternMode getMode() {
		return (bbMode?PatternMode.BB:(segmentMode?PatternMode.Segment:PatternMode.Raw));
	}

	protected boolean isForMirex() {
		return forMirex;
	}

	protected String getEncoderName() {
		return encoderName;
	}

	protected boolean isWithoutChannel10() {
		return withoutChannel10;
	}

	protected boolean isDiatonic() {
		return isDiatonic;
	}

	protected String getOutputFileExtension() {
		if (outputFileExtension != null)
			return outputFileExtension;
		return null;
	}

	protected String getLogFilePathString() {
		if (logFilePath != null)
			return logFilePath.toString();
		return null;
	}

	protected String getOutputFilePathString() {
		if (outputFilePath != null)
			return outputFilePath.toString();
		return null;
	}

	protected String getInputFilePathString() {
		if (inputFilePath != null)
			return inputFilePath.toString();
		return null;
	}

	protected int getNumberOfTECs() {
		return getTECs().size();
	}

	protected int getEncodingLength() {
		int n = 0;
		for (TEC tec : getTECs())
			n += tec.getEncodingLength();
		return n;
	}

	protected int getEncodingLengthWithoutResidualPointSet() {
		return getEncodingLength() - getNumberOfResidualPoints();
	}

	protected int getNumberOfResidualPoints() {
		return getResidualPointSet().size();
	}

	protected PointSet getResidualPointSet() {
		PointSet rps = new PointSet();
		TEC lastTEC = null;
		if (!getTECs().isEmpty())
			lastTEC = getTECs().get(getTECs().size()-1);
		if (lastTEC != null && (lastTEC.getPatternSize() == 1 || lastTEC.getTranslatorSetSize() == 1))
			rps.addAll(lastTEC.getCoveredPoints());
		return rps;
	}

	protected double getPercentageOfResidualPoints() {
		return 100.0 * (getNumberOfResidualPoints()*1.0)/dataset.size();
	}

	protected double getCompressionRatioWithoutResidualPointSet() {
		return (1.0 * (dataset.size() - getNumberOfResidualPoints()))/(getEncodingLength()-getNumberOfResidualPoints());
	}

	protected double getCompressionRatio() {
		return dataset.size()/(1.0 * getEncodingLength());
	}

	protected void setRunningTime(long runningTime) {
		this.runningTime = runningTime;
	}

	protected Long getRunningTime() {
		return runningTime;
	}

	protected void writeToFile() throws FileNotFoundException {
		if (this.outputFilePath != null) {
			PrintWriter pw = new PrintWriter(this.outputFilePath.toFile());
			pw.println(this);
			pw.flush();
			pw.close();
			if (forMirex) {
				int dotIndex = this.outputFilePathString.lastIndexOf('.');
				String outputFilePathStringToDot = this.outputFilePathString.substring(0,dotIndex);
				String mirexOutputFilePathString = outputFilePathStringToDot + ".txt";
				pw = new PrintWriter(new File(mirexOutputFilePathString));
				pw.println(this.toMIREXString());
				pw.flush();
				pw.close();			
			}
		}
	}

	public Encoding copy() {
		Encoding newEnc = new Encoding();

		newEnc.setTitle(getTitle());
		newEnc.dataset = dataset.copy();
		newEnc.occurrenceSets = null;
		if (occurrenceSets != null) {
			newEnc.occurrenceSets = new ArrayList<ArrayList<PointSet>>();
			for(ArrayList<PointSet> pointSetArray : occurrenceSets) {
				ArrayList<PointSet> newPointSetArray = new ArrayList<PointSet>();
				for(PointSet pointSet : pointSetArray)
					newPointSetArray.add(pointSet.copy());
				newEnc.occurrenceSets.add(newPointSetArray);
			}
		}
		newEnc.setTECs(null);
		if (getTECs() != null) {
			ArrayList<TEC> newTECs = new ArrayList<TEC>();
			for(TEC oldTec : getTECs())
				newTECs.add(oldTec.copy());
			newEnc.setTECs(newTECs);
		}
		newEnc.setTatumsPerBar(getTatumsPerBar());
		newEnc.setBarOneStartsAt(getBarOneStartsAt());
		newEnc.inputFilePathString = this.inputFilePathString;
		newEnc.inputFilePath = this.inputFilePath;
		newEnc.inputFileName = this.inputFileName;
		newEnc.inputFileNameWithoutExtension = this.inputFileNameWithoutExtension;
		newEnc.outputDirectoryPathString = this.outputDirectoryPathString;
		newEnc.outputDirectoryPath = this.outputDirectoryPath;
		newEnc.outputFileNameWithoutExtension = this.outputFileNameWithoutExtension;
		newEnc.logFilePathString = this.logFilePathString;
		newEnc.logFilePath = this.logFilePath;
		newEnc.logPrintStream = this.logPrintStream;
		newEnc.outputFilePathString = this.outputFilePathString;
		newEnc.outputFilePath = outputFilePath;
		newEnc.outputFileExtension = outputFileExtension;
		newEnc.omnisiaOutputFilePathString = this.omnisiaOutputFilePathString;
		newEnc.omnisiaOutputFilePath = this.omnisiaOutputFilePath;
		newEnc.isDiatonic = isDiatonic;
		newEnc.withoutChannel10 = withoutChannel10;
		newEnc.encoderName = encoderName;
		newEnc.forMirex = forMirex;
		newEnc.segmentMode = segmentMode;
		newEnc.bbMode = bbMode;
		newEnc.topNPatterns = topNPatterns;
		newEnc.setRunningTime(getRunningTime());

		return newEnc;
	}

	public void removeRedundantTranslators() {
		for(TEC tec : getTECs()) 
			tec.removeRedundantTranslators();
	}

}
