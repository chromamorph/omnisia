package dk.meredith.mel.v017;

public class ChopinOp10No5Bar1RH extends MEL {
	ChopinOp10No5Bar1RH() {
		Note 	n1 = note(0,90), 
				n2 = note(4,87), 
				n3 = note(6,85);
		Mask	m1 = mask(6,2,2,1,2,2,2,1);
		MaskStructure	s1 = maskStructure(2,2,3);
		MaskSequence 	M1 = maskSequence(mask(0,2)), 
						M2 = maskSequence(m1,mask(0,s1)), 
						M3 = maskSequence(mask(0,1)), 
						M4 = maskSequence(m1,mask(3,s1));
		Vector 	v1 = vector(1,-1,M1,M2), 
				v2 = vector(1,1,M3,M2), 
				v3 = vector(1,1,M3,M4);
		NoteSet	N1 = translate(n1,product(vectorSequence(v1),vectorSequence(v2))),
				N2 = translate(n2,product(vectorSequence(v3))),
				N3 = translate(n3,product(run(2,v1),vectorSequence(v2)));
		add(union(N1,N2,N3));
		draw();
		print();
		play(100);
	}
	
	public static void main(String[] args) {
		new ChopinOp10No5Bar1RH();
	}
}
