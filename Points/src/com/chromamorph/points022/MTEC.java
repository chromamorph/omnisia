package com.chromamorph.points022;

import java.util.ArrayList;

public class MTEC extends TEC {
	
	private VectorSet vMax = new VectorSet();
	
	public MTEC() {
		super();
	}
	
	public MTEC(TEC tec) {
		this();
		setPattern(tec.getPattern());
		setDataset(tec.getDataset());
		setTranslators(tec.getTranslators());
		setDual(tec.isDual);
		setPatternTecs(tec.getPatternTECs());
		cis = tec.getCIS();
		reset();
	}
	
	public MTEC copy() {
		MTEC newTEC = new MTEC();
		newTEC.cis = null;
		if (cis != null) {
			newTEC.cis = new ArrayList<Integer>();
			for(Integer i : cis) newTEC.cis.add(i);
		}
		newTEC.dataset = dataset.copy();
		newTEC.isDual = isDual;
		newTEC.pattern = pattern.copy();
		newTEC.patternTecs = null;
		if (patternTecs != null) {
			newTEC.patternTecs = new ArrayList<TEC>();
			for(TEC patternTec : patternTecs)
				newTEC.patternTecs.add(patternTec.copy());
		}
		newTEC.translators = translators.copy();
		newTEC.vMax = vMax.copy();
		newTEC.reset();
		return newTEC;
	}
	
	public MTEC(PointSet pattern, PointSet dataset, VectorSet vMax) {
		super(pattern, dataset);
		this.vMax = vMax;
	}
	
	public MTEC(PointSet pattern, PointSet dataset) {
		super(pattern,dataset);
	}
	
	public MTEC(PointSet pointSet, ArrayList<Integer> cis, PointSet dataSet, VectorSet vMax) {
		super(pointSet, cis, dataSet);
		this.vMax = vMax;
		
	}

	public MTEC(PointSet pointSet, ArrayList<Integer> cis, PointSet dataSet) {
		super(pointSet, cis, dataSet);
	}
	
	public MTEC(PointSet pattern, VectorSet translators, PointSet dataset, VectorSet vMax) {
		super(pattern, translators, dataset);
		this.vMax = vMax;
	}

	public MTEC(PointSet pattern, VectorSet translators, PointSet dataset) {
		super(pattern, translators, dataset);
	}

	public MTEC(ArrayList<TEC> patternTecs, VectorSet translators, PointSet dataset, VectorSet vMax) {
		super(patternTecs, translators, dataset);
		this.vMax = vMax;
	}
	
	public MTEC(ArrayList<TEC> patternTecs, VectorSet translators, PointSet dataset) {
		super(patternTecs, translators, dataset);
	}

	public void addMaxVector(Vector v) {
		vMax.add(v);
	}
	
	public void addAllMaxVectors(VectorSet vectorSet) {
		vMax.addAll(vectorSet);
	}
	
	public VectorSet getMaxVectors() {
		return vMax;
	}

	public String toString() {
		String outString = null;
		if (pattern != null) { // In this TEC, the pattern is just a point set
			PointSet normalizedPattern = pattern.translate(translators.get(0));
			VectorSet normalizedTranslators = translators.translate(translators.get(0).inverse());
			outString = "MT("+normalizedPattern+","+normalizedTranslators+","+getMaxVectors()+")";
		}
		if (patternTecs != null) { // In this TEC, the pattern is represented by a set of TECs
			outString = "T(P("+patternTecs.get(0);
			for(int i = 1; i < patternTecs.size(); i++) {
				outString += ","+patternTecs.get(i);
			}
			outString += "),"+getTranslators()+","+getMaxVectors()+")";
		}
		return outString;
	}
}
