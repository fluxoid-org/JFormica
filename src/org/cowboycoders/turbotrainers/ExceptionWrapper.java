/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.cowboycoders.turbotrainers;

public class ExceptionWrapper {

  public static enum Context {
    STARTING,
    MID_OPERATION,
    STOPPING,
  }
  
  private Exception wrappedException;
  private Context context;
  
  
  /**
   * @return the context
   */
  public Context getContext() {
    return context;
  }

  /**
   * @param context the context to set
   */
  public void setContext(Context context) {
    this.context = context;
  }

  /**
   * @return the wrappedException
   */
  public Exception getWrappedException() {
    return wrappedException;
  }

  public ExceptionWrapper(Exception e) {
    wrappedException = e;
  }
  
  public boolean hasContext() {
    if(context != null) return true;
    return false;
  }

}
