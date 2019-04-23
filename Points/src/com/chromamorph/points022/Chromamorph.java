package com.chromamorph.points022;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import com.chromamorph.notes.Notes;
import com.chromamorph.points022.NoMorpheticPitchException;
import com.chromamorph.points022.PointSet;

import processing.core.PApplet;

public class Chromamorph extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JMenu fileMenu, analyseMenu;
	private JMenuBar menuBar;
	private JMenuItem openMenuItem, COSIATECMenuItem;
	private JDesktopPane desktop;
	//	private String inputFilePathString;
	//	private String inputFileDirectory;
	//	private String inputFileName;
	public static final String[] SUPPORTED_TYPES = {".mid", ".midi", ".opndv", ".opnd", ".notes", ".pts"};

	private static boolean diatonicPitch = false; 
	private static boolean mirex = false;
	private static boolean withCompactnessTrawler = false;
	private static double a = 0.5;
	private static int b = 3;
	private static boolean forRSuperdiagonals = false;
	private static int r = 1;
	private static boolean removeRedundantTranslators = false;
	private static double minTECCompactness = 0.0;
	private static int minPatternSize = 0;
	private static boolean mergeTECs = false;
	private static int minMatchSize = 5;
	private static int numIterations = 10;

	private static JRadioButton morpheticPitch = new JRadioButton("Morphetic pitch");
	private static JRadioButton chromaticPitch = new JRadioButton("Chromatic pitch");
	private static JCheckBox mirexCheckBox = new JCheckBox("MIREX format");
	private static JCheckBox withCompactnessTrawlerCheckBox = new JCheckBox("Use compactness trawler");
	private static JTextField aTextField = new JTextField("0.5");
	private static JTextField bTextField = new JTextField("3");
	private static JLabel aLabel = new JLabel("a");
	private static JLabel bLabel = new JLabel("b");
	private static JCheckBox forRSuperdiagonalsCheckBox = new JCheckBox("For r superdiagonals");
	private static JTextField rTextField = new JTextField("1");
	private static JLabel rLabel = new JLabel("r");
	private static JCheckBox removeRedundantTranslatorsCB = new JCheckBox("Remove redundant translators");
	private static JTextField minTecCompactnessTextField = new JTextField("0.0");
	private static JLabel minTecCompactnessLabel = new JLabel("Minimum TEC compactness");
	private static JTextField minPatternSizeTextField = new JTextField("0");
	private static JLabel minPatternSizeLabel = new JLabel("Minimum pattern size");
	private static JCheckBox mergeTECsCB = new JCheckBox("Merge TECs");
	private static JTextField minMatchSizeTextField = new JTextField("5");
	private static JLabel minMatchSizeLabel = new JLabel("Minimum match size");
	private static JTextField numIterationsTextField = new JTextField("10");
	private static JLabel numIterationsLabel = new JLabel("Number of iterations");


	public Chromamorph() {
		super("Chromamorph");
		//		General setup
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//		Create desktop pane
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int inset = 0;
		setBounds(inset, inset,
				screenSize.width  - inset*2,
				screenSize.height - inset*2);		
		desktop = new JDesktopPane(); //a specialized layered pane
		setContentPane(desktop);
		setResizable(false);

		//		Create menu bar
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		//		Create File menu
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("File menu");
		menuBar.add(fileMenu);

		openMenuItem = new JMenuItem("Open...",KeyEvent.VK_O);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));
		openMenuItem.getAccessibleContext().setAccessibleDescription("Open file");
		openMenuItem.addActionListener(this);

		fileMenu.add(openMenuItem);

		//		Create Analyse menu
		analyseMenu = new JMenu("Analyse");
		analyseMenu.setMnemonic(KeyEvent.VK_A);
		analyseMenu.getAccessibleContext().setAccessibleDescription("Analyse menu");
		menuBar.add(analyseMenu);

		COSIATECMenuItem = new JMenuItem("COSIATEC...",KeyEvent.VK_C);
		COSIATECMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK));
		COSIATECMenuItem.getAccessibleContext().setAccessibleDescription("Analyse file with COSIATEC");
		COSIATECMenuItem.addActionListener(this);

		analyseMenu.add(COSIATECMenuItem);

		setVisible(true);
	}

	class ChromamorphInternalFrame extends JInternalFrame {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6976830757941072917L;
		private PointSet pointSet;
		private Notes notes;
		private String inputFilePathString;

		public String getInputFilePathString() {
			return inputFilePathString;
		}

		public void setInputFilePathString(String inputFilePathString) {
			this.inputFilePathString = inputFilePathString;
		}

		public ChromamorphInternalFrame() {
			super();
		}

		public ChromamorphInternalFrame(String title) {
			super(title,true,true,true,true);
		}

		public PointSet getPointSet() {
			return pointSet;
		}

		public void setPointSet(PointSet pointSet) {
			this.pointSet = pointSet;
		}

		public Notes getNotes() {
			return notes;
		}

		public void setNotes(Notes notes) {
			this.notes = notes;
		}

	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)(e.getSource());
		if (source==openMenuItem)
			openFile();
		else if (source==COSIATECMenuItem)
			runCOSIATEC();
	}

	private void runCOSIATEC() {
		ChromamorphInternalFrame frame = (ChromamorphInternalFrame)(desktop.getSelectedFrame());
		PointSet pointSet = frame.getPointSet();
		String inputFilePathString = frame.getInputFilePathString();
		File inputFile = new File(inputFilePathString);

		String inputFileName = inputFile.getName();
		String outputFolderName = inputFile.getParent(); 
		String fullFileName = inputFilePathString;

		JDialog dialog = new JDialog(this,"COSIATEC",true);
		Container contentPane = dialog.getContentPane();
		GroupLayout layout = new GroupLayout(contentPane);
		contentPane.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		//		Define components
		ButtonGroup group = new ButtonGroup();
		group.add(morpheticPitch);
		group.add(chromaticPitch);
		morpheticPitch.setSelected(diatonicPitch);
		JPanel diatonicPitchPanel = createPanel(morpheticPitch,chromaticPitch);
		mirexCheckBox.setSelected(mirex);
		withCompactnessTrawlerCheckBox.setSelected(withCompactnessTrawler);
		JPanel ctPanel = createPanel(withCompactnessTrawlerCheckBox,aLabel,aTextField,bLabel,bTextField);
		forRSuperdiagonalsCheckBox.setSelected(forRSuperdiagonals);
		JPanel rsdPanel = createPanel(forRSuperdiagonalsCheckBox,rTextField,rLabel);
		removeRedundantTranslatorsCB.setSelected(removeRedundantTranslators);
		JPanel minTecCompactnessPanel = createPanel(minTecCompactnessTextField,minTecCompactnessLabel);
		JPanel minPatternSizePanel = createPanel(minPatternSizeTextField,minPatternSizeLabel);
		mergeTECsCB.setSelected(mergeTECs);
		JPanel minMatchSizePanel = createPanel(minMatchSizeTextField,minMatchSizeLabel);
		JPanel numIterationsPanel = createPanel(numIterationsTextField,numIterationsLabel);
		JPanel mergeTecsPanel = createPanel(mergeTECsCB,minMatchSizePanel,numIterationsPanel);

		ItemListener itemListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getItemSelectable();
				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				if (source==mirexCheckBox)
					mirex = selected;
				else if (source==withCompactnessTrawlerCheckBox)
					withCompactnessTrawler = selected;
				else if (source==forRSuperdiagonalsCheckBox)
					forRSuperdiagonals = selected;
				else if (source==removeRedundantTranslatorsCB)
					removeRedundantTranslators = selected;
				else if (source==mergeTECsCB)
					mergeTECs = selected;
			}
			
		};
		
		mirexCheckBox.addItemListener(itemListener);
		withCompactnessTrawlerCheckBox.addItemListener(itemListener);
		forRSuperdiagonalsCheckBox.addItemListener(itemListener);
		removeRedundantTranslatorsCB.addItemListener(itemListener);
		mergeTECsCB.addItemListener(itemListener);		
		
		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source==morpheticPitch || e.getSource()==chromaticPitch)
					diatonicPitch = morpheticPitch.isSelected();
				else if (source==aTextField) {
					try {
						a = Double.parseDouble(aTextField.getText());
					} catch (NumberFormatException x) {
						a = 0.5;
						aTextField.setText("0.5");
					}
				} else if (source==bTextField) {
					try {
						b = Integer.parseInt(bTextField.getText());
					} catch (NumberFormatException x) {
						b = 3;
						bTextField.setText("3");
					}
				} else if (source==rTextField) {
					try{
						r = Integer.parseInt(rTextField.getText());
					} catch (NumberFormatException x) {
						r = 1;
						rTextField.setText("1");
					}
				} else if (source==minTecCompactnessTextField) {
					try {
						minTECCompactness = Double.parseDouble(minTecCompactnessTextField.getText());
					} catch (NumberFormatException x) {
						minTECCompactness = 0.0;
						minTecCompactnessTextField.setText("0.0");
					}
				} else if (source==minPatternSizeTextField) {
					try{
						minPatternSize = Integer.parseInt(minPatternSizeTextField.getText());
					} catch (NumberFormatException x) {
						minPatternSize = 0;
						minPatternSizeTextField.setText("0");
					}
				} else if (source==minMatchSizeTextField) {
					try{
						minMatchSize = Integer.parseInt(minMatchSizeTextField.getText());
					} catch (NumberFormatException x) {
						minMatchSize = 5;
						minMatchSizeTextField.setText("5");
					}
				} else if (source==numIterationsTextField) {
					try{
						numIterations = Integer.parseInt(numIterationsTextField.getText());
					} catch (NumberFormatException x) {
						numIterations = 10;
						numIterationsTextField.setText("10");
					}
				} 
			}

		};

		morpheticPitch.addActionListener(actionListener);
		chromaticPitch.addActionListener(actionListener);
		aTextField.addActionListener(actionListener);
		bTextField.addActionListener(actionListener);
		rTextField.addActionListener(actionListener);
		minTecCompactnessTextField.addActionListener(actionListener);
		minPatternSizeTextField.addActionListener(actionListener);
		minMatchSizeTextField.addActionListener(actionListener);
		numIterationsTextField.addActionListener(actionListener);


		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(diatonicPitchPanel)
						.addComponent(mirexCheckBox)
						.addComponent(ctPanel)
						.addComponent(rsdPanel)
						.addComponent(removeRedundantTranslatorsCB)
						.addComponent(minTecCompactnessPanel)
						.addComponent(minPatternSizePanel)
						.addComponent(mergeTecsPanel)
						));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(diatonicPitchPanel)
				.addComponent(mirexCheckBox)
				.addComponent(ctPanel)
				.addComponent(rsdPanel)
				.addComponent(removeRedundantTranslatorsCB)
				.addComponent(minTecCompactnessPanel)
				.addComponent(minPatternSizePanel)
				.addComponent(mergeTecsPanel)
				);

		dialog.pack();
		dialog.setVisible(true);

		COSIATECEncoding encoding = new COSIATECEncoding(
				pointSet,
				inputFileName,
				outputFolderName,
				diatonicPitch,
				fullFileName,
				mirex,
				withCompactnessTrawler,
				a,
				b,
				forRSuperdiagonals,
				r,
				removeRedundantTranslators,
				minTECCompactness,
				minPatternSize,
				mergeTECs,
				minMatchSize,
				numIterations
				);
		showTECAnalysis(encoding,pointSet,fullFileName);
	}

	private void showTECAnalysis(Encoding encoding, PointSet pointSet, String inputFilePathString) {
		ChromamorphInternalFrame internalFrame = new ChromamorphInternalFrame(inputFilePathString);
		PApplet embed = new DrawPoints(pointSet, encoding.getOccurrenceSets(),true);
		internalFrame.setPointSet(pointSet);
		internalFrame.setInputFilePathString(inputFilePathString);
		internalFrame.add(embed);
		embed.init();
		internalFrame.setResizable(true);
		internalFrame.pack();
		desktop.add(internalFrame);
		internalFrame.setVisible(true);
		try {
			internalFrame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
	}

	
	private JPanel createPanel(JComponent... components) {
		JPanel panel = new JPanel();
		for(JComponent c : components)
			panel.add(c);
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		return panel;
	}

	private boolean isSupportedFileType(String fileName) {
		String lcFileName = fileName.toLowerCase();
		for(String supportedType : SUPPORTED_TYPES) {
			if (lcFileName.endsWith(supportedType))
				return true;
		}
		return false;
	}

	private void openFile() {
		FileDialog fileDialog = new FileDialog(this);
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return isSupportedFileType(name);
			}
		};

		fileDialog.setFilenameFilter(filter);
		fileDialog.setVisible(true);
		String inputFileName = fileDialog.getFile();
		String inputFileDirectory = fileDialog.getDirectory();
		String inputFilePathString = inputFileDirectory + inputFileName;

		if (isMidiFile(inputFileName))
			openMidiFile(inputFilePathString);
		else if (isOPNDFile(inputFileName))
			openOPNDFile(inputFilePathString);
		else if (isNotesFile(inputFileName))
			openNotesFile(inputFilePathString);
		else if (isPtsFile(inputFileName))
			openPtsFile(inputFilePathString);
	}

	private void showPointSet(PointSet pointSet, String inputFilePathString) {
		ChromamorphInternalFrame internalFrame = new ChromamorphInternalFrame(inputFilePathString);
		PApplet embed = new DrawPoints(pointSet, false, false);
		internalFrame.setPointSet(pointSet);
		internalFrame.setInputFilePathString(inputFilePathString);
		internalFrame.add(embed);
		embed.init();
		internalFrame.setResizable(true);
		internalFrame.pack();
		internalFrame.setVisible(true);
		desktop.add(internalFrame);
		try {
			internalFrame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
	}

	private boolean isPtsFile(String fileName) {
		String lcFileName = fileName.toLowerCase();
		return lcFileName.endsWith(".pts");
	}

	private void openPtsFile(String inputFilePathString) {
		PointSet pointSet = new PointSet(inputFilePathString);
		showPointSet(pointSet,inputFilePathString);
	}

	private boolean isMidiFile(String fileName) {
		String lcFileName = fileName.toLowerCase();
		return lcFileName.endsWith(".mid") || lcFileName.endsWith(".midi");
	}

	private void openMidiFile(String inputFilePathString) {
		try {
			Notes notes = Notes.fromMIDI(inputFilePathString,true,true);
			PointSet pointSet = new PointSet(notes,false);
			showPointSet(pointSet,inputFilePathString);
			//			frames.add(pointSet.draw(inputFilePathString,false,false));
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		}
	}

	private boolean isOPNDFile(String fileName) {
		String lcFileName = fileName.toLowerCase();
		return lcFileName.endsWith(".opnd") || lcFileName.endsWith(".opndv");
	}

	private void openOPNDFile(String inputFilePathString) {
		try {
			Notes notes = Notes.fromOPND(inputFilePathString);
			PointSet pointSet = new PointSet(notes,false);
			showPointSet(pointSet,inputFilePathString);
			//			frames.add(pointSet.draw(inputFilePathString,false,false));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		}
	}

	private boolean isNotesFile(String fileName) {
		String lcFileName = fileName.toLowerCase();
		return lcFileName.endsWith(".notes");
	}

	private void openNotesFile(String inputFilePathString) {
		try {
			Notes notes = new Notes(new File(inputFilePathString));
			PointSet  pointSet = new PointSet(notes,false);
			showPointSet(pointSet,inputFilePathString);
			//			frames.add(pointSet.draw(inputFilePathString,false,false));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Chromamorph();
			}
		});
	}
}
