package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

/**
 * 
 * This transformation class is the class of 2-dimensional transformations
 * that consist of a scaling parallel to the x-axis, followed by a translation,
 * optionally followed by a reflection in the x-axis.
 * 
 * The transformation class assumes that the x-value of each point gives the
 * mid-point of each note or sequence of tied notes.
 * 
 * Each transformation parameter, alpha, is a triple, <s, v, b>, where 
 * - s is a scale factor for a stretch parallel to the x-axis
 * - v is a translation vector
 * - b is either 1 or -1, with -1 indicating a reflection in the x-axis and
 *   1 indicating that no reflection takes place.
 *   
 * If alpha = <s, v, b>, then sigma = <s, v[0], v[1], b>.
 * 
 * The identity sigma is therefore <1,0,0,1>.
 * The modified transformation class function is
 * phi'(sig, p) = <p0sig0+sig1,sig3(p1+sig2)>
 * 
 * 
 * @author David Meredith
 *
 */
public class F_2STR extends TransformationClass {

	public F_2STR() {
		super();
		setName("F_2STR");
		setSigmaLength(4);
		setBasisSize(2);
		setPerms();
		identitySigma = Utility.makeSigma(1.0, 0.0, 0.0, 1.0);
	}
	
	@Override
	Point phi(ArrayList<Double> sigma, Point p) {
		if (sigma.get(3) != -1 && sigma.get(3) != 1)
			return null;
		if (sigma.get(0) == 0.0 || sigma.get(0) == -0.0)
			return null;
		Point q = new Point(p.get(0)*sigma.get(0)+sigma.get(1),sigma.get(3)*(p.get(1)+sigma.get(2)));
		for(int i = 0; i < p.size(); i++)
			if (Utility.equalWithTolerance(Math.round(q.get(i)), q.get(i))) q.set(i, Math.round(q.get(i)));
		return q;
	}

	/**
	 * sigma0 = (pd0 - qd0)/(p0 - q0)
	 * sigma1 = (p0qd0 - pd0q0)/(p0 - q0)
	 * sigma2 = (p1qd1 - q1qd1 - pd1q1 +  q1qd1)/(pd1 - qd1)
	 * sigma3 = (pd1 - qd1)/(p1 - q1)
	 * 
	 * sigma3 must equal 1/sigma3, since sigma3 is either 1 or -1
	 */
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
		 * 0		0		1		0		2		not valid because implies sigma0 = 0 and sigma3 is 0
		 * 0		0		1		1		3		does not occur because implies p' = q' (p is different from q and p' is different from q')
		 * 0		1		0		0		4		not valid because definition of sigma3 implies that if p1 = q1 then pd1 = qd1
		 * 0		1		0		1		5	!	implies sigma3 can be either 1 or -1, so there are two cases here!!! But what about that sigma2 is dividing by zero?
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
			for(int i = 0; i < sigma.size(); i++)
				if (Utility.equalWithTolerance(0.0, sigma.get(i)%0.5))
					sigma.set(i, Utility.roundToNearestHalf(sigma.get(i)));
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
		if (Utility.equalWithTolerance(sigma3, 1.0)) 
			sigma3 = 1.0;
		else if (Utility.equalWithTolerance(sigma3, -1.0))
			sigma3 = -1.0;
		else
			return sigmas;

		double sigma0 = (pd0 - qd0)/(p0 - q0);
		double sigma1 = pd0 - p0*sigma0;
		double sigma2 = qd1*sigma3 - q1;

		if (Utility.equalWithTolerance(0.0,sigma0%0.5))
			sigma0 = Utility.roundToNearestHalf(sigma0);
		if (Utility.equalWithTolerance(0.0,sigma1%0.5))
			sigma1 = Utility.roundToNearestHalf(sigma1);
		if (Utility.equalWithTolerance(0.0,sigma2%0.5))
			sigma2 = Utility.roundToNearestHalf(sigma2);
		
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
		for(int i = 0; i < inverseSigma.size(); i++)
			if (Utility.equalWithTolerance(inverseSigma.get(i)%0.5,0.0))
				inverseSigma.set(i, Utility.roundToNearestHalf(inverseSigma.get(i)));
		return inverseSigma;
	}
	
	public static void main(String[] args) {
		System.out.println(-3.00000001 % 0.5);
		System.out.println(-4.50000001 %0.5);
		System.out.println(5.3%0.5);
	}

}
