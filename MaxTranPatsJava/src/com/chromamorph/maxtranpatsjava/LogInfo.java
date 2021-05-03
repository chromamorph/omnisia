package com.chromamorph.maxtranpatsjava;

import java.io.PrintWriter;

public class LogInfo implements Comparable<LogInfo> {
	private long timeStamp;
	private String description;
	
	public LogInfo(long timeStamp, String description, boolean printOut) {
		this.timeStamp = timeStamp;
		this.description = description;
		if (printOut) {
			System.out.println(this);
			System.out.flush();
		}
	}
	
	public LogInfo(String description, PrintWriter outputFile) {
		this(description, false);
		outputFile.println(this);
		outputFile.flush();
	}
	
	public LogInfo(String description, boolean printOut) {
		this(System.currentTimeMillis(), description, printOut);
	}
	
	public long getTimeStamp() {return timeStamp;}
	public String getDescription() {return description;}

	@Override
	public int compareTo(LogInfo o) {
		if (o == null) return 1;
		long dLong = getTimeStamp()-o.getTimeStamp();
		if (dLong != 0l) return (dLong < 0l? -1 : 1);
		return getDescription().compareTo(o.getDescription());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof LogInfo)) return false;
		return compareTo((LogInfo) obj) == 0;
	}
	
	@Override
	public String toString() {
		return String.format("%d ms:\t%s", getTimeStamp(),getDescription());
	}
}

