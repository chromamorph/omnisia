package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class RunCosiatecPlusBzip2OnNlb {

	static String cosiatecNlbDirectoryPath = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB/COSIATEC/NLB";
	static String cosiatecPlusBzipNlbDirectoryPath = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB/COSIATEC+BZIP2/NLB";
	static String[] cosiatecNlbFileNames = new File(cosiatecNlbDirectoryPath).list();
	static String cosiatecNlbPairsDirectoryPath = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB/COSIATEC/NLB-PAIRS";
	static String cosiatecPlusBzipNlbPairsDirectoryPath = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB/COSIATEC+BZIP2/NLB-PAIRS";
	static String[] cosiatecNlbPairFileNames = new File(cosiatecNlbPairsDirectoryPath).list();

	private static void createLogFile(String cosFileName, String outputDirectoryPath, String pointSetDirectoryPath, String bzipLogFileExtension, String pointSetFileExtension) {
		String fileName = cosFileName.substring(0,cosFileName.lastIndexOf('.'));

		//Find number of points in PointSet from ".log.COSIATEC" file
		String logCOSIATECFileName = fileName + ".log.COSIATEC";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(outputDirectoryPath+"/"+logCOSIATECFileName));
			String s = "taken to analyse ";
			String l = br.readLine();
			while (!l.contains(s))
				l = br.readLine();
			int start = l.indexOf(s)+s.length();
			int end = l.indexOf("points")-1;
			int numPointsInPointSet = Integer.parseInt(l.substring(start,end));

			//Find COSIATEC encoding length in vectors/points
			s = "Encoding length: ";
			while (!l.startsWith(s)) l = br.readLine();
			start = s.length();
			int cosiatecEncodingLength = Integer.parseInt(l.substring(start));

			//Find compression ratio from PointSet to COSIATEC encoding
			s = "Compression ratio: ";
			while (!l.startsWith(s)) l = br.readLine();
			start = s.length();
			double crFromPointSetToCosiatec = Double.parseDouble(l.substring(start));
			br.close();

			//Find COSIATEC file length in bytes
			String cosiatecFileName = fileName + ".cos";
			long cosiatecFileLengthInBytes = new File(outputDirectoryPath+"/"+cosiatecFileName).length();

			//Find BZIP file length in bytes
			String bzipFileName = fileName + ".cos.bz2";
			long bzipFileLengthInBytes = new File(outputDirectoryPath+"/"+bzipFileName).length();

			//Find compression ratio from COSIATEC to BZIP
			double crFromCosiatecToBzip = (1.0 * cosiatecFileLengthInBytes)/bzipFileLengthInBytes;

			//Find compression ratio from PointSet to Bzip vectors/points then bytes
			double crFromPointSetToBzipVectorsBytes = crFromPointSetToCosiatec * crFromCosiatecToBzip;

			//Find length of PointSet file in bytes
			String pointSetFileName = pointSetDirectoryPath + "/" + fileName + pointSetFileExtension;
			long pointSetFileLengthInBytes = new File(pointSetFileName).length();

			//Find compression ratio from PointSet to Bzip just in bytes
			double crFromPointSetToBzipBytes = (1.0 * pointSetFileLengthInBytes)/bzipFileLengthInBytes;

			//Find compression ratio from PointSet to Bzip without COSIATEC
			//Need to look in log file for BZIP2 algorithm - which is the same as pointSetDirectoryPath
			String bzipLogFile = pointSetDirectoryPath + "/" + fileName + bzipLogFileExtension;
			br = new BufferedReader(new FileReader(bzipLogFile));
			l = br.readLine();
			s = "Compression ratio: ";
			while (!l.startsWith(s)) l = br.readLine();
			br.close();
			start = s.length();
			double crFromPointSetToBzipWithoutCosiatec = Double.parseDouble(l.substring(start));
			
			String logFileName = fileName + ".log";
			PrintStream ps = new PrintStream(outputDirectoryPath+"/"+logFileName);
			ps.println("Number of points in PointSet: "+numPointsInPointSet);
			ps.println("COSIATEC encoding length in vectors/points: "+cosiatecEncodingLength);
			ps.println("COSIATEC file length in bytes: "+cosiatecFileLengthInBytes);
			ps.println("BZIP file length in bytes: "+bzipFileLengthInBytes);
			ps.println("Compression ratio from PointSet to BZIP (vectors/points, then bytes): "+crFromPointSetToBzipVectorsBytes);
			ps.println("PointSet file length in bytes: "+pointSetFileLengthInBytes);
			ps.println("Compression ratio from PointSet to BZIP (in bytes): "+crFromPointSetToBzipBytes);
			ps.println("Compression ratio from PointSet to COSIATEC: "+crFromPointSetToCosiatec);
			ps.println("Compression ratio from COSIATEC to BZIP: "+crFromCosiatecToBzip);
			ps.println("Compression ratio from PointSet to BZIP without COSIATEC: "+crFromPointSetToBzipWithoutCosiatec);
			ps.close();
			System.out.println("Log file created: "+outputDirectoryPath+"/"+logFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {


		//Copy all .cos and .log files for single files from COSIATEC to COSIATEC+BZIP2
		//		
		//		for(String cosiatecNlbFileName : cosiatecNlbFileNames) {
		//			if (cosiatecNlbFileName.endsWith(".cos")) {
		//				try {
		//					FileUtils.copyFileToDirectory(new File(cosiatecNlbDirectoryPath+"/"+cosiatecNlbFileName), 
		//							new File(cosiatecPlusBzipNlbDirectoryPath));
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			} else if (cosiatecNlbFileName.endsWith(".log")) {
		//				File srcFile = new File(cosiatecNlbDirectoryPath+"/"+cosiatecNlbFileName);
		//				File destFile = new File(cosiatecPlusBzipNlbDirectoryPath+"/"+cosiatecNlbFileName+".COSIATEC");
		//				try {
		//					FileUtils.copyFile(srcFile, destFile);
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}

		//Rename all files in NLB directory so that have short form 
		//(e.g., "NLB167193_01-diat-2014-1-18-22-42-59.log.COSIATEC" 
		//becomes "NLB167193_01.log.COSIATEC")

		//		String[] cosiatecPlusBzipNlbFileNames = new File(cosiatecPlusBzipNlbDirectoryPath).list();
		//		for(String nlbFileName : cosiatecPlusBzipNlbFileNames) {
		//			if (!nlbFileName.startsWith(".")) {
		//				String srcFilePath = cosiatecPlusBzipNlbDirectoryPath + "/" + nlbFileName;
		//				System.out.println(srcFilePath);
		//				int start = nlbFileName.indexOf(".");
		//				String destFilePath = cosiatecPlusBzipNlbDirectoryPath + "/" + nlbFileName.substring(0,12)+nlbFileName.substring(start);
		//				File srcFile = new File(srcFilePath);
		//				File destFile = new File(destFilePath);
		//				try {
		//					FileUtils.moveFile(srcFile, destFile);
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}

		//		Now do the same thing for the NLB-PAIRS directory

		//		String[] cosiatecPlusBzipNlbPairFileNames = new File(cosiatecPlusBzipNlbPairsDirectoryPath).list();
		//		for(String nlbPairFileName : cosiatecPlusBzipNlbPairFileNames) {
		//			if (!nlbPairFileName.startsWith(".")) {
		//				String srcFilePath = cosiatecPlusBzipNlbPairsDirectoryPath + "/" + nlbPairFileName;
		//				System.out.println(srcFilePath);
		//				int start = nlbPairFileName.indexOf(".","NLB015569_01+NLB070033_01.m".length());
		//				String destFilePath = cosiatecPlusBzipNlbPairsDirectoryPath + "/" + nlbPairFileName.substring(0,"NLB015569_01+NLB070033_01".length())+nlbPairFileName.substring(start);
		//				File srcFile = new File(srcFilePath);
		//				File destFile = new File(destFilePath);
		//				try {
		//					FileUtils.moveFile(srcFile, destFile);
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}


		//Copy all .cos and .log files for pair files from COSIATEC to COSIATEC+BZIP2
		//		
		//		for(String cosiatecNlbPairFileName : cosiatecNlbPairFileNames) {
		//			if (cosiatecNlbPairFileName.endsWith(".cos")) {
		//				try {
		//					FileUtils.copyFileToDirectory(new File(cosiatecNlbPairsDirectoryPath+"/"+cosiatecNlbPairFileName), 
		//							new File(cosiatecPlusBzipNlbPairsDirectoryPath));
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			} else if (cosiatecNlbPairFileName.endsWith(".log")) {
		//				File srcFile = new File(cosiatecNlbPairsDirectoryPath+"/"+cosiatecNlbPairFileName);
		//				File destFile = new File(cosiatecPlusBzipNlbPairsDirectoryPath+"/"+cosiatecNlbPairFileName+".COSIATEC");
		//				try {
		//					FileUtils.copyFile(srcFile, destFile);
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}

		//Compress all .cos files in NLB directory
		//		for(String cosiatecNlbFileName : cosiatecNlbFileNames) {
		//			if (cosiatecNlbFileName.endsWith(".cos")) {
		//				String command = "bzip2 -k "+cosiatecPlusBzipNlbDirectoryPath+"/"+cosiatecNlbFileName;
		//				try {
		//					Runtime.getRuntime().exec(command);
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}

		//Compress all .cos files in NLB-PAIRS directory
		//		for(String cosiatecNlbPairFileName : cosiatecNlbPairFileNames) {
		//			if (cosiatecNlbPairFileName.endsWith(".cos")) {
		//				String command = "bzip2 -k "+cosiatecPlusBzipNlbPairsDirectoryPath+"/"+cosiatecNlbPairFileName;
		//				try {
		//					Runtime.getRuntime().exec(command);
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}

		//Create log files for files in NLB directory

