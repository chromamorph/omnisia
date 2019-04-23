package com.chromamorph.points022.segmentation02;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import com.chromamorph.points022022.Encoding;
import com.chromamorph.points022022.Point;
import com.chromamorph.points022022.PointSet;
import com.chromamorph.points022022.TEC;


public class StructuralSegmentation {

	private Encoding encoding;
	private PointSet pointSet;
	private TreeSet<StructuralSegment> allSegments;
	private TreeSet<StructuralSegment> segmentation, groundTruth;
	private long endOfPiece;
	public StructuralSegmentation thisSegmentation;
	private boolean diatonicPitch = false;

	public TreeSet<StructuralSegment> getGroundTruth() {
		return groundTruth;
	}

	public StructuralSegmentation(
			Encoding encoding, 
			PointSet pointSet, 
			boolean diatonicPitch,
			String groundTruthFilePath) {
		this.encoding = encoding;
		this.pointSet = pointSet;
		computeAllSegments();
		computeSegmentation2();
		computeLetterLabels();
		computeEndOfPiece();
		thisSegmentation = this;
		this.diatonicPitch = diatonicPitch;
		readGroundTruthFromFile(groundTruthFilePath);
	}
	
	static class LabelStringPair implements Comparable<LabelStringPair>{
		String string;
		int label;
		
