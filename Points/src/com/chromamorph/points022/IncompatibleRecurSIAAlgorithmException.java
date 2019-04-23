package com.chromamorph.points022;

public class IncompatibleRecurSIAAlgorithmException extends Exception {

	private static final long serialVersionUID = 8397670521823470735L;
	
	public IncompatibleRecurSIAAlgorithmException(Algorithm algorithm) {
		super("Incompatible algorithm requested for use with RecurSIA: "+algorithm.toString());
	}

}
