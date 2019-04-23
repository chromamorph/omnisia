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

public class SCALEXIA3Encoding extends Encoding {
	private Integer maxPitch;
	private ArrayList<ArrayList<ArrayList<Long>>> vectorArray = new ArrayList<ArrayList<ArrayList<Long>>>();
	private TreeSet<PVF> S = new TreeSet<PVF>();
	private PatternPairWithPVF[] patternPairs;
	private int minPatternSize = 0;
	private double minCompactness = 0.0;
	private double maxProportionOfObjectPatternInImagePattern = 1.0;


	class PatternPairWithPVF extends PatternPair {

		PVF pvf;
		
		public PatternPairWithPVF(PointSet pattern1, PointSet pattern2, PVF pvf) {
			super(pattern1, pattern2);
			this.pvf = pvf;
		}
		
		public String toString() {
			return "PatternPairWithPVF("+getPattern1()+","+getPattern2()+","+pvf+")";
		}
		
	}
	
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
			int d = o.P.size()-P.size(); if (d != 0) return d;
			d = (int)Math.signum(o.P.getCompactness(dataset)-P.getCompactness(dataset)); if (d != 0) return d;
			d = (int)Math.signum(o.getImagePattern().getCompactness(dataset)-getImagePattern().getCompactness(dataset)); if (d != 0) return d;
			d = (int)Math.signum(getProportionOfObjectPatternInImagePattern()-o.getProportionOfObjectPatternInImagePattern()); if (d != 0) return d;
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

		public PointSet getImagePattern() {
			PointSet imagePattern = P.translate(v);
			if (isInv) 
				imagePattern = imagePattern.invert();
			imagePattern = imagePattern.scale(f);
			return imagePattern;
		}
		
		public double getProportionOfObjectPatternInImagePattern() {
			return (getObjectImageIntersection().size()*1.0)/P.size();
		}
		
		public PointSet getObjectImageIntersection() {
			return P.intersection(getImagePattern());
		}

