package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ISMIR2019JKUPDDMakeResultsTable {
	public static void main(String[] args) throws IOException {
		String resultsFolder = "D:\\Dropbox\\Work\\Research\\Papers in preparation\\2019-04-05-ISMIR-2019-OPTISIA\\JKUPDD-noAudio-Aug2013\\matlab\\results\\";
		BufferedReader input = new BufferedReader(new FileReader(resultsFolder+"results.txt"));
		PrintWriter fFile = new PrintWriter(new File(resultsFolder+"tlf1.csv"));
		PrintWriter rFile = new PrintWriter(new File(resultsFolder+"tlr.csv"));
		PrintWriter pFile = new PrintWriter(new File(resultsFolder+"tlp.csv"));
		PrintWriter rtFile = new PrintWriter(new File(resultsFolder+"rt.csv"));
		String headers = "Algorithm, Bach, Beet, Chop, Gbns, Mzrt";
		fFile.println(headers);
		rFile.println(headers);
		pFile.println(headers);
		rtFile.println(headers);
		for(int alg = 0; alg < 16; alg++) {
			String alg_name = input.readLine().replace(","," "); //Reads algorithm name line, e.g., "Algorithm 8, COSIATEC"
			input.readLine(); //Reads column header line
			for(int piece = 0; piece < 5; piece++) {
				input.readLine(); //Reads piece label
				String l = input.readLine(); //Reads results for this piece
				System.out.println(l);
				String[] a = l.split(",");				
				if (piece==0) {
					fFile.print(alg_name.trim());
					rFile.print(alg_name.trim());
					pFile.print(alg_name.trim());
					rtFile.print(alg_name.trim());
				}
				fFile.print(", "+a[10].trim());
				rFile.print(", "+a[9].trim());
				pFile.print(", "+a[8].trim());
				rtFile.print(", "+a[11].trim());
				input.readLine();
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
