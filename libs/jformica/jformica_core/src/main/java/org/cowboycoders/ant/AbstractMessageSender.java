package org.cowboycoders.ant;

import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;

public abstract class AbstractMessageSender implements MessageSender {
	
	
	private Receipt receipt;

	public AbstractMessageSender(Receipt receipt) {
		this.receipt = receipt;
	}

	@Override
	public void postSend(MessageMetaWrapper<StandardMessage> msg) {
		if (receipt != null) {
			receipt.addSent(msg);
		}
		
	}

	@Override
	public void doSend(StandardMessage msg) {
		this.postSend(this.send(msg));
		
	}

}
