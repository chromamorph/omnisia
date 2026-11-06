package com.chromamorph.kryptos;

import com.chromamorph.maths.Maths;

public class DorabellaChar implements Comparable<DorabellaChar> {
	private int position,row,column;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
	public DorabellaChar(int row, int col) {
		setPosition(0);
		setRow(row);
		setColumn(col);
	}
	
	public DorabellaChar(int position, int row, int col) {
		setPosition(position);
		setRow(row);
		setColumn(col);
	}
	
	public DorabellaChar(String s) {
		String[] a = s.split("\t");
		setPosition(Integer.parseInt(a[0]));
		setRow(Integer.parseInt(a[1]));
		setColumn(Integer.parseInt(a[2]));
	}
	
	@Override
	public int compareTo(DorabellaChar o) {
		if (o == null) return 1;
		int d = getPosition() - o.getPosition();
		if (d != 0) return d;
		d = getRow() - o.getRow();
		if (d != 0) return d;
		return getColumn() - o.getColumn();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof DorabellaChar)) return false;
		return compareTo((DorabellaChar)obj) == 0;
	}
	
	/**
	 * Assumes that we have an 8 x 3 box containing the letters in the standard
	 * alphabet arranged so that they can be read off by reading along the rows 
	 * from left to right or right to left or up or down the columns.
	 * 
	 * We define the arrangement of the letters in the box by specifying the position
	 * of A, the directions in which the rows and columns are read, and whether or not
	 * the ordering is row-major.
	 * 
	 * For example,
	 * 
	 *   0  1  2
	 * 0 O  N  M
	 * 1 L  K IJ
	 * 2 H  G  F
	 * 3 E  D  C
	 * 4 B  A  Z
	 * 5 Y  X  W
	 * 6 UV T  S 
	 * 7 R  Q  P
	 * 
	 * would be specified by
	 * aRow = 4
	 * aCol = 1
	 * rowDir = left
	 * colDir = up
	 * isRowMajor = true
	 * 
	 * whereas
	 *
	 *   0  1  2
	 * 0 W  E  N
	 * 1 UV D  M
	 * 2 T  C  L
	 * 3 S  B  K
	 * 4 R  A  IJ
	 * 5 Q  Z  H
	 * 6 P  Y  G 
	 * 7 O  X  F
	 * 
	 * would be:
	 * aRow = 4
	 * aCol = 1
	 * rowDir = right
	 * colDir = up
	 * isRowMajor = false
	 * 
	 * There are therefore a total of
	 * 24 x 2 x 2 x 2 = 192 possible arrangements
	 * 
	 * @param aRow
	 * @param aCol
	 * @param dir
	 * @return
	 */
	public char getPlainChar(int aRow, int aCol, Direction rowDir, Direction colDir, boolean isRowMajor) {
		int deltaRow = row - aRow;
		int deltaCol = column - aCol;
//		String alphabet = "ABCDEFGHIKLMNOPQRSTVWXYZ";
//		String alphabet = "DORABCEFGHIKLMNPQSTVWXYZ";
		String alphabet = "ABCDEFGHIJKLMNOPRSTUVWXY";
		char plainChar = '_';
		int deltaChar = 0;
		
		if (rowDir == Direction.RIGHT && colDir == Direction.DOWN && isRowMajor)
			deltaChar = deltaRow*3+deltaCol;
		else if (rowDir == Direction.RIGHT && colDir == Direction.DOWN && !isRowMajor)
			deltaChar = deltaCol*8+deltaRow;
		else if (rowDir == Direction.RIGHT && colDir == Direction.UP && isRowMajor)
			deltaChar = (-deltaRow*3)+deltaCol;
		else if (rowDir == Direction.RIGHT && colDir == Direction.UP && !isRowMajor)
			deltaChar = (deltaCol*8)-deltaRow;
		else if (rowDir == Direction.LEFT && colDir == Direction.DOWN && isRowMajor)
			deltaChar = deltaRow*3-deltaCol;
		else if (rowDir == Direction.LEFT && colDir == Direction.DOWN && !isRowMajor)
			deltaChar = (-deltaCol*8)+deltaRow;
		else if (rowDir == Direction.LEFT && colDir == Direction.UP && isRowMajor)
			deltaChar = (-deltaRow*3)-deltaCol;
		else if (rowDir == Direction.LEFT && colDir == Direction.UP && !isRowMajor)
			deltaChar = (-deltaCol*8)-deltaRow;
		
		plainChar = alphabet.charAt(Maths.mod(deltaChar, 24));
		return plainChar;
	}
	
	public static void drawAlphaBox(int aRow, int aCol, Direction rowDir, Direction colDir, boolean isRowMajor) {
		System.out.println("a=("+aRow+","+aCol+"),"+rowDir+","+colDir+","+(isRowMajor?"Row-major":"Col-major"));
		System.out.print("    ");
		for(int col = 0; col < 3; col++)
			System.out.print("   "+col);
		System.out.println();
		for(int row = 0; row < 8; row++) {
			System.out.print(String.format("%4d", row));
			for(int col = 0; col < 3; col++)
				System.out.print(String.format("%4s",new DorabellaChar(row,col).getPlainChar(aRow, aCol, rowDir, colDir, isRowMajor)));
			System.out.println();
		}
	}
	
	public String toString() {
		return "("+position+", "+row+", "+column+")";
	}
	
	public static void main(String[] args) {
		drawAlphaBox(4,1,Direction.LEFT,Direction.UP,true);
		drawAlphaBox(4,1,Direction.RIGHT,Direction.UP,false);
	}

}
