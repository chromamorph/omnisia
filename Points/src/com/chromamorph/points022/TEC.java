package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.TreeSet;

public class TEC implements Comparable<TEC>{
	private PointSet pattern = null;
	private ArrayList<Integer> cis = null;
	private VectorSet translators = new VectorSet();
	private Double compressionRatio = null;
	private Integer coverage = null;
	private PointSet coveredPoints = null;
	private Double compactness = null;
	private Integer numPointsInBB = null;
	private PointSet dataset = null;
	private boolean isDual = false;
	private ArrayList<TEC> patternTecs = null;

	public TEC() {}

	private void reset() {
		compressionRatio = null;
		coverage = null;
		coveredPoints = null;
		compactness = null;
		numPointsInBB = null;
	}

	public TEC copy() {
		TEC newTEC = new TEC();
		newTEC.cis = null;
		if (cis != null) {
			newTEC.cis = new ArrayList<Integer>();
			for(Integer i : cis) newTEC.cis.add(i);
		}
		newTEC.dataset = dataset.copy();
		newTEC.isDual = isDual;
		newTEC.pattern = pattern.copy();
		newTEC.patternTecs = null;
		if (patternTecs != null) {
			newTEC.patternTecs = new ArrayList<TEC>();
			for(TEC patternTec : patternTecs)
				newTEC.patternTecs.add(patternTec.copy());
		}
		newTEC.translators = translators.copy();
		newTEC.reset();
		return newTEC;
	}
	
	public TEC(PointSet pattern, PointSet dataset) {
		setPattern(pattern);
		setDataset(dataset);
		translators.add(new Vector(0,0));
	}

	public TEC(PointSet pointSet, ArrayList<Integer> cis, PointSet dataSet) {
		setPattern(pointSet);
		this.cis = cis;
		setDataset(dataSet);
	}

	public TEC(PointSet pattern, VectorSet translators, PointSet dataset) {
		setPattern(pattern);
		setTranslators(translators);
		setDataset(dataset);
	}

	public TEC(ArrayList<TEC> patternTecs, VectorSet translators, PointSet dataset) {
		setPatternTecs(patternTecs);
		setTranslators(translators);
		setDataset(dataset);
	}

	public void setPatternTecs(ArrayList<TEC> patternTecs) {
		this.patternTecs = patternTecs;
		this.pattern = null;
		reset();
	}

	/**
	 * Parses a string representing a TEC.
	 * The String must have the format
	 * 
	 * T(P(p1,p2,...),V(v1,v2,...))
	 * 
	 * where pk = p(x,y) and vk = v(x,y)
	 * where x and y are integers.
	 * @param l
	 */
	public TEC(String l) {
		PointSet pointSet = PointSet.getPointSetFromString(l);
		VectorSet vectorSet = VectorSet.getVectorSetFromString(l);
		setPattern(pointSet);
		setTranslators(vectorSet);
	}

	public void setPattern(PointSet pattern) {
		this.pattern = pattern;
		this.patternTecs = null;
		reset();
	}

	public boolean isDual() {
		return isDual;
	}

	public void setDual(boolean dual) {
		isDual = dual;
	}

	public ArrayList<Integer> getCIS() {
		return cis;
	}

	public PointSet getPattern() {
		if (pattern != null)
			return pattern;
		if (patternTecs != null) {
			PointSet covSet = new PointSet();
			for (TEC tec : patternTecs)
				covSet.addAll(tec.getCoveredPoints());
			return covSet;
		}
		return null;
	}

	public ArrayList<TEC> getPatternTECs() {
		return patternTecs;
	}

	public String toString() {
		String outString = null;
		if (pattern != null) { // In this TEC, the pattern is just a point set
			PointSet normalizedPattern = pattern.translate(translators.get(0));
			VectorSet normalizedTranslators = translators.translate(translators.get(0).inverse());
			outString = "T("+normalizedPattern+","+normalizedTranslators+")";
		}
		if (patternTecs != null) { // In this TEC, the pattern is represented by a set of TECs
			outString = "T(P("+patternTecs.get(0);
			for(int i = 1; i < patternTecs.size(); i++) {
				outString += ","+patternTecs.get(i);
			}
			outString += "),"+getTranslators()+")";
		}
		return outString;
	}

	public int getPatternSize() {
		return getPattern().size();
	}

	public int getPatternEncodingSize() {
		if (pattern != null)
			return getPatternSize();
		else if (patternTecs != null) {
			int s = 0;
			for(TEC tec : patternTecs) 
				s += tec.getEncodingLength();
			return s;
		}
		return 0;
	}

	public int getEncodingLength() {
		return getTranslators().size()-1+getPatternEncodingSize();
	}

	public int getTranslatorSetSize() {
		return translators.size();
	}

