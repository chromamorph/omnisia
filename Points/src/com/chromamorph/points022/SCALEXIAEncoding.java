package com.chromamorph.points022;

import java.util.TreeSet;

import com.chromamorph.maths.Rational;

public class SCALEXIAEncoding {

	class EncodingTuple implements Comparable<EncodingTuple>{
		PointSet P;
		Vector v;
		Point p;
		Rational s;

		EncodingTuple(PointSet P, Vector v, Point p, Rational s) {
			this.P = P;
			this.v = v;
			this.p = p;
			this.s = s;
		}

		@Override
		public int compareTo(EncodingTuple o) {
			int d = P.compareTo(o.P);
			if (d != 0) return d;
			d = v.compareTo(o.v);
			if (d != 0) return d;
			d = p.compareTo(p);
			if (d != 0) return d;
			return s.compareTo(o.s);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj instanceof EncodingTuple)) return false;
			return compareTo((EncodingTuple)obj)==0;
		}

		@Override
		public String toString() {
			return "EncodingTuple("+P+", "+v+", "+p+", "+s+")";
		}
	}

	class SXK implements Comparable<SXK> {
		Rational s;
		int k;

		SXK(Rational s, int k) {
			this.s = s;
			this.k = k;
		}

		@Override
		public int compareTo(SXK sxk) {
			int d = s.compareTo(sxk.s);
			if (d != 0) return d;
			return k - sxk.k;
		}

		@Override
		public boolean equals(Object sxk) {
			if (sxk == null) return false;
			if (!(sxk instanceof SXK)) return false;
			return compareTo((SXK)sxk)==0;
		}
	}

	TreeSet<EncodingTuple> E;

	public SCALEXIAEncoding(PointSet dataset) {
		int yMax = dataset.getMaxY();
		VectorSet[][] V = new VectorSet[dataset.size()][2*yMax+1];
		for(int i = 0; i < dataset.size(); i++)
			for(int y = 0; y < 2*yMax+1; y++)
				V[i][y] = new VectorSet();
		E = new TreeSet<EncodingTuple>();
		for(int i = 0; i < dataset.size(); i++)
			for(int j = 0; j < dataset.size(); j++) {
				Vector v = new Vector(dataset.get(i),dataset.get(j));
				int y = v.getY()+yMax;
				V[i][y].add(v);
			}
		for(int i = 0; i < dataset.size()-1; i++)
			for(int j = i + 1; j < dataset.size(); j++) {
				TreeSet<SXK> W = new TreeSet<SXK>();
				for(int k = i+1; k < dataset.size(); k++) {
					Vector v1 = new Vector(dataset.get(i),dataset.get(k));
					System.out.print("v1="+v1);
					int y = v1.getY() + yMax;
					System.out.print(", y="+y);
					for(Vector v2 : V[j][y].getVectors()) {
						System.out.print(" v2="+v2+",");
						if (v1.getX() != 0 && v2.getX() != 0) {
							Rational s = new Rational(v2.getX(),v1.getX());
							W.add(new SXK(s,k));
						} else if (v1.getX() == 0 && v2.getX() == 0)
							W.add(new SXK(new Rational(0,1),k));
					}
					System.out.println();
				}
				if (W.isEmpty()) System.out.println("W is empty");
				Rational s = W.first().s;
				Point p = dataset.get(i);
				PointSet P = new PointSet(p);
				Vector v = new Vector(p,dataset.get(j));
				for(SXK w : W) {
					if (w.s.equals(s))
						P.add(dataset.get(w.k));
					else {
						E.add(new EncodingTuple(P,v,p,s));
						P = new PointSet(dataset.get(i),dataset.get(w.k));
						s = w.s;
					}
				}
				E.add(new EncodingTuple(P,v,p,s));
			}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SCALEXIAEncoding(\n  "+E.first());
		for(EncodingTuple e : E.tailSet(E.first(),false))
			sb.append(",\n  "+e);
		sb.append("\n)");
		return sb.toString();
	}

	public static void main(String[] args) {
		String opndFileName = "/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/simple/scalingExample01.pts";
		PointSet dataset = new PointSet(opndFileName);
		System.out.println(new SCALEXIAEncoding(dataset));
	}
}
