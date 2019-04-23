package com.chromamorph.points022;

import java.io.IOException;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class ForCUDA {
	public static void main(String[] args) {
		try {
			PointSet ps = new PointSet("/Users/dave/Documents/Work/Research/2015-06-17-workspace-mars/Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv846b-done.opnd",false);
			ps.writeToPtsFile("/Users/dave/Documents/Work/Research/Data/For CUDA/bwv846a-chrom.pts");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MissingTieStartNoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
