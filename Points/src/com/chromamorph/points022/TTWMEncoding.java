package com.chromamorph.points022;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.TreeSet;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class TTWMEncoding extends Encoding {

	static class ListOfOnsets {
		ArrayList<Long> onsetList = new ArrayList<Long>();
		
		public void add(Long onset) {onsetList.add(onset);}
		public Long get(int i) {return onsetList.get(i);}
		public int size() {return onsetList.size();}
		public String toString() {
			return onsetList.toString();
		}
	}
	
	static class Triple implements Comparable<Triple> {
		int pitchInt;
		int startPitch;
		long startTime;
		long endTime;
		TreeSet<Triple> nextTriples = null;
		
		public Triple(int pitchInt, int startPitch, long startTime, long endTime) {
			this.pitchInt = pitchInt;
			this.startPitch = startPitch;
			this.startTime = startTime;
			this.endTime = endTime;
		}
		
		public void addNextTriple(Triple nextTriple) {
			if (nextTriples == null) nextTriples = new TreeSet<Triple>();
			nextTriples.add(nextTriple);
		}
		
		@Override
		public int compareTo(Triple o) {
			if (o == null) return 1;
			
			if (startTime < o.startTime) return -1;
			if (startTime > o.startTime) return 1;
			
			if (endTime < o.endTime) return -1;
			if (endTime > o.endTime) return 1;

			if (startPitch < o.startPitch) return -1;
			if (startPitch > o.startPitch) return 1;

			if (pitchInt < o.pitchInt) return -1;
			if (pitchInt > o.pitchInt) return 1;
			
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj instanceof Triple)) return false;
			return compareTo((Triple)obj) == 0;
		}
		
		@Override
		public String toString() {
			return "("+pitchInt+","+startPitch+","+startTime+","+endTime+","+nextTriples+")";
		}
		
	}
	
	static class ListOfTriples {
		TreeSet<Triple> triples = new TreeSet<Triple>();
		
		public void add(Triple triple) {
			triples.add(triple);
		}
		
		@Override
		public String toString() {
			return triples.toString();
		}
	}
	
	public TTWMEncoding(
			String inputFilePathName, 
			String outputDirectoryPathName,
			boolean diatonicPitch
			) throws FileNotFoundException, MissingTieStartNoteException {
		super(
				new PointSet(inputFilePathName),
				inputFilePathName,
				outputDirectoryPathName,
				diatonicPitch,
				true, //withoutChannel10 assumed to be true
				"TTWM",
				0, //topNPatterns - not supported
				false, //forMirex - not supported
				false, //segmentMode - not supported
				false, //bbMode - not supported
				null //outputFilePath - not supported
				);
		
		System.out.println("dataset = "+getDataset());
		long startTime = System.currentTimeMillis();
		
		//////// Compute transposed time-warped melodies
		
		//Compute array of pitch-indexed onset lists
		int maxPitch = getDataset().getMaxY();
		System.out.println("maxPitch = "+maxPitch);
		int minPitch = getDataset().getMinY();
		System.out.println("minPitch = "+minPitch);
		TreeSet<Integer> usedPitches = new TreeSet<Integer>();
		ListOfOnsets[] pitchIndexedOnsetLists = new ListOfOnsets[maxPitch-minPitch+1];
		for(int i = 0; i < pitchIndexedOnsetLists.length; i++) pitchIndexedOnsetLists[i] = new ListOfOnsets(); 
		TreeSet<Point> points = getDataset().getPoints();
		System.out.println("points = "+points);
		for(Point p : points) {
			pitchIndexedOnsetLists[p.getY()-minPitch].add(p.getX());
			usedPitches.add(p.getY());
		}
		System.out.println("usedPitches = "+usedPitches);
		System.out.println("pitchIndexedOnsetLists:");
		for(int i = 0; i < pitchIndexedOnsetLists.length; i++) {
			System.out.println(i + "\t"+pitchIndexedOnsetLists[i]);
		}

		//Compute table of interval-indexed triples
		int maxInterval = maxPitch - minPitch;
		
		ListOfTriples[] intIndexedListsOfTriples = new ListOfTriples[maxInterval+1];
		for(int i = 0; i < intIndexedListsOfTriples.length; i++)
			intIndexedListsOfTriples[i] = new ListOfTriples();
		
		TreeSet<Integer> usedIntervals = new TreeSet<Integer>();
		Integer[] usedPitchArray = new Integer[usedPitches.size()];
		usedPitches.toArray(usedPitchArray);
		for(int i = 0; i < usedPitchArray.length; i++)
			for(int j = i+1; j < usedPitchArray.length; j++) {
				int interval = usedPitchArray[j] - usedPitchArray[i];
				usedIntervals.add(interval);
				ListOfTriples lot = intIndexedListsOfTriples[interval];
				ListOfOnsets startOnsetList = pitchIndexedOnsetLists[usedPitchArray[i]-minPitch];
				ListOfOnsets endOnsetList = pitchIndexedOnsetLists[usedPitchArray[j]-minPitch];
				for(long startOnset : startOnsetList.onsetList)
					for(long endOnset : endOnsetList.onsetList) {
						int startPitch = usedPitchArray[i];
						lot.add(new Triple(interval, startPitch, startOnset, endOnset));
					}
			}
		
		for(ListOfTriples lot : intIndexedListsOfTriples)
			System.out.println(lot);
		
		////////
		
		long endTime = System.currentTimeMillis();
		System.out.println("Running time: "+(endTime-startTime)+"ms");

		setRunningTime(endTime-startTime);
		setTECs(new ArrayList<TEC>());
		
		//////// Compute TTWM pairs as TECs and add to encoding
		
		////////
		
//		writeToFile();
		
		
		
	}
	
	public static void main(String[] args) {
		try {
			TTWMEncoding e = new TTWMEncoding(
//					"data/TTWM-examples/Two-triangles.pts",
					"data/Wtcii01a-b.pts",
					null,
					false
					);
		} catch (FileNotFoundException | MissingTieStartNoteException e) {
			e.printStackTrace();
		}
	}
}
