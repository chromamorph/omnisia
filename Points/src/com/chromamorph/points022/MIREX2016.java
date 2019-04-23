package com.chromamorph.points022;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class MIREX2016 {

	public static void main(String[] args) {

		String groundTruthDirectory = "JKUPDD-noAudio-Aug2013/groundTruth/";
		String[] inputFiles = {
				"bachBWV889Fg/polyphonic/lisp/wtc2f20.txt",
				"beethovenOp2No1Mvt3/polyphonic/lisp/sonata01-3.txt",
				"chopinOp24No4/polyphonic/lisp/mazurka24-4.txt",
				"gibbonsSilverSwan1612/polyphonic/lisp/silverswan.txt",
				"mozartK282Mvt2/polyphonic/lisp/sonata04-2.txt"
		};
		String outputDirectory = "output";

		int raw=0, bb = 1, seg=2;

		for(String algorithm : new String[]{"COSIATEC", "SIATECCompress", "Forth"})
			for(boolean compactnessTrawler : new boolean[]{true, false})
				for(boolean siar : new boolean[]{true, false})
					for(boolean rrt : new boolean[]{true, false})
						for(double minc : new double[]{0.5, 0.9})
							for(int minPatSize : new int[]{4,8})
								for(int mode : new int[]{raw,bb,seg})
									for(String inputFile : inputFiles)
									{
										String algSwitch = " -a "+algorithm;
										String diatonicPitchSwitch = " -d";
										String mirexSwitch = " -m";
										String compactnessTrawlerSwitch = 
												compactnessTrawler?" -ct":"";
										String siarSwitch = siar?" -rsd":"";
										String rrtSwitch = rrt?" -rrt":"";
										String mincSwitch = " -minc "+minc;
										String minPatSizeSwitch = " -min "+minPatSize;
										String bbCompactnessSwitch = " -bbcomp";
										String modeSwitch = "";
										if (mode==bb) 
											modeSwitch = " -bbmode";
										else if (mode==seg)
											modeSwitch = " -segmode";
										String omnisiaArgs = 
												algSwitch +
												diatonicPitchSwitch +
												mirexSwitch +
												compactnessTrawlerSwitch +
												siarSwitch +
												rrtSwitch +
												mincSwitch +
												minPatSizeSwitch +
												bbCompactnessSwitch +
												modeSwitch;
										String noDateSwitch = " -nodate";
										String inputFileSwitch = 
												" -i " + groundTruthDirectory + inputFile;
										String outputDirSwitch =
												" -o " + outputDirectory + "/" + omnisiaArgs.replaceAll(" ", "").substring(2);
										//										File file = new File(outputDirectory + "/" + omnisiaArgs.replaceAll(" ", "").substring(2)+"/"+inputFile.substring(0,inputFile.indexOf("/")));
										//										file.mkdirs();
										//										try {
										//											file.createNewFile();
										//										} catch (IOException e) {
										//											e.printStackTrace();
										//										}

										String omnisiaArgsString = inputFileSwitch +outputDirSwitch +omnisiaArgs + noDateSwitch;
										System.out.println(omnisiaArgsString.trim());
										String[] argsArray = omnisiaArgsString.trim().split(" ");
										//										for(String a : argsArray)
										//											System.out.println(a);
										try {
											OMNISIA.main(argsArray);
										} catch (MissingTieStartNoteException e) {
											e.printStackTrace();
										}
									}
	}
}
