package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class F_2STR extends TransformationClass {

	public F_2STR() {
		super();
		setName("F_2STR");
		setSigmaLength(4);
		setBasisSize(2);
		identitySigma = Utility.makeSigma(1.0, 0.0, 0.0, 1.0);
	}

	@Override
	Point phi(ArrayList<Double> sigma, Point p) {
		if (sigma.get(3) != -1 && sigma.get(3) != 1)
			return null;
		if (sigma.get(0) == 0.0 || sigma.get(0) == -0.0)
			return null;
		return new Point(p.get(0)*sigma.get(0)+sigma.get(1),sigma.get(3)*(p.get(1)+sigma.get(2)));
	}

	@Override
	ArrayList<ArrayList<Double>> getSigmas(PointSequence objectBasis, PointSequence imageBasis) {
		ArrayList<ArrayList<Double>> sigmas = new ArrayList<ArrayList<Double>>();
		Point p = objectBasis.get(0), q = objectBasis.get(1), pDash = imageBasis.get(0), qDash = imageBasis.get(1);

		double 	p0 = p.get(0), p1 = p.get(1), q0 = q.get(0), q1 = q.get(1),
				pd0 = pDash.get(0), pd1 = pDash.get(1), qd0 = qDash.get(0), qd1 = qDash.get(1);


		/* 
		 * p0=q0	p1=q1	pd0=qd0	pd1=qd1	CASE	NOTE
		 * 0		0		0		0		0		
		 * 0		0		0		1		1		not valid because implies sigma3 = 0 (sigma3 must be either 1 or -1)
		 * 0		0		1		0		2		not valid because implies sigma0 = 0
		 * 0		0		1		1		3		does not occur because implies p' = q' (p is different from q and p' is different from q')
		 * 0		1		0		0		4		not valid because definition of sigma3 implies that if p1 = q1 then pd1 = qd1
		 * 0		1		0		1		5	!	implies sigma3 can be either 1 or -1, so there are two cases here!!!
		 * 0		1		1		0		6		not valid for same reason as case 4
		 * 0		1		1		1		7		does not occur because p' always different from q'
		 * 1		0		0		0		8		not valid because if p0 = q0 then pd0 = qd0 as sigma0 != 0
		 * 1		0		0		1		9		not valid for same reason as case 8
		 * 1		0		1		0		10		not valid because implies sigma0 can take any value
		 * 1		0		1		1		11		does not occur because p' always different from q'
		 * 1		1		0		0		12		does not occur because p always different from q
		 * 1		1		0		1		13		does not occur because p always different from q
		 * 1		1		1		0		14		does not occur because p always different from q
		 * 1		1		1		1		15		does not occur because p always different from q
		 * 
		 */

		//		First handle case 5 where there are two sigmas:
		if (pd1 == qd1 && p1 == q1) {
			//			=> p0 != q0 and pd0 != qd0 because p != q and p' != q' 
			//			Then sigma3 can be either 1 or -1 and we have two cases
			double sigma0 = (pd0 - qd0)/(p0 - q0);
			double sigma1 = pd0 - p0*sigma0;

			double sigma3 = -1.0;
			double sigma2 = qd1*sigma3 - q1;
			sigmas.add(Utility.makeSigma(sigma0,sigma1,sigma2,sigma3));

			sigma3 = 1.0;
			sigma2 = qd1*sigma3 - q1;
			ArrayList<Double> sigma = Utility.makeSigma(sigma0,sigma1,sigma2,sigma3);
			ArrayList<Double> ids = getIdentitySigma();
			if (!sigma.equals(ids))
				sigmas.add(sigma);

			return sigmas;
		}

		//		Now in all other cases that occur, result is invalid
		if (p0 == q0 || p1 == q1 || pd0 == qd0 || pd1 == qd1)
			return sigmas;

		//		If sigma3 is not 1 and not -1, then result is invalid
		double sigma3 = (pd1-qd1)/(p1-q1);
		if (sigma3 != 1.0 && sigma3 != -1.0)
			return sigmas;

		double sigma0 = (pd0 - qd0)/(p0 - q0);
		double sigma1 = pd0 - p0*sigma0;
		double sigma2 = qd1*sigma3 - q1;

		ArrayList<Double> sigma = Utility.makeSigma(sigma0,sigma1,sigma2,sigma3);
		if (!sigma.equals(getIdentitySigma()))
			sigmas.add(sigma);
		return sigmas;
	}

	@Override
	ArrayList<Double> getInverseSigma(ArrayList<Double> sigma) {
		if (sigma == null)
			return null;
		if (sigma.get(3) != -1 && sigma.get(3) != 1)
			return null;
		if (sigma.get(0) == 0.0 || sigma.get(0) == -0.0)
			return null;
		ArrayList<Double> inverseSigma = new ArrayList<Double>();
		inverseSigma.add(1/sigma.get(0));
		inverseSigma.add(-sigma.get(1)/sigma.get(0));
		inverseSigma.add(-sigma.get(2)*sigma.get(3));
		inverseSigma.add(sigma.get(3));
		return inverseSigma;
	}

}
