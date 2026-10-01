package com.chromamorph.kryptos;

import java.util.ArrayList;

/**
 * This class provides methods for encoding and decoding by any Vigenère cipher (https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher)
 */
public class Vigenere {
	
	private static String decrypt(String cipherAsInput, String prefix, String key) {
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
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
		
	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Usage: java -jar vigenere.jar <d|e> <cipher|plain> <alphabet_prefix> <cycling_key>");
			return;
		}
		if (args[0].toUpperCase().equals("D"))
			System.out.println("\nPlain text is "+ decrypt(args[1].toUpperCase(), args[2].toUpperCase(), args[3].toUpperCase()));
		else
			System.out.println(encrypt(args[1].toUpperCase(), args[2].toUpperCase(), args[3].toUpperCase()));
	}
}
