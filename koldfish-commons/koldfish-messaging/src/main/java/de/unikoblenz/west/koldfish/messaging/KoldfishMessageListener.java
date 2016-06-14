/**
 * 
 */
package de.unikoblenz.west.koldfish.messaging;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

/**
 * listens for KoldfishMessages
 * 
 * @author lkastler
 *
 */
public interface KoldfishMessageListener {

  /**
   * handles given KoldfishMessage.
   * 
   * @param msg - message to handle.
   */
  public void onMessage(KoldfishMessage msg);
}
