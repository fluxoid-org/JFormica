/**
 * 
 */
package org.cowboycoders.ant.interfaces;



/**
 * @author will
 *
 */
public class ServiceAlreadyClaimedException extends AntCommunicationException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public ServiceAlreadyClaimedException() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param detailMessage
   */
  public ServiceAlreadyClaimedException(String detailMessage) {
    super(detailMessage);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param throwable
   */
  public ServiceAlreadyClaimedException(Throwable throwable) {
    super(throwable);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param detailMessage
   * @param throwable
   */
  public ServiceAlreadyClaimedException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
    // TODO Auto-generated constructor stub
  }

}
