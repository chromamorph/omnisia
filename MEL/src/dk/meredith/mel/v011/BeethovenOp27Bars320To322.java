package dk.meredith.mel.v011;

public class BeethovenOp27Bars320To322 extends MEL {
	BeethovenOp27Bars320To322() {
		Note	n1 = note(0,65),
				n2 = note(0,58);
		MaskSequence	M1 = maskSequence(mask(0,3)),
						M2 = maskSequence(mask(3,2,2,1,2,2,2,1),mask(6,2,2,3));
		Vector	v1 = vector(0,1,M1,M2),
				v2 = vector(1,-1,M1,M2);
		NoteSet	N1 = translate(n1,product(run(2,v1),run(5,v2))),
				N2 = translate(n2,product(run(17,vector(1,0))));
		add(union(N1,N2));
	}
	
	public static void main(String[] args) {
		System.out.println(new BeethovenOp27Bars320To322());
	}
}
