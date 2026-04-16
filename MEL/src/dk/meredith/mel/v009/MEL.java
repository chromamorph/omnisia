package dk.meredith.mel.v009;

public class MEL {
	private Pattern pattern = new Pattern();
	
	protected MaskStructure maskStructure(int... intervals) {
		return new MaskStructure(intervals);
	}
	
	protected Mask mask(int offset, int... maskStructure) {
		return new Mask(offset, maskStructure);
	}
	
	protected Mask mask(int offset, MaskStructure maskStructure) {
		return new Mask(offset, maskStructure);
	}

	protected MaskSequence maskSequence(Mask... masks) {
		return new MaskSequence(masks);
	}
	
	protected MaskSequence maskSequence(MaskSequence maskSequence, Mask... masks) {
		return new MaskSequence(maskSequence, masks);
	}
	
	protected Vector vector(MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence, int time, int pitch) {
		return new Vector(timeMaskSequence, pitchMaskSequence, time, pitch);
	}
	
	protected VectorSequence vectorSequence(Vector... vectors) {
		return new VectorSequence(vectors);
	}
	
	protected VectorSequence vectorSequence(int multiplicity, Vector vector) {
		return new VectorSequence(multiplicity, vector);
	}
	
	protected VectorSequence vectorSequence(MaskSequence timeMaskSequence, MaskSequence pitchMaskSequence, int... coords) {
		return new VectorSequence(timeMaskSequence, pitchMaskSequence, coords);
	}
	
	protected VectorSequence vectorSequence(VectorSequence... vectorSequences) {
		return new VectorSequence(vectorSequences);
	}
	
	protected VectorSequence inv(VectorSequence vectorSequence) {
		return vectorSequence.inv();
	}
	
	protected VectorSequence ret(VectorSequence vectorSequence){
		return vectorSequence.ret();
	}
	
	protected PatternStructure patternStructure(VectorSequence... vectorSequences) {
		return new PatternStructure(vectorSequences);
	}
	
	protected Note note(int time, int pitch) {
		return new Note(time, pitch);
	}
	
	protected Pattern pattern(Note note, PatternStructure patternStructure) {
		return new Pattern(note, patternStructure);
	}
	
	protected void add(Pattern... patterns) {
		this.pattern.add(patterns);
	}
}
