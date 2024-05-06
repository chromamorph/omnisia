package com.chromamorph.points022;

public class MtpCisPairTranEq implements Comparable<MtpCisPairTranEq> {

	private MtpCisPair mtpCisPair;
	private int morphOrChroma = 0;

	public MtpCisPairTranEq(MtpCisPair mtpCisPair, int morphOrChroma) {
		this.mtpCisPair = mtpCisPair;
		this.morphOrChroma = morphOrChroma;
	}

	public MtpCisPair getMtpCisPair() {
		return mtpCisPair;
	}
	
	@Override
	public int compareTo(MtpCisPairTranEq mcpte) {
		if (mcpte == null) return 1;
		if(mcpte.getMtpCisPair().getMtp().translationallyEquivalentTo(getMtpCisPair().getMtp(), morphOrChroma))
			return 0;
		return getMtpCisPair().compareTo(mcpte.getMtpCisPair());
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof MtpCisPairTranEq)) return false;
		return compareTo((MtpCisPairTranEq)obj) == 0;
	}

}
