/**
 * 
 */
package de.unikoblenz.west.koldfish.messaging.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import de.unikoblenz.west.koldfish.messaging.KoldfishMessageListener;

/**
 * @author lkastler
 *
 */
public class ReceiverImpl implements CloseableReceiver {

  private static final Logger log = LogManager.getLogger(ReceiverImpl.class);
  private final List<KoldfishMessageListener> listeners =
      Collections.synchronizedList(new LinkedList<KoldfishMessageListener>());

  private final MessageConsumer consumer;

  ReceiverImpl(MessageConsumer consumer) throws JMSException {
    this.consumer = consumer;

    consumer.setMessageListener(new MessageListener() {

      @Override
      public void onMessage(Message message) {
        log.debug("message: {}", message);
        if (message instanceof ObjectMessage) {
          ObjectMessage om = (ObjectMessage) message;
          try {
            Serializable object = om.getObject();
            if (object instanceof KoldfishMessage) {
              synchronized (listeners) {
                for (KoldfishMessageListener l : listeners) {
                  l.onMessage((KoldfishMessage) object);
                }
              }
            } else {
              log.debug("no koldfish message");
            }
          } catch (JMSException e) {
            log.error(e);
          }
        }
      }

    });

    log.debug("started, using {}", consumer);

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.messaging.Receiver#addListener(de.unikoblenz.west.koldfish.
   * messaging.KoldfishMessageListener)
   */
  @Override
  public void addListener(KoldfishMessageListener listener) {
    synchronized (listeners) {
      listeners.add(listener);
      log.debug("listener added: {}", listener);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.messaging.Receiver#removeListener(de.unikoblenz.west.koldfish.
   * messaging.KoldfishMessageListener)
   */
  @Override
  public void removeListener(KoldfishMessageListener listener) {
    synchronized (listeners) {
      listeners.remove(listener);
      log.debug("listener removed: {}", listener);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    consumer.close();
    log.debug("closed");
  }
}