	public double getCompressionRatio() {
		if (compressionRatio != null) return compressionRatio;
		return (compressionRatio = (1.0*getCoverage())/getEncodingLength());
	}

	public PointSet getCoveredPoints() {
		if (coveredPoints != null)
			return coveredPoints;
		PointSet newCoveredSet = new PointSet();
		PointSet patt = getPattern();
		TreeSet<Vector> vectors = translators.getVectors();
		for(Vector vector : vectors)
			newCoveredSet.addAll(patt.translate(vector)); //Translators includes zero vector.
		coveredPoints = newCoveredSet;
		return coveredPoints;
	}

	public int getCoverage() {
		if (coverage != null) return coverage;
		coveredPoints = getCoveredPoints();
		return (coverage = coveredPoints.size());
	}

	public int getNumPointsInBB() {
		if (numPointsInBB != null) return numPointsInBB;
		Point tl = getPattern().getTopLeft();
		Point br = getPattern().getBottomRight();
		PointSet bbSubset = dataset.getBBSubset(tl,br);
		return (numPointsInBB = bbSubset.size());
	}

	public PointSet getDataset() {
		return dataset;
	}

	/**
	 * getCompactness returns the bounding-box compactness of
	 * the most compact pattern occurrence in this TEC.
	 * @return Bounding-box compactness of the most compact
	 * pattern occurrence in this TEC.
	 */
	public double getCompactness() {
		if (compactness != null) return compactness;
		compactness = 0.0;
		for(Vector v : getTranslators().getVectors()) {
			double c = getPattern().translate(v).getCompactness(getDataset());
			if (c > compactness) compactness = c;
		}
		return compactness;
	}

	public VectorSet getTranslators() {
		return translators;
	}

	public long getBBArea() {
		return getPattern().getBBArea();
	}

	/**
	 * Note that the dual of a recursive TEC is not recursive
	 * in this implementation, because no recursive encoding
	 * of the translators of this TEC is computed.
	 * @return
	 */
	public TEC getDual() {
		TEC dual = new TEC();
		dual.pattern = new PointSet();
		Point firstPoint = getPattern().first();
		dual.pattern.add(firstPoint);
		for(Vector v : translators.getVectors())
			dual.pattern.add(firstPoint.translate(v));
		TreeSet<Point> patternPoints = getPattern().getPoints();
		for(Point p : patternPoints)
			dual.translators.add(new Vector(firstPoint,p));
		dual.reset();
		dual.dataset = dataset;
		dual.setDual(true);
		return dual;
	}

	public PointFreqSet getPointFreqSet() {
		PointFreqSet pfs = new PointFreqSet();
		TreeSet<Point> patternPoints = getPattern().getPoints();
		for(Point p : patternPoints)
			for(Vector v : translators.getVectors())
				pfs.addPoint(p.translate(v));
		return pfs;
	}

	public String getSIAMTableLatexString(TreeSet<VectorPointPair> siamTable) {
		ArrayList<VectorPointPair> sta = new ArrayList<VectorPointPair>();
		sta.addAll(siamTable);
		StringBuilder sb = new StringBuilder();
		sb.append("\\langle&"); // Open list
		//Add first element
		sb.append(sta.get(0).getLatexString());
		for(int i = 1; i < sta.size(); i++) {
			sb.append(",");
			if (!sta.get(i).getVector().equals(sta.get(i-1).getVector()))
				sb.append("\\\\\n&");
			sb.append(sta.get(i).getLatexString());
		}
		sb.append("\\rangle"); // Close list
		return sb.toString();
	}
	
