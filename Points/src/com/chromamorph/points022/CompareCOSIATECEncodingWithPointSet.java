package com.chromamorph.points022;

import java.io.IOException;

public class CompareCOSIATECEncodingWithPointSet {
	public static void main(String[] args) {
		try {
			System.out.println(COSIATECEncoding.compareEncodingWithPointSet());
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
