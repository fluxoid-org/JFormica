
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
