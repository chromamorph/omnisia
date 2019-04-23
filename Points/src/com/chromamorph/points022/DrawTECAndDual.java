package com.chromamorph.points022;

import java.io.IOException;
import java.util.ArrayList;

import com.chromamorph.notes.Notes;

public class DrawTECAndDual {
	public static void main(String[] args) {
		try {
			ArrayList<TEC> tecs = new ArrayList<TEC>();
			String opndFileName = "/Users/dave/Documents/Work/Research/workspace/Points/data/DMRN2012-WTCI/bwv846b-mel.opnd";
			Notes notes;
			notes = Notes.fromOPND(opndFileName);
			PointSet dataset;
			dataset = new PointSet(notes,true);
			COSIATECEncoding E = new COSIATECEncoding(dataset);
			for(TEC tec : E.getTECs()) {
				tecs.add(tec);
				tecs.add(tec.getDual());
			}
			dataset.draw(tecs);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		}
	}
}
