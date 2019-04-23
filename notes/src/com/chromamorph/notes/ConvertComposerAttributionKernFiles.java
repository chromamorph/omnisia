package com.chromamorph.notes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.ArrayList;
import java.util.TreeSet;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class ConvertComposerAttributionKernFiles {

	static String rootFolder = "/Users/dave/Documents/Work/Research/Data/vanKranenburg/ComposerAttribution";
	static String cim2004Folder = "dirs-cim2004";
	static String pr2005Folder = "dirs-pr2005";


	//	public static ArrayList<String> getKernFilePathNames(String folderPathName) {
	//		ArrayList<String> kernFilePathNames = new ArrayList<String>();
	//		String[] fileNameList = new File(folderPathName).list();
	//		if (fileNameList != null)
	//			for(String fileName : fileNameList) {
	//				String filePathName = folderPathName + "/" + fileName;
	//				if (filePathName.endsWith(".krn"))
	//					kernFilePathNames.add(filePathName);
	//				else if (new File(filePathName).isDirectory())
	//					kernFilePathNames.addAll(getKernFilePathNames(filePathName));
	//			}
	//		return kernFilePathNames;
	//	}


	public static TreeSet<String> getKernFilePathNames() {
		TreeSet<String> kernFilePathNames = new TreeSet<String>();
		for(String dirFolder : new String[]{cim2004Folder, pr2005Folder}) {
			String dirFolderPathName = rootFolder + "/" + dirFolder;
			String[] dirFileNames = new File(dirFolderPathName).list();
			for(String dirFileName : dirFileNames) {
				if (dirFileName.endsWith(".dir")) {
					String dirFilePathName = dirFolderPathName + "/" + dirFileName;
					try {
						BufferedReader br = new BufferedReader(new FileReader(dirFilePathName));
						String l;
						while ((l = br.readLine())!= null) {
							String kernFilePathName = rootFolder + "/" + l;
							kernFilePathNames.add(kernFilePathName);
						}
						br.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return kernFilePathNames;
	}

	public static void main(String[] args) throws IOException, MissingTieStartNoteException {
		TreeSet<String> kernFilePathNames = getKernFilePathNames();
		//		for(String kernFilePathName : kernFilePathNames)
		//			System.out.println(kernFilePathName);

		//		Create root folder for opdv files
		String opdvRootFolder = rootFolder+"-opdv";
		new File(opdvRootFolder).mkdirs();

		//		Convert to opdv format
		for(String kernFilePathName : kernFilePathNames) {
			String kernFileName = kernFilePathName.substring(rootFolder.length());
			String opdvFileName = kernFileName.substring(0, kernFileName.length()-4) + ".opdv";
			String opdvFilePathName = opdvRootFolder + opdvFileName; 
			//			System.out.println(opdvFilePathName);
			System.out.print("Reading " + kernFilePathName + "...");
			Notes notes = Notes.fromKern(kernFilePathName);
			System.out.println("Done");
			System.out.print("Writing " + opdvFilePathName + "...");
			notes.toOPDVFile(opdvFilePathName);
			System.out.println("Done");
		}
	}

}
