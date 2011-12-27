package com.willmeyer.jfusionbrain;

import java.util.Arrays;

import org.junit.*;

import com.willmeyer.jfusionbrain.FusionBrainV3.InputVals;

public class TestBrain 
{
	public FusionBrainV3 brain = null;
	
	private void setupDevice() throws Exception {
		brain = new FusionBrainV3();
		brain.initDevice(0);
    }

	private void shutdownDevice() throws Exception {
		brain.shutdownDevice();
		brain = null;
    }

	@Before
    public void beforeTest() throws Exception {
		this.setupDevice();
    }

	@After
    public void afterTest() throws Exception {
		this.shutdownDevice();
    }

	@Test
	public void testConnectDisconnect() throws Exception 
    {
		// The pre and post test steps do this...
    }

	@Test
	public void testDigitalOutChristmasTree() throws Exception 
    {
		boolean[] digitalOuts = new boolean[12];
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 12; j++) {
				digitalOuts[j] = (Math.round(Math.random() * 1.0) == 1);
			}
			Thread.sleep(100);
			InputVals inputs = brain.setAndGet(digitalOuts);
			inputs.dump(System.out);
		}
    }

	@Test
	public void testDigitalIO() throws Exception 
    {
		boolean[] digitalOuts = {true, false, true, false, true, false, true, false, true, false, true, false};
		InputVals inputs = brain.setAndGet(digitalOuts);
		Assert.assertEquals(Arrays.equals(inputs.digitalInputs, digitalOuts), "digital IO");
    }
}
