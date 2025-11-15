package com.chromamorph.maxtranpatsjava;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JFrame;

import com.chromamorph.maths.Maths;
import com.chromamorph.notes.Note;
import com.chromamorph.notes.Notes;
import com.chromamorph.pitch.Pitch;
import com.chromamorph.points022.DrawPoints;
import com.chromamorph.points022.NoMorpheticPitchException;
import com.chromamorph.points022.SCALEXIA3Encoding;
import com.chromamorph.points022.SCALEXIA3Encoding.PVF;

import processing.core.PApplet;

public class PointSet implements Comparable<PointSet>{

	public static long TIME_AT_START_OF_COMPUTING_HETERO_OS = 0l;
	public static boolean COMPUTE_HETERO_OS_COMPLETED = false;
	public static long TIME_LIMIT = 1000 * 60 * 30; // 30 minutes
	public static boolean NO_TIME_LIMIT = true;
	public static int HASH_TABLE_SIZE = 10000001;

	private Long ticksPerSecond = null;

	private TreeSet<Point> points = new TreeSet<Point>();
	private ArrayList<Point> pointsArray;
	private boolean pointsArrayNeedsResetting = false;
	private TreeSet<TransformationPointSetPair> mtps;
	private ArrayList<TransformationPointSetPair>[] sizeMTPSetArray = null;
	private ArrayList<Integer> mtpSizes = null;
	private ArrayList<OccurrenceSet>[] mtpOccurrenceSets;
	private ArrayList<OccurrenceSet> sortedOccurrenceSets;
	private TreeSet<TransformationClass> transformationClasses;
	@SuppressWarnings("unused")
	private long pointComplexity = -1;
	private Encoding encoding = null;
	private boolean MTM = false;

	private Long tatumsPerBar = null; 
	private Long barOneStartsAt = null;
	private String title = "";
	public static double TIME_SCALE_FACTOR = 1.0;
	public static boolean IS_OSTG = false;
	protected String label = null;

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public boolean isMTM() {
		return MTM;
	}

	public void setMTM(boolean isMTM) {MTM = isMTM;}

	public ArrayList<OccurrenceSet>[] getMTPOccurrenceSets() {
		return mtpOccurrenceSets;
	}

	public ArrayList<Integer> getMTPSizes() {
		return mtpSizes;
	}

	public void setEncoding(ArrayList<OccurrenceSet> occurrenceSets) {
		this.encoding = new Encoding(occurrenceSets, this);
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
		mtpSizes = new ArrayList<Integer>();
		resetPointsArray();
	}

	public PointSet(Point... points) {
		mtpSizes = new ArrayList<Integer>();
		for(Point p : points)
			add(p);
		resetPointsArray();
	}

	public PointSet(String pointSetString) throws InvalidArgumentException {
		mtpSizes = new ArrayList<Integer>();
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
		this(file, false, false, null, false, false);
	}

	public PointSet(File file, boolean pitchSpell) throws IOException, DimensionalityException {
		this(file, pitchSpell, false, null, false, false);
	}

	public PointSet(
			File file, 
			boolean pitchSpell, 
			boolean midTimePoint, 
			String dimensionMask,
			boolean useChroma,
			boolean useMorph) throws IOException, DimensionalityException {
		mtpSizes = new ArrayList<Integer>();
		System.out.println(file.getName().toLowerCase());
		if (file.getName().toLowerCase().endsWith(".pts")) {
			makePointSetFromPTSFile(file);
			return;
		}
		if (file.getName().toLowerCase().endsWith(".mid") || file.getName().toLowerCase().endsWith(".midi")) {
			makePointSetFromMIDIFile(file, pitchSpell, midTimePoint, dimensionMask, useChroma, useMorph);
			return;
		}
		if (file.getName().toLowerCase().endsWith(".opnd")) {
			makePointSetFromOPNDFile(file, pitchSpell, midTimePoint, dimensionMask, useChroma, useMorph);
		}
		setTitle(Paths.get(file.getAbsolutePath()).getFileName().toString());
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String l;
		while ((l = br.readLine()) != null)
			sb.append(l);
		br.close();
		//		String fileText = sb.toString().trim();
		//		if (fileText.startsWith("(") && fileText.endsWith(")"))
		//			makePointSetFromLispString(fileText);
		pointComplexity = -1;
	}

