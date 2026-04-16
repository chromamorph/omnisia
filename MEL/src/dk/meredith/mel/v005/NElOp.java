package dk.meredith.mel.v005;

public class NElOp implements ElOp {

	private int k,i;
	
	public NElOp() {
		k = 1;
		i = 1;
	}
	
	public NElOp(int k) {
		this.k = k;
		i = 1;
	}
	
	public NElOp(int k, int i) {
		this.k = k;
		this.i = i;
	}
	
	public String toString() {
		return "n("+k+","+i+")";
	}
	
}
