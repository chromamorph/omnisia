package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ComparatorTest {

	static class MyThing {
		int x, y;
		
		MyThing(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public String toString() {
			return "("+x+","+y+")";
		}
	}
	
	static class MyComparator implements Comparator<MyThing> {

		@Override
		public int compare(MyThing o1, MyThing o2) {
			int d = o1.x - o2.x;
			if (d != 0) return d;
			return o1.y - o2.y;
		}

	}
	
	public static void main(String[] args) {
		ArrayList<MyThing> l = new ArrayList<MyThing>();
		for(int x = 3; x > -3; x--)
			for(int y = 5; y < 10; y++)
				l.add(new MyThing(x,y));
		Collections.sort(l, new MyComparator());
		System.out.println(l);
	}
	
	
}
