package com.chromamorph.points022;

import com.chromamorph.points018.MIREX2013Entries;

/**
 * 
 * @author David Meredith
 * @date August 3, 2013
 * 
 * Runs COSIATEC and returns the segments spanning the 
 * patterns generated, with minPatternSize set to 4.
 *
 */
public class MIREX2013CosiatecSegment {
	public static void main(String[] args) {
		String version = System.getProperty("java.version");
		if (!version.startsWith("1.6.0")) { 
			System.out.println("This program requires Java SE Runtime 1.6.0! You are currently using Java version "+version+".");
			return;
		}
		MIREX2013Entries.PARAMETER_STRING = "minPatternSize=4,segment=1";
		MIREX2013Entries.run(args);
	}
}
