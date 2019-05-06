package com.chromamorph.points022;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import com.chromamorph.points022.segmentation01.StructuralSegment;
import com.chromamorph.points022.segmentation01.StructuralSegmentation;

import processing.core.PApplet;


public class DrawPoints extends PApplet {

	private static final long serialVersionUID = 1L;

	private float margin = 60;
	private int maxDataY;
	private long maxDataX;
	private float maxScreenX;
	private float maxScreenY;
	private int minDataY;
	private long minDataX;
	private float minScreenX;
	private float minScreenY;
	private ArrayList<TEC> tecs = null;
	private ArrayList<PatternPair> patternPairs = null;
	private ArrayList<PointSetCollectionPair> arrayListOfPointSetCollectionPairs = null;
	private ArrayList<PointSet> patterns = null;
	private ArrayList<ArrayList<PointSet>> occurrenceSets = null;
	private int tecIndex = 0;
	private int patternPairIndex = 0;
	private int pointSetCollectionPairIndex = 0;
	private int occurrenceSetIndex = 0;
	private TEC tec;
	private PatternPair patternPair;
	private PointSetCollectionPair pointSetCollectionPair;
	private PointSet points;
	public static int drawWindowHeight = 400;
	public static int drawWindowWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	public static int maxNumberOfIntervals = 20;
	private int pointWidth = 4;
	private int pointHeight = 4;
	private int lineWidth = 2;

	private PatternVectorSetPairList patternVectorSetPairs = null;
	private PatternVectorSetPair patternVectorSetPair = null;
	private int patternVectorSetPairIndex = 0;

	private boolean drawAllOccurrenceSetsAtOnce = false;
	private boolean diatonicPitch = false;
	private Long tatumsPerBar = null;
	private Long barOneStartsAt = 0l;
	private String title = "";
	private boolean printToPDF = false;
	private String outputPDFFilePath = null;
	private boolean segmentation = false;
	private int[][] colours;
	private String outputFilePath;

	private StructuralSegmentation structuralSegmentation = null;
	private boolean saveImageFile = false;
	private boolean writeToImageFile = false; //If true, only writes to image file without displaying analysis.


	public DrawPoints(PointSet points) {
		super();
		tecs = null;
		patternPairs = null;
		this.points = points;
	}

	public DrawPoints(PointSet points, boolean diatonicPitch) {
		super();
		tecs = null;
		patternPairs = null;
		this.points = points;
		this.diatonicPitch = diatonicPitch;
	}

	public DrawPoints(PointSet points, boolean diatonicPitch, boolean saveImageFile) {
		super();
		tecs = null;
		patternPairs = null;
		this.points = points;
		this.diatonicPitch = diatonicPitch;
		this.saveImageFile = saveImageFile;
	}

	public DrawPoints(PointSet points, String title, boolean diatonicPitch) {
		super();
		tecs = null;
		patternPairs = null;
		this.points = points;
		this.diatonicPitch = diatonicPitch;
		this.title = title;
	}

	public DrawPoints(PointSet points, Collection<TEC> tecs2) {
		super();
		if (tecs2 != null) {
			this.tecs = new ArrayList<TEC>(tecs2);
			if (tecIndex < tecs.size())
				tec = this.tecs.get(tecIndex);
		}
		this.points = points;
	}

	public DrawPoints(PointSet pointSet, ArrayList<PointSetCollectionPair> arrayListOfPointSetCollectionPairs) {
		super();
		this.arrayListOfPointSetCollectionPairs = arrayListOfPointSetCollectionPairs;
		if (arrayListOfPointSetCollectionPairs != null)
			pointSetCollectionPair = this.arrayListOfPointSetCollectionPairs.get(pointSetCollectionPairIndex);
		this.points = pointSet;
	}


	public DrawPoints(PointSet points, PatternPair[] patternPairs) {
		super();
		this.patternPairs = new ArrayList<PatternPair>();
		for(PatternPair patternPair : patternPairs)
			this.patternPairs.add(patternPair);
		patternPair = this.patternPairs.get(patternPairIndex);
		this.points = points;
	}

	public DrawPoints(PointSet points, TreeSet<PatternPair> patternPairs) {
		super();
		this.patternPairs = new ArrayList<PatternPair>(patternPairs);
		patternPair = this.patternPairs.get(patternPairIndex);
		this.points = points;
	}

