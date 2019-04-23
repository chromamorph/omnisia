package com.chromamorph.points022;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

/**
 * 
 * @author David Meredith
 * @date 3 June 2016
 *
 */
public class RecurSIAEncoding extends Encoding {
	private Encoding ENCODING = null;

	public RecurSIAEncoding(
			String inputFilePathString,
			String outputDirectoryPathString,
			boolean isDiatonic,
			boolean withoutChannel10,
			int topNPatterns,
			boolean forMirex,
			boolean segmentMode,
			boolean bbMode,
			String omnisiaOutputFilePathString,
			boolean withCompactnessTrawler, double a, int b,
			boolean forRSuperdiagonals, int r,
			boolean removeRedundantTranslators,
			double minTECCompactness,
			int minPatternSize,
			int maxPatternSize,
			boolean mergeTECs,
			int minMatchSize,
			int numIterations,
			Algorithm algorithm,
			double crLow, double crHigh, double compVLow, double compVHigh, int cMin, double sigmaMin, boolean bbCompactness,
			boolean fromOMNISIA,
			boolean sortByPatternSize
			) throws MissingTieStartNoteException, FileNotFoundException, IncompatibleRecurSIAAlgorithmException {
		super(null,inputFilePathString,
				outputDirectoryPathString,
				isDiatonic,
				withoutChannel10,
				"RecurSIA",
				topNPatterns,
				forMirex,
				segmentMode,
				bbMode,
				omnisiaOutputFilePathString);

		long startTime = System.currentTimeMillis();

		setTECs(recurSIA(dataset,
				isDiatonic,
				withCompactnessTrawler,
				a,
				b,
				forRSuperdiagonals,
				r,
				removeRedundantTranslators,
				minTECCompactness,
				minPatternSize,
				maxPatternSize,
				mergeTECs,
				minMatchSize,
				numIterations,
				topNPatterns,
				withoutChannel10,
				algorithm,
				crLow, crHigh, compVLow, compVHigh, cMin, sigmaMin, bbCompactness,
				fromOMNISIA,
				sortByPatternSize
				));

		long endTime = System.currentTimeMillis();
		setRunningTime(endTime-startTime);

		writeToFile();

	}

	private ArrayList<TEC> recurSIA(
			PointSet pointSet,
			boolean diatonicPitch,
			boolean withCompactnessTrawler,
			double a,
			int b,
			boolean forRSuperdiagonals,
			int r,
			boolean removeRedundantTranslators,
			double minTECCompactness,
			int minPatternSize,
			int maxPatternSize,
			boolean mergeTECs,
			int minMatchSize,
			int numIterations,
			int topNPatterns,
			boolean withoutChannel10,
			Algorithm algorithm,
			double crLow, double crHigh, double compVLow, double compVHigh, int cMin, double sigmaMin, boolean bbCompactness,
			boolean fromOMNISIA,
			boolean sortByPatternSize
			) throws FileNotFoundException, MissingTieStartNoteException, IncompatibleRecurSIAAlgorithmException {
		Encoding encoding = new Encoding();
		if (pointSet.isEmpty()) System.out.println(">>>>>> pointSet argument is empty in recurSIA()<<<<<<<<");
		switch(algorithm) {
		case COSIATEC:
			encoding = new COSIATECEncoding(
					pointSet,
					null, //outputDirectoryPathString
					diatonicPitch,
					null, //inputFilePathString
					false, //mirex
					withCompactnessTrawler,a,b,
					forRSuperdiagonals,r,
					removeRedundantTranslators,
					minTECCompactness,
					minPatternSize,
					maxPatternSize,
					mergeTECs,
					minMatchSize,
					numIterations,
					false, //draw
					false, //segmentMode
					false, //bbMode
					null, //omnisiaOutputFilePathString
					topNPatterns,
					withoutChannel10,
					sortByPatternSize); 
			break;
		case SIATECCompress:
			SIATECCompress siatecCompress = new SIATECCompress();
			encoding = siatecCompress.encode(
					pointSet,
					withCompactnessTrawler,a,b,
					forRSuperdiagonals,r,
					minPatternSize,
					maxPatternSize,
					0, //minTranslatorSetSize
					removeRedundantTranslators);
			break;
		case Forth:
			encoding = new ForthEncoding(
					pointSet,
					null, //inputFilePathOrOutputFileName 
					null, //OUTPUT_DIR.getAbsolutePath(),
					(diatonicPitch?PitchRepresentation.MORPHETIC_PITCH:PitchRepresentation.CHROMATIC_PITCH),
					minPatternSize,
					maxPatternSize,
					crLow, 
					crHigh, 
					compVLow, 
					compVHigh,
					cMin,
					sigmaMin,
					forRSuperdiagonals,r,
					withCompactnessTrawler,a,b,
					bbCompactness,
					removeRedundantTranslators,
					false, //mirex
					false, //segmentMode
					false, //bbMode
					null, //omnisiaOutputFilePath
					topNPatterns,
					withoutChannel10, //Added 2/8/16 19:53
					fromOMNISIA
					);
			break;
		default: 
			throw new IncompatibleRecurSIAAlgorithmException(algorithm);			
		}
		if (ENCODING == null)
			ENCODING = encoding.copy();
		System.out.println("\n\n**********************\n\nInput point set:\n "+pointSet+"\n\nOutput encoding:\n" + encoding + "\n**********************\n\n");
		ArrayList<TEC> encodingTECs = encoding.getTECs();
		if (encodingTECs.size()==1 && encodingTECs.get(0).getTranslatorSetSize()==1)
			return encodingTECs;
		for(TEC encodingTEC : encodingTECs) {
			if (encodingTEC.getPattern().isEmpty()) System.out.println(">>>>> encodingTEC.getPattern() is empty in line 176 of recurSIA<<<<<<<<");
			ArrayList<TEC> patternTecs = recurSIA(encodingTEC.getPattern(),
					diatonicPitch,
					withCompactnessTrawler,
					a,
					b,
					forRSuperdiagonals,
					r,
					removeRedundantTranslators,
					minTECCompactness,
					minPatternSize,
					maxPatternSize,
					mergeTECs,
					minMatchSize,
					numIterations,
					topNPatterns,
					withoutChannel10,
					algorithm,
					crLow, crHigh, compVLow, compVHigh, cMin, sigmaMin, bbCompactness,
					fromOMNISIA,
					sortByPatternSize);
			if (patternTecs.size() > 1 || patternTecs.get(0).getTranslatorSetSize() > 1)
				encodingTEC.setPatternTecs(patternTecs);
		}
		return encodingTECs;
	}

