package com.chromamorph.points022;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JFrame;

import com.chromamorph.maths.Rational;
import com.chromamorph.notes.Note;
import com.chromamorph.notes.Notes;
import com.chromamorph.notes.Notes.MissingTieStartNoteException;
import com.chromamorph.pitch.Pitch;

import processing.core.PApplet;

public class PointSet implements Comparable<PointSet>{
	protected TreeSet<Point> points = new TreeSet<Point>();
	protected Long bbArea = null, maxX = null, minX = null, maxXIncludingDuration;
	protected Integer maxY = null, minY = null;
	protected Double temporalDensity = null;
	private Long ticksPerSecond = null;
	static public JFrame frame = null;
	protected int[] colArray = null;
	protected int[] colArray2 = null;
	protected String label = null;

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
	 * @throws MissingTieStartNoteException 
	 * @throws RationalOnsetTimeException 
	 */

	public PointSet(String fileName) throws MissingTieStartNoteException {
		this(fileName,false);
	}

	public PointSet(String fileName, boolean isDiatonic) throws MissingTieStartNoteException {
		this(fileName,isDiatonic,false);
	}

	public static String[] INPUT_FILE_EXTENSIONS = new String[] {
		".pts", ".pointset", ".krn", ".opnd", ".opndv", ".opdv", ".midi", ".mid",
		".notes", ".png", ".xml", ".musicxml",
	};
	
	public PointSet(String fileName, boolean isDiatonic, boolean withoutChannel10) throws MissingTieStartNoteException {
		if (fileName.toLowerCase().endsWith(".pts"))
			makePointsObjectFromPTSFile(fileName);
		else if (fileName.toLowerCase().endsWith(".pointset"))
			makePointSetObjectFromPointSetFile(fileName);
		else if (fileName.toLowerCase().endsWith(".krn"))
			makePointSetObjectFromKernFile(fileName, isDiatonic);
		else if (fileName.toLowerCase().endsWith(".opnd") || fileName.toLowerCase().endsWith(".opndv"))
			makePointSetObjectFromOPNDFile(fileName, isDiatonic);
		else if (fileName.toLowerCase().endsWith(".midi") || fileName.toLowerCase().endsWith(".mid"))
			makePointSetObjectFromMIDIFile(fileName, isDiatonic, withoutChannel10);
		else if (fileName.toLowerCase().endsWith(".notes"))
			makePointSetObjectFromNotesFile(fileName, isDiatonic);
		else if (fileName.toLowerCase().endsWith(".png"))
			makePointSetObjectFromPNGFile(fileName);
		else if (fileName.toLowerCase().endsWith(".gv"))
			makePointSetObjectFromGVFile(fileName);
		else if (fileName.toLowerCase().endsWith(".txt") || fileName.toLowerCase().endsWith(".opdv"))
			makePointSetObjectFromCollinsLispFile(fileName, isDiatonic);
		else if (fileName.toLowerCase().endsWith(".xml") || fileName.toLowerCase().endsWith(".musicxml"))
			makePointSetObjectFromMusicXMLFile(fileName, isDiatonic);
		else if (fileName.toLowerCase().endsWith(".masom"))
			makePointSetObjectFromMasomFile(fileName);
	}

	private void makePointSetObjectFromMasomFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			int time = 0;
			String l = br.readLine();
			while (l != null && !l.isEmpty()) {
				int midiNoteNumber = Integer.parseInt(l.substring(3, 6));
				Point p = new Point(time, midiNoteNumber);
				points.add(p);
				time++;
				l = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void makePointSetObjectFromMusicXMLFile(String fileName, boolean isDiatonic) {
//		Read XML file into a StringBuilder
		BufferedReader br;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(fileName));
			String l ;
			while ((l = br.readLine()) != null)
				sb.append(l);
			br.close();
//			Check that the file is partwise
			String text = sb.toString();
			if (text.indexOf("score-partwise") >= 0) {
//				Check that there is only one part
				int i = 0;
				int lastIndex = 0;
				while ((lastIndex = text.indexOf("</score-part>", lastIndex+1)) >= 0) i++;
				if (i == 1) {
//					Read list of <pitch-name, duration> pairs
					ArrayList<PitchDurationPair> pdps = new ArrayList<PitchDurationPair>();
					int noteIndex = 0; 
					for (int j = 0; j < text.length(); j++) {
						if (j+7 < text.length() && text.substring(j,j+7).equals("<backup")) {
							int backupIndex = j;
							int backupEndIndex = text.indexOf("</backup>",backupIndex);
							String backupString = text.substring(backupIndex,backupEndIndex);
							
							int backupDurationStart = backupString.indexOf("<duration>") + 10;
							int backupDurationEnd = backupString.indexOf("</duration>",backupDurationStart);
							String backupDurationString = backupString.substring(backupDurationStart,backupDurationEnd);
							
							int backupDuration = Integer.parseInt(backupDurationString);
							pdps.add(new PitchDurationPair(backupDuration));
							
						} else if (j+7 < text.length() && text.substring(j,j+7).equals("<ending")) {
							int endingIndex = j;
							int endingEndIndex = text.indexOf("/>",endingIndex);
							String endingString = text.substring(endingIndex, endingEndIndex);
							
							int endingNumberIndex = endingString.indexOf("number=") + 8;
							int endingNumberEndIndex = endingString.indexOf("\"",endingNumberIndex);
							int endingNumber = Integer.parseInt(endingString.substring(endingNumberIndex,endingNumberEndIndex));
							
							boolean isEndingStart = true;
							if (endingString.indexOf("type=\"start\"") < 0)
								isEndingStart = false;
							pdps.add(new PitchDurationPair(isEndingStart,endingNumber));
						} else if (j+7 < text.length() && text.substring(j,j+7).equals("<repeat")) {
							int repeatIndex = j;
							int repeatEndIndex = text.indexOf("/>",repeatIndex);
							String repeatString = text.substring(repeatIndex,repeatEndIndex);
							boolean isBackward = true;
							if (repeatString.indexOf("backward") < 0)
								isBackward = false;
							pdps.add(new PitchDurationPair(isBackward));
						} else if (j+5 < text.length() && text.substring(j,j+5).equals("<note")) {
							noteIndex = j;
							Pitch pitch = null;
							int noteEndIndex = text.indexOf("</note>",noteIndex+1);
							String noteSubstring = text.substring(noteIndex, noteEndIndex);
							if (noteSubstring.contains("<grace/>")) continue;
							int pitchIndex = noteSubstring.indexOf("<pitch");
//							Either a pitch...
							if (pitchIndex >= 0 && noteSubstring.indexOf("<tied type=\"stop\"/>") < 0) {
//								Find step and octave and compute Pitch object
								int pitchEndIndex = noteSubstring.indexOf("</pitch>");
								String pitchSubstring = noteSubstring.substring(pitchIndex, pitchEndIndex);

								int stepIndex = pitchSubstring.indexOf("<step>")+6;
								int stepEndIndex = pitchSubstring.indexOf("</step>");
								String stepString = pitchSubstring.substring(stepIndex,stepEndIndex);
								
								int octaveIndex = pitchSubstring.indexOf("<octave>")+8;
								int octaveEndIndex = pitchSubstring.indexOf("</octave>");
								String octaveString = pitchSubstring.substring(octaveIndex,octaveEndIndex);
								
								int alterIndex = pitchSubstring.indexOf("<alter>");
								int alter = 0;
								if (alterIndex >= 0) {
									alterIndex += 7;
									int alterEndIndex = pitchSubstring.indexOf("</alter>");
									String alterString = pitchSubstring.substring(alterIndex,alterEndIndex);
									alter = Integer.parseInt(alterString);
								}
								
								String accidentalString = "";
								if (alter == 0)
									accidentalString += "n";
								else
									for(int k = 0; k < Math.abs(alter); k++)
										accidentalString += (alter < 0)?"f":"s";
								String pitchName = stepString + accidentalString + octaveString;
								
								pitch = new Pitch();
								pitch.setPitchName(pitchName);
							}
//							Find duration
							int durationIndex = noteSubstring.indexOf("<duration>")+10;
							int durationEndIndex = noteSubstring.indexOf("</duration>");
//							if (durationEndIndex == -1) {
//								int bicycle = 1;
//							}
							int duration = Integer.parseInt(noteSubstring.substring(durationIndex, durationEndIndex));
							
//							Compute PitchDurationPair and add to list pdps
							pdps.add(new PitchDurationPair(pitch, duration));
						}
					}					
					
					for(PitchDurationPair pdp : pdps)
						System.out.println(pdp);
					
//					Convert list of <pn,d> pairs to PointSet, using isDiatonic
					PointSet pointSet = new PointSet();
					long onset = 0;
					int endingNumber = 0;
					boolean inPreviousEnding = false;
					int indexOfPrevForwardRepeat = 0;
					ArrayList<Integer> doneBackRepeats = new ArrayList<Integer>();
					ArrayList<Integer> doneForwardRepeats = new ArrayList<Integer>();
					for(int m = 0; m < pdps.size(); m++) {
//						if (m == pdps.size()-10) {
//							int blab = 2;
//						}
						PitchDurationPair pdp = pdps.get(m);
						if (pdp.isBackup())
							onset -= pdp.getBackupDuration();
						else if (pdp.getPitch() != null && !inPreviousEnding) {
							int p = isDiatonic?pdp.getPitch().getMorpheticPitch():pdp.getPitch().getChromaticPitch();
							Point point = new Point(onset,p);
							pointSet.add(point);
						} else if (pdp.getPitch() == null && pdp.isEnding()) {
							if (pdp.isEndingStart()) {
								if (pdp.getEndingNumber() > endingNumber) {
									endingNumber = pdp.getEndingNumber();
								} else
									inPreviousEnding = true;
							} else { //is ending stop
								inPreviousEnding = false;
							}
						} else if (pdp.getPitch() == null && pdp.isRepeat()) {
							if (pdp.isBackward() && !doneBackRepeats.contains(m)) {
								doneBackRepeats.add(m);
								m = indexOfPrevForwardRepeat-1;
								doneForwardRepeats.add(indexOfPrevForwardRepeat);
							} else if (pdp.isForward() && !doneForwardRepeats.contains(m)) {
								indexOfPrevForwardRepeat = m;
								endingNumber = 0;
							}
						}
						if (!inPreviousEnding)
							onset += pdp.getDuration();			

					}
					points = pointSet.points;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void makePointSetObjectFromCollinsLispFile(String collinsLispFilePathName, boolean isDiatonic) {
		MIREX2013Entries.readLispFileIntoPointSet(collinsLispFilePathName, isDiatonic);
		points = MIREX2013Entries.DATASET.points;
	}
	
	private void makePointSetObjectFromGVFile(String gvFilePathName) {
		PointSet ps = new PointSet();
		try {
			BufferedReader br = new BufferedReader(new FileReader(gvFilePathName));
			String l;
			l = br.readLine();
			int y = 0;
			while (l != null) {
				String[] a = l.split(",");
				for(int x = 0; x < a.length; x++)
					if (a[x].equals("1") && (x==0 || !a[x-1].equals("1")))
						ps.add(new Point(x,y));
				l = br.readLine();
				y++;
			}
			br.close();
			points = ps.points;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void makePointSetObjectFromPNGFile(String pngFilePathName) {
		try {
			PointSet pts = new PointSet();
			BufferedImage img = ImageIO.read(new File(pngFilePathName));
			int imgType = img.getType();
			if (imgType==BufferedImage.TYPE_BYTE_BINARY)
				System.out.println("Image type is binary");
			else if (imgType==BufferedImage.TYPE_BYTE_GRAY)
				System.out.println("Image type is GRAY");
			System.out.println("Image height is "+img.getHeight());
			System.out.println("Image width is "+img.getWidth());
			for(int x = 0; x < img.getWidth(); x++)
				for(int y = 0; y < img.getHeight(); y++) {
					if (img.getRGB(x, y)==-1 && (x==0 || img.getRGB(x-1, y)!= -1))
						pts.add(new Point(x,y));
				}
			points = pts.points;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void makePointSetObjectFromNotesFile(String filename, boolean isDiatonic) {
		try {
			Notes notes = new Notes(new File(filename));
			PointSet pointSet = new PointSet(notes, isDiatonic);
			points = pointSet.points;
			ticksPerSecond= pointSet.getTicksPerSecond();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		}
	}
	
	private void makePointSetObjectFromMIDIFile(String fileName, boolean isDiatonic, boolean withoutChannel10) {
		try{
			PointSet pointSet = new PointSet(Notes.fromMIDI(fileName,true,withoutChannel10),isDiatonic);
			points = pointSet.points;
			ticksPerSecond = pointSet.getTicksPerSecond();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void makePointSetObjectFromOPNDFile(String fileName, boolean isDiatonic) {
		try {
			PointSet pointSet = new PointSet(Notes.fromOPND(fileName),isDiatonic);
			points = pointSet.points;
		} catch (NoMorpheticPitchException | IOException e) {
			e.printStackTrace();
		}
	}

	private void makePointSetObjectFromKernFile(String kernFilePathName, boolean isDiatonic) throws MissingTieStartNoteException {
		try {
			PointSet pointSet = new PointSet(Notes.fromKern(kernFilePathName),isDiatonic);
			points = pointSet.points;
		} catch (NoMorpheticPitchException | IOException e) {
			e.printStackTrace();
		}
	}

	private void makePointSetObjectFromPointSetFile(String pointSetFilePathName) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(pointSetFilePathName));
			StringBuilder sb = new StringBuilder();
			String l;
			while ((l = br.readLine()) != null)
				sb.append(l);
			br.close();

			PointSet pointSet = PointSet.getPointSetFromString(sb.toString());
			for(Point point : pointSet.getPoints())
				add(point);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	public PointSet(String fileName, PitchRepresentation pitchRepresentation) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		boolean diatonicPitch = true;
		if (pitchRepresentation.equals(PitchRepresentation.CHROMATIC_PITCH))
			diatonicPitch = false;
		if (fileName.toLowerCase().endsWith(".pointset"))
			makePointSetObjectFromPointSetFile(fileName);
		else if (fileName.toLowerCase().endsWith("pts"))
			makePointsObjectFromPTSFile(fileName);
		else if (fileName.toLowerCase().endsWith("opnd"))
			getPointSetFromNotes(Notes.fromOPND(fileName),diatonicPitch);
		else if (fileName.toLowerCase().endsWith("mid") || fileName.toLowerCase().endsWith("midi")) {
			Notes notes = Notes.fromMIDI(fileName,diatonicPitch);
			getPointSetFromNotes(notes,diatonicPitch);
		} else if (fileName.toLowerCase().endsWith("txt")) { //assume Collins lisp format
			MIREX2013Entries.readLispFileIntoPointSet(fileName);
			points = MIREX2013Entries.DATASET.getPoints();
		} else if (fileName.toLowerCase().endsWith("krn")) { //assume kern format
			points = new PointSet(fileName,diatonicPitch).points;
		} 
		else
			throw new UnimplementedInputFileFormatException(fileName+" is not of a known input file type");
	}

	public PointSet(Notes notes, boolean diatonicPitch) throws NoMorpheticPitchException {
		getPointSetFromNotes(notes,diatonicPitch);
	}

	private void getPointSetFromNotes(Notes notes, boolean diatonicPitch) throws NoMorpheticPitchException {
		setTicksPerSecond(notes.getTicksPerSecond());
		//		System.out.println("in PointSet, ticksPerSecond == "+ticksPerSecond);
		for(Note note : notes.getNotes()) {
			Integer voice = note.getVoice();
			Long duration = note.getDuration();
			if (voice == null)
				voice = note.getChannel();
			if (diatonicPitch) {
				Integer morpheticPitch = note.getPitch().getMorpheticPitch();
				if (morpheticPitch == null)
					morpheticPitch = note.getComputedPitch().getMorpheticPitch();
				if (morpheticPitch == null)
					throw new NoMorpheticPitchException("The following note has no morphetic pitch: "+note);
				points.add(new Point(note.getOnset(),morpheticPitch,voice,duration));
			}
			else
				points.add(new Point(note.getOnset(),note.getPitch().getChromaticPitch(),voice,duration));
		}
	}

	public Long getTicksPerSecond() {
		return ticksPerSecond;
	}

	public void setTicksPerSecond(Long ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
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
	
	public PointSet getScalexiaPointSet() {
		PointSet ps = new PointSet();
		for(Point p : getPoints()) {
			Point newPoint = new Point(2 * p.getX() + p.getDuration(),p.getY());
			ps.add(newPoint);
		}
		return ps;
	}

	/**
	 * a.diff(b) returns the set of points in a 
	 * that are not in b.
	 * @param pointSet2
	 * @return
	 */
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

	public Encoding encode(Encoder encoder) {
		return encoder.encode(this);
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

	public Long getMaxX() {
		if (maxX != null || points == null || points.isEmpty()) return maxX;
		long max = points.first().getX();
		for(Point point : points)
			if (point.getX() > max) max = point.getX();
		return (maxX = max);
	}
	
	/**
	 * Returns the ending time in tatums of the note that ends last in the piece.
	 * @return
	 */
	public Long getMaxXIncludingDuration() {
		if (maxXIncludingDuration != null || points == null || points.isEmpty()) return maxX;
		Point firstPoint = points.first();
		long max = firstPoint.getX()+firstPoint.getDuration();
		for(Point point : points)
			if (point.getX()+point.getDuration() > max) max = point.getX()+point.getDuration();
		return (maxXIncludingDuration = max);		
	}

	public Integer getMaxY() {
		if (maxY != null || points == null || points.isEmpty()) return maxY;
		if (points.isEmpty())
			System.out.println("points is empty and shouldn't be");
		int max = points.first().getY();
		for(Point point : points)
			if (point.getY() > max) max = point.getY();
		return (maxY = max);
	}

	public Long getMinX() {
		if (minX != null || points == null || points.isEmpty()) return minX;
		long min = points.first().getX();
		for(Point point : points)
			if (point.getX() < min) min = point.getX();
		return (minX = min);
	}

	public Integer getMinY() {
		if (minY != null || points == null || points.isEmpty()) return minY;
		int min = points.first().getY();
		for(Point point : points)
			if (point.getY() < min) min = point.getY();
		return (minY = min);
	}

	public void draw() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(PointSet.this);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void draw(final String title, final boolean diatonicPitch) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setTitle(title);
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PApplet embed = new DrawPoints(PointSet.this,title,diatonicPitch);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public JFrame draw(final String title) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame();
				frame.setTitle(title);
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(PointSet.this);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
		return frame;
	}

	public JFrame draw(final String title, final boolean diatonicPitch, final boolean saveImageFile) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame();
				frame.setTitle(title);
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(PointSet.this, diatonicPitch, saveImageFile);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
		return frame;
	}

	public JFrame draw(final String title, final boolean diatonicPitch, final boolean saveImageFile, final String outputFilePath) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame();
				frame.setTitle(title);
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(PointSet.this, diatonicPitch, saveImageFile, outputFilePath);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
		return frame;
	}


	public void draw(final TreeSet<PatternPair> patternPairs) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PApplet embed = new DrawPoints(PointSet.this, patternPairs);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void draw(final Collection<TEC> tecs) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PApplet embed = new DrawPoints(PointSet.this,tecs);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void draw(final String title, final ArrayList<PointSetCollectionPair> arrayListOfPointSetCollectionPairs) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				frame.setTitle(title);
				//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PApplet embed = new DrawPoints(PointSet.this,arrayListOfPointSetCollectionPairs);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public String toString() {
		if (isEmpty()) return "P()";
		StringBuilder sb = new StringBuilder("P("+points.first());
		for(Point point : points.tailSet(points.first(), false))
			sb.append(","+point);
		sb.append(")");
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

	public double getCompactness(PointSet dataSet, CompactnessType compactnessType) {
		if (compactnessType.equals(CompactnessType.SEGMENT)) {
			PointSet segment = dataSet.getSegment(getMinX(), getMaxX(), true);
			return size()*1.0/segment.size();
		} 
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

	public PointSet getInversion() {
		PointSet inversion = new PointSet();
		for(Point point : points)
			inversion.add(point.getInversion());
		return inversion;
	}

	public PointSet getRetrograde() {
		PointSet retrograde= new PointSet();
		for(Point point : points)
			retrograde.add(point.getRetrograde());
		return retrograde;
	}

	public PointSet getBBSubset(Point topLeft, Point bottomRight) {
		if (topLeft.getX() > bottomRight.getX())
			System.out.println("topLeft to right of bottomRight in getBBSubset: "+topLeft+", "+bottomRight);
		PointSet bbSubset = new PointSet();
		Point bottomLeft = new Point(topLeft.getX(),bottomRight.getY());
		Point topRight = new Point(bottomRight.getX(),topLeft.getY());
		for(Point point : points.subSet(bottomLeft, true, topRight, true)) {
			if (point.getY() >= bottomLeft.getY() && point.getY() <= topRight.getY())
				bbSubset.add(point);
		}
		return bbSubset;
	}

	public long getBBArea() {
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
		TreeSet<Long> xValues = new TreeSet<Long>();
		for(Point point : points)
			xValues.add(point.getX());
		return (temporalDensity = (1.0*xValues.size())/(getMaxX()-getMinX()));
	}

	public boolean removeWithoutReset(Point point) {
		return points.remove(point);
	}

	public boolean remove(Point point) {
		boolean setChanged = removeWithoutReset(point);
		if (setChanged) {
			bbArea = maxX = minX = null;
			maxY = minY = null;
			temporalDensity = null;
		}
		return setChanged;
	}

	public long getWidth() {
		return getMaxX() - getMinX();
	}

	public int getHeight() {
		return getMaxY() - getMinY();
	}

	public double getAspectRatio() {
		return (1.0 * getWidth())/getHeight();
	}

	/**
	 * Returns a measure of how "unsquare" the bounding box is.
	 * @return
	 */
	public double getUnsquareness() {
		return Math.abs(1.0 - getAspectRatio());
	}

	public boolean removeWithoutReset(PointSet otherPointSet) {
		boolean setChanged = false;
		for(Point point : otherPointSet.getPoints())
			if (points.contains(point)) {
				setChanged = true;
				removeWithoutReset(point);
			}
		return setChanged;
	}

	public boolean remove(PointSet otherPointSet) {
		boolean setChanged = removeWithoutReset(otherPointSet);
		if (setChanged) {
			bbArea = maxX = minX = null;
			maxY = minY = null;
			temporalDensity = null;
		}
		return setChanged;
	}

	public double getMinSIAMEuclidDistance(PointSet ps) {
		//From this to ps
		double fromThisToPS = 0.0;
		for (Point p1 : getPoints()) {
			Double leastDistance = null;
			for(Point p2 : ps.getPoints()) {
				double thisDistance = new Vector(p1,p2).getLength();
				if (leastDistance == null || thisDistance < leastDistance)
					leastDistance = thisDistance;
			}
			fromThisToPS += leastDistance;
		}
		//From ps to this
		double fromPSToThis = 0.0;
		for (Point p1 : ps.getPoints()) {
			Double leastDistance = null;
			for(Point p2 : getPoints()) {
				double thisDistance = new Vector(p1,p2).getLength();
				if (leastDistance == null || thisDistance < leastDistance)
					leastDistance = thisDistance;
			}
			fromPSToThis += leastDistance;
		}
		//return minimum
		return ((fromThisToPS<fromPSToThis)?fromThisToPS:fromPSToThis);
	}

	public PointSet intersection(PointSet ps2) {
		PointSet intersection = new PointSet();
		TreeSet<Point> thisSet = null, otherSet = null;
		if (size() <= ps2.size()) {
			thisSet = getPoints();
			otherSet = ps2.getPoints();
		} else {
			thisSet = ps2.getPoints();
			otherSet = getPoints();
		}
		for(Point p : thisSet) {
			if (otherSet.contains(p))
				intersection.add(p);
		}
		return intersection;
	}

	public PointSet getSegment(long startTime, long endTime, boolean inclusive) {
		PointSet segment = new PointSet();
		for(Point point : getPoints()) {
			if (point.getX() >= startTime && (inclusive?(point.getX() <= endTime):(point.getX() < endTime)))
				segment.add(point);
		}
		return segment;
	}

	public void writeToPtsFile(String fileName) throws IOException {
		PrintStream ps = new PrintStream(fileName);
		for(Point p : points)
			ps.println(p.getX()+" "+p.getY());
		ps.close();
	}

	public static PointSet getPointSetFromTECString(String tecString) {
		/*
		 * tecString has form:
		 * 
		 * T(P(p(x1,y1),p(x2,y2),...p(xn,yn)),V(v(u1,w1),v(u2,w2),...v(um,wm)))
		 */

		TEC tec = new TEC(tecString);
		return tec.getCoveredPoints();
	}

	public static PointSet getPointSetFromString(String l) {
		System.out.println(l);
		if (l.equals("P()"))
			return new PointSet();
		//		System.out.println("Trying to find PointSet in TEC: "+l);
		int startIndex = l.indexOf("p(");
		//		System.out.println("startIndex is "+startIndex);
		int endIndex = l.indexOf("),V(");
		if (endIndex == -1)
			endIndex = l.indexOf("))")+1;
		//		System.out.println("endIndex is "+endIndex+"("+l.substring(endIndex - 1,endIndex+5)+")");
		String pointSequence = l.substring(startIndex,endIndex);
		PointSet outputPointSet = new PointSet();
		for(int start = 0; start < pointSequence.length();) {
			int end = pointSequence.indexOf(")",start)+1;
			String pointString = pointSequence.substring(start, end);
			Point point = new Point(pointString);
			outputPointSet.add(point);
			start = end+1;
		}
		//		System.out.println(outputPointSet);
		return outputPointSet;
	}


	public ArrayList<Integer> getIndexSet(PointSet pattern) {
		if (pattern == null) return null;
		ArrayList<Integer> indexArray = new ArrayList<Integer>();
		ArrayList<Point> patternArray = new ArrayList<Point>(pattern.getPoints());
		int i = 0; //index into the dataset
		int j = 0; //index into the pattern
		for(Point point : points) {
			if (point.equals(patternArray.get(j))) {
				indexArray.add(i);
				j++;
				if (j >= pattern.size()) break;
			}
			i++;
		}
		return indexArray;
	}

	public void draw(final ArrayList<PointSet> patterns, final String windowTitle) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				frame.setTitle(windowTitle);
				PApplet embed = new DrawPoints(patterns,PointSet.this);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	//	public static PointSet getPointSetFromString(String pointSetString) {
	//		PointSet pointSet = new PointSet();
	//		String[] pointStrings = pointSetString.substring(pointSetString.indexOf('p'),pointSetString.lastIndexOf(')')).split(",");
	//		for(String pointString : pointStrings)
	//			pointSet.add(new Point(pointString));
	//		return pointSet;
	//	}

	/**
	 * Returns a new pattern formed by scaling this
	 * pattern by the x-axis scale factor f, with center
	 * of enlargement at the minimum point in this pattern.
	 * @param f
	 * @return
	 */
	public PointSet scale(Rational f) {
		PointSet imagePattern = new PointSet();
		Point minPoint = first();
		for(Point p : points) {
			Vector minPointToP = new Vector(minPoint,p);
			long minPointToPTimeComponent = minPointToP.getX();
			int minPointToPPitchComponent = minPointToP.getY();
			long imageOfMinPointToPTimeComponent = f.times(new Rational(minPointToPTimeComponent,1l)).getNumerator();
			Vector imageOfMinPointToP = new Vector(imageOfMinPointToPTimeComponent,minPointToPPitchComponent);
			Point q = minPoint.translate(imageOfMinPointToP);
			imagePattern.add(q);
		}
		return imagePattern;
	}

	public PointSet invert() {
		PointSet imagePattern = new PointSet();
		Point minPoint = first();
		for(Point p : points) {
			Vector minPointToP = new Vector(minPoint,p);
			long minPointToPTimeComponent = minPointToP.getX();
			int minPointToPPitchComponent = minPointToP.getY();
			Vector imageOfMinPointToP = new Vector(minPointToPTimeComponent,-minPointToPPitchComponent);
			Point q = minPoint.translate(imageOfMinPointToP);
			imagePattern.add(q);
		}
		return imagePattern;
	}

	public String getLatexString() {
		StringBuilder sb = new StringBuilder("\\lbrace");
		if (!isEmpty())
			sb.append(get(0).getLatexString());
		for(int i = 1; i < size(); i++)
			sb.append(","+get(i).getLatexString());
		sb.append("\\rbrace");
		return sb.toString();
	}
	
	public static void main(String[] args ) {
		//		boolean diatonicPitch = true;
		//		String filePathName = "/Users/dave/Documents/Work/Research/2015-02-15-workspace/Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv846b-done.opnd";
		//		String filePathName = "/Users/dave/Documents/Work/Research/Data/Sony/From Emmanuel/to David Meredith/SC045 - Buddha Bar - Bestof by Ravin 2013 - Alfida - Allaya Lee.mid";
		//		String filePathName = null;
		//		boolean draw = true;
		//		if (args.length == 0)
		//			filePathName = MIREX2013Entries.getFileName("Choose MIDI file", "~/");
		//		else {
		//			for(int i = 0; i < args.length; i++) {
		//				if (args[i].startsWith("--draw"))
		//					draw = true;
		//				else if (!args[i].startsWith("-"))
		//					filePathName = args[i];
		//			}
		//		}
		//		String filePathName = "/Users/dave/Documents/Work/Research/Data/Sony/From Emmanuel/AllayaLeeLog/AllayaLeeLogCropped.pts";
		//		String fileName = filePathName.substring(filePathName.lastIndexOf("/")+1);
		//		boolean withoutChannel10 = true;
		//		PointSet dataset = new PointSet(filePathName,diatonicPitch,withoutChannel10);
		//		int start = filePathName.lastIndexOf("/")+1;
		//		int end = filePathName.lastIndexOf(".");
		//		String ptsFilePathName = filePathName.substring(0,start)+filePathName.substring(start,end)+".pts";
		//		try {
		//			dataset.writeToPtsFile(ptsFilePathName);
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		if (draw)
		//			dataset.draw(fileName,diatonicPitch);
		//			Notes notes = Notes.fromOPND(opndFilePathName);
		//			notes.play(4,120);
//				System.out.println(dataset.size()+" notes");
		//		System.out.println(dataset);
		//		System.out.println("Dataset stored in file: "+ptsFilePathName);

//		try {
//			Notes notes = Notes.fromMIDI("/Users/dave/Documents/Work/Research/Data/Bach/Das wohltemperirte Clavier/Tovey ABRSM 1924/BWV 871/BWV871Prelude/score.midi", true, true);
//			PointSet points = new PointSet(notes, true);
//			points.draw("BWV871 Prelude");
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (NoMorpheticPitchException e) {
//			e.printStackTrace();
//		} catch (InvalidMidiDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
////		PointSet ps = new PointSet("/Users/dave/Documents/Work/Research/Data/Gissel/picts/png/1.png");
//		PointSet ps = new PointSet("/Users/dave/Documents/Work/Research/Data/Gissel/picts/txt/1.gv");
////		PointSet ps = new PointSet("/Users/dave/Documents/Work/Research/Data/Gissel/picts/ptsfrompng/3.pts");
//		PointSet ps1 = new PointSet("/Users/dave/Documents/Work/Research/Data/Gissel/picts/ptsfrompng/5.pts");
//		PointSet ps2 = new PointSet("/Users/dave/Documents/Work/Research/Data/Gissel/picts/ptsfrompng/20.pts");
//		PointSet pairps = new PointSet("/Users/dave/Documents/Work/Research/Data/Gissel/picts/inputPairFiles/5-20.pts");
//		ps1.draw();
//		ps2.draw();
//		pairps.draw();
		PointSet ps;
		try {
//			ps = new PointSet(
//					"/Volumes/LOTOFSPACE/Work/Research/2016-08-04-workspace/chromamorph/Points/data/PATTERNS-BOOK/MISC/noMtpPattern.pts",
//					"/Volumes/LOTOFSPACE/Work/Research/Collaborations/Kat Agres/StimuliForDave_Exp1/1NormChNorm.mid",
//					"/Volumes/LOTOFSPACE/Work/Research/Collaborations/Kat Agres/StimuliForDave_Exp1/12NormSm.mid",
//					"D:\\Dropbox\\Work\\Research\\AI Song Contest\\Geerdes\\20191206geerdes\\Arcade - Duncan Laurence\\42366-mid-2019-12-06-17-03-59-809\\42366.pts",
//					"D:\\Dropbox\\Work\\Research\\AI Song Contest\\Geerdes\\20191206geerdes\\Spirit in the Sky - Keiino\\42371.0.gm.mid",
//					"D:\\Dropbox\\Work\\Research\\AI Song Contest\\Geerdes\\20191206geerdes\\Soldi - Mahmood\\42250.0.gm.mid",
//					"/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN.OPND",
//					false,true);
//			ps.draw("No MTP point set",false,true);
//			ps.play(1000);
//			Notes notes = Notes.fromOPND("/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN.OPND");
			Notes notes = Notes.fromOPND("/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/01-Debussy-Hommage-a-Haydn/DEBUSSY-HOMMAGE-A-HAYDN.OPND");
			notes.play(4,80);
//			System.out.println(ps);
		} catch (IOException | InvalidMidiDataException | MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	public float getSegmentCompactness(PointSet dataset) {
		PointSet segment = dataset.getSegment(getMinX(), getMaxX(), true);
		return size()*1.0f/segment.size();
	}

	public int[] getColArray() {
		return colArray;
	}

	public int[] getColArray2() {
		return colArray2;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setColArray(int[] newColArray) {
		if (newColArray != null) {
			colArray = new int[4];
			for (int k = 0; k < 4; k++)
				colArray[k] = newColArray[k];
		}
	}

	public void setColArray2(int[] newColArray) {
		if (newColArray != null) {
			colArray2 = new int[4];
			for (int k = 0; k < 4; k++)
				colArray2[k] = newColArray[k];
		}
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

}
