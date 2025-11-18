package com.chromamorph.maxtranpatsjava;

/**
 * Models a row in the sperm-whale-dialogues file.
 * 	 * 	REC			string
	 * 	nClicks		int
	 * 	Duration	double
	 * 	ICI1-28		double
	 *  Whale		int
	 *  TsTo		double

 */
public class Coda {
	private String rec;
	private int nClicks;
	private double duration;
	private double[] icis = new double[28];
	private int whale;
	private double tsTo;
	
	public String getRec() {
		return rec;
	}
	public void setRec(String rec) {
		this.rec = rec;
	}
	public int getnClicks() {
		return nClicks;
	}
	public void setnClicks(int nClicks) {
		this.nClicks = nClicks;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public double getIci(int i) {
		return icis[i];
	}
	public void setIci(int i, double ici) {
		icis[i] = ici;
	}
	public void setWhale(int i) {
		whale = i;
	}
	public int getWhale() {
		return whale;
	}
	public double getTsTo() {
		return tsTo;
	}
	public void setTsTo(double tsTo) {
		this.tsTo = tsTo;
	}
	
	public PointSet getPointSet() {
		PointSet ps = new PointSet();
		double onset = getTsTo();
		ps.add(new Point(onset,(double)getWhale()));
		for(double ici : icis) {
			if (ici != 0.0)
				ps.add(new Point((onset = onset + ici),(double)getWhale()));
			else
				 break;
		}
		return ps;
	}
	
	public Coda(String line) {
		String[] a = line.split(",");
		setRec(a[0].trim().replace('_', '-'));
		setnClicks(Integer.parseInt(a[1]));
		setDuration(Double.parseDouble(a[2]));
		for(int i = 0; i < 28; i++) {
			setIci(i,Double.parseDouble(a[i+3]));
		}
		setWhale(Integer.parseInt(a[31]));
		setTsTo(Double.parseDouble(a[32]));
	}
	
	public String getGTString() {
		StringBuilder sb = new StringBuilder("(\n");
		sb.append("  (label "+getTsTo()+"-"+getWhale()+")\n");
		PointSet ps = getPointSet();
		for(Point p : ps.getPoints()) {
			sb.append("  (");
			int k = p.getCoords().size();
			for(int i = 0; i < k; i++) {
				sb.append(String.format("%f", p.get(i)));
				if (i != k - 1)
					sb.append(" ");
			}
			sb.append(")\n");
		}
		sb.append(")");
		return sb.toString();
	}
}
