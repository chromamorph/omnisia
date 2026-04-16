package dk.meredith.mel.v014;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
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

import processing.core.PApplet;
import processing.core.PFont;

public class MEL {

	class DrawNoteSet extends PApplet {

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

		DrawNoteSet() {
			super();
			maxPitch = max(128,getMaxPitch());
			maxTime = getMaxTime();
			minPitch = min(getMinPitch(),0);
			minTime = getMinTime();
		}
		
		public void draw() {
			background(255);
			drawAxes();
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
			stroke(0);
			fill(0);
			strokeWeight(2);
			strokeCap(ROUND);
			float x = map(time(n),minTime,maxTime,minX,maxX);
			float y = map(pitch(n),minPitch,maxPitch,maxY,minY);
			ellipse(x,y,2,2);
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
	class Mask implements Comparable<Mask>{
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

	class MaskSequence implements Comparable<MaskSequence>{
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
		private Integer time, pitch;
		
		Note(Integer time, Integer pitch) {
			this.time = time;
			this.pitch = pitch;
		}

		public int compareTo(Note note) {
			int d = time - note.time;
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
			return "["+time+","+pitch+"]";
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
			for(Note note : notes)
				this.notes.add(note);
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
		
		public TreeSet<Note> getNotes() {
			return notes;
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
	
	class Vector implements VectorOrVectorSum, Comparable<Vector> {
		private Integer time, pitch;
		private MaskSequence timeMaskSequence, pitchMaskSequence;
		
		Vector(Integer time, Integer pitch, MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence) {
			this.time = time;
			this.pitch = pitch;
			this.timeMaskSequence = timeMaskSequence;
			this.pitchMaskSequence = pitchMaskSequence;
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
	
	interface VectorCollection{}
	
	class VectorCollectionSequence {
		
		ArrayList<VectorCollection> vectorCollections = new ArrayList<VectorCollection>();
		
		VectorCollectionSequence(VectorCollection... vectorCollections) {
			for(VectorCollection vectorCollection :vectorCollections)
				this.vectorCollections.add(vectorCollection);
		}
		
		VectorCollection get(int i) {
			return vectorCollections.get(i);
		}
		
		int length() {
			return vectorCollections.size();
		}
		
		public String toString() {
			return vectorCollections.toString();
		}
	}
	
	interface VectorOrVectorSum extends VectorCollection {}
	
	class VectorSequence implements VectorSequenceOrVectorSumSequence, VectorCollection {
		private ArrayList<Vector> vectors;
		
		VectorSequence(Vector... vectors) {
			this.vectors = new ArrayList<Vector>();
			for(int i = 0; i < vectors.length; i++) 
				this.vectors.add(vectors[i]);
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
		
		public void add(Vector v) {
			vectors.add(v);
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
	}
	
	class VectorSumSequence implements VectorSequenceOrVectorSumSequence, VectorCollection {
		private ArrayList<VectorSum> vectorSums;
		
		VectorSumSequence(VectorOrVectorSum... vectorOrVectorSums) {
			this.vectorSums = new ArrayList<VectorSum>();
			for(int i = 0; i < vectorOrVectorSums.length; i++) 
				this.vectorSums.add(new VectorSum(vectorOrVectorSums[i]));
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

	int drawWindowHeight = 600;
	
	int drawWindowWidth = 1000;
	
	private NoteSet notes = new NoteSet();
	
	private MaskSequence noteSpacePitchMaskSequence = maskSequence(mask(0,1));
	
	private MaskSequence noteSpaceTimeMaskSequence = maskSequence(mask(0,1));
	
	private String printOffset = "";
	
	void add(Note... notes) {
		this.notes.add(notes);
	}
	
	void add(NoteSet... noteSets) {
		for(NoteSet noteSet : noteSets)
			notes.add(noteSet);
	}
	
	VectorSumSet copy(VectorSumSet vectorSumSet) {
		VectorSumSet vss2 = new VectorSumSet();
		for(int i = 0; i < vectorSumSet.size(); i++) {
			vss2.vectorSums.add(vectorSumSet.get(i));
		}
		return vss2;
	}
	
	void decreasePrintOffset() {
		printOffset = printOffset.substring(0, printOffset.length()-2);
	}
	
	void draw() {
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
	
	Integer floor(Integer a, Integer b) throws IllegalArgumentException {
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
	
	Mask get(MaskSequence maskSequence, Integer i) {
		return maskSequence.get(i);
	}

	Integer get(MaskStructure maskStructure, Integer i) {
		return maskStructure.get(i);
	}
	
	Note get(NoteSet noteSet, Integer i) {
		return noteSet.get(i);
	}

	VectorOrVectorSum get(VectorSequenceOrVectorSumSequence sequence, Integer i) {
		return sequence.get(i);
	}
	
	Vector get(VectorSet vectorSet, Integer i) {
		return vectorSet.get(i);
	}
	
	Vector get(VectorSum vectorSum, Integer i) {
		return vectorSum.vectors.get(i);
	}

	VectorSum get(VectorSumSet vectorSumSet, Integer i) {
		return vectorSumSet.get(i);
	}

	
	Integer getMaxPitch() {
		return notes.getMaxPitch();
	}
	
	Integer getMaxTime() {
		return notes.getMaxTime();
	}

	Integer getMinPitch() {
		return notes.getMinPitch();
	}
	
	Integer getMinTime() {
		return notes.getMinTime();
	}
	
	NoteSet getNotes() {
		return notes;
	}
	
	void increasePrintOffset() {
		printOffset += "  ";
	}

	boolean isEmpty(VectorSequenceOrVectorSumSequence sequence) {
		return sequence.isEmpty();
	}
	
	Integer length(MaskSequence maskSequence) {
		return maskSequence.length();
	}
	
	Integer length(MaskStructure structure) {
		return structure.length();
	}
	
	Integer length(VectorSequenceOrVectorSumSequence sequence) {
		return sequence.length();
	}
	
	Integer length(VectorSum vectorSum) {
		return vectorSum.vectors.size();
	}

	Mask mask(Integer offset, Integer... intervals) {
		return new Mask(offset, intervals);
	}
	
	Mask mask(Integer offset, MaskStructure structure) {
		return new Mask(offset, structure);
	}
	
	Integer mask(Mask mask, Integer i) {
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
	
	Integer mask(MaskSequence maskSequence, Integer i) {
		Integer k = i, j = 0;
		while (j < length(maskSequence)) {
			k = mask(get(maskSequence,j),k);
			j++;
		}
		return k;
	}
	
	MaskSequence maskSequence(Mask... masks) {
		return new MaskSequence(masks);
	}
	
	MaskSequence maskSequence(MaskSequence maskSequence, Mask... masks) {
		MaskSequence M = new MaskSequence();
		for(Mask mask : maskSequence.masks)
			M.add(mask);
		for(Mask mask : masks)
			M.add(mask);
		return M;
	}
	
	MaskStructure maskStructure(Integer... intervals) {
		return new MaskStructure(intervals);
	}
	
	Integer mod(Integer a, Integer b)
			throws IllegalArgumentException {
		if (b.equals(0))
			throw new IllegalArgumentException(
					"Second argument to mod must not be zero.");
		return a - (b * floor(a,b));
	}
	
	Vector n(MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence) {
		return vector(1,1,timeMaskSequence,pitchMaskSequence);
	}
	
	Note note (Integer time, Integer pitch) {
		return new Note(time,pitch);
	}
	
	NoteSet noteSet(Note... notes) {
		return new NoteSet(notes);
	}
	
	Integer offset(Mask mask) {
		return mask.offset;
	}
	
	Vector p(MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence) {
		return vector(1,-1,timeMaskSequence,pitchMaskSequence);
	}
	
	Integer pitch(Note note) {
		return note.pitch;
	}
	
	Integer pitch(Vector vector) {
		return vector.pitch;
	}
	
	MaskSequence pitchMaskSequence(Vector vector) {
		return vector.pitchMaskSequence;
	}
	
	void play(Integer msPerTatum) {
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
	
	void print() {
		System.out.println(notes);
	}
	
	VectorSumSet product(VectorCollection... X) {
		VectorSumSet[] Y = new VectorSumSet[X.length];
		for(int i = 0; i < X.length; i++)
			Y[i] = vectorSumSet(X[i]);
		Vector zeroVector = vector(0,0);
		for(int i = 0; i < Y.length; i++) 
			Y[i].add(vectorSum(zeroVector));
		VectorSumSet vectorSumSet1 = copy(Y[0]);
		for(int i = 1; i < Y.length; i++) {
			VectorSumSet vectorSumSet2 = vectorSumSet();
			for (int k = 0; k < vectorSumSet1.size(); k++)
				for(int j = 0; j < Y[i].size(); j++)
					vectorSumSet2.add(vectorSum(vectorSumSet1.get(k),Y[i].get(j)));
			vectorSumSet1 = copy(vectorSumSet2);
		}
		return vectorSumSet1;
	}
	
	Vector reflectPitch(Vector vector) {
		return vector(time(vector),-pitch(vector),timeMaskSequence(vector),pitchMaskSequence(vector));
	}
	
	VectorSumSet reflectPitch(VectorCollection vectorCollection) {
		VectorSumSet vectorSumSet1 = vectorSumSet(vectorCollection);
		VectorSumSet vectorSumSet2 = vectorSumSet();
		for(int i = 0; i < size(vectorSumSet1); i++)
			vectorSumSet2 = union(vectorSumSet2,vectorSumSet(reflectPitch(get(vectorSumSet1,i))));
		return vectorSumSet2;
	}
	
	VectorSum reflectPitch(VectorSum vectorSum) {
		VectorSum vectorSum2 = vectorSum();
		for(int i = 0; i < length(vectorSum); i++)
			vectorSum2 = vectorSum(vectorSum2,reflectPitch(get(vectorSum,i)));
		return vectorSum2;
	}
	
	Vector reflectTime(Vector vector) {
		return vector(-time(vector),pitch(vector),timeMaskSequence(vector),pitchMaskSequence(vector));
	}
	
	VectorSumSet reflectTime(VectorCollection vectorCollection) {
		VectorSumSet vectorSumSet1 = vectorSumSet(vectorCollection);
		VectorSumSet vectorSumSet2 = vectorSumSet();
		for(int i = 0; i < size(vectorSumSet1); i++)
			vectorSumSet2 = union(vectorSumSet2,vectorSumSet(reflectTime(get(vectorSumSet1,i))));
		return vectorSumSet2;
	}

	VectorSum reflectTime(VectorSum vectorSum) {
		VectorSum vectorSum2 = vectorSum();
		for(int i = 0; i < length(vectorSum); i++)
			vectorSum2 = vectorSum(vectorSum2,reflectTime(get(vectorSum,i)));
		return vectorSum2;
	}

	VectorSequence run(Integer k, Vector v) {
		VectorSequence vectorSequence = new VectorSequence();
		for(int i = 0; i < k; i++)
			vectorSequence.vectors.add(v);
		return vectorSequence;
	}
	
	VectorSumSequence run(Integer k, VectorOrVectorSum v) {
		VectorSumSequence vectorSumSequence = new VectorSumSequence();
		for(int i = 0; i < k; i++)
			vectorSumSequence.vectorSums.add(new VectorSum(v));
		return vectorSumSequence;
	}
	
	VectorSumSet setify(VectorSequenceOrVectorSumSequence sequence) {
		if (isEmpty(sequence)) return vectorSumSet();
		VectorSum vectorSum = vectorSum(get(sequence,0));
		VectorSumSet vectorSumSet = vectorSumSet(vectorSum);
		for(int i = 1; i < length(sequence); i++) {
			vectorSum = vectorSum(vectorSum,get(sequence,i));
			vectorSumSet = union(vectorSumSet,vectorSumSet(vectorSum));
		}
		return vectorSumSet;
	}

	Integer size(NoteSet noteSet) {
		return noteSet.size();
	}
	
	Integer size(VectorSet vectorSet) {
		return vectorSet.size();
	}
	
	Integer size(VectorSumSet vectorSumSet) {
		return vectorSumSet.vectorSums.size();
	}
	
	Space space(Vector vector) {
		return new Space(vector.timeMaskSequence,vector.pitchMaskSequence);
	}
	
	MaskStructure structure(Mask mask) {
		return mask.structure;
	}
	
	Integer time(Note note) {
		return note.time;
	}
	
	Integer time(Vector vector) {
		return vector.time;
	}
	
	MaskSequence timeMaskSequence(Vector vector) {
		return vector.timeMaskSequence;
	}
	
	public String toString() {
		return this.notes.toString();
	}

	MaskSequence translate(MaskSequence maskSequence, Integer... offsetIntervals) {
		for(int i = 0; i < maskSequence.length(); i++)
			maskSequence.get(i).offset += offsetIntervals[i];
		return maskSequence;
	}
	
	Note translate(Note note, Vector vector) {
		Integer x1 = mask(timeMaskSequence(vector), time(note));
		Integer y1 = mask(pitchMaskSequence(vector), pitch(note));
		Integer x2 = x1 + time(vector);
		Integer y2 = y1 + pitch(vector);
		Integer t2 = unmask(timeMaskSequence(vector),x2);
		Integer p2 = unmask(pitchMaskSequence(vector),y2);
		return note(t2,p2);
	}
	
	NoteSet translate(Note note, VectorCollection vectorCollection) {
		VectorSumSet vectorSumSet = vectorSumSet(vectorCollection);
		NoteSet noteSet = noteSet();
		for(int i = 0; i < size(vectorSumSet); i++)
			noteSet = union(noteSet,noteSet(translate(note,get(vectorSumSet,i))));
		return noteSet;
	}
	
	Note translate(Note note, VectorSum vectorSum) {
		Note note2 = note;
		for(int i = 0; i < length(vectorSum); i++)
			note2 = translate(note2,get(vectorSum,i));
		return note2;
	}
	
	NoteSet translate(NoteSet noteSet, VectorCollection vectorCollection) {
		NoteSet noteSet2 = noteSet();
		for(int i = 0; i < size(noteSet); i++) 
			noteSet2 = union(noteSet2,translate(get(noteSet,i),vectorCollection));
		return noteSet2;
	}
	
	VectorSumSet translate(VectorCollection vectorCollection1, VectorCollection vectorCollection2) {
		VectorSumSet W1 = vectorSumSet(vectorCollection1);
		VectorSumSet W2 = vectorSumSet(vectorCollection2);
		VectorSumSet W = vectorSumSet();
		for(int i = 0; i < size(W1); i++)
			for(int j = 0; j < size(W2); j++)
				W = union(W,vectorSumSet(vectorSum(get(W1,i),get(W2,j))));
		return W;
	}
	
	VectorSumSet translate(VectorCollection vectorCollection, VectorOrVectorSum vectorOrVectorSum) {
		VectorSumSet W1 = vectorSumSet(vectorCollection);
		VectorSum w = vectorSum(vectorOrVectorSum);
		VectorSumSet W2 = vectorSumSet();
		for(int i = 0; i < W1.size(); i++)
			W2 = union(W2,vectorSumSet(vectorSum(get(W1,i),w)));
		return W2;
	}
	
	VectorSumSet translate(VectorOrVectorSum vectorOrVectorSum, VectorCollection vectorCollection) {
		VectorSumSet W1 = vectorSumSet(vectorCollection);
		VectorSum w = vectorSum(vectorOrVectorSum);
		VectorSumSet W2 = vectorSumSet();
		for(int i = 0; i < size(W1); i++)
			W2 = union(W2,vectorSumSet(vectorSum(w,get(W1,i))));
		return W2;
	}
	
	NoteSet union(NoteSet... noteSets) {
		NoteSet noteSet = new NoteSet();
		for(int i = 0; i < noteSets.length; i++)
			for(int j = 0; j < noteSets[i].size(); j++)
				noteSet.add(noteSets[i].get(j));
		return noteSet;
	}
	
	VectorSumSet union(VectorCollection... vectorCollections) {
		VectorSumSet vectorSumSet = new VectorSumSet();
		for(VectorCollection vectorCollection : vectorCollections) {
			VectorSumSet V = vectorSumSet(vectorCollection);
			for(int i = 0; i < V.size(); i++)
				vectorSumSet.vectorSums.add(V.get(i));
		}
		return vectorSumSet;
	}
	
	Integer unmask(Mask mask, Integer i) {
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
	
	Integer unmask(MaskSequence maskSequence, Integer i) {
		Integer k = i, j = length(maskSequence) - 1;
		while (j >= 0) {
			k = unmask(get(maskSequence,j), k);
			j--;
		}
		return k;
	}
	
	Vector vector(Integer time, Integer pitch) {
		return new Vector(time, pitch, noteSpaceTimeMaskSequence, noteSpacePitchMaskSequence);
	}
	
	Vector vector(Integer time, Integer pitch, MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence) {
		return new Vector(time,pitch,timeMaskSequence,pitchMaskSequence);
	}
	
	VectorCollectionSequence vectorCollectionSequence(VectorCollection... vectorCollections) {
		return new VectorCollectionSequence(vectorCollections);
	}
	
	VectorSequence vectorSequence(Vector... vectors) {
		return new VectorSequence(vectors);
	}
	
	VectorSequence vectorSequence(int k, Vector... vectors) {
		VectorSequence vs = new VectorSequence();
		for(int i = 0; i < k; i++)
			for(Vector vector: vectors)
				vs.add(vector);
		return vs;
	}
	
	VectorSet vectorSet(Vector... vectors) {
		return new VectorSet(vectors);
	}
	
	VectorSum vectorSum(VectorOrVectorSum... vectorOrVectorSums) {
		return new VectorSum(vectorOrVectorSums);
	}
	
	VectorSumSet vectorSumSet(VectorCollection vectorCollection) {
		VectorSumSet vectorSumSet = vectorSumSet();
		if (vectorCollection instanceof Vector)
			vectorSumSet.add(vectorSum((Vector)vectorCollection));
		else if (vectorCollection instanceof VectorSum)
			vectorSumSet.add((VectorSum)vectorCollection);
		else if (vectorCollection instanceof VectorSequenceOrVectorSumSequence) {
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
	
	VectorSumSet vectorSumSet(VectorOrVectorSum... vectorOrVectorSums) {
		return new VectorSumSet(vectorOrVectorSums);
	}

}
