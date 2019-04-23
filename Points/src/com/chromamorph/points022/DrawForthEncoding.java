package com.chromamorph.points022;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class DrawForthEncoding {
	public static void main(String[] args) {
		try {
			PointSet dataset;
			dataset = new PointSet("/Users/dave/Documents/Work/Research/Data/Francois/07-map-GM.mid", PitchRepresentation.MORPHETIC_PITCH);
			ForthEncoding encoding = new ForthEncoding("/Users/dave/Documents/Work/Research/Data/Francois/07-map-GM/Forth/07-map-GM.alltecs",dataset);
			encoding.draw();
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnimplementedInputFileFormatException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (MissingTieStartNoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
}
