package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class MIREX2016MakeResultsTable {
	public static void main(String[] args) throws IOException {
		String resultsFolder = "/Users/dave/Documents/Work/Research/Data/MIREX2016/mirex-results-siatecCompress/";
		BufferedReader input = new BufferedReader(new FileReader(resultsFolder+"results.csv"));
		PrintWriter fFile = new PrintWriter(new File(resultsFolder+"tlf1.csv"));
		PrintWriter rFile = new PrintWriter(new File(resultsFolder+"tlr.csv"));
		PrintWriter pFile = new PrintWriter(new File(resultsFolder+"tlp.csv"));
		PrintWriter rtFile = new PrintWriter(new File(resultsFolder+"rt.csv"));
		String headers = "Algorithm, Bach, Beet, Chop, Gbns, Mzrt";
		fFile.println(headers);
		rFile.println(headers);
		pFile.println(headers);
		rtFile.println(headers);
		String l = input.readLine();
		for(int alg = 0; alg < 72; alg++) {
			for(int piece = 0; piece < 5; piece++) {
				l = input.readLine(); 
				String[] a = l.split(",");				
				if (piece==0) {
					fFile.print(a[0].trim());
					rFile.print(a[0].trim());
					pFile.print(a[0].trim());
					rtFile.print(a[0].trim());
				}
				fFile.print(", "+a[12].trim());
				rFile.print(", "+a[11].trim());
				pFile.print(", "+a[10].trim());
				rtFile.print(", "+a[13].trim());
					
			}
			fFile.println();
			rFile.println();
			pFile.println();
			rtFile.println();
		}
		input.close();
		fFile.close();
		rFile.close();
		pFile.close();
		rtFile.close();
	}
}
