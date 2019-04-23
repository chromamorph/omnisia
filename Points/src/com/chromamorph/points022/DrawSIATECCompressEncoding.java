package com.chromamorph.points022;

public class DrawSIATECCompressEncoding {
	public static void main(String[] args) {
		SIATECCompressEncoding encoding = new SIATECCompressEncoding(MIREX2013Entries.getFileName("Choose SIATECCompress encoding file", "~/"));
		encoding.draw();
	}
}
