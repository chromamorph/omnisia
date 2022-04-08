package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import com.chromamorph.pitch.Pitch;

public class GenerateOPDDatasetsFromOPNDDatasets {

	static class OPNDDatapoint {

		private long onset, duration;
		private Pitch pitch;
		private int voice;

		/*
		 * (0 "An2" 243 6)
		 */

		public OPNDDatapoint(String lispListString) {
			String[] a = lispListString.split(" ");
			onset = Long.parseLong(a[0]);
			duration = Long.parseLong(a[2]);
			voice = Integer.parseInt(a[3]);
			pitch = new Pitch();
			pitch.setPitchName(a[1]);
		}

		public String toString() {
			return onset + " " + pitch.getChromaticPitch() + " " + pitch.getMorpheticPitch() + " " + duration + " " + voice;
		}
	}

	public static void main(String[] args) {
		String cleanOPNDDatasetFolder = "data/opnd-m from nts";
		String noisyOPNDDatasetFolder = "data/opnd-m-noisy";

		String cleanOPDDatasetFolder = "data/opd-from-nts";
		String noisyOPDDatasetFolder = "data/opd-noisy";

		File f = new File(cleanOPDDatasetFolder);
		if (!f.exists()) f.mkdirs();
		f = new File(noisyOPDDatasetFolder);
		if (!f.exists()) f.mkdirs();

		FilenameFilter fileNameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".opnd-m");
			}
		};
		
	
		try {

			for(int k = 0; k < 2; k++) {
				String inputFolder = new String[] {cleanOPNDDatasetFolder, noisyOPNDDatasetFolder}[k];
				String outputFolder = new String[] {cleanOPDDatasetFolder, noisyOPDDatasetFolder}[k];
	
				String[] listOfOPNDFileNames = new File(inputFolder).list(fileNameFilter);

				System.out.println(listOfOPNDFileNames.length + " files found in folder " + inputFolder);

				for (int i = 0; i < listOfOPNDFileNames.length; i++) {
					//		Read the input OPND file into a string
					String opndFileName = listOfOPNDFileNames[i];
					
					String inputOPNDFilePath = inputFolder + "/" + opndFileName;
					BufferedReader br = new BufferedReader(new FileReader(inputOPNDFilePath));
					String l;
					StringBuilder inputOPNDDatasetString = new StringBuilder();
					l = br.readLine().trim().replace("\"", "");
					while (l != null) {
						System.out.println(l);
						inputOPNDDatasetString.append(l + " ");
						l = br.readLine();
						if (l != null)
							l = l.trim().replace("\"", "").replace("(", "").replace(")", "x");
					}

					br.close();
					
					//		Parse the OPND dataset string into an ArrayList of OPNDDatapoints
					String datasetString = inputOPNDDatasetString.toString().trim();
					datasetString = datasetString.substring(0,datasetString.length()-1);					
					String[] arrayOfDatapointStrings = datasetString.split("x");
					
					String outputOPDFilePath = outputFolder + "/" + opndFileName.replace(".opnd-m", ".opd");
					PrintWriter pw = new PrintWriter(new File(outputOPDFilePath));
					
					for(String s : arrayOfDatapointStrings) {
						OPNDDatapoint dp = new OPNDDatapoint(s.trim());
						pw.println(dp);
					}
					
					pw.close();
					
				}

			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}


