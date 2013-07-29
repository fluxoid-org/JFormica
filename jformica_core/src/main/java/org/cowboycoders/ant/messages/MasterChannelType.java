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
package org.cowboycoders.ant.messages;

import java.util.ArrayList;
import java.util.List;



/**
 * Represents a MAster channel configuration
 * @author will
 *
 */
public class MasterChannelType extends ChannelType {
  
  /**
   * Master configuration with all options
   * @param shared if channel is shared
   * @param txOnly if transmit only channel
   */
  public MasterChannelType(boolean shared, boolean txOnly) {
    super(getChannelTypesArray(shared,txOnly));
  }
  
  /**
   * Default master configuration : two-way, non-sahred
   */
  public MasterChannelType() {
    super(getChannelTypesArray(false,false));
  }

  private static ChannelType.Types [] getChannelTypesArray(boolean shared,
      boolean rxOnly) {
    List<ChannelType.Types> types = new ArrayList<ChannelType.Types>();
    types.add(ChannelType.Types.MASTER);
    if (shared) types.add(ChannelType.Types.SHARED_TRANSMIT);
    if (rxOnly) types.add(ChannelType.Types.ONEWAY_TRANSMIT);  
    return types.toArray(new ChannelType.Types[0]);
  }
  
}