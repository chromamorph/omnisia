package com.chromamorph.maxtranpatsjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

public class MML2024Results {
	
	static class Datum implements Comparable<Datum>{
		double f1, p, r, minoc;
		long runningTime;
		boolean midTimePoint;
		int minPatternSize;
		String inputFilePath, queryFilePath, groundTruthFilePath, outputDir;
		
		@Override
		public int compareTo(Datum o) {
			if (o == null) return -1;
			double d = 0;
			if (Double.isNaN(f1) && Double.isNaN(o.f1))
				d = 0;
			if (Double.isNaN(f1))
				return 1;
			if (Double.isNaN(o.f1))
				return -1;
			d = o.f1 - f1;
			if (d != 0.0)
				return (int)Math.signum(d);
			
			if (Double.isNaN(p) && Double.isNaN(o.p))
				d = 0;
			if (Double.isNaN(p))
				return 1;
			if (Double.isNaN(o.p))
				return -1;
			d = o.p - p;
			if (d != 0.0)
				return (int)Math.signum(d);
			
			if (Double.isNaN(r) && Double.isNaN(o.r))
				d = 0;
			if (Double.isNaN(r))
				return 1;
			if (Double.isNaN(o.r))
				return -1;
			d = o.r - r;
			if (d != 0.0)
				return (int)Math.signum(d);
			
			d = o.minoc - minoc;
			if (d != 0.0) 
				return (int)Math.signum(d);
			
			int dint = o.minPatternSize - minPatternSize;
			if (dint != 0) return dint;
			
			long dl = o.runningTime - runningTime;
			if (dl != 0l) return (dl > 0l?1:-1);
			
			dint = o.inputFilePath.compareTo(inputFilePath);
			if (dint != 0) return dint;
			
			dint = o.queryFilePath.compareTo(queryFilePath);
			if (dint != 0) return dint;
			
			dint = o.groundTruthFilePath.compareTo(groundTruthFilePath);
			if (dint != 0) return dint;
			
			dint = o.outputDir.compareTo(outputDir);
			if (dint != 0) return dint;
			
			return 0;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj instanceof Datum)) return false;
			return compareTo((Datum)obj) == 0.0;
		}
		
		public Datum(File evalFile, File mtmFile) throws IOException {
			BufferedReader evalFileReader = new BufferedReader(new FileReader(evalFile));
			String line;
			outputDir = evalFile.getParent();
			while ((line = evalFileReader.readLine()) != null) {
				if (line.startsWith("2-layer F1:")) {
					String[] a = line.split(" ");
					f1 = Double.parseDouble(a[2]); 
				} else if (line.startsWith("2-layer P:")) {
					String[] a = line.split(" ");
					p = Double.parseDouble(a[2]); 
				} else if (line.startsWith("2-layer R:")) {
					String[] a = line.split(" ");
					r = Double.parseDouble(a[2]); 
				}

			}
			evalFileReader.close();
			
			BufferedReader mtmFileReader = new BufferedReader(new FileReader(mtmFile));
			while ((line = mtmFileReader.readLine())!= null) {
				if (line.startsWith("Input file path (-i):")) {
					String[] a = line.split(" ");
					inputFilePath = a[4]; 
				} else if (line.startsWith("Query file path (-q):")) {
					String[] a = line.split(" ");
					queryFilePath = a[4];
				} else if (line.startsWith("Ground-truth file path (-gt):")) {
					String[] a = line.split(" ");
					groundTruthFilePath = a[4];
				} else if (line.startsWith("Mid-time point (-mt):")) {
					String[] a = line.split(" ");
					midTimePoint = Boolean.parseBoolean(a[3].toLowerCase());
				} else if (line.startsWith("Minimum pattern size (-min):")) {
					String[] a = line.split(" ");
					minPatternSize = Integer.parseInt(a[4]);
				} else if (line.startsWith("Minimum occurrence compactness (-minoc):")) {
					String[] a = line.split(" ");
					minoc = Double.parseDouble(a[4]);
				} else if (line.startsWith("Running time in milliseconds:")) {
					String[] a = line.split(" ");
					runningTime = Long.parseLong(a[4]);
				}
			}
			mtmFileReader.close();
		}
		public static String tableHeaders() {
			return "Output dir, Input file, Query file, Ground truth file, Min size, Min occ comp, Mid time point, Running time, F1, P, R";
		}
		public String toString() {
			return String.format("%s,%s,%s,%s,%d,%f,%b,%d,%f,%f,%f",
					outputDir,
					inputFilePath,
					queryFilePath,
					groundTruthFilePath,
					minPatternSize,
					minoc,
					midTimePoint,
					runningTime,
					f1, p, r);
			
		}
		
	}
	
	public static ArrayList<File> makeListOfFiles(String rootDirPath, final String suffix) {
		ArrayList<File> files = new ArrayList<File>();
		File rootDir = new File(rootDirPath);
		File[] fileArray = rootDir.listFiles();
		for(File f : fileArray) {
			if (f.isDirectory())
				files.addAll(makeListOfFiles(f.getAbsolutePath(), suffix));
			else if (f.getAbsolutePath().endsWith(suffix))
				files.add(f);
		}
		return files;
	}
	
	public static void main(String[] args) {
//		Find best result on Contrapunctus
//		String rootDir = "output/MML2024/ContrapunctusVI";
//		String rootDir = "output/MML2024/Ravel/Soucy";
		String rootDir = "output/ICCCM2024/Ravel/DM";
		TreeSet<Datum> data = new TreeSet<Datum>();
		ArrayList<File> evalFiles =  makeListOfFiles(rootDir, "eval");
		ArrayList<File> mtmFiles = makeListOfFiles(rootDir, "mtm");
		for(int i = 0; i < evalFiles.size(); i++) {
			try {
				System.out.println("Eval file: "+evalFiles.get(i));
				System.out.println("MTM file: "+ mtmFiles.get(i));
				Datum datum = new Datum(evalFiles.get(i),mtmFiles.get(i));
				data.add(datum);
				System.out.println(datum);
				System.out.println();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		PrintWriter resultsFile;
		try {
			resultsFile = new PrintWriter(rootDir+"/results.csv");
			resultsFile.println(Datum.tableHeaders());
			for( Datum d : data) {
				resultsFile.println(d);
			}
			resultsFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
