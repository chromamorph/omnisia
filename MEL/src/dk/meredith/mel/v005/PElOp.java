package dk.meredith.mel.v005;

public class PElOp implements ElOp {

	private int k,i;
	
	public PElOp() {
		k = 1;
		i = 1;
	}
	
	public PElOp(int k) {
		this.k = k;
		i = 1;
	}
	
	public PElOp(int k, int i) {
		this.k = k;
		this.i = i;
	}
	
	public String toString() {
		return "p("+k+","+i+")";
	}
	
}
