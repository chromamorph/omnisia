package com.chromamorph.maxtranpatsjava;

public class ICCCM2024 {
	public static void main(String[] args) {
		MaxTranPats.TRANSFORMATION_CLASSES = new TransformationClass[] {new F_2STR()};
		MaxTranPats.DIATONIC_PITCH = true;
		MaxTranPats.DIMENSION_MASK = "1100";
		MaxTranPats.DRAW = false;
		MaxTranPats.DRAW_BOUNDING_BOXES = true;
		String[] pieces = new String[] {
				"Ravel",
				"Bach",
				"Dowland"
		};
		String[] queryFilePaths = new String[] {
				"/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN-QUERY-1.OPND",
				"/Users/susanne/Repos/data/BachJS/DieKunstDerFuge/ContrapunctusVI/Bach-JS-ContrapunctusVI-subject.opnd",
				"/Users/susanne/Repos/data/lachrimae/fallingfourth.opnd"
		};
		String[] inputFilePaths = new String[] {
				"/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/RAVEL-MENUET-SUR-LE-NOM-D-HAYDN.OPND",
				"/Users/susanne/Repos/data/BachJS/DieKunstDerFuge/ContrapunctusVI/Lilypond/score-from-midi.opnd",
				"/Users/susanne/Repos/data/lachrimae/Dowland_flow_my_tears.OPND"
		};
		String outputDirRoot = "/Users/susanne/Repos/omnisia/MaxTranPatsJava/output/ICCCM2024/";
		String[] outputDirPaths = new String[] {
				outputDirRoot + "Ravel/DM",
				outputDirRoot + "ContrapunctusVI",
				outputDirRoot + "lachrimae"
		};
		String[] groundTruthFilePaths = new String[] {
				"/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/Ground-truth-files/RAVEL-HAYDN-GROUND-TRUTH-DM-one-os.gt",
				"/Users/susanne/Repos/data/BachJS/DieKunstDerFuge/ContrapunctusVI/Bach-JS-Contrapunctus-VI-Subject-entries.gt",
				"/Users/susanne/Repos/data/lachrimae/Dowland_flow_my_tears.gt"
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
			for (double MIN_OCC_COMPACTNESS : new double[] {0.0 /*, 0.5, 0.75, 1.0 */}) {
				int[] minPatternSizes = piece.equals("Ravel")?new int[] {3,4,5}:new int[] {11,12,13,14};
				for(int MIN_PATTERN_SIZE : minPatternSizes) {
					for(boolean MID_TIME_POINT : new boolean[] {false, true}) {
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
								MaxTranPats.DRAW_BOUNDING_BOXES
								);
						if (piece.equals("Ravel")) {
							String oldOutputDir = MaxTranPats.OUTPUT_DIR_PATH;
							MaxTranPats.OUTPUT_DIR_PATH = outputDirRoot + "/Ravel/Soucy";
							String oldGroundTruthFile = MaxTranPats.GROUND_TRUTH_FILE_PATH;
							MaxTranPats.GROUND_TRUTH_FILE_PATH = "/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/05-Ravel-Menuet-sur-le-nom-d-Haydn/Ground-truth-files/RAVEL-HAYDN-GROUND-TRUTH-Soucy-one-os.gt";
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
									MaxTranPats.DRAW_BOUNDING_BOXES
									);
							MaxTranPats.OUTPUT_DIR_PATH = oldOutputDir;
							MaxTranPats.GROUND_TRUTH_FILE_PATH = oldGroundTruthFile;
						}
					}
				}				
			}
		}
	}
}
