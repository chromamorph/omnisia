package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.TreeSet;

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
public class F_2STR_Mod_Book extends TransformationClass {
	
	private int modulus;

	public F_2STR_Mod_Book(int modulus) {
		super();
		this.modulus = modulus;
		setName("F_2STR_Mod_Book"+modulus);
		setSigmaLength(4);
		setBasisSize(2);
		setPerms();
		identitySigma = Utility.makeSigma(1.0, 0.0, 0.0, 1.0);
	}
	
	@Override
	Point phi(ArrayList<Double> sigma, Point p) {
		if (sigma.get(3) != -1 && sigma.get(3) != 1)
			return null;
		if (sigma.get(0) == -0.0)
			sigma.set(0, 0.0);
		Point q = new Point(p.get(0)*sigma.get(0)+sigma.get(1),(double)Math.floorMod((int)(sigma.get(3)*((long)(p.get(1)+sigma.get(2)))), modulus));
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

		double sigma0, sigma1, sigma2, sigma3;
		
		if (!Utility.equalWithTolerance(q20,q10) && Utility.equalWithTolerance(p20, p10))
			return sigmas;
		
		if (Utility.equalWithTolerance(p20, p10) && Utility.equalWithTolerance(q20, q10))
			sigma0 = 1;
		else
			sigma0 = (q20-q10)/(p20-p10);
		sigma1 = q20 - p20 * sigma0;
		
		if (Utility.equalWithTolerance(Math.floorMod((int)(q11-p11),modulus), Math.floorMod((int)(q21-p21),modulus))) {
			sigma3 = 1;
			sigma2 = Math.floorMod((int)(q11-p11),modulus);
			sigmas.add(Utility.roundSigmaValuesToNearestHalf(Utility.makeSigma(sigma0, sigma1, sigma2, sigma3)));
		}
		
		if (Utility.equalWithTolerance(Math.floorMod((int)(p11+q11),modulus), Math.floorMod((int)(p21+q21),modulus))) {
			sigma3 = -1;
			sigma2 = Math.floorMod((int)(-p11-q11),modulus);
			sigmas.add(Utility.roundSigmaValuesToNearestHalf(Utility.makeSigma(sigma0, sigma1, sigma2, sigma3)));
		}
		
		return sigmas;
	}

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
