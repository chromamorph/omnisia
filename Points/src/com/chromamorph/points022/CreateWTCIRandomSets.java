package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author David Meredith 5 January 2014
 * 
 * Creates for each piece in WTCI
 * a random set of the same size in a
 * lattice of the same size.
 *
 */
public class CreateWTCIRandomSets {
	public static void main(String[] args) throws IOException {
		//Get a list of the filenames of the SIATECCompress output files
		String inputDirString = "/Users/dave/Documents/Work/Research/workspace/Points/output/points018/SIATECCompressOnWTCI";
		String outputDirString = "/Users/dave/Documents/Work/Research/workspace/Points/output/points018/WTCIRandomPointSets";
		File inputDir = new File(inputDirString);
		String[] inputFileNameList = inputDir.list();
		//For each of these files, read
		for(String inputFileName : inputFileNameList) {
			System.out.println("\n"+inputFileName);
			String fullFileName = inputDirString+"/"+inputFileName;
			BufferedReader br = new BufferedReader(new FileReader(fullFileName)); 
			String l = br.readLine().trim();
			
			//  the number of notes
			String s = "Number of notes: ";
			while(!l.startsWith(s))
				l = br.readLine().trim();
			int numberOfNotes = Integer.parseInt(l.substring(s.length()));
			System.out.println("Number of notes = "+numberOfNotes);
			
			//  the minimum and maximum time
			s = "Maximum time (in tatums): ";
			while(!l.startsWith(s))
				l = br.readLine().trim();
			long maxTime = Long.parseLong(l.substring(s.length()));
			System.out.println("Maximum time = "+maxTime);
			s = "Minimum time (in tatums): ";
			while(!l.startsWith(s))
				l = br.readLine().trim();
			long minTime = Long.parseLong(l.substring(s.length()));
			System.out.println("Minimum time = "+minTime);

			//  the minimum and maximum pitch
			s = "Maximum pitch: ";
			while(!l.startsWith(s))
				l = br.readLine().trim();
			int maxPitch = Integer.parseInt(l.substring(s.length()));
			System.out.println("Maximum pitch = "+maxPitch);
			s = "Minimum pitch: ";
			while(!l.startsWith(s))
				l = br.readLine().trim();
			int minPitch = Integer.parseInt(l.substring(s.length()));
			System.out.println("Minimum pitch = "+minPitch);
			br.close();

			//Create a random file with the same number of points
			//in a lattice of the same size as the WTCI file.
			maxTime = maxTime-minTime;
			maxPitch = maxPitch-minPitch;
			
			String randomPointSetFileName = outputDirString+"/"+inputFileName.substring(0,inputFileName.indexOf("-SIATECCompress"))+"-random.pts";
			RandomPointSets.CreateRandomPointSet(maxTime, maxPitch, numberOfNotes, randomPointSetFileName);
		}
	}
}
