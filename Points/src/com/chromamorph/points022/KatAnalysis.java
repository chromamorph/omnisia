package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class KatAnalysis {

	private static boolean diatonic = false;
	private static String inputMIDIPairFileDirectoryPathString = "/Users/dave/Documents/Work/Research/Data/Kat/Exp1midiFiles";
	private static String singleFileInputDirectoryPathString = "/Users/dave/Documents/Work/Research/Data/Kat/singleInputFiles";
	private static String compressedPairFileOutputDirectoryPathString = "/Users/dave/Documents/Work/Research/Data/Kat/output/pairs/"+(diatonic?"morphetic":"chromatic");
	private static String compressedSingleFileOutputDirectoryPathString = "/Users/dave/Documents/Work/Research/Data/Kat/output/singleFiles/"+(diatonic?"morphetic":"chromatic");
	private static String ncdFilePathName = "/Users/dave/Documents/Work/Research/Data/Kat/output/"+(diatonic?"morphetic":"chromatic")+"-ncds.txt";
	private static String omnisiaArgString = "-rrt -draw -a COSIATEC -nodate"+(diatonic?" -d ":" ");

	private static void compressPairFiles() {
		new File(compressedPairFileOutputDirectoryPathString).mkdirs();
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".mid");
			}

		};
		String[] pairFileNames = new File(inputMIDIPairFileDirectoryPathString).list(filter);
		for(String fn : pairFileNames) {
			//			String fn = "10aNormChNorm.mid";
			System.out.println(fn);
			String inputFilePathName = inputMIDIPairFileDirectoryPathString+"/"+fn;
			String[] a = (omnisiaArgString+"-i "+inputFilePathName+" -o "+compressedPairFileOutputDirectoryPathString).split(" ");
			try {
				OMNISIA.main(a);
			} catch (MissingTieStartNoteException e) {
				e.printStackTrace();
			}
		}

	}

	private static void generateSingleFiles() {
		new File(singleFileInputDirectoryPathString).mkdirs();
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".mid");
			}

		};
		String[] pairFileNames = new File(inputMIDIPairFileDirectoryPathString).list(filter);
		for(String pfn : pairFileNames) {
			PointSet ps;
			try {
				ps = new PointSet(inputMIDIPairFileDirectoryPathString+"/"+pfn);
				PointSet ps1 = ps.getSegment(0l, 3840l, false);
				PointSet ps2 = ps.getSegment(3840l, ps.getMaxX(), true).translate(new Vector(-3840,0));
				String pfnWithoutSuffix = pfn.substring(0,pfn.lastIndexOf("."));
				ps1.writeToPtsFile(singleFileInputDirectoryPathString+"/"+pfnWithoutSuffix+"-a.pts");
				ps2.writeToPtsFile(singleFileInputDirectoryPathString+"/"+pfnWithoutSuffix+"-b.pts");
			} catch (IOException | MissingTieStartNoteException e) {
				e.printStackTrace();
			}
		}
	}

	private static void compressSingleFiles() {
		new File(compressedSingleFileOutputDirectoryPathString).mkdirs();
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".pts");
			}

		};
		String[] singleFileNames = new File(singleFileInputDirectoryPathString).list(filter);
		for(String fn : singleFileNames) {
			System.out.println(fn);
			String inputFilePathName = singleFileInputDirectoryPathString+"/"+fn;
			String[] a = (omnisiaArgString+"-i "+inputFilePathName+" -o "+compressedSingleFileOutputDirectoryPathString).split(" ");
			OMNISIA.main(a);
		}
	}

	private static int getIntValueFromLogFile(String key, String filePathName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePathName));
			String l;
			while((l = br.readLine()) != null)
				if (l.startsWith(key))
					break;
			br.close();
			return Integer.parseInt(l.substring(key.length()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void computeNCDs() {
		try {
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return dir.toPath().resolve(name).toFile().isDirectory() && name.endsWith("-mid");
				}

			};
			String[] compressedPairFileNames = new File(compressedPairFileOutputDirectoryPathString).list(filter);
			double ncd;
			PrintWriter pw = new PrintWriter(ncdFilePathName);
			for(String fn : compressedPairFileNames) {
				int end = fn.indexOf("-mid");
				String pairFilePrefix = fn.substring(0,end);
				int length = getIntValueFromLogFile("Encoding length: ",compressedPairFileOutputDirectoryPathString+"/"+fn+"/"+pairFilePrefix+"-"+(diatonic?"diat":"chrom")+".log");
				int length1 = getIntValueFromLogFile("Encoding length: ",compressedSingleFileOutputDirectoryPathString+"/"+pairFilePrefix+"-a-pts/"+pairFilePrefix+"-a-"+(diatonic?"diat":"chrom")+".log");
				int length2 = getIntValueFromLogFile("Encoding length: ",compressedSingleFileOutputDirectoryPathString+"/"+pairFilePrefix+"-b-pts/"+pairFilePrefix+"-b-"+(diatonic?"diat":"chrom")+".log");
				ncd = 1.*(length - Math.min(length1, length2))/Math.max(length1, length2);
				pw.println(fn+"\t"+length+"\t"+length1+"\t"+length2+"\t"+ncd);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}



	}

	public static void main(String[] args) {
		compressPairFiles();
		//				generateSingleFiles();
		compressSingleFiles();
		computeNCDs();
	}
}
