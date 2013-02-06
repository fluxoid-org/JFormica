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
