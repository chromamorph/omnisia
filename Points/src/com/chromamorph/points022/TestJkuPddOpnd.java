package com.chromamorph.points022;

import java.io.IOException;
import java.util.ArrayList;

import com.chromamorph.notes.Notes;

/**
 * 
 * @author David Meredith
 * 
 * This file tests the conversion from Collins' "lisp"
 * format to my OPND format.
 * 
 * Each OPND format file is loaded into a Notes object and played
 *
 */
public class TestJkuPddOpnd {


	public static void main(String[] args) {
		/* Make a list of the full pathnames of all the
		 * Lisp format files in the JKU PDD
		 */


		String rootFolder = "/Users/dave/Documents/Work/Research/MIREX2013/JKUPDD-noAudio-Aug2013/groundTruth";
		ArrayList<String> lispFileNames = CollinsLispToOpnd.getLispFileNames(rootFolder);

		for(String collinsLispFileName : lispFileNames) {
			System.out.println(collinsLispFileName);
		}

		try {
			String opndFileName = rootFolder+"/bachBWV889Fg/monophonic/lisp/wtc2f20.opnd";
			opndFileName = rootFolder+"/bachBWV889Fg/polyphonic/lisp/wtc2f20.opnd";
			opndFileName = rootFolder+"/beethovenOp2No1Mvt3/polyphonic/lisp/sonata01-3.opnd";
			opndFileName = rootFolder+"/chopinOp24No4/polyphonic/lisp/mazurka24-4.opnd";
			opndFileName = rootFolder+"/gibbonsSilverSwan1612/polyphonic/lisp/silverswan.opnd";
			opndFileName = rootFolder+"/mozartK282Mvt2/polyphonic/lisp/sonata04-2.opnd";
			Notes notes = Notes.fromOPND(opndFileName);
			PointSet points = new PointSet(notes,false);
			points.draw();
			//			points.play(20);
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
//		catch (InvalidMidiDataException e) {
//			e.printStackTrace();
//		} 
//		catch (MidiUnavailableException e) {
//			e.printStackTrace();
//		} 
		catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		}
	}


}
