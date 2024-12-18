package com.chromamorph.notes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.NavigableSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import com.chromamorph.maths.Maths;
import com.chromamorph.maths.Rational;
import com.chromamorph.pitch.Chromagram;
import com.chromamorph.pitch.Pitch;


/**
 * The Notes class is a container for a set of Note objects.
 * It also contains variables storing the tatums per crotchet
 * and the time signatures.
 * 
 * @author David Meredith
 *
 */
public class Notes {

	private TreeSet<Note> notes = null;
	private Long tatumsPerCrotchet = null;
	private TreeSet<TimeSignature> timeSignatures = null;
	private Long ticksPerSecond = null;

	public Long getTicksPerSecond() {
		return ticksPerSecond;
	}

	public Notes() {}

	public Notes(String inputFilePathString) {
		try {
			String s = inputFilePathString.toLowerCase();
			Notes notesObject;
			if (s.endsWith(".mid") || s.endsWith(".midi")) {
				notesObject = fromMIDI(inputFilePathString,true,true);
			} else if (s.endsWith(".opnd") || s.endsWith(".opndv")) {
				notesObject = fromOPND(inputFilePathString);
			} else
				notesObject = new Notes(new File(inputFilePathString));
			notes = notesObject.notes;
			tatumsPerCrotchet = notesObject.tatumsPerCrotchet;
			timeSignatures = notesObject.timeSignatures;
			ticksPerSecond = notesObject.ticksPerSecond;
			convertOnsetsAndDurationsToTatums();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Notes(File notesFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(notesFile));
		notes = new TreeSet<Note>();
		for (String l = br.readLine(); l != null ; l = br.readLine()) {
			if (!l.startsWith("//") && l.length() > 0) {
				if (l.startsWith("tatumsPerCrotchet")) {
					tatumsPerCrotchet = Long.parseLong(l.split(" ")[1]);
				}
				else if (Character.isDigit(l.charAt(0))) {
					// Assume input is in format: staff, pn, onset, offset, voice
					notes.add(new Note(l,this));
				} else if (l.length() >= 3 && l.startsWith("set")) {
					//assumes every element of the split array with an odd index
					//is a parameter name and every element with an even index (except
					//for 0) is a value for the preceding parameter.
					String[] setArray = l.split(" ");
					String name = null;
					for (int i = 1; i < setArray.length; i += 2) {
						name = setArray[i];
						if (name.equals("st")) Note.STAFF = Integer.parseInt(setArray[i+1]);
						if (name.equals("on")) Note.ONSET = Long.parseLong(setArray[i+1]);
						if (name.equals("du")) Note.DURATION = Long.parseLong(setArray[i+1]);
						if (name.equals("vo")) Note.VOICE = Integer.parseInt(setArray[i+1]);
						if (name.equals("io")) {
							String val = setArray[i+1];
							if (val.equals("null"))
								Note.INTER_ONSET = null;
							else
								Note.INTER_ONSET = Long.parseLong(setArray[i+1]);
						}
					}
				} else if (l.startsWith("ts")) { //Then this row represents a time signature
					//Time signature row has format: onset offset numerator denominator
					//					System.out.println("Time signature found: "+ l);
					String[] tsArray = l.split(" ");
					if (tsArray.length == 5) {
						Long onset = Long.parseLong(tsArray[1]);
						Long offset = Long.parseLong(tsArray[2]);
						Integer numerator = Integer.parseInt(tsArray[3]);
						Integer denominator = Integer.parseInt(tsArray[4]);
						if (timeSignatures == null) timeSignatures = new TreeSet<TimeSignature>();
						timeSignatures.add(new TimeSignature(onset, offset, numerator, denominator));
					}
				} else if (l.startsWith("ch ")) { //Then it's a row of pitch names in a chord
					Note.CHORD = true;
					String[] chordArray = l.split(" ");
					for (int i = 1; i < chordArray.length; i++) {
						if (i == chordArray.length - 1) Note.CHORD = false;
						Note.PITCH_NAME = chordArray[i];
						notes.add(new Note(this));
					}
				} else { // Assume it is just a row of pitch names
					String[] pnArray = l.split(" ");
					for (String pn : pnArray) {
						Note.PITCH_NAME = pn;
						notes.add(new Note(this));
					}
				}
			}
		}
		br.close();
		this.convertOnsetsAndDurationsToTatums();
	}

	/**
	 * If pitchSpell is true, the MIDI data is pitch spelt with PS13s1 with
	 * preContext set to 10 and postContext set to 42 (values found to work 
	 * best by Meredith 2007).
	 * @param sequence
	 * @param pitchSpell
	 * @throws InvalidMidiDataException
	 * @throws IOException
	 */
	public Notes(Sequence sequence, boolean pitchSpell) throws InvalidMidiDataException, IOException {
		this(sequence,pitchSpell,false);
	}

	/**
	 * Constructs a Notes object from a MIDI file whose name is given as 
	 * an argument
	 * @throws IOException 
	 * @throws InvalidMidiDataException 
	 */
	public Notes(Sequence sequence, boolean pitchSpell, boolean withoutChannel10) throws InvalidMidiDataException, IOException {
		Track[] tracks = sequence.getTracks();
		ticksPerSecond = (1000000 * sequence.getTickLength())/sequence.getMicrosecondLength();
		for (Track track : tracks) {
			for (int i = 0; i < track.size(); i++) {
				MidiEvent midiEvent = track.get(i);
				if (midiEvent.getMessage() instanceof ShortMessage) {
					ShortMessage shortMessage = (ShortMessage)midiEvent.getMessage();
					if (shortMessage.getCommand() == ShortMessage.NOTE_ON &&
							shortMessage.getData2() != 0) {
						long onset = midiEvent.getTick();
						int midiNoteNumber = shortMessage.getData1();
						int channel = shortMessage.getChannel();
						long duration = 0;
						//Find duration
						boolean found = false;
						for (int j = i+1; j < track.size() && !found; j++) {
							MidiEvent midiEvent2 = track.get(j);
							if (midiEvent2.getMessage() instanceof ShortMessage) {
								ShortMessage shortMessage2 = (ShortMessage)midiEvent2.getMessage();
								if (shortMessage2.getChannel() == channel && 
										(shortMessage2.getData1() == midiNoteNumber) &&
										(shortMessage2.getCommand() == ShortMessage.NOTE_OFF ||
										(shortMessage2.getCommand() == ShortMessage.NOTE_ON &&
										shortMessage2.getData2() == 0))) {
									duration = midiEvent2.getTick() - onset;
									found = true;
								}
							}
						}
						if (!found)
							System.out.println("WARNING! Failed to find duration of MIDI event: onset = "+onset+", note number = "+midiNoteNumber+", channel = "+channel);
						if (!withoutChannel10 || channel != 9)
							addNote(new Note(onset, midiNoteNumber, duration, channel, this));
					}
				}
			}
		}
		convertOnsetsAndDurationsToTatums();
		if (pitchSpell) pitchSpell(10,42);
	}
	
	public ArrayList<Long> getNoteDurations() {
		TreeSet<Long> durationSet = new TreeSet<Long>();
		for(Note note : getNotes()) {
			if (note.getDuration() != null) durationSet.add(note.getDuration());
		}
		ArrayList<Long> outputList = new ArrayList<Long>();
		outputList.addAll(durationSet);
		return outputList;
	}
	
	public void convertOnsetsAndDurationsToTatums() {
//		Find greatest common divisor or all durations and onsets
		ArrayList<Long> onsets = getNoteOnsets();
		ArrayList<Long> durations = getNoteDurations();
		TreeSet<Long> allOnsetsAndDurations = new TreeSet<Long>();
		allOnsetsAndDurations.addAll(onsets);
		allOnsetsAndDurations.addAll(durations);
		Long[] timeArray = new Long[allOnsetsAndDurations.size()];
		allOnsetsAndDurations.toArray(timeArray);
		Long gcd = Maths.gcd(timeArray);
		for(Note note : getNotes()) {
			if (note.getOnset() != null)
				note.setOnset(note.getOnset()/gcd);
			if (note.getDuration() != null)
				note.setDuration(note.getDuration()/gcd);
		}
	}

	public int getNumberOfNotes() {
		return notes.size();
	}

	public void addNote(Note note) {
		if (notes == null)
			notes = new TreeSet<Note>();
		notes.add(note);
	}

	public TreeSet<Note> getNotes() {
		return notes;
	}

	public Long getTatumsPerCrotchet() {
		return tatumsPerCrotchet;
	}

	public void setTatumsPerCrotchet(Long tatumsPerCrotchet) {
		this.tatumsPerCrotchet = tatumsPerCrotchet;
	}

	public TreeSet<TimeSignature> getTimeSignatures() {
		return timeSignatures;
	}

	public void setTimeSignatures(TreeSet<TimeSignature> timeSignatures) {
		this.timeSignatures = timeSignatures;
	}

	public void play(long tatumsPerBeat, float beatsPerMinute) throws InvalidMidiDataException, MidiUnavailableException {
		this.play(tatumsPerBeat,beatsPerMinute, 0L);
	}

	public void play(long tatumsPerBeat, float beatsPerMinute, long segmentStart) throws InvalidMidiDataException, MidiUnavailableException {
		this.play(tatumsPerBeat, beatsPerMinute, segmentStart, null, null);
	}

	public void play(Long tatumsPerBeat, Float beatsPerMinute, Long segmentStart, Long segmentEnd) throws InvalidMidiDataException, MidiUnavailableException {
		this.play(tatumsPerBeat, beatsPerMinute, segmentStart, segmentEnd, null);
	}

	public void play(Long tatumsPerBeat, Float beatsPerMinute, Integer lowestMetricLevel) throws InvalidMidiDataException, MidiUnavailableException {
		this.play(tatumsPerBeat, beatsPerMinute, null, null, lowestMetricLevel);
	}

	static class PlayRunnable implements Runnable {

		private Long tatumsPerBeat;
		private Float beatsPerMinute;
		private Long segmentStart;
		private Long segmentEnd;
		private Integer lowestMetricLevel;
		private Notes notes;

		public PlayRunnable(Notes notes, Long tatumsPerBeat, Float beatsPerMinute, Long segmentStart, Long segmentEnd, Integer lowestMetricLevel) {
			this.tatumsPerBeat = tatumsPerBeat;
			this.beatsPerMinute = beatsPerMinute;
			this.segmentStart = segmentStart;
			this.segmentEnd = segmentEnd;
			this.lowestMetricLevel = lowestMetricLevel;
			this.notes = notes;
		}

		@Override
		public void run() {
			try {
				Sequencer sequencer = MidiSystem.getSequencer();
				Synthesizer synth = MidiSystem.getSynthesizer();
				try {
					//Create a MIDI sequence from the notes and play it on the default synthesizer.
					if (segmentStart == null) segmentStart = 0l;
					int ticksPerBeat = 144;
					long ticksPerTatum = ticksPerBeat / tatumsPerBeat;
					Sequence sequence = new Sequence(Sequence.PPQ, ticksPerBeat, 1);
					Track track = sequence.getTracks()[0];
					long finalOffsetTime = 0;
					System.out.println(notes);
					for (Note note : notes.getNotes()) {
						if ((segmentStart == null || note.getOnset() >= segmentStart) &&
								(segmentEnd == null || note.getOnset() < segmentEnd) &&
								(lowestMetricLevel == null || note.getMetricLevel().compareTo(lowestMetricLevel) <= 0)) {
							ShortMessage onMessage = new ShortMessage();
							onMessage.setMessage(ShortMessage.NOTE_ON, note.getMidiNoteNumber(), 96);
							ShortMessage offMessage = new ShortMessage();
							offMessage.setMessage(ShortMessage.NOTE_ON, note.getMidiNoteNumber(),0);
							long onsetTime = (note.getOnset() - segmentStart) * ticksPerTatum;
							long offsetTime = (note.getOffset() - segmentStart) * ticksPerTatum;
							track.add(new MidiEvent(onMessage, onsetTime));
							track.add(new MidiEvent(offMessage, offsetTime));
							if (offsetTime > finalOffsetTime) finalOffsetTime = offsetTime;
						}
					}
					
					for(int i = 0; i < sequence.getTracks()[0].size(); i++) {
						MidiEvent event = sequence.getTracks()[0].get(i);
//						System.out.print(event.getMessage().);
						for(int b : event.getMessage().getMessage()) {
							System.out.print(b+" ");
						}
						System.out.println();
					}
										
					sequencer.open();
					sequencer.setTempoInBPM(beatsPerMinute);
					synth.open();
					sequencer.setSequence(sequence);
					//		try {
					//			MidiSystem.write(sequence, 0, new File("data/out.midi"));
					//		} catch (IOException e1) {
					//			e1.printStackTrace();
					//		}
					sequencer.start();
					System.out.println("Playing should have started");
					double ticksPerMinute = ticksPerBeat * beatsPerMinute;
					long msPerTick = 1 + (long)(60000.0 / ticksPerMinute);
					long finalOffsetTimeInMS = finalOffsetTime * msPerTick;
					Thread.sleep(finalOffsetTimeInMS + 1000);
					sequencer.stop();
					synth.close();
					sequencer.close();
				} catch (InterruptedException e) {
					sequencer.stop();
					synth.close();
					sequencer.close();
				}
			} catch (MidiUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}		

	public void play(Long tatumsPerBeat, Float beatsPerMinute, Long segmentStart, Long segmentEnd, Integer lowestMetricLevel) {
		Thread playThread = new Thread(new PlayRunnable(this, tatumsPerBeat, beatsPerMinute, segmentStart, segmentEnd, lowestMetricLevel));
		playThread.start();
		try {
			playThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public TimeSignature getTimeSignature(Long timePoint) {
		if (timePoint == null) return null;
		if (timePoint.equals(getMaxTimePoint()) && getTimeSignatures() != null)
			return getTimeSignatures().last();
		if (getTimeSignatures() != null) {
			for(TimeSignature ts : getTimeSignatures()) {
				if (ts.getOnset().compareTo(timePoint) <= 0 && ts.getOffset().compareTo(timePoint) > 0)
					return ts;
			}
		}
		return null;
	}

	public Long getMaxTimePoint() {
		if (notes == null || notes.isEmpty()) return null;
		Long maxTimePoint = 0l;
		for (Note note : notes) {
			if (note.getOffset().compareTo(maxTimePoint) > 0)
				maxTimePoint = note.getOffset();
		}
		return maxTimePoint;
	}

	public Integer getMetricLevel(Long timePoint) {
		TimeSignature ts = getTimeSignature(timePoint);
		if (ts == null) return null;
		Long barLength = (long)(getTatumsPerCrotchet() * ts.getCrotchetsPerBar());
		Long posWithinBar = Maths.mod(timePoint - ts.getOnset(), barLength);
		ArrayList<Integer> divisionArray = Maths.factorize(ts.getNumerator());
		int divisionArrayIndex = divisionArray.size();
		Long thisPos = 0l;
		Integer metricLevel = 0;
		Long interBeatIntervalInTatums = barLength;
		int divisor;
		while(!interBeatIntervalInTatums.equals(0l)) {
			//Run through positions at next lower metric level
			metricLevel++;
			divisionArrayIndex--;
			if (divisionArrayIndex < 0) 
				divisor = 2;
			else 
				divisor = divisionArray.get(divisionArrayIndex);
			interBeatIntervalInTatums /= divisor;
			if (!interBeatIntervalInTatums.equals(0l)) {
				thisPos = 0l;
				while (thisPos.compareTo(barLength) <= 0) {
					if (thisPos.equals(posWithinBar))
						return metricLevel;
					thisPos += interBeatIntervalInTatums;
				}
			}
		}
		return null;
	}

	public Integer getMaxMetricLevel() {
		Integer thisMetricLevel = 0;
		Integer maxMetricLevel = 0;
		for (Note note : notes) {
			thisMetricLevel = getMetricLevel(note.getOnset());
			if (thisMetricLevel > maxMetricLevel)
				maxMetricLevel = thisMetricLevel;
			thisMetricLevel = getMetricLevel(note.getOffset());
			if (thisMetricLevel > maxMetricLevel)
				maxMetricLevel = thisMetricLevel;
		}
		return maxMetricLevel;

		//		Integer maxMetricLevel = 0;
		//		Long maxTimePoint = getMaxTimePoint();
		//		Integer thisMetricLevel = 0;
		//		for(Long timePoint = 0l; timePoint.compareTo(maxTimePoint) < 0; timePoint++) {
		//			thisMetricLevel = getMetricLevel(timePoint);
		//			if (thisMetricLevel.compareTo(maxMetricLevel) > 0)
		//				maxMetricLevel = thisMetricLevel;
		//		}
		//		return maxMetricLevel;
	}

	@Override
	public String toString() {
		String tatumsPerCrotchetString = "TatumsPerCrotchet("+tatumsPerCrotchet+")";

		StringBuilder timeSignaturesStringBuilder = new StringBuilder("TimeSignatures(");
		if (getTimeSignatures() != null) {
			TimeSignature firstElement = timeSignatures.first();
			timeSignaturesStringBuilder.append(timeSignatures.first().toString());
			NavigableSet<TimeSignature> timeSignaturesTail = getTimeSignatures().tailSet(firstElement, false);
			for(TimeSignature ts : timeSignaturesTail)
				timeSignaturesStringBuilder.append(","+ts);
		}
		timeSignaturesStringBuilder.append(")");
		String timeSignaturesString = timeSignaturesStringBuilder.toString();

		StringBuilder notesStringBuilder = new StringBuilder("NoteList(");
		String notesString = "";
		if (notes == null || notes.isEmpty()) 
			notesStringBuilder.append(")");
		else {
			Note firstNote = notes.first();
			notesStringBuilder.append(firstNote);
			NavigableSet<Note> notesTail = getNotes().tailSet(firstNote, false);
			for(Note note : notesTail)
				notesStringBuilder.append(","+note);
			notesStringBuilder.append(")");	
			notesString = notesStringBuilder.toString();
		}
		return "Notes("+tatumsPerCrotchetString+","+timeSignaturesString+","+notesString+")";
	}

	public ArrayList<Long> getNoteOnsets() {
		TreeSet<Long> onsets = new TreeSet<Long>();
		for (Note note : notes) {
			onsets.add(note.getOnset());
		}
		return new ArrayList<Long>(onsets);
	}

	public static Notes fromMIDI(String midiFileName, boolean pitchSpell, boolean withoutChannel10) throws InvalidMidiDataException, IOException {
		return new Notes(MidiSystem.getSequence(new File(midiFileName)),pitchSpell,withoutChannel10);
	}

	public static Notes fromMIDI(String midiFileName, boolean pitchSpell) throws InvalidMidiDataException, IOException {
		return new Notes(MidiSystem.getSequence(new File(midiFileName)),pitchSpell);
	}

	public static Notes fromMIDI(String midiFileName) throws InvalidMidiDataException, IOException {
		return new Notes(MidiSystem.getSequence(new File(midiFileName)),false,false);
	}

	/**
	 * Tries to construct a Notes object from an OPND file called opndFileName.
	 * 
	 * An OPND file has the following format
	 * 
	 * (opnd opnd ... )
	 * 
	 * where 
	 * 
	 * opnd = (onset pitchname duration voice)
	 * onset = integer number of tatums
	 * pitchname is standard ASA pitchname (F= flat, S = sharp)
	 * duration = integer number of tatums
	 * voice = integer, starting at 1
	 * 
	 * @param opndFileName
	 * @return A Notes object containing the data in the OPND file with name opndFileName.
	 * @throws IOException
	 * @throws RationalOnsetTimeException 
	 */
	public static Notes fromOPND(String opndFileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(opndFileName)));
		Notes notes = new Notes();
		String l = br.readLine();
		while (l == null || l.trim().length()==0 || l.trim().startsWith("%") || l.trim().startsWith(";") || l.trim().startsWith("//")) l = br.readLine();
		String[] sa2 = null;
		String[] sa = null;
		//		System.out.println(l);
		if (l.indexOf("(") >= 0) {
			//			System.out.println(l+" contains a (");
			StringBuilder sb = new StringBuilder();
			sb.append(l);
			while ((l = br.readLine()) != null)
				if (!(l.trim().startsWith("%") || l.trim().startsWith(";") || l.trim().startsWith("//")))
					sb.append(l);
			String text = sb.toString();
			sa2 = text.split("[()]");
			ArrayList<String> saArrayList = new ArrayList<String>();
			for (String s : sa2) {
				if (s != null && s.trim().length() > 0)
					saArrayList.add(s.trim());
			}
			sa = new String[saArrayList.size()];
			saArrayList.toArray(sa);
		} else {
			//			System.out.println(l+" does not contain a (");
			ArrayList<String> saArrayList = new ArrayList<String>();
			saArrayList.add(l.trim());
			while ((l = br.readLine()) != null)
				if (l.trim().length() > 0 && !l.trim().startsWith("%"))
					saArrayList.add(l.trim());
			sa = new String[saArrayList.size()];
			saArrayList.toArray(sa);
		}

		//		Check if there are any onset times or durations expressed as rationals
		boolean rationalTimes = false;
		for(String s : sa)
			if (s.contains("/")) {
				rationalTimes = true;
				break;
			}

		ArrayList<Long> onsetTimesInTatums = new ArrayList<Long>();
		ArrayList<Long> durationsInTatums = new ArrayList<Long>();
		if (rationalTimes) { //Find onset times and durations in tatums
			ArrayList<Long> onsetDenominators = new ArrayList<Long>();
			ArrayList<Long> onsetNumerators = new ArrayList<Long>();
			ArrayList<Long> durationDenominators = new ArrayList<Long>();
			ArrayList<Long> durationNumerators = new ArrayList<Long>();

			for(String s : sa) {
				if (s.trim().equals("")) continue;
				String[] a = s.split("[ \t]+");
				//				if (a.length == 4)
				//					System.out.println(s+":"+a[0]+","+a[1]+","+a[2]+","+a[3]);
				long num, den;
				int i = 0;
				if (a[i].contains("/")) {
					//					System.out.println(a[0]+" contains /");
					num = Long.parseLong(a[i].substring(0,a[i].indexOf("/")));
					den = Long.parseLong(a[i].substring(a[i].indexOf("/")+1));
				} else {
					//					System.out.println(a[0]+" does not contain /");
					num = Long.parseLong(a[i]);
					den = 1l;
					if (i+1 < a.length && a[i+1].contains("/")) {//Then onset time in form x n/d (e.g., "1 3/4")
						i++;
						long newNum = Long.parseLong(a[i].substring(0,a[i].indexOf("/")));
						den = Long.parseLong(a[i].substring(a[i].indexOf("/")+1));
						num = num * den + newNum;
					}
				}
				onsetDenominators.add(den);
				onsetNumerators.add(num);
				i+=2;
				if (a[i].contains("/")) {
					num = Long.parseLong(a[i].substring(0,a[i].indexOf("/")));
					den = Long.parseLong(a[i].substring(a[i].indexOf("/")+1));
				} else {
					num = Long.parseLong(a[i]);
					den = 1l;
					if (i+1 < a.length && a[i+1].contains("/")) {//Then onset time in form x n/d (e.g., "1 3/4")
						i++;
						long newNum = Long.parseLong(a[i].substring(0,a[i].indexOf("/")));
						den = Long.parseLong(a[i].substring(a[i].indexOf("/")+1));
						num = num * den + newNum;
					}
				}
				durationDenominators.add(den);
				durationNumerators.add(num);
			}

			TreeSet<Long> distinctDenominators = new TreeSet<Long>();
			distinctDenominators.addAll(onsetDenominators);
			distinctDenominators.addAll(durationDenominators);
			System.out.println("Distinct denominators are: "+distinctDenominators);
			Long lcm = Maths.lcm(distinctDenominators);
			System.out.println("Least common multiple of distinct denominators is: "+lcm);
			ArrayList<Double> lcmOverDistinctDenominators = new ArrayList<Double>();
			for(Long d : distinctDenominators)
				lcmOverDistinctDenominators.add(1.0*lcm/d);
			System.out.println("LCM divided by denominators: "+lcmOverDistinctDenominators);

			ArrayList<Rational> newRationalOnsetTimes = new ArrayList<Rational>();
			for(int i = 0; i < onsetDenominators.size(); i++) {
				Long oldNum = onsetNumerators.get(i);
				Long oldDen = onsetDenominators.get(i);
				Rational newRationalOnsetTime = new Rational(oldNum,oldDen).multiplyBy(new Rational(lcm,1l));
				if (newRationalOnsetTime.getDenominator() != 1l) {
					br.close();
					System.out.println("Illegal new rational onset time (denominator should be 1): "+newRationalOnsetTime);
				}
				newRationalOnsetTimes.add(newRationalOnsetTime);
			}
			System.out.println("New rational onset times are: "+newRationalOnsetTimes);
			for(Rational newRationalOnsetTime : newRationalOnsetTimes)
				onsetTimesInTatums.add(newRationalOnsetTime.getNumerator());
			System.out.println("New onset times in tatums are: "+onsetTimesInTatums);

			ArrayList<Rational> newRationalDurations = new ArrayList<Rational>();
			for(int i = 0; i < durationDenominators.size(); i++) {
				Long oldNum = durationNumerators.get(i);
				Long oldDen = durationDenominators.get(i);
				Rational newRationalDuration = new Rational(oldNum,oldDen).multiplyBy(new Rational(lcm,1l));
				if (newRationalDuration.getDenominator() != 1l) {
					br.close();
					System.out.println("Illegal new rational onset time (denominator should be 1): "+newRationalDuration);
				}
				newRationalDurations.add(newRationalDuration);
			}
			System.out.println("New rational durations are: "+newRationalDurations);

			for(Rational newRationalDuration : newRationalDurations)
				durationsInTatums.add(newRationalDuration.getNumerator());
			System.out.println("New durations in tatums are: "+durationsInTatums);


			//Find GCD of all distinct denominators
			//			Long[] distinctDenominatorArray = new Long[distinctDenominators.size()];
			//			distinctDenominators.toArray(distinctDenominatorArray);
			//			long denominatorGCD = Maths.gcd(distinctDenominatorArray);
			//
			//			System.out.println("Denominator GCD is "+denominatorGCD);
			//
			//			ArrayList<Long> reducedDistinctDenominators = new ArrayList<Long>();
			//			for(long d : distinctDenominators)
			//				reducedDistinctDenominators.add(d/denominatorGCD);
			//
			//			System.out.println("Reduced distinct denominators are "+reducedDistinctDenominators);
			//
			//			long productOfDenominators = 1;
			//			for(long d : reducedDistinctDenominators)
			//				productOfDenominators *= d;
			//
			//			System.out.println("Product of denominators is "+productOfDenominators);
			//			System.out.println("Maximum value of a long is "+Long.MAX_VALUE);
			//
			//			TreeSet<Long> newNumerators = new TreeSet<Long>();
			//			for(int i = 0; i < onsetNumerators.size(); i++)
			//				newNumerators.add((productOfDenominators/onsetDenominators.get(i))*onsetNumerators.get(i));
			//			for(int i = 0; i < durationNumerators.size(); i++)
			//				newNumerators.add((productOfDenominators/durationDenominators.get(i))*durationNumerators.get(i));
			//
			//			System.out.println("newNumerators is "+newNumerators);
			//
			//			Long[] newNumeratorsArray = new Long[newNumerators.size()];
			//			newNumerators.toArray(newNumeratorsArray);
			//			long gcdOfNumerators = Maths.gcd(newNumeratorsArray);
			//
			//			System.out.println("GCD of new numerators is "+gcdOfNumerators);
			//
			//			for(int i = 0; i < onsetNumerators.size(); i++) {
			//				long onsetTime = onsetNumerators.get(i)*productOfDenominators/(gcdOfNumerators*onsetDenominators.get(i));
			//				if (onsetTime < 0)
			//					System.out.println("onsetTime less than zero: "+sa[i]+", "+onsetTime);
			//				onsetTimesInTatums.add(onsetTime);
			//			}
			//			for(int i = 0; i < durationNumerators.size(); i++) {
			//				long duration = durationNumerators.get(i)*productOfDenominators/(gcdOfNumerators*durationDenominators.get(i));
			//				if (duration < 0)
			//					System.out.println("duration less than zero: "+sa[i]+", "+duration);
			//				durationsInTatums.add(duration);
			//			}
		}

		for(int i = 0; i < sa.length; i++) {
			String s = sa[i];
			//			System.out.println("s: "+s);
			String x = s.trim();
			if (x.length() > 0) {
				String[] a = x.split("[ \t]+");
				Long onsetTime = null;
				if (rationalTimes)
					onsetTime = onsetTimesInTatums.get(i);
				else
					onsetTime = Long.parseLong(a[0]);
				String pitchName = a[1];
				Long duration = null;
				if (rationalTimes)
					duration = durationsInTatums.get(i);
				else
					duration = Long.parseLong(a[2]);
				int voice = 0;
				if (a.length >= 4)
					voice = Integer.parseInt(a[3]);
				Note note = Note.fromOPND(onsetTime,pitchName,duration,voice,notes);			
				notes.addNote(note);
			}
		}
		br.close();
		return notes;
	}


	/**
	 * Tries to construct a Notes object from an ECOLM file called ecolmFileName.
	 * 
	 * An ECOLM file has the following format
	 * 
	 * #(#opc #opc ... )
	 * 
	 * where 
	 * 
	 * opc = (onset MIDI)
	 * onset = integer number of tatums
	 * MIDI = midi note number
	 * @param ecolmFileName
	 * @return A Notes object containing the data in the ECOLM file with name ecolmFileName.
	 * @throws IOException
	 */
	public static Notes fromECOLM(String ecolmFileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(ecolmFileName)));
		Notes notes = new Notes();
		StringBuilder sb = new StringBuilder();
		String l;
		while ((l = br.readLine()) != null)
			sb.append(l);
		String text = sb.toString();
		String[] sa = text.split("[#()]");
		for(String s : sa) {
			String x = s.trim();
			if (x.length() > 0) {
				//				System.out.println(x);
				//Make a note and add it to notes
				Note note = Note.fromECOLMString(x,notes);
				notes.addNote(note);
			}
		}
		br.close();
		notes.pitchSpell(10, 42);
		return notes;
	}

	private static class KernNote {
		String kernString = null;
		Integer duration = null;
		int numberOfDots = 0;
		boolean isTieStart = false;
		boolean isTieMiddle = false;
		boolean isTieEnd = false;
		String pitchString = null;
		Rational durationAsRational = null;
		String accidentalString = null;
		Pitch pitch = null;
		Rational onsetAsRational = null;
		Long onsetInTatums = null, durationInTatums = null;

		KernNote(String str) {
			kernString = str;
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < str.length(); i++)
				if (isValidChar(str.charAt(i)))
					sb.append(str.charAt(i));
			String s = sb.toString();
			//			Find duration
			boolean inDuration = false, inDots = false, inPitch = false, inAccidental = false;
			Integer start = null; 
			for(int i = 0; i < s.length(); i++) {
				if (!inDuration && ("0123456789".contains(s.substring(i,i+1)))) {
					inDuration = true;
					start = i;
				} else if (inDuration && !("0123456789".contains(s.substring(i,i+1)))) {
					inDuration = false;
					duration = Integer.parseInt(s.substring(start, i));
					i--;
				} 
				else if (!inDots && s.charAt(i) == '.') {
					inDots = true;
					start = i;
				} else if (inDots && s.charAt(i) != '.') {
					inDots = false;
					numberOfDots = i-start;
					i--;
				} 
				else if (!inPitch && "abcdefgABCDEFGr".contains(s.substring(i,i+1))) {
					inPitch = true;
					start = i;
				} else if (inPitch && !("abcdefgABCDEFGr".contains(s.substring(i, i+1)))) {
					inPitch = false;
					pitchString = s.substring(start,i);
					i--;
				} 
				else if (!inAccidental && "#-".contains(s.substring(i,i+1))) {
					inAccidental = true;
					start = i;
				} else if (inAccidental && !("#-".contains(s.substring(i,i+1)))) {
					inAccidental = false;
					accidentalString = s.substring(start,i);
					i--;
				}
				else if (s.charAt(i) == '[')
					isTieStart = true;
				else if (s.charAt(i) == ']')
					isTieEnd = true;
				else if (s.charAt(i) == '_')
					isTieMiddle = true;
			}
			if (inPitch) {
				pitchString = s.substring(start);
				inPitch = false;
			} else if (inAccidental) {
				accidentalString = s.substring(start);
				inAccidental = false;
			}

			//			Calculate duration as rational
			if (duration == null)
				System.out.println("duration is null when kern note string is "+str);
			if (duration == 0)
				durationAsRational = new Rational(2,1);
			else
				durationAsRational = new Rational(1,duration);
			Rational additionalValue = durationAsRational;			
			for(int i = 0; i < numberOfDots; i++) {
				additionalValue = additionalValue.multiplyBy(new Rational(1,2));
				durationAsRational = durationAsRational.add(additionalValue);
			}

			//			Calculate pitch
			String letterName = pitchString.substring(0, 1).toUpperCase();
			if (!letterName.equals("R")) {
				String accidentalChar = (accidentalString == null?"n":(accidentalString.charAt(0)=='#'?"s":(accidentalString.charAt(0)=='-'?"f":null)));
				if (accidentalChar == null) {
					System.out.println("accidentalChar == null when kernNote string is" +str);
				}
				String accidental = accidentalChar;
				if (accidentalChar != null && accidentalString != null && accidentalString.length() > 1)
					for(int i = 1; i < accidentalString.length(); i++) accidental += accidentalChar;
				int octaveNumber = 4 + (letterName.equals(pitchString.substring(0,1))?-pitchString.length():pitchString.length()-1); 
				String davePitchString = letterName + accidental+ octaveNumber;
				pitch = new Pitch();
				try {
					pitch.setPitchName(davePitchString);
				} catch(IllegalArgumentException e) {
					e.printStackTrace();
					System.out.println("\nERROR!!! IllegalArgumentException thrown for kern note "+str);
				}
			}
		}

		public String toString() {
			return "<"+kernString+","+onsetAsRational+","+onsetInTatums+","+(pitch != null?pitch.getPitchName():"r")+","+durationAsRational+","+durationInTatums+","+duration+","+numberOfDots+","+pitchString+","+(isTieStart?",s":(isTieEnd?",e":(isTieMiddle?",m":"")))+">";
		}

		boolean isValidChar(Character c) {
			String cStr = c.toString();
			return "abcdefgABCDEFG0123456789[]r#-_.".contains(cStr);
		}
	}

