package dk.meredith.mel.v005;

/**
 * 
 * @author David Meredith 11 March 2012
 * 
 * Encodes Figure 1 from Deutsch and Feroe (1981).
 *
 */
public class DeutschFeroeFig1_02 extends NoteSet {

	public DeutschFeroeFig1_02() {
		PitchClassSet C_tr = pcs(0,4,7);
		PitchClassSet Cr = pcs(0,1,2,3,4,5,6,7,8,9,10,11);
	    a(a(n(0,60,4)).t(vq(v(6,1),3),C_tr));
	    a(t(v(-1,-1,-3),Cr));
	}
	
	public static void main(String[] args) {
		System.out.println(new DeutschFeroeFig1_02());
	}
	
}
