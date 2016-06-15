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

  /**
   * creates a receiver listening on the given queue.
   * 
   * @param queueName - name of queue for the receiver.
   * @return a receiver listening on the given queue name.
   * @throws JMSException thrown if receiver could not be created.
   */
  public Receiver queueReceiver(String queueName) throws JMSException;

  /**
   * creates a receiver listening on the given topic.
   * 
   * @param topicName - name of topic for the receiver.
   * @return a receiver listening on the given queue name.
   * @throws JMSException thrown if receiver could not be created.
   */
  public Receiver topicReceiver(String topicName) throws JMSException;;

  /**
   * sends given KoldfishMessage via the given queue name.
   * 
   * @param queueName - queue to send message through.
   * @param msg - message to send.
   * @throws JMSException thrown message could not be sent.
   */
  public void sentToQueue(String queueName, KoldfishMessage msg) throws JMSException;;

  /**
   * sends given KoldfishMessage via the given topic name.
   * 
   * @param topicName - topic to send message through.
   * @param msg - message to send.
   * @throws JMSException thrown message could not be sent.
   */
  public void sentToTopic(String topicName, KoldfishMessage msg) throws JMSException;;
}
