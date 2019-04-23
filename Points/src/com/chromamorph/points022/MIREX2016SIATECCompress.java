package com.chromamorph.points022;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class MIREX2016SIATECCompress {

	public static void main(String[] args) {

		String groundTruthDirectory = "JKUPDD-noAudio-Aug2013/groundTruth/";
		String[] inputFiles = {
				"bachBWV889Fg/polyphonic/lisp/wtc2f20.txt",
				"beethovenOp2No1Mvt3/polyphonic/lisp/sonata01-3.txt",
				"chopinOp24No4/polyphonic/lisp/mazurka24-4.txt",
				"gibbonsSilverSwan1612/polyphonic/lisp/silverswan.txt",
				"mozartK282Mvt2/polyphonic/lisp/sonata04-2.txt"
		};
		String outputDirectory = "output-siatecCompress";

		int count = 0;
		
		//		for(String algorithm : new String[]{"COSIATEC", "SIATECCompress", "Forth"})
		String algorithm = "SIATECCompress";
		for(int r : new int[]{0,1,3})
			for(boolean rrt : new boolean[]{true, false})
				for(double minc : new double[]{0.25,0.5})
					for(int minPatSize : new int[]{4,6,8})
						for(int topNPatterns : new int[]{0,10})
							for(String inputFile : inputFiles)
							{
								
								String algSwitch = " -a "+algorithm;
								String diatonicPitchSwitch = " -d";
								String mirexSwitch = " -m";
								String siarSwitch = (r!=0)?" -rsd":"";
								String rSwitch = (r!=0)?" -r "+r:"";
								String rrtSwitch = rrt?" -rrt":"";
								String mincSwitch = " -minc "+minc;
								String minPatSizeSwitch = " -min "+minPatSize;
								String bbCompactnessSwitch = " -bbcomp";
								String modeSwitch = " -segmode";
								String topNPatternsSwitch = " -top " + topNPatterns;
								String omnisiaArgs = 
										algSwitch +
										diatonicPitchSwitch +
										mirexSwitch +
										siarSwitch +
										rSwitch +
										rrtSwitch +
										mincSwitch +
										minPatSizeSwitch +
										bbCompactnessSwitch +
										modeSwitch +
										topNPatternsSwitch;
								String noDateSwitch = " -nodate";
								String inputFileSwitch = 
										" -i " + groundTruthDirectory + inputFile;
								String outputDirSwitch =
										" -o " + outputDirectory + "/" + omnisiaArgs.replaceAll(" ", "").substring(2);
								String omnisiaArgsString = inputFileSwitch +outputDirSwitch +omnisiaArgs + noDateSwitch;
								System.out.println(++count + ". "+omnisiaArgsString.trim());
								String[] argsArray = omnisiaArgsString.trim().split(" ");
								try {
									OMNISIA.main(argsArray);
								} catch (MissingTieStartNoteException e) {
									e.printStackTrace();
								}
							}
	}
}
