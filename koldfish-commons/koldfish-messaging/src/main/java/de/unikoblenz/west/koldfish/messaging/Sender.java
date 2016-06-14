package de.unikoblenz.west.koldfish.messaging;

import javax.jms.JMSException;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

/**
 * 
 * @author lkastler
 */
public interface Sender {

  public void sendMessage(KoldfishMessage msg) throws JMSException;
}
