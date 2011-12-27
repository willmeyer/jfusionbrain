package com.willmeyer.jfusionbrain;

import com.icaste.JCommUSB_3_0.*;

import java.io.*;

import org.slf4j.*;

public final class FusionBrainV3 {

	public static final int NUM_ANALOG_INPUTS = 10;
	public static final int NUM_DIGITAL_INPUTS = 4;
	
	protected boolean flip = false;
	protected USBDevice device = null; 
	protected final Logger logger = LoggerFactory.getLogger(FusionBrainV3.class);
	
	public static class InputVals {
		
		public AnalogInput[] analogInputs = new AnalogInput[NUM_ANALOG_INPUTS];
		public boolean[] digitalInputs = new boolean[NUM_DIGITAL_INPUTS];
		
		public static class AnalogInput {
			public int valueUnscaled; // range 0 to 1023 inclusive
			public double valueScaled; // range 0.0 to 5.0V, inclusive
		}
		
		public void dump(PrintStream print) {
			for (int i = 0; i < NUM_ANALOG_INPUTS; i++) {
				print.println("ANALOG " + i + ": " + analogInputs[i].valueUnscaled + "/" + analogInputs[i].valueScaled + " V");
			}
			for (int i = 0; i < NUM_DIGITAL_INPUTS; i++) {
				print.println("DIGITAL " + i + ": " + digitalInputs[i]);
			}
		}
	}
	
	/**
	 * Sets the state of all outputs, gets the state of all inputs.
	 */
	public InputVals setAndGet(boolean[] digitalOuts) throws Exception {
		if (device == null) {
			throw new IllegalStateException("Device not initialized.");
		}
		byte[] bytesOut = new byte[64];
		
		// Init
		for (int i = 0; i < 64; i++) {
			bytesOut[i] = 0;
		}
		bytesOut[61] = (byte)255; // some kind of wakeup thing...

		// Set digital outs, with the flip bit to keep timing intact
		flip = !flip;
		for (int i = 0; i < 12; i++) {
			bytesOut[i] = digitalOuts[i] ? (byte)0x01 : (byte)0x00;
			if (flip) bytesOut[i] |= 0x02;
		}
		logger.debug("Writing 64 bytes to interface 0...");
		int numWritten = device.writePipeBulkInterrupt(0, 0, bytesOut, 0, 64);
		logger.debug("Wrote {} bytes", new Integer(numWritten));
		byte[] bytesIn = new byte[64];
		InputVals inputs = new InputVals();
		logger.debug("Reading 64 bytes from interface 0...");
		device.readPipeBulkInterrupt(0, 1, bytesIn, 0, 64);
		
		// Figure out the analog input values
		final int analogInputsBeginIndex = 12;
		for (int portNum = 0; portNum < NUM_ANALOG_INPUTS; portNum++) {
		    InputVals.AnalogInput input = new InputVals.AnalogInput();
			int byteIndex = analogInputsBeginIndex + 2 * portNum;
		    input.valueUnscaled |= bytesIn[byteIndex];
		    input.valueUnscaled |= ((bytesIn[byteIndex + 1] & 3) << 8);
		    input.valueScaled = (input.valueUnscaled / 1023.0) * 5.0;
		    inputs.analogInputs[portNum] = input;
		}
		
		// Digital inputs, all in byte 32
		int digitalInputByte = bytesIn[32];
		int mask = 64;
		for (int portNum = 0; portNum < NUM_DIGITAL_INPUTS; portNum++) {
		    if ((digitalInputByte & mask) == 0)
		        inputs.digitalInputs[portNum] = false;
		    else
		        inputs.digitalInputs[portNum] = true;
		    mask >>= 1;
		}

		// Done
		return inputs;
	}
	
	public FusionBrainV3() {
	}
	
	public void shutdownDevice() {
		logger.info("Shutting down device...");
		if (device == null) 
			return;
		try {
			device.deviceReset();
		} catch (Exception e) {
			logger.error("Error shutting down device", e);
		}
		device = null;
	}
	
	public void initDevice(int deviceIndex) throws Exception {
		try {
			logger.info("Initializing device at index {}", new Integer(deviceIndex));
			String path = USBDevice.getAttachedDevicePath(deviceIndex);
			logger.info("Device path is {}", path);
			device = new USBDevice(path);
			device.deviceReset();
		} catch (Exception e) {
			logger.error("Unable to initialize device", e);
			throw e;
		}
	}
	
	/**
	 * When run directly, sets all the outputs to on, and dumps all the inputs
	 */
	public static void main(String[] args) {
		try {
			int index = 0;
			System.out.println("Initializing device at index " + index);
			FusionBrainV3 brain = new FusionBrainV3();
			brain.initDevice(index);
			System.out.println("Turning all outputs ON");
			boolean[] digitalOuts = new boolean[12];
			for (int i = 0; i < 12; i++) { digitalOuts[i] = true; }
			InputVals inputs = brain.setAndGet(digitalOuts);
			System.out.println("Displaying inputs:");
			inputs.dump(System.out);
			System.out.println("Shutting down device");
			brain.shutdownDevice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
