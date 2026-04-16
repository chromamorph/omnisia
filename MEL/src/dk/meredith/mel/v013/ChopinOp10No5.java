package dk.meredith.mel.v013;

public class ChopinOp10No5 extends MEL {
	public ChopinOp10No5() {
		Note 			n1 = note(0,90), 
						n2 = note(4,87), 
						n3 = note(6,85),
						n4 = note(24,61),
						n5 = note(36,75);
		Mask			m1 = mask(6,2,2,1,2,2,2,1);
		MaskStructure	s1 = maskStructure(2,2,3);
		MaskSequence 	M1 = maskSequence(mask(0,2)), 
						M2 = maskSequence(m1,mask(0,s1)), 
						M3 = maskSequence(mask(0,1)), 
						M4 = maskSequence(m1,mask(3,s1)),
						M5 = maskSequence(m1,mask(4,s1),mask(0,2,1)),
						M6 = maskSequence(m1,mask(1,s1),mask(2,1,2));
		Vector 			v1 = vector(1,-1,M1,M2), 
						v2 = vector(1,1,M3,M2), 
						v3 = vector(1,1,M3,M4),
						v4 = vector(12,-12),
						v5 = vector(48,0),																		//Shouldn't have to use large numbers - 2 x 2 bars
						v6 = vector(92,0),																		//Ditto: 2 x 2 x 2 bars
						v7 = vector(1,2,M3,M5),
						v8 = vector(1,-1,M3,M5),
						v9 = vector(6,7),
						v10 = vector(1,2,M3,M6),
						v11 = vector(1,-1,M3,M6),
						v12 = vector(6,5);
		VectorSequence	S1 = vectorSequence(vector(0,0),v7,v8),
						S2 = vectorSequence(vector(0,0),v10,v11);
		VectorSumSet	V1 = translate(vectorSumSet(vector(5,19)),reflectPitch(reflectTime(S1))),
						V2 = union(S1,V1),
						V3 = translate(vectorSumSet(vector(5,17)),reflectPitch(reflectTime(S2))),
						V4 = union(S2,V3);
		NoteSet			N1 = translate(n1,product(vectorSequence(v1),vectorSequence(v2))),
						N2 = translate(n2,product(vectorSequence(v3))),
						N3 = translate(n3,product(run(2,v1),vectorSequence(v2))),
						N4 = translate(union(N1,N2,N3),product(vectorSet(v4),vectorSet(v5),vectorSet(v6))),
						N5 = translate(n4,product(V2,vectorSet(v9),vectorSet(v6))),
						N6 = translate(n5,product(V4,vectorSet(v12),vectorSet(v6)));
		
		add(N4,N5,N6);
		draw();
		print();
		play(100);
	}

	public static void main(String[] args) {
		new ChopinOp10No5();
	}
}
