package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Notes;

/**
 * 
 * @author David Meredith
 * @date Wednesday 5 June 2013
 * 
 * Compare each melody in the annotated corpus with each reference melody.
 * 
 * Define the distance between two melodies, A and B, to be the lesser of 
 * the two sums of the euclidean distances through which the notes in one
 * melody must be moved in order to give a subset of the notes in the other melody.
 * 
 * Could also be defined to be the average or the greater of the two distances.
 *
 */
public class ClassifyANNBySIAMEuclidWithRefMels {

	public static void main(String[] args) throws IOException, InvalidMidiDataException, NoMorpheticPitchException {
		//Get list of annotated corpus melody file names
		String annmidiDirectory = "/Users/dave/Documents/Work/Research/workspace/Points/data/nlb_datasets/annmidi";
		String[] annFileNames = new File(annmidiDirectory).list();
		
		//Get list of reference melody file names
		ArrayList<String> refCodes = new ArrayList<String>();
		ArrayList<String> refFamilies = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("/Users/dave/Documents/Work/Research/workspace/Points/data/nlb_datasets/referencemelodies.txt"));
		String l = br.readLine();
		while (l != null) {
			refCodes.add(l.substring(0, l.indexOf('\t')).trim());
			refFamilies.add(l.substring(l.indexOf('\t')+1).trim());
			l = br.readLine();
		}
		br.close();
		
		//Get ground truth reference code and tune family for each melody in the annotated corpus
		
		//Compare each annotated corpus file with each reference melody
		//Store shortest distance to predicted reference melody, predicted family and predicted reference melody code for each
		//annotated melody. Also store shortest distance to ground-truth reference melody.
		
		for(int annMelIndex = 0; annMelIndex < annFileNames.length; annMelIndex++) {
			String annMelMidiFileName = annmidiDirectory+"/"+annFileNames[annMelIndex];
			Notes annMelNotes = Notes.fromMIDI(annMelMidiFileName, true);
			for(int refMelIndex = 0; refMelIndex < refCodes.size(); refMelIndex++) {
				String refMelMidiFileName = annmidiDirectory+"/"+refCodes.get(refMelIndex)+".mid";
				Notes refMelNotes = Notes.fromMIDI(refMelMidiFileName, true);
				
				//Get point sets
				
				PointSet annPointSet = new PointSet(annMelNotes,true);
				PointSet refPointSet = new PointSet(refMelNotes,true);
				
				//Get minimum SIAM euclidean distance between PointSets
				
				double minSIAMEuclidDistance = annPointSet.getMinSIAMEuclidDistance(refPointSet);
				System.out.println(minSIAMEuclidDistance);
				
			}
		}
		
		
	}
}
