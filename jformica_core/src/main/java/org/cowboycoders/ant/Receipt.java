/**
 *     Copyright (c) 2012, Will Szumski
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

import org.cowboycoders.ant.utils.TimestampContainer;
import org.cowboycoders.ant.utils.TimestampQueryable;

public class Receipt {
  
  private long rxTimestamp;
  private long txTimestamp;
  
  /**
   * Compare this with object timestamp
   * @return the current timestamp
   */
  public static long getCurrentTimestamp() {
    return System.nanoTime();
  }


  public long getRxTimestamp() {
    return rxTimestamp;
  }
  
  public long getTxTimestamp() {
    return txTimestamp;
  }

  /**
   * @param rxTimestamp the rxTimestamp to set
   */
  public void setRxTimestamp(long rxTimestamp) {
    this.rxTimestamp = rxTimestamp;
  }

  /**
   * @param txTimestamp the txTimestamp to set
   */
  public void setTxTimestamp(long txTimestamp) {
    this.txTimestamp = txTimestamp;
  }
  
  public void stampRx() {
    setRxTimestamp(System.nanoTime());
  }
  
  public void stampTx() {
    setTxTimestamp(System.nanoTime());
  }
  
}
  
  