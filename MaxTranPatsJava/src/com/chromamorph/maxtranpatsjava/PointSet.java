package com.chromamorph.maxtranpatsjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import javax.sound.midi.InvalidMidiDataException;

import com.chromamorph.notes.Note;
import com.chromamorph.notes.Notes;
import com.chromamorph.points022.NoMorpheticPitchException;
import com.chromamorph.points022.SCALEXIA3Encoding;
import com.chromamorph.points022.SCALEXIA3Encoding.PVF;

public class PointSet implements Comparable<PointSet>{

	public static long TIME_AT_START_OF_COMPUTING_HETERO_OS = 0l;
	public static boolean COMPUTE_HETERO_OS_COMPLETED = false;
	public static long TIME_LIMIT = 1000 * 60 * 30; // 30 minutes
	public static boolean NO_TIME_LIMIT = true;

	private Long ticksPerSecond = null;

	private TreeSet<Point> points = new TreeSet<Point>();
	private ArrayList<Point> pointsArray;
	private boolean pointsArrayNeedsResetting = false;
	private TreeSet<TransformationPointSetPair> mtps;
	private ArrayList<TransformationPointSetPair>[] sizeMTPSetArray;
	private ArrayList<Integer> mtpSizes = new ArrayList<Integer>();
	private ArrayList<OccurrenceSet>[] mtpOccurrenceSets;
	private ArrayList<OccurrenceSet> sortedOccurrenceSets;
	private TreeSet<TransformationClass> transformationClasses;
	@SuppressWarnings("unused")
	private long pointComplexity = -1;
	private Encoding encoding = null;

