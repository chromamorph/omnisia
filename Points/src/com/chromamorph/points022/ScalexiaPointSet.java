package com.chromamorph.points022;

import java.io.IOException;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class ScalexiaPointSet {
	public static void main(String[] args) throws MissingTieStartNoteException, IOException {
		PointSet pointSet = new PointSet("data/Haydn/MenuettoAlRovescio.opnd");
//		PointSet pointSet = new PointSet("data/Die Kunst der Fuge/ContrapunctusVIStart.opnd");
		pointSet = pointSet.getScalexiaPointSet();
//		pointSet.writeToPtsFile("data/Die Kunst der Fuge/ContrapunctusVIStart.pts");
//		pointSet.draw("Contrapunctus VI", true, true, "data/Die Kunst der Fuge/ContrapunctusVIStart.png");
		pointSet.writeToPtsFile("data/Haydn/MenuettoAlRovescio.pts");
		pointSet.draw("Menuetto al Rovescio", false, true, "data/Haydn/MenuettoAlRovescio.png");
	}
}
