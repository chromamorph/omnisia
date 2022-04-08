package com.chromamorph.omnisia.gui;

import java.io.File;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.chromamorph.points022.DrawPoints;
import com.chromamorph.points022.PointSet;

import processing.core.PApplet;

import com.chromamorph.notes.Notes.MissingTieStartNoteException;

public class OmnisiaGui extends JFrame {

	private static final long serialVersionUID = 1L;
	public static PApplet drawPoints;
	public static OmnisiaGui GUI;
	public static String fileName; 

	static class PanelLayoutPair {
		JPanel panel;
		GroupLayout layout;
		
		PanelLayoutPair(JPanel panel, GroupLayout layout) {
			this.panel = panel;
			this.layout = layout;
		}
	}
	
	private JMenuItem createOpenFileMenuItem() {
		JMenuItem menuItem = new JMenuItem("Open File...");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(GUI);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						File file = fc.getSelectedFile();
						fileName = file.getAbsolutePath();
						PointSet pointSet = new PointSet(fileName);
						GUI.remove(drawPoints);
						drawPoints = new DrawPoints(pointSet);
						GUI.add(drawPoints);
						drawPoints.init();
						GUI.pack();
						GUI.setVisible(true);
					} catch (MissingTieStartNoteException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		return menuItem;
	}

	private JPanel createDrawPointsPanel() {
		JPanel drawPointsPanel = new JPanel();
		drawPoints = new DrawPoints();
		drawPointsPanel.add(drawPoints);
		drawPoints.init();
		return drawPointsPanel;
	}
	
	private PanelLayoutPair createPanel(String title) {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		if (title.isEmpty())
			panel.setBorder(BorderFactory.createEtchedBorder());
		else
			panel.setBorder(BorderFactory.createTitledBorder(title));
		return new PanelLayoutPair(panel, layout);
	}
	
