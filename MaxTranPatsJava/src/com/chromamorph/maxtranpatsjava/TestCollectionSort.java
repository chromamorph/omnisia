package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.Collections;

public class TestCollectionSort {
	public static void main(String[] args) {
		ArrayList<Integer> ints = new ArrayList<Integer>();
		ints.add(3);
		ints.add(1);
		ints.add(7);
		ints.add(0);
		System.out.println(ints);
		Collections.sort(ints);
		System.out.println(ints);
	}
}
