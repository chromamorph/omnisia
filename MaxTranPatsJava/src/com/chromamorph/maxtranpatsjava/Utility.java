package com.chromamorph.maxtranpatsjava;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TreeSet;

public class Utility {
	
	public static double TOLERANCE = 0.000001;
	
	public static int compareToArrayListOfDoubles(ArrayList<Double> a1, ArrayList<Double> a2) {
		if (a2 == null && a1 == null) return 0;
		if (a1 == null) return -1;
		if (a2 == null) return 1;
//		Neither array is null
		int d = a1.size() - a2.size();
		if (d != 0) return d;
//		Arrays are the same size
		for(int i = 0; i < a1.size(); i++) {
			if (a1.get(i) == -0.0)
				a1.set(i, 0.0);
			if (a2.get(i) == -0.0)
				a2.set(i, 0.0);
			d = a1.get(i).compareTo(a2.get(i));
			if (d != 0) return d;
		}
		return 0;
	}
	
	public static boolean equalsArrayListOfDoubles(ArrayList<Double> a1, ArrayList<Double> a2) {
		return compareToArrayListOfDoubles(a1,a2)==0;
	}
	
	public static int factorial(int n) {
		if (n == 0) return 1;
		int f = 1;
		for(int i = 2; i <= n; i++) f *= i;
		return f;
	}
	
	@SuppressWarnings("deprecation")
	public static int[][] computePermutationIndexSequences(int k) {
		int factorialK = factorial(k);
		int[][] A = new int[factorialK][k];
		
		int[] segLengths = new int[k];
		int[] numSegs = new int[k];
		int[] segsPerPrevSegs = new int[k];
		
		for(int col = 0; col < k; col++) {
			segLengths[col] = factorial(k-col-1);
			numSegs[col] = factorialK/segLengths[col];
			segsPerPrevSegs[col] = k-col;
		}
		
		ArrayList<Integer> vals = new ArrayList<Integer>();
		
		for(int col = 0; col < k; col++) {
			for(int seg = 0, valIndex = 0; seg < numSegs[col]; seg++, valIndex = (valIndex + 1) % segsPerPrevSegs[col]) {
				if (valIndex == 0) {
					vals = new ArrayList<Integer>();
					for(int i = 0; i < k; i++) vals.add(i);
					int r = seg * segLengths[col];
					for(int c = 0; c < col; c++) vals.remove(new Integer(A[r][c]));
				}
				for(int offsetWithinSeg = 0; offsetWithinSeg < segLengths[col]; offsetWithinSeg++) {
					int row = offsetWithinSeg + (seg * segLengths[col]);
					int val = vals.get(valIndex);
					A[row][col] = val;
				}
			}
		}
		return A;
	}

	public static ArrayList<Double> makeSigma(double... sigmaVals) {
		ArrayList<Double> sigma = new ArrayList<Double>();
		for(double s : sigmaVals)
			sigma.add(s);
		return sigma;
	}
	
	/**
	 * Computes the greatest common divisor of its two Integer arguments using
	 * Euclid's algorithm, as described in Cormen, Leiserson and Rivest (2000),
	 * p.810.
	 *
	 * @param a an Integer object
	 * @param b an Integer object
	 * @return the greatest common divisor of the two Integer arguments.
	 */
	public static Long gcd(Long a, Long b) {
		Long absA = Math.abs(a);
		Long absB = Math.abs(b);
		if (absB.equals(0l))
			return absA;
		else
			return gcd(absB, mod(absA, absB));
	}

	public static Integer gcd(Integer a, Integer b) {
		Integer absA = Math.abs(a);
		Integer absB = Math.abs(b);
		if (absB.equals(0))
			return absA;
		else
			return gcd(absB, mod(absA, absB));
	}

