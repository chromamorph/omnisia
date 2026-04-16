package dk.meredith.mel.v017;

public class ChopinOp10No5Bar1RHb extends MEL {
	public ChopinOp10No5Bar1RHb() {
		CoordStructure		coordStructure = product(repeat(2,p),list(p,s,run(2,p)),n);
		MaskSequence		gFlatPentatonic = maskSequence(mask(6,2,2,3,2,3)),
							gFlatMajorTriad = maskSequence(mask(6,4,4,5)),
							cFlatMajorTriad = maskSequence(mask(11,4,4,5)),
							twoOneThree = maskSequence(mask(0,2),mask(0,2,1,3)),
							semiquavers = maskSequence(mask(0,1)),
							quavers = maskSequence(mask(0,2));
		SpaceStructure		pitchSpaceStructure = product(gFlatPentatonic,
														  list(gFlatMajorTriad,
														       cFlatMajorTriad,
														       gFlatMajorTriad),
														  null), //Indicates no change from space of vector that generated parent note
							timeSpaceStructure = product(twoOneThree,
														 list(quavers,
															  semiquavers,
															  quavers),
														 semiquavers);
		NoteSet				noteSet1 = noteSet(note(0,90),coordStructure,timeSpaceStructure,pitchSpaceStructure);
		
		add(noteSet1);
		draw();
		print();
		play(100);
	}
	
	public static void main(String[] args) {
		new ChopinOp10No5Bar1RHb();
	}
}
