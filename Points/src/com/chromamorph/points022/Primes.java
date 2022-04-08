package com.chromamorph.points022;

import java.util.ArrayList;

public class Primes {
	public static void main(String[] args) {
		int max = 10000;
		ArrayList<Integer> primes = new ArrayList<Integer>();
		primes.add(2);
		primes.add(3);
		for (int i = 5; i <= max; i += 2) {
			boolean isPrime = true;
			for (int p : primes) {
				if (p > Math.sqrt(1.0 * i)) break;
				if (i % p == 0) {
					isPrime = false;
					break;
				}
			}
			if (isPrime) {
				primes.add(i);
			}
		}
//		System.out.println(primes);
		for(int i = 0; i < primes.size(); i++)
//			System.out.println((i+1)+" "+primes.get(i));
			System.out.println(primes.get(i)+" "+(i+1));

	}
	
}
