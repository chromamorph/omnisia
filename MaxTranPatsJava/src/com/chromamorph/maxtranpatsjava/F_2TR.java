package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class F_2TR extends TransformationClass {

	public F_2TR() {
		super();
		setName("F_2TR");
		setSigmaLength(3);
		setBasisSize(1);
		identitySigma = Utility.makeSigma(0.0, 0.0, 1.0);
	}
	
	@Override
	Point phi(ArrayList<Double> sigma, Point p) {
		if (sigma.get(2) != 1 && sigma.get(2) != -1) return null;
		return new Point(p.get(0)+sigma.get(0),sigma.get(2)*(p.get(1)+sigma.get(1)));
	}

	@Override
	ArrayList<ArrayList<Double>> getSigmas(PointSequence objectBasis, PointSequence imageBasis) {
		Point p = objectBasis.get(0);
		Point q = imageBasis.get(0);
		double 	p0 = p.get(0), p1 = p.get(1), 
				q0 = q.get(0), q1 = q.get(1);
		ArrayList<ArrayList<Double>> sigmas = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> sigmaA = new ArrayList<Double>();
		sigmaA.add(q0-p0);
		sigmaA.add(q1-p1);
		sigmaA.add(1.0);
		
		ArrayList<Double> sigmaB = new ArrayList<Double>();
		sigmaB.add(q0-p0);
		sigmaB.add(-q1-p1);
		sigmaB.add(-1.0);
		
		if (!sigmaA.equals(getIdentitySigma()))
			sigmas.add(sigmaA);
		sigmas.add(sigmaB);
		return sigmas;
	}

	@Override
	ArrayList<Double> getInverseSigma(ArrayList<Double> sigma) {
		if (sigma.get(2) != 1 && sigma.get(2) != -1) return null;
		ArrayList<Double> inverseSigma = new ArrayList<Double>();
		inverseSigma.add(-sigma.get(0));
		inverseSigma.add(-sigma.get(2) * sigma.get(1));
		inverseSigma.add(sigma.get(2));
		return inverseSigma;
	}

}