		@Override
		public int compareTo(LabelStringPair o) {
			if (o == null) return 1;
			int d = string.compareTo(o.string);
			if (d != 0) return d;
			return label - o.label;
		}
		
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof LabelStringPair)) return false;
			return compareTo((LabelStringPair)o)==0;
		}
		
		public LabelStringPair(String s) {
			string = s;
			label = 0;
		}
	}
	
	public void readGroundTruthFromFile(String groundTruthFilePath) {
		try {
			groundTruth = new TreeSet<StructuralSegment>();
			BufferedReader br = new BufferedReader(new FileReader(groundTruthFilePath));
			TreeSet<LabelStringPair> inputSegmentLabels = new TreeSet<LabelStringPair>();
			ArrayList<String[]> lines = new ArrayList<String[]>();
			String line;
			while((line = br.readLine()) != null) {
				String[] a = line.split("\t");
				lines.add(a);
				LabelStringPair lsp = new LabelStringPair(a[0]);
				inputSegmentLabels.add(lsp);
			}
			br.close();
//			Add labels to inputSegmentLabels
			int i = 0;
			for(LabelStringPair lsp : inputSegmentLabels)
				lsp.label = ++i;
			for(int j = 0; j < lines.size(); j++) {
				String[] a = lines.get(j);
				int label = inputSegmentLabels.ceiling(new LabelStringPair(a[0])).label;
				String[] startArray = a[4].split(":");
				String[] endArray = a[5].split(":");
				double startMins = Double.parseDouble(startArray[0]);
				double startSecs = Double.parseDouble(startArray[1]);
				double endMins = Double.parseDouble(endArray[0]);
				double endSecs = Double.parseDouble(endArray[1]);
				double startInSeconds = startMins*60 + startSecs;
				double endInSeconds = endMins*60 + endSecs;
				long start = (long)startInSeconds*pointSet.getTicksPerSecond();
				long end = (long)endInSeconds*pointSet.getTicksPerSecond();
				groundTruth.add(new StructuralSegment(label,start,end,startInSeconds,endInSeconds));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public TreeSet<StructuralSegment> getSegments() {
		return segmentation;
	}
	
	private void computeEndOfPiece() {
		endOfPiece = 0l;
		for(Point p : pointSet.getPoints()) {
			long e = p.getX()+p.getDuration();
			if (e > endOfPiece)
				endOfPiece = e;
		}
//		System.out.println("endOfPiece == "+endOfPiece);
	}

	private void computeAllSegments() {
		allSegments = new TreeSet<StructuralSegment>();
		ArrayList<TEC> tecs = encoding.getTECs();
		TreeSet<Point> allPoints = pointSet.getPoints();
		for(int i = 0; i < tecs.size(); i++) {
			TEC tec = tecs.get(i);
			ArrayList<PointSet> patterns = tec.getPointSets();
			for(PointSet pattern : patterns) {
				int label = i;
				TreeSet<Point> points = pattern.getPoints();
				long start = points.first().getX();
				long end = 0l;
				for(Point p : pattern.getPoints()) {
					Point pInPointSet = allPoints.ceiling(p);
					long thisEnd = pInPointSet.getX()+pInPointSet.getDuration();
					if (thisEnd > end)
						end = thisEnd;
				}
				StructuralSegment segment = new StructuralSegment(label,start,end);
				allSegments.add(segment);
			}
		}
	}

	/**
	 * Filters out segments longer than 30 seconds and shorter than 2 seconds
	 * Sorts remaining segments in a TreeSet
	 * Scans the treeset, selecting the first segments that start at or after the end
	 * of the previous segment.
	 */
	public void computeSegmentation() {
		ArrayList<StructuralSegment> allSegmentArray = new ArrayList<StructuralSegment>();
//		Compute starts and ends of segments in seconds and filter out segments that are too
//		long or too short.
		for(StructuralSegment s : allSegments) {
			s.setEndInSeconds((s.getEnd()*1.0)/pointSet.getTicksPerSecond());
			s.setStartInSeconds((s.getStart()*1.0)/pointSet.getTicksPerSecond());
			double duration = s.getEndInSeconds()-s.getStartInSeconds();
			System.out.println(duration);
			if (duration >= 2.0 && duration <= 30)
				allSegmentArray.add(s);
		}

//		System.out.println("allSegmentArray is "+allSegmentArray);
		segmentation = new TreeSet<StructuralSegment>();
		segmentation.add(allSegmentArray.get(0));
		System.out.println("segmentation is now "+segmentation);
		long nextStart = segmentation.first().getEnd();
//		System.out.println("end of first segment is "+nextStart);
		if (nextStart==endOfPiece) return;
		for(int i = 1; i < allSegmentArray.size(); i++) {
			StructuralSegment seg = allSegmentArray.get(i);
			if (seg.getStart()>=nextStart) {
				segmentation.add(seg);
				nextStart = seg.getEnd();
			}
			if (nextStart == endOfPiece)
				break;
		}
	}

	/**
	 * Makes a |TECs| x |tatums|  array
	 * Sets element <i,j> to 1 iff tatum j is 
	 * contained within an occurrence of TEC i.
	 * 
	 * Scans the TEC-tatum array from left to right
	 * to make a list of <tatum, TEC-list> pairs
	 * in which each element gives the TECs, TEC-list,
	 * for which occurrences are present at tatum.
	 * The list only contains elements for tatums
	 * where the TEC-list content changes.
	 * 
	 * Use F1 score to measure the difference between
	 * each TEC-list.
	 * 
	 * Use the F1 score distances to cluster the 
	 * segment boundaries (and therefore their following
	 * segments).
	 * 
	 * Make a segmentation in which there is a 
	 * segment boundary at each point at which
	 * the segment boundary cluster changes and
	 * label each segment boundary with the cluster
	 * label of the segment that starts at that 
	 * boundary.
	 */
	private void computeSegmentation2() {
		
	}
	
	private void computeLetterLabels() {
		int maxLabelNumber = 0;
		for(StructuralSegment s: segmentation)
			if (s.getLabel() > maxLabelNumber)
				maxLabelNumber = s.getLabel();
		char[] letterLabels = new char[maxLabelNumber+1];
		char currentLetterLabel = 'A'-1;
		for(StructuralSegment s : segmentation) {
			if (letterLabels[s.getLabel()]==0) {
				currentLetterLabel++;
				letterLabels[s.getLabel()] = currentLetterLabel;
				s.setLetterLabel(currentLetterLabel);
			} else
				s.setLetterLabel(letterLabels[s.getLabel()]);
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(StructuralSegment s : segmentation) {
			double startMins = Math.floor(s.getStartInSeconds()/60);
			String startMinsStr = String.format("%3.0f", startMins);
			double startSeconds = s.getStartInSeconds() - startMins * 60;
			String startSecondsStr = String.format("%06.3f", startSeconds);
			double endMins = Math.floor(s.getEndInSeconds()/60);
			String endMinsStr = String.format("%3.0f", endMins);
			double endSeconds = s.getEndInSeconds() - endMins * 60;
			String endSecondsStr = String.format("%06.3f",endSeconds);
			double duration = s.getEndInSeconds()-s.getStartInSeconds();
			double durationMins = Math.floor(duration/60);
			double durationSeconds = duration - durationMins * 60;
			String durationMinsStr = String.format("%3.0f",durationMins);
			String durationSecondsStr = String.format("%06.3f",durationSeconds);
			sb.append(s.getLetterLabel()+startMinsStr+":"+startSecondsStr+endMinsStr+":"+endSecondsStr+durationMinsStr+":"+durationSecondsStr+"\n");
		}
		return sb.toString();
	}
	
//	public void draw() {
//		javax.swing.SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				JFrame frame = new JFrame();
//				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
//				frame.setResizable(false);
//				PApplet embed = new DrawPoints(pointSet,thisSegmentation,diatonicPitch);
//				frame.add(embed);
//				embed.init();
//				frame.pack();
//				frame.setVisible(true);
//			}
//		});
//	}
}
