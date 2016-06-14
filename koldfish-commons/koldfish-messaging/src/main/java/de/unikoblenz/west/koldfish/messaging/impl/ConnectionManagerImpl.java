/**
 * 
 */
package de.unikoblenz.west.koldfish.messaging.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import de.unikoblenz.west.koldfish.messaging.ConnectionManager;
import de.unikoblenz.west.koldfish.messaging.Receiver;

/**
 * @author lkastler
 *
 */
public class ConnectionManagerImpl implements ConnectionManager {


  private static final Logger log = LogManager.getLogger(ConnectionManagerImpl.class);

  private final Connection connection;
  private final Session session;
  private final MessageProducer producer;

  private volatile boolean running = false;

  private Map<Destination, CloseableReceiver> receivers =
      Collections.synchronizedMap(new HashMap<Destination, CloseableReceiver>());

  public ConnectionManagerImpl(String... trustedPackages) throws JMSException {
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
    if (trustedPackages == null || trustedPackages.length == 0) {
      // FIXME hardcore hack:
      // http://activemq.apache.org/objectmessage.html
      factory.setTrustAllPackages(true);

    } else {
      List<String> trustedPackagesList = new ArrayList<String>();
      trustedPackagesList.add("java.lang");
      trustedPackagesList.add("java.util");
      trustedPackagesList.add("org.apache.activemq");
      trustedPackagesList.add("org.fusesource.hawtbuf");
      trustedPackagesList.add("com.thoughtworks.xstream.mapper");
      trustedPackagesList.add("de.unikoblenz.west.koldfish.messages");

      for (String trustedPackage : trustedPackages) {
        trustedPackagesList.add(trustedPackage);
      }

      factory.setTrustedPackages(trustedPackagesList);
    }
    connection = factory.createConnection();
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    producer = session.createProducer(null);

    log.debug("created: {}", ActiveMQConnection.DEFAULT_BROKER_URL);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.LifeCycle#start()
   */
  @Override
  public void start() throws Exception {
    connection.start();
    running = true;
    log.debug("started");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.LifeCycle#isStarted()
   */
  @Override
  public boolean isStarted() {
    return running;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.LifeCycle#stop()
   */
  @Override
  public void stop() throws Exception {
    synchronized (receivers) {
      for (CloseableReceiver r : receivers.values()) {
        try {
          r.close();
        } catch (Exception e) {
          // do nothing specific
        }
      }

      receivers.clear();
    }

    session.close();
    connection.close();
    running = false;

    log.debug("stopped");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.messaging.ConnectionManager#queueReceiver()
   */
  @Override
  public Receiver queueReceiver(String destination) throws JMSException {
    return getReceiver(session.createQueue(destination));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.messaging.ConnectionManager#topicReceiver()
   */
  @Override
  public Receiver topicReceiver(String destination) throws JMSException {
    return getReceiver(session.createTopic(destination));
  }

  private Receiver getReceiver(Destination dest) throws JMSException {
    if (!receivers.containsKey(dest)) {
      MessageConsumer consumer = session.createConsumer(dest);
      receivers.put(dest, new ReceiverImpl(consumer));

    }

    return receivers.get(dest);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.messaging.ConnectionManager#sentToQueue(java.lang.String,
   * de.unikoblenz.west.koldfish.messages.KoldfishMessage)
   */
  @Override
  public void sentToQueue(String destination, KoldfishMessage msg) throws JMSException {
    producer.send(session.createQueue(destination), session.createObjectMessage(msg));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.messaging.ConnectionManager#sentToTopic(java.lang.String,
   * de.unikoblenz.west.koldfish.messages.KoldfishMessage)
   */
  @Override
  public void sentToTopic(String destination, KoldfishMessage msg) throws JMSException {
    producer.send(session.createTopic(destination), session.createObjectMessage(msg));
  }
}
