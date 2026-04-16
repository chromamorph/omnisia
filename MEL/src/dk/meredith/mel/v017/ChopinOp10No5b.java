package dk.meredith.mel.v017;

import java.util.ArrayList;

public class ChopinOp10No5b extends MEL {

	public ChopinOp10No5b() {
		/*
		 * We suppose the piece is divided into streams.
		 * 
		 * Each stream has a scale structure segmentation and a rhythm structure
		 * segmentation.
		 * 
		 * The scale structure segmentation is a sequence of scale structure
		 * segments. The scale structure segmentation also has an offset time,
		 * used to define the end of the final segment.
		 * 
		 * Each scale structure segment has an onset time in
		 * tatums. It also has an associated scale structure. Its offset time is
		 * equal to the onset time of the next scale structure segment.
		 * 
		 * The scale structure is represented by a mask sequence. It represents
		 * the set of possible scales over the duration of the segment.
		 * Typically, for tonal music, the mask sequence will be something like
		 * 
		 * chromatic scale 
		 * major scale 
		 * triad 
		 * fifth 
		 * octave
		 * 
		 * The rhythmic structure (metre) is also represented using a mask sequence
		 * typically with each mask representing a separate metric level.
		 */

		MaskSequenceList scales = maskSequenceList();
		MaskSequenceList rhythms = maskSequenceList();
		MaskSequence rhythm1 = maskSequence(
				mask(0,1),
				mask(0,2),
				mask(0,3),
				mask(0,2));

		MaskSequenceSegmentation t0To12Scales = maskSequenceSegmentation(12,			//Offset in tatums of segmentation
				maskSequenceSegment(0, add(scales, 0, maskSequence(						//G flat major tonal hierarchy
						mask(0,1), 
						mask(6,2,2,1,2,2,2,1),
						mask(0,2,2,3),
						mask(0,2,1),
						mask(0,2)))),
				maskSequenceSegment(3, add(scales, 1, tran(get(scales, 0), 2, 3))),		//C flat major triad as subdominant in G flat major
				maskSequenceSegment(6, get(scales, 0))
				);

		MaskSequenceSegmentation t0To24RHScales = maskSequenceSegmentation(24,
				t0To12Scales,
				tran(t0To12Scales,12)												//Second argument is new starting time for subsegmentation
				);

		MaskSequenceSegmentation t0To72RHScales = maskSequenceSegmentation(84,
				t0To24RHScales,
				maskSequenceSegment(24,add(scales, 2, tran(get(scales,0),2,4))),		//D flat major triad as dominant in G flat major
				maskSequenceSegment(30,add(scales, 3, tran(get(scales,0),2,1))),		//A flat minor triad as supertonic in G flat major
				maskSequenceSegment(42,get(scales, 2)),
				tran(t0To24RHScales,48),
				maskSequenceSegment(72,get(scales,3))
				);
		
		MaskSequenceSegmentation scaleSegmentation = maskSequenceSegmentation(
				t0To72RHScales,
				maskSequenceSegment(84,add(scales,4, tran(get(scales,0),1,4)))			//B flat major for 1 bar
				tran(t0To72RHScales,96),
				maskSequenceSegment(184)
				);
	}
}
