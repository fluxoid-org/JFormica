package org.cowboycoders.turbotrainers.bushido.headunit;
import java.util.HashMap;
import java.util.Map;


public class BushidoButtonPressDescriptor {
  
  public static enum Button {
    LEFT(1),
    UP(2),
    OK(3),
    DOWN(4),
    RIGHT(5);
    
    private int id;
    private static Map<Integer,Button> map = new HashMap<Integer,Button>();
    public static int ID_MASK = 0x0f;
    
    static{
      
      for (Button button : Button.values()){
        map.put(button.getId(), button);
      }
    }

    Button(int id) {
      this.id = id;
    }
    
    

    /**
     * @return the id
     */
    public int getId() {
      return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
      this.id = id;
    }
    
    public static Button fromId(int id) {
      return map.get(id);
    }
    
    public static Button fromByte(byte value) {
      return map.get(value & ID_MASK);
    }
    
  }
  
  /**
   * Order from shortest to longest duration
   * @author will
   *
   */
  public static enum Duration {
    SHORT(0x00),
    MEDIUM(0x80),
    LONG(0xc0),
    
    ;
    
    private int mask;
    
    public static int DURATION_MASK = 0xf0;
    
    Duration(int mask) {
      this.mask = mask;
    }

    public int getMask() {
      return mask;
    }

   static Duration fromByte(byte value) {
      for (Duration duration : Duration.values()) {
        int mask = duration.getMask();
        if ((value & DURATION_MASK) == mask) {
          return duration;
        }
      }
      return null;
    }
    
  }

  private Button button;
  private Duration duration;
  private long recievedTimestamp = System.nanoTime();
  
  
  /**
   * @return the recievedTimestamp
   */
  public long getRecievedTimestamp() {
    return recievedTimestamp;
  }

  /**
   * @return the button
   */
  public Button getButton() {
    return button;
  }

  /**
   * @param button the button to set
   */
  private void setButton(Button button) {
    this.button = button;
  }

  /**
   * @return the duration
   */
  public Duration getDuration() {
    return duration;
  }

  /**
   * @param duration the duration to set
   */
  private void setDuration(Duration duration) {
    this.duration = duration;
  }

  public BushidoButtonPressDescriptor(Button button, Duration duration) {
    setButton(button);
    setDuration(duration);
  }
  
  public static BushidoButtonPressDescriptor fromByte(byte value) {
    Duration d = Duration.fromByte(value);
    Button b = Button.fromByte(value);
    if (b == null || d == null) return null;
    return new BushidoButtonPressDescriptor(b,d);
    
    
  }
  

}
