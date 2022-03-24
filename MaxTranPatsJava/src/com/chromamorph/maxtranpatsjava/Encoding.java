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
	
	public Encoding() {}
	
	public Encoding(ArrayList<OccurrenceSet> occurrenceSets) {
		setOccurrenceSets(occurrenceSets);
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
	
	public void setOccurrenceSets(ArrayList<OccurrenceSet> occurrenceSets) {
		this.occurrenceSets = occurrenceSets;
	}
	
	public ArrayList<OccurrenceSet> getOccurrenceSets() {
		return occurrenceSets;
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
			sb.append("Coverage: "+getCoverage()+"\n");
			sb.append("Uncompressed length: "+getUncompressedLength()+"\n");
			sb.append("Encoding length: "+getEncodingLength()+"\n");
			sb.append("Compression factor: "+getCompressionFactor()+"\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.append("Number of occurrence sets: "+getOccurrenceSets().size()+"\n");
		
		return sb.toString();
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
	
	public void drawOccurrenceSets(String outputFilePath) {
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
