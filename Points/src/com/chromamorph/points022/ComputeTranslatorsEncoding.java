package com.chromamorph.points022;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class ComputeTranslatorsEncoding extends Encoding {

	public ComputeTranslatorsEncoding(
			String inputFilePath, 
			String outputDirPath, 
			PitchRepresentation pitchRepresentation, 
			String outputFilePath,
			String queryFilePath, 
			boolean withoutChannel10) throws FileNotFoundException, MissingTieStartNoteException {
		super(null,
				inputFilePath,
				outputDirPath,
				pitchRepresentation.equals(PitchRepresentation.MORPHETIC_PITCH)?true:false,
				withoutChannel10,
				"tran",
				0,
				false,
				false,
				false,
				outputFilePath,
				queryFilePath
				);
		long startTime = System.currentTimeMillis();
		VectorSet translators = computeTranslators(queryPattern,dataset);
		long endTime = System.currentTimeMillis();
		setRunningTime(endTime - startTime);
		TEC tec = new TEC(queryPattern, translators, dataset, true);
		addTEC(tec);
		writeToFile();
	}

	private VectorSet computeTranslators(PointSet queryPattern, PointSet dataset) {
		ArrayList<Vector> V = new ArrayList<Vector>();
		VectorSet T = new VectorSet();
		for(Point p : queryPattern.getPoints())
			for(Point d : dataset.getPoints())
				V.add(new Vector(p,d));
		V.sort(null);
		Vector v = V.get(0);
		int c = 1;
		for(int i = 1; i < V.size();i++) {
			if (V.get(i).equals(v))
				c++;
			else {
				c = 1;
				v = V.get(i);
			}
			if (c == queryPattern.size())
				T.add(v);
		}
		return T;
	}
	
}