	public void setEncoding(ArrayList<OccurrenceSet> occurrenceSets) {
		this.encoding = new Encoding(occurrenceSets);
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public Long getTicksPerSecond() {
		return ticksPerSecond;
	}

	public void setTicksPerSecond(Long ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}

	public int getDimensionality() {
		if (!getPoints().isEmpty())
			return getPoints().first().size(); //Assume all points in PointSet have same dimensionality.
		return 0;
	}

	public PointSet() {
		resetPointsArray();
	}

	public PointSet(Point... points) {
		for(Point p : points)
			add(p);
		resetPointsArray();
	}

	public PointSet(String pointSetString) throws InvalidArgumentException {
		if (!pointSetString.equals("P()")) {
			if (!pointSetString.startsWith("P(p(") || !pointSetString.endsWith("))"))
				throw new InvalidArgumentException("PointSet(String) constructor called with invalid argument:\n"+pointSetString);
			String pointsString = pointSetString.replace("),p(", ";");
			pointsString = pointsString.substring(4, pointsString.length()-2);
			String[] ar = pointsString.split(";");
			for(String s : ar) {
				String[] a = s.split(",");
				Double x = Double.parseDouble(a[0]);
				Double y = Double.parseDouble(a[1]);
				add(new Point(x,y));
			}
		}
		resetPointsArray();
		pointComplexity = -1;
	}

	public PointSet(File file) throws IOException, DimensionalityException {
		this(file, false, false, null);
	}

	public PointSet(File file, boolean pitchSpell) throws IOException, DimensionalityException {
		this(file, pitchSpell, false, null);
	}

	public PointSet(
			File file, 
			boolean pitchSpell, 
			boolean midTimePoint, 
			String dimensionMask) throws IOException, DimensionalityException {
		if (file.getName().toLowerCase().endsWith(".pts")) {
			makePointSetFromPTSFile(file);
			return;
		}
		if (file.getName().toLowerCase().endsWith(".mid")) {
			makePointSetFromMIDIFile(file, pitchSpell, midTimePoint, dimensionMask);
			return;
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String l;
		while ((l = br.readLine()) != null)
			sb.append(l);
		br.close();
		String fileText = sb.toString().trim();
		if (fileText.startsWith("(") && fileText.endsWith(")"))
			makePointSetFromLispString(fileText);
		pointComplexity = -1;
	}

	private void getPointSetFromNotes(
			Notes notes, 
			boolean diatonicPitch,
			boolean midTimePoint,
			String dimensionMask) throws NoMorpheticPitchException {
		setTicksPerSecond(notes.getTicksPerSecond());
		//		System.out.println("in PointSet, ticksPerSecond == "+ticksPerSecond);
		for(Note note : notes.getNotes()) {
			double onset = (double)note.getOnset();
			if (midTimePoint)
				onset += note.getDuration() * 0.5;
			Integer voice = note.getVoice();
			Long duration = note.getDuration();
			if (voice == null)
				voice = note.getChannel();
			if (diatonicPitch) {
				Integer morpheticPitch = note.getPitch().getMorpheticPitch();
				if (morpheticPitch == null)
					morpheticPitch = note.getComputedPitch().getMorpheticPitch();
				if (morpheticPitch == null)
					throw new NoMorpheticPitchException("The following note has no morphetic pitch: "+note);

				points.add(new Point(
						dimensionMask.charAt(0) == '1'?onset:null,
								dimensionMask.charAt(1) == '1'?(double)morpheticPitch:null,
										dimensionMask.charAt(2) == '1'?(double)voice:null,
												dimensionMask.charAt(3) == '1'?(double)duration:null));
			}
			else
				points.add(new Point(
						dimensionMask.charAt(0) == '1'?onset:null,
								dimensionMask.charAt(1) == '1'?(double)note.getMidiNoteNumber():null,
										dimensionMask.charAt(2) == '1'?(double)voice:null,
												dimensionMask.charAt(3) == '1'?(double)duration:null));
		}
		//		We want to avoid having points with non-integer x values, specifically
		//		we want to avoid having the ".5" that results when a duration is an
		//		odd number of tatums. So we'll check to see if any of the points have ".5"
		//		x values, and if there are any that do, we'll multiply all the onsets by 2.
		boolean zeroPoint5Present = false;
		for(Point p : points)
			if (p.get(0)%1.0 == 0.5) {
				zeroPoint5Present = true;
				break;
			}
		if (zeroPoint5Present)
			for (Point p : points)
				p.set(0, 2 * p.get(0));
	}

	private void makePointSetFromMIDIFile(File file, boolean pitchSpell, boolean midTimePoint, String dimensionMask) {
		Notes notes;
		try {
			notes = Notes.fromMIDI(file.getAbsolutePath(), pitchSpell);
			getPointSetFromNotes(notes, pitchSpell, midTimePoint, dimensionMask);
		} catch (InvalidMidiDataException | IOException | NoMorpheticPitchException e) {
			e.printStackTrace();
		}
		resetPointsArray();
		pointComplexity = -1;
	}

	private void makePointSetFromPTSFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String l = br.readLine();
			while(l != null) {
				points.add(new Point(l));
				l = br.readLine();
			}
			br.close();
			resetPointsArray();
			pointComplexity = -1;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PointSet(PointSequence seq) {
		for(Point p : seq.getPoints()) add(p);
		resetPointsArray();
	}

	private void makePointSetFromLispString(String s) throws DimensionalityException {
		String s2 = s.substring(1,s.length()-1).trim();
		String[] a = s2.split("\\)");
		int dim = 0;
		for(String str : a) {
			Point p = new Point(str.trim()+")");
			if (dim == 0) 
				dim = p.size();
			else if (p.size() != dim)
				throw new DimensionalityException("Dataset contains points with different dimensionality: dimensionality of first point is "+dim+", but dataset contains following point: "+p);
			points.add(p);
		}
		resetPointsArray();
		pointComplexity = -1;
	}

	private void resetPointsArray() {
		pointsArray = new ArrayList<Point>();
		for(Point p : points) pointsArray.add(p);
		pointsArrayNeedsResetting = false;
	}

	public ArrayList<Point> getPointsArray() {
		if (pointsArrayNeedsResetting) resetPointsArray();
		return pointsArray;
	}

	public TreeSet<Point> getPoints() {
		return points;
	}

	public int size() {
		return points.size();
	}

	public Point get(int i) {
		if (i >= size())
			throw new IllegalArgumentException("Point.get() called with value of i that is too large (must be less than "+size()+")");
		if (pointsArrayNeedsResetting) resetPointsArray();
		return pointsArray.get(i);
	}

	public void add(Point p) {
		points.add(p);
		pointsArrayNeedsResetting = true;
		pointComplexity = -1;
	}

	public void remove(Point p) {
		points.remove(p);
		pointsArrayNeedsResetting = true;
		pointComplexity = -1;
	}

	public void addAll(PointSequence seq) {
		for(Point p : seq.getPoints()) add(p);
		pointsArrayNeedsResetting = true;
		pointComplexity = -1;
	}

	public void addAll(PointSet ps) {
		for(Point p : ps.getPoints()) add(p);
		pointsArrayNeedsResetting = true;
		pointComplexity = -1;
	}

	@Override
	public int compareTo(PointSet o) {
		if (o == null) return 1;
		int d = size() - o.size();
		if (d != 0) return d;
		//		So d == 0 : this point set and o are the same size
		for(int i = 0; i < size(); i++) {
			d = get(i).compareTo(o.get(i));
			if (d != 0) return d;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof PointSet)) return false;
		return compareTo((PointSet)obj) == 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("P(");
		if (points.size() > 0) sb.append(points.first());
		for(Point p : points.tailSet(points.first(),false))
			sb.append(","+p);
		sb.append(")");
		return sb.toString();
	}

	private ArrayList<PointSequence> computeObjectBases(int basisSize) {
		ArrayList<PointSequence> bases = new ArrayList<PointSequence>();
		int[] indices = new int[basisSize];
		int[] maxIndices = new int[basisSize];
		for(int i = 0; i < basisSize; i++)
			indices[i] = i;
		for(int i = 0; i < basisSize; i++)
			maxIndices[basisSize - i - 1] = size() - i - 1;
		boolean finished = false;
		while (!finished) {
			PointSequence seq = new PointSequence();
			for(int j = 0; j < basisSize; j++)
				seq.add(get(indices[j]));
			bases.add(seq);
			if (maxIndices[basisSize-1] != indices[basisSize-1])
				++indices[basisSize-1];
			else {
				//				Final index is at maximum value

				//				Work backwards from end of indices and find first index that is not 
				//				at its maximum value
				int firstNonMaxIndex = basisSize - 2;
				while((firstNonMaxIndex >= 0) && (indices[firstNonMaxIndex] == maxIndices[firstNonMaxIndex])) 
					firstNonMaxIndex--;
				if (firstNonMaxIndex == -1)
					finished = true;
				else {
					++indices[firstNonMaxIndex];
					for(int i= 1, j = firstNonMaxIndex+1; j < basisSize; i++, j++)
						indices[j] = indices[firstNonMaxIndex] + i;
				}
			}
		}
		return bases;
	}


	public void addTransformationClass(TransformationClass transformationClass) {
		this.transformationClasses.add(transformationClass);
	}

	public void addTransformationClasses(TransformationClass[] transformationClasses) {
		if (this.transformationClasses == null)
			this.transformationClasses = new TreeSet<TransformationClass>();
		for(TransformationClass tc : transformationClasses)
			this.transformationClasses.add(tc);
	}

	//	public long getPointComplexity() {
	//		if (pointComplexity != -1) return pointComplexity;
	//		long pc = 0;
	//		for(int coordIndex = 0; coordIndex < getDimensionality(); coordIndex++) {
	//			TreeSet<Double> valsForThisDimension = new TreeSet<Double>();
	//			for (Point p : getPoints()) {
	//				valsForThisDimension.add(p.get(coordIndex));
	//			}
	//			ArrayList<Long> intValues = new ArrayList<Long>();
	//			for(Double d : valsForThisDimension)
	//				intValues.add((long)Math.round(d*1000000000));
	//			long minValForThisDim = intValues.get(0);
	//			for(int i = 0; i < intValues.size(); i++)
	//				intValues.set(i, intValues.get(i) - minValForThisDim);
	//			long gcd = Utility.gcd(intValues);
	//			long maxIndex = intValues.get(intValues.size()-1)/gcd;
	//			long complexityForThisDim = (int)Math.ceil(Math.log1p(maxIndex)/Math.log(2.0));
	//			pc += complexityForThisDim;
	//		}
	//		pointComplexity = pc;
	//		return pc;
	//	}


	public com.chromamorph.points022.Point getPoint022(Point point) {
		long x = point.get(0).longValue();
		int y = point.get(1).intValue();
		com.chromamorph.points022.Point p022 = new com.chromamorph.points022.Point(x,y);
		return p022;
	}

	public com.chromamorph.points022.PointSet getPoints022PointSet() {
		com.chromamorph.points022.PointSet ps022 = new com.chromamorph.points022.PointSet();
		for(Point p : getPoints())
			ps022.add(getPoint022(p));
		return ps022;
	}

	public void computeMTPsWithScalexia(int minSize) {
		com.chromamorph.points022.PointSet dataset = getPoints022PointSet();

		SCALEXIA3Encoding scalexiaEnc = new SCALEXIA3Encoding(
				dataset, 
				minSize, 
				true, //includeInversions, 
				0.5, // minCompactness, 
				1.0 // maxProportionOfObjectPatternInImagePattern
				);

		//		Now we have to convert the SCALEXIA3Encoding into a TreeSet of TransformationPointSetPairs
		//		stored in this.mtps

		mtps = new TreeSet<TransformationPointSetPair>();
		for(PVF pvf : scalexiaEnc.getS()) {
			Transformation transformation = new Transformation(new F_2STR(),pvf.getSigmaForF2STR());
			PointSet pattern = new PointSet();
			com.chromamorph.points022.PointSet ps022 = pvf.getPattern();
			for(com.chromamorph.points022.Point p022 : ps022.getPoints())
				pattern.add(new Point(p022.getX()*1.0,p022.getY() * 1.0));
			pattern.resetPointsArray();
			mtps.add(new TransformationPointSetPair(transformation, pattern));
		}
	}

	public void computeMaximalTransformablePatterns(int minSize) throws NoTransformationClassesDefinedException {
		if (transformationClasses == null)
			throw new NoTransformationClassesDefinedException("No transformation classes defined! Add some transformation classes using addTransformationClasses() method.");
		TreeSet<TransformationPointSequencePair> transformationObjectBasisPairs = new TreeSet<TransformationPointSequencePair>();
		for(TransformationClass tc : transformationClasses) {
			int basisSize = tc.getBasisSize();
			ArrayList<PointSequence> objectBases = computeObjectBases(basisSize);
			int[][] perms = Utility.computePermutationIndexSequences(basisSize);
			for(int objIndex = 0; objIndex < objectBases.size(); objIndex++) {
				PointSequence objectBasis = objectBases.get(objIndex);
				for(int imgIndex = objIndex; imgIndex < objectBases.size(); imgIndex++) {
					PointSequence imageBasis = objectBases.get(imgIndex);
					for(int[] perm : perms) {
						PointSequence imgBasisPerm = new PointSequence();
						for(int i = 0; i< basisSize; i++)
							imgBasisPerm.add(imageBasis.get(perm[i]));
						ArrayList<Transformation> transformations = Transformation.getTransformations(tc, objectBasis, imgBasisPerm);
						for(Transformation transformation : transformations) {
							transformationObjectBasisPairs.add(new TransformationPointSequencePair(transformation,objectBasis));
							transformationObjectBasisPairs.add(new TransformationPointSequencePair(transformation.getInverse(),imageBasis));
							tc.addTransformationInstance(transformation);
							tc.addTransformationInstance(transformation.getInverse());
						}
					}
				}
			}
		}

		//		Compute transformation class sigma complexities
		//		for(TransformationClass tc : transformationClasses) {
		//			tc.getSigmaComplexity();
		//		}

		mtps = new TreeSet<TransformationPointSetPair>();
		if (transformationObjectBasisPairs.size() > 0) {
			PointSet ps = null;
			Transformation f = null;
			for(TransformationPointSequencePair tpsp : transformationObjectBasisPairs ) {
				if (ps == null && f == null) {
					f = tpsp.getTransformation();
					ps = new PointSet(tpsp.getPointSequence());
				} else if (tpsp.getTransformation().equals(f)) {
					ps.addAll(tpsp.getPointSequence());
				} else {
					if (ps.size() >= minSize)
						mtps.add(new TransformationPointSetPair(f,ps));
					f = tpsp.getTransformation();
					ps = new PointSet(tpsp.getPointSequence());
				}
			}
			if (ps.size() >= minSize)
				mtps.add(new TransformationPointSetPair(f,ps));
		}
	}

	public boolean contains(Point point) {
		return getPoints().contains(point);
	}

	public boolean contains(PointSet pointSet) {
		for(Point p : pointSet.getPoints())
			if (!getPoints().contains(p)) return false;
		return true;
	}

	public PointSet intersection(PointSet pointSet) {
		if (pointsArrayNeedsResetting) resetPointsArray();
		PointSet ps1, ps2;
		if (pointSet.size() < size()) {
			ps1 = pointSet;
			ps2 = this;
		} else {
			ps1 = this;
			ps2 = pointSet;
		}
		PointSet interSet = new PointSet();
		for(Point p : ps1.getPoints())
			if (ps2.contains(p))
				interSet.add(p);
		interSet.resetPointsArray();
		return interSet;
	}

	public boolean checkMaximalTransformablePatterns() throws Exception {
		if (pointsArrayNeedsResetting) resetPointsArray();
		if (mtps == null)
			throw new Exception("checkMaximalTransformablePatterns() called before MTPs have been computed!");
		for(TransformationPointSetPair mtp : mtps) {
			PointSet mtp2 = mtp.getTransformation().getInverse().phi(this).intersection(this);
			if (!mtp2.equals(mtp.getPointSet())) {
				System.out.println("Incorrectly computed MTP.\n Computed MTP: "+mtp+"\nCorrect MTP: "+mtp2);
				return false;
			} 
			//			else
			//				System.out.println(mtp.getPointSet()+"\n"+mtp2+"\n");
		}
		return true;
	}

	public TreeSet<TransformationPointSetPair> getMTPs() {
		return mtps;
	}

	@SuppressWarnings("unchecked")
	public void computeSizeMTPSetArray() {
		sizeMTPSetArray = (ArrayList<TransformationPointSetPair>[])new ArrayList[size()+1];
		for(TransformationPointSetPair mtp : getMTPs()) {
			int n = mtp.getPointSet().size();
			if (sizeMTPSetArray[n] == null) {
				mtpSizes.add(n);
				sizeMTPSetArray[n] = new ArrayList<TransformationPointSetPair>();
			}
			sizeMTPSetArray[n].add(mtp);
		}
		Collections.sort(mtpSizes);

		//		Sort each list of equally-sized MTPs in sizeMTPSetArray giving priority to the pattern,
		//		so that MTPs with the same pattern are adjacent in each resulting list
		for(int size : mtpSizes) {
			Collections.sort(sizeMTPSetArray[size], new Comparator<TransformationPointSetPair>() {

				@Override
				public int compare(TransformationPointSetPair o1, TransformationPointSetPair o2) {
					if (o1 == null && o2 == null) return 0;
					if (o1 == null) return -1;
					if (o2 == null) return 1;

					int d = o1.getPointSet().compareTo(o2.getPointSet());
					if (d != 0) return d;

					return o1.getTransformation().compareTo(o2.getTransformation());
				}

			});
		}
	}

	/**
	 * Assumes that TransformationPointSetPairs are sorted by pattern within each subset containing
	 * patterns of a specific size.
	 */
	@SuppressWarnings("unchecked")
	public void computeMTPOccurrenceSets() {
		mtpOccurrenceSets = (ArrayList<OccurrenceSet>[])new ArrayList[size()+1];
		for(int size : mtpSizes) {
			mtpOccurrenceSets[size] = new ArrayList<OccurrenceSet>();
			ArrayList<TransformationPointSetPair> mtpsForThisSize = sizeMTPSetArray[size];
			PointSet currentPattern = mtpsForThisSize.get(0).getPointSet();
			OccurrenceSet currentMergedMTP = new OccurrenceSet(currentPattern,this);
			currentMergedMTP.addTransformation(mtpsForThisSize.get(0).getTransformation());
			for(int i = 1; i < mtpsForThisSize.size(); i++) {
				TransformationPointSetPair thisMTP = mtpsForThisSize.get(i);
				if (thisMTP.getPointSet().equals(currentPattern))
					currentMergedMTP.addTransformation(thisMTP.getTransformation());
				else {
					mtpOccurrenceSets[size].add(currentMergedMTP);
					currentPattern = thisMTP.getPointSet();
					currentMergedMTP = new OccurrenceSet(currentPattern,this);
					currentMergedMTP.addTransformation(thisMTP.getTransformation());
				}
			}
			mtpOccurrenceSets[size].add(currentMergedMTP);
		}
	}

	public void computeSuperMTPs() {
		for(int i = 0; i < mtpSizes.size()-1; i++) {
			int size = mtpSizes.get(i);
			ArrayList<OccurrenceSet> mtpsOfThisSize = mtpOccurrenceSets[size];
			for(OccurrenceSet mtp : mtpsOfThisSize) {
				for (int j = i+1;j < mtpSizes.size();j++) {
					ArrayList<OccurrenceSet> largerMTPs = mtpOccurrenceSets[mtpSizes.get(j)];
					for(OccurrenceSet largerMTP : largerMTPs) {
						PointSet largerPattern = largerMTP.getPattern();
						if (largerPattern.contains(mtp.getPattern()))
							mtp.addSuperMTP(largerMTP);
					}
				}
			}
		}
	}


	public void computeHeterogeneousOccurrenceSets() throws TimeOutException {

		for (int size : mtpSizes) {
			for (OccurrenceSet mtp : mtpOccurrenceSets[size]) {
				ArrayList<OccurrenceSet> superMTPs = mtp.getSuperMTPs();
				if (superMTPs != null)
					for(OccurrenceSet superMTP : superMTPs) {
						mtp.addAllTransformations(superMTP.getTransformations());
					}
				mtp.setSuperMTPs(null);
			}
		}

		//		int processors = Runtime.getRuntime().availableProcessors();
		//        System.out.println(Integer.toString(processors) + " processor"
		//                + (processors != 1 ? "s are " : " is ")
		//                + "available");
		// 
		//		
		//		for(int size : mtpSizes) {
		////			Each iteration runs on all mtps of a particular size
		//			AddSuperMTPTransformationsRecursiveAction addSuperMTPTransformations = new AddSuperMTPTransformationsRecursiveAction(mtpOccurrenceSets[size]);
		//			ForkJoinPool pool = new ForkJoinPool();
		//			pool.invoke(addSuperMTPTransformations);
		////			for (OccurrenceSet mtp : occurrenceSets[size]) {
		//////				This loop can be done in parallel
		//////				System.out.println(mtp);
		////				mtp.addAllTransformations(mtp.getSuperMTPTransformations());
		////				mtp.setSuperMTPs(null);
		////			}
		//		}
		//		System.gc();
	}

	public void computeSortedOccurrenceSets(Comparator<OccurrenceSet> comparator) {
		sortedOccurrenceSets = new ArrayList<OccurrenceSet>();
		for(int size : mtpSizes) {
			for(OccurrenceSet os : mtpOccurrenceSets[size]) {
				sortedOccurrenceSets.add(os);
			}
		}
		Collections.sort(sortedOccurrenceSets, comparator);
	}

	public void removeRedundantTransformations() {
		//		For each MTP, remove more complex transformations that map the pattern
		//		onto the same image pattern as less complex transformations
		for(int size : mtpSizes) {
			for (OccurrenceSet mtp : mtpOccurrenceSets[size]) {
				mtp.removeRedundantTransformations();
			}
		}
	}

	public boolean isEmpty() {
		return getPoints().isEmpty();
	}

	public void removeDuplicateOccurrenceSets() {
		for(int size : mtpSizes) {
			TreeSet<OccurrenceSet> sortedDeDupedList = new TreeSet<OccurrenceSet>();
			for(OccurrenceSet os : mtpOccurrenceSets[size]) {
				sortedDeDupedList.add(os);
			}
			mtpOccurrenceSets[size] = new ArrayList<OccurrenceSet>();
			System.gc();
			mtpOccurrenceSets[size].addAll(sortedDeDupedList);
		}
		//		for(int i = 0; i < occurrenceSets[size].size() - 1; i++) {
		//			for(int j = i+1; j < occurrenceSets[size].size(); j++) {
		//				OccurrenceSet os1 = occurrenceSets[size].get(i);
		//				OccurrenceSet os2 = occurrenceSets[size].get(j);
		//				if (os1.getPattern().equals(os2.getPattern())) {
		//					occurrenceSets[size].remove(j);
		//					j--;
		//				} else {
		//					for(Transformation f : os1.getTransformations()) {
		//						if (os2.getPattern().equals(f.phi(os1.getPattern()))) {
		//							occurrenceSets[size].remove(j);
		//							j--;
		//							break;
		//						}
		//					}
		//				}
		//			}
		//		}
	}

	public void removeOccurrenceSetsWithNoTransformations() {
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				if (mtpOccurrenceSets[size].get(i).getTransformations().isEmpty()) {
					mtpOccurrenceSets[size].remove(i);
					i--;
				}
			}
	}

