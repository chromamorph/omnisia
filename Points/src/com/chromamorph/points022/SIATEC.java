package com.chromamorph.points022;

import java.util.ArrayList;

public class SIATEC {
	
	public static ArrayList<TEC> computeMtpTecs(
			PointSet points, 
			VectorPointPair[][] vectorTable, 
			ArrayList<MtpCisPair> mtpCisPairs, 
			int minMtpSize, 
			int minTranslatorSetSize) {
		return computeMtpTecs(points, 
			vectorTable, 
			mtpCisPairs, 
			minMtpSize,
			0,
			minTranslatorSetSize);
	}
	public static ArrayList<TEC> computeMtpTecs(
			PointSet points, 
			VectorPointPair[][] vectorTable, 
			ArrayList<MtpCisPair> mtpCisPairs, 
			int minMtpSize,
			int maxMtpSize,
			int minTranslatorSetSize) {
		System.out.println("\ncomputeMtpTecs");
		if (points.size() < 2) //There are no MTPs 
			return new ArrayList<TEC>();
		ArrayList<TEC> tecs = new ArrayList<TEC>();
		for(int i = 0; i < mtpCisPairs.size(); i++) {
			if (i%500 == 0 && i != 0) {
				System.out.print(".");
				System.out.flush();
			}
			if (i%5000 == 0 && i != 0) {
				System.out.println();
				System.out.flush();
			}
			TEC tec = computeTEC(mtpCisPairs.get(i), points, vectorTable);
			if (tec.getPatternSize() >= minMtpSize && tec.getTranslatorSetSize() >= minTranslatorSetSize && (maxMtpSize==0 || tec.getPatternSize() <= maxMtpSize))
				tecs.add(tec);
		}

		System.out.println("\nDONE");
		return tecs;
	}
	
	public static TEC computeTEC(MtpCisPair mtpCisPair, PointSet points, VectorPointPair[][] vectorTable) {
		VectorSet translators = new VectorSet();
		Integer[] cols = new Integer[mtpCisPair.getCis().size()];
		mtpCisPair.getCis().toArray(cols);
		int patSize = mtpCisPair.getCis().size();
		int[] rows = new int[patSize];
		rows[0] = 0;
		while(rows[0] <= points.size() - patSize) { //For each vector in the first pattern point column
			for(int j = 1; j < patSize; j++) rows[j] = rows[0]+j; //Initialize the indices for the other pattern point columns
			Vector v0 = vectorTable[cols[0]][rows[0]].getVector();
			boolean found = false;
			for(int col = 1; col < patSize; col++) { //For each pattern point
				while(rows[col] < points.size() && vectorTable[cols[col]][rows[col]].getVector().compareTo(v0) < 0) {
					rows[col]++; //Increment CI for this pattern point until >= v0
				}
				if (rows[col] >= points.size() || !v0.equals(vectorTable[cols[col]][rows[col]].getVector())) break; //If not equal then break
				if (col == patSize-1) found = true;
			}
			if (found || patSize == 1) translators.add(v0);
			rows[0]++;
		}
		return new TEC(mtpCisPair.getMtp(),translators,points);
	}

}
