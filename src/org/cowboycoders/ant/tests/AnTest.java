package org.cowboycoders.ant.tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class AnTest {
  
  @Target(value={ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface WaitForMessageOpts  {
      boolean clearBuffer() default true;
  }
  
  @WaitForMessageOpts 
  static void print() throws SecurityException, NoSuchMethodException {
    
    System.out.println((AnTest.class.getMethod("print").getAnnotation(WaitForMessageOpts.class)).clearBuffer());
  }
  
  @WaitForMessageOpts(clearBuffer=false)
  static void printFalse() throws SecurityException, NoSuchMethodException {
    print();
  }
  
  public static void main(String [] args) throws SecurityException, NoSuchMethodException {
    
    print();
    
    
    printFalse();
    
    
    
  }

}
