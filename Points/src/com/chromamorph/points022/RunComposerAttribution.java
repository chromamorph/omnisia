package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.chromamorph.points.Algorithm;

/**
 * This file runs the ComposerAttribution task, as described in GPDIM.
 * 
 * @author David Meredith
 *
 */
public class RunComposerAttribution {
	private static ArrayList<String> 
	bachList, bachListForDatasetsIIIAndIV, 
	handelList, 
	telemannList, 
	haydnList, 
	mozartList; 
	//		wfbachList;

	private static String dirsPath = "/Users/dave/Documents/Work/Research/Data/vanKranenburg/ComposerAttribution/dirs-cim2004";
	private static String rootPath = "/Users/dave/Documents/Work/Research/Data/vanKranenburg/ComposerAttribution";
	private static ArrayList<String> allPieces;
	private static Algorithm[] compressionAlgorithms = {Algorithm.COSIATEC,Algorithm.Forth,Algorithm.SIATECCompress};
	private static Algorithm[] mtpAlgorithms = {Algorithm.SIA,Algorithm.SIACT,Algorithm.SIAR,Algorithm.SIARCT};

	//Parameter values
	private static int r = 3, b = 3;
	private static double a = 0.66;

	private static ArrayList<String> makeList(String composer, String[] dirsList, boolean excludeOrganFugues) {
		ArrayList<String> list = new ArrayList<String>();
		for(String fileName : dirsList) {
			if (fileName.startsWith(composer) && (!excludeOrganFugues || !fileName.contains("organfugues"))) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(dirsPath+"/"+fileName));
					String l;
					while ((l = br.readLine()) != null)
						list.add(rootPath+"/"+l);
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("\n"+composer.toLowerCase()+"List:");
		for(int i = 0; i < list.size(); i++)
			System.out.println((i+1)+" "+ list.get(i));
		return list;
	}

	private static void makeLists() {
		String[] dirsList = new File(dirsPath).list();

		bachList = makeList("Bach", dirsList, true);
		bachListForDatasetsIIIAndIV = makeList("Bach", dirsList, false);
		handelList = makeList("Handel", dirsList, false);
		telemannList = makeList("Telemann", dirsList, false);
		haydnList = makeList("Haydn", dirsList, false);
		mozartList = makeList("Mozart", dirsList, false);
		//		wfbachList = makeList("WFBach", dirsList, false);
		allPieces = union(bachListForDatasetsIIIAndIV,handelList,telemannList,haydnList,mozartList);
	}

	@SafeVarargs
	public static ArrayList<String> union(ArrayList<String>... lists) {
		ArrayList<String> outputList = new ArrayList<String>();
		for(ArrayList<String> list : lists)
			outputList.addAll(list);
		return outputList;
	}

	public static void runAllAlgorithmsOnAllPieces() {
		for(String kernFilePathName : allPieces)
			for(Algorithm compressionAlgorithm : compressionAlgorithms)
				for(Algorithm mtpAlgorithm : mtpAlgorithms)
					switch(compressionAlgorithm) {
					case COSIATEC:
						COSIATECEncoding cosiatecEncoding = new COSIATECEncoding(
								kernFilePathName,
								outputFileDirectoryPath,
								diatonicPitch,
								mirex,
								withCompactnessTrawler,
								a,
								b,
								forRSuperdiagonals,
								r,
								removeRedundantTranslators,
								);
						break;
					case Forth:
						break;
					case SIATECCompress:
						break;
					}
	}

	public static void main(String[] args) {
		makeLists();
		runAllAlgorithmsOnAllPieces();
		constructPiecePairs();
		runAllAlgorithmsOnPiecePairs();
		runComposerAttributionExperiment("ExperimentI",
				bachList,
				telemannList,
				handelList,
				haydnList,
				mozartList);
		runComposerAttributionExperiment("ExperimentII",
				bachList,
				telemannList,
				handelList);
		runComposerAttributionExperiment("ExperimentIII",
				bachListForDatasetsIIIAndIV,
				union(telemannList,handelList));
		runComposerAttributionExperiment("ExperimentIV",
				bachListForDatasetsIIIAndIV,
				union(telemannList,handelList,haydnList,mozartList));
		runComposerAttributionExperiment("ExperimentV",
				telemannList,
				handelList);
		runComposerAttributionExperiment("ExperimentVI",
				haydnList,
				mozartList);
		runComposerAttributionExperiment("ExperimentVII",
				union(telemannList,handelList),
				union(haydnList,mozartList));
	}
}
