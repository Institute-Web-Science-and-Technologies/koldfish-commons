package de.unikoblenz.west.koldfish.messaging;

import javax.jms.JMSException;

import de.unikoblenz.west.koldfish.LifeCycle;
import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

/**
 * manages connections.
 * 
 * @author lkastler
 *
 */
public interface ConnectionManager extends LifeCycle {


  public Receiver queueReceiver(String destination) throws JMSException;

  public Receiver topicReceiver(String destination) throws JMSException;;

  public void sentToQueue(String destination, KoldfishMessage msg) throws JMSException;;

  public void sentToTopic(String destination, KoldfishMessage msg) throws JMSException;;
}
