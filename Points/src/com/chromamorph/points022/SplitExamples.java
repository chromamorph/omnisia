package com.chromamorph.points022;

public class SplitExamples {
	public static void main(String[] args){
		String s = "2 3 4  				5";
		String[] a = s.split("[ \t]+");
		for(String x : a) {
			System.out.print("\""+x+"\""+" ");
		}
	}
}
