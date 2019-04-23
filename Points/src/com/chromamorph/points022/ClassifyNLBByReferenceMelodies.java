package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Classifies annotated corpus of NLB by reference melodies.
 * 
 * Places each melody in the class of the nearest reference melody.
 * 
 * @author David Meredith
 *
 */

class SongFamilyPair implements Comparable<SongFamilyPair>{
	private String song, family, refSong;
	private Double distance = null;
	
	SongFamilyPair(String song, String family, String refSong, Double distance) {
		this.song = song;
		this.family = family;
		this.refSong = refSong;
		this.distance = distance;
	}
	
	public String getSong() {return song;}
	public String getFamily() {return family;}
	public double getDistance() {return distance;}
	public String getReferenceSong() {return refSong;}
	
	public String toString() {
		return song+"\t"+family+" ("+refSong+") "+(distance==null?"":"\t"+String.format("%.4f",distance));
	}

	@Override
	public int compareTo(SongFamilyPair sfp) {
		if (sfp == null) return 1;
		int d = getSong().compareTo(sfp.getSong());
		if (d != 0) return d;
		return getFamily().compareTo(sfp.getFamily());
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof SongFamilyPair)) return false;
		return compareTo((SongFamilyPair)obj) == 0;
	}
}

class Distance implements Comparable<Distance> {
	private String song1, song2;
	private double distance;
	
	public String getSong1() {return song1;}
	public String getSong2() {return song2;}
	public double getDistance() {return distance;}
	
	Distance(String song1, String song2, double distance) {
		this.song1 = song1;
		this.song2 = song2;
		this.distance = distance;
	}
	
	public String toString() {
		return "Distance(song1("+getSong1()+"),song2("+getSong2()+",distance("+String.format("%.4f",getDistance())+"))";
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Distance)) return false;
		return compareTo((Distance)obj) == 0;
	}
	
	public int compareTo(Distance dn) {
		if (dn == null) return 1;
		double d = getDistance() - dn.getDistance();
		if (d != 0.0) return (int)Math.signum(d);
		int x = getSong1().compareTo(dn.getSong1());
		if (x != 0) return x;
		return getSong2().compareTo(dn.getSong2());		
	}
}

public class ClassifyNLBByReferenceMelodies {
	
