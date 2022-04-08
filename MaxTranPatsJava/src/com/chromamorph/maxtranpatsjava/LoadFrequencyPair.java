package com.chromamorph.maxtranpatsjava;

public class LoadFrequencyPair implements Comparable<LoadFrequencyPair> {
	private int load, frequency;

	public int getLoad() {
		return load;
	}

	public void setLoad(int load) {
		this.load = load;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	@Override
	public String toString() {
		return "LoadFrequencyPair("+getLoad()+","+getFrequency()+")";
	}

	public LoadFrequencyPair(int load, int frequency) {
		setLoad(load);
		setFrequency(frequency);
	}
	
	public void add() {
		setFrequency(1+getFrequency());
	}

	@Override
	public int compareTo(LoadFrequencyPair o) {
		if (o == null) return 1;
		int d = getLoad() - o.getLoad();
		if (d != 0) return d;
		d = getFrequency() - o.getFrequency();
		return d;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof LoadFrequencyPair)) return false;
		return compareTo((LoadFrequencyPair)obj) == 0;
	}
	
}
