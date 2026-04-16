package dk.meredith.mel.v017;

public class BarberOp26Bars9To10RH extends MEL {
	public BarberOp26Bars9To10RH() {
		NoteSet N1 = noteSet(note(0,63),note(1,73),note(2,68));
		VectorSet V1 = vectorSet(vector(0,0),vector(3,3),vector(6,6),vector(9,9),
								 vector(12,12),vector(15,15),vector(18,18),vector(21,21));
		NoteSet N2 = minus(translate(N1,V1),note(23,89));
		add(N2);
		print();
		draw();
		play(100);
	}
	
	public static void main(String[] args)	 {
		new BarberOp26Bars9To10RH();
	}
}