//		String[] cosiatecPlusBzipNlbFileNames = new File(cosiatecPlusBzipNlbDirectoryPath).list();
//		String pointSetDirectoryPath = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB/BZIP2/NLB";
//		for (String cosiatecPlusBzipNlbFileName : cosiatecPlusBzipNlbFileNames) {
//			if (cosiatecPlusBzipNlbFileName.endsWith(".cos")) 
//				createLogFile(cosiatecPlusBzipNlbFileName,cosiatecPlusBzipNlbDirectoryPath,pointSetDirectoryPath, ".log",".PointSet");
//		}

		//Create log files for files in NLB-PAIRS directory
		
		String[] cosiatecPlusBzipNlbPairFileNames = new File(cosiatecPlusBzipNlbPairsDirectoryPath).list();
		String pointSetDirectoryPath = "/Users/dave/Documents/Work/Research/Data/NLB/OUTPUT/NLB/BZIP2/NLB-PAIRS";
		for (String cosiatecPlusBzipNlbPairFileName : cosiatecPlusBzipNlbPairFileNames) {
			if (cosiatecPlusBzipNlbPairFileName.endsWith(".cos")) {
				createLogFile(cosiatecPlusBzipNlbPairFileName,cosiatecPlusBzipNlbPairsDirectoryPath,pointSetDirectoryPath,".mid.log",".mid.PointSet");
			}
		}

	}
}
