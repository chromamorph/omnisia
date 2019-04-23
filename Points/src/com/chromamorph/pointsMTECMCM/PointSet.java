package com.chromamorph.pointsMTECMCM;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JFrame;

import com.chromamorph.notes.Note;
import com.chromamorph.notes.Notes;

import processing.core.PApplet;
import processing.core.PFont;

public class PointSet implements Comparable<PointSet>{
	private TreeSet<Point> points = new TreeSet<Point>();
	int drawWindowHeight = 600;
	int drawWindowWidth = 1440;
	private Integer bbArea = null, maxX = null, minX = null, maxY = null, minY = null;
	private Double temporalDensity = null;
	private boolean isMTEC = false;

	public PointSet() {}

	/**
	 * Reads in points from a .pts file.
	 * Each point is given on a separate line,
	 * with co-ordinates separated by white space.
	 * For example, the unit square would be 
	 * represented as follows:
	 * 
	 * 0 0
	 * 1 0
	 * 0 1
	 * 1 1
	 * 
	 * @param fileName
	 */
	public PointSet(String fileName) {
		if (fileName.toLowerCase().endsWith(".pts"))
			makePointsObjectFromPTSFile(fileName);
	}

	public void setIsMTEC(boolean isMTEC) {
		this.isMTEC = isMTEC;
	}
	
	public boolean isMTEC() {
		return isMTEC;
	}
	
	private void makePointsObjectFromPTSFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String l = br.readLine();
			while(l != null) {
				points.add(new Point(l));
				l = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PointSet(Notes notes, boolean diatonicPitch) {
		for(Note note : notes.getNotes()) {
			if (diatonicPitch)
				points.add(new Point(note.getOnset(),note.getPitch().getMorpheticPitch()));
			else
				points.add(new Point(note.getOnset(),note.getMidiNoteNumber()));
		}
	}

	public PointSet(PointSet pattern) {
		for(Point point : pattern.getPoints())
			points.add(point);
	}

	public PointSet(Point... somePoints) {
		for(Point point : somePoints)
			points.add(point);
	}

	public Point first() {
		return points.first();
	}

	public NavigableSet<Point> tail(Point fromPoint, boolean inclusive) {
		return points.tailSet(fromPoint,inclusive);
	}

	public TreeSet<Point> getPoints() {
		return points;
	}

	public PointSet diff(PointSet pointSet2) {
		PointSet newPoints = new PointSet();
		for(Point point : points)
			if (!pointSet2.contains(point))
				newPoints.add(point);
		return newPoints;
	}

	public boolean contains(Point point) {
		return points.contains(point);
	}

	public boolean contains(PointSet pointSet) {
		for (Point point : pointSet.getPoints())
			if (!contains(point)) return false;
		return true;
	}

	public void analyse(Codec encoder) {
		encoder.encode(this);
	}

	public boolean isEmpty() {
		return points.size() == 0;
	}

	public void add(Point point) {
		points.add(point);
	}
	
	public void addAll(PointSet pointSet) {
		for(Point p : pointSet.getPoints())
			add(p);
	}

	public int getMaxX() {
		if (maxX != null) return maxX;
		int max = points.first().getX();
		for(Point point : points)
			if (point.getX() > max) max = point.getX();
		return (maxX = max);
	}

	public int getMaxY() {
		if (maxY != null) return maxY;
		int max = points.first().getY();
		for(Point point : points)
			if (point.getY() > max) max = point.getY();
		return (maxY = max);
	}

	public int getMinX() {
		if (minX != null) return minX;
		int min = points.first().getX();
		for(Point point : points)
			if (point.getX() < min) min = point.getX();
		return (minX = min);
	}

	public int getMinY() {
		if (minY != null) return minY;
		int min = points.first().getY();
		for(Point point : points)
			if (point.getY() < min) min = point.getY();
		return (minY = min);
	}

	class DrawPoints extends PApplet {

		private static final long serialVersionUID = 1L;

		PFont font;
		float margin = 60;
		int maxPitch;
		int maxTime;
		float maxX;
		float maxY;
		int minPitch;
		int minTime;
		float minX;
		float minY;
		float theta = 0f;
		float xScale, yScale, scale;
		ArrayList<TEC> tecs;
		int tecIndex = 0;
		TEC tec;

		DrawPoints(Collection<TEC> tecs2) {
			super();
			this.tecs = new ArrayList<TEC>(tecs2);
			tec = this.tecs.get(tecIndex);
		}

		public void draw() {
			background(255);
			drawAxes();
			//			System.out.println("Axes drawn");
			for(Point n : points) {
				drawPoint(n);
			}
			drawTEC();
		}

		public void keyPressed() {
			if (tecIndex < tecs.size()-1) {
				tecIndex++;
				tec = tecs.get(tecIndex);
				System.out.println(String.format("%.2f", tec.getCompactness())+": "+String.format("%.2f",tec.getCompressionRatio())+": "+tec);
			} else {
				System.out.println("Reached end of TECs!!!!! Starting again...");
				tecIndex = 0;
				tec = tecs.get(tecIndex);
				System.out.println(String.format("%.2f", tec.getCompactness())+": "+String.format("%.2f",tec.getCompressionRatio())+": "+tec);
			}
		}
		
		private void drawTEC() {
			int col = color(255,0,0);
			if (tec.isDual() && !tec.isMTEC())
				col = color(0,0,255);
			if (tec.isMTEC() && !tec.isDual())
				col = color(0,255,0);
			if (tec.isMTEC() && tec.isDual())
				col = color(0,255,255);
			fill(col);
			stroke(col);
			strokeWeight(1);
			if (tec == null) System.out.println("tec is null: "+tec);
			TreeSet<Point> tecPatternPoints = tec.getPattern().getPoints();
			if (tecPatternPoints == null)
				System.out.println("TEC pattern is null: "+tecPatternPoints);
			for(Point p : tecPatternPoints)
				drawPoint(p,col,col,1,ROUND,2,2);
			Point topLeft = tec.getPattern().getTopLeft();
			Point bottomRight = tec.getPattern().getBottomRight();
			drawRectangle(topLeft,bottomRight);
			for(Vector v : tec.getTranslators().getVectors()) {
				for(Point p : tec.getPattern().getPoints()) {
					drawPoint(p.translate(v),col,col,1f,ROUND,2,2);
				}
				Point tl = topLeft.translate(v), br = bottomRight.translate(v); 
				drawRectangle(tl,br);
			}
		}
		
		private void drawRectangle(Point topLeft, Point bottomRight) {
			fill(0,0,0,0);
			float leftX = topLeft.getX()*scale+minX; 
			float rightX = bottomRight.getX()*scale+minX;
			float topY = maxY-topLeft.getY()*scale;
			float bottomY = maxY-bottomRight.getY()*scale;
			line(leftX,topY,rightX,topY);
			line(leftX,topY,leftX,bottomY);
			line(rightX,topY,rightX,bottomY);
			line(rightX,bottomY,leftX,bottomY);
		}
		
		private void drawAxes() {
			stroke(0);
			fill(0);
			strokeWeight(1);
			strokeCap(SQUARE);
			int xSep = (int)max(maxX/(12*scale),1);
			int ySep = (int)max(maxY/(12*scale),1);
			//			System.out.println(minX+" "+minY+" "+maxX+" "+maxY);
			line(minX,maxY,minX,constrain(maxY-maxPitch*scale,minY,maxY));
			//			System.out.println("Drawn Y axis");
			line(minX,maxY,constrain(minX+maxTime*scale,minX,maxX),maxY);
			//			System.out.println("Drawn X axis");
			textAlign(RIGHT,CENTER);
			//			System.out.println(maxPitch+" "+ySep);
			for(float p = 0;p <= maxPitch; p += ySep) {
				float y = maxY-scale*p;
				text(((int)p),margin-10,y);
				line(margin-10,y,minX,y);
				//				line(minX,y,maxX,y);
			}
			//			System.out.println("Finished first for loop");
			textAlign(CENTER,TOP);
			for(float t = 0;t <= maxTime; t += xSep) {
				float x = minX+scale*t;
				text(((int)t),x,maxY+10);
				line(x,maxY,x,maxY+10);
			}
			//			System.out.println("Finished second for loop");
			pushMatrix();
			translate(margin/3,constrain(maxY-maxPitch*scale,minY,maxY));
			//			rotate(-PI/2);
			textAlign(CENTER,CENTER);
			text("y",0,0);
			popMatrix();
			pushMatrix();
			translate(minX+maxTime*scale,maxY+2*margin/3);
			textAlign(CENTER,CENTER);
			text("x",0,0);
			popMatrix();

		}

		private void drawPoint(Point n, int stroke, int fill, float strokeWeight, int strokeCap, float width, float height) {
			stroke(stroke);
			fill(fill);
			strokeWeight(strokeWeight);
			strokeCap(strokeCap);
			float x = minX+n.getX()*scale;
			float y = maxY-n.getY()*scale;
			ellipse(x,y,width,height);
		}
		
		private void drawPoint(Point n) {
			drawPoint(n,color(100,100,100),color(100,100,100),2f,ROUND,2,2);
		}

		public void setup() {
			minX = margin;
			maxX = drawWindowWidth - margin;
			minY = margin;
			maxY = drawWindowHeight - margin;
			maxPitch = max(getMaxY(),1);
			maxTime = max(getMaxX(),1);
			minPitch = min(getMinY(),0);
			minTime = getMinX();

			xScale = 1.0f*(maxX-minX)/(maxTime);
			yScale = 1.0f*(maxY-minY)/(maxPitch);
			scale = min(xScale,yScale);
//						System.out.println("scale = "+scale+"; maxPitch = "+maxPitch+"; maxTime = "+maxTime+"; minPitch = "+minPitch+"; minTime = "+minTime);
			size(drawWindowWidth,drawWindowHeight);
			smooth();
			background(255);
			font = createFont("Arial", 14);
			textFont(font);
			System.out.println(String.format("%.2f", tec.getCompactness())+": "+String.format("%.2f",tec.getCompressionRatio())+": "+tec);
		}
	}

	public void draw() {
		draw(null);
	}
	
	public void draw(final Collection<TEC> tecs) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(drawWindowWidth,drawWindowHeight+23));
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PApplet embed = new DrawPoints(tecs);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public String toString() {
		if (isEmpty()) return "P()";
		if (size() == 1) return points.first().toString();
		StringBuilder sb = new StringBuilder("P("+points.first());
		for(Point point : points.tailSet(points.first(), false))
			sb.append(","+point);
		sb.append(")");
		if (isMTEC()) sb.append("[MTEC]");
		return sb.toString();
	}