	public DrawPoints(PointSet dataset, PatternVectorSetPairList patternVectorSetPairs) {
		super();
		this.patternVectorSetPairs = patternVectorSetPairs;
		this.patternVectorSetPair = this.patternVectorSetPairs.getPatternVectorSetPairs().get(patternVectorSetPairIndex);
		this.points = dataset;
	}

	public DrawPoints(ArrayList<PointSet> patterns, PointSet dataset) {
		super();
		this.patterns = patterns;
		this.points = dataset;
	}

	public DrawPoints(PointSet dataset, ArrayList<ArrayList<PointSet>> occurrenceSets, boolean drawAllOccurrenceSetsAtOnce) {
		super();
		this.points = dataset;
		this.occurrenceSets = occurrenceSets;
		this.drawAllOccurrenceSetsAtOnce = drawAllOccurrenceSetsAtOnce;
		this.occurrenceSetIndex = 0;
		//System.out.println(occurrenceSets.get(occurrenceSetIndex));
	}

	public DrawPoints(PointSet dataset, 
			ArrayList<ArrayList<PointSet>> occurrenceSets, 
			boolean drawAllOccurrenceSetsAtOnce,
			boolean diatonicPitch) {
		super();
		this.points = dataset;
		this.occurrenceSets = occurrenceSets;
		this.drawAllOccurrenceSetsAtOnce = drawAllOccurrenceSetsAtOnce;
		this.occurrenceSetIndex = 0;
		this.diatonicPitch = diatonicPitch;
		//System.out.println(occurrenceSets.get(occurrenceSetIndex));
	}

	public DrawPoints(PointSet dataset, 
			ArrayList<ArrayList<PointSet>> occurrenceSets, 
			boolean drawAllOccurrenceSetsAtOnce,
			boolean diatonicPitch,
			Long tatumsPerBar,
			Long barOneStartsAt,
			String title,
			boolean printToPDF,
			String pdfFilePath) {
		super();
		this.points = dataset;
		this.occurrenceSets = occurrenceSets;
		this.drawAllOccurrenceSetsAtOnce = drawAllOccurrenceSetsAtOnce;
		this.occurrenceSetIndex = 0;
		this.diatonicPitch = diatonicPitch;
		this.tatumsPerBar = tatumsPerBar;
		this.barOneStartsAt = barOneStartsAt;
		this.title = title;
		this.printToPDF = printToPDF;
		this.outputPDFFilePath = pdfFilePath;
		//System.out.println(occurrenceSets.get(occurrenceSetIndex));
	}

	public DrawPoints(PointSet dataset, 
			ArrayList<ArrayList<PointSet>> occurrenceSets, 
			boolean drawAllOccurrenceSetsAtOnce,
			boolean diatonicPitch,
			Long tatumsPerBar,
			Long barOneStartsAt,
			String title) {
		super();
		this.points = dataset;
		this.occurrenceSets = occurrenceSets;
		this.drawAllOccurrenceSetsAtOnce = drawAllOccurrenceSetsAtOnce;
		this.occurrenceSetIndex = 0;
		this.diatonicPitch = diatonicPitch;
		this.tatumsPerBar = tatumsPerBar;
		this.barOneStartsAt = barOneStartsAt;
		this.title = title;
		//System.out.println(occurrenceSets.get(occurrenceSetIndex));
	}

	public DrawPoints(PointSet dataset, 
			ArrayList<ArrayList<PointSet>> occurrenceSets, 
			boolean drawAllOccurrenceSetsAtOnce,
			boolean diatonicPitch,
			Long tatumsPerBar,
			Long barOneStartsAt,
			String title,
			String outputFilePath,
			boolean segmentation) {
		super();
		this.points = dataset;
		this.occurrenceSets = occurrenceSets;
		this.drawAllOccurrenceSetsAtOnce = drawAllOccurrenceSetsAtOnce;
		this.occurrenceSetIndex = 0;
		this.diatonicPitch = diatonicPitch;
		this.tatumsPerBar = tatumsPerBar;
		this.barOneStartsAt = barOneStartsAt;
		this.title = title;
		this.segmentation = segmentation;
		this.outputFilePath = outputFilePath;
		this.saveImageFile = true;
		//System.out.println(occurrenceSets.get(occurrenceSetIndex));
	}

