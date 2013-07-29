package org.cowboycoders.ant;

import net.vidageek.mirror.dsl.Mirror;
import org.cowboycoders.ant.interfaces.AbstractAntTransceiver;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class NodeTest {

    Node node;

    @Before
    public void setup() {
        node = spy(new Node(mock(AbstractAntTransceiver.class)));
        Channel channel1 = new Channel(node, 0);

        new Mirror().on(node).set().field("channels").withValue(new Channel[]{channel1});
    }

    @Test
    public void shouldSetChannelFreeFlagToFalse() {
        assertFalse(node.getFreeChannel().isFree());
    }

    @Test
    public void shouldNotReturnNonFreeChannels() {
        node.getFreeChannel();

        assertNull(node.getFreeChannel());
    }
}
