package dk.meredith.mel.v004;

public class BarberSonataBars9To10_03 extends NoteSet {

	public BarberSonataBars9To10_03() {
//		VectorSequence vs1 = vectorSequence(vector(4,10),vector(4,-5));
//		VectorSequence vs2 = vectorSequence(vector(12,3),7);
//		add(add(note(0,63,4)).translate(vs1.x(vs2)));

		a(a(n(0,63,4)).t(vq(v(4,10),v(4,-5)).x(vq(v(12,3),7))));
		
	}
	
	public static void main(String[] args) {
		System.out.println(new BarberSonataBars9To10_03());
	}
}