	public DrawPoints(PointSet dataset, 
			ArrayList<ArrayList<PointSet>> occurrenceSets, 
			boolean drawAllOccurrenceSetsAtOnce,
			boolean diatonicPitch,
			Long tatumsPerBar,
			Long barOneStartsAt,
			String title,
			String outputFilePath,
			boolean segmentation,
			boolean writeToImageFile) {
		super();
		this.points = dataset;
		this.occurrenceSets = occurrenceSets;
		this.drawAllOccurrenceSetsAtOnce = drawAllOccurrenceSetsAtOnce;
		this.occurrenceSetIndex = 0;
		this.diatonicPitch = diatonicPitch;
		this.tatumsPerBar = tatumsPerBar;
		this.barOneStartsAt = barOneStartsAt==null?0:barOneStartsAt;
		this.title = title;
		this.segmentation = segmentation;
		this.outputFilePath = outputFilePath;
		this.writeToImageFile = writeToImageFile;
		//System.out.println(occurrenceSets.get(occurrenceSetIndex));
	}

	public DrawPoints(PointSet dataset, 
			ArrayList<TEC> tecs, 
			boolean diatonicPitch,
			Long tatumsPerBar,
			Long barOneStartsAt,
			String title,
			String outputFilePath,
			boolean segmentation,
			boolean writeToImageFile) {
		super();
		this.points = dataset;
//		this.occurrenceSets = occurrenceSets;
		this.drawAllOccurrenceSetsAtOnce = true;
		this.occurrenceSetIndex = 0;
		this.diatonicPitch = diatonicPitch;
		this.tatumsPerBar = tatumsPerBar;
		this.barOneStartsAt = barOneStartsAt==null?0:barOneStartsAt;
		this.title = title;
		this.segmentation = segmentation;
		this.outputFilePath = outputFilePath;
		this.writeToImageFile = writeToImageFile;
		this.tecs = tecs;
		//System.out.println(occurrenceSets.get(occurrenceSetIndex));
	}

	public DrawPoints(PointSet dataset, ArrayList<ArrayList<PointSet>> occurrenceSets, boolean drawAllOccurrenceSetsAtOnce, String infoString) {
		this(dataset, occurrenceSets, drawAllOccurrenceSetsAtOnce);
	}

	public DrawPoints(PointSet pointSet, StructuralSegmentation thisSegmentation, boolean diatonicPitch) {
		super();
		this.points = pointSet;
		this.diatonicPitch = diatonicPitch;
		this.structuralSegmentation = thisSegmentation;
	}

	private void drawStructuralSegmentation() {
		textAlign(LEFT,TOP);
		for(StructuralSegment s : structuralSegmentation.getSegments()) {
			Point bottomPoint = new Point(s.getStart(),0);
			Point topPoint = new Point(s.getStart(),points.maxY);
			drawLine(bottomPoint,topPoint,color(255,0,0),2,0);
			float dataX = map(s.getStart(),minDataX,maxDataX,minScreenX,maxScreenX);
			text(s.getLetterLabel(),dataX+2,minScreenY);
		}
	}

	public void draw() {
		background(255);

		drawAxes();

		for(Point n : points.getPoints()) {
			drawPoint(n);
		}

		if (structuralSegmentation != null) {
			drawStructuralSegmentation();
		} else if (segmentation) {
			drawSegmentation();
			this.noLoop();
		}
		else if (drawAllOccurrenceSetsAtOnce) {
			if (occurrenceSets != null && !occurrenceSets.isEmpty()) {
				drawAllOccurrenceSetsAtOnce();
			}
			noLoop();
		}
		else if (occurrenceSets != null)
			drawOccurrenceSet();
		else if (tecs != null && !tecs.isEmpty())
			drawTEC();
		else if (patternPairs != null) {
			drawPatternPair();
		}
		else if (pointSetCollectionPair != null)
			drawPointSetCollectionPair();
		else if (patternVectorSetPair != null)
			drawPatternVectorSetPair();
		else if (patterns != null)
			drawPatterns();
		String outputImageFile = "outputFile.png";
		if (outputFilePath != null) {
			int folderEnd = outputFilePath.lastIndexOf("/")+1;
			String fileNameWithoutSuffix = outputFilePath.substring(folderEnd,outputFilePath.lastIndexOf("."));
			outputImageFile = outputFilePath.substring(0, folderEnd) + fileNameWithoutSuffix + ".png";
			System.out.println("\nOutput image file: "+outputImageFile);
		}			
		if (saveImageFile) {
			this.save(outputImageFile);
			noLoop();
			//exit();
		}
		if (writeToImageFile) {
			this.save(outputImageFile);
			noLoop();
			//exit();
		}
	}

