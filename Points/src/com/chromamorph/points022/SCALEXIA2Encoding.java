package com.chromamorph.points022;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JFrame;

import processing.core.PApplet;

import com.chromamorph.maths.Rational;
import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class SCALEXIA2Encoding extends Encoding {
	private Integer maxPitch;
	private ArrayList<ArrayList<ArrayList<Long>>> vectorArray = new ArrayList<ArrayList<ArrayList<Long>>>();
	private TreeSet<PVF> S = new TreeSet<PVF>();
	private TreeSet<PatternPair> patternPairs = new TreeSet<PatternPair>();


	class PVF implements Comparable<PVF>{
		Point p0;
		PointSet P;
		Vector v;
		Rational f;
		boolean isInv;

		PVF(Point p0, PointSet P, Vector v, Rational f) {
			this(p0,P,v,f,false);
		}

		PVF(Point p0, PointSet P, Vector v, Rational f, boolean isInv) {
			this.isInv = isInv;
			Point pMin = P.first();
			Vector pZeroToPMin = new Vector(p0,pMin);
			int pZeroToPMinPitchComponent = pZeroToPMin.getY();
			long pZeroToPMinTimeComponent = pZeroToPMin.getX();
			long imageOfPZeroToPMinTimeComponent = f.times(new Rational(pZeroToPMinTimeComponent,1l)).getNumerator();
			Vector imageOfPZeroToPMin = new Vector(imageOfPZeroToPMinTimeComponent,(isInv?-1:1)*pZeroToPMinPitchComponent);
			Point imagePMin = p0.translate(v).translate(imageOfPZeroToPMin);
			this.p0 = pMin;
			this.P = P;
			this.v = new Vector(pMin,imagePMin);
			this.f = f;
		}

		public String toString() {
			return "PVF("+p0+","+P+","+v+","+f+(isInv?", inv":"")+")";
		}

		@Override
		public int compareTo(PVF o) {
			if (o == null) return 1;
			int d = P.size()-o.P.size(); if (d != 0) return d;
			d = P.compareTo(o.P); if (d != 0) return d;
			d = p0.compareTo(o.p0); if (d != 0) return d;
			d = f.compareTo(o.f); if (d != 0) return d;
			d = v.compareTo(o.v); if (d != 0) return d;
			if (isInv!=o.isInv) return isInv?1:-1;
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PVF)) return false;
			return compareTo((PVF)obj)==0;
		}

	}

	class FCVCV implements Comparable<FCVCV> {
		Rational f;
		int c1, c2;
		Vector v1, v2;
		boolean isInv;

		FCVCV (Rational f, int c1, Vector v1, int c2, Vector v2) {
			this(f, c1, v1, c2, v2, false);
		}

		FCVCV (Rational f, int c1, Vector v1, int c2, Vector v2, boolean isInv) {
			this.f = f;
			this.c1 = c1;
			this.v1 = v1;
			this.c2 = c2;
			this.v2 = v2;
			this.isInv = isInv;
		}

		@Override
		public int compareTo(FCVCV o) {
			if (o == null) return 1;
			if (isInv!=o.isInv) return isInv?1:-1;
			int d = f.compareTo(o.f); if (d != 0) return d;
			d = o.c1 - c1;            if (d != 0) return d;
			d = v1.compareTo(o.v1);   if (d != 0) return d;
			d = o.c2 - c2;            if (d != 0) return d;
			d = v2.compareTo(o.v2);   if (d != 0) return d;
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FCVCV)) return false;
			return compareTo((FCVCV)obj)==0;
		}
	}

	public SCALEXIA2Encoding(PointSet dataset, int minPatternSize, boolean includeInversions) {
		this.dataset = dataset;
		maxPitch = dataset.getMaxY();
		/*
		 * vectorArray is a three-dimensional array.
		 * 
		 * vectorArray[i] stores all the vectors originating at dataset[i]. 
		 * 
		 * vectorArray[i][j] is a list of the time components of the vectors
		 * originating at dataset[i] whose pitch components are equal to j-maxPitch.
		 */
		for(Point p : dataset.getPoints()) {
			ArrayList<ArrayList<Long>> vectorsFromP = new ArrayList<ArrayList<Long>>();
			for(int i = 0; i < 2*maxPitch+1; i++) 
				vectorsFromP.add(new ArrayList<Long>());
			for(Point q : dataset.getPoints()) {
				Vector v = new Vector(p,q);
				ArrayList<Long> timeComponentList = vectorsFromP.get(v.getY()+maxPitch);
				timeComponentList.add(v.getX());
			}
			vectorArray.add(vectorsFromP);
		}

		/* Let S be a list of n-tuples of the form <pattern, translation vector, scale factor, isInversion>
		 * For each pair of columns, c1, c2 in the vector table:
		 *   Let L be a list of n-tuples of the form <isInv,f, c1, v1, c2, v2>
		 *   For each vector v1 in column c1
		 *     Let the pitch component of v1 be p
		 *     Let the time component of v1 be t1
		 *     Find all vectors v2 in column c2 that have the pitch component p and store their time components in a list called T
		 *     for each time component t2 in T
		 *       Find the scale factor, f = t2/t1
		 *       Add <false,f,c1,v1,c2,v2> to L
		 *     Find all vectors v2 in column c2 that have the pitch component -p and store their time components in a list called T
		 *     for each time component t2 in T
		 *       Find the scale factor, f = t2/t1
		 *       Add <true,f,c1,v1,c2,v2> to L
		 *   Sort L lexicographically
		 *   p0 = D[c1] 
		 *   P = <p0, p0 + v1.L[0]>
		 *   v = D[c2] - p0
		 *   f = f.L[0]
		 *   for i = 1 to |L| - 1
		 *     if f.L[i] == f
		 *       P.add(p0 + v1.L[i])
		 *     else
		 *       if |P| > 2
		 *         S.add(<P,v,f>)
		 *       P = <p0, p0 + v1.L[i]>
		 *       f = f.L[i]
		 *   if |P| > 2
		 *     S.add(<P,v,f>)
		 *  Return S
		 */

		for(int c1 = 0; c1 < dataset.size() - 1; c1++)
			for(int c2 = c1+1; c2 < dataset.size(); c2++) {
				TreeSet<FCVCV> L = new TreeSet<FCVCV>();
				ArrayList<ArrayList<Long>> vectorsInC1 = vectorArray.get(c1);
				for(int i = 0; i < 2*maxPitch+1; i++) {
					int p = i - maxPitch;
					for(Long t1 : vectorsInC1.get(i)) {
						ArrayList<Long> T = vectorArray.get(c2).get(i);
						for(Long t2 : T) {
							Rational f;
							if (t1 == 0l && t2 == 0l)
								f = new Rational(0,1);
							else if (t1 != 0l && t2 != 0l)
								f = new Rational(t2,t1);
							else
								continue;
							Vector v1 = new Vector(t1,p);
							Vector v2 = new Vector(t2,p);
							L.add(new FCVCV(f,c1,v1,c2,v2));
						}
						if (includeInversions) {
							T = vectorArray.get(c2).get(2*maxPitch-i);
							for(Long t2 : T) {
								Rational f;
								if (t1 == 0l && t2 == 0l)
									f = new Rational(0,1);
								else if (t1 != 0l && t2 != 0l)
									f = new Rational(t2,t1);
								else
									continue;
								Vector v1 = new Vector(t1,p);
								Vector v2 = new Vector(t2,p);
								L.add(new FCVCV(f,c1,v1,c2,v2,true));
							}
						}
					}
				}
				Point p0 = dataset.get(c1);
				PointSet P = new PointSet(p0,p0.translate(L.first().v1));
				Vector v = new Vector(p0,dataset.get(c2));
				Rational f = L.first().f;
				boolean isInv = L.first().isInv;
				ArrayList<FCVCV> LArray = new ArrayList<FCVCV>(L);
				for(int i = 1; i < L.size(); i++) {
					if (LArray.get(i).f.equals(f) && LArray.get(i).isInv==isInv)
						P.add(p0.translate(LArray.get(i).v1));
					else {
						if (P.size() > minPatternSize)
							S.add(new PVF(p0,P,v,f,isInv));
						P = new PointSet(p0,p0.translate(LArray.get(i).v1));
						f = LArray.get(i).f;
						isInv = LArray.get(i).isInv;
					}
				}
				if (P.size() > minPatternSize)
					S.add(new PVF(p0,P,v,f,isInv));				
			}
		//Make patternPairs
		for(PVF pvf : S) {
			PointSet objectPattern = pvf.P;
			PointSet imagePattern = pvf.P.translate(pvf.v);
			if (pvf.isInv) 
				imagePattern = imagePattern.invert();
			imagePattern = imagePattern.scale(pvf.f);
			patternPairs.add(new PatternPair(objectPattern,imagePattern));
		}
	}

	public SCALEXIA2Encoding(
			String filePathName, 
			PitchRepresentation pitchRepresentation,
			int minPatternSize,
			boolean includeInversions) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(new PointSet(filePathName, pitchRepresentation), minPatternSize, includeInversions);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		int indexOfPoint = 0;
		for(ArrayList<ArrayList<Long>> vectorsFromP : vectorArray) {
			sb.append("Vectors from "+dataset.get(indexOfPoint)+"\n");
			int pitchComponent=-maxPitch;
			for(ArrayList<Long> timeComponents : vectorsFromP) {
				if (!timeComponents.isEmpty()) {
					sb.append("  "+pitchComponent+"\t"+timeComponents+"\n");
				}
				pitchComponent++;
			}
			indexOfPoint++;
		}

		sb.append("PATTERNS FOUND\n");
		for(PVF pvf : S) {
			sb.append("  "+pvf.toString()+"\n");
		}
		return sb.toString();
	}

	@Override
	public void draw() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(dataset,patternPairs);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});

	}

	public static void main(String[] args) throws MissingTieStartNoteException {
		try {
			SCALEXIA2Encoding encoding = new SCALEXIA2Encoding(
//					"/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/data/simple/scalingExample01.pts",
//					"/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/data/simple/inversion-example.pts",
					"/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/data/simple/scalexia-example.pts",
//					"/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/data/Haydn/MenuettoAlRovescio.opnd",
//					"/Users/dave/Documents/Work/Research/2014-09-15-workspace/Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv847b-done.opnd",
					PitchRepresentation.CHROMATIC_PITCH,
					//					PitchRepresentation.MORPHETIC_PITCH,
					2, //minPatternSize
					true //includeInversions
					);
			System.out.println(encoding);
			for(PatternPair patternPair : encoding.patternPairs)
				System.out.println(patternPair);
			encoding.draw();
		} catch (NoMorpheticPitchException | IOException
				| UnimplementedInputFileFormatException
				| InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}


}
