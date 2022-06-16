package com.chromamorph.javacppcompare;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * 
 * @author David Meredith
 * 
 * Java equivalent of Antti Laaksonen's
 * a.cpp
 * 
 * Hardware Overview:

  Model Name:	MacBook Pro
  Model Identifier:	MacBookPro16,1
  Processor Name:	8-Core Intel Core i9
  Processor Speed:	2.4 GHz
  Number of Processors:	1
  Total Number of Cores:	8
  L2 Cache (per Core):	256 KB
  L3 Cache:	16 MB
  Hyper-Threading Technology:	Enabled
  Memory:	64 GB
  Boot ROM Version:	1715.81.2.0.0 (iBridge: 19.16.10744.0.0,0)
  Serial Number (system):	C02DV21KMD6T
  Hardware UUID:	7C85C2BB-4FD0-5E4E-8101-12E7DDBFE7D3
  Activation Lock Status:	Enabled

 * a.cpp compiled with no optimisation:
 * 	number of MTPs: 7567810
	running time: 59.2737 s

   a.cpp compiled with -O3 optimisation:
    number of MTPs: 7567810
	running time: 11.4801 s

   java -Xms64G -jar JavaCppCompareA.jar ../data/antti-20000-point-dataset/test.in
number of MTPs: 199990000

running time: 125.241 seconds

Number of MTPs seems wrong.

15:42:29:~/Repos/omnisia/JavaCPPCompare/jars on branch master % java -Xms64G -jar JavaCppCompareA02.jar ../data/antti-20000-point-dataset/test.in
number of MTPs: 7567810
running time: 58.256 seconds

- this is faster than the non-optimized C++ version!
 *
 */
public class JavaCppCompareA02 {
	
	static int n;
	
	public static int[][] dataset = new int[20000][2];
	public static Comparator<int[]> comparator = new Comparator<int[]>() {

		@Override
		public int compare(int[] o1, int[] o2) {
			int d = o1[0] - o2[0];
			if (d != 0)
				return d;
			return o1[1] - o2[1];
		}
		
	};
	
	public static int calc(int k) {
		int[][] vectorTable = new int[k * (k-1)/2][2];
		int m = 0;
		for(int i = 0; i < k; i++) {
			for (int j = i + 1; j < k; j++) {
				vectorTable[m][0] = dataset[j][0] - dataset[i][0];
				vectorTable[m][1] =	dataset[j][1] - dataset[i][1];
				m++;
			}
		}
		Arrays.sort(vectorTable,comparator);
		int r = 1;
		for(int i = 1; i < vectorTable.length; i++) {
			if (vectorTable[i-1][0] != vectorTable[i][0] ||
					vectorTable[i-1][1] != vectorTable[i][1]) r++;
		}
		return r;
	}
	public static void main(String[] args) {
		FileReader fr;
		try {
			fr = new FileReader(args[0]);
			BufferedReader br = new BufferedReader(fr);
			
			n = Integer.parseInt(br.readLine().trim());
			for(int i = 0; i < n; i++) {
				String s = br.readLine();
				String[] a = s.split(" ");
				dataset[i][0] = Integer.parseInt(a[0].trim());
				dataset[i][1] = Integer.parseInt(a[1].trim());
			}
			Arrays.sort(dataset, comparator);
			long start = System.currentTimeMillis();
			int result = calc(n);
			long end = System.currentTimeMillis();
			br.close();		
			fr.close();
			long duration  = end - start;
			System.out.println("number of MTPs: " + result);
			System.out.println("running time: "+duration/1000.0+" seconds");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