	/**
	 * Removes as many translators as it can from
	 * the translator set for this TEC such that
	 * set of covered points remains unchanged.
	 */
	public void removeRedundantTranslators() {

		PointFreqSet pfs = getPointFreqSet();
		
		//System.out.println("pfs ="+pfs.getLatexString());

		/*
		 * If there are no multipoints in pfs,
		 * then we cannot remove any translators.
		 */

		if (pfs.getMultiPoints().isEmpty()) return;

		/*
		 * The set of translators that can be removed
		 * is a subset of those that map the pattern onto
		 * a subset of the multipoints.
		 * 
		 * We do SIAM on the pattern and the multipoints.
		 * We then only need to look at vectors that map
		 * the whole pattern onto multipoints - i.e.,
		 * vectors that have the same number of points
		 * as are in the pattern.
		 */

		TreeSet<VectorPointPair> siamTable = new TreeSet<VectorPointPair>();
		for(Point p1 : getPattern().getPoints()) 
			for(PointFreq p2 : pfs.getMultiPoints()) 
				siamTable.add(new VectorPointPair(p1,p2.getPoint()));

//		String siamTableLatexString = getSIAMTableLatexString(siamTable);
//		System.out.println("\n"+siamTableLatexString);
		
		TreeSet<Vector> removableVectors = new TreeSet<Vector>();

		Vector v = siamTable.first().getVector();
		int count = 0;
		for(VectorPointPair vp : siamTable) {
			if (count == 0) v = vp.getVector();
			if (vp.getVector().equals(v)) {
				count++;
				if (count == getPatternSize()) {
					count = 0;
					removableVectors.add(v);
				}
			} else {
				count = 1;
				v = vp.getVector();
			}
		}

//		for(Vector vec : removableVectors)
//			System.out.println(vec.getLatexString());
		////////////////////////////////////////////////////////		

		/*
		 * Check to see if all removable vectors can be removed
		 */

		PointFreqSet remVecPFS = new PointFreqSet();

		TreeSet<PointFreq> maxPoints = new TreeSet<PointFreq>();

		boolean allCanBeRemoved = true;

		for(Vector vec : removableVectors) {
			TreeSet<Point> patternPoints = getPattern().getPoints();
			for(Point pnt : patternPoints) {
				Point newPoint = pnt.translate(vec);
				PointFreq pf = remVecPFS.addPoint(newPoint,vec);
				if (pf.getFreq() == pfs.getFreq(newPoint)) { 
					//Then we've removed all instances of this point
					allCanBeRemoved = false;
					pf.setMaxPoint(true);
					maxPoints.add(pf);
				}
			}
		}
		
//		System.out.print("maxPoints = ");
//		for(PointFreq pf : maxPoints) System.out.println(" "+pf.getLatexString());
//		System.out.println("allCanBeRemoved="+allCanBeRemoved);
		
		if (allCanBeRemoved) {
			for(Vector vec : removableVectors)
				translators.remove(vec);
			return;
		}

		////////////////////////////////////////////////////////

		/*
		 * So we can't remove all of the removable vectors.
		 * 
		 * But we now know which vectors are responsible for 
		 * maxPoints in remVecPFS. For each maxPoint, we cannot
		 * remove all of the vectors that map points onto it.
		 * 
		 * We have to find the smallest set of vectors that includes
		 * at least one vector for each maxPoint.
		 * 
		 * We begin by making a list of <vector,pointSet> pairs. Each
		 * such pair gives, for each vector, the set of max points onto which
		 * that vector maps pattern points.
		 * 
		 * We can adopt a greedy strategy, where we start by choosing
		 * the vector that has the most max points attached to it.
		 * 
		 * We then remove the covered max points from all the remaining
		 * <vector,pointSet> pairs and re-sort the <vector,pointSet> pairs
		 * by pointSet size.
		 * 
		 * Repeat until all max points are covered.
		 * 
		 * This gives us a list of retained removable vectors. The other removable
		 * vectors can be removed.
		 */

		/* First find the set of <vector,point set> pairs. This will be sorted into
		 * decreasing order of size of point set.
		 */

		TreeSet<VectorPointSetPair> vectorMaxPointSetPairs = new TreeSet<VectorPointSetPair>();

		for(PointFreq pf : maxPoints) {
			for(Vector vec : pf.getTranslators()) {
				VectorPointSetPair vmpFloor = vectorMaxPointSetPairs.floor(new VectorPointSetPair(vec, new PointSet()));
				if (vmpFloor != null && vmpFloor.getVector().equals(vec)) {
					vmpFloor.addPoint(pf.getPoint());
				} else {
					vectorMaxPointSetPairs.add(new VectorPointSetPair(vec,new PointSet(pf.getPoint())));
				}
			}
		}

		/*
		 * Now we find the set of retained vectors. The first retained vector
		 * is the one whose set of max points is the largest - this should be the
		 * first vectorPointSetPair in vectorMaxPointSetPairs.
		 */

		TreeSet<Vector> retainedVectors = new TreeSet<Vector>();

		while(!vectorMaxPointSetPairs.isEmpty()) {
			VectorPointSetPair firstVPS = vectorMaxPointSetPairs.first();
			retainedVectors.add(firstVPS.getVector());

			for(Point maxPoint : firstVPS.getPointSet().getPoints()) {
				for(VectorPointSetPair vps : vectorMaxPointSetPairs.tailSet(firstVPS, false)) {
					vps.getPointSet().removeWithoutReset(maxPoint);
				}
			}

			TreeSet<VectorPointSetPair> newVectorPointSetPairs = new TreeSet<VectorPointSetPair>();
			for(VectorPointSetPair vps: vectorMaxPointSetPairs.tailSet(firstVPS, false)) {
				if (!vps.getPointSet().isEmpty())
					newVectorPointSetPairs.add(vps);
			}
			vectorMaxPointSetPairs = newVectorPointSetPairs;
		}

		////////////////////////////////////////////////////////

		/*
		 * Now remove all removable vectors apart from retained vectors.
		 */

		VectorSet newTranslators = new VectorSet();

		for(Vector vec : translators.getVectors()) {
			if (!removableVectors.contains(vec) || retainedVectors.contains(vec)){
				newTranslators.add(vec);
			}
		}

		translators = newTranslators;
		reset();

		////////////////////////////////////////////////////////



	}

