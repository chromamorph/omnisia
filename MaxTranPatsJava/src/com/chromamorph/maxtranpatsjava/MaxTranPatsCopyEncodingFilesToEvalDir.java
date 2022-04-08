package com.chromamorph.maxtranpatsjava;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MaxTranPatsCopyEncodingFilesToEvalDir {
	public static void main(String[] args) {
//		Make an array of paths to the source pair files for F2STR
		String[] sourcePaths = new String[64620];
		for(int i = 0; i < 64620; i++)
			sourcePaths[i] = null;

		String rootFolder = "/Users/susanne/Repos/nlb20210504/";
		
		String[] F2STRSingleFileSourceFolders = new String[] {
				"output/single-files-F2STR"
		};
		String F2STRSingleFileDestinationFolder = "20210827-output/F2STR-scalexia/NLB/";
		
		String[] F2TRSingleFileSourceFolders = new String[] {
				"output/single-files-F2TR"
		};
		String F2TRSingleFileDestinationFolder = "20210827-output/F2TR/NLB/";
		
		String[] F2TSingleFileSourceFolders = new String[] {
				"output/single-files-F2T"
		};
		String F2TSingleFileDestinationFolder = "20210827-output/F2T/NLB/";
		
		
		
		
		String[] F2STRPairFileSourceFolders = new String[]{
				"output/pair-files-F2STR-with-scalexia",
				"output/pair-files-F2STR-with-scalexia-corsair",
				"output/pair-files-F2STR-with-scalexia-mac2010",
				"output/pair-files-F2STR-with-scalexia-new-mac",
				"output/pair-files-F2STR-with-scalexia-p50",
				"output/pair-files-F2STR-with-scalexia-missing"
		};
		String F2STRPairFileDestinationFolder = "20210827-output/F2STR-scalexia/NLB-PAIRS/";
		
		String[] F2TRPairFileSourceFolders = new String[] {
				"output/pair-files-F2TR"
		};
		String F2TRPairFileDestinationFolder = "20210827-output/F2TR/NLB-PAIRS/";
		
		String[] F2TPairFileSourceFolders = new String[] {
				"output/pair-files-F2T"
		};
		String F2TPairFileDestinationFolder = "20210827-output/F2T/NLB-PAIRS/";
		
		String[][] sourceFolderSets = new String[][] {
//			F2TPairFileSourceFolders,
//			F2TRPairFileSourceFolders,
//			F2STRPairFileSourceFolders,
			F2TSingleFileSourceFolders,
			F2TRSingleFileSourceFolders,
			F2STRSingleFileSourceFolders
		};
		
		String[] destinationFolders = new String[] {
//				F2TPairFileDestinationFolder,
//				F2TRPairFileDestinationFolder,
//				F2STRPairFileDestinationFolder,
				F2TSingleFileDestinationFolder,
				F2TRSingleFileDestinationFolder,
				F2STRSingleFileDestinationFolder
		};
		
		for(int i = 0; i < 3; i++) {
			String[] sourceFolders = sourceFolderSets[i];
			String destinationFolder = destinationFolders[i];
			for(String sourceFolder : sourceFolders) {
				File sf = new File(rootFolder + sourceFolder);
				String[] dirList = sf.list(
						new FilenameFilter() {

							@Override
							public boolean accept(File dir, String name) {
								if (name.startsWith("."))
									return false;
								File f = new File(dir, name);
								if (!(f.isDirectory()))
									return false;
								String[] d = f.list();
								if (d.length != 1)
									return false;
								if (!d[0].endsWith(".enc"))
									return false;
//								for(int i = 0; i < 5; i++)
//									if (!Character.isDigit(name.charAt(i)))
//										return false;
								return true;
							}
							
						});
				for(String dir : dirList) {
					String dirPath = rootFolder + sourceFolder + "/" + dir;
					String[] fileList = new File(dirPath).list();
					if (fileList.length == 0)
						System.out.println("Directory, "+ dirPath + ", does not contain any files");
					String fn = fileList[0];
					String sourcePath = dirPath + "/" + fn;
					String targetPath = rootFolder + destinationFolder + fn;
					try {
						Files.copy(Paths.get(sourcePath), Paths.get(targetPath));
						System.out.println("Copying" + sourcePath + " to " + targetPath);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