	static public class MissingTieStartNoteException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MissingTieStartNoteException() {
			super();
		}

		public MissingTieStartNoteException(String message, int row) {
			super(message+" in row "+row+".");
		}

	}

	public static Notes fromKern(String kernFileName) throws IOException, MissingTieStartNoteException {

		//		Read in all lines in input file
		BufferedReader br = new BufferedReader(new FileReader(new File(kernFileName)));
		ArrayList<String[]> lines = new ArrayList<String[]>();
		String l;
		while((l = br.readLine()) != null) {
			try {
				if (l.length()>0 && !"!=*".contains(l.substring(0,1)))
					lines.add(l.split("\t"));
			} catch(StringIndexOutOfBoundsException e) {
				System.out.println("Following line caused problems: "+l);
				br.close();
				throw e;
			}
		}
		br.close();

		//		Construct matrix of note strings from kern file
		int numberOfSpines = lines.get(0).length;
		boolean allSpinesSameLength = true;
		for(int i = 0; i < lines.size(); i++) {
			int len = lines.get(i).length;
			if (len > numberOfSpines) {
				allSpinesSameLength = false;
				numberOfSpines = len;
			}
		}
		if (!allSpinesSameLength)
			System.out.println("\nWARNING!!! NOT ALL SPINES SAME LENGTH IN THIS FILE.");
		KernNote[][] matrix = new KernNote[lines.size()][numberOfSpines];
		for(int row = 0; row < lines.size(); row++) {
			String[] a = lines.get(row);
			for(int spine = 0; spine < numberOfSpines; spine++) {
				if (spine < a.length && !a[spine].trim().equals("."))
					matrix[row][spine] = new KernNote(a[spine].trim());
				//				else if (spine >= a.length) {
				//					StringBuilder sb = new StringBuilder();
				//					for(String s : a)
				//						sb.append(s+" ");
				//					System.out.println("WARNING!!! Record " + row + " has " + a.length + " spines, which is less than the maximum number of spines (" + numberOfSpines + ") in this encoding: "+sb.toString());
				//				}
			}
		}

		//		Calculate onsets and tied durations
		for(int spine = 0; spine < numberOfSpines; spine++) {
			Rational onset = new Rational(0,1);
			KernNote tieStartNote = null;
			for(int row = 0; row < lines.size(); row++) {
				KernNote kn = matrix[row][spine];
				if (kn != null) {
					if (kn.isTieStart) {
						tieStartNote = kn;
						kn.onsetAsRational = onset;
						onset = onset.add(kn.durationAsRational);
					} else if (kn.isTieMiddle) {
						onset = onset.add(kn.durationAsRational);
						if (tieStartNote == null) {
							throw new MissingTieStartNoteException(kernFileName+": "+kn.toString(),row);
						}
						tieStartNote.durationAsRational = tieStartNote.durationAsRational.add(kn.durationAsRational);
						matrix[row][spine] = null;
					} else if (kn.isTieEnd) {
						if (tieStartNote == null) {
							throw new MissingTieStartNoteException(kernFileName+": "+kn.toString(),row);
						}
						onset = onset.add(kn.durationAsRational);
						tieStartNote.durationAsRational = tieStartNote.durationAsRational.add(kn.durationAsRational);
						tieStartNote = null;
						matrix[row][spine] = null;
					}
					else {
						kn.onsetAsRational = onset;
						onset = onset.add(kn.durationAsRational);
					}
				}
			}
		}

		//		Convert durations and onsets to integers
		//		Find least common multiple of the denominators of all onsets and durations
		TreeSet<Long> denominators = new TreeSet<Long>();
		for(int row = 0; row < matrix.length; row++)
			for(int spine = 0; spine < numberOfSpines; spine++) {
				if (matrix[row][spine] != null) {
					denominators.add(matrix[row][spine].durationAsRational.getDenominator());
					denominators.add(matrix[row][spine].onsetAsRational.getDenominator());
				}
			}

		Long lcm = Maths.lcm(denominators.toArray(new Long[denominators.size()]));
		//		System.out.println("Least common multiple of onset and duration denominators is "+lcm);

		//		Calculate all onsets and durations in tatums
		Rational lcmAsRational = new Rational(lcm,1l);
		for(int row = 0; row < matrix.length; row++)
			for(int spine = 0; spine < numberOfSpines; spine++) {
				if (matrix[row][spine] != null) {
					matrix[row][spine].durationInTatums = matrix[row][spine].durationAsRational.multiplyBy(lcmAsRational).getNumerator();
					matrix[row][spine].onsetInTatums = matrix[row][spine].onsetAsRational.multiplyBy(lcmAsRational).getNumerator();
				}
			}


		//		Print out matrix
		//		for(int row = 0; row < matrix.length; row++) {
		//			KernNote[] r = matrix[row];
		//			for(int spine = 0; spine < numberOfSpines; spine++)
		//				System.out.print(String.format("%-35s",r[spine]));
		//			System.out.println();
		//		}

		Notes notes = new Notes();
		for(int row = 0; row < matrix.length; row++)
			for(int spine = 0; spine < numberOfSpines; spine++) {
				if (matrix[row][spine] != null && matrix[row][spine].pitch != null) {
					KernNote kn = matrix[row][spine];
					Long onsetTime = kn.onsetInTatums;
					String pitchName = kn.pitch.getPitchName();
					Long duration = kn.durationInTatums;
					Integer voice = spine+1;
					notes.addNote(Note.fromOPND(onsetTime, pitchName, duration, voice, notes));
				}
			}
		return notes;
	}


	/**
	 * Computes a pitch name for each note in this Notes object. Uses
	 * the PS13s1 algorithm with the precontext and postcontext set to 
	 * kPre and kPost, respectively.
	 * 
	 * @param kPre
	 * @param kPost
	 */
	public void pitchSpell(int kPre, int kPost) {
		ArrayList<Note> noteArray = new ArrayList<Note>(notes);

		//Make chromagramList.
		ArrayList<Chromagram> chromagramList = new ArrayList<Chromagram>();
		for(int i = 0; i < noteArray.size(); i++) {
			Chromagram chromagram = new Chromagram();
			for(int j = i - kPre; j < i + kPost; j++) {
				if (j >= 0 && j < noteArray.size()) {
					int chroma = noteArray.get(j).getPitch().getChroma();
					chromagram.addOneToChromaFrequency(chroma);
				}
			}
			chromagramList.add(chromagram);
		}

		//Make morphList
		//Actually sets the morph value of the computedPitch field in each Note in noteArray
		int[] morphInt = {0, 1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6};
		int[] initMorph = {0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 6};
		int c0 = noteArray.get(0).getPitch().getChroma();
		int m0 = initMorph[c0];
		Integer[] tonicMorphForTonicChroma = {null, null, null, null, null, null, null, null, null, null, null, null};
		for(int ct = 0; ct < 12; ct++)
			tonicMorphForTonicChroma[ct] = Maths.mod(m0 - morphInt[Maths.mod(c0-ct, 12)],7);
		Integer[] morphForTonicChroma = {null, null, null, null, null, null, null, null, null, null, null, null};
		ArrayList<TreeSet<Integer>> tonicChromaSetForMorph = null;
		Integer[] morphStrength = {null, null, null, null, null, null, null};
		for (int j = 0; j < noteArray.size(); j++) {
			for(int ct = 0; ct < 12; ct++) {
				int c = noteArray.get(j).getPitch().getChroma();
				morphForTonicChroma[ct] = Maths.mod(morphInt[Maths.mod(c - ct, 12)] + tonicMorphForTonicChroma[ct], 7);
			}
			tonicChromaSetForMorph = new ArrayList<TreeSet<Integer>>();
			for(int m = 0; m < 7; m++) tonicChromaSetForMorph.add(new TreeSet<Integer>());
			for(int m = 0; m < 7; m++)
				for(int ct = 0; ct < 12; ct++)
					if (morphForTonicChroma[ct] == m)
						tonicChromaSetForMorph.get(m).add(ct);
			for(int m = 0; m < 7; m++) {
				int thisMorphStrength = 0;
				for(Integer tonicChroma : tonicChromaSetForMorph.get(m))
					thisMorphStrength += chromagramList.get(j).get(tonicChroma);
				morphStrength[m] = thisMorphStrength;
			}
			int maxStrengthMorph = 0;
			int maxMorphStrength = morphStrength[0];
			for(int m = 1; m < 7; m++)
				if (morphStrength[m] > maxMorphStrength) {
					maxStrengthMorph = m;
					maxMorphStrength = morphStrength[m];
				}
			noteArray.get(j).getComputedPitch().setMorph(maxStrengthMorph);
		}

		//Make morphetic pitch list
		//Actually sets morphetic pitch value of computedPitch field in each note in NoteArray
		for(int i = 0; i < noteArray.size(); i++) {
			int chromaticPitch = noteArray.get(i).getPitch().getChromaticPitch();
			int morph = noteArray.get(i).getComputedPitch().getMorph();
			int morphOct1 = Maths.floor(chromaticPitch, 12);
			int morphOct2 = morphOct1 + 1;
			int morphOct3 = morphOct1 - 1;
			float mp1 = morphOct1 + morph / 7.0f;
			float mp2 = morphOct2 + morph / 7.0f;
			float mp3 = morphOct3 + morph / 7.0f;
			int chroma = Maths.mod(chromaticPitch,12);
			float cp = morphOct1 + chroma/12.0f;
			float[] diffList = {Math.abs(cp-mp1),Math.abs(cp-mp2),Math.abs(cp-mp3)};
			int[] morphOctList = {morphOct1,morphOct2,morphOct3};
			int bestMorphOctIndex = 0;
			for(int j = 1; j < 3; j++)
				if (diffList[j] < diffList[bestMorphOctIndex])
					bestMorphOctIndex = j;
			int bestMorphOct = morphOctList[bestMorphOctIndex];
			int bestMorpheticPitch = morph + 7 * bestMorphOct;
			noteArray.get(i).getComputedPitch().setMorpheticPitch(bestMorpheticPitch);
		}

		//Set chromamorphetic pitch of computed pitch field of every note in note array
		for(int i = 0; i < noteArray.size(); i++) {
			int chromaticPitch = noteArray.get(i).getPitch().getChromaticPitch();
			int morpheticPitch = noteArray.get(i).getComputedPitch().getMorpheticPitch();
			noteArray.get(i).getComputedPitch().setChromamorpheticPitch(chromaticPitch, morpheticPitch);
		}

		//Set pitch name of each note in notes TreeSet
		int i = 0;
		for(Note note : notes) {
			note.getComputedPitch().setPitchName(noteArray.get(i).getComputedPitch().getPitchName());
			i++;
		}

	}

	public int getPitchSpellingNoteErrorCount() {
		int errorCount = 0;
		for(Note note : getNotes()) {
			if(!note.getComputedPitch().getPitchName().equals(note.getPitchName()))
				errorCount++;
		}
		return errorCount;
	}

	public double getPitchSpellingNoteAccuracy() {
		return 100.0 * (1.0 - 1.0 * getPitchSpellingNoteErrorCount()/notes.size());
	}

	public void addAll(Notes newNotes) {
		if (newNotes.getNotes() != null)
			for(Note note : newNotes.getNotes())
				addNote(note);
	}

	public Notes getVoiceSegment(double onsetTatum, double offsetTatum, int voiceNumber) {
		Notes voiceSegment = new Notes();
		for(Note note : getNotes()) {
			if (note.getOnset()>=onsetTatum && note.getOffset()<=offsetTatum && note.getVoice()==voiceNumber)
				voiceSegment.addNote(note);
		}
		return voiceSegment;
	}

	public Notes getVoiceSegmentForGiraud(double onsetTatum, double offsetTatum, int voiceNumber,boolean includeFinalNote) {
		Notes voiceSegment = new Notes();
		for(Note note : getNotes()) {
			if (includeFinalNote) {
				if (note.getOnset()>=onsetTatum && note.getOnset()<=offsetTatum && note.getVoice()==voiceNumber)
					voiceSegment.addNote(note);
			} else {
				if (note.getOnset()>=onsetTatum && note.getOnset()<offsetTatum && note.getVoice()==voiceNumber)
					voiceSegment.addNote(note);
			}
		}
		return voiceSegment;
	}

	public Notes getVoice(int voiceNumber) {
		Notes voice = new Notes();
		for(Note note : getNotes())
			if (note.getVoice()==voiceNumber)
				voice.addNote(note);
		return voice;
	}

	public int getMaxVoice() {
		int maxVoice = 0;
		for(Note note : getNotes())
			if (note.getVoice() > maxVoice)
				maxVoice = note.getVoice();
		return maxVoice;
	}

	public void toOPDVFile(String opdvFileName) {
		toOPDVFile(opdvFileName, true, false, false);
	}
	
	public void toOPDVFile(String opdvFileName, 
			boolean includeVoice, 
			boolean lispFormat,
			boolean commaSeparated) {
		try {
			String separator = commaSeparated?",":" ";
			Path outputFilePath = Paths.get(opdvFileName);
			outputFilePath.getParent().toFile().mkdirs();
			PrintWriter pr = new PrintWriter(outputFilePath.toFile());
			if (lispFormat) pr.print("(");
			for(Note note : notes) {
				Integer mp = note.getPitch().getMorpheticPitch();
				if (mp == null)
					mp = note.getComputedPitch().getMorpheticPitch();
				Integer v = note.getVoice();
				pr.println(
						(lispFormat?"(":"") +
						note.getOnset()+ separator +
						note.getPitch().getChromaticPitch() + separator +
						mp + separator +
						note.getDuration() +
						((v != null && includeVoice)?(separator+v):"") +
						(lispFormat?")":""));
			}
			if (lispFormat) pr.print(")");
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void toOPCDFile(String opdvFileName, 
			boolean includeVoice, 
			boolean lispFormat,
			boolean commaSeparated) {
		try {
			String separator = commaSeparated?",":" ";
			Path outputFilePath = Paths.get(opdvFileName);
			outputFilePath.getParent().toFile().mkdirs();
			PrintWriter pr = new PrintWriter(outputFilePath.toFile());
			if (lispFormat) pr.print("(");
			for(Note note : notes) {
				Integer v = note.getVoice();
				pr.println(
						(lispFormat?"(":"") +
						note.getOnset()+ separator +
						note.getPitch().getChromaticPitch() + separator +
						note.getDuration() +
						((v != null && includeVoice)?(separator+v):"") +
						(lispFormat?")":""));
			}
			if (lispFormat) pr.print(")");
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void toOPMFileForRetrograde(String opmrFileName) {
		try {
			Path outputFilePath = Paths.get(opmrFileName);
			outputFilePath.getParent().toFile().mkdirs();
			PrintWriter pr = new PrintWriter(outputFilePath.toFile());
			for(Note note : notes) {
				Integer mp = note.getPitch().getMorpheticPitch();
				if (mp == null)
					mp = note.getComputedPitch().getMorpheticPitch();
				Double noteMidPoint = note.getMidPoint();
				pr.println(noteMidPoint + " " + mp);
			}
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
//	public void toOPNDFile(String opndFileName) {
//		try {
//			Path outputFilePath = Paths.get(opndFileName);
//			outputFilePath.getParent().toFile().mkdirs();
//			PrintWriter pr = new PrintWriter(outputFilePath.toFile());
//			for(Note note : notes) {
//				String pn = note.getPitchName();
//				if (pn ==null)
//					pn = note.getComputedPitch().getPitchName();
//				Integer v = note.getVoice();
//				pr.println(
//						note.getOnset()+" "+
//								pn+" "+
//								note.getDuration()+
//								((v != null)?(" "+v):""));
//			}
//			pr.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}

	public void toOPNDFile(String opndFileName) {
		toOPNDFile(opndFileName, true, false, false);
	}
	
	public void toOPNDFile(String opndFileName, 
			boolean includeVoice, 
			boolean lispFormat,
			boolean commaSeparated) {
		try {
			String separator = commaSeparated?",":" ";
			Path outputFilePath = Paths.get(opndFileName);
			outputFilePath.getParent().toFile().mkdirs();
			PrintWriter pr = new PrintWriter(outputFilePath.toFile());
			if (lispFormat) pr.print("(");
			for(Note note : notes) {
				String pn = note.getPitchName();
				if (pn == null)
					pn = note.getComputedPitch().getPitchName();
				Integer v = note.getVoice();
				pr.println(
						(lispFormat?"(":"") +
						note.getOnset()+ separator +
						pn + separator +
						note.getDuration() +
						((v != null && includeVoice)?(separator+v):"") +
						(lispFormat?")":""));
			}
			if (lispFormat) pr.print(")");
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	
	public void toGVFile(String gvFilePathString) {
		try {
			Path outputFilePath = Paths.get(gvFilePathString);
			outputFilePath.getParent().toFile().mkdirs();
			PrintWriter pr = new PrintWriter(outputFilePath.toFile());
			pr.println("//onset\tduration\tcp\tmidi\tmp\tcomputed mp\tchannel\tvoice");
			for(Note note : notes) {
				pr.println(
						note.getOnset()+"\t"+
								note.getDuration()+"\t"+
								note.getPitch().getChromaticPitch()+"\t"+
								note.getPitch().getMIDINoteNumber()+"\t"+
								note.getPitch().getMorpheticPitch()+"\t"+
								note.getComputedPitch().getMorpheticPitch()+"\t"+
								note.getChannel()+"\t"+
								note.getVoice());
			}
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds delayInTatums to onset of every note in this Notes object.
	 * @param delayInTatums
	 */
	public void translateInTime(long delayInTatums) {
		for(Note note : notes)
			note.setOnset(note.getOnset()+delayInTatums);
	}


	
	public static void main2(String[] args) {
		String inputDirectory = "/Users/dave/Documents/Work/Research/Data/Gissel/BachPF/";
		String outputDirectory = "/Users/dave/Documents/Work/Research/Data/Gissel/BachPF-OPD/";
		boolean pitchSpell = true;
		boolean withoutChannel10 = true;
		for(int bwv = 846; bwv < 894; bwv++) {
			for(int i = 1; i < 3; i++) {
				try {
					String midiFilePath = inputDirectory+"0"+bwv+"/0"+i+".mid";
					Notes notes = Notes.fromMIDI(midiFilePath, pitchSpell, withoutChannel10);
					String opdvFilePath = outputDirectory+ "0"+bwv+"/0"+i+".opd";
					notes.toOPDVFile(opdvFilePath);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {

//		try {
//			Notes notes = Notes.fromOPND("/Users/susanne/Repos/omnisia/Points/data/Die Kunst der Fuge/ContapunctusVI/ContrapunctusVIStart.opnd");
//			notes.toOPDVFile("/Users/susanne/Repos/omnisia/Points/data/Die Kunst der Fuge/ContapunctusVI/ContrapunctusVIStart.opd");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		

//		try {
//			Notes notes = Notes.fromOPND("/Users/susanne/Repos/data/Hommage-a-Joseph-Haydn-1909/01 Debussy-Hommage-a-Haydn/DEBUSSY-HOMMAGE-A-HAYDN.OPND");
//			notes.play(4, 50);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InvalidMidiDataException e) {
//			e.printStackTrace();
//		} catch (MidiUnavailableException e) {
//			e.printStackTrace();
//		}
		
//		try {
//			String opndFileName = "../Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv846b-done.opnd";
//			Notes notes = Notes.fromOPND(opndFileName);
//			notes.toOPMFileForRetrograde("../Points/data/WTCI-FUGUES-FOR-JNMR-2014/bwv846b-729.opmr");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//		String notesFileName = "data/chopin-etude-op10-no3.notes";
////		String opndFileName = "/Users/dave/Documents/Work/Research/2015-06-17-workspace-mars/notes/data/chopin-etude-op10-no1.opndv";
////		new Notes(notesFileName).toOPNDFile(opndFileName);
//		try {
//			new Notes(notesFileName).play(4,176);
//		} catch (InvalidMidiDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MidiUnavailableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Notes notes = new Notes("/Users/susanne/Repos/omnisia/MaxTranPatsJava/data/DieKunstDerFuge/ContrapunctusVI/ContrapunctusVI.opnd");
		notes.toOPNDFile("/Users/susanne/Repos/omnisia/MaxTranPatsJava/data/DieKunstDerFuge/ContrapunctusVI/ContrapunctusVI-from-notes.opnd");
	}

	public Notes getSegment(Long startTatum, Long endTatum, boolean zeroOnset) {
		Notes segment = new Notes();
		TreeSet<Note> noteSet = getNotes();
		for(Note note : noteSet) {
			if ((endTatum == null || note.getOnset() <= endTatum) && (startTatum == null || note.getOnset() >= startTatum))
				segment.addNote(note);
		}
		Long segmentOnset = segment.getNotes().first().getOnset();
		if (segmentOnset != 0l && zeroOnset) {
			for(Note note : segment.getNotes())
				note.setOnset(note.getOnset()-segmentOnset);
		}
		return segment;
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
	
	static class OccurrenceSetEndIndexPair {
		ArrayList<Notes> occurrenceSet = new ArrayList<Notes>();
		int endIndex;
		
	}

	static class OccurrenceEndIndexPair {
		Notes occurrence = new Notes();
		int endIndex = 0;
	}
	
	static class NoteEndIndexPair {
		Note note;
		int endIndex;
	}
	
	static public NoteEndIndexPair readNote(StringBuilder sb, int startIndex, Notes notes) {
		NoteEndIndexPair noteEndIndexPair = new NoteEndIndexPair();
		int endIndex = sb.indexOf(")", startIndex);
		Note note = Note.fromOPNDString(sb.substring(startIndex+1, endIndex), notes);
		noteEndIndexPair.endIndex = endIndex + 1;
		noteEndIndexPair.note = note;
		return noteEndIndexPair;
	}

	public static OccurrenceEndIndexPair readOccurrence(StringBuilder sb, int startIndex) {
		OccurrenceEndIndexPair occEi = new OccurrenceEndIndexPair();
		int i = startIndex + 1;
		while (sb.charAt(i) != ')') {
			while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++; //Puts i at beginning of encoding of next point or end of point set
			if (sb.charAt(i) != ')') {
				NoteEndIndexPair pEi = readNote(sb,i,occEi.occurrence);
				i = pEi.endIndex;
				occEi.occurrence.addNote(pEi.note);
			}
		}
		occEi.endIndex = i + 1;
		return occEi;
	}

	public static OccurrenceSetEndIndexPair readOccurrenceSet(StringBuilder sb, int startIndex) {
		OccurrenceSetEndIndexPair osei = new OccurrenceSetEndIndexPair();
		//Find start of first pattern within this occurrence set
		int i = startIndex + 1;
		while (sb.charAt(i) != ')') {
			while (sb.charAt(i) != '(' && sb.charAt(i) != ')') i++; //Puts i at beginning of encoding of next pattern occurrence or end of occurrence set
			if (sb.charAt(i) != ')') {
				OccurrenceEndIndexPair occEi = readOccurrence(sb,i);
				i = occEi.endIndex; //Should be one character after the occurrence just read.
				osei.occurrenceSet.add(occEi.occurrence);
			}
		}
		osei.endIndex = i+1;
		return osei;
	}

	public static ArrayList<ArrayList<Notes>>readGroundTruthPatternsFromFile(String groundTruthFilePath) {
		StringBuilder sb = readGroundTruthFileIntoStringBuilder(groundTruthFilePath);
		
		ArrayList<ArrayList<Notes>> groundTruthPatterns = new ArrayList<ArrayList<Notes>>();
		
		int i = 0;
		while (i < sb.length()) {
			while (i < sb.length() && sb.charAt(i) != '(') i++; //Puts i at beginning of encoding of next occurrence set or end of file
			if (i < sb.length()) {
				OccurrenceSetEndIndexPair osei = readOccurrenceSet(sb,i);
				i = osei.endIndex; //Should be one character after the end of the occurrence set encoding
				groundTruthPatterns.add(osei.occurrenceSet);
			}
		}
		
		return groundTruthPatterns;
	}
	
}