	public PointSet setMinus(PointSet pointSet) {
		PointSet result = new PointSet();
		for(Point p : getPoints()) {
			if (!pointSet.contains(p))
				result.add(p);
		}
		result.resetPointsArray();
		return result;
	}

	/**
	 * Assumes sortedOccurenceSets holds a list of OccurrenceSets in descending order of quality.
	 * @throws SuperMTPsNotNullException 
	 * @throws Exception
	 */
	public void computeEncoding() throws SuperMTPsNotNullException {
		ArrayList<OccurrenceSet> encoding = new ArrayList<OccurrenceSet>();

		// Assumes first occurrence set in list is the "best"
		encoding.add(sortedOccurrenceSets.get(0));
		PointSet coveredSet = new PointSet();
		coveredSet.addAll(sortedOccurrenceSets.get(0).getCoveredSet());
		for(int i = 1; i < sortedOccurrenceSets.size(); i++) {
			OccurrenceSet os = sortedOccurrenceSets.get(i);
			PointSet diffSet = os.getCoveredSet().setMinus(coveredSet);
			int osEncodingLength = os.getPatternLength() + os.getTransformationSetLength();
			if (osEncodingLength < diffSet.getDimensionality()*diffSet.size()) {
				encoding.add(os);
				coveredSet.addAll(os.getCoveredSet());
			}
		}
		PointSet residualSet = this.setMinus(coveredSet);
		if (!residualSet.isEmpty()) {
			OccurrenceSet residualOccurrenceSet = new OccurrenceSet(residualSet, this);
			encoding.add(residualOccurrenceSet);
		}
		setEncoding(encoding);
	}

