package com.chromamorph.points022;

public class DrawCOSIATECEncoding {
	public static void main(String[] args) {
		COSIATECEncoding encoding = new COSIATECEncoding(MIREX2013Entries.getFileName("Choose COSIATEC encoding file", "~/"));
		encoding.draw();
	}

}
