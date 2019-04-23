package com.chromamorph.midi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

/**
 * Recorder Class
 * 
 * @author David Meredith
 * 
 *         This class allows streamed MIDI input to be recorded. The user
 *         chooses which MIDI input port to use and when to start and stop
 *         recording. While recording, all MIDI data received from the chosen
 *         source is stored into a Sequence object which is then
 *         stored to a MIDI file.
 * 
 */
public class RecordMIDI {

	private Sequence sequence = null;


	public RecordMIDI() {
		Scanner sc = new Scanner(System.in);

		//Get information on all MIDI input devices available
		MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
		ArrayList<MidiDevice.Info> validDeviceInfos = new ArrayList<MidiDevice.Info>();
		for (MidiDevice.Info info : midiDeviceInfos) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice(info);
				device.open();
				device.close();
				validDeviceInfos.add(info);
			} catch (MidiUnavailableException e) {
			} catch (ClassCastException e) {
			}
		}
		for (int i = 0; i < validDeviceInfos.size(); i++) {
			System.out.println(i+1+". "+validDeviceInfos.get(i).getDescription()+", "+validDeviceInfos.get(i).getClass().getCanonicalName());
		}
		try {
			System.out.print("Select an input device from the above list and press ENTER:");
			int deviceNumber = sc.nextInt(); sc.nextLine();
			MidiDevice inputDevice = MidiSystem.getMidiDevice(validDeviceInfos.get(deviceNumber - 1));
			System.out.println(inputDevice.getDeviceInfo().getDescription()+" selected as input device");
			
			System.out.print("Now select a sequencer device from the above list and press ENTER:");
			deviceNumber = sc.nextInt(); sc.nextLine();
			Sequencer sequencer = (Sequencer)MidiSystem.getMidiDevice(validDeviceInfos.get(deviceNumber - 1));
			System.out.println(sequencer.getDeviceInfo().getDescription()+" selected as sequencer");
			
			System.out.print("Now select a synthesizer device from the above list and press ENTER:");
			deviceNumber = sc.nextInt(); sc.nextLine();
			Synthesizer synth = (Synthesizer)MidiSystem.getMidiDevice(validDeviceInfos.get(deviceNumber - 1));
			System.out.println(synth.getDeviceInfo().getDescription()+" selected as synthesizer");

			System.out.print("Now select an output device from the above list and press ENTER:");
			deviceNumber = sc.nextInt(); sc.nextLine();
			MidiDevice outputDevice = MidiSystem.getMidiDevice(validDeviceInfos.get(deviceNumber - 1));
			System.out.println(outputDevice.getDeviceInfo().getDescription()+" selected as output device");

			
			inputDevice.open();
			sequencer.open();
			Transmitter inputTransmitter = inputDevice.getTransmitter();
			Receiver sequencerReceiver = sequencer.getReceiver();
			inputTransmitter.setReceiver(sequencerReceiver);
			
			sequence = new Sequence(Sequence.PPQ,256,1);
			sequencer.setSequence(sequence);
			sequencer.recordEnable(sequence.getTracks()[0], -1);
			
			System.out.println("Press ENTER when you're ready to start recording.");
			sc.nextLine();
			sequencer.startRecording();
			if (sequencer.isRecording())
				System.out.println("Press ENTER when you're ready to stop recording.");
			sc.nextLine();
			sequencer.stopRecording();
			if (!sequencer.isRecording())
				System.out.println("Recording has stopped.");
			inputTransmitter.close();
			sequencerReceiver.close();
			
			//Check timestamp of earliest event in Sequence. Subtract this value from all events in the sequence.
			
			Track[] tracks = sequence.getTracks();
			Long earliestTick = null;
			for(int trackNumber = 0; trackNumber < tracks.length; trackNumber++) {
				for(int eventNumber = 0; eventNumber < tracks[trackNumber].size(); eventNumber++) {
					long thisTick = tracks[trackNumber].get(eventNumber).getTick();
					if (earliestTick == null)
						earliestTick = thisTick;
					else
						if (thisTick < earliestTick)
							earliestTick = thisTick;
				}
			}
			System.out.println("Earliest tick is: " + earliestTick);

			for(int trackNumber = 0; trackNumber < tracks.length; trackNumber++) {
				for(int eventNumber = 0; eventNumber < tracks[trackNumber].size(); eventNumber++) {
					long thisTick = tracks[trackNumber].get(eventNumber).getTick();
					tracks[trackNumber].get(eventNumber).setTick(thisTick-earliestTick);
				}
			}

			sequencer.recordDisable(sequence.getTracks()[0]);
			
			Transmitter seqTrans = sequencer.getTransmitter();
			synth.open();
			Receiver synthReceiver = synth.getReceiver();
			seqTrans.setReceiver(synthReceiver);
			sequencer.start();
			System.out.println("Press ENTER when finished playing.");
			sc.nextLine();
			sequencer.stop();
			seqTrans.close();
			synthReceiver.close();

			sequencer.setTickPosition(0l);
			Transmitter seqTrans2 = sequencer.getTransmitter();
			outputDevice.open();
			Receiver outputReceiver = outputDevice.getReceiver();
			seqTrans2.setReceiver(outputReceiver);
			sequencer.start();
			System.out.println("Press ENTER when finished playing.");
			sc.nextLine();
			sequencer.stop();
			seqTrans2.close();
			outputReceiver.close();

			//Store sequence to a file
			System.out.println("Please enter a filename for the output MIDI file: ");
			String midiFileName = sc.nextLine();
			File midiFile = new File(midiFileName);
			MidiSystem.write(sequence, 0, midiFile);
			
			inputDevice.close();
			System.out.println("Input device closed");
			sequencer.close();
			System.out.println("Sequencer closed");
			synth.close();
			System.out.println("Synthesizer closed");
			outputDevice.close();
			System.out.println("Output device closed");
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sc.close();
		System.out.println("Scanner closed");
	}

	public static void main(String[] args) {
		RecordMIDI recorder = new RecordMIDI();
	}
}
