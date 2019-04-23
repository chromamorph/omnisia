package com.chromamorph.points022;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.chromamorph.notes.Notes;

public class RunSIAScale {

	public static String fullFileName;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					JFileChooser chooser = new JFileChooser("data");
					chooser.setDialogTitle("Choose input file");
					int returnVal = chooser.showOpenDialog(null);
					if (returnVal != JFileChooser.APPROVE_OPTION) return;
					JFileChooser outputFolderChooser = new JFileChooser("output");
					outputFolderChooser.setDialogTitle("Choose output folder");
					outputFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal2 = outputFolderChooser.showOpenDialog(null);
					if (returnVal2 != JFileChooser.APPROVE_OPTION) return;

					fullFileName = chooser.getSelectedFile().getAbsolutePath();
					String fileName = chooser.getSelectedFile().getName();
					fileName = fileName.substring(0,fileName.indexOf('.'));
//					String outputFolder = outputFolderChooser.getSelectedFile().getAbsolutePath();
					System.out.println("\nFile: "+fullFileName);
					PointSet points = null;
					boolean diatonicPitch = true;
//					boolean chromaticPitch = false;
					if (fullFileName.endsWith("pts"))
						points = new PointSet(fullFileName);
					else if (fullFileName.endsWith("opnd"))
						points = new PointSet(Notes.fromOPND(fullFileName),diatonicPitch);
					else if (fullFileName.endsWith("ecolm"))
						points = new PointSet(Notes.fromECOLM(fullFileName),diatonicPitch);
					else if (fullFileName.toLowerCase().endsWith("mid")) {
						Notes notes = Notes.fromMIDI(fullFileName,diatonicPitch);
						System.out.println(notes);
						points = new PointSet(notes,diatonicPitch);
					}
					else
						points = new PointSet(new Notes(new File(fullFileName)),diatonicPitch);
					System.out.print(new SIAScaleEncodingOld(points));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				} catch (NoMorpheticPitchException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
