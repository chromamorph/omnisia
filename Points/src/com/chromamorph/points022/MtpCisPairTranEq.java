package com.chromamorph.points022;

public class MtpCisPairTranEq implements Comparable<MtpCisPairTranEq> {

	private MtpCisPair mtpCisPair;

	public MtpCisPairTranEq(MtpCisPair mtpCisPair) {
		this.mtpCisPair = mtpCisPair;
	}

	public MtpCisPair getMtpCisPair() {
		return mtpCisPair;
	}
	
	@Override
	public int compareTo(MtpCisPairTranEq mcpte) {
		if (mcpte == null) return 1;
		if(mcpte.getMtpCisPair().getMtp().translationallyEquivalentTo(getMtpCisPair().getMtp()))
			return 0;
		return getMtpCisPair().compareTo(mcpte.getMtpCisPair());
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof MtpCisPairTranEq)) return false;
		return compareTo((MtpCisPairTranEq)obj) == 0;
	}

}
