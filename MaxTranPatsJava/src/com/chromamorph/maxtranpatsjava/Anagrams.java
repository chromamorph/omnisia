package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;

public class Anagrams {
	
	static char[] source = new char[]{'R','E','S','T','I','K'};
	
	public static ArrayList<ArrayList<Character>> computeKSubsets(int basisSize) {
		ArrayList<ArrayList<Character>> bases = new ArrayList<ArrayList<Character>>();
		int[] indices = new int[basisSize];
		int[] maxIndices = new int[basisSize];
		for(int i = 0; i < basisSize; i++)
			indices[i] = i;
		for(int i = 0; i < basisSize; i++)
			maxIndices[basisSize - i - 1] = source.length - i - 1;
		boolean finished = false;
		while (!finished) {
			ArrayList<Character> seq = new ArrayList<Character>();
			for(int j = 0; j < basisSize; j++)
				seq.add(source[indices[j]]);
			bases.add(seq);
			if (maxIndices[basisSize-1] != indices[basisSize-1])
				++indices[basisSize-1];
			else {
//				Final index is at maximum value
				
//				Work backwards from end of indices and find first index that is not 
//				at its maximum value
				int firstNonMaxIndex = basisSize - 2;
				while((firstNonMaxIndex >= 0) && (indices[firstNonMaxIndex] == maxIndices[firstNonMaxIndex])) 
					firstNonMaxIndex--;
				if (firstNonMaxIndex == -1)
					finished = true;
				else {
					++indices[firstNonMaxIndex];
					for(int i= 1, j = firstNonMaxIndex+1; j < basisSize; i++, j++)
						indices[j] = indices[firstNonMaxIndex] + i;
				}
			}
		}
		return bases;
	}

	public static void main(String[] args) {
		ArrayList<ArrayList<Character>> kSubsets = computeKSubsets(4);
		int[][] permSeqs = Utility.computePermutationIndexSequences(4);
		int j = 0;
		for(ArrayList<Character> kSubset : kSubsets) {
			for(int[] indexSeq : permSeqs) {
				ArrayList<Character> thisWord = new ArrayList<Character>();
				for(int i : indexSeq)
					thisWord.add(kSubset.get(i));
				System.out.println(++j + ". "+ thisWord);
			}
		}
	}
}
