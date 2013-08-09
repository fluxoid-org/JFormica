package org.cowboycoders.ant;

import org.junit.Test;

public class NetworkTest {
	
	// dummy exception which is thrown when the listener is called
	private static class ListenerCalledException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	// throws ListenerCalledException when called
	private NetworkListener listener = new NetworkListener() {

		@Override
		public void onFree(Network network) {
			throw new ListenerCalledException();
			
		}
		
	};
	
	@Test (expected=ListenerCalledException.class)
	public void shouldNotifyListenerWhenAllReferencesHaveBeenFreed() {
		final int NUMBER_OF_HANDLES = 5;
		Network network = new Network(0,null,listener);
		for (int i =0 ; i < NUMBER_OF_HANDLES ; i++) {
			network.use();
		}
		
		// free all but last
		for (int i =0 ; i < NUMBER_OF_HANDLES -1 ; i++) {
			try {
				network.free();
			} catch (ListenerCalledException e){
				throw new RuntimeException("should not call listener yet");
			}
		}
		
		network.free();
		
		
	}

}
