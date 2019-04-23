package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;

public class MIREX2016MoveOutputFiles {
	
	public static void main(String[] args) {
		String targetOutputFolder = "/Users/dave/Documents/Work/Research/Data/MIREX2016/mirex-output-siatecCompress";
		String[] folderList = new File(targetOutputFolder).list();
		for(String s : folderList)
			System.out.println("fullfile(outputDir, \'"+s+"\'),...");
	}
	
	public static void main2(String[] args) throws IOException {
		String sourceOutputFolder = "/Users/dave/Documents/Work/Research/Data/MIREX2016/output-siatecCompress";
		String targetOutputFolder = "/Users/dave/Documents/Work/Research/Data/MIREX2016/mirex-output-siatecCompress";

		String[] sourceOutputFolderFileList = new File(sourceOutputFolder).list();
		for(String s : sourceOutputFolderFileList) System.out.println(s);
		for(String algFolder : sourceOutputFolderFileList) {
			if (algFolder.startsWith(".")) continue;
			System.out.println(algFolder);
			String sourceAlgFolderPath = sourceOutputFolder+"/"+algFolder;
			File sourceAlgFolder = new File(sourceAlgFolderPath);
			String targetAlgFolderPath = targetOutputFolder+"/"+algFolder;
			File targetAlgFolder = new File(targetAlgFolderPath);
			targetAlgFolder.mkdirs();
			String[] pieceFolders = sourceAlgFolder.list();
			for(String pieceFolder : pieceFolders) {
				System.out.println("  "+pieceFolder);
				String sourcePieceFolderPath = sourceAlgFolderPath+"/"+pieceFolder;
				String sourcePieceFileName = null;
				if (algFolder.startsWith("COSIATEC")) {
					sourcePieceFileName = pieceFolder.substring(0,pieceFolder.length()-4)+"-diat.cos";
					FileReader sourcePieceFile = new FileReader(sourcePieceFolderPath+"/"+sourcePieceFileName);
					FileWriter targetPieceFile = new FileWriter(targetAlgFolderPath+"/"+getTargetPieceFileName(sourcePieceFileName));
					IOUtils.copy(sourcePieceFile, targetPieceFile);
					sourcePieceFile.close();
					targetPieceFile.close();
//					Runtime file
					String sourceLogFileName = pieceFolder.substring(0,pieceFolder.length()-4)+"-diat.log";
					BufferedReader logFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourceLogFileName));
					PrintWriter runtimeFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetRuntimeFileName(sourcePieceFileName)));
					String l = null;
					while ((l = logFile.readLine()) != null)
						if (l.startsWith("Running time: ")) {
							String[] a = l.split(" ");
							runtimeFile.println("runtime "+a[2]);
							runtimeFile.println("FRT 0");
							break;
						}
					logFile.close();
					runtimeFile.close();
				} else if (algFolder.startsWith("Forth")) {
					String[] algFolderFileList = new File(sourcePieceFolderPath).list();
					for(String algFolderFile : algFolderFileList)
						if (algFolderFile.endsWith(".Forth")) {
							sourcePieceFileName = algFolderFile;
							break;
						}	
					FileReader sourcePieceFile = new FileReader(sourcePieceFolderPath+"/"+sourcePieceFileName);
					FileWriter targetPieceFile = new FileWriter(targetAlgFolderPath+"/"+getTargetPieceFileName(sourcePieceFileName));
					IOUtils.copy(sourcePieceFile, targetPieceFile);
					sourcePieceFile.close();
					targetPieceFile.close();
//					Runtime file
					String sourceLogFileName = pieceFolder.substring(0,pieceFolder.length()-4)+".alltecs";
					BufferedReader logFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourceLogFileName));
					PrintWriter runtimeFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetRuntimeFileName(sourcePieceFileName)));
					String l = null;
					while ((l = logFile.readLine()) != null)
						if (l.startsWith("Running time (ms): ")) {
							String[] a = l.split(" ");
							runtimeFile.println("runtime "+a[3]);
							runtimeFile.println("FRT 0");
							break;
						}
					logFile.close();
					runtimeFile.close();
				} else if (algFolder.startsWith("SIATECCompress")) {
					sourcePieceFileName = pieceFolder.substring(0,pieceFolder.length()-4)+".SIATECCompress";
					BufferedReader sourcePieceFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourcePieceFileName));
					PrintWriter targetPieceFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetPieceFileName(sourcePieceFileName)));
					String l = null;
					while ((l = sourcePieceFile.readLine())!= null && !l.isEmpty())
						targetPieceFile.println(l);
					sourcePieceFile.close();
					targetPieceFile.close();
//					Runtime file
					String sourceLogFileName = pieceFolder.substring(0,pieceFolder.length()-4)+".log";
					BufferedReader logFile = new BufferedReader(new FileReader(sourcePieceFolderPath+"/"+sourceLogFileName));
					PrintWriter runtimeFile = new PrintWriter(new File(targetAlgFolderPath+"/"+getTargetRuntimeFileName(sourcePieceFileName)));
					l = null;
					while ((l = logFile.readLine()) != null)
						if (l.startsWith("Running time: ")) {
							String[] a = l.split(" ");
							runtimeFile.println("runtime "+a[2]);
							runtimeFile.println("FRT 0");
							break;
						}
					logFile.close();
					runtimeFile.close();
				}
			}
		}
	}

	public static String getTargetPieceFileName(String sourcePieceFileName) {
		if (sourcePieceFileName.startsWith("mazurka")) return "chop_mazurka24-4.txt";
		if (sourcePieceFileName.startsWith("silverswan")) return "gbns_silverswan.txt";
		if (sourcePieceFileName.startsWith("sonata01-3")) return "beet_sonata01-3.txt";
		if (sourcePieceFileName.startsWith("sonata04-2")) return "mzrt_sonata04-2.txt";
		if (sourcePieceFileName.startsWith("wtc2f20")) return "bach_wtc2f20.txt";
		return null;
	}
	
	public static String getTargetRuntimeFileName(String sourcePieceFileName) {
		if (sourcePieceFileName.startsWith("mazurka")) return "chop_mazurka24-4_runtime.txt";
		if (sourcePieceFileName.startsWith("silverswan")) return "gbns_silverswan_runtime.txt";
		if (sourcePieceFileName.startsWith("sonata01-3")) return "beet_sonata01-3_runtime.txt";
		if (sourcePieceFileName.startsWith("sonata04-2")) return "mzrt_sonata04-2_runtime.txt";
		if (sourcePieceFileName.startsWith("wtc2f20")) return "bach_wtc2f20_runtime.txt";
		return null;
	}
}
