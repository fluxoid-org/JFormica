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

public class AntError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -29940270544783811L;

  public AntError() {
    super();
    // TODO Auto-generated constructor stub
  }

  public AntError(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
    // TODO Auto-generated constructor stub
  }

  public AntError(String detailMessage) {
    super(detailMessage);
    // TODO Auto-generated constructor stub
  }

  public AntError(Throwable throwable) {
    super(throwable);
    // TODO Auto-generated constructor stub
  }

}
