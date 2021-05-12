package com.chromamorph.maxtranpatsjava;

import java.util.ArrayList;
import java.util.Collection;

public class Encoding {
	private ArrayList<OccurrenceSet> occurrenceSets;
	
	public Encoding() {}
	
	public Encoding(ArrayList<OccurrenceSet> occurrenceSets) {
		setOccurrenceSets(occurrenceSets);
	}
	
	public void setOccurrenceSets(ArrayList<OccurrenceSet> occurrenceSets) {
		this.occurrenceSets = occurrenceSets;
	}
	
	public ArrayList<OccurrenceSet> getOccurrenceSets() {
		return occurrenceSets;
	}
	
	public void add(OccurrenceSet occurrenceSet) {
		if (getOccurrenceSets() == null)
			occurrenceSets = new ArrayList<OccurrenceSet>();
		getOccurrenceSets().add(occurrenceSet);
	}
	
	public void addAll(Collection<OccurrenceSet> occurrenceSets) {
		if (getOccurrenceSets() == null)
			occurrenceSets = new ArrayList<OccurrenceSet>();
		getOccurrenceSets().addAll(occurrenceSets);
	}
	
	public int getCoverage() throws Exception {
		return getCoveredSet().size();
	}
	
	public int getUncompressedLength() throws Exception {
		return getCoverage() * getDimensionality();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(OccurrenceSet os : getOccurrenceSets()) {
			sb.append(os);
			sb.append("\n");
		}
		
		sb.append("\n\n");
		try {
			sb.append("Coverage: "+getCoverage()+"\n");
			sb.append("Uncompressed length: "+getUncompressedLength()+"\n");
			sb.append("Encoding length: "+getEncodingLength()+"\n");
			sb.append("Compression factor: "+getCompressionFactor()+"\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.append("Number of occurrence sets: "+getOccurrenceSets().size()+"\n");
		
		return sb.toString();
	}
	
	public PointSet getCoveredSet() throws Exception {
		PointSet coveredSet = new PointSet();
		for(OccurrenceSet os : getOccurrenceSets())
			coveredSet.addAll(os.getCoveredSet());
		return coveredSet;
	}
	
	public int getEncodingLength() throws Exception {
		int length = 0;
		for(OccurrenceSet os : getOccurrenceSets())
			length += os.getEncodingLength();
		return length;
	}
	
	public int getDimensionality() {
		return getOccurrenceSets().get(0).getPattern().getDimensionality();
	}
	
	public double getCompressionFactor() throws Exception {
		return (1.0*getUncompressedLength())/getEncodingLength();
	}
	
}
