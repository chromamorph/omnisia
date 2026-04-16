package dk.meredith.mel.v008;

public class ChopinOp10No1Bar1RH extends MEL {
	
	public ChopinOp10No1Bar1RH() {
//		Note          	p1				= note(0,90);
//		MaskSequence  	semiquaver   	= maskSequence(mask(0,1));
//		MaskSequence  	quaver			= maskSequence(mask(0,2));
//		MaskStructure 	majorScale		= maskStructure(2,2,1,2,2,2,1);
//		MaskStructure 	triad			= maskStructure(2,2,3);
//		MaskSequence  	gFlatMajorTriad	= maskSequence(mask(6,majorScale),mask(0,triad));
//		Vector 		  	v1				= vector(1,1,semiquaver,gFlatMajorTriad);
//		Vector 		  	v2				= vector(1,-1,quaver,gFlatMajorTriad);
//		NoteSet 	  	group1			= cross(p1,v2,v1);
//		
//		Note			p2				= note(4,87);
//		NoteSet			group2			= cross(p2,v1);
		
//		add(group1);
		
		MaskSequence		tripletSemis		= maskSequence(mask(0,1));
		MaskSequence		dupleQuaver			= maskSequence(tripletSemis, mask(0,2));
		MaskSequence		quaver				= maskSequence(tripletSemis, mask(0,3));
		MaskSequence		crotchet			= maskSequence(tripletSemis, quaver, mask(0,2));
		MaskSequence		bar					= maskSequence(tripletSemis, mask(0,12));
		MaskSequence		fourBars			= maskSequence(tripletSemis, mask(0,48));
		MaskSequence		rhythm1				= maskSequence(tripletSemis, dupleQuaver, mask(0,2,1,3));
		
		MaskStructure		octave				= maskStructure(12);
		MaskStructure		pentatonic			= maskStructure(2,2,3,2,3);
		MaskStructure		major				= maskStructure(2,2,1,2,2,2,1);
		MaskStructure		diatonicTriad		= maskStructure(2,2,3);
		MaskStructure		diatonicFifth		= maskStructure(2,1);
		
		MaskSequence		chromatic			= maskSequence(mask(0,1));
		MaskSequence		gfOctave			= maskSequence(chromatic, mask(6, octave));
		MaskSequence		gfMajorScale		= maskSequence(chromatic, mask(6, major));
		MaskSequence		tonicTriad			= maskSequence(gfMajorScale, mask(0, diatonicTriad));
		MaskSequence		subdomTriad			= maskSequence(gfMajorScale, mask(3, diatonicTriad));
		MaskSequence		gfPent				= maskSequence(chromatic, mask(6, pentatonic));
		MaskSequence		gfMaVFifth			= maskSequence(gfMajorScale, mask(4, diatonicFifth));
		MaskSequence		gfMaIIFifth			= maskSequence(gfMajorScale, mask(1, diatonicFifth));
		MaskSequence		gfMaITr				= maskSequence(gfMajorScale,tonicTriad);
		MaskSequence		gfMaIVTr			= maskSequence(gfMajorScale,subdomTriad);
		
		VectorSequence		vs0					= vectorSequence(vector(bar,gfOctave,1,-1));
		VectorSequence		vs1					= vectorSequence(2,vector(rhythm1,gfPent,1,-1));
		Vector				v1					= vector(dupleQuaver,gfMaITr,1,-1);
		VectorSequence		vs2a				= vectorSequence(v1);
		VectorSequence		vs2b				= vectorSequence(vector(dupleQuaver,gfMaIVTr,0,0));
		VectorSequence		vs2c				= vectorSequence(2, v1);
		VectorSequence		vs2					= vectorSequence(vs2a,vs2b,vs2c);
		VectorSequence		vs3					= vectorSequence(vector(tripletSemis,null,1,1));
		VectorSequence		vs4					= vectorSequence(vector(fourBars,gfOctave,0,1));
		VectorSequence		vs5					= vectorSequence(vector(crotchet,gfMaVFifth,1,1));
		VectorSequence		vs6					= vectorSequence(vector(quaver,null,1,2));
		VectorSequence		vs7					= vectorSequence(vector(tripletSemis,gfMaVFifth, 1,2, 1,-1));
		VectorSequence		vs8					= vectorSequence(vs7,ret(inv(vs7)));
		
		PatternStructure 	patternStructure1	= prime(vs4,vs0,vs1,vs2,vs3);
		PatternStructure	patternStructure2	= prime(vs5,vs6,vs8);
		
		Note				note1				= note(0,90);
		Pattern				pattern1			= pattern(note1,patternStructure1);
		
		Note				note2				= note(24,61);
		Pattern				pattern2			= pattern(note2,patternStructure2);
		
		add(pattern1,pattern2);
	}
}