		public boolean isGoodEnough() {
			if (P.size() < minPatternSize) return false;
			if (P.getCompactness(dataset) < minCompactness) return false;
			if (getProportionOfObjectPatternInImagePattern() > maxProportionOfObjectPatternInImagePattern) return false;
			return true;
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

	public SCALEXIA3Encoding(			
			PointSet dataset, 
			int minPatternSize, 
			boolean includeInversions) {
		this(dataset, minPatternSize, includeInversions, 0.0, 1.0);
	}
	
	public SCALEXIA3Encoding(
			PointSet dataset, 
			int minPatternSize, 
			boolean includeInversions,
			double minCompactness,
			double maxProportionOfObjectPatternInImagePattern) {
		this.dataset = dataset;
		maxPitch = dataset.getMaxY();
		this.minPatternSize = minPatternSize;
		this.minCompactness = minCompactness;
		this.maxProportionOfObjectPatternInImagePattern = maxProportionOfObjectPatternInImagePattern;

		/*
		 * pitchCompLists is an ArrayList of ArrayList<Integer>s
		 * pitchCompLists[c] lists the pitch components the vectors
		 * in column c of the vector table. That is, pitchCompLists[c]
		 * contains the pitch components of the inter-point vectors
		 * originating on dataset[c] 
		 */

		ArrayList<TreeSet<Integer>> pitchCompLists = new ArrayList<TreeSet<Integer>>();

		/*
		 * A list of lists of vectors.
		 * positiveVectors.get(i) returns a list of the positive interpoint vectors
		 * from dataset[i]
		 */
		ArrayList<ArrayList<Vector>> positiveVectors = new ArrayList<ArrayList<Vector>>();

		/*
		 * vectorArray is a three-dimensional array.
		 * 
		 * vectorArray[i] stores all the vectors originating at dataset[i]. 
		 * 
		 * vectorArray[i][j] is a list of the time components of the vectors
		 * originating at dataset[i] whose pitch components are equal to j-maxPitch.
		 */
		for(Point p : dataset.getPoints()) {
			ArrayList<Vector> posVecsFromThisPoint = new ArrayList<Vector>();
			ArrayList<ArrayList<Long>> vectorsFromP = new ArrayList<ArrayList<Long>>();
			TreeSet<Integer> pitchCompsForP = new TreeSet<Integer>();
			for(int i = 0; i < 2*maxPitch+1; i++) 
				vectorsFromP.add(new ArrayList<Long>());
			for(Point q : dataset.getPoints()) {
				Vector v = new Vector(p,q);
				if (v.compareTo(new Vector(0,0))>0)
					posVecsFromThisPoint.add(v);
				ArrayList<Long> timeComponentList = vectorsFromP.get(v.getY()+maxPitch);
				timeComponentList.add(v.getX());
				pitchCompsForP.add(v.getY());
			}
			vectorArray.add(vectorsFromP);
			pitchCompLists.add(pitchCompsForP);
			positiveVectors.add(posVecsFromThisPoint);
		}

		System.out.println("vectorArray and pitchCompLists computed");


		for(int c1 = 0; c1 < dataset.size() - 1; c1++) //Column for origin point for object vectors
			for(int c2 = c1+1; c2 < dataset.size(); c2++) { //Column for origin point for image vectors
				TreeSet<FCVCV> L = new TreeSet<FCVCV>(); //List of FCVCV n-tuples for this pair of origin points
				//Each FCVCV n-tuple gives object vector, image vector, time scale factor and if inv
				TreeSet<FCVCV> LZeros = new TreeSet<FCVCV>();
				TreeSet<FCVCV> LInv = new TreeSet<FCVCV>(); //List of FCVCV n-tuples for this pair of origin points for inversionally related vectors
				TreeSet<FCVCV> LInvZeros = new TreeSet<FCVCV>();
				for(Vector v1 : positiveVectors.get(c1)) {
					long t1 = v1.getX();
					int p = v1.getY();
					int pInv = -p;
					int i = p + maxPitch;
					int iInv = 2*maxPitch-i;
					ArrayList<Long> T = vectorArray.get(c2).get(i);
					ArrayList<Long> TInv = vectorArray.get(c2).get(iInv);
					for(long t2 : T) {
						Rational f;
						if (t1 == 0l && t2 == 0l)
							f = new Rational(0,1);
						else if (t1 != 0l && t2 != 0l)
							f = new Rational(t2,t1);
						else
							continue;
						Vector v2 = new Vector(t2,p);
						FCVCV fcvcv = new FCVCV(f,c1,v1,c2,v2,false);
						if (f.equals(new Rational(0,1)))
							LZeros.add(fcvcv);
						else
							L.add(fcvcv);
					}
					for(long t2 : TInv) {
						Rational f;
						if (t1 == 0l && t2 == 0l)
							f = new Rational(0,1);
						else if (t1 != 0l && t2 != 0l)
							f = new Rational(t2,t1);
						else
							continue;
						Vector v2 = new Vector(t2,pInv);
						FCVCV fcvcv = new FCVCV(f,c1,v1,c2,v2,true);
						if (f.equals(new Rational(0,1)))
							LInvZeros.add(fcvcv);
						else
							LInv.add(fcvcv);
					}
				}
				//First add patterns for no inversion
				addPatternsToS(L,LZeros,false,c1,c2);
				//Now add patterns for inversion
				addPatternsToS(LInv,LInvZeros,true,c1,c2);
			}

		//Make patternPairs
		TreeSet<PatternPair> patternPairList = new TreeSet<PatternPair>();
		for(PVF pvf : S) {
			PointSet objectPattern = pvf.P;
			PointSet imagePattern = pvf.getImagePattern();
			patternPairList.add(new PatternPairWithPVF(objectPattern,imagePattern,pvf));
		}
		patternPairs = new PatternPairWithPVF[patternPairList.size()];
		patternPairList.toArray(patternPairs);
	}

	private void addPatternsToS(TreeSet<FCVCV> LOrLInv, TreeSet<FCVCV> LZerosOrLInvZeros, boolean isInv, int c1, int c2) {
		Point p0 = dataset.get(c1);
		Vector v = new Vector(p0,dataset.get(c2));
		PointSet P = null;
		Rational f = null;
		ArrayList<FCVCV> LArray = new ArrayList<FCVCV>(LOrLInv);
		P = new PointSet(p0);
		if (!LArray.isEmpty()) {
			P.add(p0.translate(LArray.get(0).v1));
			f = LArray.get(0).f;
		}
		for(FCVCV fcvcv : LZerosOrLInvZeros)
			P.add(p0.translate(fcvcv.v1));
		if (LArray.size() == 0 && P.size() >= minPatternSize) {
			f = new Rational(0,1);
//			System.out.println("P has size "+P.size()+"and LArray is empty");
		}
		for(int i = 1; i < LArray.size(); i++) {
			if (LArray.get(i).f.equals(f))
				P.add(p0.translate(LArray.get(i).v1));
			else {
				PVF pvf = new PVF(p0,P,v,f,isInv);
				if (pvf.isGoodEnough())
					S.add(pvf);
				P = new PointSet(p0,p0.translate(LArray.get(i).v1));
				for(FCVCV fcvcv : LZerosOrLInvZeros)
					P.add(p0.translate(fcvcv.v1));
				f = LArray.get(i).f;
			}
		}
		if (f== null && !P.isEmpty())
			f = new Rational(0,1);
		PVF pvf = new PVF(p0,P,v,f,isInv);
		if (pvf.isGoodEnough())
			S.add(pvf);
	}

	public SCALEXIA3Encoding(
			String filePathName, 
			PitchRepresentation pitchRepresentation,
			int minPatternSize,
			boolean includeInversions) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(new PointSet(filePathName, pitchRepresentation), minPatternSize, includeInversions);
	}

