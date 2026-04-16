package dk.meredith.mel.v005;

public class BarberSonataBars9To10_02  extends NoteSet {

	/**
	 * Generates the notes in bars 9 - 10 of
	 * Barber's Sonata for Piano.
	 */
	public BarberSonataBars9To10_02() {
		
		add(add(add(note(0,63,4)).translate(4,10)).translate(4,-5));
		for(int i = 0; i < 3; i++)
			add(translate(12*p2(i),3*p2(i)));
	}
	
	public static void main(String[] args) {
		
		System.out.println(new BarberSonataBars9To10_02());
	}
}
