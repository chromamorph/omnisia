package dk.meredith.mel.v014;

public class DF1 extends MEL {
	DF1() {
		Note note1 = note(1,60);
		MaskSequence maskSequence1 = maskSequence(mask(1,2),mask(0,3));
		MaskSequence maskSequence2 = maskSequence(mask(0,2,2,1,2,2,2,1),mask(0,2,2,3));
		Vector vector1 = vector(1,1,maskSequence1,maskSequence2);
		Vector vector2 = vector(-1,-1);
		add(translate(note1,product(run(3,vector1),vectorSequence(vector2))));
		
		draw();
		print();
		play(100);
	}
	
	public static void main(String[] args) {
		new DF1();
	}
}
