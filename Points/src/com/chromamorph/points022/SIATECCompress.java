package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.Collections;

public class SIATECCompress implements Encoder {


//	private int totalNumberOfMTPs;
	private int numberOfTranDistMTPs;

//	public void setTotalNumberOfMTPs(int totalNumberOfMTPs) {
//		this.totalNumberOfMTPs = totalNumberOfMTPs; 
//	}
//
//	public int getTotalNumberOfMTPs() {
//		return totalNumberOfMTPs;
//	}

	public void setNumberOfTranDistMTPs(int numberOfTranDistMTPs) {
		this.numberOfTranDistMTPs = numberOfTranDistMTPs;
	}

	public int getNumberOfTranDistMTPs() {
		return numberOfTranDistMTPs;
	}

//	private VectorPointPair[][] computeVectorTable(PointSet points) {
//		System.out.print("computeVectorTable...");
//		TreeSet<Point> pointsTreeSet = points.getPoints();
//		VectorPointPair[][] vectorTable = new VectorPointPair[points.size()][points.size()];
//		int i = 0;
//		for(Point p1 : pointsTreeSet) {
//			int j = 0;
//			for(Point p2 : pointsTreeSet) {
//				VectorPointPair vp = new VectorPointPair(p1,p2,i);
//				vectorTable[i][j] = vp;
//				j++;
//			}
//			i++;
//		}
//
//		System.out.println("completed");
//		return vectorTable;
//	}

//	private ArrayList<MtpCisPair> computeMtpCisPairs(VectorPointPair[][] vectorTable, int minMtpSize) {
//		TreeSet<VectorPointPair> sortedSIAVectorTable = new TreeSet<VectorPointPair>();
//
//		for(int i = 0; i < vectorTable.length; i++)
//			for(int j = i+1; j < vectorTable.length; j++)
//				sortedSIAVectorTable.add(vectorTable[i][j]);
//
//		ArrayList<PointSet> MTPs = new ArrayList<PointSet>();
//		ArrayList<ArrayList<Integer>> CISs = new ArrayList<ArrayList<Integer>>();
//		VectorPointPair firstVP = sortedSIAVectorTable.first(); 
//		Vector v = firstVP.getVector();
//		PointSet mtp = new PointSet();
//		ArrayList<Integer> cis = new ArrayList<Integer>();
//		mtp.add(firstVP.getPoint());
//		cis.add(firstVP.getIndex());
//		NavigableSet<VectorPointPair> rest = sortedSIAVectorTable.tailSet(firstVP, false); 
//		for(VectorPointPair vp : rest) {
//			if (vp.getVector().equals(v)) {
//				mtp.add(vp.getPoint());
//				cis.add(vp.getIndex());
//			} else {
//				if (mtp.size() >= minMtpSize) {
//					MTPs.add(mtp);
//					CISs.add(cis);
//				}
//				mtp = new PointSet();
//				cis = new ArrayList<Integer>();
//				v = vp.getVector();
//				mtp.add(vp.getPoint());
//				cis.add(vp.getIndex());
//			}
//		}		
//
//		if (mtp.size() >= minMtpSize) {
//			MTPs.add(mtp);
//			CISs.add(cis);
//		}
//
//		ArrayList<MtpCisPair> mtpCisPairs = new ArrayList<MtpCisPair>();
//
//		for(int i = 0; i < MTPs.size(); i++) {
//			mtpCisPairs.add(new MtpCisPair(MTPs.get(i),CISs.get(i)));
//		}
//
//		return mtpCisPairs;
//
//	}

