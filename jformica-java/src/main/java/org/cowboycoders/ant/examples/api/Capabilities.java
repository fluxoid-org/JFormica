package org.cowboycoders.ant.examples.api;

import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.responses.Capability;

public class Capabilities {
	
	public static void main(String[] args) {
		AntTransceiver antchip = new AntTransceiver(0);
		Node node = new Node(antchip);
		
		node.start();
		
		for (Capability c : node.getCapabiltites()) {
			System.out.println(c);
		}
		
		node.stop();
		
	}

}
