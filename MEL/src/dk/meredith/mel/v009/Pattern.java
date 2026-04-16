package dk.meredith.mel.v009;

import java.util.TreeSet;

public class Pattern {
	private TreeSet<Note> notes = new TreeSet<Note>();
	
	public Pattern() {}
	
	public Pattern(Note note, PatternStructure patternStructure) {
		for(VectorSum vectorSum : patternStructure.getVectorSums())
			notes.add(note.translate(vectorSum));
	}
	
	public void add(Pattern... patterns) {
		for(Pattern pattern : patterns)
			this.notes.addAll(pattern.notes);
	}
	
	public String toString() {
		return notes.toString();
	}
}
