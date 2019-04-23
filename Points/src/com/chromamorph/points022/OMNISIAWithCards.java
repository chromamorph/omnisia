package com.chromamorph.points022;


import javax.sound.midi.InvalidMidiDataException;
import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;












/**
 * OMNISIA
 * @author David Meredith
 * 
 * Loads interface that allows running of all SIA family
 * of algorithms
 *
 */
/*
 * CardLayoutDemo.java
 *
 */
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OMNISIAWithCards implements ItemListener, ActionListener, ChangeListener {
	JPanel cards; //a panel that uses CardLayout
	final static String SIA = "SIA";
	final static String SIATEC = "SIATEC";
	final static String COSIATEC = "COSIATEC";
	final static String FORTH = "FORTH";
	final static String SIACT = "SIACT";
	final static String SIATECCompress = "SIATECCompress";
	final static String SIAScale = "SIAScale";
	final static String comboBoxItems[] = { SIA, SIATEC, COSIATEC, FORTH, SIACT, SIATECCompress, SIAScale};
	static String chromaticPitchString = "Chromatic Pitch";
	static String morpheticPitchString = "Morphetic Pitch";
	static String inputFilePathName = null;
	static String outputDirectoryPathName = null;
	static int minPatternSize = 0;
	static PitchRepresentation pitchRepresentation = PitchRepresentation.MORPHETIC_PITCH;
	static JButton runButton = null;
	static JRadioButton morpheticPitchRadioButton = null;
	static JRadioButton chromaticPitchRadioButton = null; 
	static JSpinner minPatSizeSpinner = null;
	static JTextField inputFileTextField = null;
	static JTextField outputDirectoryTextField = null;
	static JButton chooseInputFileButton = null;
	static JButton chooseOutputDirectoryButton = null;
	static JCheckBox drawOutputCheckBox = null;
	static boolean drawOutput = false;

	private JPanel createInputFilePanel() {
		JPanel inputFilePanel = new JPanel();
		GroupLayout layout = new GroupLayout(inputFilePanel);
		inputFilePanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		inputFilePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Input file"));
		inputFileTextField = new JTextField(40);
		inputFileTextField.setEditable(false);
		inputFileTextField.addActionListener(this);
		chooseInputFileButton = new JButton("Choose file...");
		chooseInputFileButton.addActionListener(this);

		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addComponent(inputFileTextField)
				.addComponent(chooseInputFileButton));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(inputFileTextField)
						.addComponent(chooseInputFileButton)));

		return inputFilePanel;
	}

	private JPanel createOutputDirectoryPanel() {
		JPanel outputDirectoryPanel = new JPanel();
		GroupLayout layout = new GroupLayout(outputDirectoryPanel);
		outputDirectoryPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		outputDirectoryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Output directory"));
		outputDirectoryTextField = new JTextField(40);
		outputDirectoryTextField.setEditable(false);
		outputDirectoryTextField.addActionListener(this);
		chooseOutputDirectoryButton = new JButton("Choose directory...");
		chooseOutputDirectoryButton.addActionListener(this);

		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addComponent(outputDirectoryTextField)
				.addComponent(chooseOutputDirectoryButton));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(outputDirectoryTextField)
						.addComponent(chooseOutputDirectoryButton)));

		return outputDirectoryPanel;
	}

	private JPanel createParametersPanel() {
		JPanel parametersPanel = new JPanel();
		GroupLayout layout = new GroupLayout(parametersPanel);
		parametersPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		parametersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Parameters"));

		JLabel minPatternSizeLabel = new JLabel("Minimum pattern size: ");
		minPatSizeSpinner = new JSpinner(new SpinnerNumberModel(0,0,1000000,1));
		minPatSizeSpinner.addChangeListener(this);
		Dimension maximumSize = new Dimension(100,50);
		minPatSizeSpinner.setMaximumSize(maximumSize);

		morpheticPitchRadioButton = new JRadioButton(morpheticPitchString);
		morpheticPitchRadioButton.addActionListener(this);
		morpheticPitchRadioButton.setActionCommand(morpheticPitchString);
		morpheticPitchRadioButton.setSelected(true);

		chromaticPitchRadioButton = new JRadioButton(chromaticPitchString);
		chromaticPitchRadioButton.setActionCommand(chromaticPitchString);
		chromaticPitchRadioButton.addActionListener(this);

		ButtonGroup group = new ButtonGroup();
		group.add(morpheticPitchRadioButton);
		group.add(chromaticPitchRadioButton);

		morpheticPitchRadioButton.addActionListener(this);
		chromaticPitchRadioButton.addActionListener(this);

		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(minPatternSizeLabel)
						.addComponent(morpheticPitchRadioButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(minPatSizeSpinner)
								.addComponent(chromaticPitchRadioButton)));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(minPatternSizeLabel)
						.addComponent(minPatSizeSpinner))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(morpheticPitchRadioButton)
								.addComponent(chromaticPitchRadioButton)));

		return parametersPanel;
	}

	private JPanel createOptionsPanel() {
		JPanel optionsPanel = new JPanel();
		GroupLayout layout = new GroupLayout(optionsPanel);
		optionsPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Options"));
		drawOutputCheckBox = new JCheckBox("Draw output");
		drawOutputCheckBox.addActionListener(this);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(drawOutputCheckBox));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(drawOutputCheckBox));

		return optionsPanel;

	}
	
	private JPanel createButtonPanel(String algorithm) {
		JPanel buttonPanel = new JPanel();
		runButton = new JButton("Run");
		runButton.setActionCommand(algorithm);
		runButton.addActionListener(this);
		buttonPanel.add(runButton);
		return buttonPanel;
	}
	
	private JPanel createSIACard() {
		JPanel card = new JPanel();
		GroupLayout layout = new GroupLayout(card); 
		card.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		JPanel inputFilePanel = createInputFilePanel();
		JPanel outputDirectoryPanel = createOutputDirectoryPanel();
		JPanel parametersPanel = createParametersPanel();
		JPanel optionsPanel = createOptionsPanel();
		JPanel buttonPanel = createButtonPanel(SIA);

		//Run and Cancel buttons


		layout.linkSize(SwingConstants.HORIZONTAL, inputFilePanel,outputDirectoryPanel,parametersPanel,optionsPanel);

		//Add components to card
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(inputFilePanel)
						.addComponent(outputDirectoryPanel)
						.addComponent(parametersPanel)
						.addComponent(optionsPanel)
						.addComponent(buttonPanel)));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(inputFilePanel)
				.addComponent(outputDirectoryPanel)
				.addComponent(parametersPanel)
				.addComponent(optionsPanel)
				.addComponent(buttonPanel));
		return card;
	}

	private JPanel createSIATECCard() {
		JPanel card = new JPanel();
		GroupLayout layout = new GroupLayout(card); 
		card.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		JPanel inputFilePanel = createInputFilePanel();
		JPanel outputDirectoryPanel = createOutputDirectoryPanel();
		JPanel parametersPanel = createParametersPanel();
		JPanel optionsPanel = createOptionsPanel();
		JPanel buttonPanel = createButtonPanel(SIATEC);

		//Run and Cancel buttons


		layout.linkSize(SwingConstants.HORIZONTAL, inputFilePanel,outputDirectoryPanel,parametersPanel,optionsPanel);

		//Add components to card
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(inputFilePanel)
						.addComponent(outputDirectoryPanel)
						.addComponent(parametersPanel)
						.addComponent(optionsPanel)
						.addComponent(buttonPanel)));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(inputFilePanel)
				.addComponent(outputDirectoryPanel)
				.addComponent(parametersPanel)
				.addComponent(optionsPanel)
				.addComponent(buttonPanel));
		return card;
	}

	private JPanel createCOSIATECCard() {
		JPanel card = new JPanel();
		GroupLayout layout = new GroupLayout(card); 
		card.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		JPanel inputFilePanel = createInputFilePanel();
		JPanel outputDirectoryPanel = createOutputDirectoryPanel();
		JPanel parametersPanel = createParametersPanel();
		JPanel optionsPanel = createOptionsPanel();
		JPanel buttonPanel = createButtonPanel(COSIATEC);

		//Run and Cancel buttons


		layout.linkSize(SwingConstants.HORIZONTAL, inputFilePanel,outputDirectoryPanel,parametersPanel,optionsPanel);

		//Add components to card
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(inputFilePanel)
						.addComponent(outputDirectoryPanel)
						.addComponent(parametersPanel)
						.addComponent(optionsPanel)
						.addComponent(buttonPanel)));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(inputFilePanel)
				.addComponent(outputDirectoryPanel)
				.addComponent(parametersPanel)
				.addComponent(optionsPanel)
				.addComponent(buttonPanel));
		return card;
	}


	public void addComponentToPane(Container pane) {
		//Put the JComboBox in a JPanel to get a nicer look.
		JPanel comboBoxPane = new JPanel(); //use FlowLayout
		JComboBox cb = new JComboBox(comboBoxItems);
		cb.setEditable(false);
		cb.addItemListener(this);
		comboBoxPane.add(cb);

		//Create the "cards".
		JPanel SIACard = createSIACard();
		JPanel SIATECCard = createSIATECCard();
//		JPanel COSIATECCard = createCOSIATECCard();
		
		//Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(SIACard, SIA);
		cards.add(SIATECCard, SIATEC);
//		cards.add(COSIATECCard, COSIATEC);

		pane.add(comboBoxPane, BorderLayout.PAGE_START);
		pane.add(cards, BorderLayout.CENTER);
	}

	public void itemStateChanged(ItemEvent evt) {
		CardLayout cl = (CardLayout)(cards.getLayout());
		cl.show(cards, (String)evt.getItem());
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("OMNISIA");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		OMNISIAWithCards demo = new OMNISIAWithCards();
		demo.addComponentToPane(frame.getContentPane());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		/* Use an appropriate Look and Feel */
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource().equals(minPatSizeSpinner))
			respondToMinPatSizeSpinner();
	}
	
	private void respondToMinPatSizeSpinner() {
		minPatternSize = (Integer)minPatSizeSpinner.getValue();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(runButton))
			try {
				if (event.getActionCommand().equals(SIA))
					respondToSiaRunButton();
				else if (event.getActionCommand().equals(SIATEC))
					respondToSiatecRunButton();
				else if (event.getActionCommand().equals(COSIATEC))
					respondToCosiatecRunButton();
			} catch (NoMorpheticPitchException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnimplementedInputFileFormatException e) {
				e.printStackTrace();
			} catch (InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else if (event.getSource().equals(inputFileTextField))
			respondToInputFileTextField();
		else if (event.getSource().equals(chooseInputFileButton))
			respondToChooseInputFileButton();
		else if (event.getSource().equals(outputDirectoryTextField))
			respondToOutputDirectoryTextField();
		else if (event.getSource().equals(chooseOutputDirectoryButton))
			respondToChooseOutputDirectoryButton();
		else if (event.getSource().equals(morpheticPitchRadioButton))
			respondToMorpheticPitchRadioButton();
		else if (event.getSource().equals(chromaticPitchRadioButton))
			respondToChromaticPitchRadioButton();
		else if (event.getSource().equals(drawOutputCheckBox))
			respondToDrawOutputCheckBox();
	}
	
	private void respondToDrawOutputCheckBox() {
		if (drawOutputCheckBox.isSelected())
			drawOutput = true;
		else
			drawOutput = false;
	}
	
	private void respondToMorpheticPitchRadioButton() {
		if (morpheticPitchRadioButton.isSelected())
			pitchRepresentation = PitchRepresentation.MORPHETIC_PITCH;
	}
	
	private void respondToChromaticPitchRadioButton() {
		if (chromaticPitchRadioButton.isSelected())
			pitchRepresentation = PitchRepresentation.CHROMATIC_PITCH;
	}
	
	private void respondToOutputDirectoryTextField() {
		outputDirectoryPathName = outputDirectoryTextField.getText();
	}
	
	private void respondToChooseOutputDirectoryButton() {
		JFileChooser outputFolderChooser = new JFileChooser("output");
		outputFolderChooser.setDialogTitle("Choose output folder");
		outputFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal2 = outputFolderChooser.showOpenDialog(null);
		if (returnVal2 != JFileChooser.APPROVE_OPTION) return;
		outputDirectoryPathName = outputFolderChooser.getSelectedFile().getAbsolutePath();
		outputDirectoryTextField.setText(outputDirectoryPathName);
	}
	private void respondToInputFileTextField() {
		inputFilePathName = inputFileTextField.getText();
	}
	
	private void respondToChooseInputFileButton() {
		JFileChooser chooser = new JFileChooser("data");
		chooser.setDialogTitle("Choose input file");
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) return;
		inputFilePathName = chooser.getSelectedFile().getAbsolutePath();
		inputFileTextField.setText(inputFilePathName);
	}

	private void respondToSiaRunButton() throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException {
		try {
			new SIAEncoding(inputFilePathName, outputDirectoryPathName, minPatternSize, pitchRepresentation, drawOutput);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void respondToSiatecRunButton() throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException {
		try {
			new SIATECEncoding(inputFilePathName, outputDirectoryPathName, minPatternSize, pitchRepresentation, drawOutput);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void respondToCosiatecRunButton() throws NoMorpheticPitchException, IOException, UnimplementedInputFileFormatException, InvalidMidiDataException {
		try {
			new COSIATECEncoding(inputFilePathName, outputDirectoryPathName, minPatternSize, pitchRepresentation, drawOutput);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
