/**
 *     Copyright (c) 2013, Will Szumski
 *
 *     This file is part of formicidae.
 *
 *     formicidae is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     formicidae is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with formicidae.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cowboycoders.ant;

import net.vidageek.mirror.dsl.Mirror;
import org.cowboycoders.ant.interfaces.AbstractAntTransceiver;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class NodeTest {

    Node node;
    Channel channel1;

    @Before
    public void setup() {
        node = new Node(mock(AbstractAntTransceiver.class));
        channel1 = mock(Channel.class);

        new Mirror().on(node).set().field("channels").withValue(new Channel[]{channel1});
    }

    @Test
    public void shouldSetChannelFreeFlagToFalse() {
        when(channel1.isFree()).thenReturn(true);
        Channel channel = node.getFreeChannel();

        assertSame(channel1, channel);
        verify(channel1).setFree(false);
    }

    @Test
    public void shouldNotGetNonFreeChannels() {
        when(channel1.isFree()).thenReturn(false);

        assertNull(node.getFreeChannel());
    }

    @Test
    public void shouldReturnFreeChannelToPool() {
        node.freeChannel(channel1);

        verify(channel1).setFree(true);
    }
}
