package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * 
 * This transformation class is the class of 2-dimensional transformations
 * that consist of a scaling parallel to the x-axis, followed by a translation.
  * 
 * Each transformation parameter, alpha, is a pair, <s, v>, where 
 * - s is a scale factor for a stretch parallel to the x-axis
 * - v is a translation vector
 *   
 * If alpha = <s, v>, then sigma = <s, v[0], v[1]>.
 * 
 * The identity sigma is therefore <1,0,0>.
 * The modified transformation class function is
 * phi'(sig, p) = <p0sig0+sig1,p1+sig2)>
 * 
 * 
 * @author David Meredith
 * @date 17 November 2025
 *
 */
public class F_2ST extends TransformationClass {

	public F_2ST() {
		super();
		setName("F_2ST");
		setSigmaLength(3);
		setBasisSize(2);
		setPerms();
		identitySigma = Utility.makeSigma(1.0, 0.0, 0.0);
	}
	
	@Override
	Point phi(ArrayList<Double> sigma, Point p) {
		if (sigma.get(0) == -0.0)
			sigma.set(0, 0.0);
		Point q = new Point(p.get(0)*sigma.get(0)+sigma.get(1),p.get(1)+sigma.get(2));
		for(int i = 0; i < p.size(); i++)
			if (Utility.equalWithTolerance(Math.round(q.get(i)), q.get(i))) q.set(i, Math.round(q.get(i)));
		return q;
	}

	@Override
	ArrayList<ArrayList<Double>> getSigmas(PointSequence objectBasis, PointSequence imageBasis) {
		ArrayList<ArrayList<Double>> sigmas = new ArrayList<ArrayList<Double>>();
		if (objectBasis.equals(imageBasis)) {
			sigmas.add(identitySigma);
			return sigmas;
		}
		Point p1 = objectBasis.get(0), p2 = objectBasis.get(1), q1 = imageBasis.get(0), q2 = imageBasis.get(1);

		double 	p10 = p1.get(0), p11 = p1.get(1), p20 = p2.get(0), p21 = p2.get(1),
				q10 = q1.get(0), q11 = q1.get(1), q20 = q2.get(0), q21 = q2.get(1);

		double sigma0, sigma1, sigma2;
		
		if (!Utility.equalWithTolerance(q20,q10) && Utility.equalWithTolerance(p20, p10))
			return sigmas;
		
		if (Utility.equalWithTolerance(p20, p10) && Utility.equalWithTolerance(q20, q10))
			sigma0 = 1;
		else
			sigma0 = (q20-q10)/(p20-p10);
		sigma1 = q20 - p20 * sigma0;
		
		if (Utility.equalWithTolerance(q11-p11, q21-p21)) {
			sigma2 = q11-p11;
			sigmas.add(Utility.roundSigmaValuesToNearestHalf(Utility.makeSigma(sigma0, sigma1, sigma2)));
		}
				
		return sigmas;
	}

	public String getOSTGString(Transformation tran) {
		StringBuilder sb = new StringBuilder();
		sb.append("$");
		if (tran.getSigma().get(0) != 1.0)
			sb.append("S_{"+Utility.getIntString(tran.getSigma().get(0))+"}");
		sb.append("T_{\\langle"+Utility.getIntString(tran.getSigma().get(1))+","+Utility.getIntString(tran.getSigma().get(2))+"\\rangle}");
		sb.append("$");
		return sb.toString();
	}
	
	
	public static void main(String[] args) {
		System.out.println(-3.00000001 % 0.5);
		System.out.println(-4.50000001 %0.5);
		System.out.println(5.3%0.5);
	}
	
}