	public void keyPressed() {
		if (occurrenceSets != null && !drawAllOccurrenceSetsAtOnce){
			occurrenceSetIndex++;
			System.out.println(occurrenceSets.get(occurrenceSetIndex));
			if (occurrenceSetIndex > occurrenceSets.size()-1) {
				System.out.println("Reached end of occurrenceSets!!!!! Starting again...");
				occurrenceSetIndex = 0;
			}
		}else if (tecs != null) {
			if (tecIndex < tecs.size()-1) {
				tecIndex++;
				tec = tecs.get(tecIndex);
				System.out.println(String.format("%.2f", tec.getCompactness())+": "+String.format("%.2f",tec.getCompressionRatio())+": "+tec);
			} else {
				System.out.println("Reached end of TECs!!!!! Starting again...");
				tecIndex = 0;
				tec = tecs.get(tecIndex);
				System.out.println(String.format("%.2f", tec.getCompactness())+": "+String.format("%.2f",tec.getCompressionRatio())+": "+tec);
			}
		} else if (patternPairs != null) {
			if (patternPairIndex < patternPairs.size()-1) {
				patternPairIndex++;
				patternPair = patternPairs.get(patternPairIndex);
				System.out.println(patternPair);
			} else {
				System.out.println("Reached end of PatternPairs!!!!! Starting again...");
				patternPairIndex = 0;
				patternPair = patternPairs.get(patternPairIndex);
				System.out.println(patternPair);
			}
		} else if (arrayListOfPointSetCollectionPairs != null) {
			if (pointSetCollectionPairIndex < arrayListOfPointSetCollectionPairs.size()-1) {
				pointSetCollectionPairIndex++;
			} else {
				System.out.println("Reached end of PatternPairs!!!!! Starting again...");
				pointSetCollectionPairIndex = 0;
			}
			pointSetCollectionPair = arrayListOfPointSetCollectionPairs.get(pointSetCollectionPairIndex);
			System.out.println(pointSetCollectionPair.pointSetCollection1);
			System.out.println(pointSetCollectionPair.pointSetCollection2);
			System.out.println();
		} else if (patternVectorSetPairs != null) {
			if (patternVectorSetPairIndex < patternVectorSetPairs.size()-1) {
				patternVectorSetPairIndex++;
			} else {
				System.out.println("Reached end of PatternVectorSetPairs!!!!! Starting again...");
				patternVectorSetPairIndex = 0;
			}
			patternVectorSetPair = patternVectorSetPairs.get(patternVectorSetPairIndex);
			System.out.println(patternVectorSetPair);
		}
	}

	private void drawSegmentation() {
		makeColourArray();
		int numberOfTecs = occurrenceSets.size();
		for(int i = 0; i < numberOfTecs; i++){
			if (i==numberOfTecs-1 && occurrenceSets.get(i).size()==1)
				break;
			ArrayList<PointSet> occurrenceSet = occurrenceSets.get(i);
			for(PointSet occurrence : occurrenceSet) {
				Point topLeft = new Point(occurrence.getTopLeft().getX(),points.maxY);
				Point bottomRight = new Point(occurrence.getBottomRight().getX(),points.minY);
				//				Calculate transparency based on segment compactness - more compact = more opaque
				float opacity = 100f *occurrence.getSegmentCompactness(points);
				drawFilledRectangle(color(colours[i][0],colours[i][1],colours[i][2]),topLeft,bottomRight,opacity);
			}

		}
	}

	private void makeColourArray() {
		int numberOfTecs = occurrenceSets.size();			
		colours = new int[numberOfTecs][4];
		for(int j = 0, K = -1; j < numberOfTecs; j++) {
			int r = 0, g = 0, b = 0;
			if (j%3==0) K++;
			int brightness = (int)(255 - K*255/(numberOfTecs/4.0));
			switch(j%6) {
			case 0 : r = brightness; g = 0; b = 0; break;
			case 1 : r = 0; g = brightness; b = 0; break;
			case 2 : r = 0; g = 0; b = brightness; break;
			case 3 : r = 0; g = brightness; b = brightness; break;
			case 4 : r = brightness; g = 0; b = brightness; break;
			case 5 : r = brightness; g = brightness; b = 0; break;
			default: System.out.println("ERROR: illegal value of j%3"); break;
			}
			int alpha = 255 - (int)(1.0*j*255/(numberOfTecs+5));
			colours[j][0] = r;
			colours[j][1] = g;
			colours[j][2] = b;
			colours[j][3] = alpha;
		}
	}

