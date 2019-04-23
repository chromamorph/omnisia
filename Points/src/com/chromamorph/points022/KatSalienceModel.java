package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.chromamorph.maths.Maths;
import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class KatSalienceModel {

	//	For Experiment 1
	static String pathToParentDir = "data/Kat Agres/Experiment 1/";
	//	For Experiment 2
//		static String pathToParentDir = "data/Kat Agres/Experiment 2/";
	static String pathToOutputFile = pathToParentDir+"20180715-output-normalized-diff-master.txt";


	static double[] majorHierarchy = new double[]{6.35, 2.23, 3.48, 2.33, 4.38, 4.09, 2.52, 5.19, 2.39, 3.66, 2.29, 2.88};
	static int[] metricalHierarchy = new int[]{16, 8, 12, 4, 14, 6, 10, 2, 15, 7, 11, 3, 13, 5, 9, 1};
	static long ticksPerQuarterNote, ticksPerEighthNote;

	static String pathToStimuliFiles = pathToParentDir+"Stimuli";
	static String pathToNormNonMusAvgFile = pathToParentDir+"normNonMusAvg.txt";
	static String pathToNormProAvgFile = pathToParentDir+"normProAvg.txt";

	static ArrayList<ResultPair> resultPairList = new ArrayList<ResultPair>();

	//	Following read in from normNonMusAvg.txt and normProAvg.txt files, respectively.
	static Double[] normNonMusAvg;
	static Double[] normProAvg;
	static ArrayList<String> normAvgDataFileNames = null;

	/**
	 * Following data comes from sheet KatSalienceModel01
	 * in file /Volumes/LOTOFSPACE/Dropbox/Work/Research/Data/Kat/Data_forDMeredith_AvgPerStim-modified.xlsx
	 * actually now in file data/Kat Agres/Data_forDMeredith_AvgPerStim-modified.xlsx
	 */
	//	static double[] normNonMusAvg = new double[]{
	//			0.173913043,
	//			0.942028986,
	//			0.260869565,
	//			0.231884058,
	//			0.347826087,
	//			0.347826087,
	//			0.579710145,
	//			0.449275362,
	//			0.275362319,
	//			0.333333333,
	//			0.550724638,
	//			0.753623188,
	//			0.666666667,
	//			0.449275362,
	//			0.579710145,
	//			0.637681159,
	//			0.347826087,
	//			0.449275362,
	//			0.594202899,
	//			0.31884058,
	//			0.333333333,
	//			0.333333333,
	//			0.463768116,
	//			0.608695652,
	//			0.333333333,
	//			0.594202899,
	//			0.492753623,
	//			0.144927536,
	//			0.420289855,
	//			0.579710145,
	//			0.260869565,
	//			0.31884058,
	//			0.405797101,
	//			0.347826087,
	//			0.753623188,
	//			0.52173913,
	//			1,
	//			0.304347826,
	//			0.869565217,
	//			0.405797101,
	//			0.231884058,
	//			0.666666667,
	//			0.550724638,
	//			0.231884058,
	//			0.289855072,
	//			0.31884058,
	//			0.202898551,
	//			0.202898551,
	//			0.449275362,
	//			0.47826087,
	//			0.449275362,
	//			0.927536232,
	//			0.695652174,
	//			0.202898551,
	//			0.115942029,
	//			0.608695652,
	//			0.376811594,
	//			0.391304348,
	//			0.260869565,
	//			0.623188406,
	//			0,
	//			0.304347826,
	//			0.869565217,
	//			0.739130435,
	//			0.347826087,
	//			0.434782609,
	//			0.695652174,
	//			0.52173913,
	//			0.289855072,
	//			0.463768116,
	//			0.855072464,
	//			0.826086957,
	//	};
	//
	//	static double[] normProAvg = new double[]{
	//			0.134615385,
	//			1,
	//			0.173076923,
	//			0.288461538,
	//			0.461538462,
	//			0.384615385,
	//			0.730769231,
	//			0.634615385,
	//			0.346153846,
	//			0.326923077,
	//			0.730769231,
	//			1,
	//			0.980769231,
	//			0.884615385,
	//			0.730769231,
	//			0.865384615,
	//			0.480769231,
	//			0.403846154,
	//			0.788461538,
	//			0.423076923,
	//			0.807692308,
	//			0.365384615,
	//			0.634615385,
	//			0.596153846,
	//			0.442307692,
	//			0.576923077,
	//			0.269230769,
	//			0.057692308,
	//			0.307692308,
	//			0.557692308,
	//			0.480769231,
	//			0.288461538,
	//			0.192307692,
	//			0.576923077,
	//			1,
	//			0.865384615,
	//			0.903846154,
	//			0.519230769,
	//			0.980769231,
	//			0.346153846,
	//			0.384615385,
	//			0.788461538,
	//			0.480769231,
	//			0.230769231,
	//			0.115384615,
	//			0.673076923,
	//			0.384615385,
	//			0.211538462,
	//			0.673076923,
	//			0.557692308,
	//			0,
	//			0.942307692,
	//			0.884615385,
	//			0.115384615,
	//			0.576923077,
	//			0.653846154,
	//			0.596153846,
	//			0.634615385,
	//			0.673076923,
	//			0.903846154,
	//			0.403846154,
	//			0.673076923,
	//			0.961538462,
	//			0.903846154,
	//			0.519230769,
	//			0.826923077,
	//			0.961538462,
	//			0.961538462,
	//			0.326923077,
	//			0.576923077,
	//			0.961538462,
	//			0.980769231,
	//	};

	static int N;

	static double calcMean(Double[] data) {
		double sum = 0.0;
		for(int i = 0; i < data.length; i++)
			sum += data[i];
		return sum/data.length;
	}

	static double calcStdDev(Double[] data) {
		double mean = calcMean(data);
		double sumSquareDiffs = 0.0;
		for(int i = 0; i < data.length; i++)
			sumSquareDiffs += (data[i]-mean)*(data[i]-mean);
		return Math.sqrt(sumSquareDiffs/(N-1));
	}



	/**
	 * Returns 0.5 for an eighth note and 1.0 for a quarter note
	 * @param point
	 * @return
	 */
	static double computeDurationSalience(Point point) {
		return Math.round((2.0*point.getDuration())/ticksPerQuarterNote)/2.0;
//		return (1.0*point.getDuration())/ticksPerQuarterNote;

	}

	static ArrayList<Double> computeDurationSaliences(PointSet ps1) {
		ArrayList<Double> durationSaliences = new ArrayList<Double>();
		for (Point point : ps1.getPoints()) {
			durationSaliences.add(computeDurationSalience(point));
		}
		return durationSaliences;
	}

	/**
	 * Computes tonic by comparing chromagram for whole first melody with 
	 * tonal hierarchies for F major, C major, G major and D major.
	 * @param ps1
	 * @return
	 */
	static int getTonic(PointSet ps1) {
		int[] chromagram = new int[12];
		for(Point p : ps1.getPoints())
			chromagram[Maths.mod(p.getY(), 12)]++;
		double bestMatch = 0.0;
		int bestTonic = 5;
		for(int tonic = 5; tonic != 9; tonic = Maths.mod(tonic+7, 12)) {
			double match = 0.0;
			for(int i = 0; i < 12; i++) {
				match += majorHierarchy[i]*chromagram[Maths.mod(i+tonic, 12)];
			}
			if (match > bestMatch) {
				bestMatch = match;
				bestTonic = tonic;
			}
		}
		return bestTonic;
	}

	/**
	 * Returns tonal hierarchy rating (Krumhansl and Kessler 1982), scaled
	 * to a number between 0 and 1 for the pitch class, relative to the 
	 * given tonic pitch class.
	 * @param point
	 * @param tonic
	 * @return
	 */
	static double computeTonalSalience(Point point, int tonic) {
		return majorHierarchy[Maths.mod(point.getY()-tonic,12)]/6.35;
	}

	static ArrayList<Double> computeTonalSaliences(PointSet ps1) {
		int tonic = getTonic(ps1);
		ArrayList<Double> saliences = new ArrayList<Double>();
		for (Point point : ps1.getPoints()) {
			saliences.add(computeTonalSalience(point, tonic));
		}
		return saliences;
	}

	/**
	 * Assumes binary meter spreading over both measures,
	 * i.e., positions in second measure are one less than
	 * their corresponding positions in the first bar.
	 * Returns a value between 0 and 1, where 1 corresponds 
	 * to the strongest beat and 0 to the weakest.
	 * @param point
	 * @return
	 */
	static double computeMetricalSalience(Point point) {
		long onset = point.getX();
		int position = (int)(onset/ticksPerEighthNote);
		return (1.0*metricalHierarchy[position]-1)/15;
	}

	static ArrayList<Double> computeMetricalSaliences(PointSet ps1) {
		ArrayList<Double> saliences = new ArrayList<Double>();
		for (Point point : ps1.getPoints()) {
			saliences.add(computeMetricalSalience(point));
		}
		return saliences;
	}

	static double computeInstabilitySalience(Point point, int tonic) {
		return (6.35-majorHierarchy[Maths.mod(point.getY()-tonic,12)])/6.35;
	}

	static ArrayList<Double> computeInstabilitySaliences(PointSet ps) {
		int tonic = getTonic(ps);
		ArrayList<Double> saliences = new ArrayList<Double>();
		for (Point point : ps.getPoints()) {
			saliences.add(computeInstabilitySalience(point, tonic));
		}
		return saliences;
	}

	static ArrayList<Double> computeTotalSaliences(
			ArrayList<Double> durationSaliences, double durationWeight,
			ArrayList<Double> tonalSaliences, double tonalWeight,
			ArrayList<Double> metricalSaliences, double metricalWeight,
			ArrayList<Double> instabilityOneSaliences, double instabilityOneWeight,
			ArrayList<Double> instabilityTwoSaliences, double instabilityTwoWeight,
			ArrayList<Double> stabilityTwoSaliences, double stabilityTwoWeight) {
		ArrayList<Double> totalSaliences = new ArrayList<Double>();
		for(int i = 0; i < tonalSaliences.size(); i++) {
			double totalSalience = 
					durationWeight*durationSaliences.get(i)+
					tonalWeight*tonalSaliences.get(i)+
					metricalWeight*metricalSaliences.get(i)+
					instabilityOneWeight*instabilityOneSaliences.get(i)+
					instabilityTwoWeight*instabilityTwoSaliences.get(i)+
					stabilityTwoWeight*stabilityTwoSaliences.get(i);
			totalSaliences.add(totalSalience);
		}
		return totalSaliences;
	}

	//							nonmus		mus
	//	tonal and metrical		.213		.027
	//	tonal, met and dur		.341		.256
	//	dur		ton		met
	//	1.5		0.75	0.75	0.362		0.313
	//	1.5		0		1.5		0.165		0.119
	//	1.8		.6		.6		0.367		0.340
	//	2.0		.5		.5		0.3673		0.3532
	//	3.0		0		0		0.3495		0.3884
	//	

	static class ResultPair {
		double difference, maxSalience, minSalience, durWt, tonOneWt, metWt, insOneWt, insTwoWt, tonTwoWt;
		String fileName;
		ArrayList<Double> durationSaliences, 
			tonalSaliences, metricalSaliences, totalSaliences, instabilityOneSaliences, instabilityTwoSaliences, stabilityTwoSaliences;

		ResultPair(PointSet ps1, PointSet ps2, String fileName, 
				double durationWeight,
				double tonalWeight,
				double metricalWeight,
				double instabilityOneWeight,
				double instabilityTwoWeight,
				double stabilityTwoWeight) {
			
			durWt = durationWeight;
			tonOneWt = tonalWeight;
			metWt = metricalWeight;
			insOneWt = instabilityOneWeight;
			insTwoWt = instabilityTwoWeight;
			tonTwoWt = stabilityTwoWeight;
			
			this.fileName = fileName;
			durationSaliences = computeDurationSaliences(ps1);
			tonalSaliences = computeTonalSaliences(ps1);
			metricalSaliences = computeMetricalSaliences(ps1);
			instabilityOneSaliences = computeInstabilitySaliences(ps1);
			instabilityTwoSaliences = computeInstabilitySaliences(ps2);
			stabilityTwoSaliences = computeTonalSaliences(ps2);
			totalSaliences = computeTotalSaliences(
					durationSaliences, durationWeight,
					tonalSaliences, tonalWeight,
					metricalSaliences, metricalWeight,
					instabilityOneSaliences, instabilityOneWeight,
					instabilityTwoSaliences, instabilityTwoWeight,
					stabilityTwoSaliences, stabilityTwoWeight);
			maxSalience = totalSaliences.get(0);
			minSalience = totalSaliences.get(0);
			for(double s : totalSaliences)
				if (s > maxSalience)
					maxSalience = s;
				else if (s < minSalience)
					minSalience = s;
			
			if (ps1.equals(ps2)) {
				//				Likelihood of sets being perceived as different is inversely related to the max salience minus the min salience.
				//				If there is a broad range in salience, then the pattern is perceived to have more structure and it is easier to detect
				//				that two occurrences of it are the same.
				difference = maxSalience-minSalience;
			} else {
				//				The two patterns are different by one note.
				//				Identify the index of the note that is different.
				int i = 0;
				while(i < ps1.size())
					if (ps1.get(i).getY()!=ps2.get(i).getY())
						break;
					else
						i++;

				//				The difference is proportional to the salience of the changed note in the first pattern.
				//				If the changed note has high salience in the first pattern, then it is more likely to be remembered.
				if (i >= totalSaliences.size()) {
					//					System.out.println(ps1);
					//					System.out.println(ps2);
					//					System.out.println(fileName);
					difference = maxSalience-minSalience;
				} else
//					difference = totalSaliences.get(i);
					difference = (totalSaliences.get(i) - minSalience)/(maxSalience - minSalience);
			}
		}

		private String printSaliences(String rowHeader, ArrayList<Double> saliences) {
			String outputString = "\n" + rowHeader + "\t" + String.format("\t%.2f", saliences.get(0));
			for(int i = 1; i < saliences.size(); i++)
				outputString += String.format("\t%.2f", saliences.get(i));
			return outputString;
		}

		private String printSaliences(String rowHeader, Double wt, ArrayList<Double> saliences) {
			String outputString = "\n" + rowHeader + String.format("\t%.2f", wt) + String.format("\t%.2f", saliences.get(0));
			for(int i = 1; i < saliences.size(); i++)
				outputString += String.format("\t%.2f", saliences.get(i));
			return outputString;
		}
		
		private String printColumnHeadings() {
			String outputString = "\n"+String.format("\twt\t%d", 0);
			for(int i = 1; i < durationSaliences.size(); i++)
				outputString += String.format("\t%d", i);
			return outputString;
		}
		
		public String toString() {
//			return ""+difference;
			return fileName+
					printColumnHeadings()+
					printSaliences("dur", durWt, durationSaliences)+
					printSaliences("met", metWt, metricalSaliences)+
					printSaliences("ins1", insOneWt, instabilityOneSaliences)+
					printSaliences("ton1", tonOneWt, tonalSaliences)+
					printSaliences("ins2", insTwoWt, instabilityTwoSaliences)+
					printSaliences("ton2", tonTwoWt, stabilityTwoSaliences)+
					printSaliences("tot", totalSaliences)+
					"\nMax salience:\t" + String.format("%.2f", maxSalience)+
					"\nMin salience:\t" + String.format("%.2f", minSalience)+
					"\nDifference:\t"+String.format("%.2f", difference)
					;
		}
	}

	static long computeTicksPerEighthNote(PointSet ps1) {
		long tpen = ps1.get(1).getX()-ps1.get(0).getX();
		for(int i = 2; i < ps1.size(); i++) {
			long ioi = ps1.get(i).getX() - ps1.get(i-1).getX();
			if (ioi < tpen)
				tpen = ioi;
		}
		return tpen;
	}

	static ResultPair getResultPair(String pfn,
			double durationWeight,
			double tonalWeight,
			double metricalWeight,
			double instabilityOneWeight,
			double instabilityTwoWeight,
			double stabilityTwoWeight,
			boolean usePathToStimuliFiles) {
		try {
			PointSet ps;
			if (usePathToStimuliFiles)
				ps = new PointSet(pathToStimuliFiles+"/"+pfn);
			else
				ps = new PointSet(pfn);
			int totalNumberOfPoints = ps.size();
			PointSet ps1 = new PointSet();
			PointSet ps2 = new PointSet();
			for (int i = 0; i < totalNumberOfPoints/2; i++) {
				ps1.add(ps.get(i));
				ps2.add(ps.get(i+totalNumberOfPoints/2));
			}
			long timeComponent = ps2.get(0).getX();
			Vector v = new Vector(-timeComponent, 0);
			ps2 = ps2.translate(v);
			ticksPerEighthNote = computeTicksPerEighthNote(ps1);
			ticksPerQuarterNote = 2 * ticksPerEighthNote;
			return new ResultPair(ps1,ps2,pfn,
					durationWeight,
					tonalWeight,
					metricalWeight,
					instabilityOneWeight,
					instabilityTwoWeight,
					stabilityTwoWeight);
		} catch (MissingTieStartNoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	static Double calcCorrel(Double[] x, Double[] y) {
		//		System.out.println("calcCorrel called with N = "+x.length);
		double meanX = calcMean(x);
		double meanY = calcMean(y);
		double stdDevX = calcStdDev(x);
		double stdDevY = calcStdDev(y);
		double corrNum = 0.0;
		for(int i = 0; i < x.length; i++)
			corrNum += (x[i]-meanX)*(y[i]-meanY);
		return corrNum/((N-1)*stdDevX*stdDevY);
	}

	public static Double[] readNormAvgData(String pathToNormAvgDataFile) throws Exception {
		ArrayList<Double> data = new ArrayList<Double>();
		boolean makeNormAvgDataFileNames = false;
		if (normAvgDataFileNames == null) {
			normAvgDataFileNames = new ArrayList<String>();
			makeNormAvgDataFileNames = true;
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(pathToNormAvgDataFile));
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				String[] a = line.trim().split("\t");
				data.add(Double.parseDouble(a[1]));
				if (makeNormAvgDataFileNames)
					normAvgDataFileNames.add(a[0]);
				else
					if (!normAvgDataFileNames.get(i).equals(a[0])) {
						br.close();
						throw new Exception("normalized averages in different order in pro and non-mus files!");
					}
				i++;
			}
			br.close();
			System.out.println("\nFinished reading file "+pathToNormAvgDataFile);
			System.out.println("Number of items in file is "+data.size());
			System.out.println("Last item in file is "+data.get(data.size()-1));
			return data.toArray(new Double[data.size()]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static class WeightCombinationResult {
		double durationWeight, tonalWeight, instabilityOneWeight, 
		instabilityTwoWeight, stabilityTwoWeight, metricalWeight,
		nonMusCorrel, proCorrel;

		WeightCombinationResult (double durationWeight, double tonalWeight, double instabilityOneWeight, 
				double instabilityTwoWeight, double stabilityTwoWeight, double metricalWeight,
				double nonMusCorrel, double proCorrel) {
			this.durationWeight = durationWeight; 
			this.tonalWeight = tonalWeight; 
			this.instabilityOneWeight = instabilityOneWeight; 
			this.instabilityTwoWeight = instabilityTwoWeight;
			this.stabilityTwoWeight = stabilityTwoWeight;
			this.metricalWeight = metricalWeight;
			this.nonMusCorrel = nonMusCorrel; 
			this.proCorrel = proCorrel;
		}

		public String toString() {
			return String.format("%.1f",durationWeight)+"\t"+
					String.format("%.1f", tonalWeight)+"\t"+
					String.format("%.1f",metricalWeight)+"\t"+
					String.format("%.1f", instabilityOneWeight)+"\t"+
					String.format("%.1f", instabilityTwoWeight)+"\t"+
					String.format("%.1f", stabilityTwoWeight)+"\t"+
					String.format("%.4f", nonMusCorrel)+"\t"+
					String.format("%.4f", proCorrel);
		}
	}

	static class ProComparator implements Comparator<WeightCombinationResult> {
		@Override
		public int compare(WeightCombinationResult o1, WeightCombinationResult o2) {
			int d = (int)Math.signum(o2.proCorrel-o1.proCorrel);
			if (d != 0) return d;
			d = (int)Math.signum(o2.nonMusCorrel-o1.nonMusCorrel);
			if (d != 0) return d;
			d = (int)Math.signum(o1.durationWeight-o2.durationWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.instabilityOneWeight-o2.instabilityOneWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.instabilityTwoWeight-o2.instabilityTwoWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.metricalWeight-o2.metricalWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.stabilityTwoWeight-o2.stabilityTwoWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.tonalWeight-o2.tonalWeight);
			if (d != 0) return d;					
			return 0;
		}
	}

	static class NonMusComparator implements Comparator<WeightCombinationResult> {
		@Override
		public int compare(WeightCombinationResult o1, WeightCombinationResult o2) {
			int d = (int)Math.signum(o2.nonMusCorrel-o1.nonMusCorrel);
			if (d != 0) return d;
			d = (int)Math.signum(o2.proCorrel-o1.proCorrel);
			if (d != 0) return d;
			d = (int)Math.signum(o1.durationWeight-o2.durationWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.instabilityOneWeight-o2.instabilityOneWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.instabilityTwoWeight-o2.instabilityTwoWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.metricalWeight-o2.metricalWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.stabilityTwoWeight-o2.stabilityTwoWeight);
			if (d != 0) return d;
			d = (int)Math.signum(o1.tonalWeight-o2.tonalWeight);
			if (d != 0) return d;					
			return 0;
		}
	}

	public static void katSalienceModel() throws Exception {

		normNonMusAvg = readNormAvgData(pathToNormNonMusAvgFile);
		normProAvg = readNormAvgData(pathToNormProAvgFile);

		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".mid");
			}

		};
		String[] pairFileNames = new File(pathToStimuliFiles).list(filter);
		//		Check pairFileNames in same order as in normalized avg data
		for(int i = 0; i < pairFileNames.length; i++)
			if (!pairFileNames[i].equals(normAvgDataFileNames.get(i))) {
				throw new Exception("pairFileNames in different order from normNonMusAvg data!");
			}
		N = normNonMusAvg.length;
		Double[] compDiffList = new Double[pairFileNames.length];

		ArrayList<WeightCombinationResult> weightCombinationResults = new ArrayList<WeightCombinationResult>();
		for(double durationWeight = 0.0; durationWeight <= 1.000001; durationWeight += .1)
			for(double tonalWeight = 0.0; tonalWeight <= 1.000001; tonalWeight += 0.1) 
				for(double instabilityOneWeight = 0.0; instabilityOneWeight <= 1.000001; instabilityOneWeight += 0.1)
					for(double instabilityTwoWeight = 0.0; instabilityTwoWeight <= 1.000001; instabilityTwoWeight += 0.1) 
						for(double stabilityTwoWeight = 0.0; stabilityTwoWeight <= 1.000001; stabilityTwoWeight += 0.1) {
							double metricalWeight = 1.000001 - durationWeight - tonalWeight - instabilityOneWeight - instabilityTwoWeight - stabilityTwoWeight;
							if (metricalWeight >= -0.000001) {
								for(int i = 0; i < pairFileNames.length; i++) {
									String pfn = pairFileNames[i];
									compDiffList[i] = getResultPair(pfn,
											durationWeight,
											tonalWeight,
											metricalWeight,
											instabilityOneWeight,
											instabilityTwoWeight,
											stabilityTwoWeight,
											true).difference;
								}
								double nonMusCorrel = calcCorrel(compDiffList,normNonMusAvg);
								double proCorrel = calcCorrel(compDiffList,normProAvg);
								weightCombinationResults.add(new WeightCombinationResult(durationWeight, tonalWeight, instabilityOneWeight, 
										instabilityTwoWeight, stabilityTwoWeight, metricalWeight,
										nonMusCorrel, proCorrel));
							}
						}
		PrintWriter output;
		try {
			output = new PrintWriter(pathToOutputFile);
			String title = "\ndur\tton\tmet\tinstability 1\tinstability 2\tstability 2\tnonMus\tPro";
			output.println("Top ten combinations for non-musicians:");
			Collections.sort(weightCombinationResults, new NonMusComparator());
			output.println(title);
			for(int i = 0; i < 10; i++)
				output.println(weightCombinationResults.get(i));
			output.println("\n\nTop ten combinations for Pros:");
			Collections.sort(weightCombinationResults, new ProComparator());
			output.println(title);
			for(int i = 0; i < 10; i++)
				output.println(weightCombinationResults.get(i));			
			output.println("\n\nAll results");
			output.println(title);			
			for(WeightCombinationResult wcr : weightCombinationResults)
				output.println(wcr);		
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//		for(int i = 0; i < pairFileNames.length; i++) {
		//			String pfn = pairFileNames[i];
		//			compDiffList[i] = getResultPair(pfn,
		//					0.4,
		//					0.4,
		//					0.0,
		//					0.0,
		//					0.2).difference;
		//		}
		//
		//		for(double r : compDiffList)
		//			System.out.println(r);
	}

	public static void showSaliencesForStimulus(String pathToStimulusFile, 
			double durationWeight,
			double metricalWeight,
			double instabilityOneWeight,
			double tonalOneWeight,
			double instabilityTwoWeight,
			double tonalTwoWeight) {
		ResultPair resultPair = getResultPair(pathToStimulusFile,
				durationWeight,
				tonalOneWeight,
				metricalWeight,
				instabilityOneWeight,
				instabilityTwoWeight,
				tonalTwoWeight,
				false);
		System.out.println(resultPair);
	}

	public static void main(String[] args) {
		try {
			katSalienceModel();
//			showSaliencesForStimulus("data/Kat Agres/Experiment 2/Stimuli/r1p1i3t2a.mid",
//					.1, 0, 0, .3, .6, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