	public static void encodePointSet(PointSet ps, String outputFileName, TransformationClass[] transformationClasses) throws Exception {
		encodePointSet(ps, outputFileName, transformationClasses, false, 3);
	}
	public static void encodePointSet (
			PointSet ps, 
			String outputFilePath, 
			TransformationClass[] transformationClasses,
			boolean useScalexia,
			int minSize) throws TimeOutException, FileNotFoundException, NoTransformationClassesDefinedException, SuperMTPsNotNullException {
		ArrayList<LogInfo> log = new ArrayList<LogInfo>();


		ps.addTransformationClasses(transformationClasses);		

		log.add(new LogInfo("computeMaximalTransformablePatterns starts", true));
		if (useScalexia)
			ps.computeMTPsWithScalexia(minSize);
		else
			ps.computeMaximalTransformablePatterns(minSize);
		log.add(new LogInfo("computeMaximalTransformablePatterns ends", true));

		int numMTPsBeforeRemoval = ps.getMTPs().size();
		ps.computeSizeMTPSetArray();
		log.add(new LogInfo("computeSizeMTPSetArray ends", true));

		ps.computeMTPOccurrenceSets();
		log.add(new LogInfo("computePatternTransformationSetPairs ends", true));

		ps.computeSuperMTPs();
		log.add(new LogInfo("computeSuperMTPs ends", true));

		TIME_AT_START_OF_COMPUTING_HETERO_OS = Calendar.getInstance().getTimeInMillis();
		COMPUTE_HETERO_OS_COMPLETED = false;
		ps.computeHeterogeneousOccurrenceSets();
		log.add(new LogInfo("computeHeterogeneousOccurrenceSets ends", true));

		ps.removeDuplicateOccurrenceSets();
		log.add(new LogInfo("removeDuplicateOccurrenceSets ends", true));

		ps.removeRedundantTransformations();
		log.add(new LogInfo("removeRedundantTransformations ends", true));

		ps.removeOccurrenceSetsWithNoTransformations();
		log.add(new LogInfo("removeOccurrenceSetsWithEmptyTransformationSets ends", true));

		ps.computeSortedOccurrenceSets(OccurrenceSet.DECREASING_CF_THEN_COVERAGE_COMPARATOR);
		log.add(new LogInfo("computeSortedOccurrenceSets ends", true));

		ps.computeEncoding();
		log.add(new LogInfo("computeEncoding ends", true));

		//			ps.computeSortedOccurrenceSets(OccurrenceSet.DECREASING_CF_TIMES_COVERAGE_COMPARATOR);			
		//			Utility.println(output, "\nOccurrence sets sorted decreasing by cf x coverage:");
		//			int j = 0;
		//			for(OccurrenceSet os : ps.sortedOccurrenceSets) {
		//				String s = String.format("%5d. %s", ++j, os);
		//				Utility.println(output, s);
		//			}
		//
		//			ps.computeSortedOccurrenceSets(OccurrenceSet.DECREASING_CF_THEN_COVERAGE_COMPARATOR);			
		//			Utility.println(output, "\nOccurrence sets sorted decreasing by cf then coverage:");
		//			j = 0;
		//			for(OccurrenceSet os : ps.sortedOccurrenceSets) {
		//				String s = String.format("%5d. %s", ++j, os);
		//				Utility.println(output, s);
		//			}
		//
		//			ps.computeSortedOccurrenceSets(OccurrenceSet.DECREASING_COVERAGE_THEN_CF_COMPARATOR);			
		//			Utility.println(output, "\nOccurrence sets sorted decreasing by coverage then cf:");
		//			j = 0;
		//			for(OccurrenceSet os : ps.sortedOccurrenceSets) {
		//				String s = String.format("%5d. %s", ++j, os);
		//				Utility.println(output, s);
		//			}

		log.add(new LogInfo("Program ends\n\n", true));

		PrintWriter output = new PrintWriter(outputFilePath);
		Utility.println(output, ps.getEncoding());

		Utility.println(output, "\n\nLog:");
		for(int i = 0; i < log.size(); i++) {
			Utility.println(output, log.get(i));
		}

		Utility.println(output, "Number of points: " + ps.size());
		Utility.println(output, "Number of MTPs before removal: " + numMTPsBeforeRemoval);
		Utility.println(output, "Number of OSs after removal: " + ps.sortedOccurrenceSets.size());

		output.close();

	}

