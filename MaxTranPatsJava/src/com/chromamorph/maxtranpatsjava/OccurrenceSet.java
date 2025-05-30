package com.chromamorph.maxtranpatsjava;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class OccurrenceSet implements Comparable<OccurrenceSet>{
	private PointSet pattern;
	private TreeSet<Transformation> transformations = new TreeSet<Transformation>();
	private ArrayList<OccurrenceSet> superMTPs;
	private PointSet coveredSet = null; //Do not access this directly - use getCoveredSet()
	private int encodingLength = -1; //Do not access this directly - use getEncodingLength()
	private PointSet dataset;

	public PointSet getDataset() {return dataset;}

	private void resetProperties() {
		coveredSet = null;
		encodingLength = -1;
	}

	public ArrayList<OccurrenceSet> getSuperMTPs() {
		return superMTPs;
	}

	public void setSuperMTPs(ArrayList<OccurrenceSet> superMTPs) {
		this.superMTPs = superMTPs;
		resetProperties();
	}

	public void addSuperMTP(OccurrenceSet superMTP) {
		if (superMTPs == null)
			superMTPs = new ArrayList<OccurrenceSet>();
		superMTPs.add(superMTP);
		resetProperties();
	}

	public OccurrenceSet(PointSet pattern, PointSet dataset) {
		setPattern(pattern);
		this.dataset = dataset;
		resetProperties();
	}

	public OccurrenceSet(String l, PointSet dataset) throws InvalidArgumentException {
		int patternStart, patternEnd, transStart, transEnd;
		if (l.trim().startsWith("OS(P(")) {
			patternStart = 3;
			patternEnd = l.indexOf("[")-1;
		} else if (l.contains("Pat=") && !l.contains("Trans=")) {
			patternStart = l.indexOf("Pat=")+4;
			patternEnd = l.indexOf("Trans=")-2;
		} else
			throw new InvalidParameterException("OccurrenceSet constructor called with invalid String argument,\n  "+l);
		transStart = l.indexOf("[");
		transEnd = l.lastIndexOf("]")+1;
		String patternString = l.substring(patternStart,patternEnd);
		String transformationsString = l.substring(transStart,transEnd);
		setPattern(new PointSet(patternString));
		this.dataset = dataset;
		addAllTransformations(Transformation.readTransformationsFromString(transformationsString));
	}

	public void addTransformation(Transformation transformation) {
		transformations.add(transformation);
		resetProperties();
	}

	public void addAllTransformations(Collection<Transformation> transformations) {
		for(Transformation f : transformations) {
			addTransformation(f);
		}
		resetProperties();
	}

	public PointSet getPattern() {
		return pattern;
	}
	private void setPattern(PointSet pattern) {
		this.pattern = pattern;
		resetProperties();
	}
	public TreeSet<Transformation> getTransformations() {
		return transformations;
	}
	public void setTransformations(Collection<Transformation> transformations) {
		this.transformations = new TreeSet<Transformation>();
		this.transformations.addAll(transformations);
		resetProperties();
	}

	@Override
	public String toString() {
		String s = "ERROR";
//			s = String.format("CF=%.2f, COV=%d, UL=%d, PL=%d, TL=%d, Pat=%s, Trans=%s, SupMTPs=%s", 
//					getSuperMTPs() == null?getCompressionFactor():-1, 
//					getSuperMTPs() == null?getCoverage():-1,
//					getSuperMTPs() == null?getUncompressedLength():-1, 
//					getPatternLength(), 
//					getTransformationSetLength(), 
//					getPattern(), 
//					getTransformations(), 
//					getSuperMTPs()
////					getSuperMTPs() == null?getCoveredSet():null
//					);
			s = String.format("OS(%s,%s,%s)",getPattern(),getTransformations(),getSuperMTPs());
		return s;
	}

	public TreeSet<Transformation> getSuperMTPTransformations()  throws TimeOutException {
		long time = Calendar.getInstance().getTimeInMillis();
		if (!PointSet.NO_TIME_LIMIT && time - PointSet.TIME_AT_START_OF_COMPUTING_HETERO_OS > PointSet.TIME_LIMIT)
			throw new TimeOutException("getSupeMTPTransformations timed out");
		TreeSet<Transformation> superMTPTransformations = new TreeSet<Transformation>();
		if (superMTPs == null)
			return superMTPTransformations;
		for(OccurrenceSet mtp : superMTPs) {
			superMTPTransformations.addAll(mtp.getTransformations());
			superMTPTransformations.addAll(mtp.getSuperMTPTransformations());
		}
		return superMTPTransformations;
	}

	public PointSet getCoveredSet() throws SuperMTPsNotNullException {
		if (getSuperMTPs() != null)
			throw new SuperMTPsNotNullException("superMTPs needs to be null in order to compute covered set. Run PointSet.computeHeterogeneousOccurrenceSets() first on the owning PointSet.");
		if (coveredSet == null) {
			coveredSet = new PointSet();
			coveredSet.addAll(getPattern());
			for(Transformation transformation : getTransformations())
				coveredSet.addAll(transformation.phi(getPattern()));
		}
		return coveredSet;
	}

	public int getCoverage() throws SuperMTPsNotNullException {
		if (getSuperMTPs() != null)
			throw new SuperMTPsNotNullException("superMTPs needs to be null in order to compute coverage. Run PointSet.computeHeterogeneousOccurrenceSets() first on the owning PointSet.");
		PointSet cs = getCoveredSet();
		return cs.size();
	}
	
	public void removeNonCompactOccurrences(double minOccurrenceCompactness, PointSet dataset) throws SuperMTPsNotNullException {
		if (getSuperMTPs() != null)
			throw new SuperMTPsNotNullException("superMTPs needs to be null in order to compute coverage. Run PointSet.computeHeterogeneousOccurrenceSets() first on the owning PointSet.");
		ArrayList<Transformation> transformationsArray = new ArrayList<Transformation>();
		for(Transformation transformation : getTransformations())
			try {
				transformationsArray.add(new Transformation(transformation.toString()));
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			}
		for(Transformation transformation : transformationsArray ) {
			if (transformation.phi(getPattern()).getCompactness(dataset) < minOccurrenceCompactness) {
				removeTransformation(transformation);
			}
		}
	}
	
	public double getMaxCompactness(boolean isMTM, PointSet dataset) throws SuperMTPsNotNullException {
		if (getSuperMTPs() != null)
			throw new SuperMTPsNotNullException("superMTPs needs to be null in order to compute coverage. Run PointSet.computeHeterogeneousOccurrenceSets() first on the owning PointSet.");
		Double maxCompactness = null;
		if (!isMTM)
			maxCompactness = getPattern().getCompactness(dataset);
		for(Transformation transformation : getTransformations()) {
			double thisMaxComp = transformation.phi(getPattern()).getCompactness(dataset);
			if (maxCompactness == null || thisMaxComp > maxCompactness)
				maxCompactness = thisMaxComp;
			if (maxCompactness == 1.0)
				break;
		}
		return maxCompactness;
	}

	public int getPatternEncodingLength() {
		return getPattern().size()*getPattern().getDimensionality();
		//			return getPattern().size()*getDataset().getPointComplexity();
	}

	public int getTransformationSetEncodingLength() throws SuperMTPsNotNullException {
		if (getSuperMTPs() != null)
			throw new SuperMTPsNotNullException("superMTPs needs to be null in order to compute encoding length. Run PointSet.computeHeterogeneousOccurrenceSets() first on the owning PointSet.");
		int el = 0;
		for(Transformation f : getTransformations())
			el += f.getTransformationClass().getSigmaLength();
		return el;
	}

	public long getEncodingLength() throws SuperMTPsNotNullException {
		if (getSuperMTPs() != null)
			throw new SuperMTPsNotNullException("superMTPs needs to be null in order to compute encoding length. Run PointSet.computeHeterogeneousOccurrenceSets() first on the owning PointSet.");
		if (encodingLength != -1) 
			return encodingLength;
		return getPatternEncodingLength() + getTransformationSetEncodingLength();
	}

	public int getDimensionality() {
		return getPattern().getDimensionality();
	}
	
	public int getUncompressedLength() throws SuperMTPsNotNullException {
		return getCoverage()*getDimensionality();
		//			return getCoverage()*getDataset().getPointComplexity();
	}

	public int getPatternLength() {
		return getPattern().getDimensionality() * getPattern().size();
	}

	public int getTransformationSetLength() {
		int transformationSetLength = 0;
		for(Transformation f : getTransformations()) {
			transformationSetLength += f.getSigmaLength();
		}
		return transformationSetLength;
	}

	public double getCompressionFactor() throws SuperMTPsNotNullException {
		return 1.0 * getUncompressedLength() / getEncodingLength();
	}

	public PointFreqSet getPointFreqSet() {
		PointFreqSet pfs = new PointFreqSet();
		TreeSet<Point> patternPoints = getPattern().getPoints();
		for(Point p : patternPoints) {
			pfs.addPoint(p);
			for(Transformation f : getTransformations())
				pfs.addPoint(f.phi(p));
		}
		return pfs;
	}

	public void removeTransformation(Transformation tran) {
		TreeSet<Transformation> newTransformations = new TreeSet<Transformation>();
		for (Transformation thisTran : transformations) {
			if (!tran.equals(thisTran))
				newTransformations.add(thisTran);
		}
		transformations = newTransformations;
		resetProperties();
	}

	public void removeRedundantTransformations(boolean isMTM) {
		//		Remove more complex transformations that map the pattern
		//		onto the same image pattern as less complex transformations
		ArrayList<TransformationPointSetPair> tranImagePatPairs = new ArrayList<TransformationPointSetPair>();
		TreeSet<Transformation> trans = getTransformations();
		PointSet pat = getPattern();
		for(Transformation tran : trans) {
			PointSet imagePattern = tran.phi(pat);
			TransformationPointSetPair tranImagePatPair = new TransformationPointSetPair(tran, imagePattern);
			tranImagePatPairs.add(tranImagePatPair);
		}

		//		Now sort tranImagePatPairs so that they're in increasing order by pattern and then increasing order by 
		//		transformation sigma length

		Collections.sort(tranImagePatPairs, new Comparator<TransformationPointSetPair>() {

			@Override
			public int compare(TransformationPointSetPair o1, TransformationPointSetPair o2) {
				int d = o1.getPointSet().compareTo(o2.getPointSet());
				if (d != 0) return d;
				d = o1.getTransformation().getSigmaLength() - o2.getTransformation().getSigmaLength();
				if (d != 0) return d;
				return o1.getTransformation().compareTo(o2.getTransformation());
			}

		});

		//		Now replace transformations with new transformations consisting of only 
		//		simplest transformation for each distinct image pattern.
		//		Transformations for which image pattern equals object pattern are omitted.

		ArrayList<Transformation> newTrans = new ArrayList<Transformation>();
		PointSet currentPattern = null;
		for(TransformationPointSetPair tranPatPair : tranImagePatPairs) {
			if (!tranPatPair.getPointSet().equals(currentPattern)) {
				currentPattern = tranPatPair.getPointSet();
				//				Do not add transformation if it results in the object pattern for this occurrence set.
				if ( isMTM ||!tranPatPair.getPointSet().equals(getPattern())) {
					newTrans.add(tranPatPair.getTransformation());
					//					System.out.println(tranPatPair);
				}
			}
		}
		Collections.sort(newTrans);
		setTransformations(newTrans);

		//		FOLLOWING BASED ON TEC.removeRedundantTranslators()
		//		FROM omnisia-recursia-rrt-mml-2019 repo.

		PointFreqSet pfs = getPointFreqSet();

		/*
		 * If there are no multipoints in pfs,
		 * then we cannot remove any transformations.
		 */

		if (pfs.getMultiPoints().isEmpty()) return;

		/*
		 * The set of transformations that can be removed
		 * is a subset of those that map the pattern onto
		 * a subset of the multipoints.
		 */

		TreeSet<Transformation> removableTransformations = new TreeSet<Transformation>();

		trans = getTransformations();

		//		Following gives us a PointSet containing the multiPoints
		PointSet multiPointSet = new PointSet();
		TreeSet<PointFreq> multiPointPointFreqs = pfs.getMultiPoints();
		for(PointFreq pf : multiPointPointFreqs) multiPointSet.add(pf.getPoint());

		//		Now we check for each transformation whether the image pattern
		//		is contained within the multipoints. If it is, then the
		//		transformation is added to the list of removable transformations.

		for(Transformation tran : trans) {
			if (multiPointSet.contains(tran.phi(getPattern()))) {
				removableTransformations.add(tran);
			}
		}

		/*
		 * Check to see if all removable transformations can be removed
		 */

		PointFreqSet remTranPFS = new PointFreqSet();

		TreeSet<PointFreq> maxPoints = new TreeSet<PointFreq>();

		boolean allCanBeRemoved = true;

		for(Transformation tran : removableTransformations) {
			TreeSet<Point> patternPoints = getPattern().getPoints();
			for(Point pnt : patternPoints) {
				Point newPoint = tran.phi(pnt);
				PointFreq pf = remTranPFS.addPoint(newPoint,tran);
				if (pf.getFreq() == pfs.getFreq(newPoint)) { 
					//Then we've removed all instances of this point
					allCanBeRemoved = false;
					pf.setMaxPoint(true);
					maxPoints.add(pf);
				}
			}
		}

		if (allCanBeRemoved) {
			for(Transformation tran : removableTransformations)
				transformations.remove(tran);
			return;
		}

		/*
		 * So we can't remove all of the removable transformations.
		 * 
		 * But we now know which transformations are responsible for 
		 * maxPoints in remTranPFS. For each maxPoint, we cannot
		 * remove all of the transformations that map points onto it.
		 * 
		 * We have to find the smallest set of transformations that includes
		 * at least one transformation for each maxPoint.
		 * 
		 * We begin by making a list of <transformation,pointSet> pairs. Each
		 * such pair gives, for each transformation, the set of max points onto which
		 * that transformation maps pattern points.
		 * 
		 * We can adopt a greedy strategy, where we start by choosing
		 * the transformation that has the most max points attached to it.
		 * 
		 * We then remove the covered max points from all the remaining
		 * <transformation,pointSet> pairs and re-sort the <transformation,pointSet> pairs
		 * by pointSet size.
		 * 
		 * Repeat until all max points are covered.
		 * 
		 * This gives us a list of retained removable transformations. The other removable
		 * transformations can be removed.
		 */

		/* First find the set of <transformation,point set> pairs. This will be sorted into
		 * decreasing order of size of point set.
		 */

		TreeSet<TransformationPointSetPair> transformationMaxPointSetPairs = new TreeSet<TransformationPointSetPair>();

		for(PointFreq pf : maxPoints) {
			for(Transformation tran : pf.getTransformations()) {
				TransformationPointSetPair tranMaxPointSetPair = transformationMaxPointSetPairs.floor(new TransformationPointSetPair(tran, new PointSet()));
				if (tranMaxPointSetPair != null && tranMaxPointSetPair.getTransformation().equals(tran)) {
					tranMaxPointSetPair.addPoint(pf.getPoint());
				} else {
					transformationMaxPointSetPairs.add(new TransformationPointSetPair(tran,new PointSet(pf.getPoint())));
				}
			}
		}

		ArrayList<TransformationPointSetPair> tranMaxPointSetPairArray = new ArrayList<TransformationPointSetPair>(transformationMaxPointSetPairs);
		Comparator<TransformationPointSetPair> decreasingSizeComparator = new Comparator<TransformationPointSetPair>() {

			@Override
			public int compare(TransformationPointSetPair o1, TransformationPointSetPair o2) {
				if (o1 == null & o2 == null) return 0;
				if (o1 == null) return -1;
				if (o2 == null) return 1;
				//				Neither o1 nor o2 are null.
				int d = o2.getPointSet().size() - o1.getPointSet().size();
				if (d != 0) return d;
				d = o1.getPointSet().compareTo(o2.getPointSet());
				if (d != 0) return d;
				return o1.getTransformation().compareTo(o2.getTransformation());
			}

		};
		Collections.sort(tranMaxPointSetPairArray, decreasingSizeComparator);

		/*
		 * Now we find the set of retained transformations. The first retained transformation
		 * is the one whose set of max points is the largest - this should be the
		 * first transformationPointSetPair in tranMaxPointSetPairArray.
		 */

		TreeSet<Transformation> retainedTransformations = new TreeSet<Transformation>();

		while(!tranMaxPointSetPairArray.isEmpty()) {
			TransformationPointSetPair firstTranMaxPointSetPair = tranMaxPointSetPairArray.get(0);
			retainedTransformations.add(firstTranMaxPointSetPair.getTransformation());

			for(Point maxPoint : firstTranMaxPointSetPair.getPointSet().getPoints()) {
				for(int i = 1; i < tranMaxPointSetPairArray.size(); i++) {
					tranMaxPointSetPairArray.get(i).getPointSet().remove(maxPoint);
				}
			}

			ArrayList<TransformationPointSetPair> newTranMaxPointSetPairArray = new ArrayList<TransformationPointSetPair>();
			for(int i = 1; i < tranMaxPointSetPairArray.size(); i++)
				if (!tranMaxPointSetPairArray.get(i).getPointSet().isEmpty())
					newTranMaxPointSetPairArray.add(tranMaxPointSetPairArray.get(i));

			tranMaxPointSetPairArray = newTranMaxPointSetPairArray;
			Collections.sort(tranMaxPointSetPairArray, decreasingSizeComparator);
		}

		/*
		 * Now remove all removable transformations apart from retained transformations.
		 */

		ArrayList<Transformation> newTransformations = new ArrayList<Transformation>();
		for(Transformation tran : transformations)
			if (!removableTransformations.contains(tran) || retainedTransformations.contains(tran))
				newTransformations.add(tran);

		setTransformations(newTransformations);

	}

	public static Comparator<OccurrenceSet> DECREASING_PATTERN_SIZE = new Comparator<OccurrenceSet>() {

		@Override
		public int compare(OccurrenceSet o1, OccurrenceSet o2) {
			if (o1 == null && o2 == null) return 0;
			if (o1 == null) return 1;
			if (o2 == null) return -1;
			int d;
			d = o2.getPattern().size()-o1.getPattern().size();
			if (d != 0) return d;
			return o1.getPattern().compareTo(o2.getPattern());
		}
	};
	
	public static Comparator<OccurrenceSet> DECREASING_CF_THEN_COVERAGE_COMPARATOR = new Comparator<OccurrenceSet>() {

		@Override
		public int compare(OccurrenceSet o1, OccurrenceSet o2) {
			if (o1 == null && o2 == null) return 0;
			if (o1 == null) return 1;
			if (o2 == null) return -1;
			int d;
			try {
				d = (int) Math.signum(o2.getCompressionFactor()-o1.getCompressionFactor());
				if (d != 0) return d;
				d = o2.getCoverage() - o1.getCoverage();
				if (d != 0) return d;
			} catch (SuperMTPsNotNullException e) {
				e.printStackTrace();
			}
			d = o2.getPattern().size() - o1.getPattern().size();
			if (d != 0) return d;
			return o1.getPattern().compareTo(o2.getPattern());
		}
	};

	public static Comparator<OccurrenceSet> DECREASING_CF_TIMES_COVERAGE_COMPARATOR = new Comparator<OccurrenceSet>() {

		@Override
		public int compare(OccurrenceSet o1, OccurrenceSet o2) {
			if (o1 == null && o2 == null) return 0;
			if (o1 == null) return 1;
			if (o2 == null) return -1;
			int d;
			try {
				d = (int) Math.signum(o2.getCompressionFactor()*o2.getCoverage()-o1.getCompressionFactor()*o1.getCoverage());
				if (d != 0) return d;
				d = (int) Math.signum(o2.getCompressionFactor() - o1.getCompressionFactor());
				if (d != 0) return d;
				d = o2.getCoverage() - o1.getCoverage();
				if (d != 0) return d;
			} catch (SuperMTPsNotNullException e) {
				e.printStackTrace();
			}
			d = o2.getPattern().size() - o1.getPattern().size();
			if (d != 0) return d;
			return o1.getPattern().compareTo(o2.getPattern());
		}
	};

	public static Comparator<OccurrenceSet> DECREASING_COVERAGE_THEN_CF_COMPARATOR = new Comparator<OccurrenceSet>() {

		@Override
		public int compare(OccurrenceSet o1, OccurrenceSet o2) {
			if (o1 == null && o2 == null) return 0;
			if (o1 == null) return 1;
			if (o2 == null) return -1;
			int d;
			try {
				d = o2.getCoverage() - o1.getCoverage();
				if (d != 0) return d;
				d = (int) Math.signum(o2.getCompressionFactor()-o1.getCompressionFactor());
				if (d != 0) return d;
			} catch (SuperMTPsNotNullException e) {
				e.printStackTrace();
			}
			d = o2.getPattern().size() - o1.getPattern().size();
			if (d != 0) return d;
			return o1.getPattern().compareTo(o2.getPattern());
		}
	};

	public TreeSet<PointSet> getOccurrences() throws SuperMTPsNotNullException {
		if (getSuperMTPs() != null)
			throw new SuperMTPsNotNullException("superMTPs needs to be null in order to compute covered set. Run PointSet.computeHeterogeneousOccurrenceSets() first on the owning PointSet.");
		TreeSet<PointSet> occurrences = new TreeSet<PointSet>();
		for(Transformation transformation : getTransformations()) {
			occurrences.add(transformation.phi(getPattern()));
		}
		return occurrences;
	}
	
	@Override
	public int compareTo(OccurrenceSet o) {
		if (getSuperMTPs() != null || o.getSuperMTPs() != null) {
			System.out.println("ERROR: superMTPs needs to be null in order to compute covered set. Run PointSet.computeHeterogeneousOccurrenceSets() first on the owning PointSet.");
			System.exit(1);
		}
		if (o == null) return 1;
		int d = getPattern().size() - o.getPattern().size();
		if (d != 0) return d;
		d = getTransformations().size() - o.getTransformations().size();
		if (d != 0) return d;
		try {
			TreeSet<PointSet> thisPointSets = getOccurrences();
			TreeSet<PointSet> otherPointSets = o.getOccurrences();
			d = thisPointSets.size() - otherPointSets.size();
			if (d != 0) return d;
			Iterator<PointSet> thisIterator = thisPointSets.iterator();
			Iterator<PointSet> otherIterator = otherPointSets.iterator();
			while(thisIterator.hasNext()) {
				PointSet thisPattern = thisIterator.next();
				PointSet otherPattern = otherIterator.next();
				d = thisPattern.compareTo(otherPattern);
				if (d != 0) 
					return d;
			}
		} catch (SuperMTPsNotNullException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof OccurrenceSet)) return false;
		return compareTo((OccurrenceSet)obj)==0;
	}
	
}