	public static void main(String[] args) throws Exception {
		
		//Load reference melody info
		TreeSet<SongFamilyPair> referenceSongFamilies = new TreeSet<SongFamilyPair>();
		
		BufferedReader br = new BufferedReader(new FileReader("/Users/dave/Documents/Work/Research/workspace/Points/data/nlb_datasets/referencemelodies.txt"));
		String l = br.readLine();
		while (l != null) {
			int tabIndex = l.indexOf('\t');
			String song = l.substring(0,tabIndex).trim();
			String family = l.substring(tabIndex+1).trim();
			referenceSongFamilies.add(new SongFamilyPair(song, family, song, null));
			l = br.readLine();
		}
		br.close();
		
		System.out.println("\nREFERENCE SONG FAMILIES\n=======================");
		int i = 0;
		for(SongFamilyPair sfp : referenceSongFamilies)
			System.out.println(++i+". "+sfp);
		
		///////////////////////////////
		
		//Make a list of the reference songs
		
		TreeSet<String> referenceSongs = new TreeSet<String>();
		for(SongFamilyPair rmi : referenceSongFamilies)
			referenceSongs.add(rmi.getSong());
		
		System.out.println("\nREFERENCE SONGS\n===============");
		i = 0;
		for(String song : referenceSongs)
			System.out.println(++i+". "+song);
		
		///////////////////////////////
		
		//Load distances between annotated corpus melodies and reference melodies
		//Store in sorted set so that then can just take first occurring distance for each melody
		
		TreeSet<Distance> allDistances = new TreeSet<Distance>();
		
		br = new BufferedReader(new FileReader("/Users/dave/Documents/Work/Research/workspace/Points/output/points017/nlb-analysis/nlbncd.txt"));
		br.readLine(); //Ignore column titles
		l = br.readLine();
		while (l != null) {
			int tabIndex = l.indexOf('\t');
			int lastTabIndex = l.lastIndexOf('\t');
			String song1 = l.substring(0,tabIndex).trim();
			String song2 = l.substring(tabIndex+1,lastTabIndex).trim();
			double distance = Double.parseDouble(l.substring(lastTabIndex+1));
			if (referenceSongs.contains(song1) || referenceSongs.contains(song2))
				allDistances.add(new Distance(song1, song2, distance));
			l = br.readLine();
		}
		br.close();
		
		System.out.println("\nALL DISTANCES\n=============");
		i = 0;
		for(Distance dn : allDistances)
			System.out.println(++i+". "+dn);
		
		///////////////////////////////

		//Load ground truth

		TreeSet<SongFamilyPair> groundTruth = new TreeSet<SongFamilyPair>();
		
		br = new BufferedReader(new FileReader("/Users/dave/Documents/Work/Research/workspace/Points/data/nlb_datasets/ann_labels.txt"));
		l = br.readLine();
		while (l != null) {
			int tabIndex = l.indexOf('\t');
			String song = l.substring(0,tabIndex).trim();
			String family = l.substring(tabIndex+1).trim();
			//Find ref song for this family
			String refSong = null;
			for(SongFamilyPair sfp : referenceSongFamilies) {
				if (sfp.getFamily().equals(family)) {
					refSong = sfp.getReferenceSong();
					break;
				}
			}
			//Find distance between song and ref song
			Double d = null;
			for(Distance distance : allDistances) {
				ArrayList<String> distanceSongs = new ArrayList<String>();
				distanceSongs.add(distance.getSong1());
				distanceSongs.add(distance.getSong2());
				if (distanceSongs.contains(song) && distanceSongs.contains(refSong)) {
					d = distance.getDistance();
					break;
				}
			}
			if (d == null) {
				br.close();
				throw new Exception("Unable to find distance node that contains "+song+" and "+refSong);
			}
			groundTruth.add(new SongFamilyPair(song, family, refSong, d));			
			l = br.readLine();
		}
		br.close();
		
		System.out.println("\nGROUND TRUTH\n============");
		i = 0;
		for(SongFamilyPair sfp : groundTruth)
			System.out.println(++i+". "+sfp);
		
		///////////////////////////////
		
		//Find classification errors and print them out
		
		//Classify each song in the annotated corpus to the family of the reference melody 
		//that it is closest to. This will be given by the first item in the list, distances,
		//that contains the corpus melody, since distances only contains distances between
		//corpus melodies and reference melodies and the items in the list are sorted into 
		//increasing order of distance.
				
		//Iterate through distances and identify each distance node which is the first one in which
		//an annotated corpus melody occurs.
		
		TreeSet<SongFamilyPair> classifiedSongs = new TreeSet<SongFamilyPair>();
		TreeSet<String> foundSongs = new TreeSet<String>();
		for(Distance distance : allDistances) {
			String refSong, song;
			if (referenceSongs.contains(distance.getSong1())) {
				refSong = distance.getSong1();
				song = distance.getSong2();
			} else {
				refSong = distance.getSong2();
				song = distance.getSong1();
			}
			if (!foundSongs.contains(song)) {
				String family = null;
				for(SongFamilyPair sfp : referenceSongFamilies)
					if (sfp.getSong().equals(refSong)) {
						family = sfp.getFamily();
						break;
					}
				if (family != null) {
					SongFamilyPair classifiedSongFamilyPair = new SongFamilyPair(song,family,refSong,distance.getDistance());
					classifiedSongs.add(classifiedSongFamilyPair);
					System.out.println(song+" "+refSong+" "+family+" "+String.format("%.4f",distance.getDistance())+" "+classifiedSongFamilyPair);
				}
				else
					throw new Exception("Reference song in distance not found in reference song family list.");
				foundSongs.add(song);
			}
		}
		
		///////////////////////////////
		
		//Compare classification with ground truth
		
		ArrayList<SongFamilyPair> testArray = new ArrayList<SongFamilyPair>(classifiedSongs);
		ArrayList<SongFamilyPair> groundTruthArray = new ArrayList<SongFamilyPair>(groundTruth);
		ArrayList<ArrayList<SongFamilyPair>> errorList = new ArrayList<ArrayList<SongFamilyPair>>();
		
		
		for(int j = 0; j < testArray.size(); j++) {
			SongFamilyPair testPair = testArray.get(j);
			SongFamilyPair groundTruthPair = groundTruthArray.get(j);
			if (!testPair.getSong().equals(groundTruthPair.getSong()))
				throw new Exception("Test pair song not equal to ground truth song - ordering of test array different from ground truth array");
			if (!testPair.getFamily().equals(groundTruthPair.getFamily())) {
				ArrayList<SongFamilyPair> errorItem = new ArrayList<SongFamilyPair>();
				errorItem.add(testPair);
				errorItem.add(groundTruthPair);
				errorList.add(errorItem);
			}
		}
		
		System.out.println("\n"+errorList.size()+" CLASSIFICATION ERRORS ("+String.format("%.2f", 100.0*(1.0-(1.0*errorList.size()/groundTruth.size())))+"% correct)");
		i = 0;
		for(ArrayList<SongFamilyPair> errorItem : errorList) {
			System.out.println(++i+".\t"+errorItem.get(0)+"\t\t\t\t"+errorItem.get(1));
		}
		
		///////////////////////////////
		
	}
}
