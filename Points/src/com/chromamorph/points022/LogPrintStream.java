package com.chromamorph.points022;

import java.io.PrintStream;

public class LogPrintStream {
	
	public static void println(PrintStream logPrintStream) {
		System.out.println();
		if (logPrintStream != null) logPrintStream.println();
	}
	
	public static void println(PrintStream logPrintStream, Object s) {
		System.out.println(s);
		if (logPrintStream != null) logPrintStream.println(s);
	}

	public static void print(PrintStream logPrintStream, Object s) {
		System.out.print(s);
		if (logPrintStream != null) logPrintStream.print(s);
	}


}
