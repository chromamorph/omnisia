package com.chromamorph.pointsMTECMCM;

import java.util.ArrayList;
import java.util.TreeSet;

public class TEC {
	private PointSet pointSet = null;
	private ArrayList<Integer> cis = null;
	private VectorSet translators = new VectorSet();
	private Double compressionRatio = null;
	private Integer coverage = null;
	private PointSet coveredPoints = null;
	private Double compactness = null;
	private Integer numPointsInBB = null;
	private PointSet points = null;
	private boolean isDual = false;

	public TEC() {}
	
	public TEC(PointSet pointSet, ArrayList<Integer> cis, PointSet dataSet) {
		this.pointSet = pointSet;
		this.cis = cis;
		points = dataSet;
	}

	public boolean isMTEC() {
		return pointSet.isMTEC();
	}
	
	public boolean isDual() {
		return isDual;
	}
	
	public void setDual(boolean dual) {
		isDual = dual;
	}
	
	public ArrayList<Integer> getCIS() {
		return cis;
	}
	
	public PointSet getPointSet() {
		return pointSet;
	}
	
	public String toString() {
		return pointSet.size()+" : "+pointSet+" : "+cis+" : "+translators;
	}

	public int getPatternSize() {
		if (pointSet == null) return 0;
		return pointSet.size();
	}
	
	public int getTranslatorSetSize() {
		return translators.size();
	}
	
	public double getCompressionRatio() {
		if (compressionRatio != null) return compressionRatio;
		return (compressionRatio = (1.0*getCoverage())/(getPatternSize()+getTranslatorSetSize()-1));
	}

	public PointSet getCoveredPoints() {
		if (coveredPoints == null) {
			coveredPoints = new PointSet();
			TreeSet<Point> points = pointSet.getPoints();
			TreeSet<Vector> vectors = translators.getVectors();
			for(Point point : points)
				for(Vector vector : vectors)
					coveredPoints.add(point.translate(vector));
		}
		return coveredPoints;
	}
	
	public int getCoverage() {
		if (coverage != null) return coverage;
		coveredPoints = getCoveredPoints();
		return (coverage = coveredPoints.size());
	}
	
	public int getNumPointsInBB() {
		if (numPointsInBB != null) return numPointsInBB;
		return (numPointsInBB = points.getBBSubset(pointSet.getTopLeft(),pointSet.getBottomRight()).size());
	}
	
	public double getCompactness() {
		if (compactness != null) return compactness;
		return (compactness = (1.0 * getPatternSize())/getNumPointsInBB());
	}

	public PointSet getPattern() {
		return pointSet;
	}
	
	public VectorSet getTranslators() {
		return translators;
	}
	
	public int getBBArea() {
		return getPattern().getBBArea();
	}
	
	public TEC getDual() {
		TEC dual = new TEC();
		dual.pointSet = new PointSet();
		Point firstPoint = pointSet.first();
		dual.pointSet.add(firstPoint);
		for(Vector v : translators.getVectors())
			dual.pointSet.add(firstPoint.translate(v));
		for(Point p : pointSet.getPoints())
			dual.translators.add(new Vector(firstPoint,p));
		dual.coverage = coverage;
		dual.compressionRatio = compressionRatio;
		dual.coveredPoints = coveredPoints;
		dual.points = points;
		dual.setDual(true);
		return dual;
	}
}
