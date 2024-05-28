package com.chromamorph.points022;

public class TECWeightPair implements Comparable<TECWeightPair> {
	private TEC tec;
	private double weight;

	public TEC getTEC() {return tec;}
	public double getWeight() {return weight;}
	
	public void setTEC(TEC tec) {
		this.tec = tec;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	TECWeightPair(TEC tec, double weight) {
		setTEC(tec);
		setWeight(weight);
	}
	
	public String toString() {
		return "TecWeight("+getTEC()+","+getWeight()+")";
	}

	@Override
	public int compareTo(TECWeightPair o) throws NullPointerException {
		if (o == null) throw new NullPointerException("TECWeightPair.compareTo() is given a null argument.");
		if (getWeight() < o.getWeight()) return 1;
		if (getWeight() > o.getWeight()) return -1;
		return getTEC().compareTo(o.getTEC());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TECWeightPair)) return false;
		return compareTo((TECWeightPair)obj) == 0;
	}
}
