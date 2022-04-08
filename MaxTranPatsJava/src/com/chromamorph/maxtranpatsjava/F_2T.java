package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class F_2T extends TransformationClass {

	public F_2T() {
		super();
		setName("F_2T");
		setSigmaLength(2);
		setBasisSize(1);
		setPerms();
		identitySigma = Utility.makeSigma(0.0, 0.0);
	}

	@Override
	Point phi(ArrayList<Double> sigma, Point p) {
		return new Point(sigma.get(0)+p.get(0),sigma.get(1)+p.get(1));
	}

	@Override
	ArrayList<ArrayList<Double>> getSigmas(PointSequence objectBasis, PointSequence imageBasis) {
		Double x = imageBasis.get(0).get(0) - objectBasis.get(0).get(0);
		Double y = imageBasis.get(0).get(1) - objectBasis.get(0).get(1);
		ArrayList<Double> sigma = new ArrayList<Double>();
		sigma.add(x);
		sigma.add(y);
		ArrayList<ArrayList<Double>> sigmas = new ArrayList<ArrayList<Double>>();
		if (!sigma.equals(getIdentitySigma()))
			sigmas.add(sigma);
		return sigmas;
	}

	@Override
	ArrayList<Double> getInverseSigma(ArrayList<Double> sigma) {
		ArrayList<Double> inverseSigma = new ArrayList<Double>();
		inverseSigma.add(-sigma.get(0));
		inverseSigma.add(-sigma.get(1));
		return inverseSigma;
	}
}
