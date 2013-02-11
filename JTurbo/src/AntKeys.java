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

public class AntKeys {
  
  public static void main(String [] args) {
    byte [] antplus_key = new byte[] { -71, -91, 33, -5, -67, 114, -61, 69 };
    print_key(antplus_key);
    byte [] antfs_key = new byte[] {-88, -92, 35, -71, -11, 94, 99, -63 };
    print_key(antfs_key);
  }

  private static void print_key(byte[] antplus_key) {
    for (byte b : antplus_key) {
      System.out.printf("%2x ", b);
    }
    System.out.println();
  }

}
