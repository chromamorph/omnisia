package dk.meredith.mel.v002;

import java.util.TreeSet;

public class NoteSet {
	private TreeSet<Note> notes = new TreeSet<Note>();
	private int generation = 0;

	public Note a(Note n) {
		++generation;
		n.generation = generation;
		notes.add(n); 
		return n;
	}

	public NoteSet a(NoteSet ns) {
		++generation;
		for(Note n : ns.notes) {
			n.generation = generation;
			notes.add(n);
		}
		return ns;
	}

	public boolean d(Note n) {
		return notes.remove(n);
	}

	public NoteSet t(Vector v) {
		NoteSet ns = new NoteSet();
		for(Note note : notes)
			ns.a(note.t(v));
		return ns;
	}
	
	public NoteSet t(int t, int p) {
		return t(new Vector(t,p));
	}

	public Note n(int onset, int pitch, int duration) {
		return new Note(onset, pitch, duration);
	}
	
	public NoteSet ns(Note... ns) {
		NoteSet noteSet = new NoteSet();
		for(Note n : ns)
			noteSet.a(n);
		return noteSet;
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

}