	/**
	 * Calculates the GCD of a set of integers, using Euclid's
	 * algorithm recursively:
	 * gcd(a,b,c,d) = gcd(a,gcd(b,c,d))
	 * 
	 * See Cormen, Leiserson, Rivest and Stein (2009, p.939).
	 * @param ints
	 * @return
	 */
	public static Integer gcd(Integer... ints) {
		if (ints == null || ints.length == 0) return null;
		if (ints.length == 1) return ints[0];
		if (ints.length == 2) return gcd(ints[0],ints[1]);
		Integer[] rest = Arrays.copyOfRange(ints, 1, ints.length);
		return gcd(ints[0],gcd(rest));
	}
	
//	public static Long gcd(Long... longs) {
//		if (longs == null || longs.length == 0) return null;
//		if (longs.length == 1) return longs[0];
//		if (longs.length == 2) return gcd(longs[0],longs[1]);
//		Long[] rest = Arrays.copyOfRange(longs, 1, longs.length);
//		return gcd(longs[0],gcd(rest));
//	}

//	public static Long gcd(Collection<Long> longCollection) {
//		Long[] intArray = new Long[longCollection.size()];
//		longCollection.toArray(intArray);
//		return gcd(intArray);
//	}

	
	/**
	 * Returns the largest integer less than or equal to a/b.
	 * @param a an Integer object
	 * @param b an Integer object
	 * @return an Integer object representing the largest integer less than or equal to a/b.
	 * @throws IllegalArgumentException if b is 0.
	 */
	public static Long floor(Long a, Long b) throws IllegalArgumentException {
		if (b.equals(0l))
			throw new IllegalArgumentException("Second argument to floor cannot be zero.");
		Long a2 = a, b2 = b;
		if (b < 0l) {
			b2 = -b;
			a2 = -a;
		}
		Long r = a2/b2; 
		/* This truncates if b2 does not divide a2, so if
		 * a2 is negative, r will be one greater than the
		 * desired result. Therefore...
		 */
		if (a%b != 0l && a2 < 0l) r--;
		return r;
	}

	public static Integer floor(Integer a, Integer b) throws IllegalArgumentException {
		if (b.equals(0))
			throw new IllegalArgumentException("Second argument to floor cannot be zero.");
		Integer a2 = a, b2 = b;
		if (b < 0) {
			b2 = -b;
			a2 = -a;
		}
		Integer r = a2/b2; 
		/* This truncates if b2 does not divide a2, so if
		 * a2 is negative, r will be one greater than the
		 * desired result. Therefore...
		 */
		if (a%b != 0 && a2 < 0) r--;
		return r;
	}

	/**
	 * Returns the least positive residue of a modulo b using formula 33.2 on
	 * p.803 of Cormen, Leiserson and Rivest (2000). Note that this method does
	 * NOT return the same value as the standard Java % operator!
	 *
	 * @param a
	 *            must be a Long
	 * @param b
	 *            must be a Long
	 * @return the least positive residue of a modulo b.
	 * @throws IllegalArgumentException
	 *             if b is zero.
	 */
	public static Long mod(Long a, Long b)
	throws IllegalArgumentException {
		if (b.equals(0l))
			throw new IllegalArgumentException(
					"Second argument to mod must not be zero.");
		return a - (b * floor(a,b));
	}

	public static Integer mod(Integer a, Integer b)
	throws IllegalArgumentException {
		if (b.equals(0))
			throw new IllegalArgumentException(
					"Second argument to mod must not be zero.");
		return a - (b * floor(a,b));
	}

	public static int getHighestPrimeFactor(int n) {
		int i;
		for (i = n; i > 0 && !(isPrime(i) && mod(n,i).equals(0)); i--);
		return i;
	}
	
	public static Boolean isPrime(int i) {
		if (i == 1) return true;
		for(int j = 2; j <= Math.sqrt(i); j++)
			if (mod(i,j) == 0) return false;
		return true;
	}

	public static ArrayList<Integer> factorize(Integer n) {
		int p;
		if (n.equals(0)) return null;
		ArrayList<Integer> output = new ArrayList<Integer>();
		do {
			p = getHighestPrimeFactor(n);
			output.add(p);
			n /= p;
		} while (p != 1);
		return output;
	}
	
	/**
	 * Returns least common multiple (lcm) of the integers a and b.
	 * 
	 * Uses formula lcm(a,b)=ab/gcd(a,b)
	 * 
	 * @param a, b are integers
	 * @return
	 */
	public static Integer lcm(Integer a, Integer b) {
		return (a*b)/gcd(a,b);
	}
	
	public static Long lcm(Long a, Long b) {
		return (a*b)/gcd(a,b);
	}
	
	/**
	 * Returns the least common multiple (lcm) of a sequence of ints.
	 * @param array of ints
	 */
	public static Integer lcm(Integer... nums) {
		if (nums.length == 1)
			return nums[0];
		if (nums.length == 2)
			return lcm(nums[0],nums[1]);
		Arrays.sort(nums);
		return lcm(nums[0],lcmAlreadySorted(Arrays.copyOfRange(nums, 1, nums.length)));
	}
	
	private static Integer lcmAlreadySorted(Integer... sortedNums) {
		if (sortedNums.length == 1)
			return sortedNums[0];
		if (sortedNums.length == 2)
			return lcm(sortedNums[0],sortedNums[1]);
		return lcm(sortedNums[0],lcmAlreadySorted(Arrays.copyOfRange(sortedNums, 1, sortedNums.length)));
	}
	
