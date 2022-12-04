package com.chromamorph.maxtranpatsjava;

import java.io.PrintWriter;

public class LogInfo implements Comparable<LogInfo> {
	public static long PREV_TIME_STAMP = 0l;
	public static long ACCUMULATED_TIME = 0l;
	private long timeStamp;
	private String description;
	private long timeSincePrevTimeStamp = 0l;
	private long thisAccumulatedTime = 0l;
	
	public LogInfo(long timeStamp, String description, boolean printOut) {
		this.timeStamp = timeStamp;
		this.description = description;
		this.timeSincePrevTimeStamp = PREV_TIME_STAMP!=0l?(timeStamp - PREV_TIME_STAMP):0l;
		ACCUMULATED_TIME += timeSincePrevTimeStamp;
		thisAccumulatedTime = ACCUMULATED_TIME;
		PREV_TIME_STAMP = timeStamp;
		if (printOut) {
			System.out.println(this);
			System.out.flush();
		}
	}
	
	public long getAccumulatedTime() {
		return thisAccumulatedTime;
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
	
	public long getTimeSinceLastTimeStamp() {return timeSincePrevTimeStamp;}

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
		return String.format("%15d ms%15d ms  %s", thisAccumulatedTime, getTimeSinceLastTimeStamp(),getDescription());
	}
}

