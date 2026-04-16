package dk.meredith.mel.v005;

import java.util.TreeSet;

public class NoteSet {
	private TreeSet<Note> notes = new TreeSet<Note>();
	private int generation = 0;

	public Note add(Note n) {
		++generation;
		n.generation = generation;
		notes.add(n); 
		return n;
	}
	
	public Note a(Note n) {
		return add(n);
	}

	public NoteSet add(NoteSet ns) {
		++generation;
		for(Note n : ns.notes) {
			n.generation = generation;
			notes.add(n);
		}
		return ns;
	}
	
	public NoteSet a(NoteSet ns) {
		return add(ns);
	}

	public NoteSet translate(Vector v) {
		NoteSet ns = new NoteSet();
		for(Note note : notes)
			ns.add(note.translate(v));
		return ns;
	}
	
	public NoteSet translate(Vector vector, PitchClassSet pitchClassSet) {
		NoteSet ns = new NoteSet();
		for(Note note: notes)
			ns.add(note.t(vector,pitchClassSet));
		return ns;
	}
	
	public NoteSet t(Vector v, PitchClassSet pcs) {
		return translate(v,pcs);
	}
	
	public NoteSet t(Vector v) {
		return translate(v);
	}
	
	public NoteSet translate(int t, int p) {
		NoteSet ns = translate(new Vector(t,p));
		return ns;
	}
	
	public NoteSet t(int t, int p) {
		return translate(t,p);
	}

	public Note note(int onset, int pitch, int duration) {
		Note n = new Note(onset, pitch, duration);
		return n;
	}
	
	public Note n(int onset, int pitch, int duration) {
		return note(onset,pitch,duration);
	}
	
	public NoteSet noteSet(Note... notes) {
		NoteSet noteSet = new NoteSet();
		for(Note n : notes)
			noteSet.add(n);
		return noteSet;
	}
	
	public NoteSet ns(Note...notes) {
		return noteSet(notes);
	}
	
	public String toString() {
		return notes.toString();
	}

	int getMaxPitch() {
		int p = notes.first().pitch;
		for(Note n : notes) {
			if (n.pitch > p)
				p = n.pitch;
		}
		return p;
	}

	int getMinPitch() {
		int p = notes.first().pitch;
		for(Note n : notes) {
			if (n.pitch < p)
				p = n.pitch;
		}
		return p;
	}

	int getMaxTime() {
		int t = notes.first().onset + notes.first().duration;
		for(Note n : notes) {
			if (n.onset + n.duration > t)
				t = n.onset + n.duration;
		}
		return t;
	}

	int getMinTime() {
		int t = notes.first().onset;
		for(Note n : notes) {
			if (n.onset < t)
				t = n.onset;
		}
		return t;
	}

	int getMaxGeneration() {
		int g = 0;
		for (Note n : notes)
			if (n.generation > g)
				g = n.generation;
		return g;
	}
	
	public TreeSet<Note> getNotes() {
		return notes;
	}

	public static int p2(int i) {
		return (int)Math.pow(2, i);
	}
		
	public Vector vector(int time, int pitch) {
		return new Vector(time,pitch);
	}
	
	public Vector v(int time, int pitch) {
		return vector(time,pitch);
	}
	
	public Vector vector(int time, int pitch, int duration) {
		return new Vector(time,pitch, duration);
	}
	
	public Vector v(int time, int pitch, int duration) {
		return vector(time,pitch, duration);
	}
	
	public VectorSequence vectorSequence(Vector...vectors){
		return new VectorSequence(vectors);
	}
	
	public VectorSequence vq(Vector...vectors){
		return vectorSequence(vectors);
	}
	
	public VectorSequence vectorSequence(Vector vector, int multiplicity) {
		return new VectorSequence(vector, multiplicity);
	}
	
	public VectorSequence vq(Vector vector, int multiplicity) {
		return vectorSequence(vector, multiplicity);
	}
	
	public VectorSet vectorSet(Vector...vectors) {
		return new VectorSet(vectors);
	}
	
	public VectorSet vs(Vector...vectors){
		return vectorSet(vectors);
	}
	
	public NoteSet delete(Note note) {
		for(Note n : notes) {
			if (n.onset == note.onset && n.pitch == note.pitch && n.duration == note.duration)
				notes.remove(n);
		}
		return this;
	}
	
	public NoteSet d(Note note) {
		return delete(note);
	}
	
	public PitchClassSet pcs(int...pcs) {
		return new PitchClassSet(pcs);
	}

	
	/***********************************
	 * IMPLEMENTING DEUTSCH AND FEROE (1981)
	 * 
	 * The following functions allow a NoteSet to
	 * implement the whole of Deutsch and Feroe's 
	 * encoding language.
	 ***********************************/
	
	/**
	 * Implements Deutsch and Feroe's (1981, p.505) concept of an alphabet.
	 * 
	 * @param pitchClasses
	 * @return Alphabet
	 */
	public Alphabet alp(int... pitchClasses) {
		return new Alphabet(pitchClasses);
	}
	
	/**
	 * Implements DF's elementary operator, s, with
	 * no multiplier. s() is s
	 * @return
	 */
	public SElOp s() {
		return new SElOp();
	}
	
	/**
	 * Implements Deutsch and Feroe's (1981, p.505)
	 * elementary operator, s. This has the general
	 * form, ks = {s, s,...} where s is repeated
	 * k times. s(k) is ks.
	 */
	public SElOp s(int k) {
		return new SElOp(k);
	}

	/**
	 * Implements DF's n elementary operator
	 * with no multiplier and no power.
	 * n() is n
	 * @return
	 */
	public NElOp n() {
		return new NElOp();
	}

	/**
	 * Implements DF's n elementary operator with
	 * a multiplier. n(k) is kn.
	 * 
	 * @param k
	 * @return
	 */
	public NElOp n(int k) {
		return new NElOp(k);
	}
	
	/**
	 * Implements DF's n elementary operator with
	 * a multiplier, k, and a power, i. So
	 * n(k,i) is kn^i.
	 * @param k
	 * @param i
	 * @return
	 */
	public NElOp n(int k, int i) {
		return new NElOp(k,i);
	}
	
	/**
	 * Implements DF's p elementary operator
	 * with no multiplier and no power.
	 * p() is p
	 * @return
	 */
	public PElOp p() {
		return new PElOp();
	}

	/**
	 * Implements DF's p elementary operator with
	 * a multiplier. p(k) is kp.
	 * 
	 * @param k
	 * @return
	 */
	public PElOp p(int k) {
		return new PElOp(k);
	}
	
	/**
	 * Implements DF's p elementary operator with
	 * a multiplier, k, and a power, i. So
	 * p(k,i) is kp^i.
	 * @param k
	 * @param i
	 * @return
	 */
	public PElOp p(int k, int i) {
		return new PElOp(k,i);
	}
	
	/**
	 * Implements DF's * reference elementary
	 * operator. So * is r()
	 * @return
	 */
	public RefElOp r() {
		return new RefElOp();
	}
	
	public Structure str()


}
