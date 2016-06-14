/**
 * 
 */
package de.unikoblenz.west.koldfish.messaging;

/**
 * @author lkastler
 *
 */
public interface Receiver {

  public void addListener(KoldfishMessageListener listener);

  public void removeListener(KoldfishMessageListener listener);
}
