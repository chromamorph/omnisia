package com.chromamorph.points022;

public class LinearRegression {
	public static void main(String[] args) {
		double[] logp = {-6.214608098,
				-5.521460918,
				-5.11599581,
				-4.828313737,
				-4.605170186,
				-4.422848629,
				-4.268697949,
				-4.135166557,
				-4.017383521,
				-3.912023005,
				-3.816712826,
				-3.729701449,
				-3.649658741,
				-3.575550769,
				-3.506557897,
				-3.442019376,
				-3.381394754,
				-3.324236341,
				-3.270169119,
				-3.218875825};
		double[] cr = {
				1.10,
				1.18,
				1.17,
				1.17,
				1.20,
				1.21,
				1.20,
				1.23,
				1.25,
				1.25,
				1.26,
				1.25,
				1.29,
				1.29,
				1.30,
				1.28,
				1.31,
				1.30,
				1.31,
				1.31	
		};
		
		double aSum = 0.0;
		
		for(int i1 = 0; i1 < cr.length-1;i1++)
			for(int i2 = i1+1; i2 < cr.length; i2++)
				aSum += (cr[i1]-cr[i2])/(logp[i1]-logp[i2]);
		int n = cr.length;
		int N = (n * (n-1))/2;
		double crSum = 0.0;
		double logPSum = 0.0;
		for(int i = 0; i < n; i++) {
			crSum += cr[i];
			logPSum += logp[i];
		}
		double meanCr=crSum/n;
		double meanLogP = logPSum/n;
		double a = aSum/N;
		System.out.println("a="+a);
		double b = meanCr-a*meanLogP;
		System.out.println("b="+b);
	}
}