	public double getUnsquareness() {
		return getPattern().getUnsquareness();
	}

	public void setDataset(PointSet dataset) {
		this.dataset = dataset;
	}

	public void setTranslators(VectorSet translators) {
		this.translators = translators;
		reset();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TEC)) return false;
		return compareTo((TEC)obj) == 0;
	}

	@Override
	public int compareTo(TEC tec) {
		if (tec == null) return 1;
		int d = getPattern().compareTo(tec.getPattern());
		if (d != 0) return d;
		return getTranslators().compareTo(tec.getTranslators());
	}

	public ArrayList<PointSet> getPointSets() {
		ArrayList<PointSet> pointSets = new ArrayList<PointSet>();
		TreeSet<Vector> translators = getTranslators().getVectors();
		PointSet patternPointSet = getPattern();
		for (Vector vector : translators) {
			pointSets.add(patternPointSet.translate(vector));
		}
		return pointSets;
	}
	
	public String getLatexString() {
		return "\\langle" + getPattern().getLatexString()+","+getTranslators().getLatexString()+"\\rangle";
	}
	

	//	public static TEC TEC_CONST = new TEC("T(P(p(118,23),p(124,22),p(274,23),p(280,22),p(910,23),p(916,22)),V(v(0,0),v(50,8),v(50,10),v(52,-1),v(52,6),v(52,8),v(54,1),v(62,9),v(62,11),v(64,0),v(64,7),v(64,9),v(66,2),v(130,0),v(132,0),v(132,5),v(134,1),v(134,6),v(136,4),v(142,-2),v(144,-2),v(144,14),v(146,-3),v(146,15),v(148,-1),v(148,13),v(150,-1),v(152,-2),v(154,0),v(156,0),v(156,3),v(156,12),v(158,2),v(158,13),v(160,11),v(162,11),v(164,1),v(168,1),v(194,-3)))");
	//	public static TEC TEC_CONST_2 = new TEC("T(P(p(118,23),p(124,22),p(274,23),p(280,22),p(910,23),p(916,22)),V(v(0,0),v(50,8),v(50,10),v(52,-1),v(52,6),v(52,8),v(54,1),v(62,9),v(62,11),v(64,0),v(64,7),v(64,9),v(66,2),v(130,0),v(132,0),v(132,5),v(134,1),v(134,6),v(136,4),v(142,-2),v(144,-2),v(144,14),v(146,-3),v(146,15),v(148,-1),v(148,13),v(150,-1),v(152,-2),v(154,0),v(156,0),v(156,3),v(156,12),v(158,2),v(158,13),v(160,11),v(162,11),v(164,1),v(168,1),v(194,-3)))");

	public static void main(String[] args) {
		//		PointSet tecPattern = TEC_CONST.getPattern();
		//		PointSet tecPattern2 = TEC_CONST_2.getPattern();
		//		System.out.println("tecPattern: "+tecPattern);
		//		System.out.println("tecPattern2: "+tecPattern2);
		//		System.out.println("Patterns equal? " +tecPattern.equals(tecPattern2));
		//		TEC tec = TEC_CONST;
		//		TEC dual = tec.getDual();
		//		System.out.println("tec: "+tec);
		//		System.out.println("dual: "+dual);
		//		System.out.println("tec width = "+tec.getPattern().getWidth());
		//		System.out.println("dual width = "+dual.getPattern().getWidth());
		//		System.out.println("tec cf: "+tec.getCompressionRatio());
		//		System.out.println("dual cf: "+dual.getCompressionRatio());
		//		System.out.println("tec cov: "+tec.getCoverage());
		//		System.out.println("dual cov: "+dual.getCoverage());
		//		TECQualityComparator comp = new TECQualityComparator();
		//		System.out.println(comp.compare(tec, dual));
		
		TEC tec1 = new TEC("T(P(p(1,1),p(2,2),p(3,3)),V(v(0,0),v(1,1),v(2,2),v(3,3),v(4,4)))");
		System.out.println(tec1.getLatexString());
		tec1.removeRedundantTranslators();
		System.out.println(tec1.getPattern());
		System.out.println(tec1.getTranslators());
//		TEC tec2 = new TEC("T(P(p(0,0),p(1,1),p(0,1),p(1,0)),V(v(0,0),v(1,0),v(2,0),v(2,1),v(2,2)))");
//		System.out.println(tec2);
//		tec2.removeRedundantTranslators();
//		System.out.println(tec2.getPattern());
//		System.out.println(tec2.getTranslators());
	}

}