	//	private ArrayList<MtpCisPair> removeTranslationallyEquivalentMtps(ArrayList<MtpCisPair> mtpCisPairs) {
	//		System.out.print("removeTranslationallyEquivalentMtps");
	//		//Put the MTPs into order of size - then only have to compare each MTP 
	//		//with other MTPs of the same size when removing translationally 
	//		//equivalent MTPs.
	//
	//		PointSetSizeComparator pointSetSizeComparator = new PointSetSizeComparator();
	//		Collections.sort(mtpCisPairs, pointSetSizeComparator);
	//
	//		//Remove MTPs that are translationally equivalent to other MTPs
	//		//so that we don't have to compute the same TEC more than once.
	//
	//		ArrayList<MtpCisPair> newMtpCisPairList = new ArrayList<MtpCisPair>();
	//
	//		MtpCisPair s1, s2;
	//		int n = mtpCisPairs.size();
	//		for(int i = 0; i < n; i++) {
	//			if (i%500 == 0 && i != 0) {
	//				System.out.print(".");
	//				System.out.flush();
	//			}
	//			if (i%25000 == 0) {
	//				System.out.println();
	//				System.out.flush();
	//			}
	//			s1 = mtpCisPairs.get(i);
	//			int x = s1.getMtp().size();
	//			boolean found = false;
	//			for(int j = i + 1; !found && j < n && (s2 = mtpCisPairs.get(j)).getMtp().size() == x; j++) {
	//				if (s1.getMtp().translationallyEquivalentTo(s2.getMtp()))
	//					found = true;
	//			}
	//			if (!found)
	//				newMtpCisPairList.add(s1);
	//		}
	//
	//		System.out.println("\ncompleted: "+newMtpCisPairList.size()+" MTPs after removing translational equivalents");
	//		setNumberOfTranDistMTPs(newMtpCisPairList.size());
	//		
	//		return newMtpCisPairList;
	//	}


	private void addDualTECs(ArrayList<TEC> tecs, int minMtpSize, int maxMtpSize, int minTranslatorSetSize) {
		System.out.print("addDualTECs...");
		ArrayList<TEC> dualList = new ArrayList<TEC>();
		for(int i = 0; i < tecs.size(); i++ ) {
			TEC tec = tecs.get(i);
			if (tec.getPattern().size() >= minMtpSize && tec.getTranslatorSetSize() >= minTranslatorSetSize && (maxMtpSize==0 || tec.getPattern().size() <= maxMtpSize))
				dualList.add(tec.getDual());
			if (i%500 == 0 && i != 0) {
				System.out.print(".");
				System.out.flush();
			}
			if (i%25000 == 0) {
				System.out.println();
				System.out.flush();
			}
		}
		tecs.addAll(dualList);
		System.out.println("\ncompleted");
	}

	private void sortTECsByQuality(PointSet points, ArrayList<TEC> tecs) {
		System.out.print("sortTECsByQuality...");
		TECQualityComparator tecQualityComparator = new TECQualityComparator();
		Collections.sort(tecs,tecQualityComparator);
		System.out.println("completed");
	}

	private SIATECCompressEncoding computeEncoding(PointSet points, ArrayList<TEC> tecs) {
		System.out.print("computeEncoding...");
		PointSet coveredPoints = new PointSet();

		ArrayList<TEC> encodingTecs = new ArrayList<TEC>();

		for(TEC tec : tecs) {
			PointSet tecCoveredPoints = tec.getCoveredPoints();
			if (tecCoveredPoints.diff(coveredPoints).size() >= tec.getPatternSize()+tec.getTranslatorSetSize()-1) {
				encodingTecs.add(tec);
				coveredPoints.addAll(tecCoveredPoints);
				if (coveredPoints.size() == points.size())
					break;
			}
		}

		PointSet residualPointSet = points.diff(coveredPoints);
		SIATECCompressEncoding encoding = new SIATECCompressEncoding(encodingTecs,residualPointSet, points);
		System.out.println("completed");
		return encoding;
	}

	public static void removeRedundantTranslators(ArrayList<TEC> tecs) {
		System.out.print("\nremoveRedundantTranslators\n");
		for(int i = 0; i < tecs.size(); i++) {
			if (i%500 == 0 && i != 0) {
				System.out.print(".");
				System.out.flush();
			}
			if (i%5000 == 0 && i != 0) {
				System.out.println();
				System.out.flush();
			}
			TEC tec = tecs.get(i);
			tec.removeRedundantTranslators();
		}
		System.out.println("\ncompleted");
	}

	/*
	 * Replaces each pattern in each TEC with a SIATECCompressEncoding
	 * that compresses the pattern.
	 */
	//	private void compressPatterns(ArrayList<TEC> tecs) {
	//		for(TEC tec : tecs)
	//			tec.setPattern((SIATECCompressEncoding)encode(tec.getPattern()));
	//	}