	public SCALEXIA3Encoding(
			String filePathName, 
			PitchRepresentation pitchRepresentation,
			int minPatternSize,
			boolean includeInversions,
			double minCompactness,
			double maxProportionOfObjectPatternInImagePattern) throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException, MissingTieStartNoteException {
		this(new PointSet(filePathName, pitchRepresentation), minPatternSize, includeInversions, minCompactness, maxProportionOfObjectPatternInImagePattern);
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

	public static void main(String[] args) {
		try {
			int minPatternSize = args.length > 1?Integer.parseInt(args[1]):5;
			double minCompactness = args.length > 2?Double.parseDouble(args[2]):0.5;
			SCALEXIA3Encoding encoding = new SCALEXIA3Encoding(
//										"data/simple/scalingExample01.pts",
//										"data/simple/inversion-example.pts",
//										"data/simple/scalexia-example.pts",
//										"data/Haydn/MenuettoAlRovescio.opnd",
//										"data/Die Kunst der Fuge/ContrapunctusVIStart.opnd",
//										"data/WTCI-FUGUES-FOR-JNMR-2014/bwv847b-done.opnd",
//										"data/Die Kunst der Fuge/ContrapunctusVI.opnd",
					args[0],
					PitchRepresentation.CHROMATIC_PITCH,
//					PitchRepresentation.MORPHETIC_PITCH,
					minPatternSize, //minPatternSize 20 for rovescio, 5 for contrapunctus
					true, //includeInversions
					minCompactness, //minCompactness .5 for contrapunctus, 0.0 for rovescio
					0.1 //maxProportionOfObjectPatternInImagePattern
					);
//			System.out.println(encoding);
//			for(PatternPair patternPair : encoding.patternPairs)
//				System.out.println(patternPair);
			encoding.draw();
		} catch (NoMorpheticPitchException | IOException
				| UnimplementedInputFileFormatException
				| InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (MissingTieStartNoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
