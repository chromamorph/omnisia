package com.chromamorph.points022;

/**
 * 
 * @author David Meredith
 * @date August 3, 2013
 * 
 * Runs SIATECCompress and returns the segments spanning the 
 * patterns generated, with minPatternSize set to 4.
 *
 */
public class MIREX2013SiatecCompressSegment {
	public static void main(String[] args) {
		String version = System.getProperty("java.version");
//		if (!version.startsWith("1.6.0")) { 
//			System.out.println("This program requires Java SE Runtime 1.6.0! You are currently using Java version "+version+".");
//			return;
//		}
		MIREX2013Entries.PARAMETER_STRING = "segment=1,minPatternSize=4,siatecCompress=1";
		MIREX2013Entries.run(args);
	}
}
