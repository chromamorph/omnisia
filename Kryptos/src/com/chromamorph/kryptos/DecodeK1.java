package com.chromamorph.kryptos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

import com.chromamorph.maths.Maths;

public class DecodeK1 {

	static char[][] tableau = new char[28][32];
	static char[][] cipher = new char[28][32];
	static String tableauFilePath = "data/tableau.txt";
	static String cipherFilePath = "data/cipher.txt";


	public static void readCharArray(char[][] a, String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		for (int r = 0; r < 28; r++) {
			String line = br.readLine();
			for(int c = 0; c < 32; c++) {
				a[r][c] = (c<line.length()?line.charAt(c):' ');
			}
		}
		br.close();
	}

	public static void print2DCharArray(char[][] a) {
		System.out.println();
		for(int r = 0; r < a.length; r++) {
			for(int c = 0; c < a[r].length; c++)
				System.out.print(a[r][c]);
			System.out.println();
		}
	}

	public static void print1DCharArray(char[] a) {
		System.out.println();
		for(int c = 0; c < a.length; c++)
			System.out.print(a[c]);
		System.out.println();
	}

	public static char[] constructVigenereAlphabet(String prefix) {
		String pref = prefix.toUpperCase();
		TreeSet<Character> lettersInPrefix = new TreeSet<Character>();
		for(int i = 0; i < prefix.length(); i++)
			lettersInPrefix.add(pref.charAt(i));
		if (lettersInPrefix.size() < prefix.length())
			throw new IllegalArgumentException("constructVigenereAlphabet called with invalid prefix!");
		char[] vigAlph = new char[26];
		int i = 0;
		for(; i < pref.length(); i++)
			vigAlph[i] = pref.charAt(i);
		for(char c = 'A'; c <= 'Z';  c++)
			if (!lettersInPrefix.contains(c)) {
				vigAlph[i] = c;
				i++;
			}
		return vigAlph;
	}

	public static void decodeK1() {
//		char[] vigAlph = constructVigenereAlphabet("KRYPTOS");
//		print1DCharArray(vigAlph);
		char[] key = new char[] {'P','A','L','I','M','P','S','E','S','T'};
		char[][] plain = new char[2][32];
		System.out.println();
		int cipherIndex = 0;
		for(int row = 0; row < 2; row++) {
			for (int col = 0; col < 32; col++) {
				char cipherChar = cipher[row][col];
				if (cipherChar != ' ') {
					int keyIndex = Maths.mod(cipherIndex, key.length);
					char keyChar = key[keyIndex];
					int tc = keyChar - 'A' + 1;
					int tr = 1;
					while (tableau[tr][tc]!=cipherChar) tr++;
					plain[row][col] = (char) (tr-1+'A');
					cipherIndex++;
				}
			}
			System.out.println();
		}
		print2DCharArray(plain);
	}

	public static void main(String[] args) {
		try {
			readCharArray(tableau, tableauFilePath);
			print2DCharArray(tableau);
			readCharArray(cipher, cipherFilePath);
			print2DCharArray(cipher);
			decodeK1();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
