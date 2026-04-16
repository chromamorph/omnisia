package dk.meredith.mel.v014;

public class ChopinOp10No5c extends MEL {
	public ChopinOp10No5c() {
		MaskSequence 		metre1 = maskSequence(mask(0,1),mask(0,3),mask(0,2),
												  mask(0,2),mask(0,2),mask(0,2),
												  mask(0,2),mask(0,2),mask(0,3)),
							scale1 = maskSequence(mask(0,2,2,1,2,2,2,1),
												  mask(0,2,2,3),
												  mask(0,2,1),
												  mask(0,3));
		VectorSumSet		b1_2And5_6 = product(vector(1,0,metre1)),
							b1To7Half = union(b1_2And5_6,b3To4,b7Half);
										   
		translate (
				note(0,90),
				product(vector(1,0,metre1,translate(scale1,6)),
						b1To7Half);
	}
	}
}
