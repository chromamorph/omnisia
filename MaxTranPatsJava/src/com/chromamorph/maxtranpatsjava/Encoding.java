package com.chromamorph.maxtranpatsjava;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.JFrame;

import com.chromamorph.points022.DrawPoints;

import processing.core.PApplet;

public class Encoding {
	private ArrayList<OccurrenceSet> occurrenceSets;
	private PointSet dataset;
	private long runningTime;
	
//	public Encoding() {}
	
	public Encoding(ArrayList<OccurrenceSet> occurrenceSets, PointSet dataset) {
		setOccurrenceSets(occurrenceSets);
		setDataset(dataset);
	}
	
	public Encoding(File encodingFile, PointSet dataset) throws IOException, InvalidArgumentException {
		BufferedReader br = new BufferedReader(new FileReader(encodingFile));
		String l = br.readLine();
		while (l != null) {
			if (l.contains("Pat=") && l.contains("Trans=")) {
				add(new OccurrenceSet(l, dataset));
			}
			l = br.readLine();
		}
		br.close();
		if (getOccurrenceSets() == null) { // Then try alternative encoding format OS(P(p(x1,y1),...),[T(tc,sigma),...],<superMTPs>)
			br = new BufferedReader(new FileReader(encodingFile));
			l = br.readLine();
			while (l != null) {
				if (l.trim().startsWith("OS(P(p(")) {
					add(new OccurrenceSet(l, dataset));
				}
				l = br.readLine();
			}
			br.close();
		}
	}
	
	public void setDataset(PointSet dataset) {
		this.dataset = dataset;
	}
	
	public PointSet getDataset() {
		return dataset;
	}
	
	public int getDatasetSize() {
		return dataset.size();
	}
	
	public void setOccurrenceSets(ArrayList<OccurrenceSet> occurrenceSets) {
		this.occurrenceSets = occurrenceSets;
	}
	
	public ArrayList<OccurrenceSet> getOccurrenceSets() {
		return occurrenceSets;
	}
	
	public int getNumberOfOccurrenceSets() {
		return getOccurrenceSets().size();
	}
	
	public void add(OccurrenceSet occurrenceSet) {
		if (getOccurrenceSets() == null)
			occurrenceSets = new ArrayList<OccurrenceSet>();
		getOccurrenceSets().add(occurrenceSet);
	}
	
	public void addAll(Collection<OccurrenceSet> occurrenceSets) {
		if (getOccurrenceSets() == null)
			occurrenceSets = new ArrayList<OccurrenceSet>();
		getOccurrenceSets().addAll(occurrenceSets);
	}
	
	public int getCoverage() throws Exception {
		return getCoveredSet().size();
	}
	
