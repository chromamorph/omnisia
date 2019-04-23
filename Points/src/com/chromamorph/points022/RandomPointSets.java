package com.chromamorph.points022;

import java.io.IOException;

public class RandomPointSets {
	
	public static void CreateRandomPointSet(long maxTime, int maxPitch, long numberOfPoints, String outputFileName) {
		PointSet pointSet = new PointSet();
		for(int i = 0; i < numberOfPoints; i++) {
			Point point;
			do {
				int p = (int)Math.round(Math.random() * maxPitch);
				long t = (long)Math.round(Math.random() * maxTime);
				point = new Point(t,p);
			} while (pointSet.contains(point));
			pointSet.add(point);
		}

		String ofn = outputFileName+(outputFileName.endsWith(".pts")?"":".pts");
		try {
			pointSet.writeToPtsFile(ofn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {

		//Let's suppose that pitch varies from 0 to 100
		//and onset time varies from 0 to 500

		int maxPitch = 35 - 18;
		int maxTime = 1674480 - 1680;
		int numberOfPoints = 4911;


//		for( int numberOfPoints = 100; numberOfPoints <= 2000; numberOfPoints += 100) {
//		for(maxTime = 100; maxTime <= 1000; maxTime += 100) {
//		for(int j = 0; j < 10; j++) {
			PointSet pointSet = new PointSet();
			for(int i = 0; i < numberOfPoints; i++) {
				Point point;
				do {
					int p = (int)(Math.random() * maxPitch);
					long t = (int)(Math.random() * maxTime);
					point = new Point(t,p);
				} while (pointSet.contains(point));
				pointSet.add(point);
			}

			String folderName = "/Users/dave/Documents/Work/Research/workspace/Points/data/Random point sets";
			String fileName = folderName+"/randomPointSet"+numberOfPoints+"-"+maxPitch+"-"+maxTime+"-b.pts";
			try {
				pointSet.writeToPtsFile(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//	}
}
