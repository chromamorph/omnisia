package com.chromamorph.points022;

public class SetContainment {

	public static boolean contains(int[] query, int[] text) {
		int qi = 0, ti = 0;
		if (text.length < query.length)
			return false;
		while(qi < query.length && ti < text.length) {
			if (query[qi] == text[ti]) {
				qi++;
				ti++;
			} else if (query[qi] < text[ti]) {
				return false;
			} else if (query[qi] > text[ti]) {
				while(query[qi] > text[ti] && ti < text.length)
					ti++;
			}
		}
		if (qi < query.length)
			return false;
		return true;
	}
	
	public static void main(String[] args) {
		int[] query = new int[] {1,3};
		int[] text = new int[] {1, 4, 7, 11};
		
		System.out.println(contains(query, text));
		
	}
}
