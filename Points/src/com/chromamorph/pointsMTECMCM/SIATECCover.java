package com.chromamorph.pointsMTECMCM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;

public class SIATECCover implements Codec {

	private double minimumCompactness = 0.5;
	private int minimumPatternSize = 3;
	private double minimumTemporalDensity = 0.0;
	private double minimumTECOverlap = 0.1;

	class VectorPointPair implements Comparable<VectorPointPair>{
		Vector vector;
		Point originPoint;
		Integer columnIndex;

		VectorPointPair(Point p1, Point p2, int columnIndex) {
			vector = new Vector(p1,p2);
			originPoint = p1;
			this.columnIndex = columnIndex;
		}

		@Override
		public int compareTo(VectorPointPair vp) {
			if (vp == null) return 1;
			if (vp.vector == null) return 1;
			if (vp.originPoint == null) return 1;
			int d = vector.compareTo(vp.vector);
			if (d != 0) return d;
			return (originPoint.compareTo(vp.originPoint));
		}

		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof VectorPointPair)) return false;
			return (compareTo((VectorPointPair)o) == 0);
		}
	}

	static ArrayList<Integer> toArrayList(int[] intArray) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for(int i : intArray) a.add(i);
		return a;
	}

	static ArrayList<Integer> toArrayList(Integer[] integerArray) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for(int i : integerArray) a.add(i);
		return a;
	}

	@Override
	public void encode(final PointSet points) {
		long t1 = System.currentTimeMillis();
		String thisEncoderName = this.getClass().toString();
		System.out.println(thisEncoderName + "\n=====================================\n");

		//Compute SIATEC vector table and sorted SIA vector table
		VectorPointPair[][] vectorTable = new VectorPointPair[points.size()][points.size()];
		TreeSet<VectorPointPair> sortedSIAVectorTable = new TreeSet<VectorPointPair>();
		Point[] pointsArray = new Point[points.size()];
		int k = 0;
		for(Point point : points.getPoints()) {
			pointsArray[k] = point;
			k++;
		}
		int n = points.size();
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				VectorPointPair vp = new VectorPointPair(pointsArray[i],pointsArray[j],i);
				vectorTable[i][j] = vp;
				if (j > i) sortedSIAVectorTable.add(vp);
			}
		}
		//////////////////////////////

		//Compute MTPs and column index sets
		ArrayList<PointSet> MTPs = new ArrayList<PointSet>();
		ArrayList<ArrayList<Integer>> CISs = new ArrayList<ArrayList<Integer>>();
		VectorPointPair firstVP = sortedSIAVectorTable.first(); 
		Vector v = firstVP.vector;
		PointSet mtp = new PointSet();
		ArrayList<Integer> cis = new ArrayList<Integer>();
		mtp.add(firstVP.originPoint);
		cis.add(firstVP.columnIndex);
		NavigableSet<VectorPointPair> rest = sortedSIAVectorTable.tailSet(firstVP, false); 
		for(VectorPointPair vp : rest) {
			if (vp.vector.equals(v)) {
				mtp.add(vp.originPoint);
				cis.add(vp.columnIndex);
			} else {
				MTPs.add(mtp);
				CISs.add(cis);
				mtp = new PointSet();
				cis = new ArrayList<Integer>();
				v = vp.vector;
				mtp.add(vp.originPoint);
				cis.add(vp.columnIndex);
			}
		}		

		System.out.println(MTPs.size()+" MTPs found");

		//////////////////////////////
		
		//Now compute intersections of pairs of MTPs
		
		ArrayList<PointSet> MTECs = new ArrayList<PointSet>();
		ArrayList<ArrayList<Integer>> MTEC_CISs = new ArrayList<ArrayList<Integer>>();
				
		for(int i1 = 0; i1 < MTPs.size()-1; i1++)
			for(int i2 = i1+1; i2 < MTPs.size(); i2++) {
				PointSet mtp1 = MTPs.get(i1);
				ArrayList<Integer> cis1 = CISs.get(i1);
				PointSet mtp2 = MTPs.get(i2);
				ArrayList<Integer> cis2 = CISs.get(i2);
				PointSet mtec = new PointSet();
				ArrayList<Integer> mtecCis = new ArrayList<Integer>();
				PointSet mtpTest, mtpOther;
				if (mtp1.size() < mtp2.size()) {
					mtpTest = mtp1;
					mtpOther = mtp2;
				} else {
					mtpOther = mtp1;
					mtpTest= mtp2;
				}
				ArrayList<Integer> cisTest = (cis1.size() < cis2.size())? cis1 : cis2;
				for (int l = 0; l < mtpTest.size(); l++) {
					if (mtpOther.contains(mtpTest.get(l))) {
						mtec.add(mtpTest.get(l));
						mtecCis.add(cisTest.get(l));
					}
				}
				if (!mtec.isEmpty()) {
					mtec.setIsMTEC(true);
					MTECs.add(mtec);
					MTEC_CISs.add(mtecCis);
				}
			}
		
		System.out.println(MTECs.size()+" MTECs found");
		
