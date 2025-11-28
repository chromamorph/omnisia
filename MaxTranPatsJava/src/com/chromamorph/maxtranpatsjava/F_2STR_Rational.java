package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

import com.chromamorph.maths.Rational;


/**
 * 
 * This transformation class is the class of 2-dimensional transformations
 * that consist of a scaling parallel to the x-axis, followed by a translation,
 * optionally followed by a reflection in the x-axis. The scale factor of the 
 * x-axis scaling must be a rational number.
 * 
 * The transformation class assumes that the x-value of each point gives the
 * mid-point of each note or sequence of tied notes.
 * 
 * Each transformation parameter, alpha, is a triple, <s, v, b>, where 
 * - s is a rational scale factor for a stretch parallel to the x-axis
 * - v is a translation vector
 * - b is either 1 or -1, with -1 indicating a reflection in the x-axis and
 *   1 indicating that no reflection takes place.
 *   
 * If alpha = <s, v, b>, then sigma = <n_s, d_s, v[0], v[1], b>
 * where s = n_s/d_s and we assume that the fraction is an
 * improper fraction in lowest terms.
 * 
 * The identity sigma is therefore <1,1,0,0,1>.
 * The modified transformation class function is
 * phi'(sig, p) = <p0sig0/sig1+sig2,sig4(p1+sig3)>
 *
 * 
 * @author David Meredith
 *
 */
public class F_2STR_Rational extends TransformationClass {

	public F_2STR_Rational() {
		super();
		setName("F_2STR_Rational");
		setSigmaLength(5);
		setBasisSize(2);
		identitySigma = Utility.makeSigma(1.0, 1.0, 0.0, 0.0, 1.0);
	}

	@Override
	Point phi(ArrayList<Double> sigma, Point p) {
		if (sigma.get(4) != -1 && sigma.get(4) != 1)
			return null;
		if (sigma.get(0) == 0.0 || sigma.get(0) == -0.0)
			return null;
		return new Point(p.get(0)*sigma.get(0)/sigma.get(1)+sigma.get(2),sigma.get(4)*(p.get(1)+sigma.get(3)));
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
		 * 0		0		0		1		1		not valid because implies sigma4 = 0 (sigma4 must be either 1 or -1)
		 * 0		0		1		0		2		not valid because implies sigma0 = 0
		 * 0		0		1		1		3		does not occur because implies p' = q' (p is different from q and p' is different from q')
		 * 0		1		0		0		4		not valid because definition of sigma4 implies that if p1 = q1 then pd1 = qd1
		 * 0		1		0		1		5	!	implies sigma4 can be either 1 or -1, so there are two cases here!!!
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
			//			Then sigma4 can be either 1 or -1 and we have two cases
			
			try {
				if (pd0 - qd0 != (pd0 - qd0)%1)
					throw new NonIntegerDifferenceBetweenPointsException(pd0, qd0);
				if (p0 - q0 != (p0 - q0)%1)
					throw new NonIntegerDifferenceBetweenPointsException(p0, q0);
			} catch (NonIntegerDifferenceBetweenPointsException e) {
				e.printStackTrace();
			}
			
			long num = (long)(pd0 - qd0);
			long den = (long)(p0 - q0);
			Rational s = new Rational(num,den);
			
			double sigma0 = s.getNumerator();//pd0 - qd0;
			double sigma1 = s.getDenominator();//p0 - q0;
			double sigma2 = pd0 - p0*sigma0/sigma1;

			double sigma4 = -1.0;
			double sigma3 = qd1*sigma4 - q1;
			sigmas.add(Utility.makeSigma(sigma0,sigma1,sigma2,sigma3,sigma4));

			sigma4 = 1.0;
			sigma3 = qd1*sigma4 - q1;
			ArrayList<Double> sigma = Utility.makeSigma(sigma0,sigma1,sigma2,sigma3,sigma4);
			ArrayList<Double> ids = getIdentitySigma();
			if (!sigma.equals(ids))
				sigmas.add(sigma);

			return sigmas;
		}

		//		Now in all other cases that occur, result is invalid
		if (p0 == q0 || p1 == q1 || pd0 == qd0 || pd1 == qd1)
			return sigmas;

		//		If sigma3 is not 1 and not -1, then result is invalid
		double sigma4 = (pd1-qd1)/(p1-q1);
		if (sigma4 != 1.0 && sigma4 != -1.0)
			return sigmas;

		try {
			if (pd0 - qd0 != (pd0 - qd0)%1)
				throw new NonIntegerDifferenceBetweenPointsException(pd0, qd0);
			if (p0 - q0 != (p0 - q0)%1)
				throw new NonIntegerDifferenceBetweenPointsException(p0, q0);
		} catch (NonIntegerDifferenceBetweenPointsException e) {
			e.printStackTrace();
		}
		
		long num = (long)(pd0 - qd0);
		long den = (long)(p0 - q0);
		Rational s = new Rational(num,den);
		
		double sigma0 = s.getNumerator();//pd0 - qd0;
		double sigma1 = s.getDenominator();//p0 - q0;
		
		double sigma2 = pd0 - p0*sigma0/sigma1;
		double sigma3 = qd1*sigma4 - q1;

		ArrayList<Double> sigma = Utility.makeSigma(sigma0,sigma2,sigma3,sigma4);
		if (!sigma.equals(getIdentitySigma()))
			sigmas.add(sigma);
		return sigmas;
	}

//	@Override
//	ArrayList<Double> getInverseSigma(ArrayList<Double> sigma) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public String getOSTGString(Transformation tran) {
		StringBuilder sb = new StringBuilder();
		sb.append("$");
		if (tran.getSigma().get(0) != 1.0)
			sb.append("S_{"+Utility.getIntString(tran.getSigma().get(0))+"}");
		sb.append("T_{\\langle"+Utility.getIntString(tran.getSigma().get(1))+","+Utility.getIntString(tran.getSigma().get(2))+"\\rangle}");
		if (tran.getSigma().get(3) == -1)
			sb.append("R_x");
		sb.append("$");
		return sb.toString();
	}

	
}