	public double getMax(int dimension) {
		double max = getPoints().first().get(dimension);
		for(Point p : getPoints())
			if (p.get(dimension) > max)
				max = p.get(dimension);
		return max;
	}

	private static void encodePairOfPointSetsFromFiles(
			String filePath1, 
			String filePath2, 
			TransformationClass[] transformationClasses,
			boolean pitchSpell, 
			boolean midTimePoint, 
			String dimensionMask, 
			String outputDirectory,
			boolean useScalexia,
			int minSize,
			int count) {
		String outputFileName = Utility.getOutputPathForPairFileEncoding(outputDirectory, filePath1, filePath2, transformationClasses, count);
		try {
			PointSet ps1 = new PointSet(
					new File(filePath1), 
					pitchSpell, 
					midTimePoint,
					dimensionMask);
			PointSet ps2 = new PointSet(
					new File(filePath2), 
					pitchSpell, 
					midTimePoint,
					dimensionMask);

			//			We're going to form the union of ps1 with ps2 translated by twice the maximum x-value of ps1
			Transformation tran = new Transformation(new F_2T(), Utility.makeSigma(ps1.getMax(0) * 2, 0));
			PointSet translatedPS2 = tran.phi(ps2);
			PointSet ps = new PointSet();
			ps.addAll(ps1);
			ps.addAll(translatedPS2);
			encodePointSet(ps, outputFileName, transformationClasses, useScalexia, minSize);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DimensionalityException e) {
			e.printStackTrace();
		} catch (TimeOutException e) {
			Utility.moveOutputFilesToFailedDir(outputFileName);
			System.out.println("ERROR: Computing of heterogeneous occurrence sets timed out on file, "+new File(outputFileName).getParent());
		} catch (NoTransformationClassesDefinedException e) {
			e.printStackTrace();
		} catch (SuperMTPsNotNullException e) {
			e.printStackTrace();
		} 
	}

