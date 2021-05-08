package com.chromamorph.maxtranpatsjava;

public class NonIntegerDifferenceBetweenPointsException extends Exception {

	private static final long serialVersionUID = 1L;

	public NonIntegerDifferenceBetweenPointsException() {
		super();
	}
	
	public NonIntegerDifferenceBetweenPointsException(double x1, double x2) {
		super("Non-integer difference between x value: "+x1+", "+x2);
	}
	
	public NonIntegerDifferenceBetweenPointsException(String message) {
		super(message);
	}
}
