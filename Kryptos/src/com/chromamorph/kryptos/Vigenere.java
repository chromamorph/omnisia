package com.chromamorph.kryptos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class provides methods for encoding, decoding and cracking any Vigenère cipher (https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher)
 */
public class Vigenere {
	public static String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private static String decrypt(String cipherAsInput, String prefix, String key) {
		StringBuilder cipherSB = new StringBuilder();
		for(int i = 0; i < cipherAsInput.length(); i++)
			if (alpha.contains(((Character)(cipherAsInput.charAt(i))).toString()))
				cipherSB.append(cipherAsInput.charAt(i));
		
		String cipher = cipherSB.toString();

		System.out.println("Ciphertext is "+cipher);
		StringBuilder plain = new StringBuilder();
		
		char[][] table = new char[key.length()][26]; //rows by columns
		ArrayList<Character> alphabet = new ArrayList<Character>();
		for(int i = 0; i < alpha.length(); i++)
			alphabet.add(alpha.charAt(i));
		
//		System.out.println(alphabet);
		
		StringBuilder cryptAlph = new StringBuilder(prefix);
//		System.out.println(cryptAlph);
		for(int i = 0; i < prefix.length(); i++)
			alphabet.remove((Character)(prefix.charAt(i)));
//		System.out.println(alphabet);
		for(Character c : alphabet)
			cryptAlph.append(c);
		System.out.println("Prefix is "+prefix);
		System.out.println("Key is "+key);
		System.out.println("Crypto-alphabet is "+cryptAlph.toString());
		
		for(int row = 0; row < key.length(); row++)
			for(int col = 0; col < 26; col++) {
				int offset = cryptAlph.indexOf(((Character)(key.charAt(row))).toString());
				table[row][col] = cryptAlph.charAt((offset+col)%26);
			}
		
		System.out.println("Vigenère table is");
		
		for(int i = 0; i < cryptAlph.length(); i++)
			System.out.print(cryptAlph.charAt(i)+" ");
		System.out.println(" <-- Crypto-alphabet");
			
		
		for(int row = 0; row < key.length(); row++) {
			for(int col = 0; col < 26; col++) {
				System.out.print(table[row][col]+" ");
			}
			System.out.println();
		}
			
		for(int i = 0; i < cipher.length(); i++) {
			int row = i%key.length();
			char cipherChar = cipher.charAt(i);
			int col = 0;
			while(col < 26 && table[row][col] != cipherChar) col++;
			
			plain.append(col<26?cryptAlph.charAt(col):cipherChar);
		}
		
		
		return plain.toString();
	}
	
	private static String encrypt(String plain, String prefix, String key) {
		StringBuilder cipher = new StringBuilder();
		return cipher.toString();
	}
		
	public static ArrayList<Integer> textToIntArray(String text) {
		String upcaseText = text.toUpperCase();
		ArrayList<Integer> intArray = new ArrayList<Integer>();
		for(int i = 0; i < text.length(); i++) {
			char c = upcaseText.charAt(i);
			if (c >= 'A' && c <= 'Z')
				intArray.add((int)c-(int)('A'));
		}
		return intArray;
	}
	
	public static String crackVigenere(String cipherText) {
		int N = cipherText.length();
				
		String plainText = null;
		System.out.println("Cipher text: "+cipherText);
		
		for(int i = 0; i < N; i++)
			System.out.print(String.format("%4d", i));
		System.out.println();
		for(int i = 0; i < N; i++)
			System.out.print(String.format("%4s", cipherText.charAt(i)));
		System.out.println();
		
		ArrayList<Integer> cipherIntArray = textToIntArray(cipherText);
		for(int i = 0; i < N; i++)
			System.out.print(String.format("%4d", cipherIntArray.get(i)));
		System.out.println();
		System.out.println("Cipher text length: "+cipherText.length());
		
		// Find best cycleLength
		////////////////////////
		
		for(int cycleLength = 1; cycleLength <= cipherText.length(); cycleLength++) {
			
			int[][] histograms = new int[cycleLength][26];
			// row with index i is a frequency histogram of the letters
			// at positions {j | j = i + k*cycleLength && 0 <= j < N}
			// where k is a non-negative integer and N is the length of the 
			// cipherText
			
			// Initialize histograms
			for(int i = 0; i < cycleLength; i++)
				for(int c = 0; c < 26; c++)
					histograms[i][c] = 0;
			
			// Compute histograms
			for(int i = 0; i < cycleLength; i++) {
				for(int k = 0; i+k*cycleLength < N; k++)
					histograms[i][cipherIntArray.get(i+k*cycleLength)]++;
			}
			
			// Print out histograms
			System.out.println("\n\nCycle length = "+cycleLength);
			for(int c = 0; c < 26; c++)
				System.out.print(String.format("%4s", alpha.charAt(c)));
			for(int i = 0; i < cycleLength; i++) {
				System.out.println();
				for(int c = 0; c < 26; c++)
					System.out.print(String.format("%4d", histograms[i][c]));
				// compute similarity of sorted histogram to english frequency vector
			}
			
			
			// Compute  cycleLength x cycleLength x 2 array that gives the best rotation and minimum distance
			// between each pair of histograms
		}
		
		return plainText;
	}

	public static void printHelp() {
		System.out.println(
				"Syntax: java -jar vigenere.jar <d|e|c> <text-file-path> [alphabet-prefix cycling-key]"
				);
	}
	
	public static boolean getIsDecrypt(String[] args) {
		return args[0].toUpperCase().equals("D");
	}
	
	public static boolean getIsEncrypt(String[] args) {
		return args[0].toUpperCase().equals("E");
	}
	
	public static boolean getIsCrack(String[] args) {
		return args[0].toUpperCase().equals("C");
	}
	
	public static String getText(String[] args) throws IOException {
		String filePath = args[1];
		StringBuilder text = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String l;
		while ((l = br.readLine()) != null)
			text.append(l);
		br.close();
		return text.toString().toUpperCase();
	}
	
	public static String getAlphabetPrefix(String[] args) {
		if (args.length >= 3)
			return args[2].toUpperCase();
		return null;
	}
	
	public static String getCyclingKey(String[] args) {
		if (args.length >= 4)
			return args[3].toUpperCase();
		return null;
	}
	
	public static void main(String[] args) {
		try {
		if (args.length < 2) {
			printHelp();
			return;
		}
		boolean isDecrypt = getIsDecrypt(args);
		boolean isEncrypt = getIsEncrypt(args);
		boolean isCrack = getIsCrack(args);
		String text = getText(args);
		String alphabetPrefix = getAlphabetPrefix(args);
		String cyclingKey = getCyclingKey(args);
		if ((isDecrypt || isEncrypt) && (text == null || alphabetPrefix == null || cyclingKey == null)) {
			printHelp();
			return;
		}
		if (isCrack && text == null) {
			printHelp();
			return;
		}
		if (isDecrypt)
			System.out.println("\nPlain text is "+ decrypt(text, alphabetPrefix, cyclingKey));
		else if (isEncrypt)
			System.out.println(encrypt(text, alphabetPrefix, cyclingKey));
		else if (isCrack)
			System.out.println(crackVigenere(text));
		} catch(Exception e) {
			e.printStackTrace();
			printHelp();
		}
	}
}
