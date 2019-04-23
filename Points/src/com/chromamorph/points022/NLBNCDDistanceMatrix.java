package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class NLBNCDDistanceMatrix {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/Users/dave/Documents/Work/Research/workspace/Points/output/points017/nlb-analysis/nlbncd.txt"));
		double[][] distances = new double[360][360];
		String l = br.readLine();
		for(int i = 0; i < 360; i++)
			for(int j = i+1; j < 360; j++) {
				l = br.readLine();
				double d = Double.parseDouble(l.substring(l.lastIndexOf('\t')+1));
				distances[i][j] = d;
			}
		for(int i = 1; i < 360; i++)
			for(int j = 0; j < i; j++)
				distances[i][j] = distances[j][i];
		for(int i = 0; i < 360; i++)
			distances[i][i] = 0;
		br.close();
		PrintStream out = new PrintStream("/Users/dave/Documents/Work/Research/workspace/Points/output/points017/nlb-analysis/nlbncd-distances.txt");
		for(int i = 0; i < 360; i++)
			for(int j = 0; j < 360; j++) {
				out.print(String.format("%.4f",distances[i][j]));
				if (j == 359) 
					out.println();
				else
					out.print("\t");
			}
		out.close();
	}
}