	public int getUncompressedLength() throws Exception {
		return getCoverage() * getDimensionality();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(OccurrenceSet os : getOccurrenceSets()) {
			sb.append(os);
			sb.append("\n");
		}
		
		sb.append("\n\n");
		try {
			sb.append("Coverage: "+getCoverage());
			sb.append("\nDataset size: "+getDatasetSize());
			sb.append("\nUncompressed length: "+getUncompressedLength());
			sb.append("\nEncoding length: "+getEncodingLength());
			sb.append("\nCompression factor: "+getCompressionFactor());
			sb.append("\nRunning time in milliseconds: "+getRunningTimeInMillis());
			sb.append("\nEncoding length without residual point set: "+getEncodingLengthWithoutResidualPoints());
			sb.append("\nNumber of residual points: "+getNumberOfResidualPoints());
			sb.append("\nPercentage of residual points: "+String.format("%.2f",getPercentageOfResidualPoints()) + "%");
			sb.append("\nCompression factor without residual point set: "+ getCompressionFactorWithoutResidualPoints());
			sb.append("\nNumber of occurrence sets: "+getNumberOfOccurrenceSets()+"\n");
	} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	public void setRunningTimeInMillis(long runningTime) {
		this.runningTime = runningTime;
	}
	
	public long getRunningTimeInMillis() {
		return runningTime;
	}
	
	public int getNumberOfResidualPoints() {
		if (getOccurrenceSets().size() == 0) return 0;
		OccurrenceSet lastOS = getOccurrenceSets().get(getNumberOfOccurrenceSets()-1);
		if (lastOS.getTransformations() == null || lastOS.getTransformations().size() == 0)
			return lastOS.getPattern().size();
		return 0;
	}
	
	public double getPercentageOfResidualPoints() {
		return getNumberOfResidualPoints() * 100.0 / getDatasetSize();
	}
	
	public OccurrenceSet getResidualPointSetAsOccurrenceSet() {
		OccurrenceSet lastOS = getOccurrenceSets().get(getNumberOfOccurrenceSets()-1);
		if (lastOS.getTransformations() == null || lastOS.getTransformations().size() == 0)
			return lastOS;
		return null;
	}
	
	public PointSet getResidualPointSet() {
		OccurrenceSet os = getResidualPointSetAsOccurrenceSet();
		if (os != null)
			return os.getPattern();
		return new PointSet();
	}
	
	public int getEncodingLengthWithoutResidualPoints() throws Exception {
		return getEncodingLength() - getResidualPointSet().size() * getDimensionality();
	}
	
	public double getCompressionFactorWithoutResidualPoints() throws Exception {
		return 1.0*(getCoverage()-getNumberOfResidualPoints())*getDimensionality()/getEncodingLengthWithoutResidualPoints();
	}
	public PointSet getCoveredSet() throws Exception {
		PointSet coveredSet = new PointSet();
		for(OccurrenceSet os : getOccurrenceSets())
			coveredSet.addAll(os.getCoveredSet());
		return coveredSet;
	}
	
	public int getEncodingLength() throws Exception {
		int length = 0;
		for(OccurrenceSet os : getOccurrenceSets())
			length += os.getEncodingLength();
		return length;
	}
	
	public int getDimensionality() {
		return getOccurrenceSets().get(0).getPattern().getDimensionality();
	}
	
	public double getCompressionFactor() throws Exception {
		return (1.0*getUncompressedLength())/getEncodingLength();
	}

	public ArrayList<ArrayList<com.chromamorph.points022.PointSet>> getOccurrenceSetsAsArrayListsOfPointSets() {
		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> occSets = new ArrayList<ArrayList<com.chromamorph.points022.PointSet>>();
	
		for (OccurrenceSet os : getOccurrenceSets()) {
			ArrayList<com.chromamorph.points022.PointSet> thisOccSet = new ArrayList<com.chromamorph.points022.PointSet>();
			occSets.add(thisOccSet);
			
//			Add pattern for this occurrence set
			com.chromamorph.points022.PointSet thisOcc = new com.chromamorph.points022.PointSet();
			TreeSet<Point> points = os.getPattern().getPoints();
			for(Point p : points) {
				thisOcc.add(new com.chromamorph.points022.Point((long)(Math.floor(p.get(0))),(int)(Math.floor(p.get(1)))));
			}
			thisOccSet.add(thisOcc);
			
			for (Transformation tran : os.getTransformations()) {
				thisOcc = new com.chromamorph.points022.PointSet();
				PointSet thisPattern = tran.phi(os.getPattern());
				points = thisPattern.getPoints();
				for(Point p : points) {
					thisOcc.add(new com.chromamorph.points022.Point((long)(Math.floor(p.get(0))),(int)(Math.floor(p.get(1)))));
				}
				thisOccSet.add(thisOcc);
			}
		}
		return occSets;
	}
	
	public void drawOccurrenceSets(String outputFilePath, boolean diatonicPitch) {
		final PointSet dataset = getOccurrenceSets().get(0).getDataset();
		final TreeSet<Point> points = dataset.getPoints();
		com.chromamorph.points022.PointSet ps = new com.chromamorph.points022.PointSet(); 
		for(Point p : points) {
			ps.add(new com.chromamorph.points022.Point((long)(Math.floor(p.get(0))),(int)(Math.floor(p.get(1)))));
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(
						ps,
						getOccurrenceSetsAsArrayListsOfPointSets(),
						outputFilePath,
						true);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	
}