	private void drawRecursiveTecs() {
		makeColourArray();
	}
	
	private void drawAllOccurrenceSetsAtOnce() {
		if (occurrenceSets == null)
			drawRecursiveTecs();
		int numberOfTecs = occurrenceSets.size();
		makeColourArray();

		System.out.println("EXECUTING drawAllOccurrenceSetsAtOnce");
		//Figure out colours for different TECs - should be maximally different
		//		int k = (int)Math.ceil(Math.cbrt(numberOfTecs)* 1.0);
		//		int[] colours = new int[numberOfTecs];
		//		for(int i = 0, r = 0; r < k; r++) {
		//			int red = (int)((1.0*r*200)/k);
		//			for(int g = 0; g < k; g++) {
		//				int green = (int)((1.0*g*200)/k);
		//				for(int b = 0; b < k; b++) {
		//					int blue = (int)((1.0*b*200)/k);
		//					colours[i] = color(red,green,blue);
		//					i++;
		//					if (i == occurrenceSets.size())
		//						break;
		//				}
		//				if (i == occurrenceSets.size())
		//					break;
		//			}
		//			if (i == occurrenceSets.size())
		//				break;
		//		}


		for(int i = numberOfTecs-1; i >= 0; i--) {
			if (i==numberOfTecs-1 && occurrenceSets.get(i).size()==1)
				continue;
			ArrayList<PointSet> occurrenceSet = occurrenceSets.get(i);
			int col = color(colours[i][0],colours[i][1],colours[i][2],colours[i][3]);
			fill(col);
			stroke(col);
			strokeWeight(2);
			for(PointSet occurrence : occurrenceSet) {
				Point lastPoint = occurrence.first();
				for(Point p : occurrence.getPoints()) {
					drawPoint(p,col,col,1,ROUND,pointWidth+1,pointHeight+1);
					drawLine(lastPoint,p,col,lineWidth,ROUND);
					//					drawPoint(p,0,0,1,ROUND,pointWidth+1,pointHeight+1);
					//					drawLine(lastPoint,p,0,lineWidth,ROUND);
					lastPoint = p;
				}
				//				Point topLeft = occurrence.getTopLeft();
				//				Point bottomRight = occurrence.getBottomRight();
				//				drawFilledRectangle(col,topLeft,bottomRight);
			}
			if ((numberOfTecs-i)%500==0)
				System.out.print(".");
			if ((numberOfTecs-i)%5000==0)
				System.out.println();
		}
	}

	private void drawOccurrenceSet() {
		ArrayList<PointSet> occurrenceSet = occurrenceSets.get(occurrenceSetIndex);
		int col = color(255,0,0);
		fill(col);
		stroke(col);
		strokeWeight(1);
		for(int i = 0; i < occurrenceSet.size(); i++) {
			TreeSet<Point> occurrence = occurrenceSet.get(i).getPoints();
			for(Point p : occurrence)
				drawPoint(p,col,col,1,ROUND,2,2);
			Point topLeft = tec.getPattern().getTopLeft();
			Point bottomRight = tec.getPattern().getBottomRight();
			drawRectangle(topLeft,bottomRight);
		}
	}

	private void drawTEC() {
		int col = color(255,0,0);
		if (tec.isDual())
			col = color(0,0,255);
		fill(col);
		stroke(col);
		strokeWeight(1);
		if (tec == null) System.out.println("tec is null: "+tec);
		TreeSet<Point> tecPatternPoints = tec.getPattern().getPoints();
		if (tecPatternPoints == null)
			System.out.println("TEC pattern is null: "+tecPatternPoints);
		for(Point p : tecPatternPoints)
			drawPoint(p,col,col,1,ROUND,2,2);
		Point topLeft = tec.getPattern().getTopLeft();
		Point bottomRight = tec.getPattern().getBottomRight();
		drawRectangle(topLeft,bottomRight);
		for(Vector v : tec.getTranslators().getVectors()) {
			for(Point p : tec.getPattern().getPoints()) {
				drawPoint(p.translate(v),col,col,1f,ROUND,2,2);
			}
			Point tl = topLeft.translate(v), br = bottomRight.translate(v); 
			drawRectangle(tl,br);
		}
	}

