package dk.meredith.mel.v004;

public class Maths {

	public static Integer floor(Integer a, Integer b) throws IllegalArgumentException {
		if (b.equals(0))
			throw new IllegalArgumentException("Second argument to floor cannot be zero.");
		Integer a2 = a, b2 = b;
		if (b < 0) {
			b2 = -b;
			a2 = -a;
		}
		Integer r = a2/b2; 
		/* This truncates if b2 does not divide a2, so if
		 * a2 is negative, r will be one greater than the
		 * desired result. Therefore...
		 */
		if (a%b != 0 && a2 < 0) r--;
		return r;
	}

	public static Integer mod(Integer a, Integer b)
			throws IllegalArgumentException {
		if (b.equals(0))
			throw new IllegalArgumentException(
					"Second argument to mod must not be zero.");
		return a - (b * floor(a,b));
	}




}
