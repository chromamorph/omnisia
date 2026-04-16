package dk.meredith.mel.v025;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
//import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import processing.core.PApplet;
import processing.core.PFont;

import java.lang.reflect.Array;

public class MEL {

	class Coords implements Comparable<Coords> {
		private Integer time, pitch;

		Coords(Integer time, Integer pitch) {
			this.time = time;
			this.pitch = pitch;
		}

		@Override
		public int compareTo(Coords c) {
			int d = time - c.time;
			if (d != 0) return d;
			d = pitch - c.pitch;
			if (d != 0) return d;
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Coords)
				return compareTo((Coords)obj) == 0;
			return false;
		}

		public String toString() {
			return "["+time+","+pitch+"]";
		}
	}

	class DrawNoteSet extends PApplet {

		private static final long serialVersionUID = 1L;

		PFont font;
		float margin = 60;
		int maxPitch;
		int maxTime;
		int maxPriority;
		float maxX;
		float maxY;
		int minPitch;
		int minTime;
		float minX;
		float minY;
		float theta = 0f;
		int largestNoteSize = 7;
		int smallestNoteSize = 3;
		int lightestNoteShade = 150;

		DrawNoteSet() {
			super();
			maxPitch = constrain(getMaxPitch(),10,128);
			maxTime = max(getMaxTime(),10);
			minPitch = min(getMinPitch(),0);
			minTime = getMinTime();
			maxPriority = getMaxPriority();
		}

		public void draw() {
			background(255);
			drawAxes();
			for(NotePair v : DRAWN_VECTORS)
				drawNotePair(v);
			for(Note n : notes.getNotes()) {
				drawNote(n);
			}	
		}

		private void drawAxes() {
			stroke(0);
			fill(0);
			strokeWeight(1);
			strokeCap(SQUARE);
			line(minX,minY,minX,maxY);
			line(minX,maxY,maxX,maxY);
			textAlign(RIGHT,CENTER);
			for(float p = 0;p <= maxPitch; p += 12) {
				float y = map(p,minPitch,maxPitch,maxY,minY);
				text(((int)p),margin-10,y);
				line(margin-10,y,minX,y);
				line(minX,y,maxX,y);
			}
			textAlign(CENTER,TOP);
			for(float t = 0;t <= maxTime; t += 10) {
				float x = map(t,minTime,maxTime,minX,maxX);
				text(((int)t),x,maxY+10);
				line(x,maxY,x,maxY+10);
			}
			pushMatrix();
			translate(margin/3,drawWindowHeight/2);
			rotate(-PI/2);
			textAlign(CENTER,CENTER);
			text("Pitch",0,0);
			popMatrix();
			pushMatrix();
			translate(drawWindowWidth/2,maxY+30);
			textAlign(CENTER,CENTER);
			text("Time",0,0);
			popMatrix();

		}

		private void drawNote(Note n) {
			float col = map(n.priority, 0, maxPriority, 0, lightestNoteShade);
			stroke(col);
			fill(0);
			strokeWeight(2);
			strokeCap(ROUND);
			float x = map(time(n),minTime,maxTime,minX,maxX);
			float y = map(pitch(n),minPitch,maxPitch,maxY,minY);
			float d = map(n.priority, 0,maxPriority, largestNoteSize,smallestNoteSize);
			ellipse(x,y,d,d);
		}

		private void drawNotePair(NotePair v) {
			stroke(100);
			fill(100);
			strokeWeight(.5f);
			strokeCap(ROUND);
			float x1 = map(time(v.note1),minTime,maxTime,minX,maxX);
			float y1 = map(pitch(v.note1),minPitch,maxPitch,maxY,minY);
			float x2 = map(time(v.note2),minTime,maxTime,minX,maxX);
			float y2 = map(pitch(v.note2),minPitch,maxPitch,maxY,minY);
			line(x1,y1,x2,y2);
		}

		public void setup() {
			minX = margin;
			maxX = drawWindowWidth - margin;
			minY = margin;
			maxY = drawWindowHeight - margin;
			size(drawWindowWidth,drawWindowHeight);
			smooth();
			background(255);
			font = createFont("Arial", 14);
			textFont(font);
		}
	}

	class Mask implements Comparable<Mask>, MaskOrMaskSequence {
		private Integer offset;
		private MaskStructure structure;

		Mask(Integer offset, Integer... intervals) {
			this.offset = offset;
			structure = new MaskStructure(intervals);
		}

		Mask(Integer offset, MaskStructure structure) {
			this.offset = offset;
			this.structure = structure;
		}

		public int compareTo(Mask m) {
			int d = offset - m.offset;
			if (d != 0) return d;
			d = structure.intervals.size() - m.structure.intervals.size();
			if (d != 0) return d;
			for(int i = 0; i < structure.intervals.size(); i++) {
				d = structure.intervals.get(i) - m.structure.intervals.get(i);
				if (d != 0) return d;
			}
			return 0;
		}

		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj instanceof Mask) 
				return compareTo((Mask)obj) == 0;
			return false;
		}

		public String toString() {
			return "["+offset+","+structure+"]";
		}
	}

	interface MaskOrMaskSequence {}

	class MaskSequence implements Comparable<MaskSequence>, MaskOrMaskSequence {
		private ArrayList<Mask> masks;

		MaskSequence(Mask... masks) {
			this.masks = new ArrayList<Mask>();
			for(int i = 0; i < masks.length; i++)
				this.masks.add(masks[i]);
		}

		public int compareTo(MaskSequence m) {
			int d = masks.size() - m.masks.size();
			if (d != 0) return d;
			for(int i = 0; i < masks.size(); i++) {
				d = masks.get(i).compareTo(m.masks.get(i));
				if (d != 0) return d;
			}
			return 0;
		}

		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj instanceof MaskSequence)
				return compareTo((MaskSequence)obj) == 0;
			return false;
		}

		Mask get(Integer i) {
			return masks.get(i);
		}

		Integer length() {
			return masks.size();
		}

		public String toString() {
			return masks.toString();
		}

		public void add(Mask mask) {
			masks.add(mask);
		}
	}

	class MaskStructure implements Comparable<MaskStructure> {
		private ArrayList<Integer> intervals;

		MaskStructure(Integer... intervals) {
			this.intervals = new ArrayList<Integer>();
			for(int i = 0; i < intervals.length; i++)
				this.intervals.add(intervals[i]);
		}

		public int compareTo(MaskStructure s) {
			int d = intervals.size() - s.intervals.size();
			if (d != 0) return d;
			for(int i = 0; i < intervals.size(); i++) {
				d = intervals.get(i) - s.intervals.get(i);
				if (d != 0) return d;
			}
			return 0;
		}

		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj instanceof MaskStructure)
				return compareTo((MaskStructure)obj) == 0;
			return false;
		}

		Integer get(Integer i) {
			return intervals.get(i);
		}

		Integer length() {
			return intervals.size();
		}

		public String toString() {
			return intervals.toString();
		}
	}

	class Note implements Comparable<Note> {
		private Integer time, pitch, priority = 0;

		Note(Integer time, Integer pitch, Integer priority) {
			this.time = time;
			this.pitch = pitch;
			this.priority = priority;
			//add(this); Causes errors with vector sums - intermediate notes 
			//created while calculating resultant of vector sum.
		}

		public Note(Integer time, Integer pitch) {
			this.time = time;
			this.pitch = pitch;
		}

		public int compareTo(Note note) {
			int d = priority - note.priority;
			if (d != 0) return d;
			d = time - note.time;
			if (d != 0) return d;
			return pitch - note.pitch;
		}

		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj instanceof Note)
				return compareTo((Note)obj) == 0;
			return false;
		}

		public String toString() {
			return "["+time+","+pitch+","+priority+"]";
		}

	}

	class NotePair implements Comparable<NotePair>{
		Note note1, note2;

		NotePair(Note note1, Note note2) {
			this.note1 = note1;
			this.note2 = note2;
		}

		@Override
		public int compareTo(NotePair v) {
			int d = note1.compareTo(v.note1);
			if (d != 0) return d;
			d = note2.compareTo(v.note2);
			if (d != 0) return d;			
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NotePair)
				return compareTo(((NotePair)obj)) == 0;
			return false;
		}

	}

	class NoteSet {
		private TreeSet<Note> notes;

		NoteSet(Note... notes) {
			this.notes = new TreeSet<Note>();
			for(Note note : notes) {
				this.notes.add(note);
			}
		}

		void add(Note... notes) {
			for(Note note : notes) {
				this.notes.add(note);
			}
		}

		void add(NoteSet... noteSets) {
			for(NoteSet noteSet : noteSets)
				notes.addAll(noteSet.notes);
		}

		Note get(Integer i) {
			return new ArrayList<Note>(notes).get(i);
		}

		public Integer getMaxPitch() {
			Integer pitch = null;
			for(Note n : notes)
				if (pitch == null || n.pitch > pitch)
					pitch = n.pitch;
			return pitch;
		}

		public Integer getMaxTime() {
			Integer time = null;
			for(Note n : notes)
				if (time == null || n.time > time)
					time = n.time;
			return time;
		}

		public Integer getMinPitch() {
			Integer pitch = null;
			for(Note n : notes)
				if (pitch == null || n.pitch < pitch)
					pitch = n.pitch;
			return pitch;
		}

		public Integer getMinTime() {
			Integer time = null;
			for(Note n : notes)
				if (time == null || n.time < time)
					time = n.time;
			return time;
		}

		public Integer getMaxPriority() {
			Integer priority = null;
			for(Note n : notes)
				if (priority == null || n.priority > priority)
					priority = n.priority;
			return priority;
		}

		public TreeSet<Note> getNotes() {
			return notes;
		}

		void remove(Note note) {
			notes.remove(note);
		}

		Integer size() {
			return notes.size();
		}

		public String toString() {
			return notes.toString();
		}
	}

	class Space {
		private MaskSequence timeMaskSequence, pitchMaskSequence;

		Space(MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence) {
			this.timeMaskSequence = timeMaskSequence;
			this.pitchMaskSequence = pitchMaskSequence;
		}

		public String toString() {
			return "["+timeMaskSequence+","+pitchMaskSequence+"]";
		}
	}

	class Variable implements Comparable<Variable> {
		private String name;
		private Object value;

		Variable(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public int compareTo(Variable v) {
			return getName().compareTo(v.getName());
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Variable)) return false;
			return compareTo((Variable)obj) == 0;
		}

		String getName() {
			return name;
		}		

		Object getValue() {
			return value;
		}
	}

	class Vector implements VectorOrVectorSum, Comparable<Vector> {
		private Integer time, pitch;
		private MaskSequence timeMaskSequence = new MaskSequence(new Mask(0,1)),
				pitchMaskSequence = new MaskSequence(new Mask(0,1));

		Vector(Integer time, Integer pitch, MaskOrMaskSequence timeMaskSequence, MaskOrMaskSequence pitchMaskSequence) {

			this.time = time;
			this.pitch = pitch;
			if (timeMaskSequence instanceof MaskSequence)
				this.timeMaskSequence = (MaskSequence)timeMaskSequence;
			else if (timeMaskSequence instanceof Mask)
				this.timeMaskSequence = maskSequence((Mask)timeMaskSequence);
			if (pitchMaskSequence instanceof MaskSequence)
				this.pitchMaskSequence = (MaskSequence)pitchMaskSequence;
			else if (pitchMaskSequence instanceof Mask)
				this.pitchMaskSequence = maskSequence((Mask)pitchMaskSequence);
		}

		public Vector(int i, int j) {
		}

		public int compareTo(Vector v) {
			int d = time-v.time;
			if (d != 0) return d;
			d = pitch - v.pitch;
			if (d != 0) return d;
			d = timeMaskSequence.compareTo(v.timeMaskSequence);
			if (d != 0) return d;
			d = pitchMaskSequence.compareTo(v.pitchMaskSequence);
			if (d != 0) return d;
			return 0;
		}

		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj instanceof Vector)
				return compareTo((Vector)obj) == 0;
			return false;
		}

		public String toString() {
			return "["+time+","+pitch+","+timeMaskSequence+","+pitchMaskSequence+"]";
		}
	}


	interface VectorCollection {
		VectorSumSet getVectorSumSet();
	}

	interface VectorOrVectorSum {}

	class VectorSequence implements VectorSequenceOrVectorSumSequence, VectorCollection {
		private ArrayList<Vector> vectors;

		VectorSequence(Vector... vectors) {
			this.vectors = new ArrayList<Vector>();
			for(int i = 0; i < vectors.length; i++) 
				this.vectors.add(vectors[i]);
		}

		public VectorSumSet getVectorSumSet() {
			VectorSumSet s = new VectorSumSet();
			for(int i = 0; i < vectors.size(); i++) {
				VectorSum vs = new VectorSum();
				for(int j = 0; j <= i; j++)
					vs.add(vectors.get(j));
				s.add(vs);
			}
			return s;
		}

		public Vector get(Integer i) {
			return vectors.get(i);
		}

		public boolean isEmpty() {
			return (vectors.size() == 0);
		}

		public Integer length() {
			return vectors.size();
		}

		public String toString() {
			return vectors.toString();
		}
	}

	interface VectorSequenceOrVectorSumSequence {
		VectorOrVectorSum get(Integer i);
		boolean isEmpty();
		Integer length();
	}

	class VectorSet implements VectorCollection {
		private TreeSet<Vector> vectors;

		VectorSet(Vector... vectors) {
			this.vectors = new TreeSet<Vector>();
			for(int i = 0; i < vectors.length; i++) 
				this.vectors.add(vectors[i]);
		}

		public VectorSumSet getVectorSumSet() {
			VectorSumSet s = new VectorSumSet();
			for(Vector v : vectors) {
				s.add(new VectorSum(v));
			}
			return s;
		}

		public Vector get(Integer i) {
			return new ArrayList<Vector>(vectors).get(i);
		}

		public Integer size() {
			return vectors.size();
		}

		public String toString() {
			return vectors.toString();
		}
	}

	class VectorSum implements VectorOrVectorSum, Comparable<VectorSum> {
		private ArrayList<Vector> vectors;

		VectorSum(VectorOrVectorSum... vectorOrVectorSums) {
			this.vectors = new ArrayList<Vector>();
			for(int i = 0; i < vectorOrVectorSums.length; i++) {
				if (vectorOrVectorSums[i] instanceof Vector)
					this.vectors.add((Vector)vectorOrVectorSums[i]);
				else {
					VectorSum vectorSum = (VectorSum)vectorOrVectorSums[i];
					for(int j = 0; j < vectorSum.vectors.size(); j++)
						this.vectors.add(vectorSum.vectors.get(j));
				}
			}
		}

		public void add(Vector vector) {
			vectors.add(vector);
		}

		public int compareTo(VectorSum vectorSum) {
			int d = length() - vectorSum.length();
			if (d != 0) return d;
			for(int i = 0; i < vectorSum.length(); i++) {
				d = get(i).compareTo(vectorSum.get(i));
				if (d != 0) return d;
			}
			return 0;
		}

		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj instanceof VectorSum)
				return compareTo((VectorSum)obj) == 0;
			return false;
		}

		Vector get(Integer i) {
			return vectors.get(i);
		}

		Integer length() {
			return vectors.size();
		}

		public String toString() {
			return vectors.toString();
		}

		public void add(VectorSum vectorSum) {
			for(Vector v: vectorSum.vectors)
				this.vectors.add(v);
		}
	}

	class VectorSumSequence implements VectorSequenceOrVectorSumSequence, VectorCollection {
		private ArrayList<VectorSum> vectorSums;

		VectorSumSequence(VectorOrVectorSum... vectorOrVectorSums) {
			this.vectorSums = new ArrayList<VectorSum>();
			for(int i = 0; i < vectorOrVectorSums.length; i++) 
				this.vectorSums.add(new VectorSum(vectorOrVectorSums[i]));
		}

		public VectorSumSet getVectorSumSet() {
			VectorSumSet s = new VectorSumSet();
			for(int i = 0; i < vectorSums.size(); i++) {
				VectorSum vs = new VectorSum();
				for(int j = 0; j <= i; j++)
					vs.add(get(j));
			}
			return s;
		}

		public VectorSum get(Integer i) {
			return vectorSums.get(i);
		}

		public boolean isEmpty() {
			return (vectorSums.size() == 0);
		}

		public Integer length() {
			return vectorSums.size();
		}

		public String toString() {
			return vectorSums.toString();
		}
	}

	class VectorSumSet implements VectorCollection {
		private TreeSet<VectorSum> vectorSums;

		VectorSumSet(VectorOrVectorSum... vectorOrVectorSums) {
			this.vectorSums = new TreeSet<VectorSum>();
			for(int i = 0; i < vectorOrVectorSums.length;i++) 
				this.vectorSums.add(new VectorSum(vectorOrVectorSums[i]));
		}

		public VectorSumSet getVectorSumSet() {
			return this;
		}

		void add(VectorSum... vectorSums) {
			for(int i = 0; i < vectorSums.length; i++)
				this.vectorSums.add(vectorSums[i]);
		}

		void add(VectorSumSet... vectorSumSets) {
			for(int i = 0; i < vectorSumSets.length; i++)
				for(int j = 0; j < vectorSumSets[i].size(); j++)
					add(vectorSumSets[i].get(j));
		}

		public VectorSum get(Integer i) {
			return new ArrayList<VectorSum>(vectorSums).get(i); 
		}

		public Integer size() {
			return vectorSums.size();
		}

		public String toString() {
			return vectorSums.toString();
		}
	}

	private static final String THIS_LANGUAGE_IDENTIFIER = "MEL25";

	private static String HOME_DIR = "/Users/dave/Documents/Work/Research/workspace/MEL/data/"+THIS_LANGUAGE_IDENTIFIER.toLowerCase();

	private static void errorMessage(String fileName, int lineNumber, String message) {
		System.out.println("\nERROR in file "+fileName+" at line "+lineNumber+": "+message);
		System.exit(1);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFileChooser chooser = new JFileChooser(HOME_DIR);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("MEL files", "mel");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION)
					try {
						new MEL(chooser.getSelectedFile().getAbsolutePath());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
			}
		});
	}

	private ArrayList<Method> ALL_METHODS = new ArrayList<Method>();

	private TreeSet<NotePair> DRAWN_VECTORS = new TreeSet<NotePair>();

	int drawWindowHeight = 800;

	int drawWindowWidth = 1200;

	private NoteSet notes = new NoteSet();

	private MaskSequence noteSpacePitchMaskSequence = maskSequence(mask(0,1));

	private MaskSequence noteSpaceTimeMaskSequence = maskSequence(mask(0,1));

	private TreeSet<Variable> variables = new TreeSet<Variable>();

	private MEL(String fileName) throws IOException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		readMELFile(fileName);
	}

	protected void add(Note... notes) {
		this.notes.add(notes);
	}

	protected void add(NoteSet... noteSets) {
		for(NoteSet noteSet : noteSets)
			notes.add(noteSet);
	}

	private void addVariable(String name, Object value) {
		variables.add(new Variable(name, value));
	}

	private boolean argumentTypesMatch(Method method, Class<?>[] argumentTypes, int lineNumber) {
		Class<?>[] methodParameterTypes = method.getParameterTypes();
		if (method.isVarArgs()) {
			if (methodParameterTypes.length > argumentTypes.length+1) {
				return false;
			}
			for(int i = 0; i < methodParameterTypes.length-1; i++) {
				if (!subclassOrImplements(argumentTypes[i],methodParameterTypes[i])) {
					return false;
				}
			}
			Class<?> componentType = methodParameterTypes[methodParameterTypes.length-1].getComponentType();
			for(int i = methodParameterTypes.length-1; i < argumentTypes.length; i++)
				if (!subclassOrImplements(argumentTypes[i],componentType)) {
					return false;
				}
			return true;
		} else {
			if (methodParameterTypes.length != argumentTypes.length) {
				return false;
			}
			for(int i = 0; i < methodParameterTypes.length; i++) {
				if (!subclassOrImplements(argumentTypes[i],methodParameterTypes[i]))
					return false;
			}
			return true;
		}
	}

	protected Coords coords(Integer time, Integer pitch) {
		return new Coords(time,pitch);
	}

	private VectorSumSet copy(VectorSumSet vectorSumSet) {
		VectorSumSet vss2 = new VectorSumSet();
		for(int i = 0; i < vectorSumSet.size(); i++) {
			vss2.vectorSums.add(vectorSumSet.get(i));
		}
		return vss2;
	}

	protected void draw() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(drawWindowWidth,drawWindowHeight+23));
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PApplet embed = new DrawNoteSet();
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}


	private Integer floor(Integer a, Integer b) throws IllegalArgumentException {
		if (b.equals(0))
			throw new IllegalArgumentException("Second argument to floor cannot be zero.");
		Integer a2 = a, b2 = b;
		if (b < 0) {
			b2 = -b;
			a2 = -a;
		}
		Integer r = a2/b2; 
		/* This truncates if b2 does not divide a2, so if
		 * a2 is negative, r will be one greater than the
		 * desired result. Therefore...
		 */
		if (a%b != 0 && a2 < 0) r--;
		return r;
	}

	private Mask get(MaskSequence maskSequence, Integer i) {
		return maskSequence.get(i);
	}

	private Integer get(MaskStructure maskStructure, Integer i) {
		return maskStructure.get(i);
	}

	private Note get(NoteSet noteSet, Integer i) {
		return noteSet.get(i);
	}

	private VectorOrVectorSum get(VectorSequenceOrVectorSumSequence sequence, Integer i) {
		return sequence.get(i);
	}

	private Vector get(VectorSet vectorSet, Integer i) {
		return vectorSet.get(i);
	}

	private Vector get(VectorSum vectorSum, Integer i) {
		return vectorSum.vectors.get(i);
	}

	private VectorSum get(VectorSumSet vectorSumSet, Integer i) {
		return vectorSumSet.get(i);
	}

	private String[] getArgumentStrings(String methodCall, int lineNumber, String fileName) {
		//Assumes methodCall has been validated as a valid method call using isMethodCall
		int i = methodCall.indexOf('(');
		if (i == -1) 
			errorMessage(fileName, lineNumber,"getArgumentStrings() cannot find a starting parenthesis in \""+methodCall+"\"");
		int j = methodCall.lastIndexOf(')');
		if (j == -1) 
			errorMessage(fileName, lineNumber,"getArgumentStrings() cannot find a closing parenthesis in \""+methodCall+"\"");
		//Arguments are between i and j
		ArrayList<String> argStringList = new ArrayList<String>();
		for(int k = i+1; k < j;) {
			//Find the first comma
			int firstComma = methodCall.indexOf(',',k);
			if (firstComma == -1) {
				//No comma between here and j, then this must be the last argument string
				argStringList.add(methodCall.substring(k,j));
				k = j; //So that for loop ends
			} else {
				//A comma is found between here and j. This comma
				//could be ending the current argument string or it
				//could be the first comma in the argument string of a
				//nested method call within this argument string.
				int firstParenthesis = methodCall.indexOf('(',k);
				if (firstParenthesis == -1 || firstParenthesis > firstComma) {
					//No '(' between here (k) and end of methodCall
					//So no nested method calls in this argument list
					argStringList.add(methodCall.substring(k,firstComma));
					k = firstComma+1;
				}
				else {
					//Then the first comma is in the argument string of a nested
					//method call within this argument string.
					//This argument string is a method call.
					//Need to find closing parenthesis for this argument string's method call
					int level = 1, l = firstParenthesis+1;
					for(; l < j && level > 0; l++)
						if (methodCall.charAt(l) == '(') 
							level++;
						else if (methodCall.charAt(l) == ')')
							level--;
					if (level == 0 && methodCall.charAt(l-1) == ')') {
						//Found end of this argument string which is a method call
						argStringList.add(methodCall.substring(k,l));
						k = l+1;
					} else if (level != 0) {
						//Must be unbalanced parentheses in line
						errorMessage(fileName, lineNumber, "getArgumentsString(): Unbalanced parentheses in: "+methodCall+" i="+i+" j="+j+" k="+k+" l="+l);
					} else {
						//Level is 0, but preceding character is not a ')'
						errorMessage(fileName, lineNumber, "getArgumentsString(): Level is 0, but preceding character isn't ')' - should never happen!");
					}
				}
			}
		}
		return argStringList.toArray(new String[argStringList.size()]);
	}

	private Object getDefinedVariableValue(String name) {
		return variables.ceiling(new Variable(name,null)).getValue();
	}

	private String getFunctionCallInStatement(String statement) {
		int i = statement.indexOf('=');
		if (i == -1)
			return statement.trim();
		else
			return statement.substring(i+1).trim();
	}

	private Integer getMaxPitch() {
		return notes.getMaxPitch();
	}

	private Integer getMaxTime() {
		return notes.getMaxTime();
	}

	private Integer getMinPitch() {
		return notes.getMinPitch();
	}

	private Integer getMaxPriority() {
		return notes.getMaxPriority();
	}

	private Integer getMinTime() {
		return notes.getMinTime();
	}

	private String getSelector(String valueString, int lineNumber, String fileName) {
		int i = valueString.indexOf('(');
		if (i == - 1) 
			errorMessage(fileName, lineNumber,"getSelector cannot find a starting parenthesis in \""+valueString+"\"");
		int j = i - 1;
		for(; j >= 0 && !Character.isWhitespace(valueString.charAt(j)); j--);
		return valueString.substring(j+1,i);
	}

	private Object getValue(String valueString, int lineNumber, String fileName) {
		if (lineNumber == 1 && valueString.equals(THIS_LANGUAGE_IDENTIFIER))
			return null;
		else if (isMethodCall(valueString, fileName)) {
			try{
				String[] argumentStrings = getArgumentStrings(valueString, lineNumber, fileName);
				String selector = getSelector(valueString, lineNumber, fileName);
				Object[] argumentValues = new Object[argumentStrings.length];
				for(int i = 0; i < argumentValues.length; i++)
					argumentValues[i] = getValue(argumentStrings[i], lineNumber, fileName);
				Class<?>[] argumentTypes = new Class[argumentStrings.length];
				for(int i = 0; i < argumentStrings.length; i++)
					argumentTypes[i] = argumentValues[i].getClass();

				Method method = null;
				for(Method m : ALL_METHODS) {
					if(m.getName().equals(selector) && argumentTypesMatch(m,argumentTypes, lineNumber)) {
						method = m;
						break;
					}
				}
				if (method == null) {
					ArrayList<Class<?>> argumentTypesArrayList = new ArrayList<Class<?>>();
					for(Class<?> c : argumentTypes)
						argumentTypesArrayList.add(c);
					errorMessage(fileName, lineNumber,"No matching method found for: "+valueString+
							". Argument types for this value string are: "+argumentTypesArrayList);
				}
				if (method.isVarArgs()) {
					//Size of var-args method's parameter list 
					int nrArgs = method.getParameterTypes().length;

					//This will hold the new argument list to be passed to invoke
					Object[] newArgumentValues = new Object[nrArgs];

					//Add the initial, non var-args arguments to the new argument list
					for(int i = 0; i < nrArgs - 1; i++)
						newArgumentValues[i] = argumentValues[i];

					//Now we have to add the new final var-args argument to the new argument list
					//This will be stored in varArgsArray, which is an array of Objects
					Object[] varArgsArray = new Object[argumentValues.length - nrArgs + 1];
					for(int j = nrArgs - 1; j < argumentValues.length; j++)
						varArgsArray[j - nrArgs + 1] = argumentValues[j];

					//Now we have to typecast varArgsArray to an array with the component type required by method
					Class<?> componentClass = method.getParameterTypes()[nrArgs-1].getComponentType();

					//We need to create a new array whose component type is the correct componentClass

					Object typeCastVarArgsArray = Array.newInstance(componentClass, varArgsArray.length);

					for(int i = 0; i < varArgsArray.length; i++)
						Array.set(typeCastVarArgsArray,i,varArgsArray[i]);
					newArgumentValues[newArgumentValues.length - 1] = typeCastVarArgsArray;
					try {
						return method.invoke(this,newArgumentValues);
					} catch (IllegalArgumentException e) {
						ArrayList<Object> varArgsArgValuesArrayList = new ArrayList<Object>();
						for(Object o : newArgumentValues) varArgsArgValuesArrayList.add(o);
						ArrayList<Object> varArgsArrayList = new ArrayList<Object>();
						for(Object o : varArgsArray) varArgsArrayList.add(o);
						errorMessage(fileName, lineNumber, "varArgsArgValues:"+varArgsArgValuesArrayList+"; varArgsArray: "+varArgsArrayList+"; method: "+method+" "+e.getMessage());
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				} else {
					try {
						return method.invoke(this, argumentValues);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			} catch(StringIndexOutOfBoundsException e) {
				errorMessage(fileName, lineNumber, "getValue throws StringIndexOutOfBoundsException with valueString="+valueString);
			}
			return null;
		} else if (isDefinedVariableName(valueString)) {
			return getDefinedVariableValue(valueString);
		} else if (valueString.startsWith("\"") && valueString.endsWith("\"")) {
			return valueString.substring(1, valueString.length()-1);
		} else {//Must be an int
			return new Integer(Integer.parseInt(valueString));
		}
	}

	private String getVariableName(String assignment) {
		String[] tokens = assignment.split("=");
		return tokens[0].trim();
	}

	protected void include(String fileName) {
		readMELFile(HOME_DIR+"/"+fileName+".mel");
	}

	private boolean isAssignment(String statement) {
		return statement.contains("=");
	}

	private boolean isDefinedVariableName(String name) {
		return variables.contains(new Variable(name,null));
	}

	private boolean isEmpty(VectorSequenceOrVectorSumSequence sequence) {
		return sequence.isEmpty();
	}

	private boolean isMethodCall(String valueString, String fileName) {
		String s = valueString.trim();
		if (s.length()<3) return false;	//method call must be at least "m()"
		if (s.charAt(s.length()-1) != ')') return false; //last character must be ')'
		//First character must be a valid Java identifier first character
		if (!Character.isJavaIdentifierStart(s.charAt(0))) return false;
		//All characters from second character up to first '(' must be valid Java identifier characters
		int i = 1;
		for(; i < s.length()-1 && s.charAt(i) != '('; i++)
			if (!Character.isJavaIdentifierPart(s.charAt(i))) return false;
		if (s.charAt(i) != '(') return false;
		return true;
	}

	private Integer length(MaskSequence maskSequence) {
		return maskSequence.length();
	}

	private Integer length(MaskStructure structure) {
		return structure.length();
	}

	private Integer length(VectorSequenceOrVectorSumSequence sequence) {
		return sequence.length();
	}

	private Integer length(VectorSum vectorSum) {
		return vectorSum.vectors.size();
	}

	protected void listFunctions() {
		System.out.println("Functions for "+THIS_LANGUAGE_IDENTIFIER+"\n");
		TreeSet<String> functionSignatures = new TreeSet<String>();
		for(Method method : ALL_METHODS) {
			String returnType = method.getReturnType().getSimpleName();
			String selector = method.getName();
			Type[] parameterTypeList = method.getGenericParameterTypes();
			StringBuilder argumentTypeList = new StringBuilder();
			String thisParameterName;
			if (parameterTypeList.length > 0) {
				Class<?> c = (Class<?>)parameterTypeList[0];
				thisParameterName = c.getSimpleName();
				if (method.isVarArgs() && c.isArray() && parameterTypeList.length == 1)
					thisParameterName = c.getComponentType().getSimpleName()+"...";
				argumentTypeList.append(thisParameterName);
			}
			for(int i = 1; i < parameterTypeList.length; i++) { 
				Class<?> c = (Class<?>)parameterTypeList[i];
				thisParameterName = c.getSimpleName();
				if (method.isVarArgs() && c.isArray() && parameterTypeList.length == i+1)
					thisParameterName = c.getComponentType().getSimpleName()+"...";
				argumentTypeList.append(", "+thisParameterName);
			}
			functionSignatures.add(selector+"("+argumentTypeList.toString()+") : "+returnType);
		}
		int n = 0;
		for(String sig : functionSignatures) {
			n++;
			System.out.println(n+". "+sig);
		}
	}

	protected Mask mask(Integer offset, Integer... intervals) {
		return new Mask(offset, intervals);
	}

	protected Mask mask(Integer offset, MaskStructure structure) {
		return new Mask(offset, structure);
	}

	private Integer mask(Mask mask, Integer i) {
		if (i == null) return null;
		int j = offset(mask), k = 0;
		if (i >= offset(mask)) {
			while (j < i) {
				k++;
				j += get(structure(mask),mod(k-1,length(structure(mask))));
			}
		} else {
			while (j > i) {
				k--;
				j -= get(structure(mask),mod(k,length(structure(mask))));
			}
		}
		if (j == i) return k;
		return null;
	}

	private Integer mask(MaskSequence maskSequence, Integer i) {
		Integer k = i, j = 0;
		while (j < length(maskSequence)) {
			k = mask(get(maskSequence,j),k);
			j++;
		}
		return k;
	}

	protected MaskSequence maskSequence(Mask... masks) {
		return new MaskSequence(masks);
	}

	protected MaskStructure maskStructure(Integer... intervals) {
		return new MaskStructure(intervals);
	}

	private Integer mod(Integer a, Integer b)
			throws IllegalArgumentException {
		if (b.equals(0))
			throw new IllegalArgumentException(
					"Second argument to mod must not be zero.");
		return a - (b * floor(a,b));
	}

	protected Note note(Integer time, Integer pitch) {
		return new Note(time,pitch);
	}

	protected NoteSet noteSet(Note... notes) {
		return new NoteSet(notes);
	}

	private Integer offset(Mask mask) {
		return mask.offset;
	}

	private Integer pitch(Note note) {
		return note.pitch;
	}

	private Integer pitch(Vector vector) {
		return vector.pitch;
	}

	private MaskSequence pitchMaskSequence(Vector vector) {
		return vector.pitchMaskSequence;
	}

	protected void play(Integer msPerTatum) {
		try {
			int ticksPerQuarterNote = 500;
			int msPerTick = 1;
			Sequence sequence = new Sequence(Sequence.PPQ,ticksPerQuarterNote);
			sequence.createTrack();
			Track track = sequence.getTracks()[0];
			for(Note note : notes.getNotes()) {
				int midiNoteNumber = pitch(note);
				ShortMessage noteOnMessage = new ShortMessage();
				noteOnMessage.setMessage(ShortMessage.NOTE_ON,midiNoteNumber,96);
				ShortMessage noteOffMessage = new ShortMessage();
				noteOffMessage.setMessage(ShortMessage.NOTE_ON,midiNoteNumber,0);
				long noteOnTick = time(note) * msPerTatum / msPerTick;
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

	protected MaskSequence pop(MaskSequence maskSequence) {
		MaskSequence newMaskSequence = new MaskSequence();
		for(int i = 0; i < maskSequence.length()-1; i++)
			newMaskSequence.add(maskSequence.get(i));
		return newMaskSequence;
	}

	protected void print() {
		System.out.println(notes);
	}

	protected VectorSumSet product(Object... X) {
		System.out.println("product called with:");
		for(int i = 0; i < X.length; i++)
			System.out.print(X[i]+" ");
		System.out.println();
		VectorSumSetSequence[] Y = new VectorSumSetSequence[X.length];
		for(int i = 0; i < X.length; i++) {
			if (X[i] instanceof VectorSumSetSequence)
				Y[i] = (VectorSumSetSequence)X[i];
			else if (X[i] instanceof VectorCollection)
				Y[i] = new VectorSumSetSequence((VectorCollection)X[i]);
			else
				Y[i] = new VectorSumSetSequence(new VectorSumSet((VectorOrVectorSum)X[i]));
		}
		Vector zeroVector = vector(0,0);
		for(int i = 0; i < Y.length; i++) {
			for(int j = 0; j < Y[i].size(); j++)
				Y[i].get(j).add(vectorSum(zeroVector));
		}
		System.out.println("Y:");
		for(VectorSumSetSequence vsss : Y)
			System.out.println(vsss);
		VectorSumSet vectorSumSet1 = copy(Y[0].get(0));
		for(int i = 1; i < Y.length; i++) {
			VectorSumSet vectorSumSet2 = vectorSumSet();
			for (int k = 0; k < vectorSumSet1.size(); k++) {
				VectorSumSet vss = Y[i].get(mod(k,Y[i].size()));
				for(int j = 0; j < vss.size(); j++)
					vectorSumSet2.add(vectorSum(vectorSumSet1.get(k),vss.get(j)));
			}
			vectorSumSet1 = copy(vectorSumSet2);
		}
		return vectorSumSet1;
	}

	private void readMELFile(String fileName) {
		try {
			//Read the MEL program into a StringBuilder
			File file = new File(fileName);
			BufferedReader bufferedReader;
			bufferedReader = new BufferedReader(new FileReader(file));
			StringBuilder text = new StringBuilder(bufferedReader.readLine());
			String line;
			int commentIndex;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("//")) {
				} else if ((commentIndex = line.indexOf("//")) != -1)
					text.append(line.substring(0, commentIndex).trim());
				else
					text.append(line.trim());
			}
			bufferedReader.close();

			//Parse the MEL program text into statements
			String textString = text.toString();
			String[] statements = textString.split(";");

			//Check program uses the correct version of the language
			if (!statements[0].equals(THIS_LANGUAGE_IDENTIFIER)) {
				errorMessage(fileName, 1, "Incorrect language: \""+statements[0]+"\", should be "+THIS_LANGUAGE_IDENTIFIER+".");
			}

			//Get declared methods that are protected, non-static and do not override
			Method[] allMethods = this.getClass().getDeclaredMethods();
			for(Method method : allMethods) {
				if (Modifier.isProtected(method.getModifiers()) 
						&& !Modifier.isStatic(method.getModifiers())) {
					ALL_METHODS.add(method);
				}
			}

			//Parse program statements
			for(int i = 0; i < statements.length; i++) {
				String statement = statements[i];
				if (statement.startsWith("//")) continue;
				int lineNumber = i + 1;
				Object value = getValue(getFunctionCallInStatement(statement), lineNumber, fileName);
				if (isAssignment(statement)) {
					addVariable(getVariableName(statement),value);
				}
				System.out.println(statement+" = "+value);
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected Vector reflectPitch(Vector vector) {
		return vector(time(vector),-pitch(vector),timeMaskSequence(vector),pitchMaskSequence(vector));
	}

	protected VectorSumSet reflectPitch(VectorCollection vectorCollection) {
		VectorSumSet vectorSumSet1 = vectorSumSet(vectorCollection);
		VectorSumSet vectorSumSet2 = vectorSumSet();
		for(int i = 0; i < size(vectorSumSet1); i++)
			vectorSumSet2 = union(vectorSumSet2,vectorSumSet(reflectPitch(get(vectorSumSet1,i))));
		return vectorSumSet2;
	}

	protected VectorSum reflectPitch(VectorSum vectorSum) {
		VectorSum vectorSum2 = vectorSum();
		for(int i = 0; i < length(vectorSum); i++)
			vectorSum2 = vectorSum(vectorSum2,reflectPitch(get(vectorSum,i)));
		return vectorSum2;
	}

	protected Vector reflectTime(Vector vector) {
		return vector(-time(vector),pitch(vector),timeMaskSequence(vector),pitchMaskSequence(vector));
	}

	protected VectorSumSet reflectTime(VectorCollection vectorCollection) {
		VectorSumSet vectorSumSet1 = vectorSumSet(vectorCollection);
		VectorSumSet vectorSumSet2 = vectorSumSet();
		for(int i = 0; i < size(vectorSumSet1); i++)
			vectorSumSet2 = union(vectorSumSet2,vectorSumSet(reflectTime(get(vectorSumSet1,i))));
		return vectorSumSet2;
	}

	protected VectorSum reflectTime(VectorSum vectorSum) {
		VectorSum vectorSum2 = vectorSum();
		for(int i = 0; i < length(vectorSum); i++)
			vectorSum2 = vectorSum(vectorSum2,reflectTime(get(vectorSum,i)));
		return vectorSum2;
	}

	protected VectorSequence repeat(Integer k, Vector v) {
		VectorSequence vectorSequence = new VectorSequence();
		for(int i = 0; i < k; i++)
			vectorSequence.vectors.add(v);
		return vectorSequence;
	}

	protected VectorSumSequence repeat(Integer k, VectorOrVectorSum v) {
		VectorSumSequence vectorSumSequence = new VectorSumSequence();
		for(int i = 0; i < k; i++)
			vectorSumSequence.vectorSums.add(new VectorSum(v));
		return vectorSumSequence;
	}

	protected NoteSet setDifference(NoteSet noteSet, Note... notes) {
		for(Note note : notes)
			noteSet.remove(note);
		return noteSet;
	}

	class VectorSumSetSequence {
		private ArrayList<VectorSumSet> vectorSumSets = new ArrayList<VectorSumSet>();

		VectorSumSetSequence(VectorCollection... vectorCollections) {
			for(VectorCollection s : vectorCollections)
				vectorSumSets.add(s.getVectorSumSet());
		}

		public int size() {
			return vectorSumSets.size();
		}

		public VectorSumSet get(int i) {
			return vectorSumSets.get(i);
		}
		
		public String toString() {
			return vectorSumSets.toString();
		}
	}

	protected VectorSumSetSequence sequence(VectorCollection... vectorCollections) {
		return new VectorSumSetSequence(vectorCollections);
	}

	private VectorSumSet setify(VectorSequenceOrVectorSumSequence sequence) {
		if (isEmpty(sequence)) return vectorSumSet();
		VectorSum vectorSum = vectorSum(get(sequence,0));
		VectorSumSet vectorSumSet = vectorSumSet(vectorSum);
		for(int i = 1; i < length(sequence); i++) {
			vectorSum = vectorSum(vectorSum,get(sequence,i));
			vectorSumSet = union(vectorSumSet,vectorSumSet(vectorSum));
		}
		return vectorSumSet;
	}

	private Integer size(NoteSet noteSet) {
		return noteSet.size();
	}

	private Integer size(VectorSet vectorSet) {
		return vectorSet.size();
	}


	private Integer size(VectorSumSet vectorSumSet) {
		return vectorSumSet.vectorSums.size();
	}

	protected Space space(MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence) {
		return new Space(timeMaskSequence, pitchMaskSequence);
	}

	private MaskStructure structure(Mask mask) {
		return mask.structure;
	}

	private boolean subclassOrImplements(Class<?> class1, Class<?> class2) {
		if (class1.equals(class2))
			return true;
		Type[] interfaces = class1.getGenericInterfaces();

		ArrayList<Type> superclasses = new ArrayList<Type>();
		Type superclass = class1.getGenericSuperclass();
		while (superclass != null) {
			superclasses.add(superclass);
			superclass = ((Class<?>)superclass).getGenericSuperclass();
		}

		for (Type type : interfaces)
			if (((Type)class2).equals(type)) 
				return true;
		for (Type type : superclasses)
			if (((Type)class2).equals(type)) 
				return true;
		return false;
	}

	private Integer time(Note note) {
		return note.time;
	}

	private Integer time(Vector vector) {
		return vector.time;
	}

	private MaskSequence timeMaskSequence(Vector vector) {
		return vector.timeMaskSequence;
	}

	@Override
	public String toString() {
		return this.notes.toString();
	}

	protected Note translate(Note note, Vector vector) {
		Integer x1 = mask(timeMaskSequence(vector), time(note));
		Integer y1 = mask(pitchMaskSequence(vector), pitch(note));
		Integer x2 = x1 + time(vector);
		Integer y2 = y1 + pitch(vector);
		Integer t2 = unmask(timeMaskSequence(vector),x2);
		Integer p2 = unmask(pitchMaskSequence(vector),y2);
		Note note2;
		if (vector.time != 0 || vector.pitch != 0)
			note2 = new Note(t2,p2,note.priority+1);
		else
			note2 = new Note(t2,p2,note.priority);
		DRAWN_VECTORS.add(new NotePair(note, note2));
		return note2;
	}

	protected NoteSet translate(Note note, VectorCollection vectorCollection) {
		VectorSumSet vectorSumSet = vectorSumSet(vectorCollection);
		NoteSet noteSet = noteSet();
		for(int i = 0; i < size(vectorSumSet); i++)
			noteSet = union(noteSet,noteSet(translate(note,get(vectorSumSet,i))));
		return noteSet;
	}

	protected Note translate(Note note, VectorSum vectorSum) {
		Note note2 = note;
		for(int i = 0; i < length(vectorSum); i++) {
			note2 = translate(note2,get(vectorSum,i));
		}
		return note2;
	}

	protected NoteSet translate(NoteSet noteSet, Vector vector) {
		NoteSet noteSet2 = noteSet();
		for(int i = 0; i < size(noteSet); i++) {
			noteSet2 = union(noteSet2,noteSet(translate(get(noteSet,i),vector)));
		}
		return noteSet2;
	}

	protected NoteSet translate(NoteSet noteSet, VectorCollection vectorCollection) {
		NoteSet noteSet2 = noteSet();
		for(int i = 0; i < size(noteSet); i++) 
			noteSet2 = union(noteSet2,translate(get(noteSet,i),vectorCollection));
		return noteSet2;
	}

	protected NoteSet translate(NoteSet noteSet, VectorSum vectorSum) {
		NoteSet noteSet2 = noteSet();
		for(int i = 0; i < size(noteSet); i++) {
			noteSet2 = union(noteSet2,noteSet(translate(get(noteSet,i),vectorSum)));
		}
		return noteSet2;
	}

	protected NoteSet union(NoteSet... noteSets) {
		NoteSet noteSet = new NoteSet();
		for(int i = 0; i < noteSets.length; i++)
			for(int j = 0; j < noteSets[i].size(); j++)
				noteSet.add(noteSets[i].get(j));
		return noteSet;
	}

	protected VectorSumSet union(VectorCollection... vectorCollections) {
		VectorSumSet vectorSumSet = new VectorSumSet();
		for(VectorCollection vectorCollection : vectorCollections) {
			VectorSumSet V = vectorSumSet(vectorCollection);
			for(int i = 0; i < V.size(); i++)
				vectorSumSet.vectorSums.add(V.get(i));
		}
		return vectorSumSet;
	}

	private Integer unmask(Mask mask, Integer i) {
		if (i == null) return null;
		int j = offset(mask), k = 0;
		if (i >= 0) {
			while (k < i) {
				k++;
				j += get(structure(mask),mod(k-1,length(structure(mask))));
			}
		} else {
			while (k > i) {
				k--;
				j -= get(structure(mask),mod(k,length(structure(mask))));
			}
		}
		return j;
	}

	private Integer unmask(MaskSequence maskSequence, Integer i) {
		Integer k = i, j = length(maskSequence) - 1;
		while (j >= 0) {
			k = unmask(get(maskSequence,j), k);
			j--;
		}
		return k;
	}

	protected Vector vector(Coords coords, MaskOrMaskSequence timeMaskSequence, MaskOrMaskSequence pitchMaskSequence) {
		return new Vector(coords.time, coords.pitch,timeMaskSequence,pitchMaskSequence);
	} 

	protected Vector vector(Coords coords, Space space) {
		return new Vector(coords.time, coords.pitch, space.timeMaskSequence, space.pitchMaskSequence);
	}

	protected Vector vector(Integer time, Integer pitch) {
		return new Vector(time, pitch, noteSpaceTimeMaskSequence, noteSpacePitchMaskSequence);
	}

	protected Vector vector(Integer time, Integer pitch, MaskOrMaskSequence timeMaskSequence, MaskOrMaskSequence pitchMaskSequence) {
		return new Vector(time,pitch,timeMaskSequence,pitchMaskSequence);
	}

	protected Vector vector(Integer time, Integer pitch, Space space) {
		return new Vector(time, pitch, space.timeMaskSequence, space.pitchMaskSequence);
	}

	protected VectorSumSet vectorAdd(VectorCollection vectorCollection1, VectorCollection vectorCollection2) {
		VectorSumSet W1 = vectorSumSet(vectorCollection1);
		VectorSumSet W2 = vectorSumSet(vectorCollection2);
		VectorSumSet W = vectorSumSet();
		for(int i = 0; i < size(W1); i++)
			for(int j = 0; j < size(W2); j++)
				W = union(W,vectorSumSet(vectorSum(get(W1,i),get(W2,j))));
		return W;
	}

	protected VectorSumSet vectorAdd(VectorCollection vectorCollection, VectorOrVectorSum vectorOrVectorSum) {
		VectorSumSet W1 = vectorSumSet(vectorCollection);
		VectorSum w = vectorSum(vectorOrVectorSum);
		VectorSumSet W2 = vectorSumSet();
		for(int i = 0; i < W1.size(); i++)
			W2 = union(W2,vectorSumSet(vectorSum(get(W1,i),w)));
		return W2;
	}

	protected VectorSumSet vectorAdd(VectorOrVectorSum vectorOrVectorSum, VectorCollection vectorCollection) {
		VectorSumSet W1 = vectorSumSet(vectorCollection);
		VectorSum w = vectorSum(vectorOrVectorSum);
		VectorSumSet W2 = vectorSumSet();
		for(int i = 0; i < size(W1); i++)
			W2 = union(W2,vectorSumSet(vectorSum(w,get(W1,i))));
		return W2;
	}

	protected VectorSequence vectorSequence(Vector... vectors) {
		return new VectorSequence(vectors);
	}

	protected VectorSet vectorSet(Vector... vectors) {
		return new VectorSet(vectors);
	}

	protected VectorSum vectorSum(VectorOrVectorSum... vectorOrVectorSums) {
		return new VectorSum(vectorOrVectorSums);
	}

	protected VectorSumSet vectorSumSet(VectorCollection vectorCollection) {
		VectorSumSet vectorSumSet = vectorSumSet();
		if (vectorCollection instanceof VectorSequenceOrVectorSumSequence) {
			VectorSequenceOrVectorSumSequence sequence = (VectorSequenceOrVectorSumSequence)vectorCollection;
			vectorSumSet = setify(sequence);
		} else if (vectorCollection instanceof VectorSet) {
			VectorSet vectorSet = (VectorSet)vectorCollection;
			vectorSumSet = vectorSumSet();
			for(int i = 0; i < size(vectorSet); i++) 
				vectorSumSet = union(vectorSumSet,vectorSumSet(vectorSum(get(vectorSet,i))));
		} else
			vectorSumSet = (VectorSumSet)vectorCollection;
		return vectorSumSet;
	}

	protected VectorSumSet vectorSumSet(VectorOrVectorSum... vectorOrVectorSums) {
		return new VectorSumSet(vectorOrVectorSums);
	}

	protected VectorSumSet vectorSumSet(VectorOrVectorSum vectorOrVectorSum) {
		return new VectorSumSet(vectorOrVectorSum);
	}

}
