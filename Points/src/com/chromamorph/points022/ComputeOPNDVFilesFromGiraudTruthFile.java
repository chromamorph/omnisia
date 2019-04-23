package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.chromamorph.notes.Note;
import com.chromamorph.notes.Notes;

/**
 * 
 * @author David Meredith
 * 
 * Takes as input the Giraud Fugue Truth file available here:
 * http://www.algomus.fr/truth/fugues.truth.2013.12
 * and the set of opnd files or the fugues from Book 1 of the 48, available here:
 * https://code.google.com/p/chromamorph/source/browse/trunk/Points/data/WTCI-FUGUES-FOR-JNMR-2014/
 * 
 * For each fugue, program creates a folder for each of the patterns (S, CS, CS2, etc.) containing a number of opnd files.
 * Each opnd file name is a four digit, zero-padded number, 0001.opnd, 0002.opnd, ...
 * Each opnd file contains one occurrence of the pattern corresponding to the folder in which it is saved.
 * 
 */
public class ComputeOPNDVFilesFromGiraudTruthFile {

	static String truthFilePathName = "/Users/dave/Documents/Work/Research/Data/GiraudFugueTruthFiles/fugues.truth.2013.12.corrected";
	static String tatumsPerBarFilePathName = "/Users/dave/Documents/Work/Research/Data/GiraudFugueTruthFiles/tatumsPerBar.txt";
	static String voiceOrderFilePathName = "/Users/dave/Documents/Work/Research/Data/GiraudFugueTruthFiles/opndVoiceOrder.txt";
	static String groundTruthPatternRootFolder = "/Users/dave/Documents/Work/Research/Data/GiraudFugueTruthFiles/ground-truth-patterns/opnd";
	static ArrayList<String> truthFile = new ArrayList<String>();
	static ArrayList<Fugue> fugues = new ArrayList<Fugue>();
	static String patternStringPadding = "    ";
	static String occurrenceStringPadding = patternStringPadding + "    ";

	static class Occurrence {
		String string;
		String voice;
		Double start;
		boolean isVaried = false;
		Double startOffset;
		Double changeAfter;
		Double change2After;
		Double endOffset;
		Double resolutionOffset;
		boolean isInvertedStart = false;
		boolean isInverted = false;
		Double length;
		Notes notes = new Notes();

		public Occurrence copy() {
			Occurrence copy = new Occurrence();
			copy.string = string;
			copy.voice = voice;
			copy.start = start;
			copy.isVaried = isVaried;
			copy.startOffset = startOffset;
			copy.changeAfter = changeAfter;
			copy.change2After = change2After;
			copy.endOffset = endOffset;
			copy.resolutionOffset = resolutionOffset;
			copy.isInverted = isInverted;
			copy.isInvertedStart = isInvertedStart;
			copy.length = length;
			copy.notes = new Notes();
			copy.notes.addAll(notes);
			return copy;
		}
		
		public String toString() {
			return patternStringPadding + "OCCURRENCE\n" +
					occurrenceStringPadding +                            "  string:           " + string + "\n" +
					occurrenceStringPadding +                            "  voice:            " + voice + "\n" +
					(start==null?"":occurrenceStringPadding +            "  start:            " + start + "\n") +
					(isVaried?occurrenceStringPadding +                  "  isVaried:         " + isVaried + "\n":"") +
					(startOffset==null?"":occurrenceStringPadding +      "  startOffset:      " + startOffset + "\n") +
					(changeAfter==null?"":occurrenceStringPadding +      "  changeAfter:      " + changeAfter + "\n") +
					(change2After==null?"":occurrenceStringPadding +     "  change2After:     " + change2After + "\n") +
					(endOffset==null?"":occurrenceStringPadding +        "  endOffset:        " + endOffset + "\n") +
					(resolutionOffset==null?"":occurrenceStringPadding + "  resolutionOffset: " + resolutionOffset + "\n") +
					(isInvertedStart?occurrenceStringPadding +           "  isInvertedStart:  " + isInvertedStart + "\n":"") +
					(isInverted?occurrenceStringPadding +                "  isInverted:       " + isInverted + "\n":"") +
					(notes==null?"":occurrenceStringPadding +            "  notes:            " + notes + "\n") +
					(length==null?"":occurrenceStringPadding +           "  length:           " + length + "\n");
		}
	}

