package dk.meredith.mel.v017;

public class ChopinOp10No5Bar17RHb extends MEL {
	public ChopinOp10No5Bar17RHb() {
		MaskSequence metre = 
				maskSequence(mask(0,1),		//0 semiquaver
							 mask(0,2,1),  	//1 2 sq - 1sq, max even -cf octatonic
							 mask(0,2),		//2 quaver = 3 triplet semiq (me)
							 mask(0,2),		//3 crotchet (me)
							 mask(0,2));	//4 bar (me)

		MaskSequence scales = 
				maskSequence(mask(0,1),					//0 chromatic
							 mask(1,2,2,1,2,2,2,1),		//1 major scale (me) - transposed to D flat (1)
							 mask(0,2,2,3),				//2 triad (me) - tonic triad in D flat major
							 mask(0,2,1),				//3 fifth (me) - tonic fifth in D flat major
							 mask(0,2));				//4 octave (me) - tonic octave (pitch class) in D flat major

		CoordSumSet coordStructure = 
				product(p,p,list(n,s)); /* Need to define two types of coord lists: list and sequence. In list, the
										   coords are not applied in sequence - they are all applied to the same initial note.
										   In sequence, the coords are applied in sequence. This is to avoid the problem that
										   coords' natural ordering may not be the same as that of mask sequences. So we cannot
										   use just "set(n,s)".
				 */

		MaskSequenceSumSet timeMaskSequenceSumSet = 
				product(maskSequence(metre,3),		//crotchet level
						pop(parent,1),				//quaver level (3 triplet semiquavers)
						list(pop(2),		//semiquaver level for n 
							 pop(1)));		//q-sq level for s

		MaskSequenceSumSet pitchMaskSequenceSumSet = 
				product(maskSequence(scales,3),				//D flat major tonic triad fifth
						list(tran(0,4),				//A flat fifth - transpose parent fifth's parent triad up 4 steps
							 null),					//inherits Df fifth from parent node
							 list(pop(2),			//Df major scale - take the parent scale and move left 2 steps in sequence
							  	  null,				//inherit from parent
								  push(tran(1),1), 	//move reference for fifth to fifth and then shift up to octave
								  null));

		NoteSet noteSet1 = 
				noteSet(note(0,92),
						coordStructure,
						timeMaskSequenceSumSet,
						pitchMaskSequenceSumSet);

		add(noteSet1);
		print(), draw(), play(100);
	}

	public static void main(String[] args) {
		new ChopinOp10No5Bar17RH()
	}
}
