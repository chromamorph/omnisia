package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class PatternPair implements Comparable<PatternPair>{
	private PointSet pattern1, pattern2;
	
	public PatternPair(PointSet pattern1, PointSet pattern2) {
		this.pattern1 = pattern1;
		this.pattern2 = pattern2;
	}
	
	/**
	 * Assumes parameter, l, has the following format:
	 * 
	 * PP(pattern1,pattern2)
	 * 
	 * where pattern1 and pattern2 have the following format
	 * P(p_1,p_2,...p_n)
	 * 
	 * where p_i has the following format
	 * p(x,y)
	 * 
	 * where x and y are integers.
	 * 
	 * @param l A string representation of a pattern pair
	 * @throws InvalidArgumentException 
	 */
	public PatternPair(String l) throws InvalidArgumentException {
		if (!l.startsWith("PP("))
			throw new InvalidArgumentException("Argument to PatternPair constructor has an invalid format.");
		String l2 = l.trim();
		int patternOneStart = 3;
		int patternOneEnd = l2.indexOf("))")+2;
		String patternOneString = l.substring(patternOneStart,patternOneEnd);
		int patternTwoStart = patternOneEnd + 1;
		int patternTwoEnd = l2.length() - 1;
		String patternTwoString = l.substring(patternTwoStart,patternTwoEnd);
		
		pattern1 = new PointSet();
		for(int i = 2;i < patternOneString.length()-1;i++) {
			int pointEndPos = patternOneString.indexOf(")",i)+1;
			pattern1.add(new Point(patternOneString.substring(i,pointEndPos)));
			i = pointEndPos;
		}
		pattern2 = new PointSet();
		for(int i = 2;i < patternTwoString.length()-1;i++) {
			int pointEndPos = patternTwoString.indexOf(")",i)+1;
			pattern2.add(new Point(patternTwoString.substring(i,pointEndPos)));
			i = pointEndPos;
		}
	}
	
	public PointSet getPattern1() {return pattern1;}
	public PointSet getPattern2() {return pattern2;}
	
	@Override
	public int compareTo(PatternPair pp) {
		int d = pattern1.compareTo(pp.getPattern1());
		if (d != 0) return d;
		return pattern2.compareTo(pp.getPattern2());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PatternPair)) return false;
		return compareTo((PatternPair)obj) == 0;
	}
	
	@Override
	public String toString() {
		return "PP("+pattern1+","+pattern2+")";
	}
	
	public static TreeSet<PatternPair> readPatternPairsFromFile(String fileName) throws IOException, InvalidArgumentException {
		TreeSet<PatternPair> patternPairs = new TreeSet<PatternPair>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String l;
		while ((l = br.readLine())!=null) {
			if (l.startsWith("PP("))
				patternPairs.add(new PatternPair(l));
		}
		br.close();
		return patternPairs;
	}
	
	static TreeSet<PatternPair> removeSubsumedPatternPairs(TreeSet<PatternPair> patternPairs) {
		ArrayList<PatternPair> patternPairList = new ArrayList<PatternPair>();
		patternPairList.addAll(patternPairs);
		for(int i = 1; i < patternPairList.size(); i++) {
			for (int j = i-1; j >= 0; j--) {
				PointSet pairIPattern1 = patternPairList.get(i).getPattern1();
				PointSet pairIPattern2 = patternPairList.get(i).getPattern2();
				PointSet pairJPattern1 = patternPairList.get(j).getPattern1();
				PointSet pairJPattern2 = patternPairList.get(j).getPattern2();
				
				if (pairJPattern1.contains(pairIPattern1) &&
						pairJPattern2.contains(pairIPattern2) &&
						pairJPattern2.first().getY()-pairJPattern1.first().getY() == pairIPattern2.first().getY()-pairIPattern1.first().getY()) {
					patternPairList.remove(i);
					i--;
					j = 0;
				}
			}
		}
		TreeSet<PatternPair> newTreeSet = new TreeSet<PatternPair>();
		newTreeSet.addAll(patternPairList);
		return newTreeSet;
	}
	
	public static void main(String[] args) {
		try {
//			PointSet pointSet = new PointSet("C:/cygwin64/home/dave/laaksonen-lemstrom/test.pts");
			PointSet pointSet = new PointSet("C:/cygwin64/home/dave/laaksonen-lemstrom/bwv846b-done.pts");

//			TreeSet<PatternPair> patternPairs = readPatternPairsFromFile("C:/cygwin64/home/dave/laaksonen-lemstrom/output-test.txt");
			TreeSet<PatternPair> patternPairs = readPatternPairsFromFile("C:/cygwin64/home/dave/laaksonen-lemstrom/bwv846b-done.out");
			patternPairs = PatternPair.removeSubsumedPatternPairs(patternPairs);
			pointSet.draw(patternPairs);
			System.out.println(patternPairs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (MissingTieStartNoteException e) {
			e.printStackTrace();
		}
	}
}