	private JPanel createParametersPanel() {
		///////////////////////////////////////////////////
		//		Input file panel
		JLabel inputFileLabel = new JLabel("Input file");
		JTextField inputFileTextField = new JTextField();
		JButton setInputFileButton = new JButton("Set");
		
		PanelLayoutPair inputFilePanelLayoutPair = createPanel("");
		
		JPanel inputFilePanel = inputFilePanelLayoutPair.panel;
		GroupLayout inputFileLayout = inputFilePanelLayoutPair.layout;
		
		inputFileLayout.setHorizontalGroup(inputFileLayout.createSequentialGroup()
				.addComponent(inputFileLabel)
				.addComponent(inputFileTextField)
				.addComponent(setInputFileButton));
		inputFileLayout.setVerticalGroup(inputFileLayout.createSequentialGroup()
				.addGroup(inputFileLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(inputFileLabel)
						.addComponent(inputFileTextField)
						.addComponent(setInputFileButton)));

//////////////////////////////////////////////////////////
//		Output dir panel
		JLabel outputDirLabel = new JLabel("Output directory");
		JTextField outputDirTextField = new JTextField();
		JButton setOutputDirButton = new JButton("Set");
		
		PanelLayoutPair outputDirPanelLayoutPair = createPanel("");
		JPanel outputDirPanel = outputDirPanelLayoutPair.panel;
		GroupLayout outputDirLayout = outputDirPanelLayoutPair.layout;
		
		outputDirLayout.setHorizontalGroup(outputDirLayout.createSequentialGroup()
				.addComponent(outputDirLabel)
				.addComponent(outputDirTextField)
				.addComponent(setOutputDirButton));
		outputDirLayout.setVerticalGroup(outputDirLayout.createSequentialGroup()
				.addGroup(outputDirLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(outputDirLabel)
						.addComponent(outputDirTextField)
						.addComponent(setOutputDirButton)));
		

//////////////////////////////////////////////////////////
//		Output file panel
		JLabel outputFileLabel = new JLabel("Output file");
		JTextField outputFileTextField = new JTextField();

		PanelLayoutPair outputFilePanelLayoutPair = createPanel("");
		JPanel outputFilePanel = outputFilePanelLayoutPair.panel;
		GroupLayout outputFileLayout = outputFilePanelLayoutPair.layout;
		
		outputFileLayout.setHorizontalGroup(outputFileLayout.createSequentialGroup()
				.addComponent(outputFileLabel)
				.addComponent(outputFileTextField));
		outputFileLayout.setVerticalGroup(outputFileLayout.createSequentialGroup()
				.addGroup(outputFileLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(outputFileLabel)
						.addComponent(outputFileTextField)));

		///////////////////////////////////////////////////////////
		//		Forth parameters panel
		JLabel CFLowLabel = new JLabel("CF Low");
		JLabel CFHighLabel = new JLabel("CF High");
		JLabel CompVLowLabel = new JLabel("Comp V Low");
		JLabel CompVHighLabel = new JLabel("Comp V High");
		JLabel SigmaMinLabel = new JLabel("Sigma Min");
		JLabel CMinLabel = new JLabel("C Min");
		JSpinner CFLowSpinner = new JSpinner();
		JSpinner CFHighSpinner = new JSpinner();
		JSpinner CompVLowSpinner = new JSpinner();
		JSpinner CompVHighSpinner = new JSpinner();
		JSpinner SigmaMinSpinner = new JSpinner();
		JSpinner CMinSpinner = new JSpinner();
		JCheckBox bbCompactnessCheckBox = new JCheckBox("Use BB compactness");
		
		JPanel forthParameterPanel = new JPanel();
		GroupLayout forthParameterPanelLayout = new GroupLayout(forthParameterPanel);
		forthParameterPanel.setLayout(forthParameterPanelLayout);
		forthParameterPanelLayout.setAutoCreateGaps(true);
		forthParameterPanelLayout.setAutoCreateContainerGaps(true);
		forthParameterPanel.setBorder(BorderFactory.createTitledBorder("Forth parameters"));

		forthParameterPanelLayout.setHorizontalGroup(
				forthParameterPanelLayout.createSequentialGroup()
					.addGroup(forthParameterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(CFLowLabel)
							.addComponent(CFHighLabel)
							.addComponent(bbCompactnessCheckBox))
					.addGroup(forthParameterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(CFLowSpinner)
							.addComponent(CFHighSpinner))
					.addGroup(forthParameterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(CompVLowLabel)
							.addComponent(CompVHighLabel))
					.addGroup(forthParameterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(CompVLowSpinner)
							.addComponent(CompVHighSpinner))
					.addGroup(forthParameterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(SigmaMinLabel)
							.addComponent(CMinLabel))
					.addGroup(forthParameterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(SigmaMinSpinner)
							.addComponent(CMinSpinner)));
		
		forthParameterPanelLayout.setVerticalGroup(
				forthParameterPanelLayout.createSequentialGroup()
					.addGroup(forthParameterPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(CFLowLabel)
							.addComponent(CFLowSpinner)
							.addComponent(CompVLowLabel)
							.addComponent(CompVLowSpinner)
							.addComponent(SigmaMinLabel)
							.addComponent(SigmaMinSpinner))
					.addGroup(forthParameterPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(CFHighLabel)
							.addComponent(CFHighSpinner)
							.addComponent(CompVHighLabel)
							.addComponent(CompVHighSpinner)
							.addComponent(CMinLabel)
							.addComponent(CMinSpinner))
					.addComponent(bbCompactnessCheckBox));
				
		////////////////////////////////////////////////////////////////////////////////
		//		Basic algorithm panel
		JRadioButton COSIATECRadioButton = new JRadioButton("COSIATEC");
		JRadioButton SIATECCompressRadioButton = new JRadioButton("SIATECCompress");
		JRadioButton ForthRadioButton = new JRadioButton("Forth");
		JRadioButton SIARadioButton = new JRadioButton("SIA");
		JRadioButton SIATECRadioButton = new JRadioButton("SIATEC");
		JRadioButton ScaleXIARadioButton = new JRadioButton("ScaleXIA");
		ButtonGroup basicAlgorithmButtonGroup = new ButtonGroup();
		basicAlgorithmButtonGroup.add(COSIATECRadioButton);
		basicAlgorithmButtonGroup.add(SIATECCompressRadioButton);
		basicAlgorithmButtonGroup.add(ForthRadioButton);
		basicAlgorithmButtonGroup.add(SIARadioButton);
		basicAlgorithmButtonGroup.add(SIATECRadioButton);
		basicAlgorithmButtonGroup.add(ScaleXIARadioButton);

		JPanel basicAlgorithmPanel = new JPanel();
		GroupLayout basicAlgorithmLayout = new GroupLayout(basicAlgorithmPanel);
		basicAlgorithmPanel.setLayout(basicAlgorithmLayout);
		basicAlgorithmLayout.setAutoCreateGaps(true);
		basicAlgorithmLayout.setAutoCreateContainerGaps(true);
		basicAlgorithmPanel.setBorder(BorderFactory.createTitledBorder("Basic algorithm"));

		basicAlgorithmLayout.setHorizontalGroup(
				basicAlgorithmLayout.createSequentialGroup()
					.addGroup(basicAlgorithmLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(COSIATECRadioButton)
							.addComponent(SIATECCompressRadioButton)
							.addComponent(SIARadioButton)
							.addComponent(SIATECRadioButton)
							.addComponent(ScaleXIARadioButton)
							.addComponent(ForthRadioButton))
					.addComponent(forthParameterPanel));
		
		basicAlgorithmLayout.setVerticalGroup(
				basicAlgorithmLayout.createSequentialGroup()
					.addComponent(COSIATECRadioButton)
					.addComponent(SIATECCompressRadioButton)
					.addComponent(SIARadioButton)
					.addComponent(SIATECRadioButton)
					.addComponent(ScaleXIARadioButton)
					.addGroup(basicAlgorithmLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(ForthRadioButton)
							.addComponent(forthParameterPanel)));

		////////////////////////////////////////////////////////////////////////////////
		//		Mode group
		JLabel modeLabel = new JLabel("Mode");
		JRadioButton rawRadioButton = new JRadioButton("Raw");
		JRadioButton bbRadioButton = new JRadioButton("Bounding box (BB)");
		JRadioButton segmentRadioButton = new JRadioButton("Segment");
		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(rawRadioButton);
		modeGroup.add(bbRadioButton);
		modeGroup.add(segmentRadioButton);

		//		Compactness trawler
		JCheckBox compactnessTrawlerCheckBox = new JCheckBox("Compactness trawler");
		JLabel ctaLabel = new JLabel("Minimum subpattern compactness (a):");
		JSpinner ctaSpinner = new JSpinner();
		JLabel ctbLabel = new JLabel("Minimum subpattern size (b):");
		JSpinner ctbSpinner = new JSpinner();

		//		SIAR
		JCheckBox SIARCheckBox = new JCheckBox("Use SIAR instead of SIA");
		JLabel rLabel = new JLabel("Number of superdiagonals (r):");
		JSpinner rSpinner = new JSpinner();

		//		Merge TECs
		JCheckBox mergeTECsCheckBox = new JCheckBox("Merge TECs");
		JLabel minMatchSizeLabel = new JLabel("Minimum match size:");
		JSpinner minMatchSizeSpinner = new JSpinner();
		JLabel numIterationsLabel = new JLabel("Number of iterations");
		JSpinner numIterationsSpinner = new JSpinner();

		//		Other check boxes
		JCheckBox omitChannelTenCheckBox = new JCheckBox("Omit channel 10");
		JCheckBox diatonicPitchCheckBox = new JCheckBox("Diatonic pitch");
		JCheckBox MIREXFormatCheckBox = new JCheckBox("MIREX format output");
		JCheckBox RecurSIACheckBox = new JCheckBox("RecurSIA");
		JCheckBox RRTCheckBox = new JCheckBox("Remove redundant translators (RRT)");
		JCheckBox sortByPatternSizeCheckBox = new JCheckBox("Sort by pattern size");

		JSpinner minTECCompactnessSpinner = new JSpinner();
		JSpinner minPatternSizeSpinner = new JSpinner();

		JCheckBox maxPatternSizeCheckBox = new JCheckBox("Max pattern size:");
		JSpinner maxPatternSizeSpinner = new JSpinner();
		JCheckBox topNPatternsCheckBox = new JCheckBox("Max number of patterns:");
		JSpinner maxNPatternsSpinner = new JSpinner();

		JButton analyseButton = new JButton("Analyse");

		JPanel parametersPanel = new JPanel();
		GroupLayout layout = new GroupLayout(parametersPanel);
		parametersPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		//		Define horizontal layout
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(inputFilePanel)
								.addComponent(outputDirPanel)
								.addComponent(outputFilePanel)
								.addComponent(basicAlgorithmPanel)
								.addComponent(modeLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(rawRadioButton)
										.addComponent(bbRadioButton)
										.addComponent(segmentRadioButton)))
				));
		//		Define vertical layout
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(inputFilePanel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(outputDirPanel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(outputFilePanel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(basicAlgorithmPanel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(modeLabel)
						.addComponent(rawRadioButton)
						.addComponent(bbRadioButton)
						.addComponent(segmentRadioButton)));
		return parametersPanel;
	}

	private JPanel createStatisticsPanel() {
		JPanel panel = new JPanel();
		return panel;
	}

	public OmnisiaGui () {
		super();
		GUI = this;
		setMinimumSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,java.awt.Toolkit.getDefaultToolkit().getScreenSize().height));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//		GUI.getContentPane().add(createDrawPointsPanel());
		//		GUI.getContentPane().add(createStatisticsPanel());
		GUI.getContentPane().add(createParametersPanel());
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new OmnisiaGui();
			}
		});
	}

}
