package com.chromamorph.points022;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.chromamorph.notes.Notes;
  
public class RunMe {

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
					String outputFolder = outputFolderChooser.getSelectedFile().getAbsolutePath();
					System.out.println("\nFile: "+fullFileName);
					PointSet points = null;
					boolean diatonicPitch = true;
					boolean chromaticPitch = false;
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
						points = new PointSet(new Notes(new File(fullFileName)),chromaticPitch);
					//						points.encode(new SIARetrogradeInversions());
					//						points.encode(new SIARetrogrades());
					//						points.encode(new SIAInversions());
					//						points.encode(new SIATECEncoder());
//											points.encode(new SIATECCompress());
					//						points.play(4);
					//						PitchClassSequenceEncoding pcse = (PitchClassSequenceEncoding)(points.encode(new PitchClassSequenceEncoder()));
					//						PointSet pcsePointSet = pcse.getPoints();
					//						if (pcsePointSet == null) {
					//							System.out.println("pcsePointSet is null");
					//							return;
					//						}
					////						pcsePointSet.draw();
//					SIATECCompressEncoding encoding = (SIATECCompressEncoding)points.encode(new SIATECCompress());
//					points.draw(encoding.getTECs());
//											pcsePointSet.draw(((SIATECCompressEncoding)(pcsePointSet.encode(new SIATECCompress()))).getTECs());
//					points.play(100);
//					points.draw();
					
					(new COSIATECEncoding(points,fileName,outputFolder,chromaticPitch,fullFileName)).draw();
					
//					System.out.println("\nTECQualityComparator data\n========================\n");
//					System.out.println("Compression ratio used: "+TECQualityComparator.COMPRESSION_RATIO_USED_FREQ);
//					System.out.println("Compactness used: "+TECQualityComparator.COMPACTNESS_USED_FREQ);
//					System.out.println("Coverage used: "+TECQualityComparator.COVERAGE_USED_FREQ);
//					System.out.println("Pattern size used: "+TECQualityComparator.PATTERN_SIZE_USED_FREQ);
//					System.out.println("Width used: "+TECQualityComparator.WIDTH_USED_FREQ);
//					System.out.println("BB area used: "+TECQualityComparator.BBAREA_USED_FREQ);
//					System.out.println("Zero returned: "+TECQualityComparator.ZERO_RETURNED_FREQ);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidMidiDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoMorpheticPitchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
