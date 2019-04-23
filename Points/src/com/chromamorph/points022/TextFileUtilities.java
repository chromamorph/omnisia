package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TextFileUtilities {

	public static ArrayList<String> readTextFileIntoListOfLines(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			ArrayList<String> lines = new ArrayList<String>();
			String line;
			while((line = br.readLine()) != null)
				lines.add(line);
			br.close();
			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