	public static void encodePointSetFromFile(
			String fileName, 
			TransformationClass[] transformationClasses, 
			boolean pitchSpell,
			boolean midTimePoint,
			String dimensionMask,
			String outputDir) {
		try {
			String outputFileName = Utility.getOutputFilePath(outputDir, fileName, transformationClasses);
			PointSet ps = new PointSet(
					new File(fileName), 
					pitchSpell, 
					midTimePoint,
					dimensionMask);
			encodePointSet(ps, outputFileName, transformationClasses);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DimensionalityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void encodePointSetFromFile(
			String fileName, 
			TransformationClass[] transformationClasses, 
			boolean pitchSpell,
			boolean midTimePoint,
			String dimensionMask,
			String outputDir,
			boolean useScalexia,
			int minSize) {
		try {
			String outputFileName = Utility.getOutputFilePath(outputDir, fileName, transformationClasses);
			PointSet ps = new PointSet(
					new File(fileName), 
					pitchSpell, 
					midTimePoint,
					dimensionMask);
			encodePointSet(ps, outputFileName, transformationClasses, useScalexia, minSize);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DimensionalityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void compressNLBSingleFiles(int startIndex, int endIndex) {
		String inputDir = "../../nlb20210504/data/nlb/nlb_datasets/annmidi";
		String outputDir = "../../nlb20210504/output/debug-hetero-occ-sets-test";
		String[] nlbFileNames = Utility.getInputFileNames(inputDir);

		TransformationClass[][] transformationClassArrays = new TransformationClass[][] {
			//			new TransformationClass[] {new F_2T()},
			//			new TransformationClass[] {new F_2TR()},
			new TransformationClass[] {new F_2STR()},
			//			new TransformationClass[] {new F_2T(), new F_2TR()},
			//			new TransformationClass[] {new F_2TR(), new F_2STR()},
			//			new TransformationClass[] {new F_2STR(), new F_2T()},
			//			new TransformationClass[] {new F_2T(), new F_2TR(), new F_2STR() }
		};

		for(int i = startIndex; i < nlbFileNames.length && i < endIndex; i++) {
			String fileName = nlbFileNames[i];
			for(TransformationClass[] transformationClassArray : transformationClassArrays)
				encodePointSetFromFile(
						inputDir+"/"+fileName, 
						transformationClassArray, 
						true, // pitchSpell
						true, // midTimePoint
						"1100",
						outputDir,
						true,
						3);
		}
	}

	public static void compressMissingNLBPairFiles() {
		CheckMissingPairFiles.main(null); //Fills out PAIR_FILE_PRESENT array and NLB_FILE_NAMES ArrayList		
		String inputDir = CheckMissingPairFiles.INPUT_DIR;
		String outputDir = CheckMissingPairFiles.ROOT_FOLDER + "output/pair-files-F2STR-with-scalexia-missing";		
		TransformationClass[][] transformationClassArrays = new TransformationClass[][] {
			new TransformationClass[] {new F_2STR()},
		};
		int count = 0;
		for(int i = 0; i < CheckMissingPairFiles.NLB_FILE_NAMES.size() - 1; i++)
			for(int j = i + 1; j < CheckMissingPairFiles.NLB_FILE_NAMES.size(); j++)
				for(TransformationClass[] transformationClassArray : transformationClassArrays) {

					if (!CheckMissingPairFiles.PAIR_FILE_PRESENT[count]) {
						encodePairOfPointSetsFromFiles(
								inputDir+"/"+CheckMissingPairFiles.NLB_FILE_NAMES.get(i),
								inputDir+"/"+CheckMissingPairFiles.NLB_FILE_NAMES.get(j),
								transformationClassArray, 
								true, // pitchSpell
								true, // midTimePoint
								"1100",
								outputDir,
								true,
								3,
								count);		
					}
					count++;
				}
	}

	public static void compressNLBPairFiles(int startIndex, int endIndex) {
		//		String inputDir = "data/nlb/nlb_datasets/annmidi";
		//		String inputDir = "D:\\Repos\\nlb20210504\\data\\nlb\\nlb_datasets\\annmidi";
		String inputDir = "../../nlb20210504/data/nlb/nlb_datasets/annmidi";
		//		String outputDir = "output/nlb-20210504/pair-files-F2STR-with-scalexia-new-mac";
		//		String outputDir = "D:\\Repos\\nlb20210504\\output\\parallel-test";
		String outputDir = "../../nlb20210504/output/debug-hetero-occ-sets-test";

		//		Find file pairs within the range between startIndex and endIndex for which there is
		//		no output file in the outputDir

		String[] existingOutputFilesArray = new File(outputDir).list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (new File(dir, name).isDirectory() && !name.startsWith("."));
			}

		});
		TreeSet<String> existingOutputFiles = new TreeSet<String>();
		for(String name : existingOutputFilesArray)
			existingOutputFiles.add(name.substring(0, name.indexOf("F_")-1));

		System.out.println("\n"+existingOutputFiles.size()+" files in output directory:");
		for(String name : existingOutputFiles)
			System.out.println("  "+name);

		///////////////////////////////

		//		Get input file names in alphabetical order - note it is necessary to sort the names
		//		because the ordering returned by the operating system is different on a Mac from a PC

		String[] nlbFileNamesArray = Utility.getInputFileNames(inputDir);
		ArrayList<String> nlbFileNames = new ArrayList<String>();
		for (String nlbFileName : nlbFileNamesArray)
			nlbFileNames.add(nlbFileName);
		nlbFileNames.sort(null);

		///////////////////////////////

		TransformationClass[][] transformationClassArrays = new TransformationClass[][] {
			//			new TransformationClass[] {new F_2T()},
			//			new TransformationClass[] {new F_2TR()},
			new TransformationClass[] {new F_2STR()},
			//			new TransformationClass[] {new F_2T(), new F_2TR()},
			//			new TransformationClass[] {new F_2TR(), new F_2STR()},
			//			new TransformationClass[] {new F_2STR(), new F_2T()},
			//			new TransformationClass[] {new F_2T(), new F_2TR(), new F_2STR() }
		};

		int count = 0;
		for(int i = 0; i < nlbFileNames.size() - 1; i++)
			for(int j = i + 1; j < nlbFileNames.size(); j++)
				for(TransformationClass[] transformationClassArray : transformationClassArrays) {

					String fn1 = nlbFileNames.get(i);
					fn1 = fn1.replace(".", "-");
					String fn2 = nlbFileNames.get(j);
					fn2 = fn2.replace(".","-");
					String countStr = String.format("%05d", count);
					String outputFilePrefix = countStr+"-"+fn1+"-"+fn2;

					if (!existingOutputFiles.contains(outputFilePrefix) 
							&& count >= startIndex 
							&& (count < endIndex || endIndex == 0)) {
						encodePairOfPointSetsFromFiles(
								inputDir+"/"+nlbFileNames.get(i),
								inputDir+"/"+nlbFileNames.get(j),
								transformationClassArray, 
								true, // pitchSpell
								true, // midTimePoint
								"1100",
								outputDir,
								true,
								3,
								count);		
					}
					count++;
				}
	}

	public static void encodeFile() {
		encodePointSetFromFile(
				"data/nlb/nlb_datasets/annmidi/NLB072912_01.mid", 
				//				"data/test/test/F_2STR-simple-test-dataset.lisp",
				new TransformationClass[] {new F_2STR()}, 
				true, // pitchSpell
				true, // midTimePoint
				"1100",
				"output/nlb-20210504/single-files-with-scalexia",
				true, // useScalexia
				3 //minSize
				);
	}

	private static void renameNLBPairFileOutputFiles() {
		String inputFileDir = "data/nlb/nlb_datasets/annmidi";
		String outputFileDir = "output/nlb-20210504/pair-files-F2STR-with-scalexia";

		String[] nlbFileNamesArray = Utility.getInputFileNames(inputFileDir);
		ArrayList<String> nlbFileNames = new ArrayList<String>();
		for (String nlbFileName : nlbFileNamesArray)
			nlbFileNames.add(nlbFileName);
		nlbFileNames.sort(null);

		String[] existingOutputFilesArray = new File(outputFileDir).list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (new File(dir, name).isDirectory() && !name.startsWith("."));
			}

		});
		TreeSet<String> existingOutputFiles = new TreeSet<String>();
		for(String name : existingOutputFilesArray)
			existingOutputFiles.add(name);

		int count = 0;
		for(int i = 0; i < nlbFileNames.size() - 1; i++)
			for(int j = i + 1; j < nlbFileNames.size(); j++){

				String fn1 = nlbFileNames.get(i);
				fn1 = fn1.replace(".", "-");
				String fn2 = nlbFileNames.get(j);
				fn2 = fn2.replace(".","-");
				String outputFilePrefix = fn1+"-"+fn2;

				String existingFileName = existingOutputFiles.ceiling(outputFilePrefix);

				if (existingFileName != null && existingFileName.startsWith(outputFilePrefix)) {
					String newCountStr = String.format("%05d",count);
					boolean success = new File(outputFileDir+"/"+existingFileName).renameTo(new File(outputFileDir+"/"+newCountStr+"-"+existingFileName));
					if (!success)
						System.out.println("ERROR: Failed to rename "+existingFileName);
				} else {
					System.out.println("ERROR: Failed to find existing file starting with "+outputFilePrefix);
				}
				count++;
			}
	}

	public static void main(String[] args) {
		int start = 25, end = 26;
		//		if (args.length > 0) start = Integer.parseInt(args[0]);
		//		if (args.length > 1) end = Integer.parseInt(args[1]);
//		compressNLBSingleFiles(0, 1);
//		compressNLBPairFiles(start,end);
		compressMissingNLBPairFiles();
		//		encodeFile();
		//		renameNLBPairFileOutputFiles();
	}

}