	public static Long lcm(Long... nums) {
		if (nums.length == 1)
			return nums[0];
		if (nums.length == 2)
			return lcm(nums[0],nums[1]);
		Arrays.sort(nums);
		return lcm(nums[0],lcmAlreadySorted(Arrays.copyOfRange(nums, 1, nums.length)));
	}
	
	private static Long lcmAlreadySorted(Long... sortedNums) {
		if (sortedNums.length == 1)
			return sortedNums[0];
		if (sortedNums.length == 2)
			return lcm(sortedNums[0],sortedNums[1]);
		return lcm(sortedNums[0],lcmAlreadySorted(Arrays.copyOfRange(sortedNums, 1, sortedNums.length)));
	}

	public static Long lcm(TreeSet<Long> sortedNums) {
		if (sortedNums.size() == 1)
			return sortedNums.first();
		if (sortedNums.size() == 2)
			return lcm(sortedNums.first(),sortedNums.tailSet(sortedNums.first(),false).first());
		return lcm(sortedNums.first(),lcm((TreeSet<Long>)(sortedNums.tailSet(sortedNums.first(),false))));
	}

	public static double log2(long l) {
		return Math.log10(l)/Math.log10(2.0);
	}


	
	/**
	 * Set outputDir to null if you want the output files to be stored in a subdirectory of 
	 * the input directory.
	 * @param outputDir
	 * @param inputFilePath
	 * @param transformationClasses
	 * @return
	 */
	public static String getOutputFilePath(
			String outputDir,
			String inputFilePath, 
			TransformationClass[] transformationClasses) {
		int startOfSuffix = inputFilePath.lastIndexOf('.'); // includes dot
		int startOfName = inputFilePath.lastIndexOf('/')+1;
		String inputDir = inputFilePath.substring(0,startOfName); // includes trailing /
		if (outputDir == null) {
			outputDir = inputDir;
		}
		String outputFilePath = outputDir + (outputDir.endsWith("/")?"":"/");
		
		String inputFileName = inputFilePath.substring(startOfName,startOfSuffix);
		
//		Append name of subdirectory to contain output files
		String subdirName = inputFileName;
		subdirName += "-" + inputFilePath.substring(startOfSuffix+1);
		
		for(TransformationClass tc : transformationClasses)
			subdirName += "-" + tc.getName();
		Calendar cal = Calendar.getInstance();
		String timeString = cal.get(Calendar.YEAR)+"-"+String.format("%02d", 1+cal.get(Calendar.MONTH))+"-"+String.format("%02d", cal.get(Calendar.DATE))+"-"+String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))+"-"+String.format("%02d", cal.get(Calendar.MINUTE))+"-"+String.format("%02d", cal.get(Calendar.SECOND))+"-"+String.format("%03d", cal.get(Calendar.MILLISECOND));
		subdirName += "-"+timeString+"/";
		outputFilePath += subdirName;
		new File(outputFilePath).mkdirs();
		outputFilePath += inputFileName + ".enc";
		return outputFilePath;
	}
	
	public static String getOutputPathForPairFileEncoding(
			String outputDirectory, 
			String inputFilePath1, 
			String inputFilePath2,
			TransformationClass[] transformationClasses,
			int count) {
	
		int startOfSuffix1 = inputFilePath1.lastIndexOf('.'); // includes dot
		int startOfName1 = inputFilePath1.lastIndexOf('/')+1;
		String inputDir1 = inputFilePath1.substring(0,startOfName1); // includes trailing /

		int startOfSuffix2 = inputFilePath2.lastIndexOf('.'); // includes dot
		int startOfName2 = inputFilePath2.lastIndexOf('/')+1;

		if (outputDirectory == null) {
			outputDirectory = inputDir1;
		}
		
		String outputFilePath = outputDirectory + (outputDirectory.endsWith("/")?"":"/");
		
		String inputFileName1 = inputFilePath1.substring(startOfName1,startOfSuffix1);
		String inputFileName2 = inputFilePath2.substring(startOfName2,startOfSuffix2);
		
//		Append name of subdirectory to contain output files
		String countStr = String.format("%05d", count);
		String subdirName = countStr+"-"+inputFileName1;
		subdirName += "-" + inputFilePath1.substring(startOfSuffix1+1);
		subdirName += "-" + inputFileName2 + "-" + inputFilePath2.substring(startOfSuffix2+1);
		
		for(TransformationClass tc : transformationClasses)
			subdirName += "-" + tc.getName();
		Calendar cal = Calendar.getInstance();
		String timeString = cal.get(Calendar.YEAR)+"-"+String.format("%02d", 1+cal.get(Calendar.MONTH))+"-"+String.format("%02d", cal.get(Calendar.DATE))+"-"+String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))+"-"+String.format("%02d", cal.get(Calendar.MINUTE))+"-"+String.format("%02d", cal.get(Calendar.SECOND))+"-"+String.format("%03d", cal.get(Calendar.MILLISECOND));
		subdirName += "-"+timeString+"/";
		outputFilePath += subdirName;
		new File(outputFilePath).mkdirs();
		outputFilePath += inputFileName1 + "-" + inputFileName2 + ".enc";
		return outputFilePath;
	}
	

	
	public static void println(PrintWriter output, Object s) {
		System.out.println(s.toString());
		output.println(s.toString());
	}

	public static void print(PrintWriter output, Object s) {
		System.out.print(s.toString());
		output.print(s.toString());
	}

	public static String[] getInputFileNames(String inputDirStr) {
		File inputDir = new File(inputDirStr);
		return inputDir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return 
						name.endsWith(".mid") || 
						name.endsWith(".pts") ||
						name.endsWith(".opnd");
			}
		});
	}

	public static boolean equalWithTolerance(double x, double y) {
		return Math.abs(x - y) <= TOLERANCE;
	}
	
	public static double roundToNearestHalf(double x) {
		return (1.0 * Math.round(2*x))/2;
	}
	
	public static boolean moveOutputFilesToFailedDir(String outputFilePathName) {
		File outputFile = new File(outputFilePathName);
		File outputFileDir = outputFile.getParentFile();
		File destinationFile = new File(outputFileDir.getParent()+"-failed/"+outputFileDir.getName()+"/"+outputFile.getName());
		destinationFile.getParentFile().mkdirs();
		boolean fileRenamed = outputFile.renameTo(destinationFile);
		boolean fileDeleted = outputFile.delete();
		boolean dirDeleted = outputFileDir.delete();
		return fileRenamed && fileDeleted && dirDeleted;
	}
	
	public static void main(String[] args) {
//		ArrayList<Double> a = new ArrayList<Double>();
//		a.add(1.0);
//		a.add(0.0);
//		a.add(0.0);
//		a.add(1.0);
//		ArrayList<Double> b = Utility.makeSigma(1.0,0.0,0.0,1.0);
//		System.out.println(a.equals(b));
//
//		System.out.println(lcm(3,4,8));
//		
//		String[] inputFileNames = getInputFileNames("data/nlb/nlb_datasets/annmidi");
//		for(String s : inputFileNames)
//			System.out.println(s);
//		
//		System.out.println("Number of files: "+inputFileNames.length);
//		
		
//		//Test mod
//		for (long a = -10; a < 10; a++)
//			for (long b = -10; b < 10; b++) {
//				try {
//					System.out.println(""+b+" mod "+a+" = "+mod(b,a));
//				} catch(IllegalArgumentException e) {
//					System.out.println(e.getMessage());
//				}
//			}
//
//		//Test floor
//		for (long a = -10; a < 10; a++)
//			for (long b = -10; b < 10; b++) {
//				try {
//					System.out.println("floor("+a+","+b+") = "+floor(a,b));
//				} catch(IllegalArgumentException e) {
//					System.out.println(e.getMessage());
//				}
//			}
//
//		//Test gcd
//		for (long a = -10; a < 10; a++)
//			for (long b = -10; b < 10; b++) {
//				try {
//					System.out.println("gcd("+a+","+b+") = "+gcd(a,b));
//				} catch(IllegalArgumentException e) {
//					System.out.println(e.getMessage());
//				}
//			}
//		
//		//Test isPrime
//		System.out.println("Primes less than 100");
//		for(int i = 0; i < 100; i++) 
//			if (isPrime(i))
//				System.out.println(i);
//		
//		//Test or highest prime factors
//		for(int i = 0; i < 100; i++)
//			System.out.println(""+i+" "+getHighestPrimeFactor(i));
//		
//		//Test factorize
//		for(int i = 0; i < 100; i++)
//			System.out.println(""+i+" "+factorize(i));
//		
//		//Test gcd with list of numbers
//		System.out.println(gcd(9,15,30));

	
		moveOutputFilesToFailedDir("output/nlb-20210504/move-files-test/test_folder_1/test_file_1.txt");
		moveOutputFilesToFailedDir("output/nlb-20210504/move-files-test/test_folder_2/test_file_2.txt");

	}

	
}
