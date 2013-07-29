package org.cowboycoders.ant;

/**
 * Default, do nothing implementation of {@link ChannelEventHandler}. Override methods
 * Associated with events you care about.
 * @author will
 *
 */
public class DefaultChannelEventHandler extends ChannelEventHandler {

	@Override
	public void onTransferNextDataBlock() {
		// Do nothing
		
	}

	@Override
	public void onTransferTxStart() {
		// Do nothing
	}

	@Override
	public void onTransferTxCompleted() {
		// Do nothing
	}

	@Override
	public void onTransferTxFailed() {
		// Do nothing
		
	}

	@Override
	public void onChannelClosed() {
		// Do nothing
		
	}

	@Override
	public void onRxFailGoToSearch() {
		// Do nothing
		
	}

	@Override
	public void onChannelCollision() {
		// Do nothing
		
	}

	@Override
	public void onTransferRxFailed() {
		// Do nothing
		
	}

	@Override
	public void onRxSearchTimeout() {
		// Do nothing
		
	}

	@Override
	public void onRxFail() {
		// Do nothing
		
	}

	@Override
	public void onTxSuccess() {
		// Do nothing
		
	}
	

}