	private void drawPatterns() {
		int col = color(255,0,0);
		fill(col);
		stroke(col);
		strokeWeight(1);
		if (patterns == null) System.out.println("patterns==null: no patterns to draw!");
		for(PointSet pattern : patterns) {
			TreeSet<Point> patternPoints = pattern.getPoints();
			for(Point p : patternPoints)
				drawPoint(p,col,col,1,ROUND,2,2);
			Point topLeft = pattern.getTopLeft();
			Point bottomRight = pattern.getBottomRight();
			drawRectangle(topLeft,bottomRight);
		}
	}


	/**
	 * This is used to draw a pair of point set collections.
	 * For example, it is used to view the output of COSIATEC on the JKU PDD.
	 * 
	 * The variable pointSetCollectionPair contains a PointSetCollectionPair object
	 * which contains a pair of Collections of PointSets. This method must draw
	 * both of these point set collections, one collection in one colour and the
	 * other collection in a different colour.
	 */
	private void drawPointSetCollectionPair() {
		int col1 = color(255,0,0); //Colour for point set collection 1
		int col2 = color(0,0,255); //Colour for point set collection 2

		//Let's draw the first collection of patterns first
		if (pointSetCollectionPair.pointSetCollection1 == null)
			System.out.println("Point set collection 1 is null!!!!");
		for(PointSet pointSet1 : pointSetCollectionPair.pointSetCollection1) {
			for(Point p : pointSet1.getPoints()) {
				drawPoint(p,col1,col1,1,ROUND,3,3);
			}
			drawRectangle(col1,pointSet1.getTopLeft(),pointSet1.getBottomRight());
		}

		//Now draw second collection of patterns
		if (pointSetCollectionPair.pointSetCollection2 == null)
			System.out.println("Point set collection 2 is null!!!!");
		for(PointSet pointSet2 : pointSetCollectionPair.pointSetCollection2) {
			for(Point p : pointSet2.getPoints()) {
				drawPoint(p,col2,col2,1,ROUND,1,1);
			}
			drawRectangle(col2,pointSet2.getTopLeft(),pointSet2.getBottomRight());
		}

	}

	private void drawPatternPair() {
		//		System.out.println("Got here"+patternPair);
		int col1 = color(255,0,0);
		int col2 = color(0,0,255);
		fill(col1);
		stroke(col1);
		strokeWeight(1);
		TreeSet<Point> pattern1 = patternPair.getPattern1().getPoints();
		for(Point p : pattern1)
			drawPoint(p,col1,col1,1,ROUND,6,6);
		Point topLeft = patternPair.getPattern1().getTopLeft();
		Point bottomRight = patternPair.getPattern1().getBottomRight();
		drawRectangle(topLeft,bottomRight);

		//Now draw pattern2
		fill(col2);
		stroke(col2);
		strokeWeight(1);
		TreeSet<Point> pattern2 = patternPair.getPattern2().getPoints();
		for(Point p : pattern2)
			drawPoint(p,col2,col2,1,ROUND,4,4);
		topLeft = patternPair.getPattern2().getTopLeft();
		bottomRight = patternPair.getPattern2().getBottomRight();
		drawRectangle(topLeft,bottomRight);
	}

	private void drawPatternVectorSetPair() {
		int patternColor = color(255,0,0);
		int occurrenceColor = color(0,0,255);

		//Draw pattern
		drawPatternWithBoundingBox(patternVectorSetPair.getMtp(), patternColor, 1);


		//Draw occurrences
		for(Vector v : patternVectorSetPair.getVectorSet().getVectors())
			drawPatternWithBoundingBox(patternVectorSetPair.getMtp().translate(v), occurrenceColor, 1);
	}

	private void drawPatternWithBoundingBox(PointSet pattern, int color, int pointSize) {
		fill(color);
		stroke(color);
		strokeWeight(pointSize);
		for(Point point : pattern.getPoints()) {
			drawPoint(point,color,color,pointSize,ROUND,pointSize,pointSize);
		}
		Point topLeft = pattern.getTopLeft();
		Point bottomRight = pattern.getBottomRight();
		drawRectangle(topLeft,bottomRight);		
	}

