package dk.meredith.mel.v003;

public class BarberSonataBars9To10_01  extends NoteSet {

	/**
	 * Generates the notes in bars 9 - 10 of
	 * Barber's Sonata for Piano.
	 */
	public BarberSonataBars9To10_01() {
		add(note(0,63,4));
		add(note(4,73,4));
		add(note(8,68,4));
		add(note(12,66,4));
		add(note(16,76,4));
		add(note(20,71,4));
		add(note(24,69,4));
		add(note(28,79,4));
		add(note(32,74,4));
		add(note(36,72,4));
		add(note(40,82,4));
		add(note(44,77,4));
		add(note(48,75,4));
		add(note(52,85,4));
		add(note(56,80,4));
		add(note(60,78,4));
		add(note(64,88,4));
		add(note(68,83,4));
		add(note(72,81,4));
		add(note(76,91,4));
		add(note(80,86,4));
		add(note(84,84,4));
		add(note(88,94,4));
		
		add(note(0,58,21));
		add(note(21,59,3));
		add(note(24,61,9));
		add(note(33,60,3));
		add(note(36,62,9));
		add(note(45,64,3));
		add(note(48,63,21));
		add(note(69,65,3));
		add(note(72,67,9));
		add(note(81,66,3));
		add(note(84,68,9));
		add(note(93,69,3));
		
		add(note(93,70,3));
		
		add(note(0,53,96));
		add(note(0,47,96));
	}
	
	public static void main(String[] args) {
		System.out.println(new BarberSonataBars9To10_01());
	}
}