	static class Pattern {
		Double patternLength = null;
		Double patternStart = null;
		boolean isAlternative = false;
		boolean isTonalAnswer = false;
		String patternName = null;
		String base = null;
		Double end = null;
		Double resolution = null;
		ArrayList<Occurrence> occurrences = new ArrayList<Occurrence>();

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(Occurrence occurrence : occurrences) {
				sb.append("    "+occurrence+"\n");
			}
			return patternStringPadding + "PATTERN\n" +
			patternStringPadding +                         "  name:          " + patternName + "\n" +
			(patternLength==null?"":patternStringPadding + "  patternLength: " + patternLength + "\n") +
			(patternStart==null?"":patternStringPadding +  "  patternStart:  " + patternStart + "\n") +
			(isAlternative?patternStringPadding +          "  isAlternative: " + isAlternative + "\n":"") +
			(isTonalAnswer?patternStringPadding +          "  isTonalAnswer: " + isTonalAnswer + "\n":"") +
			(base==null?"":patternStringPadding +          "  base:          " + base + "\n") +
			(end==null?"":patternStringPadding +           "  end:           " + end + "\n") +
			(resolution==null?"":patternStringPadding +    "  resolution:    " + resolution + "\n") +
			patternStringPadding +                         "  occurrences:\n" + 
			sb.toString() + "\n";
		}
	}

	static class Fugue {
		String fugueTitle = null;
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		int tatumsPerBar = 0;
		int barOneStartsAt = 0;
		String voiceOrder;

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(Pattern pattern : patterns) {
				sb.append(pattern+"\n");
			}
			return "FUGUE\n" +
			"  title:          " + fugueTitle + "\n" +
			"  tatumsPerBar:   " + tatumsPerBar + "\n" +
			"  barOneStartsAt: " + barOneStartsAt + "\n" + 
			"  patterns:\n" + 
			sb.toString();
		}
	}

	public static void main(String[] args) {
		try {
			//Load tatumsPerBar data
			ArrayList<Integer> tatumsPerBarList = new ArrayList<Integer>();
			ArrayList<Integer> barOneStartsAtList = new ArrayList<Integer>();
			BufferedReader br = new BufferedReader(new FileReader(tatumsPerBarFilePathName));
			String l = null;
			while ((l = br.readLine()) != null) {
				tatumsPerBarList.add(getIntegerFromKey(l, "tatumsPerBar"));
				Integer barOneStartsAt = getIntegerFromKey(l,"barOneStartsAt");
				if (barOneStartsAt != null)
					barOneStartsAtList.add(barOneStartsAt);
				else
					barOneStartsAtList.add(0);
			}
			br.close();

			System.out.println(tatumsPerBarList);
			System.out.println(barOneStartsAtList);
			
			// Load the truth file into memory
			br = new BufferedReader(new FileReader(truthFilePathName));
			l = null;
			while((l = br.readLine()) != null) {
				truthFile.add(l);
			}
			br.close();
			
			// Read through the list of truth file lines, creating a list of Fugue objects
			Fugue currentFugue = null;
			Pattern currentPattern = null;
			for(String line : truthFile) {
				if (line.startsWith("==== wtc-i")) {
					if (currentFugue != null) {
						fugues.add(currentFugue);
					}
					currentFugue = new Fugue();
					currentFugue.fugueTitle = line;
				} else if (line.startsWith("  == S") || line.startsWith("  == CS")) {
					if (currentPattern == null) {
						currentPattern = new Pattern();
					}
					currentPattern.patternName = line;
					currentPattern.patternLength = getDoubleFromKey(line, "length");
					currentPattern.patternStart = getDoubleFromKey(line, "start");
					currentPattern.isAlternative = getBooleanFromKey(line, "alternative");
					currentPattern.isTonalAnswer = getBooleanFromKey(line, "(tonal_answer)");
					currentPattern.base = getStringValueFromKey(line, "base");
					currentPattern.end = getDoubleFromKey(line, "end");
					currentPattern.resolution = getDoubleFromKey(line, "resolution");
				} else if (line.startsWith("===="))
					break;
				else if (line.trim().isEmpty() && currentPattern != null) {
					currentFugue.patterns.add(currentPattern);
					currentPattern = null;
				} else if (currentPattern != null && 
						(line.startsWith("        S") ||
								line.startsWith("        A") ||
								line.startsWith("        T") ||
								line.startsWith("        B") ||
								line.startsWith("        C"))) {
					int startIndex = line.trim().indexOf(" ");
					//					Get voice list for this line
					String voiceListForThisLine = "";
					if (startIndex > 0)
						voiceListForThisLine = line.trim().substring(0,startIndex);
					//					Get occurrences for this line
					if (startIndex > 0) {
						String[] occArray = line.trim().substring(startIndex).trim().split(",");
						if (occArray.length > 0) {
							for(String occ : occArray) {
								String occTrim = occ.trim();
								System.out.println("occ = "+occTrim);
								Occurrence occurrence = new Occurrence();
								occurrence.change2After = getDoubleFromKey(occTrim, "change2after");
								occurrence.changeAfter = getDoubleFromKey(occTrim, "changeafter");
								occurrence.endOffset = getDoubleFromKey(occTrim, "end");
								occurrence.isInverted = getBooleanFromKey(occTrim, "(inverted)");
								occurrence.isInvertedStart = getBooleanFromKey(occTrim, "(inverted_start)");
								occurrence.isVaried = getBooleanFromKey(occTrim,"(varied)");
								occurrence.length = getDoubleFromKey(occTrim,"length");
								occurrence.resolutionOffset = getDoubleFromKey(occTrim,"resolution");
								int endIndex = occTrim.indexOf(" ");
								if (endIndex < 0) endIndex = occTrim.length();
								occurrence.start = Double.parseDouble(occTrim.substring(0,endIndex));
								occurrence.startOffset = getDoubleFromKey(occTrim,"start");
								occurrence.string = occTrim;
								occurrence.voice = voiceListForThisLine;
								currentPattern.occurrences.add(occurrence);
								occurrence = null;
							}
						}
					}
				}
			}
			fugues.add(currentFugue);
			
			//Add in tatumsPerBar and barOneStartsAt to each Fugue
			for(int i = 0; i < tatumsPerBarList.size(); i++) {
				fugues.get(i).barOneStartsAt = barOneStartsAtList.get(i);
				fugues.get(i).tatumsPerBar = tatumsPerBarList.get(i);
			}
			
			//Add voiceOrder to each fugue
			br = new BufferedReader(new FileReader(voiceOrderFilePathName));
			ArrayList<String> voiceOrders = new ArrayList<String>();
			l = null;
			while((l = br.readLine()) != null && l.length() != 0)
				voiceOrders.add(l);
			br.close();
			
			for(int k = 0; k < fugues.size(); k++)
				fugues.get(k).voiceOrder = voiceOrders.get(k);
			
//			Set attributes of derived patterns (i.e., ones with base not equal to null)
			for(Fugue fugue : fugues) {
				for(Pattern pattern : fugue.patterns) {
					if (pattern.base != null) {
//						Find base pattern and set values in pattern
						for(Pattern basePattern : fugue.patterns) {
							if (basePattern.patternName.contains(" "+pattern.base+" ")) {
								if (pattern.patternLength == null && basePattern.patternLength != null)
									pattern.patternLength = basePattern.patternLength;
								if (pattern.patternStart == null && basePattern.patternStart != null)
									pattern.patternStart = basePattern.patternStart;
								if (pattern.end == null && basePattern.end != null)
									pattern.end = basePattern.end;
								if (pattern.resolution == null && basePattern.resolution != null)
									pattern.resolution = basePattern.resolution;
								break;
							}
						}
					}
				}
			}
			
//			Set attributes of alternative patterns
			for(Fugue fugue : fugues) {
				for(Pattern pattern : fugue.patterns) {
					if (pattern.isAlternative) {
//						Find base pattern and set values in pattern
						String altPatternName = pattern.patternName.trim();
						System.out.print(fugue.fugueTitle+": altPatternName = "+altPatternName+"; ");
						String basePatternPrefix = altPatternName.substring(0,altPatternName.indexOf("alternative"));
						System.out.print(" basePatternPrefix = "+basePatternPrefix+"; ");
						for(Pattern basePattern : fugue.patterns) {
							if (basePattern.patternName.contains(basePatternPrefix)) {
								System.out.print("basePattern.patternName = "+basePattern.patternName+"; ");
								if (pattern.patternLength == null && basePattern.patternLength != null)
									pattern.patternLength = basePattern.patternLength;
								if (pattern.patternStart == null && basePattern.patternStart != null)
									pattern.patternStart = basePattern.patternStart;
								if (pattern.end == null && basePattern.end != null)
									pattern.end = basePattern.end;
								if (pattern.resolution == null && basePattern.resolution != null)
									pattern.resolution = basePattern.resolution;
								
//								Add occurrences to alternative pattern
								ArrayList<Occurrence> oldOccurrences = pattern.occurrences;
								pattern.occurrences = new ArrayList<Occurrence>();
								for(Occurrence occurrence : basePattern.occurrences)
									pattern.occurrences.add(occurrence.copy());
								for(Occurrence oldOccurrence : oldOccurrences) {
									for(int i = 0; i < pattern.occurrences.size(); i++) {
										Occurrence newOccurrence = pattern.occurrences.get(i);
										if (newOccurrence.start.equals(oldOccurrence.start) && newOccurrence.voice.equals(oldOccurrence.voice)) {
											pattern.occurrences.remove(newOccurrence);
											break;
										}
									}
									pattern.occurrences.add(oldOccurrence);
								}
//								System.out.print("OCCURRENCES: "+pattern.occurrences);
								break;
							}
						}
						System.out.println();
					}
				}
			}

//			Set null occurrence attributes to parent pattern attribute values
			for(Fugue fugue : fugues)
				for(Pattern pattern : fugue.patterns)
					for(Occurrence occurrence : pattern.occurrences) {
						if (occurrence.length == null && pattern.patternLength != null)
							occurrence.length = pattern.patternLength;
						if (occurrence.startOffset == null && pattern.patternStart != null)
							occurrence.startOffset = pattern.patternStart;
						if (occurrence.endOffset == null && pattern.end != null)
							occurrence.endOffset = pattern.end;
						if (occurrence.resolutionOffset == null && pattern.resolution != null)
							occurrence.resolutionOffset = pattern.resolution;
					}
			
			//Find Notes object for each pattern occurrence
			for(Fugue fugue : fugues) {
				//Read opnd file for this fugue into a Notes object
				String opndFileName = "./data/WTCI-FUGUES-FOR-JNMR-2014/bwv"+fugue.fugueTitle.substring(18,21)+"b-done.opnd";
				Notes fugueNotes = Notes.fromOPND(opndFileName);
				for(Pattern pattern : fugue.patterns) {
					for(Occurrence occurrence : pattern.occurrences) {
						occurrence.notes = getOccurrenceNotes(fugue,fugueNotes,pattern,occurrence,fugue.voiceOrder);
					}
				}
			}

			// Create opnd ground truth pattern files
			for(Fugue fugue : fugues) {
//				Create folder for fugue
				String fugueFolderName = "bwv"+fugue.fugueTitle.substring(18,21);
				String fugueFolderPath = groundTruthPatternRootFolder+"/"+fugueFolderName;
				for(Pattern pattern : fugue.patterns) {
//					Create folder for pattern
					int start = 3;
					int end = pattern.patternName.trim().indexOf(" ",start);
					String patternFolderName = pattern.patternName.trim().substring(start,end);
					if (pattern.isAlternative) {
						int startCommentStringIndex = pattern.patternName.indexOf("##")+3;
						String commentString = pattern.patternName.substring(startCommentStringIndex);
						commentString = commentString.replaceAll("[, (\")]", "");
						patternFolderName += "-alt-" + commentString;
					}
					String patternFolderPath = fugueFolderPath+"/"+patternFolderName;
					File patternFolder = new File(patternFolderPath);
					patternFolder.mkdirs();
					for(int k = 0; k < pattern.occurrences.size(); k++) {
						Occurrence occurrence = pattern.occurrences.get(k);
//						Create file for this occurrence
						String occurrenceFileName = String.format("%04d",k+1)+".opnd";
						String occurrenceFilePath = patternFolderPath+"/"+occurrenceFileName;
//						Write opnd data to file
						File occurrenceFile = new File(occurrenceFilePath);
						PrintStream ps = new PrintStream(occurrenceFile);
						ps.print("(");
						for(Note occNote : occurrence.notes.getNotes()) {
							ps.println(new OPNDV(occNote));
						}
						ps.print(")");
						ps.close();
					}
				}
			}
			
//			for(Fugue fugue : fugues)
//				System.out.println(fugue);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static Notes getOccurrenceNotes(Fugue fugue, Notes fugueNotes, Pattern pattern, Occurrence occurrence, String voiceOrder) {
		/*
		 * If the occurrence has changeafter and possibly change2after, then
		 * Notes will contain notes from more than one voice. It will be the union
		 * of at most 3 voice segments.
		 */
		
		double referenceStartTatum = fugue.tatumsPerBar*(occurrence.start-1)+fugue.barOneStartsAt;
		if (occurrence.change2After != null) {
			double onsetTatum1 = fugue.tatumsPerBar*(occurrence.start-1+(occurrence.startOffset==null?0:occurrence.startOffset))+fugue.barOneStartsAt;
			double offsetTatum1 = referenceStartTatum+fugue.tatumsPerBar*occurrence.changeAfter;
			double offsetTatum2 = referenceStartTatum+fugue.tatumsPerBar*occurrence.change2After;
			double offsetTatum3 = referenceStartTatum+fugue.tatumsPerBar*(occurrence.length+(occurrence.endOffset==null?0:occurrence.endOffset));
			int voiceNumber1 = voiceOrder.indexOf(occurrence.voice.charAt(0))+1;
			int voiceNumber2 = voiceOrder.indexOf(occurrence.voice.charAt(1))+1;
			Notes notes = fugueNotes.getVoiceSegmentForGiraud(onsetTatum1,offsetTatum1,voiceNumber1,false);
			notes.addAll(fugueNotes.getVoiceSegmentForGiraud(offsetTatum1,offsetTatum2,voiceNumber2,false));
			notes.addAll(fugueNotes.getVoiceSegmentForGiraud(offsetTatum2,offsetTatum3,voiceNumber1,true));
			return notes;
		} else if (occurrence.changeAfter != null) {
			double onsetTatum1 = fugue.tatumsPerBar*(occurrence.start-1+(occurrence.startOffset==null?0:occurrence.startOffset))+fugue.barOneStartsAt;
			double offsetTatum1 = referenceStartTatum+fugue.tatumsPerBar*occurrence.changeAfter;
			double offsetTatum2 = referenceStartTatum+fugue.tatumsPerBar*(occurrence.length+(occurrence.endOffset==null?0:occurrence.endOffset));
			int voiceNumber1 = voiceOrder.indexOf(occurrence.voice.charAt(0))+1;
			int voiceNumber2 = voiceOrder.indexOf(occurrence.voice.charAt(1))+1;
			Notes notes = fugueNotes.getVoiceSegmentForGiraud(onsetTatum1,offsetTatum1,voiceNumber1,false);
			notes.addAll(fugueNotes.getVoiceSegmentForGiraud(offsetTatum1,offsetTatum2,voiceNumber2,true));
			return notes;
		} else {// no change of voice
			double onsetTatum = fugue.tatumsPerBar*(occurrence.start-1+(occurrence.startOffset==null?0:occurrence.startOffset))+fugue.barOneStartsAt;
			double offsetTatum = referenceStartTatum+fugue.tatumsPerBar*(occurrence.length+(occurrence.endOffset==null?0:occurrence.endOffset));
			int voiceNumber = voiceOrder.indexOf(occurrence.voice.charAt(0))+1;
			return fugueNotes.getVoiceSegmentForGiraud(onsetTatum,offsetTatum,voiceNumber,true);
		}
	}
	
	static boolean getBooleanFromKey(String s, String key) {
		return s.contains(key);
	}

	static String getStringValueFromKey(String s, String key) {
		String s1 = s.replace('[', ' ').replace(']',' ').trim();
		String[] a = s1.split(" ");
		for(int i = 0; i < a.length; i++) {
			if (a[i].equals(key)) {
				i++;
				while(a[i].length() == 0) i++;
				return a[i].trim();
			}
		}
		return null;
	}

	static Integer getIntegerFromKey(String s, String key) {
		String stringValue = getStringValueFromKey(s, key);
		if (stringValue != null)
			return Integer.parseInt(stringValue);
		return null;
	}

	static Double getDoubleFromKey(String s, String key) {
		String stringValue = getStringValueFromKey(s, key);
		if (stringValue == null) return null;
		boolean hasSign = (stringValue.charAt(0)=='+' || stringValue.charAt(0)=='-');
		int start = 0;
		int sign = 1;
		if (hasSign) {
			start = 1;
			if (stringValue.charAt(0)=='-') sign = -1;
		}
		if (stringValue.contains(".") || !stringValue.contains("/"))
			return sign * Double.parseDouble(stringValue.substring(start));
		int slashPos = stringValue.indexOf('/');
		if (slashPos >= 0) {
			//			It's a rational number
			int num = Integer.parseInt(stringValue.substring(start,slashPos));
			int den = Integer.parseInt(stringValue.substring(slashPos+1));
			return sign * 1.0 * num / den;
		}
		return null;
	}
}
