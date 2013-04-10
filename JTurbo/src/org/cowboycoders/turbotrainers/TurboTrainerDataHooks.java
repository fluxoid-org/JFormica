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

public interface TurboTrainerDataHooks {
  
	/**
	 * Hook to control speed passed to listeners. 
	 * 
	 * @param speed the actual speed of the wheel. 
	 * @return speed you want listeners to observe (e.g virtual speed)
	 */	
  public abstract double onSpeedChange(double speed);
  
	/**
	 * Hook to control power passed to listeners. 
	 * 
	 * @param power the actual power from the turbo trainer. 
	 * @return power you want listeners to observe (e.g virtual power)
	 */	
  public abstract double onPowerChange(double power);
  
	/**
	 * Hook to control cadence passed to listeners. 
	 * 
	 * @param cadence the actual cadence from the turbo trainer. 
	 * @return cadence you want listeners to observe (e.g virtual cadence)
	 */	
  public abstract double onCadenceChange(double cadence);
  
	/**
	 * Hook to control distance passed to listeners. 
	 * 
	 * @param distance the actual distance from the turbo trainer. 
	 * @return distance you want listeners to observe (e.g virtual distance)
	 */	
  public abstract double onDistanceChange(double distance);

}