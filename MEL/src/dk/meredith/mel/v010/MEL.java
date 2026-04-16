package dk.meredith.mel.v010;

public class MEL {
	public Note n(Integer time, Integer pitch) {
		return new Note(time, pitch);
	}
	
	public Integer t(Note note) {
		return note.getTime();
	}
	
	public Integer p(Note note) {
		return note.getPitch();
	}
	
	public NoteSet nset(Note... notes) {
		return new NoteSet(notes);
	}
	
	public Vector v(Integer time, Integer pitch, MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence) {
		return new Vector(time, pitch, timeMaskSequence, pitchMaskSequence);
	}
	
	public Integer t(Vector v) {
		return v.getTime();
	}
	
	public Integer p(Vector v) {
		return v.getPitch();
	}
	
	public MaskSequence tms(Vector v) {
		return v.getTimeMaskSequence();
	}

	public MaskSequence pms(Vector v) {
		return v.getPitchMaskSequence();
	}
	
	public VectorSequence vseq(Vector... vectors) {
		return new VectorSequence(vectors);
	}
	
	public VectorSum vsum(Vector... vectors) {
		return new VectorSum(vectors);
	}

	public VectorSum vset(Vector... vectors) {
		return new VectorSum(vectors);
	}

	
	
}
