package com.chromamorph.points022;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import com.chromamorph.notes.Note;
import com.chromamorph.notes.Notes;
import com.chromamorph.pitch.Pitch;

public class PitchSpell {

	public static String prettyPrint(String jsonString) {
		String jstr = jsonString.strip();
		StringBuffer sb = new StringBuffer();
		int indentCount = 0;
		String indentString = "  ";
		for(int i = 0; i < jstr.length(); i++) {
			if ("[{".contains(String.format("%s", jstr.charAt(i)))) {
//				sb.append('\n');
//				sb.append(indentString.repeat(indentCount));
				sb.append(jstr.charAt(i));
				sb.append('\n');
				indentCount++;
				sb.append(indentString.repeat(indentCount));
			} else if ("]}".contains(String.format("%s", jstr.charAt(i)))) {
				sb.append('\n');
				indentCount--;
				sb.append(indentString.repeat(indentCount));
				sb.append(jstr.charAt(i));
			} else if (jstr.charAt(i) == ',') {
				sb.append(jstr.charAt(i));
				sb.append('\n');
				sb.append(indentString.repeat(indentCount));
			} else if (jstr.charAt(i) == ':') {
				sb.append(": ");
			} else
				sb.append(jstr.charAt(i));
		}
		return sb.toString();
	}
	
	public static void pitchSpellFile(Path inputPath) throws IOException, ParseException {
		FileReader reader = new FileReader(inputPath.toFile());
		JSONParser jsonParser = new JSONParser();
		PointSet ps = new PointSet();
		JSONArray jsonArr =  (JSONArray)jsonParser.parse(reader);
		reader.close();
		for(Object obj : jsonArr) {
			JSONObject jsonObj = (JSONObject)obj;
			String typeStr = (String)jsonObj.get("tType");
			if (typeStr.equals("Chord")) {
				long time = (long)jsonObj.get("time");
				long duration = (long)jsonObj.get("duration");
				JSONArray tabNotes = (JSONArray)jsonObj.get("TabNotes");
				for (Object noteObject : tabNotes) {
					JSONObject note = (JSONObject)noteObject;
					String midiPitchStr = String.format("%d", note.get("midiPitch"));
//					System.out.println(midiPitchStr);
					int midiPitch = Integer.parseInt(midiPitchStr);
					int voice = 1;
					ps.add(new Point(time,midiPitch,voice,duration));
				}
			}
		}
//		System.out.println(ps);
		Notes notes = new Notes();
		for(Point p : ps.getPoints()) {
			Note note = new Note(p.getX(),p.getY(),p.getDuration(),p.getVoice(),notes);
			notes.addNote(note);
		}
		notes.pitchSpell(10, 42);
		TreeSet<Note> notesTree = notes.getNotes();
		for(Object obj : jsonArr) {
			JSONObject jsonObj = (JSONObject) obj;
			String typeStr = (String)jsonObj.get("tType");
			if (typeStr.equals("Chord")) {
				long time = (long)jsonObj.get("time");
				long duration = (long)jsonObj.get("duration");
				JSONArray tabNotes = (JSONArray)jsonObj.get("TabNotes");
				for (Object noteObject : tabNotes) {
					JSONObject note = (JSONObject)noteObject;
					String midiPitchStr = String.format("%d", note.get("midiPitch"));
//					System.out.println(midiPitchStr);
					int midiPitch = Integer.parseInt(midiPitchStr);
					int voice = 1;
					
					Note ceilNote = notesTree.ceiling(new Note(time,midiPitch,duration,voice,null));
					Note floorNote = notesTree.floor(new Note(time,midiPitch,duration,voice,null));
					if (ceilNote == floorNote) {
						Pitch pitch = ceilNote.getComputedPitch();
						int morpheticPitch = pitch.getMorpheticPitch();
						int chromaticPitch = pitch.getChromaticPitch();
						String pitchName = pitch.getPitchName();
						note.put("morpheticPitch", morpheticPitch);
						note.put("chromaticPitch", chromaticPitch);
						note.put("pitchName", pitchName);
					}
				}
			}
		}
		String outputString = prettyPrint(jsonArr.toJSONString());
		System.out.println("Input file: "+inputPath);
//		System.out.println(outputString);
		String inputPathString = inputPath.toString();
		int end = inputPathString.lastIndexOf(".");
		String outputPathString = inputPathString.substring(0,end)+"-ps.json";
		System.out.println("Output file: "+ outputPathString); System.out.println();
		PrintWriter pw = new PrintWriter(outputPathString);
		pw.print(outputString);
		pw.close();
	}

	public static ArrayList<String> getInputFileList(Path inputPath) {
		ArrayList<String> fileList = new ArrayList<String>();
		String[] fileArray = inputPath.toFile().list();
		for(String fileName : fileArray) {
			Path path = Paths.get(inputPath.toString(),fileName);
			if (path.toFile().isDirectory()) {
				fileList.addAll(getInputFileList(path));
			} else if (fileName.toLowerCase().endsWith(".json"))
				fileList.add(path.toString());
		}
		return fileList;
	}
	
	public static void pitchSpellDirectory(Path inputPath) throws IOException, ParseException {
		ArrayList<String> fileList = getInputFileList(inputPath);
		for(String pathString : fileList) {
			pitchSpellFile(Paths.get(pathString));
		}
	}
	
	public static void main(String[] args) {
		try {
			if (args.length == 1) { //then one argument which must be either a file name or a directory name
				Path inputPath = Paths.get(args[0]);
				if (inputPath.toFile().exists() && inputPath.toString().toLowerCase().endsWith(".json")) {
					pitchSpellFile(inputPath);
				} else if (inputPath.toFile().isDirectory()) {
					pitchSpellDirectory(inputPath);
				}
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
}
