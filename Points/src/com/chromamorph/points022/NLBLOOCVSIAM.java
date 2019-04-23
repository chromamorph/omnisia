package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author David Meredith
 * 
 * Measures the similarity between two NLB tunes as
 * |M|/min{|T1|,|T2|}
 * where |M| is the size of the maximal, translation-invariant
 * subset match between the two tunes T1 and T2.
 * 
 * Then classifies each tune into the tune family of its nearest neighbour.
 *
 */

class LabelledPointSet {
	PointSet pointSet;
	String label;
}

public class NLBLOOCVSIAM {
	public static void main(String[] args) {
		//Get list of file paths for NLB MIDI files
		File inputFileDirectory = new File("/Users/dave/Documents/Work/Research/workspace/Points/data/nlb_datasets/annmidi");
		String[] midiFileNames = inputFileDirectory.list();
		ArrayList<String> midiFilePaths = new ArrayList<String>();
		for(int i = 0; i < midiFileNames.length; i++) {
			if (midiFileNames[i].endsWith(".mid"))
				midiFilePaths.add(inputFileDirectory+"/"+midiFileNames[i]);
		}

		//Get list of labels
		ArrayList<String> labels = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("/Users/dave/Documents/Work/Research/workspace/Points/data/nlb_datasets/ann_labels.txt"));
			String l;
			while((l = br.readLine()) != null) {
				String label = l.substring(l.indexOf("\t")+1).trim();
				labels.add(label);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Load all NLB MIDI files into PointSets
		
		//Label each NLB PointSet with its tune family
		
		//For each NLB PointSet, find the tune family of
		//the most similar NLB PointSet
		
		
	}
}
