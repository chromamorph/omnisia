package dk.meredith.mel.v005;

public class SElOp implements ElOp {

	private int k;
	
	public SElOp() {
		this.k = 1;
	}
	
	public SElOp(int k) {
		this.k = k;
	}
	
	public String toString() {
		return "s("+k+")";
	}
	
	public int getK() {
		return k;
	}
}
