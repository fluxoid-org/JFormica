package org.cowboycoders.ant.examples.api;

import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.interfaces.AntDeviceId;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.responses.Capability;

public class ChooseUsbDevice {
	
	public static void main(String [] args) {
		
		// Uncomment one of the following:
		
		// choose first ANTUSB_2 device
		AntTransceiver antchip = new AntTransceiver(AntDeviceId.ANTUSB_2,0);
		
		// choose first ANTUSB_M device
		//AntTransceiver antchip = new AntTransceiver(AntDeviceId.ANTUSB_M,0);
		
		// search for all known devices and select first device found
		//AntTransceiver antchip = new AntTransceiver(0);
		
		Node node = new Node(antchip);
		
		node.start();
		
		for (Capability c : node.getCapabiltites()) {
			System.out.println(c);
		}
		
		node.stop();
	}
	


}
