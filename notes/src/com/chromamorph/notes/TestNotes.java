package com.chromamorph.notes;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;


public class TestNotes {
	public static void main(String[] args) {
		try {
			//			Notes notes = new Notes(new File("/Users/dave/Documents/Work/Teaching/Med8/AS/midi/grouping_MIDI/beethoven.mid"));
			//			Notes notes = new Notes("/Users/dave/Documents/Work/Teaching/Med8/AS/midi/metre_midi/beethovenop110.mid");
//						Notes notes = Notes.fromOPND("/Users/dave/Documents/Work/Research/Data/Barber/BarberSonataOp26Mvt1.opnd");
//			Notes notes = new Notes(new File("/Users/dave/Documents/Work/Research/Data/Chopin/Etude Op.10 No.1/chopin-etude-op10-no1.notes"));
//			Notes notes = new Notes(new File("data/chopin-etude-op10-no2.notes"));
			//			Notes notes = new Notes(new File("data/chord-progressions"));
			//			notes.pitchSpell(10, 42);
			//			notes.play(4,145.0f,545l,764l);
			//			for(Note note : notes.getNotes()) {
			//				if(!note.getComputedPitch().getPitchName().equals(note.getPitchName()))
			//					System.out.println(note.getOnset()+"\t"+note.getPitchName()+"\t"+note.getComputedPitch().getPitchName());
			//			}
			//			System.out.println("Note accuracy = " + notes.getPitchSpellingNoteAccuracy());
			//			System.out.println("Error count = " + notes.getPitchSpellingNoteErrorCount());
			//			Notes notes = new Notes(new File("data/chord-progressions"));
			//			Notes notes = new Notes(new File("data/chopin-etude-op10-no2"));
			//			notes.play(4,172.0f,1);
			//			System.out.println(notes);
			//			for (Note note : notes.getNotes())
			//				System.out.println(note);
			//			Notes notes = Notes.fromOPND("/Users/dave/Documents/Work/Research/Data/PS13 ESCOM/ps13escomopnd/bwv850b-mel.opnd");
			//			notes.play(4,144.0f);

//			String opndFolderPathName = "/Users/dave/Documents/Work/Research/workspace-to-2014-01-17/Points/data/WTCI-FUGUES-FOR-JNMR-2014";
//			Notes notes = Notes.fromOPND(opndFolderPathName+"/"+"bwv869b.opnd");
//			Notes notes = Notes.fromMIDI("/Users/dave/Documents/Work/Research/Data/Sony/Lounge music segmentation/FilesWithGroundTruthDM/LC0103.mid");
//			Notes notes = Notes.fromMIDI("/Users/dave/Documents/Work/Research/Data/Beethoven/sonata05-1.mid");
//			System.out.println("maxVoice = "+notes.getMaxVoice());
//			Notes voice = notes.getVoice(5);
//			voice.play(8, 60.0f,256l);
//			notes.toOPNDFile("data/chopin-etude-op10-now.opndv");
			Notes notes = Notes.fromMIDI("/Volumes/LOTOFSPACE/Work/Research/Collaborations/Kat Agres/StimuliForDave_Exp1/1aNormChNorm.mid");
			notes.play(128l, 120f);
			

		} catch (IOException e) {
			e.printStackTrace(); }
		catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); }
		catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); }
	}
}
