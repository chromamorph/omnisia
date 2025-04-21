package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import com.chromamorph.maths.*;

/**
 * 
 * This transformation class is the class of 2-dimensional transformations
 * that consist of a scaling parallel to the x-axis, followed by a translation,
 * optionally followed by a reflection in y = 0, where the y-axis is a circular 
 * axis, mod modulus. 
 * 
 * The transformation class assumes that the x-value of each point gives the
 * mid-point of each note or sequence of tied notes.
 * 
 * Each transformation parameter, alpha, is a triple, <s, v, b>, where 
 * - s is a scale factor for a stretch parallel to the x-axis
 * - v is a translation vector, where the y-component of v is added mod modulus and 
 *   where y is an integer in the interval [0,modulus-1].
 * - b is either 1 or -1, with -1 indicating a reflection in y and
 *   1 indicating that no reflection takes place.
 *   
 * If alpha = <s, v, b>, then sigma = <s, v[0], v[1], b>.
 * 
 * The identity sigma is therefore <1,0,0,1>.
 * The modified transformation class function is
 * phi'(sig, p) = <p0sig0+sig1,(sig3((p1+sig2) mod modulus)) mod modulus>
 * phi'(sig, p) = <p0sig0+sig1,(sig3(p1+sig2)) mod modulus> (from Theorem 45 on page 70-71 of http://www.titanmusic.com/papers/public/mips20010910.pdf)
 * 
 * 
 * 
 * @author David Meredith
 *
 */
public class F_2STR_Mod extends TransformationClass {

	private int modulus;
	
	public F_2STR_Mod(int modulus) {
		super();
		this.modulus = modulus;
		setName("F_2STR_Mod"+modulus);
		setSigmaLength(4);
		setBasisSize(2);
		setPerms();
		identitySigma = Utility.makeSigma(1.0, 0.0, 0.0, 1.0);
	}
	
	@Override
	Point phi(ArrayList<Double> sigma, Point p) {
		if (sigma.get(3) != -1 && sigma.get(3) != 1)
			return null;
//		if (sigma.get(0) == 0.0 || sigma.get(0) == -0.0)
//			return null;
		if (sigma.get(0) == -0.0)
			sigma.set(0, 0.0);
		Point q = new Point(p.get(0)*sigma.get(0)+sigma.get(1),(double)Math.floorMod((int)(sigma.get(3)*((long)(p.get(1)+sigma.get(2)))), modulus));
		for(int i = 0; i < p.size(); i++)
			if (Utility.equalWithTolerance(Math.round(q.get(i)), q.get(i))) q.set(i, Math.round(q.get(i)));
		return q;
	}

	/**
	 * sigma0 = (q2x - q1x)/(p2x - p1x)
	 * sigma1 = (q1x - p1x)(q2x - q1x)/(p2x - p1x)
	 * sigma2 = (sigma3*q1y - p1y) mod modulus = (sigma3*q2y - p2y) mod modulus
	 * where sigma3 is either 1 or -1
	 */
	@Override
	ArrayList<ArrayList<Double>> getSigmas(PointSequence objectBasis, PointSequence imageBasis) {
		ArrayList<ArrayList<Double>> sigmas = new ArrayList<ArrayList<Double>>();
		Point p1 = objectBasis.get(0), p2 = objectBasis.get(1), q1 = imageBasis.get(0), q2 = imageBasis.get(1);
		double 	p1x = p1.get(0), p1y = p1.get(1), p2x = p2.get(0), p2y = p2.get(1),
				q1x = q1.get(0), q1y = q1.get(1), q2x = q2.get(0), q2y = q2.get(1);

		if (Utility.equalWithTolerance(p2x, p1x)) // because denominator of expressions for sigma0 and sigma1 must not be 0
			return sigmas;
		
		double sigma0 = (q2x - q1x)/(p2x - p1x);
		double sigma1 = q1x - p1x*sigma0;
		
		for (double sigma3 : new double[] {1,-1}) {
			double sigma2 = Math.floorMod((int)(sigma3*q1y-p1y), modulus);
			double sigma2b = Math.floorMod((int)(sigma3*q2y-p2y), modulus);
			if (Utility.equalWithTolerance(sigma2, sigma2b))
				sigmas.add(Utility.makeSigma(sigma0,sigma1,sigma2,sigma3));
//			else
//				System.out.println("sigma2 not equal for both points: p1="+p1+", p2="+p2+", q1="+q1+", q2="+q2 );
		}
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
		double inverseSigma2 = Math.floorMod((int)(-sigma.get(2)*sigma.get(3)),modulus);
		inverseSigma.add(inverseSigma2);
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