	private void drawRectangle(Point topLeft, Point bottomRight) {
		fill(0,0,0,0);
		float leftX = map(topLeft.getX(),minDataX,maxDataX,minScreenX,maxScreenX);//topLeft.getX()*scale+minScreenX; 
		float rightX = map(bottomRight.getX(),minDataX,maxDataX,minScreenX,maxScreenX);//bottomRight.getX()*scale+minScreenX;
		float topY = map(topLeft.getY(),minDataY,maxDataY,maxScreenY,minScreenY);//maxScreenY-topLeft.getY()*scale;
		float bottomY = map(bottomRight.getY(),minDataY,maxDataY,maxScreenY,minScreenY);//maxScreenY-bottomRight.getY()*scale;
		line(leftX,topY,rightX,topY);
		line(leftX,topY,leftX,bottomY);
		line(rightX,topY,rightX,bottomY);
		line(rightX,bottomY,leftX,bottomY);
	}

	private void drawRectangle(int colour, Point topLeft, Point bottomRight) {
		stroke(colour);
		drawRectangle(topLeft,bottomRight);
	}

	//	private void drawFilledRectangle(int colour, Point topLeft, Point bottomRight) {
	//		drawFilledRectangle(colour, topLeft, bottomRight, 20f);
	//	}

	private void drawFilledRectangle(int colour, Point topLeft, Point bottomRight, float transparency) {
		stroke(colour,transparency);
		fill(colour,transparency);
		float leftX = map(topLeft.getX(),minDataX,maxDataX,minScreenX,maxScreenX);//topLeft.getX()*scale+minScreenX; 
		float rightX = map(bottomRight.getX(),minDataX,maxDataX,minScreenX,maxScreenX);//bottomRight.getX()*scale+minScreenX;
		float topY = map(topLeft.getY(),minDataY,maxDataY,maxScreenY,minScreenY);//maxScreenY-topLeft.getY()*scale;
		float bottomY = map(bottomRight.getY(),minDataY,maxDataY,maxScreenY,minScreenY);//maxScreenY-bottomRight.getY()*scale;
		rect(leftX,topY,rightX-leftX,bottomY-topY);
		//		line(leftX,topY,rightX,topY);
		//		line(leftX,topY,leftX,bottomY);
		//		line(rightX,topY,rightX,bottomY);
		//		line(rightX,bottomY,leftX,bottomY);
	}

	private Point getScreenPoint(long l, int dataY) {
		int screenX = (int)map(l,minDataX,maxDataX,minScreenX,maxScreenX);
		int screenY = (int)map(dataY,minDataY,maxDataY,maxScreenY,minScreenY);
		return new Point(screenX,screenY);
	}

