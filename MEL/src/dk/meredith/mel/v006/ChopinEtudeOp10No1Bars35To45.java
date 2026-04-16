package dk.meredith.mel.v006;

public class ChopinEtudeOp10No1Bars35To45 extends PointSet {

	public ChopinEtudeOp10No1Bars35To45() {
		/*
		 * In this example, we set constraints and any solution
		 * that satisfies the constraints is OK.
		 * 
		 * In particular, whether the bass note is doubled and 
		 * whether it goes up or down seems to be not perfectly
		 * simple, and probably doesn't really matter. What
		 * really matters is what pitch classes are in the bass
		 */
		
		/*
		 * Bass pitch class sequence
		 * A D G C F B E A D G C F B E
		 * This is a C major diatonic set, starting
		 * on A and then rising by a fourth 13 times.
		 * 
		 * We really have a pitch class cycle:
		 * 
		 *                0
		 *           4         3
		 *           
		 *          1           6
		 *          
		 *             5     2
		 *             
		 *  This is a minimal simple pitch class cycle for
		 *  the interval sets {3}_7 and {5,6}_12.

		 *                0
		 *           7         5
		 *           
		 *          2           11
		 *          
		 *             9     4

		 *  It is the unique maximally even cycle of size 7 
		 *  over 3 octaves.
		 *  
		 *  So we have a mapping:
		 *  
		 *  pc in chromatic:	0  1  2  3  4  5  6  7  8  9 10 11
		 *  pc in diatonic:     0     1     2  3     4     5     6 
		 *  
		 *  Could generate the diatonic from the chromatic with
		 *  7 steps of 7 or 7 steps of 5 or by specifying the
		 *  maximally even set of size 7.  
		 */
		
		
		/*
		 * Start with pc 5, repeatedly rise by 7 semitones until
		 * 7 pitch classes have been generated
		 */
		PitchClassSet pcs = pcs(pc(5),pci(7),7); //Generates the C maj diatonic pc set
		
		/*
		 * To specify a single interval cycle, all we need is
		 * the starting pitch class (numbered 0 to |cDiat| - 1)
		 * and the interval, expressed as an interval in the set.
		 */
		
		PitchClassCycle pcc = pcc(pcs,pc(5,7),pci(3,7)); //pitch class set, starting pitch class, interval
		
		/*
		 * Now we want a pitch class sequence in the bass stream.
		 */
		
		PitchClassSequence pcq = pcq(pcc, 4, 14);
		
		/*
		 * Needs to be in a stream so that all durations are just
		 * within-stream inter-onset intervals.
		 */
		
		MusicStream bass = new MusicStream();
		bass.add(pcq);
		
		/* 
		 * Maybe indicate onsets using stacked metres
		 * metre(2,2,2,2)
		 * pop and push metric levels?
		 * 
		 * Have to generate the following sequence of onsets:
		 * 
		 * pc  9  2  7  0  5  11  4   9   2   7   0   5   11   11   11   11   4
		 * on  0  32 48 64 80 96  112 120 128 136 144 152 160  172  176  188  192
		 * ioi 32 16 16 16 16 16  8   8   8   8   8   8   12   4    12   4
		 */
		
		
		
	}
	
}
