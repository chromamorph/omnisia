package dk.meredith.mel.v005;

public class ChopinOp10No1_01 extends NoteSet {

	public ChopinOp10No1_01() {
		VectorSet vs1 = vq(v(1,2)).add(v(1,1),2).x(vq(v(4,3),3));
		a(a(n(1,48,1)).t(vs1,pcs(0,4,7)));
		d(n(16,100,1));
		VectorSequence vq1 = vq(v(1,-1),2).add(v(1,-2)); 
		VectorSet vs2 = vq1.x(vq(v(4,-3),3));
		a(a(n(16,100,1)).t(vs2,pcs(0,4,7)));
		a(a(n(33,48,1)).t(vs1,pcs(0,5,9)));
		d(n(48,101,1));
		a(a(n(48,100,1)).t(vq1.x(vq(v(4,-3),2)),pcs(0,4,9)));
		a(a(n(60,62,1)).t(vq1,pcs(0,2,6,9)));
	}
}
