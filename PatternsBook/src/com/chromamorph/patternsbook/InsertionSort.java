package com.chromamorph.patternsbook;

import java.util.ArrayList;

public class InsertionSort {
	
	public static void sort(int[] A) {
		for (int j = 1; j < A.length; j++) {
			int k = A[j];
			int i = j - 1;
			while (i >= 0 && A[i] > k) {
				A[i+1] = A[i];
				i = i - 1;
			}
			A[i + 1] = k;
		}
	}
	
	public static void main(String[] args) {
		int[] A = new int[args.length];
		for(int i = 0; i < args.length; i++)
			A[i] = Integer.parseInt(args[i]);

		sort(A);
		ArrayList<Integer> AList = new ArrayList<Integer>();
		for(int a : A) AList.add(a);
		System.out.println(AList);
	}
}


