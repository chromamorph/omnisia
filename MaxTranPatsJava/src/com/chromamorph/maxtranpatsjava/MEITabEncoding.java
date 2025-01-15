package com.chromamorph.maxtranpatsjava;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

import com.chromamorph.maths.Rational;
import com.chromamorph.pitch.Pitch;

public class MEITabEncoding {

	private TabTuning tuning = null;
	private String fileName = null;
	private TreeSet<TabNote> tabNotes = new TreeSet<TabNote>();
	
	
	public TreeSet<TabNote> getTabNotes() {
		return tabNotes;
	}
	
	public TabTuning getTuning() {
		return tuning;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private static int getTabGrpDur(String l) throws Exception {
		int start = l.indexOf("dur=\"")+5;
		int end = l.indexOf('"',start);
		if (l.substring(start, end).contains(" "))
			throw new Exception("TabGrpDur exception");
		return Integer.parseInt(l.substring(start, end));
	}
	
	public MEITabEncoding(String fileName) throws IllegalArgumentException, Exception {
		try {
			
			setFileName(fileName);
			boolean readingTuning = false;
			boolean readingTabGrp = false;
			
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			String l;
			Rational onset = new Rational(0,1);
			Rational duration = null;
			
			while ((l = br.readLine()) != null) {
				l = l.trim();
//				Read tuning
				if (l.startsWith("<tuning")) {
					readingTuning = true;
					tuning = new TabTuning();
				}
				else if (l.startsWith("</tuning"))
					readingTuning = false;
				else if (readingTuning && l.startsWith("<course"))
					tuning.addCoursePitch(l);
				
//				Read a tabGrp
				else if (l.startsWith("<tabGrp")) {
					duration = new Rational(1,getTabGrpDur(l));
					readingTabGrp = true;
				}
				else if (l.startsWith("</tabGrp")) {
					readingTabGrp = false;
					onset = onset.add(duration);
				}
				else if (readingTabGrp && l.startsWith("<note")) 
					tabNotes.add(new TabNote(onset,duration,l, tuning));
					
			}
			
//			Computer onsets and durations
			long largestDenominator = 1l;
			for(TabNote note : tabNotes) {
				long d = note.getRationalOnset().getDenominator() ;
				if (d > largestDenominator)
					largestDenominator = d;
				d = note.getRationalDuration().getDenominator();
				if (d > largestDenominator)
					largestDenominator = d;
			}
			for (TabNote note : tabNotes) {
				Rational r = note.getRationalOnset().divideBy(new Rational(1l,largestDenominator));
				if (r.getDenominator() != 1l)
					throw new Exception("Rational onset denominator is not equal to 1 when expected!");
				note.setOnset(r.getNumerator());
				r = note.getRationalDuration().divideBy(new Rational(1l,largestDenominator));
				if (r.getDenominator() != 1l)
					throw new Exception("Rational duration denominator is not equal to 1 when expected!");
				note.setDuration(r.getNumerator());
				
			}
			
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("MEITabEncoding(");
		sb.append(getTuning().toString());
		for(TabNote note : tabNotes) {
			sb.append("\n  "+note);
		}
		sb.append("\n)");
		return sb.toString();
	}
	
	public PointSet getPointSet() {
		PointSet ps = new PointSet();
		for(TabNote tabNote : tabNotes)
			ps.add(new Point((double)tabNote.getOnset(),(double)tabNote.getChromaticPitch(),(double)tabNote.getDuration()));
		return ps;
	}
	
	public static void main(String[] args) {
		try {
			Pitch p = new Pitch();
			p.setPitchName("Cn4");
			System.out.println(p.getChromaticPitch());
			
			String tabFileName = "/Users/susanne/Repos/olja/thesis/approaches/2_practices_of_pre-existing_templates/data/senfl_S329/2v/D-Mbs_Mus.ms._1512_2v_04v-05r_GLT.mei";
			MEITabEncoding tab;
			tab = new MEITabEncoding(tabFileName);
			System.out.println(tab);
			
			PointSet ps = tab.getPointSet();                      
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
