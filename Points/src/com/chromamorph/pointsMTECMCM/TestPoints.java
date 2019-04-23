package com.chromamorph.pointsMTECMCM;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.chromamorph.notes.Notes;

public class TestPoints {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFileChooser chooser = new JFileChooser("data");
				int returnVal = chooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION)
					try {
						String fileName = chooser.getSelectedFile().getAbsolutePath();
						System.out.println("\nFile: "+fileName);
						PointSet points = null;
						if (fileName.endsWith("pts"))
							points = new PointSet(fileName);
						else if (fileName.endsWith("opnd"))
							points = new PointSet(Notes.fromOPND(fileName),true);
						else if (fileName.endsWith("ecolm"))
							points = new PointSet(Notes.fromECOLM(fileName),true);
						else
							points = new PointSet(new Notes(new File(fileName)),true);
						points.analyse(new SIATECCover());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} 
			}
		});
	}

	
	
//	public static void main(String[] args) {
//		try {
//			Points points = new Points(new Notes(new File("/Users/dave/Documents/Work/Research/Data/Chopin/Etude Op.10 No.1/chopin-etude-op10-no1")));
//			PointSet points = new PointSet(new Notes(new File("/Users/dave/Documents/Work/Research/Data/Chopin/Etude Op.10 No.2/chopin-etude-op10-no2")));
//			points.draw();
//			points.play(100);

//			PointSet points = new PointSet("data/simple/TestRemoveOnePoint2.pts");
//			PointSet points = new PointSet(Notes.fromOPND("data/BWV846a/bwv846b-mel.opnd"));
//			PointSet points = new PointSet(Notes.fromOPND("/Users/dave/Documents/Work/Research/Data/PS13 ESCOM/ps13escomopnd/bwv846b-mel.opnd"),true);
//			points.draw();
//			points.analyse(new COSIATEC());
//			points.analyse(new SIATEC());
//			points.play(150);
			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
////		PointSet points = new PointSet("data/simple/Cross.pts");
//		PointSet points = new PointSet("data/simple/TrianglesAndSquares4.pts");
//		points.draw();
//		points.analyse(new COSIATEC());
//		points.analyse(new COSIATEC002());
//	}
}