	public static void main(String[] args) {
		String outputDirectoryPathString = "/Users/dave/Documents/Work/Research/neon/chromamorph/Points/output/2016-07-29";
		boolean isDiatonic = true;
		boolean withoutChannel10 = true;
		int topNPatterns = 0;
		boolean forMirex = false;
		boolean segmentMode = false;
		boolean bbMode = false;
		String omnisiaOutputFilePathString = null;
		boolean withCompactnessTrawler = false;
		double a = 0.67;
		int b = 3;
		boolean forRSuperdiagonals = false;
		int r = 1;
		boolean removeRedundantTranslators = true;
		double minTECCompactness = 0.5;
		int minPatternSize = 0;
		int maxPatternSize = 0;
		boolean mergeTECs = false;
		int minMatchSize = 0;
		int numIterations = 0;
		Algorithm algorithm = Algorithm.COSIATEC;
		double crLow = 0.2;
		double crHigh = 1.0;
		double compVLow = 0.2;
		double compVHigh = 1.0;
		int cMin = 15; 
		double sigmaMin =0.5;
		boolean bbCompactness = false;
		boolean fromOMNISIA = false;
		boolean sortByPatternSize = false;
		for (int n = 846; n < 870; n++) {
			String inputFilePathString = "/Users/dave/Documents/Work/Research/neon/chromamorph/Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv"+n+"b-done.opnd";
			try {
				new RecurSIAEncoding(
						inputFilePathString,
						outputDirectoryPathString,
						isDiatonic,
						withoutChannel10,
						topNPatterns,
						forMirex,
						segmentMode,
						bbMode,
						omnisiaOutputFilePathString,
						withCompactnessTrawler, a, b,
						forRSuperdiagonals, r,
						removeRedundantTranslators,
						minTECCompactness,
						minPatternSize,
						maxPatternSize,
						mergeTECs,
						minMatchSize,
						numIterations,
						algorithm,
						crLow, 
						crHigh, 
						compVLow, 
						compVHigh, 
						cMin, 
						sigmaMin, 
						bbCompactness,
						fromOMNISIA,
						sortByPatternSize
						);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (MissingTieStartNoteException e) {
				e.printStackTrace();
			} catch (IncompatibleRecurSIAAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}

}
