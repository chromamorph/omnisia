package com.chromamorph.maxtranpatsmpi;

import java.util.ArrayList;

import com.chromamorph.maxtranpatsjava.LogInfo;

public class MPITest {

	
	static int[] a = new int[2139999999];
	
	public static void main(String[] args) {
		LogInfo.ACCUMULATED_TIME = 0l;
		ArrayList<LogInfo> log = new ArrayList<LogInfo>();
		log.add(new LogInfo("About to start filling array", true));

		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}
		log.add(new LogInfo("Completed filling array", true));
		System.out.println(a[a.length-1]);
	}

}
