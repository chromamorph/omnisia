package com.chromamorph.maxtranpatsjava;

public class BarberSonataAnalysis {
	public static void main(String[] args) {
		MaxTranPats.TRANSFORMATION_CLASSES = new TransformationClass[] {new F_2STR()};
		MaxTranPats.DIATONIC_PITCH = false;
		MaxTranPats.DIMENSION_MASK = "1100";
		MaxTranPats.DRAW = true;
		MaxTranPats.DRAW_BOUNDING_BOXES = true;
		String[] pieces = new String[] {
				"BarberSonataMvt3"
		};
		String[] queryFilePaths = new String[] {
//				"/Users/susanne/Daves Docs/Work/Research/Data/Barber/Barber Piano Sonata/BarberPianoSonataMvt3-query-1.opnd",
				"/Users/susanne/Daves Docs/Work/Research/Data/Barber/Barber Piano Sonata/BarberPianoSonataMvt3-query-2.opnd"
				};
		String[] inputFilePaths = new String[] {
				"/Users/susanne/Daves Docs/Work/Research/Data/Barber/Barber Piano Sonata/BarberPianoSonataMvt3.mid"
		};
		String outputDirRoot = "/Users/susanne/Daves Docs/Work/Research/Data/Barber/Barber Piano Sonata/output/";
		String[] outputDirPaths = new String[] {
				outputDirRoot + "BarberSonataMvt3"
		};
		String[] groundTruthFilePaths = new String[] {
				null
		};
		for(int i = 0; i < pieces.length; i++) {
			String piece = pieces[i];
//			MaxTranPats.QUERY_FILE_PATH = piece.equals("Ravel")?"/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN-QUERY-1.OPND":"/Users/susanne/Repos/data/BachJS/DieKunstDerFuge/ContrapunctusVI/Bach-JS-ContrapunctusVI-subject.opnd";
//			MaxTranPats.INPUT_FILE_PATH = piece.equals("Ravel")?"/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN.OPND":"/Users/susanne/Repos/data/BachJS/DieKunstDerFuge/ContrapunctusVI/Lilypond/score-from-midi.opnd";
//			MaxTranPats.OUTPUT_DIR_PATH = piece.equals("Ravel")?"/Users/susanne/Repos/omnisia/MaxTranPatsJava/output/MML2024/Ravel/DM":"/Users/susanne/Repos/omnisia/MaxTranPatsJava/output/MML2024/ContrapunctusVI";
//			MaxTranPats.GROUND_TRUTH_FILE_PATH = piece.equals("Ravel")?"/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/Ground-truth-files/RAVEL-HAYDN-GROUND-TRUTH-DM-one-os.gt":"/Users/susanne/Repos/data/BachJS/DieKunstDerFuge/ContrapunctusVI/Bach-JS-Contrapunctus-VI-Subject-entries.gt";

			MaxTranPats.QUERY_FILE_PATH = queryFilePaths[i];
			MaxTranPats.INPUT_FILE_PATH = inputFilePaths[i];
			MaxTranPats.OUTPUT_DIR_PATH = outputDirPaths[i];
			MaxTranPats.GROUND_TRUTH_FILE_PATH = groundTruthFilePaths[i];
			
			MaxTranPats.MIN_COMPACTNESS = 0.0;
			for (double MIN_OCC_COMPACTNESS : new double[] {0.5}) {
				int[] minPatternSizes = new int[] {8};
				for(int MIN_PATTERN_SIZE : minPatternSizes) {
					for(boolean MID_TIME_POINT : new boolean[] {false}) {
						MaxTranPats.MID_TIME_POINT = MID_TIME_POINT;
						MaxTranPats.MIN_PATTERN_SIZE = MIN_PATTERN_SIZE;
						MaxTranPats.MIN_OCC_COMPACTNESS = MIN_OCC_COMPACTNESS;
						System.out.println(MaxTranPats.getParameterSettings());
						PointSet.maximalTransformedMatchesFromFiles(
								MaxTranPats.QUERY_FILE_PATH,
								MaxTranPats.INPUT_FILE_PATH,
								MaxTranPats.TRANSFORMATION_CLASSES,
								MaxTranPats.DIATONIC_PITCH, //pitchSpell
								MaxTranPats.MID_TIME_POINT, //midTimePoint
								MaxTranPats.DIMENSION_MASK, //dimensionMask
								MaxTranPats.OUTPUT_DIR_PATH, //outputDir
								MaxTranPats.MIN_PATTERN_SIZE, //minSize
								MaxTranPats.DRAW, //draw
								MaxTranPats.MIN_COMPACTNESS,
								MaxTranPats.MIN_OCC_COMPACTNESS,
								MaxTranPats.GROUND_TRUTH_FILE_PATH,
								MaxTranPats.DRAW_BOUNDING_BOXES,
								MaxTranPats.CHROMA,
								MaxTranPats.MORPH
								);
					}
				}				
			}
		}
	}
}
