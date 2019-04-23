package com.chromamorph.points022;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class TabulateDataFromCOSIATECRun {
	
	private static class CPV {
		private int c, p, v;
		
		public CPV(int c, int p, int v) {
			setC(c);
			setP(p);
			setV(v);
		}
		
		public int getC() {
			return c;
		}
		
		public int getP() {
			return p;
		}
		
		public int getV() {
			return v;
		}
		
		public void setC(int c) {
			this.c = c;
		}
		
		public void setP(int p) {
			this.p = p;
		}
		
		public void setV(int v) {
			this.v = v;
		}
	}
	
	private static class TecCRComp {
		private double compressionRatio, compactness;
		
		public TecCRComp(double compressionRatio, double compactness) {
			setCompressionRatio(compressionRatio);
			setCompactness(compactness);
		}
		
		public double getCompactness() {
			return compactness;
		}
		
		public double getCompressionRatio() {
			return compressionRatio;
		}
		
		public void setCompactness(double compactness) {
			this.compactness = compactness;
		}
		
		public void setCompressionRatio(double compressionRatio) {
			this.compressionRatio = compressionRatio;
		}
	}
	
	private static class NumPropResPts {
		private int num;
		private double prop;
		
		public NumPropResPts(int num, double prop) {
			setNum(num);
			setProp(prop);
		}
		
		public void setNum(int num) {
			this.num = num;
		}
		
		public void setProp(double prop) {
			this.prop = prop;
		}
		
		public int getNum() {
			return num;
		}
		
		public double getProp() {
			return prop;
		}
		
	}
	
	private ArrayList<String> fileNames = new ArrayList<String>();
	private ArrayList<Integer> datasetSizes = new ArrayList<Integer>();
	private ArrayList<ArrayList<Integer>> mtpMultiplicityData = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer> numberOfEncodingTECsList = new ArrayList<Integer>();
	private ArrayList<Integer> encodingLengthList = new ArrayList<Integer>();
	private ArrayList<Integer> encodingLengthWithoutResPtsList = new ArrayList<Integer>();
	private ArrayList<Long> runningTimes = new ArrayList<Long>();
	private ArrayList<Double> compressionRatios = new ArrayList<Double>();
	private ArrayList<Double> compressionRatiosWithoutResPts = new ArrayList<Double>();
	private ArrayList<NumPropResPts> numPropResPtsList = new ArrayList<NumPropResPts>();
	private ArrayList<ArrayList<CPV>> tecCpvData = new ArrayList<ArrayList<CPV>>();
	private ArrayList<ArrayList<TecCRComp>> tecCompressionCompactnessData = new ArrayList<ArrayList<TecCRComp>>();

	private ArrayList<String> textFileLines = null;

	public static void main(String[] args) throws FileNotFoundException {
		String dataDir = "/Users/dave/Documents/Work/Research/workspace/Points/output/points018/COSIATECOnRandomPointSets";
		TabulateDataFromCOSIATECRun data = new TabulateDataFromCOSIATECRun(dataDir);
		System.out.println(data);
		PrintStream ps = new PrintStream(dataDir+"/dataTable.csv");
		ps.println(data.toString());
		ps.close();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("File name,#Points,#MTPs (1st iter.),#Encoding TECs,Encoding length,Encoding length w/o res. pts.,Running time (ms),Compression ratio,Compression ratio w/o res. pts.,#Residual points,% Residual points,1st TEC coverage,%1st TEC coverage,1st TEC pattern size,1st TEC #occurrences,1st TEC CR,1st TEC compactness\n");
		int n = fileNames.size();
		for(int i = 0; i < n; i++) {
			System.out.println(i+" "+fileNames.get(i));
			sb.append(fileNames.get(i)+",");
			sb.append(datasetSizes.get(i)+",");
			sb.append(mtpMultiplicityData.get(i).get(0)+",");
			sb.append(numberOfEncodingTECsList.get(i)+",");
			sb.append(encodingLengthList.get(i)+",");
			sb.append(encodingLengthWithoutResPtsList.get(i)+",");
			sb.append(runningTimes.get(i)+",");
			sb.append(compressionRatios.get(i)+",");
			sb.append(compressionRatiosWithoutResPts.get(i)+",");
				System.out.println(numPropResPtsList.get(i));
			sb.append(numPropResPtsList.get(i).getNum()+",");
			sb.append(numPropResPtsList.get(i).getProp()+",");
			sb.append(tecCpvData.get(i).get(0).getC()+",");
			double percentCoverage = (tecCpvData.get(i).get(0).getC()*1.0)/datasetSizes.get(i); 
			sb.append(percentCoverage+",");
			sb.append(tecCpvData.get(i).get(0).getP()+",");
			sb.append(tecCpvData.get(i).get(0).getV()+",");
			sb.append(tecCompressionCompactnessData.get(i).get(0).getCompressionRatio()+",");
			sb.append(tecCompressionCompactnessData.get(i).get(0).getCompactness()+"\n");
		}
		//Now add TEC data for each file
		for(int i = 0; i < n; i++) {
			sb.append("\n\n");
			sb.append(fileNames.get(i)+"\n");
			sb.append("TEC#,#MTPs,Coverage,Pattern size,#Translators,CR,Compactness\n");
			int m = mtpMultiplicityData.get(i).size();
			for(int j = 0; j < m; j++) {
				sb.append((j+1)+",");
				sb.append(mtpMultiplicityData.get(i).get(j)+",");
				sb.append(tecCpvData.get(i).get(j).getC()+",");
				sb.append(tecCpvData.get(i).get(j).getP()+",");
				sb.append(tecCpvData.get(i).get(j).getV()+",");
				sb.append(tecCompressionCompactnessData.get(i).get(j).getCompressionRatio()+",");
				sb.append(tecCompressionCompactnessData.get(i).get(j).getCompactness()+"\n");
			}
		}
		return sb.toString();
	}
	
	public TabulateDataFromCOSIATECRun (String cosiatecOutputDirName) throws FileNotFoundException {
		File cosiatecOutputDir = new File(cosiatecOutputDirName);
		String[] fileNameList = cosiatecOutputDir.list();
		for(String fileName : fileNameList) {
			if (fileName.endsWith(".cos18")) {
				ArrayList<TEC> tecs = ViewCOSEncoding.readCOSIATECEncoding(cosiatecOutputDir+"/"+fileName);
				//Store list of points <covered-set-size,pattern-size,vector-set-size> for each TEC
				ArrayList<CPV> cpvDataForThisFile = new ArrayList<CPV>();
				for(TEC tec : tecs) {
					cpvDataForThisFile.add(new CPV(tec.getCoverage(),tec.getPatternSize(),tec.getTranslatorSetSize()));
				}
				tecCpvData.add(cpvDataForThisFile);
				fileNames.add(fileName.substring(0,fileName.length()-6));
			} else if(fileName.endsWith(".log18")) {
				textFileLines = TextFileUtilities.readTextFileIntoListOfLines(cosiatecOutputDir+"/"+fileName);
				mtpMultiplicityData.add(getMTPMultiplicityList(textFileLines));
				tecCompressionCompactnessData.add(getTECCompressionCompactnessDataForThisFile(textFileLines));
				runningTimes.add(getRunningTime(textFileLines));
				datasetSizes.add(getDatasetSize(textFileLines));
				numberOfEncodingTECsList.add(getNumberOfEncodingTECs(textFileLines));
				encodingLengthList.add(getEncodingLength(textFileLines));
				encodingLengthWithoutResPtsList.add(getEncodingLengthWithoutResPts(textFileLines));
				NumPropResPts nprp = getNumPropResPts(textFileLines);
				System.out.println(nprp.getNum()+" "+nprp.getProp());
				numPropResPtsList.add(nprp);
				compressionRatios.add(getCompressionRatio(textFileLines));
				compressionRatiosWithoutResPts.add(getCompressionRatioWithoutResPts(textFileLines));
			}
		}
	}
	
	private static Double getCompressionRatioWithoutResPts(ArrayList<String> textFileLines2) {
		for(String line : textFileLines2) {
			String startString = "Compression ratio excluding residual point set: ";
			if (line.startsWith(startString)) {
				int beginIndex = startString.length();
				String doubleString = line.substring(beginIndex);
				return Double.parseDouble(doubleString);
			}
		}
		return null;
	}

	private static Double getCompressionRatio(ArrayList<String> textFileLines2) {
		for(String line : textFileLines2) {
			String startString = "Compression ratio: ";
			if (line.startsWith(startString)) {
				int beginIndex = startString.length();
				String doubleString = line.substring(beginIndex);
				return Double.parseDouble(doubleString);
			}
		}
		return null;
	}

	private static NumPropResPts getNumPropResPts(ArrayList<String> textFileLines2) {
		String startString = "Number and proportion of residual points: ";
		for(String line : textFileLines2) {
			if (line.startsWith(startString)) {
				int beginIndex = startString.length();
				int endIndex = line.indexOf(",");
				String intString = line.substring(beginIndex,endIndex);
				int num = Integer.parseInt(intString);
				
				beginIndex = endIndex + 2;
				endIndex = line.indexOf("%");
				String doubleString = line.substring(beginIndex,endIndex);
				double prop = Double.parseDouble(doubleString);
				
				System.out.println(num+" "+prop);
				NumPropResPts nprp = new NumPropResPts(num, prop);
				return nprp;
			}
		}
		System.out.println("Should never get here");
		for(String line : textFileLines2)
			System.out.println(line);
		return null;
	}

	private static Integer getEncodingLengthWithoutResPts(ArrayList<String> textFileLines2) {
		for(String line : textFileLines2) {
			String startString = "Encoding length without residual point set: ";
			if (line.startsWith(startString)) {
				int beginIndex = startString.length();
				String intString = line.substring(beginIndex);
				int intValue = Integer.parseInt(intString);
				return intValue;
			}
		}
		return null;
	}

	private static Integer getEncodingLength(ArrayList<String> textFileLines2) {
		for(String line : textFileLines2) {
			String startString = "Encoding length: ";
			if (line.startsWith(startString)) {
				int beginIndex = startString.length();
				String encodingLengthString = line.substring(beginIndex);
				int encodingLength = Integer.parseInt(encodingLengthString);
				return encodingLength;
			}
		}
		return null;
	}

	private static Integer getNumberOfEncodingTECs(ArrayList<String> textFileLines2) {
		for(String line : textFileLines2) {
			String startString = "Number of TECs: ";
			if (line.startsWith(startString)) {
				int beginIndex = startString.length();
				String numberOfTecsString = line.substring(beginIndex);
				int numberOfTecs = Integer.parseInt(numberOfTecsString);
				return numberOfTecs;
			}
		}
		return null;
	}

	private static Integer getDatasetSize(ArrayList<String> textFileLines2) {
		for(String line : textFileLines2) {
			if (line.contains("ms taken to analyse")) {
				int startIndex = line.indexOf("analyse")+"analyse".length()+1;
				String datasetSizeString = line.substring(startIndex,line.indexOf("points")-1);
				return Integer.parseInt(datasetSizeString);
			}
		}
		return null;
	}

	private static Long getRunningTime(ArrayList<String> textFileLines2) {
		for(String line : textFileLines2) {
			if (line.contains("ms taken to analyse")) {
				String runningTimeString = line.substring(0,line.indexOf(" "));
				return Long.parseLong(runningTimeString);
			}
		}
		return null;
	}

	private static ArrayList<TecCRComp> getTECCompressionCompactnessDataForThisFile(
			ArrayList<String> textFileLines2) {
		ArrayList<TecCRComp> tecCrCompList = new ArrayList<TecCRComp>();
		for(String line : textFileLines2) {
			String startString = "Best TEC: (";
			if (line.startsWith(startString)) {
				int beginIndex = startString.length();
				int endIndex = line.indexOf(",");
				String compressionRatioString = line.substring(beginIndex, endIndex);
				double compressionRatio = Double.parseDouble(compressionRatioString);
				beginIndex = endIndex + 1;
				endIndex = line.indexOf(")");
				String compactnessString = line.substring(beginIndex,endIndex);
				double compactness = Double.parseDouble(compactnessString);
				tecCrCompList.add(new TecCRComp(compressionRatio, compactness));
			}
		}
		return tecCrCompList;
	}

	public static ArrayList<Integer> getMTPMultiplicityList(ArrayList<String> textFileLines) {
		ArrayList<Integer> multiplicityData = new ArrayList<Integer>();
		for(String line : textFileLines) {
			if (line.startsWith("computeMtpCisPairs...completed: ")) {
				int startIndex = "computeMtpCisPairs...completed: ".length();
				int endIndex = line.indexOf("MTPs found")-1;
				String intString = line.substring(startIndex, endIndex).trim();
				int thisNoMTPs = Integer.parseInt(intString);
				multiplicityData.add(thisNoMTPs);
			}
		}
		return multiplicityData;
	}
}
