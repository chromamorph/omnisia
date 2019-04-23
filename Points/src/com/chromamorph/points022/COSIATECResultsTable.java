package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;

public class COSIATECResultsTable {
	public static String COSIATECOutputDirectoryPath = "/Users/dave/Documents/Work/Research/2015-06-17-workspace/Points/output/2015-11-09b";
	public static String resultsDirectoryPath = COSIATECOutputDirectoryPath+"/RESULTS";
	public static String csvFilePath = resultsDirectoryPath+"/"+"COSIATEC-results.csv";
	public static String latexFilePath = resultsDirectoryPath+"/"+"COSIATEC-results.tex";
	private static TreeSet<ResultsObject> resultObjects = new TreeSet<ResultsObject>();

	static class ResultsObject implements Comparable<ResultsObject>{
		private int numberOfTecs, encodingLength, numberOfNotes, numberOfResidualPoints, runningTime;
		private String name;

		public int getNumberOfTecs() {
			return numberOfTecs;
		}
		public void setNumberOfTecs(int numberOfTecs) {
			this.numberOfTecs = numberOfTecs;
		}
		public int getEncodingLength() {
			return encodingLength;
		}
		public void setEncodingLength(int encodingLength) {
			this.encodingLength = encodingLength;
		}
		public int getNumberOfNotes() {
			return numberOfNotes;
		}
		public void setNumberOfNotes(int numberOfNotes) {
			this.numberOfNotes = numberOfNotes;
		}
		public int getEncodingLengthNoRPS() {
			System.out.println(encodingLength+"-"+numberOfResidualPoints);
			return encodingLength-numberOfResidualPoints;
		}
		public int getNumberOfResidualPoints() {
			return numberOfResidualPoints;
		}
		public void setNumberOfResidualPoints(int numberOfResidualPoints) {
			this.numberOfResidualPoints = numberOfResidualPoints;
		}
		public int getRunningTime() {
			return runningTime;
		}
		public void setRunningTime(int runningTime) {
			this.runningTime = runningTime;
		}
		public double getCompressionRatio() {
			return 1.0* numberOfNotes/encodingLength;
		}
		public double getCompressionRatioNoRPS() {
			return 1.0* (numberOfNotes-numberOfResidualPoints)/(encodingLength-numberOfResidualPoints);
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public int compareTo(ResultsObject o) {
			if (o == null) return 1;
			return getName().compareTo(o.getName());
		}

		@Override
		public boolean equals(Object obj) {
			return compareTo((ResultsObject)obj) == 0;
		}

		public double getPercentageResidualPoints() {
			return 100.0 * getNumberOfResidualPoints()/getNumberOfNotes();
		}

		public String toString(String separator, String terminator) {
			return 	name + ","
					+ numberOfNotes  + separator
					+ encodingLength + separator
					+ getEncodingLengthNoRPS()  + separator
					+ String.format("%.2f",getCompressionRatio()) + separator
					+ String.format("%.2f",getCompressionRatioNoRPS()) + separator
					+ numberOfTecs + separator
					+ numberOfResidualPoints  + separator
					+ String.format("%.2f",getPercentageResidualPoints()) + separator
					+ runningTime
					+ terminator;

		}

	}

	private static void makeCSVFile() {
		try {
			new File(csvFilePath).getParentFile().mkdirs();
			PrintWriter csvFile = new PrintWriter(csvFilePath);
			csvFile.println("Name,Number of notes,Encoding length,Encoding length (no RPS),Compression factor,Compression factor (no RPS),Number of TECs,Number of RPs,% RPs,Runtime (ms)");
			for(ResultsObject r : resultObjects)
				csvFile.print(r.toString(",","\n"));
			csvFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private static void makeLatexFile() {

	}

	private static void makeListOfResultObjects() {
		try {
			String[] fileNames = new File(COSIATECOutputDirectoryPath).list();
			for(String fileName : fileNames) {
				if (fileName.endsWith(".log")) {
					BufferedReader br;
					br = new BufferedReader(new FileReader(COSIATECOutputDirectoryPath+"/"+fileName));
					String l;
					ResultsObject r = new ResultsObject();
					while (!(l = br.readLine()).startsWith("Input file:"));
					r.setName(l.substring(l.lastIndexOf("/")+1));
					while(!(l = br.readLine()).startsWith("Number of TECs:"));
					r.setNumberOfTecs(Integer.parseInt(l.substring(l.lastIndexOf(" ")+1)));
					while(!(l = br.readLine()).startsWith("Encoding length:"));
					r.setEncodingLength(Integer.parseInt(l.substring(l.lastIndexOf(" ")+1)));
					while(!(l = br.readLine()).startsWith("Number of notes:"));
					r.setNumberOfNotes(Integer.parseInt(l.substring(l.lastIndexOf(" ")+1)));
					while(!(l = br.readLine()).startsWith("Number and proportion of residual points:"));
					r.setNumberOfResidualPoints(Integer.parseInt(l.substring(l.lastIndexOf(": ")+2,l.indexOf(","))));
					while(!(l = br.readLine()).startsWith("Running time:"));
					r.setRunningTime(Integer.parseInt(l.substring(l.lastIndexOf(": ")+2,l.indexOf(" milliseconds"))));
					System.out.println(r.toString(", ", "\n"));
					br.close();
					resultObjects.add(r);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		makeListOfResultObjects();
		makeCSVFile();
		makeLatexFile();
	}
}