	@Override
	public Encoding encode(final PointSet points) {
		return encode(points, false, 0.0, 0, false, 0, 0,1);
	}

	public Encoding encode(PointSet dataset, double a, int b, int r) {
		return encode(dataset, (a>0?true:false), a, b, (r>0?true:false), r, 0,1);
	}

	public Encoding encode(final PointSet points, 
			boolean withCompactnessTrawling, 
			double a, 
			int b,
			boolean forRSuperdiagonals,
			int r) {
		return encode(points, 
			withCompactnessTrawling, 
			a, 
			b,
			forRSuperdiagonals,
			r,
			0,
			1);
	}

	public Encoding encode(final PointSet points, 
			boolean withCompactnessTrawling, 
			double a, 
			int b,
			boolean forRSuperdiagonals,
			int r,
			int minMtpSize,
			int minTranslatorSetSize) {
		return encode(points, 
			withCompactnessTrawling, 
			a, 
			b,
			forRSuperdiagonals,
			r,
			minMtpSize,
			0,
			minTranslatorSetSize);
	}
	
	public Encoding encode(final PointSet points, 
			boolean withCompactnessTrawling, 
			double a, 
			int b,
			boolean forRSuperdiagonals,
			int r,
			int minMtpSize,
			int maxPatternSize,
			int minTranslatorSetSize
			) {
		return encode(points, 
			withCompactnessTrawling, 
			a, 
			b,
			forRSuperdiagonals,
			r,
			minMtpSize,
			maxPatternSize,
			minTranslatorSetSize,
			true);
	}

	public Encoding encode(final PointSet points, 
			boolean withCompactnessTrawling, 
			double a, 
			int b,
			boolean forRSuperdiagonals,
			int r,
			int minMtpSize,
			int maxPatternSize,
			int minTranslatorSetSize,
			boolean removeRedundantTranslators) {
		long t1 = System.currentTimeMillis();
		String thisEncoderName = this.getClass().toString();
		System.out.println(thisEncoderName);

		if (points.size() == 1) {
			long t2 = System.currentTimeMillis();
			System.out.println((t2-t1)+" ms taken to analyse "+points.size()+" points\n");
			SIATECCompressEncoding encoding = new SIATECCompressEncoding(points,points);

			encoding.setRunningTime(t2-t1);
			encoding.setNumberOfTranDistMTPs(getNumberOfTranDistMTPs());
			encoding.setTotalNumberOfMTPs(SIA.TOTAL_NUMBER_OF_MTPs);

			System.out.println(encoding);
			System.out.println("Encoding length: "+encoding.getEncodingLength());
			System.out.println("Compression ratio: "+String.format("%.2f",encoding.getCompressionRatio()));

			return encoding;
		}

		ArrayList<TEC> tecs = null;

		VectorPointPair[][] vectorTable = SIA.computeVectorTable(points);
		ArrayList<MtpCisPair> mtpCisPairs = SIA.run(
				points, 
				vectorTable, 
				forRSuperdiagonals, r, 
				withCompactnessTrawling, a, b, 
				null,
				true, //remove tran equiv mtps
				false,
				minMtpSize,
				maxPatternSize
				);
		
		
		tecs = SIATEC.computeMtpTecs(points,vectorTable,mtpCisPairs, minMtpSize, maxPatternSize, minTranslatorSetSize);
		addDualTECs(tecs, minMtpSize, maxPatternSize, minTranslatorSetSize);
		if (removeRedundantTranslators)
			removeRedundantTranslators(tecs);
		//		compressPatterns(tecs);
		setNumberOfTranDistMTPs(mtpCisPairs.size());
		sortTECsByQuality(points,tecs);
		SIATECCompressEncoding encoding = computeEncoding(points,tecs);

		long t2 = System.currentTimeMillis();
		encoding.setRunningTime(t2-t1);
		encoding.setNumberOfTranDistMTPs(getNumberOfTranDistMTPs());
		encoding.setTotalNumberOfMTPs(SIA.TOTAL_NUMBER_OF_MTPs);
		
		
		return encoding;
	}



	
}