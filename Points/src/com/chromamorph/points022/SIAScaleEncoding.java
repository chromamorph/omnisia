package com.chromamorph.points022;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.maths.Rational;
import com.chromamorph.notes.Notes;

public class SIAScaleEncoding extends Encoding {

	PointSet dataset = new PointSet();
	
	@Override
	public
	void draw() {
	}

	public SIAScaleEncoding(String filePathName, boolean diatonicPitch) {
		if (filePathName.toLowerCase().endsWith(".mid")||filePathName.toLowerCase().endsWith(".midi")) {
			try {
				dataset = new PointSet(Notes.fromMIDI(filePathName), diatonicPitch);
			} catch (NoMorpheticPitchException | InvalidMidiDataException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (filePathName.toLowerCase().endsWith(".opnd")||filePathName.toLowerCase().endsWith(".opndv")) {
			try {
				dataset = new PointSet(Notes.fromOPND(filePathName), diatonicPitch);
			} catch (NoMorpheticPitchException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
//		Initialize PVArrays
		int maxPitch = dataset.getMaxY();
		int minPitch = dataset.getMinY();
		int maxIndex = maxPitch - minPitch + 1;
		ArrayList<ArrayList<VectorPointPair>> pVArrayPlus1 = new ArrayList<ArrayList<VectorPointPair>>();
		ArrayList<ArrayList<VectorPointPair>> pVArrayMinus1 = new ArrayList<ArrayList<VectorPointPair>>();
		ArrayList<ArrayList<VectorPointPair>> pVArrayPlus2 = new ArrayList<ArrayList<VectorPointPair>>();
		ArrayList<ArrayList<VectorPointPair>> pVArrayMinus2 = new ArrayList<ArrayList<VectorPointPair>>();
		ArrayList<ArrayList<ArrayList<VectorPointPair>>> pvArrays = new ArrayList<ArrayList<ArrayList<VectorPointPair>>>();
		pvArrays.add(pVArrayPlus1); pvArrays.add(pVArrayMinus1); pvArrays.add(pVArrayPlus2); pvArrays.add(pVArrayMinus2);
		for(ArrayList<ArrayList<VectorPointPair>> pvArray : pvArrays)
			for(int i = 0; i <= maxIndex; i++)
				pvArray.add(new ArrayList<VectorPointPair>());
//		For time scale factor greater than 0
//		For each object pattern reference point, r1, in the dataset. 
//		r1 is lexicographically least point in MSP
		TreeSet<Point> points = dataset.getPoints();
		ArrayList<Point> pointsArray = new ArrayList<Point>(points);
		TreeSet<SFListItem> sFList = new TreeSet<SFListItem>();
		for(int ir1 = 0; ir1 < pointsArray.size(); ir1++) {
			Point r1 = pointsArray.get(ir1);
//			For each image pattern reference point, r2, in the dataset.
//			When time scale factor is positive, we are only concerned with onset time and pitch
//			Returns matches even when durations don't match
//			Only need to consider r2s that are >= r1 lexicographically because
//			if r2 is less than r1, the MSP found is the image of an MSP that has
//			already been found on a previous iteration when r1 was equal to r2.
			for(int ir2 = ir1; ir2 < pointsArray.size(); ir2++) {
				Point r2 = pointsArray.get(ir2);
//				Reset PVArrays
				for(ArrayList<ArrayList<VectorPointPair>> pvArray : pvArrays)
					for(int i = 0; i <= maxIndex; i++)
						pvArray.set(i,new ArrayList<VectorPointPair>());
//				For each onset point, p1, other than r1, p1 > r1, lexicographically
//				We don't need to consider p1 less than r1 because then r1 would not
//				be the reference point for the MSP found.
				for(int ip1 = ir1+1; ip1 < pointsArray.size(); ip1++) {
					Point p1 = pointsArray.get(ip1);
//					v1 = p1 - r1
					Vector v1 = new Vector(r1,p1);
//					Store (p1,v1) in a list in PVArray1+ or PVArray1-, depending on sign of pitch(v1), at index abs(pitch(v1))
					if (v1.getY() < 0)
						pVArrayMinus1.get(-v1.getY()).add(new VectorPointPair(v1,p1));
					else
						pVArrayPlus1.get(v1.getY()).add(new VectorPointPair(v1,p1));
				}
//				For each onset point, p2, other than r2, p2 > r2
//				We don't need to consider p2 less than r2 because, if sf > 0,
//				r2 must be lex least point in image pattern.
				for(int ip2 = ir2 + 1; ip2 < pointsArray.size(); ip2++) {
					Point p2 = pointsArray.get(ip2);
//					v2 = p2 - r2
					Vector v2 = new Vector(r2,p2);
//					Store (p2,v2) in a list in PVArray2+ or PVArray2-, depending on sign of pitch(v2), at index abs(pitch(v2))
					if (v2.getY() < 0)
						pVArrayMinus2.get(-v2.getY()).add(new VectorPointPair(v2,p2));
					else
						pVArrayPlus2.get(v2.getY()).add(new VectorPointPair(v2,p2));
				}
//				For i = 0 to size(PVArray1+)-1
				for(int i = 0; i < pVArrayPlus1.size(); i++) {
//					for each (p1,v1) in PVArray1+[i]
					for(VectorPointPair pv1 : pVArrayPlus1.get(i)) {
//						for each (p2,v2) in PVArray2+[i]
						for(VectorPointPair pv2 : pVArrayPlus2.get(i)) {
//							st = time(v2)/time(v1)
							Rational st = new Rational(pv2.getVector().getX(),pv1.getVector().getX());
//							Store (st,1,r1,r2,p1,p2) in SFList
							sFList.add(new SFListItem(st,1,r1,r2,pv1.getPoint(),pv2.getPoint()));
						}
//						for each (p2,v2) in PVArray2-[i]
						for(VectorPointPair pv2 : pVArrayMinus2.get(i)) {
//							st = time(v2)/time(v1)
							Rational st = new Rational(pv2.getVector().getX(),pv1.getVector().getX());
//							Store (st,-1,r1,r2,p1,p2) in SFList
							sFList.add(new SFListItem(st,-1,r1,r2,pv1.getPoint(),pv2.getPoint()));
						}
					}
				}
//				For i = 0 to size(PVArray1-)-1
				for(int i = 0; i < pVArrayMinus1.size()-1; i++) {
//					for each (p1,v1) in PVArray1-[i]
					for(VectorPointPair pv1 : pVArrayMinus1.get(i)) {
//						for each (p2,v2) in PVArray2+[i]
						for(VectorPointPair pv2 : pVArrayPlus2.get(i)) {
//							st = time(v2)/time(v1)
							Rational st = new Rational(pv2.getVector().getX(),pv1.getVector().getX());
//							Store (st,-1,r1,r2,p1,p2) in SFList
							sFList.add(new SFListItem(st,-1,r1,r2,pv1.getPoint(),pv2.getPoint()));
						}
//						for each (p2,v2) in PVArray2-[i]
						for(VectorPointPair pv2 : pVArrayMinus2.get(i)) {
//							st = time(v2)/time(v1)
							Rational st = new Rational(pv2.getVector().getX(),pv1.getVector().getX());
//							Store (st,1,r1,r2,p1,p2) in SFList
							sFList.add(new SFListItem(st,1,r1,r2,pv1.getPoint(),pv2.getPoint()));
						}
					}
				}
			}
		}
//		Sort SFList lexicographically
//		DONE - sFList is a TreeSet and SFListItem is Comparable<SFListItem>
//		Segment SFList at positions where r2 changes
//		The p1s in each segment make up the MSP for <st, sp, r1, r2> in that segment.
//		The set of p2s in each segment give the MSP for <-st, sp, r2, r1> in that segment.
		ArrayList<SFListItem> sFListArray = new ArrayList<SFListItem>(sFList);
		TreeSet<MSP> msps = new TreeSet<MSP>();
		SFListItem sf = sFListArray.get(0);
		MSP msp = new MSP(sf.timeScaleFactor,sf.pitchScaleFactor,sf.r1,sf.r2,sf.p1,sf.p2);
		//p1 and p2 just given to start off object and image patterns
		
		for(int i = 1; i < sFListArray.size(); i++) {
			sf = sFListArray.get(i);
			if (sf.timeScaleFactor.equals(msp.timeScaleFactor) &&
					sf.pitchScaleFactor == msp.pitchScaleFactor &&
					sf.r1.equals(msp.r1) &&
					sf.r2.equals(msp.r2))
				msp.add(sf.p1, sf.p2);
			else {
				msps.add(msp);
				msp = new MSP(sf.timeScaleFactor,sf.pitchScaleFactor,sf.r1,sf.r2,sf.p1,sf.p2);
			}
		}
		msps.add(msp);
		
//		Reset SFList
		sFList = new TreeSet<SFListItem>();

//		For time scale factor less than 0 - i.e., retrograde and retrograde inversions
//		Now the offset time for each object point maps to the onset time of the image point
//		In order to allow for image notes to have different durations from their corresponding
//		object notes, we ignore the image offsets and thereore the object onsets.
//		For each object pattern reference point, r1, in the dataset
//		r1 is the lexicographically least point in the MSP
		for(int ir1 = 0; ir1 < pointsArray.size(); ir1++) {
			Point r1 = pointsArray.get(ir1);
//			For each image pattern reference point, r2
//			r2 is the image of the lexicographically least point in the translation 
//				of the object pattern that is retrograded
//			r2's onset time is the image of r1's offset time
//			We can ignore offset time of r2.
			for(int ir2 = ir1; ir2 < pointsArray.size(); ir2++) {
//				Reset PVArrays
				for(ArrayList<ArrayList<VectorPointPair>> pvArray : pvArrays)
					for(int i = 0; i <= maxIndex; i++)
						pvArray.set(i,new ArrayList<VectorPointPair>());
//				For each onset point, p1, other than r1, that is lexicographically later than r1
				for(int ip1 = ir1 + 1; ip1 < pointsArray.size(); ip1++) {
					Point p1 = pointsArray.get(ip1);
					Vector v1 = new Vector(r1,p1);
//					Store (p1,v1) in a list in PVArray1+ or PVArray1-, depending on sign of pitch(v1), at index abs(pitch(v1))
					if (v1.getY() < 0)
						pVArrayMinus1.get(-v1.getY()).add(new VectorPointPair(v1,p1));
					else
						pVArrayPlus1.get(v1.getY()).add(new VectorPointPair(v1,p1));					
				}
//				For each offset point, p2, other than r2, that occurs at the same time or before r2
//					v2 = p2 - r2
//					Store (p2,v2) in a list in PVArray2+ or PVArray2-, depending on sign of pitch(v2), at index abs(pitch(v2))
//				For i = 0 to size(PVArray1+)-1
//					for each (p1,v1) in PVArray1+[i]
//						for each (p2,v2) in PVArray2+[i]
//							st = time(v2)/time(v1)
//							if d(p2)/d(p1) == st
//								Store (st,1,r1,r2,p1,p2) in SFList
//						for each (p2,v2) in PVArray2-[i]
//							st = time(v2)/time(v1)
//							if d(p2)/d(p1) == st
//								Store (st,-1,r1,r2,p1,p2) in SFList
//				For i = 0 to size(PVArray1-)-1
//					for each (p1,v1) in PVArray1-[i]
//						for each (p2,v2) in PVArray2+[i]
//							st = time(v2)/time(v1)
//							if d(p2)/d(p1) == st
//								Store (st,-1,r1,r2,p1,p2) in SFList
//						for each (p2,v2) in PVArray2-[i]
//							st = time(v2)/time(v1)
//							if d(p2)/d(p1) == st
//								Store (st,1,r1,r2,p1,p2) in SFList
			}
		}
//		Sort SFList lexicographically
//		Segment SFList at positions where r2 changes: the p1s in each segment make up the MSP for the st, sp, r1 and r2 in that segment
		
		
	}
	
}