	private void getPointSetFromNotes(
			Notes notes, 
			boolean diatonicPitch,
			boolean midTimePoint,
			String dimensionMask,
			boolean useChroma,
			boolean useMorph) throws NoMorphException, NoMorpheticPitchException, NoChromaException{
		setTicksPerSecond(notes.getTicksPerSecond());
		//		System.out.println("in PointSet, ticksPerSecond == "+ticksPerSecond);
		for(Note note : notes.getNotes()) {
			double onset = (double)note.getOnset();
			double originalOnset = onset;
			if (midTimePoint) {
				onset += note.getDuration() * 0.5;
			}
			Integer voice = note.getVoice();
			Long duration = note.getDuration();
			if (voice == null)
				voice = note.getChannel();
			Point p;
			if (diatonicPitch) {
				Integer morpheticPitch = note.getPitch().getMorpheticPitch();
				if (morpheticPitch == null)
					morpheticPitch = note.getComputedPitch().getMorpheticPitch();
				if (morpheticPitch == null)
					throw new NoMorpheticPitchException("The following note has no morphetic pitch: "+note);
				p = new Point(
						dimensionMask.charAt(0) == '1'?onset:null,
								dimensionMask.charAt(1) == '1'?(double)morpheticPitch:null,
										dimensionMask.charAt(2) == '1'?(double)voice:null,
												dimensionMask.charAt(3) == '1'?(double)duration:null);
			}
			else if (useMorph) {
				Integer morph = note.getPitch().getMorph();
				if (morph == null)
					morph = note.getComputedPitch().getMorph();
				if (morph == null)
					throw new NoMorphException("The following note has no morph: "+note);
				p = new Point(
						dimensionMask.charAt(0) == '1'?onset:null,
								dimensionMask.charAt(1) == '1'?(double)morph:null,
										dimensionMask.charAt(2) == '1'?(double)voice:null,
												dimensionMask.charAt(3) == '1'?(double)duration:null);
			}
			else if (useChroma) {
				Integer chroma = note.getPitch().getChroma();
				if (chroma == null)
					chroma = note.getComputedPitch().getChroma();
				if (chroma == null)
					throw new NoChromaException("The following note has no chroma: "+note);
				p = new Point(
						dimensionMask.charAt(0) == '1'?onset:null,
								dimensionMask.charAt(1) == '1'?(double)chroma:null,
										dimensionMask.charAt(2) == '1'?(double)voice:null,
												dimensionMask.charAt(3) == '1'?(double)duration:null);
			}
			else {
				p = new Point(
						dimensionMask.charAt(0) == '1'?onset:null,
								dimensionMask.charAt(1) == '1'?(double)note.getPitch().getChromaticPitch():null,
										dimensionMask.charAt(2) == '1'?(double)voice:null,
												dimensionMask.charAt(3) == '1'?(double)duration:null);
			}
			p.setDuration(duration);
			p.setOnset((long)originalOnset);
			p.setVoice(voice);
			p.setPitch(note.getPitch());

			points.add(p);
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
			TIME_SCALE_FACTOR = 2.0;
		if (zeroPoint5Present) {
			if (getTatumsPerBar() != null)
				setTatumsPerBar(getTatumsPerBar()*2);
			if (getBarOneStartsAt() != null)
				setBarOneStartsAt(getBarOneStartsAt()*2);
			for (Point p : points)
				p.set(0, 2 * p.get(0));
		}
	}

	private void makePointSetFromMIDIFile(
			File file, 
			boolean pitchSpell, 
			boolean midTimePoint, 
			String dimensionMask,
			boolean useChroma,
			boolean useMorph) {
		Notes notes;
		try {
			notes = Notes.fromMIDI(file.getAbsolutePath(), pitchSpell);
			getPointSetFromNotes(notes, pitchSpell, midTimePoint, dimensionMask, useChroma, useMorph);
		} catch (InvalidMidiDataException | IOException | NoMorpheticPitchException | NoMorphException | NoChromaException e) {
			e.printStackTrace();
		}
		resetPointsArray();
		pointComplexity = -1;
	}

	public Long getTatumsPerBar() {
		return tatumsPerBar;
	}
	public void setTatumsPerBar(Long tatumsPerBar) {
		this.tatumsPerBar = tatumsPerBar;
	}
	public Long getBarOneStartsAt() {
		return barOneStartsAt;
	}
	public void setBarOneStartsAt(Long barOneStartsAt) {
		this.barOneStartsAt = barOneStartsAt;
	}

	private void setTatumsPerBarAndBarOneStartsAt(File opndFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(opndFile));
			String l;
			while((l = br.readLine()) != null) {
				if (l.trim().startsWith("%tatumsPerBar"))
					setTatumsPerBar(Long.parseLong(l.trim().split(" ")[1]));
				else if (l.trim().startsWith("%barOneStartsAt"))
					setBarOneStartsAt(Long.parseLong(l.trim().split(" ")[1]));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makePointSetFromOPNDFile(
			File file, 
			boolean pitchSpell, 
			boolean midTimePoint, 
			String dimensionMask,
			boolean useChroma,
			boolean useMorph) {
		Notes notes;
		setTatumsPerBarAndBarOneStartsAt(file);
		try {
			notes = Notes.fromOPND(file.getAbsolutePath());
			getPointSetFromNotes(notes, pitchSpell, midTimePoint, dimensionMask, useChroma, useMorph);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoMorpheticPitchException | NoChromaException | NoMorphException e) {
			// TODO Auto-generated catch block
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
		mtpSizes = new ArrayList<Integer>();
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

	private synchronized void resetPointsArray() {
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

	public synchronized Point get(int i) {
		if (pointsArrayNeedsResetting) resetPointsArray();
		if (size() != pointsArray.size())
			System.out.println("this.size != pointsArray.size() in PointSet.get(int): this = "+this+"; pointsArray = "+pointsArray+"; this.size() = "+this.size()+"; pointsArray.size() ="+pointsArray.size());
		if (i >= size())
			throw new IllegalArgumentException("Point.get() called with value of i that is too large (must be less than "+size()+")");
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
		resetPointsArray();
		pointComplexity = -1;
	}

	public void addAll(PointSet ps) {
		for(Point p : ps.getPoints()) add(p);
		resetPointsArray();
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

	private  ArrayList<PointSequence> computeObjectBases(int basisSize) {
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


	public  void addTransformationClass(TransformationClass transformationClass) {
		if (this.transformationClasses == null)
			this.transformationClasses = new TreeSet<TransformationClass>();
		this.transformationClasses.add(transformationClass);
	}

	public  void addTransformationClasses(TransformationClass[] transformationClasses) {
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

	public  com.chromamorph.points022.PointSet getPoints022PointSet() {
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

	public  PointSequence computeBasis(int basisSize, long basisIndex) {
		if (basisSize > this.size())
			System.out.println("this point set is "+this+", basisSize = "+basisSize+"; basisIndex = "+basisIndex);
		ArrayList<Integer> basisIndexSequence = null;
		PointSequence basis = new PointSequence();
		try {
			basisIndexSequence = Utility.computeCombinationIndexSequence(basisIndex,basisSize,size());
			//			System.out.println(basisIndexSequence);
			for(int i : basisIndexSequence) {
				if (this.size() == 0) {
					throw new Exception("this.size() == 0 in PointSet.computeBasis() with basisSize = "+basisSize+" and basisIndex = "+basisIndex);
				}
				basis.add(get(i));
			}
		} catch(Exception e) {
			System.out.println(this);
			e.printStackTrace();
			System.exit(1);
		}
		return basis;
	}

	public void computeMaximalTransformedMatchesForkJoin(PointSet pattern, int minSize) throws NoTransformationClassesDefinedException {
		if (transformationClasses == null)
			throw new NoTransformationClassesDefinedException("No transformation classes defined! Add some transformation classes using addTransformationClasses() method.");
		ListOfTransformationPointSetPairs[] mtmArray = new ListOfTransformationPointSetPairs[HASH_TABLE_SIZE];
		for (int i = 0; i < mtmArray.length; i++)
			mtmArray[i] = new ListOfTransformationPointSetPairs();
		for (TransformationClass tc : transformationClasses) {
			int numObjectBases = Utility.computeNumCombinations(pattern.size(), tc.getBasisSize());
			int numImageBases = Utility.computeNumCombinations(size(), tc.getBasisSize());
			int[][] perms = tc.getPerms();
			long numComputations = numObjectBases * numImageBases * perms.length;
			if (!IS_OSTG) 
				System.out.println("basisSize = " + tc.getBasisSize() + "\n" + 
						"numObjectBases = " + numObjectBases + "\n" +
						"numImageBases = " + numImageBases + "\n" + 
						"numPerms = " + perms.length + "\n" +
						"numComputations = " + numComputations);
			ComputeMaximalTransformedMatches action = new ComputeMaximalTransformedMatches(
					pattern, 
					this, 
					tc, 
					mtmArray, 
					0, 
					numComputations, 
					numObjectBases, 
					numImageBases);
			ForkJoinPool.commonPool().invoke(action);
		}
		TreeSet<Integer> hashValues = new TreeSet<Integer>();
		for(int i = 0; i < mtmArray.length; i++) {
			if (!(mtmArray[i].isEmpty()))
				hashValues.add(i);
		}
		mtps = new TreeSet<TransformationPointSetPair>();
		int maxLoad = 0;
		for(int i : hashValues) {
			if (mtmArray[i].size() > maxLoad)
				maxLoad = mtmArray[i].size();
			for (TransformationPointSetPair mtp : mtmArray[i].getPairs()) {
				if (mtp.getPointSet().size() >= minSize)
					mtps.add(mtp);
			}
		}

		int[] loadHistogram = new int[maxLoad+1];
		for(int i : hashValues)
			loadHistogram[mtmArray[i].size()]++;

	}

	public void computeMaximalTransformablePatternsInParallel(int minSize, int numThreads) throws NoTransformationClassesDefinedException {
		if (transformationClasses == null)
			throw new NoTransformationClassesDefinedException("No transformation classes defined! Add some transformation classes using addTransformationClasses() method.");

		ListOfTransformationPointSetPairs[] mtpArray = new ListOfTransformationPointSetPairs[HASH_TABLE_SIZE];
		for (int i = 0; i < mtpArray.length; i++)
			mtpArray[i] = new ListOfTransformationPointSetPairs();

		for (TransformationClass tc : transformationClasses) {
			int numObjectBases = Utility.computeNumCombinations(size(), tc.getBasisSize());
			System.out.println("basisSize = " + tc.getBasisSize());
			System.out.println("numObjectBases = " + numObjectBases);
			int[][] perms = tc.getPerms();
			int numComputations = numObjectBases * numObjectBases *perms.length;
			System.out.println("numComputations = "+numComputations);

			//			Find number of processors
			int numProcessors = Runtime.getRuntime().availableProcessors();

			//			If number of processors is less than the square root of numComputations,
			//			then number of threads is set to number of processors; otherwise it is 
			//			set to the square root of numComputations
			ArrayList<Thread> threadArray = new ArrayList<Thread>();

			if (numThreads == 0)
				numThreads = Math.min(numProcessors-1, (int)Math.floor(Math.sqrt(numComputations)));

			int computationsPerThread = (int)Math.ceil(numComputations/(1.0*numThreads));
			for(int threadNum = 0; threadNum < numThreads; threadNum++) {
				ComputeMaximalTransformablePatternsRunnable runnable = new ComputeMaximalTransformablePatternsRunnable(
						this, 
						tc,
						mtpArray,
						minSize,
						threadNum * computationsPerThread,
						Math.min((threadNum + 1)*computationsPerThread,numComputations),
						numObjectBases
						);
				Thread thread = new Thread(runnable);
				thread.start();
				threadArray.add(thread);
			}
			for(Thread thread : threadArray) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		TreeSet<Integer> hashValues = new TreeSet<Integer>();
		for(int i = 0; i < mtpArray.length; i++) {
			if (!(mtpArray[i].isEmpty()))
				hashValues.add(i);
		}

		mtps = new TreeSet<TransformationPointSetPair>();
		int maxLoad = 0;
		for(int i : hashValues) {
			if (mtpArray[i].size() > maxLoad)
				maxLoad = mtpArray[i].size();
			for (TransformationPointSetPair mtp : mtpArray[i].getPairs()) {
				mtps.add(mtp);
			}
		}

		int[] loadHistogram = new int[maxLoad+1];
		for(int i : hashValues)
			loadHistogram[mtpArray[i].size()]++;

		//		for(int i = 0; i < loadHistogram.length; i++)
		//			if (loadHistogram[i] > 0) {
		//				System.out.println(String.format("%5d:%8d", i, loadHistogram[i]));
		//			}


	}

	public void computeMaximalTransformablePatternsForkJoin(int minSize) throws NoTransformationClassesDefinedException {
		if (transformationClasses == null)
			throw new NoTransformationClassesDefinedException("No transformation classes defined! Add some transformation classes using addTransformationClasses() method.");

		ListOfTransformationPointSetPairs[] mtpArray = new ListOfTransformationPointSetPairs[HASH_TABLE_SIZE];
		for (int i = 0; i < mtpArray.length; i++)
			mtpArray[i] = new ListOfTransformationPointSetPairs();

		for (TransformationClass tc : transformationClasses) {
			int numObjectBases = Utility.computeNumCombinations(size(), tc.getBasisSize());
			System.out.println("basisSize = " + tc.getBasisSize());
			System.out.println("numObjectBases = " + numObjectBases);
			int[][] perms = tc.getPerms();
			int numComputations = numObjectBases * numObjectBases *perms.length;
			System.out.println("numComptutations = "+numComputations);
			ComputeMaximalTransformablePatterns action = new ComputeMaximalTransformablePatterns(this, tc, mtpArray, minSize, 0, numComputations, numObjectBases,0);
			//			System.out.println(ForkJoinPool.commonPool().toString());
			//			ForkJoinPool.commonPool().invoke(action);
			ForkJoinPool pool = new ForkJoinPool(
					Runtime.getRuntime().availableProcessors(),
					ForkJoinPool.defaultForkJoinWorkerThreadFactory,
					null,
					true
					);
			ComputeMaximalTransformablePatterns.FORK_JOIN_POOL = pool;
			pool.invoke(action);
		}

		TreeSet<Integer> hashValues = new TreeSet<Integer>();
		for(int i = 0; i < mtpArray.length; i++) {
			if (!(mtpArray[i].isEmpty()))
				hashValues.add(i);
		}

		mtps = new TreeSet<TransformationPointSetPair>();
		int maxLoad = 0;
		for(int i : hashValues) {
			if (mtpArray[i].size() > maxLoad)
				maxLoad = mtpArray[i].size();
			for (TransformationPointSetPair mtp : mtpArray[i].getPairs()) {
				mtps.add(mtp);
			}
		}

		int[] loadHistogram = new int[maxLoad+1];
		for(int i : hashValues)
			loadHistogram[mtpArray[i].size()]++;

		//		for(int i = 0; i < loadHistogram.length; i++)
		//			if (loadHistogram[i] > 0) {
		//				System.out.println(String.format("%5d:%8d", i, loadHistogram[i]));
		//			}

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
				for(int imgIndex = 0; imgIndex < objectBases.size(); imgIndex++) {
					PointSequence imageBasis = objectBases.get(imgIndex);
					for(int[] perm : perms) {
						PointSequence imgBasisPerm = new PointSequence();
						for(int i = 0; i< basisSize; i++)
							imgBasisPerm.add(imageBasis.get(perm[i]));
						ArrayList<Transformation> transformations = Transformation.getTransformations(tc, objectBasis, imgBasisPerm);
						for(Transformation transformation : transformations) {
							transformationObjectBasisPairs.add(new TransformationPointSequencePair(transformation,objectBasis));
							//							transformationObjectBasisPairs.add(new TransformationPointSequencePair(transformation.getInverse(),imageBasis));
							tc.addTransformationInstance(transformation);
							//							tc.addTransformationInstance(transformation.getInverse());
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


	public void computeMaximalTransformablePatternsWithHashTable(int minSize) throws NoTransformationClassesDefinedException {
		if (transformationClasses == null)
			throw new NoTransformationClassesDefinedException("No transformation classes defined! Add some transformation classes using addTransformationClasses() method.");
		ListOfTransformationPointSetPairs[] mtpArray = new ListOfTransformationPointSetPairs[HASH_TABLE_SIZE];

		TreeSet<Integer> hashValues = new TreeSet<Integer>();
		for(TransformationClass tc : transformationClasses) {
			int basisSize = tc.getBasisSize();
			//			System.out.println("basisSize = " + basisSize);
			int numObjectBases = Utility.computeNumCombinations(size(),basisSize);
			//			System.out.println("numObjectBases = " + numObjectBases);
			int[][] perms = Utility.computePermutationIndexSequences(basisSize);
			//			for(int[] perm : perms) {
			//				for(int index : perm)
			//					System.out.print(index + " ");
			//				System.out.println();
			//			}
			for(int objIndex = 0; objIndex < numObjectBases; objIndex++) {
				PointSequence objectBasis = computeBasis(basisSize, objIndex);
				for(int imgIndex = 0; imgIndex < numObjectBases; imgIndex++) {
					PointSequence imageBasis = computeBasis(basisSize, imgIndex);
					for(int[] perm : perms) {
						PointSequence imgBasisPerm = new PointSequence();
						for(int i = 0; i < basisSize; i++)
							imgBasisPerm.add(imageBasis.get(perm[i]));
						ArrayList<Transformation> transformations = Transformation.getTransformations(tc, objectBasis, imgBasisPerm);
						for(Transformation transformation : transformations) {
							int i = transformation.hash(HASH_TABLE_SIZE);
							hashValues.add(i);
							if (mtpArray[i] == null)
								mtpArray[i] = new ListOfTransformationPointSetPairs();
							mtpArray[i].add(transformation,objectBasis);
							//							System.out.println(String.format("%5d: %8d %5d", hashValues.size(),i,mtpArray[i].size()));

							//							i = transformation.getInverse().hash(HASH_TABLE_SIZE);
							//							hashValues.add(i);
							//							if (mtpArray[i] == null)
							//								mtpArray[i] = new ListOfTransformationPointSetPairs();
							//							mtpArray[i].add(transformation.getInverse(),imageBasis);
							//							System.out.println(String.format("%5d: %8d %5d", hashValues.size(),i,mtpArray[i].size()));
							tc.addTransformationInstance(transformation);
							//							tc.addTransformationInstance(transformation.getInverse());
						}
					}
				}
			}
		}

		//		System.out.println("mtpArray.length = " + mtpArray.length);
		//		System.out.println("HASH_TABLE_SIZE = " + HASH_TABLE_SIZE);

		//	    int x = 0;
		//	    for(int h : hashValues) {
		//	    	if (mtpArray[h].size()==3)
		//	    		System.out.print(String.format("%5d : %8d %3d\n", ++x, h, mtpArray[h].size()));
		//	    }

		//		int[] hashCodes = new int[] {3919845, 4372172, 9593469};
		//		for(int i : hashCodes) {
		//			System.out.println(""+i+": "+mtpArray[i]);
		//		}

		mtps = new TreeSet<TransformationPointSetPair>();
		int maxLoad = 0;
		for(int i : hashValues) {
			if (mtpArray[i].size() > maxLoad)
				maxLoad = mtpArray[i].size();
			for (TransformationPointSetPair mtp : mtpArray[i].getPairs()) {
				mtps.add(mtp);
			}
		}

		int[] loadHistogram = new int[maxLoad+1];
		for(int i : hashValues)
			loadHistogram[mtpArray[i].size()]++;

		//		System.out.println(" Load:    Freq");
		//		for(int i = 0; i < loadHistogram.length; i++)
		//			if (loadHistogram[i] > 0) {
		//				System.out.println(String.format("%5d:%8d", i, loadHistogram[i]));
		//			}

		//		System.out.println(mtps.size() + " MTPs computed");

		//		Output histogram of MTP sizes
		int[] mtpSizeArray = new int[this.size()+1];
		TreeSet<Integer> mtpSizeSet = new TreeSet<Integer>();
		for(TransformationPointSetPair mtp : mtps) {
			if (mtpSizeArray[mtp.getPointSet().size()] == 0)
				mtpSizeSet.add(mtp.getPointSet().size());
			mtpSizeArray[mtp.getPointSet().size()]++;			
		}
		//		System.out.println("\n Size:    Freq");
		//		for(int size : mtpSizeSet)
		//			System.out.println(String.format("%5d:%8d", size, mtpSizeArray[size]));

	}

	public boolean contains(Point point) {
		return getPoints().contains(point);
	}

	public boolean contains(PointSet pointSet) {
		for(Point p : pointSet.getPoints())
			if (!getPoints().contains(p)) return false;
		return true;
	}

	public  PointSet intersection(PointSet pointSet) {
		if (pointsArrayNeedsResetting) resetPointsArray();
		if (pointSet.pointsArrayNeedsResetting) pointSet.resetPointsArray();
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

	//	public boolean checkMaximalTransformablePatterns() throws Exception {
	//		if (pointsArrayNeedsResetting) resetPointsArray();
	//		if (mtps == null)
	//			throw new Exception("checkMaximalTransformablePatterns() called before MTPs have been computed!");
	//		for(TransformationPointSetPair mtp : mtps) {
	//			PointSet mtp2 = mtp.getTransformation().getInverse().phi(this).intersection(this);
	//			if (!mtp2.equals(mtp.getPointSet())) {
	//				System.out.println("Incorrectly computed MTP.\n Computed MTP: "+mtp+"\nCorrect MTP: "+mtp2);
	//				return false;
	//			} 
	//			//			else
	//			//				System.out.println(mtp.getPointSet()+"\n"+mtp2+"\n");
	//		}
	//		return true;
	//	}

	public TreeSet<TransformationPointSetPair> getMTPs() {
		return mtps;
	}

	@SuppressWarnings("unchecked")
	public void computeSizeMTPSetArray(int minSize, int sizeMTPSetArraySize) {
		sizeMTPSetArray = (ArrayList<TransformationPointSetPair>[])new ArrayList[sizeMTPSetArraySize];
		for(TransformationPointSetPair mtp : getMTPs()) {
			int n = mtp.getPointSet().size();
			if (n == 7 && sizeMTPSetArray.length == 6)
				System.out.println("n=7 and SizeMTPSetArray.length = 6");
			if (n >= minSize) {
				if (sizeMTPSetArray[n] == null) {
					mtpSizes.add(n);
					sizeMTPSetArray[n] = new ArrayList<TransformationPointSetPair>();
				}
				sizeMTPSetArray[n].add(mtp);
			}
		}
		TreeSet<Integer> mtpSizesTreeSet = new TreeSet<Integer>();
		for(int size :mtpSizes)
			mtpSizesTreeSet.add(size);
		if (mtpSizesTreeSet.size() != mtpSizes.size())
			System.out.println("mtpSizes contains non-distinct members: "+mtpSizes);
		Collections.sort(mtpSizes);
		//		System.out.println("mtp_sizes: "+mtpSizes);

		//		Sort each list of equally-sized MTPs in sizeMTPSetArray giving priority to the pattern,
		//		so that MTPs with the same pattern are adjacent in each resulting list
		for(int size : mtpSizes) {
			if (sizeMTPSetArray[size] == null) {
				System.out.println("size = "+size);
				System.out.println("sizeMTPSetArray["+size+"] = null");
				System.out.println("mtpSizes = "+mtpSizes);
			}
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
		//		System.out.println("mtpSizes:\n");
		//		for(int size : mtpSizes) {
		//			System.out.println(size + " : " + sizeMTPSetArray[size].size());
		//		}
	}

	/**
	 * Assumes that TransformationPointSetPairs are sorted by pattern within each subset containing
	 * patterns of a specific size.
	 */
	@SuppressWarnings("unchecked")
	public void mergeMTPs(int mtpOccurrenceSetsSize) {
		mtpOccurrenceSets = (ArrayList<OccurrenceSet>[])new ArrayList[mtpOccurrenceSetsSize];
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

		if (!IS_OSTG) {
			System.out.print("\nAfter running merge_mtps:\nmtp_sizes: ");
			for(int size : mtpSizes)
				System.out.print(size + " ");
			System.out.println();

			System.out.println("\nNum occurrence sets of each size");
			for(int size = 0; size < mtpOccurrenceSets.length; size++) {
				if (mtpOccurrenceSets[size] != null) {
					System.out.println(size + " : " + mtpOccurrenceSets[size].size());
				}
			}
		}

	}

	public void computeSuperMTPsForkJoin() {
		ComputeSuperMTPsAction action = new ComputeSuperMTPsAction(this);
		ForkJoinPool.commonPool().invoke(action);

		//		System.out.println("\nNumber of superMTPs for each MTP");
		//		for(int i = 0; i < mtpSizes.size(); i++) {
		//			int size = mtpSizes.get(i);
		//			System.out.print(size + " : ");
		//			ArrayList<OccurrenceSet> mtpsOfThisSize = mtpOccurrenceSets[size];
		//			for(OccurrenceSet mtp : mtpsOfThisSize) {
		//				if (mtp.getSuperMTPs() != null)
		//					System.out.print(mtp.getSuperMTPs().size() + " ");
		//			}
		//			System.out.println();
		//		}

	}

	public void computeSuperMTPs() {
		for(int i = 0; i < mtpSizes.size()-1; i++) {
			int size = mtpSizes.get(i);
			ArrayList<OccurrenceSet> mtpsOfThisSize = mtpOccurrenceSets[size];
			for(OccurrenceSet mtp : mtpsOfThisSize) {
				for (int j = i+1;j < mtpSizes.size();j++) {
					ArrayList<OccurrenceSet> largerMTPs = mtpOccurrenceSets[mtpSizes.get(j)];

					///////////////
					//DEBUGGING
					//	                System.out.println("\n\nDebugging from Encoding::compute_super_mtps");
					//	                System.out.println(String.format("mtps of size %d\n", mtpSizes.get(j)));
					//	                for(OccurrenceSet largerMTP : largerMTPs) {
					//	                    System.out.println(largerMTP);
					//	                }
					//END DEBUGGING



					for(OccurrenceSet largerMTP : largerMTPs) {
						PointSet largerPattern = largerMTP.getPattern();
						if (largerPattern.contains(mtp.getPattern()))
							mtp.addSuperMTP(largerMTP);
					}
				}
			}
		}

		//		System.out.println("\nNumber of superMTPs for each MTP");
		//		for(int i = 0; i < mtpSizes.size(); i++) {
		//			int size = mtpSizes.get(i);
		//			System.out.print(size + " : ");
		//			ArrayList<OccurrenceSet> mtpsOfThisSize = mtpOccurrenceSets[size];
		//			for(OccurrenceSet mtp : mtpsOfThisSize) {
		//				if (mtp.getSuperMTPs() != null)
		//					System.out.print(mtp.getSuperMTPs().size() + " ");
		//			}
		//			System.out.println();
		//		}
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

		//	    System.out.println("computeHeterogeneousOccurrenceSets finished:\n");
		//	    for (int size : mtpSizes) {
		//	        System.out.println("\nsize = " + size);
		//	        for (OccurrenceSet mtp : mtpOccurrenceSets[size]) {
		//	            System.out.println(mtp);
		//	        }
		//	    }


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

		//		System.out.println("Number of sorted occurrence sets: " + sortedOccurrenceSets.size());
		//		for(int i = 0; i < 20; i++)
		//			try {
		//				System.out.println(i+". "+sortedOccurrenceSets.get(i) + " (" + sortedOccurrenceSets.get(i).getCompressionFactor() + ")");
		//			} catch (SuperMTPsNotNullException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
	}

	public void removeRedundantTransformations() {
		//		For each MTP, remove more complex transformations that map the pattern
		//		onto the same image pattern as less complex transformations
		for(int size : mtpSizes) {
			for (OccurrenceSet mtp : mtpOccurrenceSets[size]) {
				mtp.removeRedundantTransformations(isMTM());
			}
		}
	}

	public boolean isEmpty() {
		return getPoints().isEmpty();
	}

	public void removeDuplicateOccurrenceSets() {

		int num_os = 0;
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				num_os++;
			}
		if (!IS_OSTG)
			System.out.println("Number of occurrence sets at beginning of removeDuplicateOccurrenceSets is " + num_os);

		for(int size : mtpSizes) {
			TreeSet<OccurrenceSet> sortedDeDupedList = new TreeSet<OccurrenceSet>();
			for(OccurrenceSet os : mtpOccurrenceSets[size]) {
				//				System.out.println(os);
				//				System.out.println(sortedDeDupedList);
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

		num_os = 0;
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				num_os++;
			}
		if (!IS_OSTG)
			System.out.println("Number of occurrence sets at end of removeDuplicateOccurrenceSets is " + num_os);


	}

	public void removeOccurrenceSetsWithNoTransformations() {

		int num_os = 0;
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				num_os++;
			}
		if (!IS_OSTG)
			System.out.println("Number of occurrence sets at beginning of removeOccurrenceSetsWithNoTransformations is " + num_os);


		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				//				System.out.println(mtpOccurrenceSets[size].get(i));
				if (mtpOccurrenceSets[size].get(i).getTransformations().isEmpty()) {
					mtpOccurrenceSets[size].remove(i);
					i--;
				}
			}

		num_os = 0;
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				num_os++;
			}
		if (!IS_OSTG)
			System.out.println("Number of occurrence sets at end of removeOccurrenceSetsWithNoTransformations is " + num_os);

	}

	public double getCompactness(PointSet dataset) {
		TreeSet<Point> thisPoints = getPoints();
		Double minX, minY, maxX, maxY;
		minX = minY = maxX = maxY = null;
		for (Point p : thisPoints) {
			if (minX == null || minX > p.get(0))
				minX = p.get(0);
			if (minY == null || minY > p.get(1))
				minY = p.get(1);
			if (maxX == null || maxX < p.get(0))
				maxX = p.get(0);
			if (maxY == null || maxY < p.get(1))
				maxY = p.get(1);
		}
		PointSet datasetBBSubset = new PointSet();
		for(Point p : dataset.getPoints()) {
			if (minX <= p.get(0) && p.get(0) <= maxX && minY <= p.get(1) && p.get(1) <= maxY)
				datasetBBSubset.add(p);
		}
		return (1.0 * this.size())/datasetBBSubset.size();
	}

	public void removeNonCompactOccurrenceSets(double minCompactness) throws SuperMTPsNotNullException {

		int num_os = 0;
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				num_os++;
			}
		System.out.println("Number of occurrence sets at beginning of removeNonCompactOccurrenceSets is " + num_os);


		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				if (mtpOccurrenceSets[size].get(i).getMaxCompactness(isMTM(), this) < minCompactness) {
					mtpOccurrenceSets[size].remove(i);
					i--;
				}
			}

		num_os = 0;
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				num_os++;
			}
		System.out.println("Number of occurrence sets at end of removeNonCompactOccurrenceSets is " + num_os);

	}

	public void removeNonCompactOccurrences(double minOccurrenceCompactness) throws SuperMTPsNotNullException {

		int num_os = 0;
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				num_os++;
			}
		System.out.println("Number of occurrence sets at beginning of removeNonCompactOccurrences is " + num_os);


		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				mtpOccurrenceSets[size].get(i).removeNonCompactOccurrences(minOccurrenceCompactness,this);
			}

		num_os = 0;
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				num_os++;
			}
		System.out.println("Number of occurrence sets at end of removeNonCompactOccurrences is " + num_os);

	}


	public void removeContainedOccurrences() {
		for(int size : mtpSizes)
			for(int i = 0; i < mtpOccurrenceSets[size].size(); i++) {
				OccurrenceSet thisOS = mtpOccurrenceSets[size].get(i);
				PointSet thisOSPattern = thisOS.getPattern();
				TreeSet<Transformation> transformations = thisOS.getTransformations();
				for(Transformation tran : transformations) {
					PointSet superPattern = tran.phi(thisOSPattern);
					ArrayList<Integer> subPatternSizes = new ArrayList<Integer>();
					for(Integer s : mtpSizes)
						if (s < size)
							subPatternSizes.add(s);
					for(int subPatternSize : subPatternSizes) {
						for(int j = 0; j < mtpOccurrenceSets[subPatternSize].size(); j++) {
							OccurrenceSet subOS = mtpOccurrenceSets[subPatternSize].get(j);
							PointSet subOSPattern = subOS.getPattern();
							TreeSet<Transformation> subTrans = subOS.getTransformations();
							TreeSet<Transformation> newSubTrans = new TreeSet<Transformation>();
							for(Transformation subTran : subTrans) {
								if (!superPattern.contains(subTran.phi(subOSPattern)))
									newSubTrans.add(subTran);
							}
							subOS.setTransformations(newSubTrans);
						}
					}
				}
			}		
	}

	public PointSet setMinus(PointSet pointSet) {
		if (pointSet.pointsArrayNeedsResetting) pointSet.resetPointsArray();
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

		if (sortedOccurrenceSets.isEmpty()) {
			if (!IS_OSTG)
				System.out.println("sortedOccurrenceSets is empty in computeEncoding");
			setEncoding(encoding);
			return;
		}
		// Assumes first occurrence set in list is the "best"
		encoding.add(sortedOccurrenceSets.get(0));
		PointSet coveredSet = new PointSet();
		coveredSet.addAll(sortedOccurrenceSets.get(0).getCoveredSet());
		for(int i = 1; i < sortedOccurrenceSets.size(); i++) {
			OccurrenceSet os = sortedOccurrenceSets.get(i);
			PointSet diffSet = os.getCoveredSet().setMinus(coveredSet);
			int osEncodingLength = os.getPatternLength() + os.getTransformationSetLength();
			if (isMTM() || (osEncodingLength < diffSet.getDimensionality()*diffSet.size())) {
				encoding.add(os);
				coveredSet.addAll(os.getCoveredSet());
			}
		}
		if (!isMTM()) {
			PointSet residualSet = this.setMinus(coveredSet);
			if (!residualSet.isEmpty()) {
				OccurrenceSet residualOccurrenceSet = new OccurrenceSet(residualSet, this);
				encoding.add(residualOccurrenceSet);
			}
		}
		setEncoding(encoding);
	}

	public static void encodePointSet(PointSet ps, String outputFileName, TransformationClass[] transformationClasses, boolean draw, boolean diatonicPitch, double minCompactness, double minOccurrenceCompactness, String groundTruthFileName, boolean midTimePoint, boolean drawBoundingBoxes, boolean useChroma, boolean useMorph, int numThreads, boolean multiThreaded, boolean forkJoin) throws Exception {
		encodePointSet(
				ps, 
				outputFileName, 
				transformationClasses, 
				false, 
				3, 
				HASH_TABLE_SIZE, 
				draw, 
				diatonicPitch, 
				minCompactness, 
				minOccurrenceCompactness, 
				groundTruthFileName, 
				midTimePoint, 
				drawBoundingBoxes, 
				useChroma, 
				useMorph,
				numThreads,
				multiThreaded,
				forkJoin);
	}

	public static PointSet maximalTransformedMatches(
			PointSet pattern, 
			PointSet dataset, 
			String outputFilePath, 
			TransformationClass[] transformationClasses, 
			int minSize, 
			int hashTableSize, 
			boolean draw, 
			boolean pitchSpell,
			double minCompactness,
			double minOccurrenceCompactness,
			String groundTruthFileName,
			boolean midTimePoint,
			boolean drawBoundingBoxes,
			boolean useChroma,
			boolean useMorph,
			int numThreads,
			boolean multiThreaded,
			boolean forkJoin) throws FileNotFoundException, TimeOutException, NoTransformationClassesDefinedException, SuperMTPsNotNullException {
		return encodePointSet(
				dataset, 
				outputFilePath, 
				transformationClasses,
				false,
				minSize,
				hashTableSize,
				draw,
				pitchSpell,
				pattern,
				minCompactness,
				minOccurrenceCompactness,
				groundTruthFileName,
				midTimePoint,
				drawBoundingBoxes,
				useChroma,
				useMorph,
				numThreads,
				multiThreaded,
				forkJoin
				);
	}

	public static void encodePointSet (
			PointSet ps, 
			String outputFilePath, 
			TransformationClass[] transformationClasses,
			boolean useScalexia,
			int minSize,
			int hashTableSize,
			boolean draw,
			boolean diatonicPitch,
			double minCompactness,
			double minOccurrenceCompactness,
			String groundTruthFileName,
			boolean midTimePoint,
			boolean drawBoundingBoxes,
			boolean useChroma, 
			boolean useMorph,
			int numThreads,
			boolean multiThreaded,
			boolean forkJoin) throws FileNotFoundException, TimeOutException, NoTransformationClassesDefinedException, SuperMTPsNotNullException {
		encodePointSet (
				ps, 
				outputFilePath, 
				transformationClasses,
				useScalexia,
				minSize,
				hashTableSize,
				draw,
				diatonicPitch,
				null,
				minCompactness,
				minOccurrenceCompactness,
				groundTruthFileName,
				midTimePoint,
				drawBoundingBoxes,
				useChroma,
				useMorph,
				numThreads,
				multiThreaded,
				forkJoin
				);
	}


	public static PointSet encodePointSet (
			PointSet ps, 
			String outputFilePath, 
			TransformationClass[] transformationClasses,
			boolean useScalexia,
			int minSize,
			int hashTableSize,
			boolean draw,
			boolean diatonicPitch,
			PointSet ps2,
			double minCompactness,
			double minOccurrenceCompactness,
			String groundTruthFileName,
			boolean midTimePoint,
			boolean drawBoundingBoxes,
			boolean useChroma,
			boolean useMorph,
			int numThreads,
			boolean multiThreaded,
			boolean forkJoin) throws TimeOutException, FileNotFoundException, NoTransformationClassesDefinedException, SuperMTPsNotNullException {

		LogInfo.ACCUMULATED_TIME = 0l;
		ArrayList<LogInfo> log = new ArrayList<LogInfo>();

		if (ps2 != null) {
			ps.setMTM(true);
			//			if (minSize < 0)
			//				minSize = ps2.size()-minSize;
		}

		ps.addTransformationClasses(transformationClasses);		

		log.add(new LogInfo("computeMaximalTransformablePatterns starts", !IS_OSTG));
		if (useScalexia)
			ps.computeMTPsWithScalexia(minSize);
		else if (ps2 == null) {
			if (multiThreaded || numThreads != 0)
				ps.computeMaximalTransformablePatternsInParallel(minSize, numThreads);
			else if (forkJoin)
				ps.computeMaximalTransformablePatternsForkJoin(minSize);
			else
				ps.computeMaximalTransformablePatterns(minSize);
		} else //ps2 is non-null
			ps.computeMaximalTransformedMatchesForkJoin(ps2,minSize);
		log.add(new LogInfo("computeMaximalTransformablePatterns ends", !IS_OSTG));

		//		int numMTPsBeforeRemoval = ps.getMTPs().size();
		//		System.out.println("Number of MTPs before removal: "+numMTPsBeforeRemoval);

		int sizeMTPSetArraySize = 1+ps.size();

		if (ps2 != null)
			sizeMTPSetArraySize = 1+Math.max(ps.size(), ps2.size());
		ps.computeSizeMTPSetArray(minSize,sizeMTPSetArraySize);
		log.add(new LogInfo("computeSizeMTPSetArray ends", !IS_OSTG));

		ps.mergeMTPs(sizeMTPSetArraySize);
		log.add(new LogInfo("mergeMTPs ends", !IS_OSTG));

		//		ps.computeSuperMTPs();
		ps.computeSuperMTPsForkJoin();
		log.add(new LogInfo("computeSuperMTPs ends", !IS_OSTG));

		TIME_AT_START_OF_COMPUTING_HETERO_OS = Calendar.getInstance().getTimeInMillis();
		COMPUTE_HETERO_OS_COMPLETED = false;
		ps.computeHeterogeneousOccurrenceSets();
		log.add(new LogInfo("computeHeterogeneousOccurrenceSets ends", !IS_OSTG));

		ps.removeDuplicateOccurrenceSets();
		log.add(new LogInfo("removeDuplicateOccurrenceSets ends", !IS_OSTG));
		if (!ps.isMTM())
			ps.removeRedundantTransformations();
		log.add(new LogInfo("removeRedundantTransformations ends", !IS_OSTG));

		if (minCompactness > 0)
			ps.removeNonCompactOccurrenceSets(minCompactness);
		if (minOccurrenceCompactness > 0)
			ps.removeNonCompactOccurrences(minOccurrenceCompactness);

		ps.removeOccurrenceSetsWithNoTransformations();
		log.add(new LogInfo("removeOccurrenceSetsWithEmptyTransformationSets ends", !IS_OSTG));

		if (ps.isMTM()) {
			ps.removeContainedOccurrences();			
		}

		//		ps.removeOccurrenceSetsWithNoTransformations();
		//		log.add(new LogInfo("removeOccurrenceSetsWithEmptyTransformationSets ends", true));
		//
		ps.computeSortedOccurrenceSets(ps.isMTM()?OccurrenceSet.DECREASING_PATTERN_SIZE:OccurrenceSet.DECREASING_CF_THEN_COVERAGE_COMPARATOR);
		log.add(new LogInfo("computeSortedOccurrenceSets ends", !IS_OSTG));

		ps.computeEncoding();
		log.add(new LogInfo("computeEncoding ends", !IS_OSTG));

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

		log.add(new LogInfo("Program ends\n\n", !IS_OSTG));
		ps.getEncoding().setRunningTimeInMillis(log.get(log.size()-1).getAccumulatedTime());
		if (outputFilePath != null && !IS_OSTG) {
			PrintWriter output = new PrintWriter(outputFilePath);
			System.out.println("Output file: "+ outputFilePath);
			System.out.println("Encoding:\n" + ps.getEncoding());
			Utility.println(output, ps.getEncoding());

			Utility.println(output, "\n\nLog:");
			for(int i = 0; i < log.size(); i++) {
				Utility.println(output, log.get(i));
			}

			Utility.println(output, "Number of points: " + ps.size());
			//		Utility.println(output, "Number of MTPs before removal: " + numMTPsBeforeRemoval);
			Utility.println(output, "Number of OSs after removal: " + ps.sortedOccurrenceSets.size());

			Utility.println(output, MaxTranPats.getParameterSettings());

			output.close();			
		}

		////		Output MIREX format file for comparison with ground truth.
		//		int endIndex = outputFilePath.lastIndexOf('.');
		//		outputFilePath = outputFilePath.substring(0, endIndex) + ".mirex";
		//		output = new PrintWriter(outputFilePath);
		//		Utility.println(output, ps.getEncoding().toMIREXString());
		//		output.close();

		//		Compare computed encoding with ground truth file
		if (groundTruthFileName != null) {
			ps.compareWithGroundTruthFile(groundTruthFileName, diatonicPitch, outputFilePath, !ps.isMTM(), ps.isMTM(), midTimePoint, useMorph, useChroma);
		}

		if (draw) {
			int posOfDot = outputFilePath.lastIndexOf(".");
			String imageFilePath = outputFilePath.substring(0,posOfDot) + ".png";
			ps.getEncoding().drawOccurrenceSets(imageFilePath,diatonicPitch,!ps.isMTM(),midTimePoint, drawBoundingBoxes, useChroma, useMorph);
		}

		return ps;
	}

	public static StringBuilder readGroundTruthFileIntoStringBuilder(String groundTruthFilePath) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(groundTruthFilePath));
			String line = br.readLine();
			while (line != null) {
				if (!line.isEmpty() && !line.startsWith("%") && !line.startsWith("//") && !line.startsWith(";"))
					sb.append(line.trim());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;

	}

	/**
	 * Assumes ground-truth pattern file has following format:
	 * file := occurrence-set*
	 * occurrence-set := (pattern*)
	 * pattern := (point*)
	 * point := (onset pitch-name duration [voice])
	 * 
	 * Empty lines in the file are ignored.
	 * Lines beginning with % or // are ignored (comments).
	 * 
	 * If diatonicPitch is set to true, then it produces PointSets in which the y value is chromatic pitch,
	 * otherwise the y value is set to morphetic pitch. Chromatic pitch and morphetic pitch are computed
	 * from the pitch name.
	 * 
	 */
	private static ArrayList<ArrayList<com.chromamorph.points022.PointSet>> readGroundTruthPatternsFromFile(String groundTruthFilePath, boolean diatonicPitch, boolean withMidTimePoints, int xScaleFactor, boolean useMorph, boolean useChroma) {
		StringBuilder sb = readGroundTruthFileIntoStringBuilder(groundTruthFilePath);

		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> groundTruthPatterns = new ArrayList<ArrayList<com.chromamorph.points022.PointSet>>();

		int i = 0;
		while (i < sb.length()) {
			while (i < sb.length() && sb.charAt(i) != '(') i++; //Puts i at beginning of encoding of next occurrence set or end of file
			if (i < sb.length()) {
				OccurrenceSetEndIndexPair osei = readOccurrenceSet(sb,i, diatonicPitch, withMidTimePoints, xScaleFactor, useMorph, useChroma);
				i = osei.endIndex; //Should be one character after the end of the occurrence set encoding
				groundTruthPatterns.add(osei.occurrenceSet);
			}
		}
		return groundTruthPatterns;
	}


	static class OccurrenceSetEndIndexPair {
		ArrayList<com.chromamorph.points022.PointSet> occurrenceSet = new ArrayList<com.chromamorph.points022.PointSet>();
		int endIndex;

	}

	/**
	 * Assumes that character at index i starts an encoding of an occurrence set in sb.
	 * Parses this encoding of the occurrence set and converts it into an ArrayList of OMNISIA-style PointSets.
	 * Returns a pair containing the list of point sets and the index of the first character *after* the encoding
	 * of the occurrence set.
	 * 
	 * Occurrence set has the following format:
	 * occurrence-set := (pattern*)
	 * pattern := (point*)
	 * point := (onset pitch-name duration [voice])
	 * 
	 * @param sb
	 * @param startIndex
	 * @return
	 */
	private static OccurrenceSetEndIndexPair readOccurrenceSet(StringBuilder sb, int startIndex, boolean diatonicPitch, boolean withMidTimePoints, int xScaleFactor, boolean useMorph, boolean useChroma) {
		OccurrenceSetEndIndexPair osei = new OccurrenceSetEndIndexPair();
		//Find start of first pattern within this occurrence set
		int i = startIndex + 1;
		while (sb.charAt(i) != ')') {
			while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++; //Puts i at beginning of encoding of next pattern occurrence or end of occurrence set
			if (sb.charAt(i) != ')') {
				OccurrenceEndIndexPair occEi = readOccurrence(sb,i, diatonicPitch, withMidTimePoints, xScaleFactor, useMorph, useChroma);
				i = occEi.endIndex; //Should be one character after the occurrence just read.
				osei.occurrenceSet.add(occEi.occurrence);
			}
		}
		osei.endIndex = i+1;
		return osei;
	}

	static class OccurrenceEndIndexPair {
		com.chromamorph.points022.PointSet occurrence = new com.chromamorph.points022.PointSet();
		int endIndex = 0;
	}

	private static OccurrenceEndIndexPair readOccurrence(StringBuilder sb, int startIndex, boolean diatonicPitch, boolean withMidTimePoints, int xScaleFactor, boolean useMorph, boolean useChroma) {
		OccurrenceEndIndexPair occEi = new OccurrenceEndIndexPair();
		int i = startIndex + 1;
		int[] colArray = null;
		int[] colArray2 = null;
		String label = null;
		Float strokeWidth = null;
		Float pointWidth = null;
		Float pointHeight = null;

		//		Read colArrays
		while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++;
		if (sb.substring(i).startsWith("(col")) {
			colArray = new int[4];
			int colEndIndex = sb.indexOf(")", i);
			String colStr = sb.substring(i+4, colEndIndex);
			String[] colStrArr = colStr.trim().split(" ");
			for(int k = 0; k < 4; k++)
				colArray[k] = Integer.parseInt(colStrArr[k]);
			i = colEndIndex+1;
		}
		while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++;
		if (sb.substring(i).startsWith("(col2")) {
			colArray2 = new int[4];
			int colEndIndex = sb.indexOf(")", i);
			String colStr = sb.substring(i+4, colEndIndex);
			String[] colStrArr = colStr.trim().split(" ");
			for(int k = 0; k < 4; k++)
				colArray2[k] = Integer.parseInt(colStrArr[k]);
			i = colEndIndex+1;
		}

		//*********************************

		//		Read pattern label
		while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++;
		if (sb.substring(i).startsWith("(label")) {
			int labelEndIndex = sb.indexOf(")", i);
			label = sb.substring(i + "(label".length(), labelEndIndex).trim();
			i = labelEndIndex + 1;
		}
		//		Read stroke-width
		while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++;
		if (sb.substring(i).startsWith("(stroke-width")) {
			int strokeWidthEndIndex = sb.indexOf(")", i);
			strokeWidth = Float.parseFloat(sb.substring(i + "(stroke-width".length(), strokeWidthEndIndex).trim());
			i = strokeWidthEndIndex + 1;
		}
		//		Read point-width
		while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++;
		if (sb.substring(i).startsWith("(point-width")) {
			int pointWidthEndIndex = sb.indexOf(")", i);
			pointWidth = Float.parseFloat(sb.substring(i + "(point-width".length(), pointWidthEndIndex).trim());
			i = pointWidthEndIndex + 1;
		}

		//		Read point-height
		while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++;
		if (sb.substring(i).startsWith("(point-height")) {
			int pointHeightEndIndex = sb.indexOf(")", i);
			pointHeight = Float.parseFloat(sb.substring(i + "(point-height".length(), pointHeightEndIndex).trim());
			i = pointHeightEndIndex + 1;
		}

		while (sb.charAt(i) != ')') {
			while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++; //Puts i at beginning of encoding of next point or end of point set
			if (sb.charAt(i) != ')') {
				PointEndIndexPair pEi = readPoint(sb,i, diatonicPitch, withMidTimePoints, xScaleFactor, useMorph, useChroma);
				i = pEi.endIndex;
				occEi.occurrence.add(pEi.point);
				occEi.occurrence.setColArray(colArray);
				occEi.occurrence.setColArray2(colArray2);
				occEi.occurrence.setLabel(label);
				occEi.occurrence.setStrokeWidth(strokeWidth);
				occEi.occurrence.setPointWidth(pointWidth);
				occEi.occurrence.setPointHeight(pointHeight);
			}
		}
		occEi.endIndex = i + 1;
		return occEi;
	}

	static class PointEndIndexPair {
		com.chromamorph.points022.Point point;
		int endIndex;
	}

	private static PointEndIndexPair readPoint(StringBuilder sb, int startIndex, boolean diatonicPitch, boolean withMidTimePoints, int xScaleFactor, boolean useMorph, boolean useChroma) {
		int i = startIndex;
		Float strokeWidth = null;
		Float pointWidth = null;
		Float pointHeight = null;
		while (sb.charAt(i) != '(') i++; // puts i at beginning of this point encoding
		i++; //puts i at first character following opening parenthesis of this point encoding
		while (Character.isWhitespace(sb.charAt(i))) i++; 
		// 		puts i at first non-whitespace char within point encoding after opening parenthesis
		if (sb.charAt(i) == '(') {
			//			Read stroke-width
			if (sb.substring(i).startsWith("(stroke-width")) {
				int strokeWidthEndIndex = sb.indexOf(")", i);
				strokeWidth = Float.parseFloat(sb.substring(i + "(stroke-width".length(), strokeWidthEndIndex).trim());
				i = strokeWidthEndIndex + 1;
			}
			//			Find next non-whitespace character following stroke-width spec
			while (Character.isWhitespace(sb.charAt(i))) i++; 
			if (sb.charAt(i) == '(') {
				//				Read point-width
				if (sb.substring(i).startsWith("(point-width")) {
					int pointWidthEndIndex = sb.indexOf(")", i);
					pointWidth = Float.parseFloat(sb.substring(i + "(point-width".length(), pointWidthEndIndex).trim());
					i = pointWidthEndIndex + 1;
				}

				while (Character.isWhitespace(sb.charAt(i))) i++; 
				if (sb.charAt(i) == '(') {
					//					Read point-height
					while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++;
					if (sb.substring(i).startsWith("(point-height")) {
						int pointHeightEndIndex = sb.indexOf(")", i);
						pointHeight = Float.parseFloat(sb.substring(i + "(point-height".length(), pointHeightEndIndex).trim());
						i = pointHeightEndIndex + 1;
					}					
				}
			}
		}
		//		i now points at the first character of the onset of the point
		PointEndIndexPair pEi = new PointEndIndexPair();
		int endIndex = sb.indexOf(")", i);
		String pointString = sb.substring(i, endIndex);
		String[] a = pointString.split("\s");
		//		pointString has format onset pitch-name duration [voice]
		long originalOnset = Long.parseLong(a[0]);
		long duration = Long.parseLong(a[2]);
		double xdbl = originalOnset;
		if (withMidTimePoints)
			xdbl = originalOnset + (duration/2.0);
		long x = (long)(xdbl * TIME_SCALE_FACTOR);
		x *= xScaleFactor;

		com.chromamorph.pitch.Pitch p = new com.chromamorph.pitch.Pitch();
		int y;
		if ("0123456789".contains(a[1].subSequence(0, 1)))
			y = Integer.parseInt(a[1]);
		else {
			p.setPitchName(a[1]);
			if (useMorph)
				y = p.getMorph();
			else if (useChroma)
				y = p.getChroma();
			else if (diatonicPitch)
				y = p.getMorpheticPitch();
			else
				y = p.getChromaticPitch();
		}
		pEi.point = new com.chromamorph.points022.Point(x,y);
		pEi.point.setPointHeight(pointHeight);
		pEi.point.setPointWidth(pointWidth);
		pEi.point.setStrokeWidth(strokeWidth);
		pEi.endIndex = endIndex + 1;
		return pEi;
	}

	private void compareWithGroundTruthFile(String groundTruthFilePath, boolean diatonicPitch, String outputFilePath, boolean includePattern, boolean isMTM, boolean withMidTimePoints, boolean useMorph, boolean useChroma) {
		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> groundTruthPatterns = readGroundTruthPatternsFromFile(groundTruthFilePath, diatonicPitch, withMidTimePoints, 1, useMorph, useChroma);
		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> computedPatterns = getEncoding().getOccurrenceSetsAsArrayListsOfPointSets(includePattern);
		if (isMTM) {
			//			Make computedPatterns a single occurrence set of patterns
			ArrayList<ArrayList<com.chromamorph.points022.PointSet>> newComputedPatterns = new ArrayList<ArrayList<com.chromamorph.points022.PointSet>>();
			TreeSet<com.chromamorph.points022.PointSet> newOS = new TreeSet<com.chromamorph.points022.PointSet>();
			for(ArrayList<com.chromamorph.points022.PointSet> os : computedPatterns) {
				newOS.addAll(os);
			}
			ArrayList<com.chromamorph.points022.PointSet> newOSArray = new ArrayList<com.chromamorph.points022.PointSet>();
			newOSArray.addAll(newOS);
			newComputedPatterns.add(newOSArray);
			computedPatterns = newComputedPatterns;

			//			Make groundTruthPatterns a single occurrence set of patterns
			//			ArrayList<ArrayList<com.chromamorph.points022.PointSet>> newGroundTruthPatterns = new ArrayList<ArrayList<com.chromamorph.points022.PointSet>>();
			//			TreeSet<com.chromamorph.points022.PointSet> newGTOS = new TreeSet<com.chromamorph.points022.PointSet>();
			//			for(ArrayList<com.chromamorph.points022.PointSet> os : groundTruthPatterns) {
			//				newGTOS.addAll(os);
			//			}
			//			ArrayList<com.chromamorph.points022.PointSet> newGTOSArray = new ArrayList<com.chromamorph.points022.PointSet>();
			//			newGTOSArray.addAll(newGTOS);
			//			newGroundTruthPatterns.add(newGTOSArray);
			//			groundTruthPatterns = newGroundTruthPatterns;

		}

		System.out.println("Ground truth patterns");
		for(int i = 0; i < groundTruthPatterns.size(); i++) {
			ArrayList<com.chromamorph.points022.PointSet> os = groundTruthPatterns.get(i);
			System.out.println("Occurrence set: "+(i+1));
			for(int j = 0; j < os.size(); j++) {
				System.out.println("   Occurrence "+(j+1)+": "+os.get(j));
			}
		}		

		System.out.println("Computed patterns");
		for(int i = 0; i < computedPatterns.size(); i++) {
			ArrayList<com.chromamorph.points022.PointSet> os = computedPatterns.get(i);
			System.out.println("Occurrence set: "+(i+1));
			for(int j = 0; j < os.size(); j++) {
				System.out.println("   Occurrence "+(j+1)+": "+os.get(j));
			}
		}		

		double f1, p, r;

		if (!isMTM) { //Use 3-layer F1, P and R
			f1 = com.chromamorph.points022.EvaluateMIREX2013.getThreeLayerF1(groundTruthPatterns, computedPatterns);
			p = com.chromamorph.points022.EvaluateMIREX2013.getP3(groundTruthPatterns, computedPatterns);
			r = com.chromamorph.points022.EvaluateMIREX2013.getR3(groundTruthPatterns, computedPatterns);
		} else { //Use 2-layer F1, P and R
			f1 = com.chromamorph.points022.EvaluateMIREX2013.getF1(groundTruthPatterns.get(0),computedPatterns.get(0));
			p = com.chromamorph.points022.EvaluateMIREX2013.getPb(groundTruthPatterns.get(0),computedPatterns.get(0));
			r = com.chromamorph.points022.EvaluateMIREX2013.getRb(groundTruthPatterns.get(0),computedPatterns.get(0));
		}

		System.out.println("Ground truth file: " + groundTruthFilePath);
		System.out.println(diatonicPitch?"Morphetic pitch":"Chromatic pitch");
		System.out.println((isMTM?"2":"3")+"-layer F1: " + f1);
		System.out.println((isMTM?"2":"3")+"-layer P: " + p);
		System.out.println((isMTM?"2":"3")+"-layer R: " + r);

		int endIndex = outputFilePath.lastIndexOf('.');
		String evaluationFilePathString = outputFilePath.substring(0,endIndex)+".eval";
		try {
			PrintWriter pw = new PrintWriter(evaluationFilePathString);
			Utility.println(pw, "Ground truth file: " + groundTruthFilePath);
			Utility.println(pw, diatonicPitch?"Morphetic pitch":"Chromatic pitch");
			Utility.println(pw, (isMTM?"2":"3")+"-layer F1: " + f1);
			Utility.println(pw, (isMTM?"2":"3")+"-layer P: " + p);
			Utility.println(pw, (isMTM?"2":"3")+"-layer R: " + r);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
			int count,
			boolean draw,
			double minCompactness,
			double minOccurrenceCompactness,
			String groundTruthFileName,
			boolean drawBoundingBoxes,
			boolean useChroma,
			boolean useMorph,
			int numThreads,
			boolean multiThreaded,
			boolean forkJoin) {
		String outputFileName = Utility.getOutputPathForPairFileEncoding(outputDirectory, filePath1, filePath2, transformationClasses, count);
		try {
			PointSet ps1 = new PointSet(
					new File(filePath1), 
					pitchSpell, 
					midTimePoint,
					dimensionMask,
					useChroma,
					useMorph);
			PointSet ps2 = new PointSet(
					new File(filePath2), 
					pitchSpell, 
					midTimePoint,
					dimensionMask,
					useChroma,
					useMorph);

			//			We're going to form the union of ps1 with ps2 translated by twice the maximum x-value of ps1
			Transformation tran = new Transformation(new F_2T(), Utility.makeSigma(ps1.getMax(0) * 2, 0));
			PointSet translatedPS2 = tran.phi(ps2);
			PointSet ps = new PointSet();
			ps.addAll(ps1);
			ps.addAll(translatedPS2);
			encodePointSet(
					ps, 
					outputFileName, 
					transformationClasses, 
					useScalexia, 
					minSize, 
					HASH_TABLE_SIZE, 
					draw, 
					pitchSpell, 
					minCompactness, 
					minOccurrenceCompactness, 
					groundTruthFileName, 
					midTimePoint, 
					drawBoundingBoxes, 
					useChroma, 
					useMorph,
					numThreads,
					multiThreaded,
					forkJoin);
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
			String outputDir,
			boolean draw,
			double minCompactness,
			double minOccurrenceCompactness, 
			String groundTruthFileName,
			boolean drawBoundingBoxes,
			boolean useChroma,
			boolean useMorph,
			int numThreads,
			boolean multiThreaded,
			boolean forkJoin) {
		try {
			String outputFileName = Utility.getOutputFilePath(outputDir, fileName, transformationClasses);
			PointSet ps = new PointSet(
					new File(fileName), 
					pitchSpell, 
					midTimePoint,
					dimensionMask,
					useChroma,
					useMorph);
			encodePointSet(ps, outputFileName, transformationClasses, draw, pitchSpell, minCompactness, minOccurrenceCompactness, groundTruthFileName, midTimePoint, drawBoundingBoxes, useChroma, useMorph, numThreads, multiThreaded, forkJoin);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DimensionalityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static PointSet maximalTransformedMatchesFromFiles(
			String patternFileName,
			String datasetFileName,
			TransformationClass[] transformationClasses,
			boolean pitchSpell,
			boolean midTimePoint,
			String dimensionMask,
			String outputDir,
			int minSize,
			boolean draw,
			double minCompactness,
			double minOccurrenceCompactness,
			String groundTruthFileName,
			boolean drawBoundingBoxes,
			boolean useChroma,
			boolean useMorph,
			int numThreads,
			boolean multiThreaded,
			boolean forkJoin) {
		try {
			String outputFileName = Utility.getOutputFilePath(outputDir, patternFileName, datasetFileName, transformationClasses);
			TIME_SCALE_FACTOR = 1.0;
			PointSet pattern = new PointSet(
					new File(patternFileName), 
					pitchSpell, 
					midTimePoint,
					dimensionMask,
					useChroma,
					useMorph);
			System.out.println("Pattern:\n"+pattern+"\n");
			PointSet dataset = new PointSet(
					new File(datasetFileName),
					pitchSpell,
					midTimePoint,
					dimensionMask,
					useChroma,
					useMorph);
			System.out.println("Dataset:\n"+dataset+"\n");
			if (minSize < 0)
				minSize = pattern.size()+minSize;
			if (minSize < 0) System.out.println(">>>ERROR! minSize is still less than zero: "+minSize+"<<<");
			int beginIndex = patternFileName.lastIndexOf("/")+1;
			String patternFileNameOnly = patternFileName.substring(beginIndex);
			beginIndex = datasetFileName.lastIndexOf("/")+1;
			String datasetFileNameOnly = datasetFileName.substring(beginIndex);
			dataset.setTitle(String.format("Matches of %s in %s wrt to %s, with parameters %s%s%s%s%s%s%s%s",
					patternFileNameOnly,
					datasetFileNameOnly,
					MaxTranPats.getTransformationClasses(),
					pitchSpell?"-"+MaxTranPats.DIATONIC_PITCH_SWITCH:"",
							midTimePoint?" -"+MaxTranPats.MID_TIME_POINT_SWITCH:"",
									(" -"+MaxTranPats.DIMENSION_MASK_SWITCH +" "+ MaxTranPats.DIMENSION_MASK),
									minSize != 0?" -"+MaxTranPats.MIN_PATTERN_SIZE_SWITCH+ " " + MaxTranPats.MIN_PATTERN_SIZE:"",
											minCompactness != 0.0?" -"+MaxTranPats.MIN_COMPACTNESS_SWITCH+" "+MaxTranPats.MIN_COMPACTNESS:"",
													minOccurrenceCompactness != 0.0?" -"+MaxTranPats.MIN_OCC_COMPACTNESS_SWITCH+" "+MaxTranPats.MIN_OCC_COMPACTNESS:"",
															useChroma?" -"+MaxTranPats.CHROMA_SWITCH:"",
																	useMorph?" -"+MaxTranPats.MORPH_SWITCH:""
					));
			return maximalTransformedMatches(
					pattern, 
					dataset, 
					outputFileName, 
					transformationClasses, 
					minSize, 
					HASH_TABLE_SIZE, 
					draw, 
					pitchSpell, 
					minCompactness, 
					minOccurrenceCompactness,
					groundTruthFileName,
					midTimePoint,
					drawBoundingBoxes,
					useChroma,
					useMorph,
					numThreads,
					multiThreaded,
					forkJoin);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DimensionalityException e) {
			e.printStackTrace();
		} catch (SuperMTPsNotNullException e) {
			e.printStackTrace();
		} catch (NoTransformationClassesDefinedException e) {
			e.printStackTrace();
		} catch (TimeOutException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PointSet encodePointSetFromFile(
			String fileName, 
			TransformationClass[] transformationClasses, 
			boolean pitchSpell,
			boolean midTimePoint,
			String dimensionMask,
			String outputDir,
			boolean useScalexia,
			int minSize,
			boolean draw,
			double minCompactness,
			double minOccurrenceCompactness,
			String groundTruthFileName,
			boolean drawBoundingBoxes,
			boolean useChroma,
			boolean useMorph,
			int numThreads,
			boolean multiThreaded,
			boolean forkJoin) {
		try {
			String outputFileName = Utility.getOutputFilePath(outputDir, fileName, transformationClasses);
			PointSet ps = new PointSet(
					new File(fileName), 
					pitchSpell, 
					midTimePoint,
					dimensionMask,
					useChroma,
					useMorph);
			encodePointSet(
					ps, 
					outputFileName, 
					transformationClasses, 
					useScalexia, 
					minSize, 
					HASH_TABLE_SIZE, 
					draw, 
					pitchSpell, 
					minCompactness, 
					minOccurrenceCompactness, 
					groundTruthFileName, 
					midTimePoint, 
					drawBoundingBoxes, 
					useChroma, 
					useMorph,
					numThreads,
					multiThreaded,
					forkJoin);
			return ps;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DimensionalityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void compressNLBSingleFiles(int startIndex, int endIndex) {
		String inputDir = "../../nlb20210504/data/nlb/nlb_datasets/annmidi";
		String outputDir = "../../nlb20210504/output/single-files-F2T";
		String[] nlbFileNames = Utility.getInputFileNames(inputDir);

		TransformationClass[][] transformationClassArrays = new TransformationClass[][] {
			new TransformationClass[] {new F_2T()},
			//						new TransformationClass[] {new F_2TR()},
			//			new TransformationClass[] {new F_2STR()},
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
						3,
						false,
						0.0,
						0.0,
						null,
						false,
						false,
						false,
						0,
						false,
						true);
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
								count,
								false,
								0.0,
								0.0,
								null,
								false,
								false,
								false,
								0,
								false,
								true);		
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
			//						new TransformationClass[] {new F_2TR()},
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
								count,
								false,
								0.0,
								0.0,
								null,
								false,
								false,
								false,
								0,
								false,
								true);		
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
				3, //minSize
				false, // draw
				0.0, // minCompactness
				0.0, // minOccurrenceCompactness
				null,
				false,
				false,
				false,
				0,
				false,
				true
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

	public void computeMaximalTransformablePatterns(int minSize, TransformationClass... tcs) throws NoTransformationClassesDefinedException, Exception {
		transformationClasses = new TreeSet<TransformationClass>();
		for (TransformationClass tc : tcs)
			addTransformationClass(tc);
		computeMaximalTransformablePatternsWithHashTable(minSize);
	}

	public static void encodeFilesInFolder(String inputFolder, String outputFolder, String filter) {
		String[] inputFileNames = Utility.getInputFileNames(inputFolder);
		TransformationClass[][] transformationClassArrays = new TransformationClass[][] {
			//			new TransformationClass[] {new F_2T()},
			//			new TransformationClass[] {new F_2TR()},
			new TransformationClass[] {new F_2STR()},
			//			new TransformationClass[] {new F_2T(), new F_2TR()},
			//			new TransformationClass[] {new F_2TR(), new F_2STR()},
			//			new TransformationClass[] {new F_2STR(), new F_2T()},
			//			new TransformationClass[] {new F_2T(), new F_2TR(), new F_2STR() }
		};
		for(int i = 0; i < inputFileNames.length; i++) {
			String fileName = inputFileNames[i];
			for(TransformationClass[] transformationClassArray : transformationClassArrays) {
				if (fileName.startsWith(filter)) {
					encodePointSetFromFile(
							inputFolder+"/"+fileName, 
							transformationClassArray, 
							true, // pitchSpell
							true, // midTimePoint
							"1100", //dimensionMask
							outputFolder,
							false, //useScalexia
							3, // minSize
							false,
							0.0,
							0.0,
							null,
							false,
							false,
							false,
							0,
							false,
							true
							);
				}
			}
		}

	}

	public static void drawGroundTruthFile(
			String groundTruthFilePath,
			String datasetFilePath,
			boolean diatonicPitch, 
			boolean midTimePoint,
			String dimensionMask,
			boolean drawBoundingBoxes,
			boolean useChroma,
			boolean useMorph) throws IOException, DimensionalityException {
		final PointSet dataset = new PointSet(new File(datasetFilePath),diatonicPitch,midTimePoint,dimensionMask,useChroma,useMorph);
		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> groundTruthPatterns = readGroundTruthPatternsFromFile(groundTruthFilePath, diatonicPitch, midTimePoint, 1, useMorph, useChroma);
		final TreeSet<com.chromamorph.maxtranpatsjava.Point> points = dataset.getPoints();
		final int endIndex = groundTruthFilePath.lastIndexOf(".");
		String pitchType = useChroma?"C":(useMorph?"M":(diatonicPitch?"MP":"CP"));
		String timeType = midTimePoint?"M":"O";
		final String outputFilePath = groundTruthFilePath.substring(0, endIndex)+"-"+pitchType+timeType+".png";
		com.chromamorph.points022.PointSet ps = new com.chromamorph.points022.PointSet(); 
		for(com.chromamorph.maxtranpatsjava.Point p : points) {
			long onset = midTimePoint?(long)Math.floor(p.get(0)):p.getOnset();
			ps.add(new com.chromamorph.points022.Point(onset,(int)(Math.floor(p.get(1)))));
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
				PApplet embed = new DrawPoints(
						ps,
						groundTruthPatterns,
						true,//drawAllOccurrenceSetsAtOnce
						diatonicPitch,
						dataset.getTatumsPerBar(),
						dataset.getBarOneStartsAt(),
						dataset.getTitle(),
						outputFilePath,
						false, //segmentation
						true, //writeToImageFile
						drawBoundingBoxes, //drawBoundingBoxes
						useChroma,
						useMorph
						);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public static void computeOccurrenceSetTransformationGraph(
			String groundTruthFilePath,
			TransformationClass[] transformationClasses, 
			String outputDirPath, 
			boolean diatonicPitch,
			boolean midTimePoint, 
			String dimensionMask, 
			boolean chroma, 
			boolean morph,
			int xScaleFactor,
			int numThreads,
			boolean multiThreaded,
			boolean forkJoin) {
		IS_OSTG = true;
		ArrayList<ArrayList<com.chromamorph.points022.PointSet>> groundTruthPatterns = readGroundTruthPatternsFromFile(groundTruthFilePath, diatonicPitch, midTimePoint, xScaleFactor, morph, chroma);
		//		Flatten groundTruthPatterns into an array of MaxTranPats PointSets
		ArrayList<PointSet> patternList = new ArrayList<PointSet>();
		for(ArrayList<com.chromamorph.points022.PointSet> os : groundTruthPatterns) {
			for (com.chromamorph.points022.PointSet ps : os) {
				PointSet newPs = new PointSet();
				TreeSet<com.chromamorph.points022.Point> points = ps.getPoints();
				for (com.chromamorph.points022.Point oldPoint : points)
					newPs.add(new Point((double)oldPoint.getX(),(double)oldPoint.getY()));
				newPs.setLabel(ps.getLabel());
				patternList.add(newPs);
			}
		}
		//		Sort patterns in patternList by label
		Collections.sort(patternList,new Comparator<PointSet>() {

			@Override
			public int compare(PointSet o1, PointSet o2) {
				if (o1.getLabel() == null && o2.getLabel() == null) 
					return o1.compareTo(o2);
				if (o1.getLabel() == null) return -1;
				if (o2.getLabel() == null) return 1;
				if (o1.getLabel().equals(o2.getLabel()))
					return o1.compareTo(o2);
				try {
					int d = Integer.parseInt(o1.getLabel())-Integer.parseInt(o2.getLabel());
					if (d != 0)
						return d;
				} catch (NumberFormatException e) {}
				if (o1.getLabel()==null)
					return -1;
				if (o2.getLabel()==null)
					return 1;
				return o1.getLabel().compareTo(o2.getLabel());
			}

		});
		TIME_SCALE_FACTOR = 1.0;
		ArrayList<ArrayList<TreeSet<Transformation>>> transformationGraph = new ArrayList<ArrayList<TreeSet<Transformation>>>();
		for(int i = 0; i < patternList.size(); i++) {
			ArrayList<TreeSet<Transformation>> row = new ArrayList<TreeSet<Transformation>>();
			for(int j = 0; j < patternList.size(); j++) {
				row.add(null);
			}
			transformationGraph.add(row);
		}
		TreeSet<Integer> colsWithMultipleTransformations = new TreeSet<Integer>();
		for(int i = 0; i < patternList.size(); i++)
			for(int j = 0; j < patternList.size(); j++) {
				try {
					PointSet objectPattern = new PointSet(patternList.get(i).toString());
					PointSet imagePattern = new PointSet(patternList.get(j).toString());
					TreeSet<Transformation> transformations = null;
					if (imagePattern.size() == objectPattern.size()) {

						//				Now we need to do MTM of objectPattern in imagePattern
						//				and select only complete matches, 
						//				then find the transformations for these complete matches
						PointSet maxTransformedMatches = maximalTransformedMatches(			
								objectPattern, 
								imagePattern, 
								null, 
								transformationClasses, 
								objectPattern.size(),
								HASH_TABLE_SIZE, 
								false, 
								diatonicPitch,
								0,
								0,
								null,
								midTimePoint,
								false,
								chroma,
								morph,
								numThreads,
								multiThreaded,
								forkJoin);
						ArrayList<OccurrenceSet> occurrenceSets = maxTransformedMatches.getEncoding().getOccurrenceSets();
						transformations = (occurrenceSets != null && occurrenceSets.size() > 0)?occurrenceSets.get(0).getTransformations():null;
					}

					transformationGraph.get(i).set(j,transformations);
					if (transformations != null) {
						System.out.println(patternList.get(i).getLabel()+": "+objectPattern);
						System.out.println(patternList.get(j).getLabel()+": "+imagePattern);
						System.out.println(transformations);
						System.out.println();
					}
					if (transformations != null && transformations.size() > 1)
						colsWithMultipleTransformations.add(j);
				} catch (FileNotFoundException | TimeOutException | NoTransformationClassesDefinedException
						| SuperMTPsNotNullException | NullPointerException e) {
					e.printStackTrace();
					System.exit(1);
				} catch (InvalidArgumentException e) {
					e.printStackTrace();
				}
			}
		try {
			int startOfSuffix = groundTruthFilePath.indexOf('.');
			String pitchType = (chroma?"C":(morph?"M":(diatonicPitch?"MP":"CP")));
			String timeType = (midTimePoint?"M":"O");
			String repType = "-"+pitchType+timeType;
			String transformationClassesString = TransformationClass.getTransformationClassesString(transformationClasses);
			String graphFilePath = groundTruthFilePath.substring(0,startOfSuffix)+repType+"-"+transformationClassesString+"-IPTG.tex";
			PrintWriter graphFile = new PrintWriter(graphFilePath);
			graphFile.println("\\begin{sidewaystable}");
			graphFile.println("\\caption{"+graphFilePath+"}");
			int startOfName = graphFilePath.lastIndexOf('/')+1;
			String graphFileName = graphFilePath.substring(startOfName,startOfSuffix);
			graphFile.println("\\label{"+graphFileName+repType+"-"+transformationClassesString+"-IPTG}");
			graphFile.println("\\resizebox{\\linewidth}{!}{");
			graphFile.print("\\begin{tabularx}{2\\linewidth}{ll|");
			for(int i = 0; i < patternList.size();i++) {
				if (colsWithMultipleTransformations.contains(i))
					graphFile.print("p{1.8cm}");
				else
					graphFile.print("l");
			}
			graphFile.println("}");
			graphFile.println("&&\\multicolumn{"+patternList.size()+"}{c}{{\\bfseries To pattern}}\\\\");
			graphFile.print("&"); // For the "From" column
			for (int i = 0; i < patternList.size(); i++) 
				graphFile.print("&"+patternList.get(i).getLabel());
			graphFile.println("\\\\\\cline{1-"+(patternList.size()+2)+"}");
			for (int i = 0; i < patternList.size()-1; i++) {
				graphFile.print("&"+patternList.get(i).getLabel()+" &");
				for (int j = 0; j < patternList.size(); j++) {
					String tranString = F_2STR.getOSTGString(transformationGraph.get(i).get(j));
					graphFile.print(tranString+" "+((j!=patternList.size()-1)?"&":"\\\\\\cline{2-"+(patternList.size()+2)+"}\n"));
				}
			}
			//			Print last line in table which also prints vertical From label
			graphFile.print("\\multirow{-"+patternList.size()+"}{*}{\\rotatebox[origin=c]{90}{{\\bfseries From pattern}}}&"+patternList.get(patternList.size()-1).getLabel()+" &");
			for (int j = 0; j < patternList.size(); j++) {
				String tranString = F_2STR.getOSTGString(transformationGraph.get(patternList.size()-1).get(j));
				graphFile.print(tranString+" "+((j!=patternList.size()-1)?"&":"\\\\\\cline{2-"+(patternList.size()+2)+"}\n"));
			}

			//			graphFile.println("\\hline");
			graphFile.println("\\end{tabularx}");
			graphFile.println("}");
			graphFile.println("\\end{sidewaystable}");
			graphFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			PointSet ps = new PointSet("P(p(252,62),p(256,60),p(258,53),p(260,53),p(262,58))");
			System.out.println("PointSet is "+ps);
			System.out.println("points is "+ps.getPoints());
			System.out.println("pointsArray is "+ps.getPointsArray());
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}

		//		for (int patternSize = 20; patternSize < 10000; patternSize += 10) {
		//			PointSet P = new PointSet();
		//			for (double i = 0; i < patternSize; i++) {
		//				P.add(new Point(i,2*i));
		//			}
		//			PointSequence basis = null;
		//			long start = System.currentTimeMillis();
		//
		//			try {
		//				basis = P.computeBasis(13, 8);
		//			} catch (Exception e) {
		//				System.out.println(e.getMessage());
		//			}
		//			long end = System.currentTimeMillis();
		//			long runningTime = end - start;
		//			System.out.println(String.format("%20s%10d%10dms%10.20f", basis, patternSize, runningTime, runningTime/(1.0 * patternSize)));
		//
		//		}		
	}



}