	private void drawAxes() {

		stroke(0);
		fill(0);
		strokeWeight(1);
		strokeCap(SQUARE);
		float zeroX = map(0,minDataX,maxDataX,minScreenX,maxScreenX);
		float zeroY = map(0,minDataY,maxDataY,maxScreenY,minScreenY);
		line(minScreenX,zeroY,maxScreenX,zeroY);
		line(zeroX,maxScreenY,zeroX,minScreenY);
		textAlign(RIGHT,CENTER);
		int dataYSep = maxDataY>20?10:1;
		if (diatonicPitch == false) dataYSep = 12;
		if (diatonicPitch == true) dataYSep = 7;

		for(float dataY = minDataY;dataY <= maxDataY; dataY += dataYSep) {
			stroke(0);
			float screenY = map(dataY,minDataY,maxDataY,maxScreenY,minScreenY);
			text(((int)dataY),zeroX-10,screenY);
			line(zeroX-10,screenY,zeroX,screenY);
			stroke(200);
			line(zeroX,screenY,maxScreenX,screenY);
		}
		textAlign(CENTER,TOP);
		long dataXSep = maxDataX/maxNumberOfIntervals;
		if (dataXSep == 0l) dataXSep = 1l;
		if (tatumsPerBar != null) dataXSep = tatumsPerBar;
		//		System.out.println("Got here in drawAxes method: minDataX="+minDataX+", maxDataX="+maxDataX+", dataXSep="+dataXSep);
		int i = 0, k = 1, pixelsPerBar;
		if (tatumsPerBar != null) {
			int minLabelSep = 40;
			pixelsPerBar = (int)(maxScreenX*tatumsPerBar/maxDataX);
			k = (int)(1+ minLabelSep/pixelsPerBar);
		}
		if (barOneStartsAt == null)
			System.out.println("barOneStartsAt is null");
		for(float dataX = minDataX+barOneStartsAt; dataX <= maxDataX; dataX += dataXSep) {
			stroke(0);
			float screenX = map(dataX,minDataX,maxDataX,minScreenX,maxScreenX);
			String xTickMarkLabel = String.format("%.0f",tatumsPerBar==null?dataX:(dataX-barOneStartsAt)/tatumsPerBar);
			if (i%k == 0) { 
				text(xTickMarkLabel,screenX,zeroY+10);
				if (k != 1) strokeWeight(1.5f);
				line(screenX,zeroY,screenX,zeroY+10);
				strokeWeight(1);
			} else
				line(screenX,zeroY,screenX,zeroY+10);
			stroke(200);
			line(screenX,zeroY,screenX,minScreenY);
			i++;
		}
		//		System.out.println("Got here in drawAxes method - after loop");
		String pitchString = (diatonicPitch==true?"morphetic pitch":"chromatic pitch");
		pushMatrix();
		translate(zeroX-40,minScreenY+(maxScreenY-minScreenY)/2);
		rotate(-PI/2);
		textAlign(CENTER,CENTER);
		text(pitchString,0,0);
		popMatrix();
		pushMatrix();
		textAlign(CENTER,CENTER);
		String timeString = tatumsPerBar==null?"time/tatums":"time/bars";
		translate(minScreenX+(maxScreenX-minScreenX)/2,zeroY+35);
		text(timeString,0,0);
		popMatrix();
		//		Draw axis lines
		stroke(0);
		line(minScreenX,zeroY,maxScreenX,zeroY);
		line(zeroX,maxScreenY,zeroX,minScreenY);
		textAlign(CENTER,CENTER);
		text(title,minScreenX+(maxScreenX-minScreenX)/2,minScreenY-30);
	}

	private void drawPoint(Point n, int stroke, int fill, float strokeWeight, int strokeCap, float width, float height) {
		stroke(stroke);
		fill(fill);
		strokeWeight(strokeWeight);
		strokeCap(strokeCap);
		Point p = getScreenPoint(n.getX(), n.getY());
		float x = p.getX();
		float y = p.getY();
		ellipse(x,y,width,height);
	}

	private void drawLine(Point n1, Point n2, int stroke, float strokeWeight, int strokeCap) {
		stroke(stroke);
		strokeWeight(strokeWeight);
		strokeCap(strokeCap);
		Point p1 = getScreenPoint(n1.getX(), n1.getY());
		Point p2 = getScreenPoint(n2.getX(), n2.getY());
		float x1 = p1.getX();
		float y1 = p1.getY();
		float x2 = p2.getX();
		float y2 = p2.getY();
		line(x1,y1,x2,y2);
	}

	private void drawPoint(Point n) {
		drawPoint(n,color(0,0,0),color(0,0,0),2f,ROUND,pointWidth-2,pointHeight-2);
	}

	public void setup() {
		if (points.isEmpty()) exit();
		minScreenX = margin;
		maxScreenX = drawWindowWidth - margin;
		minScreenY = margin;
		maxScreenY = drawWindowHeight - margin;
		maxDataY = max(points.getMaxY(),1);
		maxDataX = (long) max(points.getMaxX(),1);
		minDataY = min(points.getMinY(),0);
		minDataX = (long) min(points.getMinX(),0);

		if (printToPDF)
			size(drawWindowWidth,drawWindowHeight,PDF,outputPDFFilePath);
		else 
			size(drawWindowWidth,drawWindowHeight);
		smooth();
		background(255);
		//		font = createFont("Arial", 14);
		textFont(createFont("Arial", 14));
		if (tecs != null && tecs.size() > 0) 
			System.out.println(String.format("%.2f", tec.getCompactness())+": "+String.format("%.2f",tec.getCompressionRatio())+": "+tec);
		else if (patternPairs != null) {
			System.out.println(patternPair);
			System.out.println("Printing pattern pairs");
			System.out.println(patternPair);
		} else if (arrayListOfPointSetCollectionPairs != null)
			System.out.println("Drawing point set collection pairs");
	}

}

