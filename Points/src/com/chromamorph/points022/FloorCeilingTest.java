package com.chromamorph.points022;

import java.util.TreeSet;

public class FloorCeilingTest {
	
	static class Node implements Comparable<Node> {
		Integer x = null, y = null, z = null;
		
		public Node(Integer x, Integer y, Integer z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public Node() {
			x = null;
			y = null;
			z = null;
		}
		
		public int compareTo(Node n) {
			if (n == null) return 1;
			if (x == null) {
				if (n.x != null) return -1;
				//So x and n.x are both null - go onto y
			} else { //So x != null
				if (n.x == null) return 1;
				//Neither x not n.x are null
				int d = x-n.x;
				if (d != 0) return d;
				//x and n.x are equal and non-null - go onto y
			}
		
			if (y == null) {
				if (n.y != null) return -1;
				//So y and n.y are both null - go onto z
			} else { //So y != null
				if (n.y == null) return 1;
				//Neither y not n.y are null
				int d = y-n.y;
				if (d != 0) return d;
				//y and n.y are equal and non-null - go onto z
			}

			if (z == null) {
				if (n.z != null) return -1;
				//So z and n.z are both null - return 0
			} else { //So z != null
				if (n.z == null) return 1;
				//Neither z not n.z are null
				return z-n.z;
			}

			return 0;
		}

		
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof Node)) return false;
			return compareTo((Node)o)==0;
		}
		
		public String toString() {
			return "("+x+","+y+","+z+")";
		}
	}
	
	public static void main(String[] args) {
		TreeSet<Node> nodes = new TreeSet<Node>();
		nodes.add(new Node(0,0,0));
		nodes.add(new Node(0,1,0));
		nodes.add(new Node(0,1,2));
		nodes.add(new Node(-1,1,2));
		Node searchNode = new Node();
		searchNode.x = 0;
		Node c = nodes.ceiling(searchNode);
		Node f = nodes.floor(searchNode);
		TreeSet<Node> segmentWithout = (TreeSet<Node>) nodes.subSet(f, c);
		TreeSet<Node> segmentWith = (TreeSet<Node>) nodes.subSet(f,true,c,true);
		System.out.println("Nodes: "+nodes);
		System.out.println("Ceiling: "+c);
		System.out.println("Floor: "+f);
		System.out.println("Segment without: "+segmentWithout);
		System.out.println("Segment with: "+segmentWith);
	}
}
