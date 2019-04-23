package com.chromamorph.points022;

public class PitchClassSequenceEncoder implements Encoder {

	@Override
	public Encoding encode(PointSet points) {
		PitchClassSequenceEncoding pitchClassSequenceEncoding = new PitchClassSequenceEncoding(points);
		return pitchClassSequenceEncoding;
	}


}
