package com.chromamorph.kryptos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

public class Dorabella {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/dorabella.pts"));
			TreeSet<DorabellaChar> cipher = new TreeSet<DorabellaChar>();
			String l;
			while ((l = br.readLine()) != null)
				cipher.add(new DorabellaChar(l));
			br.close();
			Direction[] rowDirs = new Direction[] {Direction.LEFT,Direction.RIGHT};
			Direction[] colDirs = new Direction[] {Direction.UP,Direction.DOWN};
			boolean[] majors = new boolean[] {true, false};
			int n = 1;
			for(int aRow = 0; aRow < 8; aRow++)
				for(int aCol = 0; aCol < 3; aCol++)
					for(Direction rowDir : rowDirs)
						for(Direction colDir : colDirs)
							for(boolean major : majors) {
								System.out.println(n++);
								DorabellaChar.drawAlphaBox(aRow, aCol, rowDir, colDir, major);
								System.out.println();
								for(DorabellaChar c : cipher)
									System.out.print(c.getPlainChar(aRow, aCol, rowDir, colDir, major));
								System.out.println();
							}
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