	protected void play(Integer msPerTatum) {
		try {
			int ticksPerQuarterNote = 500;
			int msPerTick = 1;
			Sequence sequence = new Sequence(Sequence.PPQ,ticksPerQuarterNote);
			sequence.createTrack();
			Track track = sequence.getTracks()[0];
			for(Point point : points) {
				int midiNoteNumber = point.getY();
				ShortMessage noteOnMessage = new ShortMessage();
				noteOnMessage.setMessage(ShortMessage.NOTE_ON,midiNoteNumber,96);
				ShortMessage noteOffMessage = new ShortMessage();
				noteOffMessage.setMessage(ShortMessage.NOTE_ON,midiNoteNumber,0);
				long noteOnTick = point.getX() * msPerTatum / msPerTick;
				long noteOffTick = noteOnTick + msPerTatum;
				MidiEvent noteOnEvent = new MidiEvent(noteOnMessage,noteOnTick);
				MidiEvent noteOffEvent = new MidiEvent(noteOffMessage, noteOffTick);
				track.add(noteOnEvent);
				track.add(noteOffEvent);
			}
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.setSequence(sequence);
			sequencer.open();
			sequencer.start();
			System.out.println("Press ENTER when finished playing.");
			System.in.read();
			sequencer.stop();
			sequencer.close();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int size() {
		return getPoints().size();
	}

	public PointSet copy() {
		PointSet newPoints = new PointSet();
		for(Point point : points)
			newPoints.add(point.copy());
		return newPoints;
	}

	@Override
	public int compareTo(PointSet pointSet) {
		if (pointSet == null) return 1;
		int d = pointSet.size() - size();
		if (d != 0) return d;
		//Same size
		ArrayList<Point> a1 = new ArrayList<Point>(points);
		ArrayList<Point> a2 = new ArrayList<Point>(pointSet.getPoints());
		for(int i = 0; i < a1.size(); i++) {
			d = a1.get(i).compareTo(a2.get(i));
			if (d != 0) return d;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof PointSet)) return false;
		return compareTo((PointSet)obj) == 0;
	}

	public double getCompactness(PointSet dataSet) {
		double N_D = dataSet.getBBSubset(getTopLeft(),getBottomRight()).size();
		double N_P = size();
		double C = N_P/N_D;
		return C;
	}
	
	public boolean translationallyEquivalentTo(PointSet otherPointSet) {
		if (otherPointSet == null) return false;
		if (otherPointSet.size() != size()) return false;
		Vector vector = null;
		for(int i = 0; i < size(); i++) {
			if (vector == null)
				vector = new Vector(get(i),otherPointSet.get(i));
			else if (!vector.equals(new Vector(get(i),otherPointSet.get(i))))
				return false;
		}
		return true;
	}

	public Point get(int i) {
		ArrayList<Point> a = new ArrayList<Point>(points);
		return a.get(i);
	}

	public PointSet translate(Vector vector) {
		PointSet newPoints = new PointSet();
		for(Point point : points)
			newPoints.add(point.translate(vector));
		return newPoints;
	}

	public PointSet getBBSubset(Point topLeft, Point bottomRight) {
		PointSet bbSubset = new PointSet();
		Point bottomLeft = new Point(topLeft.getX(),bottomRight.getY());
		Point topRight = new Point(bottomRight.getX(),topLeft.getY());
		for(Point point : points.subSet(bottomLeft, true, topRight, true)) {
			if (point.getY() >= bottomLeft.getY() && point.getY() <= topRight.getY())
				bbSubset.add(point);
		}
		return bbSubset;
	}
	
	public int getBBArea() {
		if (bbArea != null) return bbArea;
		return (bbArea = (getMaxX() - getMinX()) * (getMaxY() - getMinY()));
	}
	
	public Point getTopLeft() {
		return new Point(getMinX(),getMaxY());
	}

	public Point getTopRight() {
		return new Point(getMaxX(),getMaxY());
	}
	
	public Point getBottomLeft() {
		return new Point(getMinX(),getMinY());
	}

	public Point getBottomRight() {
		return new Point(getMaxX(),getMinY());
	}

	public double getEccentricity() {
		double sumX = 0.0;
		double sumY = 0.0;
		for(Point p : points) {
			sumX += p.getX();
			sumY += p.getY();
		}
		double meanX = sumX/points.size();
		double meanY = sumY/points.size();
		double centreX = getMinX()+(getMaxX()-getMinX())/2.0;
		double centreY = getMinY()+(getMaxY()-getMinY())/2.0;
		double deltaX = meanX-centreX;
		double deltaY = meanY-centreY;
		double width = getMaxX()-getMinX();
		double height = getMaxY()-getMinY();
		return (Math.abs(deltaX)/width)+(Math.abs(deltaY)/height);
	}

	public double getTemporalDensity() {
		if (temporalDensity != null) return temporalDensity;
		TreeSet<Integer> xValues = new TreeSet<Integer>();
		for(Point point : points)
			xValues.add(point.getX());
		return (temporalDensity = (1.0*xValues.size())/(getMaxX()-getMinX()));
	}
	
}
