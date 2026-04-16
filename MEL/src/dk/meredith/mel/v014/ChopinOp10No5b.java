package dk.meredith.mel.v014;

public class ChopinOp10No5b extends MEL {
	public ChopinOp10No5b() {
		Note					n0_90 = note(0,90);
		Mask					m1 = mask(0,1),
								m2 = mask(0,2),
								m3 = mask(0,3);
		MaskStructure			ms1 = maskStructure(2,2,1,2,2,2,1),
								ms2 = maskStructure(2,2,3),
								ms3 = maskStructure(2,1),
								ms4 = maskStructure(2);
		MaskSequence			r1 = maskSequence(m1),					//tatum - triplet semiquaver
								r2a = maskSequence(m2),				//two triplet semiquavers
								r3a = maskSequence(r2a,mask(0,2,1,3)),	//background rhythm for Bar 1 RH
								r2 = maskSequence(m3),					//quaver
								r3 = maskSequence(r2,m2),				//crotchet
								r4 = maskSequence(r3,m2),				//bar
								r5 = maskSequence(r4,m2),				//2 bars
								r6 = maskSequence(r5,m2),				//4 bars
								r7 = maskSequence(r6,m2),				//8 bars
								r8 = maskSequence(r7,m2),				//16 bars
								r9 = maskSequence(r8,m3),				//48 bars
								s1 = maskSequence(mask(6,2,2,3,2,3)),
								s2 = maskSequence(mask(6,ms1)),
								s3 = maskSequence(s2,mask(0,ms2)),
								s4 = translate(s3,0,3),
								s5 = maskSequence(s3,mask(0,ms3)),
								s6 = maskSequence();
		VectorSumSet			VSS1 = product(p(r4,)
											   vectorSequence(2,p(r3a,s1)),										//background bar 1
											   vectorCollectionSequence(product(p(r2a,s3),						//first and third semiquavers of bar 1
													                            n(r1,s3)),						//second and fourth semiquavers in bar 1
													                    n(r1,s4),								//5th and 6th semiquavers of bar 1
													                    product(vectorSequence(2,p(r2a,s3)),	//semiquavers 7, 9 and 11 in bar 1
													                    		n(r1,s3))));					//semiquavers 8, 10 and 12 in bar 1
		NoteSet					B1RH = t(n0_90,B1RHStruc),
								B2RH = t(B1RH,p(r4,ss4)),
								B1 = u(B1LH,B1RH),
								B2 = u(B2LH,B2RH),
								B1To2 = u(B1,B2),
								B5To6 = t(B1To2,s(r6)),
								B1To7_half = u(B1To2,B5To6,B3,B4,B7_half),
								B49To55_half = t(B1To7_half,s(r9));	
		
		add(B1To7_half,B49To55_half);
		print();
		play(100);
		draw();
	}
}
