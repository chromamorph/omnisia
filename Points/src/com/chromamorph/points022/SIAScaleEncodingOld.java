package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.TreeSet;

import com.chromamorph.maths.Rational;

public class SIAScaleEncodingOld extends Encoding {

	@Override
	public void draw() {
	}

	@Override
	public ArrayList<TEC> getTECs() {
		return null;
	}

	class VectorPatternPair implements Comparable<VectorPatternPair> {
		private Vector vector;
		private PointSet pattern;

		VectorPatternPair(Vector vector, PointSet pattern) {
			this.vector = vector;
			this.pattern = pattern;
		}

		Vector getVector() {
			return vector;
		}

		PointSet getPattern() {
			return pattern;
		}

		void addPoint(Point point) {
			pattern.add(point);
		}

		@Override
		public int compareTo(VectorPatternPair vpp) {
			if (vpp == null) return 1;
			int d = getVector().compareTo(vpp.getVector());
			if (d != 0) return d;
			d = getPattern().compareTo(vpp.getPattern());
			return d;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj instanceof VectorPatternPair)
				return compareTo((VectorPatternPair)obj) == 0;
			return false;
		}
	}

	public SIAScaleEncodingOld(PointSet dataset) {
		//Find full vector table
		VectorPointPair[][] vectorTable = computeVectorTable(dataset);

		TreeSet<VectorPointPair> sortedSIAVectorTable = new TreeSet<VectorPointPair>();

		for(int i = 0; i < vectorTable.length; i++)
			for(int j = 0; j < vectorTable.length; j++)
				sortedSIAVectorTable.add(vectorTable[i][j]);

		//Get sorted list of <vector,mtp> pairs
		TreeSet<VectorPatternPair> sortedListofVectorMTPPairs = new TreeSet<VectorPatternPair>();

		Vector thisVector = sortedSIAVectorTable.first().getVector();
		VectorPatternPair thisVectorPatternPair = new VectorPatternPair(thisVector, new PointSet());
		for(VectorPointPair vpp : sortedSIAVectorTable) {
			if (vpp.getVector().equals(thisVector))
				thisVectorPatternPair.addPoint(vpp.getPoint());
			else {
				sortedListofVectorMTPPairs.add(thisVectorPatternPair);
				thisVector = vpp.getVector();
				thisVectorPatternPair = new VectorPatternPair(thisVector, new PointSet());
				thisVectorPatternPair.addPoint(vpp.getPoint());
			}
		}
		sortedListofVectorMTPPairs.add(thisVectorPatternPair);

		//Compute scaling vector table
		int n = sortedListofVectorMTPPairs.size();
		ScalingVectorTableEntry[][] scalingVectorTable = new ScalingVectorTableEntry[n][n];

		VectorPatternPair[] vecMtpPairArray = new VectorPatternPair[n];
		sortedListofVectorMTPPairs.toArray(vecMtpPairArray);

		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++) {
				// column header/row header
				VectorPatternPair numerator = vecMtpPairArray[j];
				VectorPatternPair denominator = vecMtpPairArray[i];
				if (denominator.getVector().getX()==0 || denominator.getVector().getY()==0)
					scalingVectorTable[i][j] = null;
				else {
					RationalVector scalingVector = 
							new RationalVector(new Rational(numerator.getVector().getX(),denominator.getVector().getX()),
								               new Rational((long)numerator.getVector().getY(),(long)denominator.getVector().getY()));
					scalingVectorTable[i][j] = new ScalingVectorTableEntry(scalingVector,numerator,denominator);
				}
			}
	}

	class RationalVector implements Comparable<RationalVector> {
		Rational x, y;

		RationalVector(Rational x, Rational y) {
			this.x = x;
			this.y = y;
		}

		Rational getX() {return x;}
		Rational getY() {return y;}

		@Override
		public int compareTo(RationalVector rv) {
			if (rv == null) return 1;
			int d = x.compareTo(rv.getX());
			if (d != 0) return d;
			return y.compareTo(rv.getY());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj instanceof RationalVector)) return false;
			return compareTo((RationalVector)obj) == 0;
		}
	}

	class ScalingVectorTableEntry implements Comparable<ScalingVectorTableEntry> {
		RationalVector scalingVector;
		VectorPatternPair numeratorVectorPatternPair;
		VectorPatternPair denominatorVectorPatternPair;

		ScalingVectorTableEntry(RationalVector scalingVector, 
				                VectorPatternPair numeratorVectorPatternPair,
				                VectorPatternPair denominatorVectorPatternPair) {
			this.scalingVector = scalingVector;
			this.numeratorVectorPatternPair = numeratorVectorPatternPair;
			this.denominatorVectorPatternPair = denominatorVectorPatternPair;
		}

		RationalVector getScalingVector() {return scalingVector;}
		VectorPatternPair getNumeratorVectorPatternPair() {return numeratorVectorPatternPair;}
		VectorPatternPair getDenominatorVectorPatternPair() {return denominatorVectorPatternPair;}

		@Override
		public int compareTo(ScalingVectorTableEntry svte) {
			if (svte==null) return 1;
			int d = getScalingVector().compareTo(svte.getScalingVector());
			if (d != 0) return d;
			d = getNumeratorVectorPatternPair().compareTo(svte.getNumeratorVectorPatternPair());
			if (d != 0) return d;
			return getDenominatorVectorPatternPair().compareTo(svte.getDenominatorVectorPatternPair());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj instanceof ScalingVectorTableEntry)) return false;
			return compareTo((ScalingVectorTableEntry)obj) == 0;
		}
	}

	private VectorPointPair[][] computeVectorTable(PointSet points) {
		TreeSet<Point> pointsTreeSet = points.getPoints();
		VectorPointPair[][] vectorTable = new VectorPointPair[points.size()][points.size()];
		int i = 0;
		for(Point p1 : pointsTreeSet) {
			int j = 0;
			for(Point p2 : pointsTreeSet) {
				VectorPointPair vp = new VectorPointPair(p1,p2,i);
				vectorTable[i][j] = vp;
				j++;
			}
			i++;
		}

		return vectorTable;
	}


}
