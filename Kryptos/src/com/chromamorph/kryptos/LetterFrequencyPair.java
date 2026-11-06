package com.chromamorph.kryptos;

public class LetterFrequencyPair implements Comparable<LetterFrequencyPair>{
	private char letter;
	private int freq;
	
	public char getLetter() {
		return letter;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public LetterFrequencyPair(char letter, int freq) {
		setLetter(letter);
		setFreq(freq);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof LetterFrequencyPair)) return false;
		return compareTo((LetterFrequencyPair)obj) == 0;
	}
	
	@Override
	public int compareTo(LetterFrequencyPair o) {
		if (o == null) return -1;
		if (getFreq() > o.getFreq()) return -1;
		if (getFreq() < o.getFreq()) return 1;
//		freq is equal
		if (getLetter() > o.getLetter()) return -1;
		if (getLetter() < o.getLetter()) return 1;
//		letter is equal
		return 0;
	}

	@Override
	public String toString() {
		return "("+letter+","+freq+")";
	}

	
	
	
}
