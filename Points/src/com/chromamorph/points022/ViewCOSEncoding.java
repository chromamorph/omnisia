package com.chromamorph.points022;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import processing.core.PApplet;

/**
 * 
 * @author David Meredith
 * 
 * This class reads and draws a COSIATEC (or SIATECCompress) encoding.
 * 
 * The input encoding must be in the form of a list of TECs. For example:
 * 
 * 
 * T(P(p(8,24),p(12,25),p(16,26),p(22,27),p(23,26),p(24,25),p(28,28),p(32,24),p(36,27),p(42,28),p(44,27),p(46,26),p(48,25)),V(v(0,0),v(48,4),v(96,-3),v(144,-7),v(192,7),v(200,-3),v(256,4),v(304,-10),v(312,1),v(352,-5),v(416,0),v(448,-10),v(488,7),v(496,4),v(512,-2),v(528,-6),v(576,-2),v(584,2),v(632,4),v(656,-1),v(736,-7),v(752,3)))
T(P(p(50,26),p(52,25),p(54,24),p(56,23),p(58,24)),V(v(0,0),v(144,-7),v(230,-1),v(256,7),v(306,4),v(594,-3),v(662,-8),v(736,9),v(752,3)))
T(P(p(98,27),p(100,28),p(102,29),p(104,30)),V(v(0,0),v(8,2),v(16,4),v(54,-3),v(56,-2),v(64,0),v(80,-6),v(158,-6),v(192,-6),v(270,-2),v(272,-1),v(280,-2),v(296,-1),v(584,3),v(588,5),v(624,-10),v(688,-10),v(692,-8),v(704,-9),v(708,-7),v(722,2),v(724,3)))
T(P(p(136,31),p(138,30),p(140,29),p(142,28)),V(v(0,0),v(6,-3),v(114,5),v(128,-8),v(162,-8),v(474,-13),v(482,-17),v(522,-3),v(558,3),v(562,1)))
T(P(p(60,23),p(62,22),p(64,21)),V(v(0,0),v(110,4),v(144,-7),v(212,0),v(256,7),v(262,7),v(496,11),v(574,3),v(584,-2),v(646,-2),v(736,9),v(752,3)))
T(P(p(160,23),p(164,24),p(172,23)),V(v(0,0),v(236,-5),v(276,-1),v(312,3),v(316,6),v(328,4),v(388,11),v(392,7),v(408,8),v(420,8),v(552,2),v(552,7),v(572,6),v(580,5)))
T(P(p(226,21),p(228,22),p(230,20)),V(v(0,0),v(162,7),v(336,12),v(352,-3),v(612,13)))
T(P(p(100,26),p(124,29),p(128,30)),V(v(0,0),v(288,-9),v(542,-2),v(648,3)))
T(P(p(4,23),p(68,26),p(72,27)),V(v(0,0),v(360,-6),v(628,-4),v(696,4)))
T(P(p(116,28),p(124,27)),V(v(0,0),v(8,-1),v(32,-5),v(44,-7),v(100,-7),v(256,-9),v(280,3),v(290,1),v(292,1),v(336,-3),v(356,-4),v(372,-3),v(400,-9),v(524,-3),v(632,-1)))
T(P(p(86,25),p(88,26)),V(v(0,0),v(48,0),v(280,4),v(294,3),v(296,4),v(482,8),v(568,-6),v(596,3),v(712,-5),v(728,-4),v(744,2),v(760,11)))
T(P(p(212,17),p(316,24),p(588,21)),V(v(0,0),v(256,10),v(260,11)))
T(P(p(100,20),p(516,21),p(584,20)),V(v(0,0),v(120,-1),v(152,5)))
T(P(p(84,26),p(96,27)),V(v(0,0),v(8,-2),v(100,3),v(152,-9),v(494,4),v(752,7)))
T(P(p(176,26),p(184,27)),V(v(0,0),v(120,7),v(364,9),v(398,7),v(512,4)))
T(P(p(108,24),p(120,24)),V(v(0,0),v(44,2),v(84,-4),v(608,0)))
T(P(p(244,35),p(280,34)),V(v(0,0),v(172,-21),v(240,-14),v(444,-14)))
T(P(p(148,16),p(232,16)),V(v(0,0),v(32,8),v(112,11),v(488,11)))
T(P(p(338,30),p(386,27)),V(v(0,0),v(82,-7),v(242,-9)))
T(P(p(342,28),p(406,17)),V(v(0,0),v(50,4),v(330,-1)))
T(P(p(446,24),p(447,23)),V(v(0,0),v(40,7)))
T(P(p(272,32),p(276,33)),V(v(0,0),v(236,-19)))
T(P(p(348,32),p(352,31)),V(v(0,0),v(152,-17)))
T(P(p(372,24),p(404,21)),V(v(0,0),v(172,7)))
T(P(p(818,27),p(819,28)),V(v(0,0),v(15,3),v(16,4)))
T(P(p(52,27)),V(v(0,0),v(80,1),v(98,0),v(188,-6),v(226,1),v(256,-14),v(304,-9),v(312,-1),v(316,-11),v(336,6),v(352,-9),v(416,-2),v(440,3),v(476,-10),v(523,7),v(536,-2),v(540,-10),v(600,-10),v(608,-5),v(652,0),v(680,-14),v(688,-11),v(704,4),v(780,-4)))

 * The program allows the user to step through each TEC, displayed against the background of the 
 * complete set of points generated by the whole list of TECs.
 */
public class ViewCOSEncoding {

	private static String defaultFolder = "./";

	public static void main(String[] args) {
		run();
	}
	public static void run() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFileChooser chooser = new JFileChooser(defaultFolder);
				chooser.setDialogTitle("Choose input COS file");
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) return;
				String fullFileName = chooser.getSelectedFile().getAbsolutePath();

				System.out.println("Full file name: "+fullFileName);

				ArrayList<TEC> tecs = readCOSIATECEncoding(fullFileName);
				
				System.out.println("TECs:");
				for(TEC tec : tecs)
					System.out.println(tec);

				//Now we need to find the complete set of points covered 
				//by the set of TECs that we've just read in.

				PointSet pointSet = new PointSet();
				for(TEC tec : tecs) {
					pointSet.addAll(tec.getCoveredPoints());
				}

				System.out.println("Point set: "+pointSet);
				for(TEC tec : tecs)
					tec.setDataset(pointSet);


				//Now we draw this PointSet along with the TECs that have been read in

				JFrame frame = new JFrame();
				frame.setMinimumSize(new Dimension(DrawPoints.drawWindowWidth,DrawPoints.drawWindowHeight+23));
				frame.setResizable(false);
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PApplet embed = new DrawPoints(pointSet,tecs);
				frame.add(embed);
				embed.init();
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	public static ArrayList<TEC> readCOSIATECEncoding(String fileName) {
		//First read in each line of the COS file.

		ArrayList<String> tecStrings = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			for(String l = br.readLine(); l != null; l = br.readLine())
				if (l.trim().length() > 0 && l.trim().startsWith("T(P("))
					tecStrings.add(l.trim());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Now parse each tecString in tecStrings to get a TEC and
		//store these TECs in a new list.

		ArrayList<TEC> tecs = new ArrayList<TEC>();
		for(int i = 0; i < tecStrings.size(); i++)
			tecs.add(new TEC(tecStrings.get(i)));

		return tecs;
	}
	
}