//		MTPs.addAll(MTECs);
//		CISs.addAll(MTEC_CISs);
		
		//////////////////////////////
		
		//Make list of PointSet-CIS pairs for MTPs

		ArrayList<TEC> mtpCISPairs = new ArrayList<TEC>();
		int m = MTPs.size();
		
		System.out.println("m is "+m+"; CISs size is "+CISs.size());
		
		for(int i = 0; i < m; i++) {
			mtpCISPairs.add(new TEC(MTPs.get(i),CISs.get(i), points));
		}

		//////////////////////////////

		//Put the MTPs into order of size - then only have to compare each MTP 
		//with other MTPs of the same size when removing translationally 
		//equivalent MTPs.

		class PointSetSizeComparator implements Comparator<TEC> {

			@Override
			public int compare(TEC pc1, TEC pc2) {
				PointSet s1 = pc1.getPointSet();
				PointSet s2 = pc2.getPointSet();
				if (s2 == null) return -1;
				if (s1 == null) return 1;
				int d = s2.size() - s1.size();
				if (d != 0) return d;
				if (!pc1.isMTEC() && pc2.isMTEC()) return -1;
				if (pc1.isMTEC() && !pc2.isMTEC()) return 1;
				return d;
			}

		}

		PointSetSizeComparator pointSetSizeComparator = new PointSetSizeComparator();
		Collections.sort(mtpCISPairs, pointSetSizeComparator);

		///////////////////////////////

		//Remove MTPs that are translationally equivalent to other MTPs
		//so that we don't have to compute the same TEC more than once.

		ArrayList<TEC> newTECList = new ArrayList<TEC>();
		TEC s1, s2;
		for(int i = 0; i < m; i++) {
			s1 = mtpCISPairs.get(i);
			int x = s1.getPointSet().size();
			boolean found = false;
			for(int j = i + 1; !found && j < m && (s2 = mtpCISPairs.get(j)).getPointSet().size() == x; j++) {
				if (s1.getPointSet().translationallyEquivalentTo(s2.getPointSet()))
					found = true;
			}
			if (!found)
				newTECList.add(s1);
		}

		System.out.println(newTECList.size()+" MTPs after removing translational equivalents");
		
		int numberOfMTECs = 0;
		for(TEC tec : newTECList)
			if (tec.isMTEC())
				numberOfMTECs++;
		
		System.out.println(numberOfMTECs+" MTECs that are not translationally equivalent to MTPs");
		///////////////////////////////

		//Find TECs for MTPs

		m = newTECList.size();
		for(int i = 0; i < m; i++) {
			s1 = newTECList.get(i);
			Integer[] cols = new Integer[s1.getCIS().size()];
			s1.getCIS().toArray(cols);
			int patSize = s1.getCIS().size();
			int[] rows = new int[patSize];
			rows[0] = 0;
			while(rows[0] <= n - patSize) { //For each vector in the first pattern point column
				for(int j = 1; j < patSize; j++) rows[j] = rows[0]+j; //Initialize the indices for the other pattern point columns
				Vector v0 = vectorTable[cols[0]][rows[0]].vector;
				boolean found = false;
				for(int col = 1; col < patSize; col++) { //For each pattern point
					while(rows[col] < n && vectorTable[cols[col]][rows[col]].vector.compareTo(v0) < 0) rows[col]++; //Increment CI for this pattern point until >= v0
					if (rows[col] >= n || !v0.equals(vectorTable[cols[col]][rows[col]].vector)) break; //If not equal then break
					if (col == patSize-1) found = true;
				}
				if (found) s1.getTranslators().add(v0);
				rows[0]++;
			}
		}

		///////////////////////////////

		//Sort TECs into decreasing order of quality

		class TECQualityComparator implements Comparator<TEC> {

			@Override
			public int compare(TEC tec1, TEC tec2) {
				if (tec1 == null && tec2 == null) return 0;
				if (tec1 == null) return 1;
				if (tec2 == null) return -1;
				int d;
				d = (int)Math.signum(tec2.getCompressionRatio() - tec1.getCompressionRatio());
				if (d != 0) return d;
				d = tec2.getCoverage()-tec1.getCoverage();
				if (d != 0) return d;
				d = (int)Math.signum(tec2.getCompactness()-tec1.getCompactness());
				if (d != 0) return d;
				d = tec2.getPatternSize() - tec1.getPatternSize();
				if (d != 0) return d;
				d = tec1.getBBArea() - tec2.getBBArea();
				if (d != 0) return d;
				if (!tec1.isMTEC() && tec2.isMTEC()) return -1;
				if (tec1.isMTEC() && !tec2.isMTEC()) return 1;
				if (!tec1.isDual() && tec2.isDual()) return -1;
				if (tec1.isDual() && !tec2.isDual()) return 1;
				return 0;
			}

		}

		TECQualityComparator tecQualityComparator = new TECQualityComparator();
		TreeSet<TEC> tecs = new TreeSet<TEC>(tecQualityComparator);

		for(TEC tec : newTECList) {
			TEC dual = tec.getDual();
			if (dual.getCompactness() >= minimumCompactness &&
				dual.getPattern().size() >= minimumPatternSize &&
				dual.getPattern().getTemporalDensity() >= minimumTemporalDensity)
				tecs.add(dual);
			if (tec.getCompactness() >= minimumCompactness &&
				tec.getPattern().size() >= minimumPatternSize &&
				tec.getPattern().getTemporalDensity() >= minimumTemporalDensity)
				tecs.add(tec);
		}

		System.out.println(tecs.size()+" TECs satisfy following conditions:");
		System.out.println("   Compactness >= "+minimumCompactness);
		System.out.println("   Pattern size >= "+minimumPatternSize);
		System.out.println("   Temporal density >= "+minimumTemporalDensity);
		
		////////////////////////////////////////////

		//Find set of TECs that covers whole dataset
		
		System.out.println("Finding TECs that cover this dataset");
		PointSet coveredSet = new PointSet();		
		ArrayList<TEC> newTECs = new ArrayList<TEC>();
		
		for(TEC tec : tecs) {
			//Find covered set for this tec
			PointSet tecCoveredSet = tec.getCoveredPoints();
			if (tecCoveredSet.diff(coveredSet).size() > minimumTECOverlap * tecCoveredSet.size()) {
				newTECs.add(tec);
				coveredSet.addAll(tecCoveredSet);
				if (coveredSet.size() == points.size())
					break;
			}
		}
		
		System.out.println("Found set of "+newTECs.size() + " TECs that cover "+coveredSet.size()+" out of "+points.size()+" points in the dataset");
		
		////////////////////////////////////////////
		
		//Draw TECs

		points.draw(newTECs);

		////////////////////////////////////////////

		long t2 = System.currentTimeMillis();
		System.out.println("\n"+(t2-t1)+" ms taken to analyse "+points.size()+" points");
	}


}
