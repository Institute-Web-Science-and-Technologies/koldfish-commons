package de.unikoblenz.west.koldfish.fluid;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import java.util.*;

/**
 * Created by Martin Leinberger on 21.03.2016.
 * I don't think that AutoClosable is usefull when dealing with a Singleton, but if we ever change that it should
 * definetely be auto closable
 */
public class ConnectionManager implements AutoCloseable {
    private final static String PROPERTY_FILE_PATH = "connection.properties";

    private final static Properties properties = new Properties();
    private final static Logger log = LogManager.getLogger(ConnectionManager.class);

    private final Map<String, Destination> destinations = new HashMap<>();

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    private boolean activeConnection = false;

    private static final ConnectionManager INSTANCE = new ConnectionManager();

    // Better with a HOLDER class? Or is this enough?
    public static ConnectionManager get() {
        return INSTANCE;
    }

    private ConnectionManager() {
        try {
            properties.load(getClass().getResourceAsStream(PROPERTY_FILE_PATH));
            log.debug("Loaded config from {}", PROPERTY_FILE_PATH);
        } catch (Exception e) {
            log.warn("Could not read from {} - using default values for connection.", PROPERTY_FILE_PATH);
        }
    }

    private Destination remember(Destination destination) {
        destinations.put(destination.toString(),destination);
        return destination;
    }

    private void setListener(Destination destination, MessageListener listener) throws JMSException {
        if (listener != null)
            session.createConsumer(destination).setMessageListener(listener);
    }

    public ConnectionManager init() throws JMSException {
        connection = new ActiveMQConnectionFactory(
                properties.getProperty("user", ActiveMQConnectionFactory.DEFAULT_USER),
                properties.getProperty("pass", ActiveMQConnectionFactory.DEFAULT_PASSWORD),
                properties.getProperty("uri", ActiveMQConnectionFactory.DEFAULT_BROKER_URL)
        ).createConnection();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        // Use given ID or compute very simple unique id for computer
        connection.setClientID(properties.getProperty("client-id",
                Integer.toHexString(System.getProperty("user.name").hashCode())));


        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(null);
        return this;
    }

    public ConnectionManager start() throws JMSException {
        if (connection == null) init();
        connection.start();
        activeConnection = true;
        return this;
    }

    public void close() {
        log.debug("Trying to close connection");
        try {
            if (connection != null) {
                connection.close();
                log.debug("Connection successfully closed");
            }
        } catch (JMSException ex) {
            // Not much I can do at this point except for logging the exception :/
            log.error(ex);
        }
        connection = null;
        session = null;
        activeConnection = false;
    }

    public ConnectionManager createTopic(String topicName) throws JMSException {
        createTopic(topicName, null);
        return this;
    }

    public ConnectionManager createTopic(String topicName, MessageListener listener) throws JMSException {
        if (session == null) init();
        setListener(remember(session.createTopic(topicName)), listener);
        return this;
    }

    public ConnectionManager createQueue(String queueName) throws JMSException {
        createQueue(queueName,null);
        return this;
    }

    public ConnectionManager createQueue(String queueName, MessageListener listener) throws JMSException {
        if (connection == null) init();
        setListener(remember(session.createQueue(queueName)), listener);
        return this;
    }

    private void send(Destination destination, javax.jms.Message message) throws JMSException {
        if (!isConnectionActive()) start();
        producer.send(destination, message);
    }

    public ObjectMessage createObjectMessage(KoldfishMessage msg) throws JMSException {
        if (connection == null) init();
        return session.createObjectMessage(msg);
    }

    public void send(Destination destination, KoldfishMessage msg) throws JMSException {
        send(destination, createObjectMessage(msg));
    }

    public void sendToQueue(String queueName, KoldfishMessage message) throws JMSException {
        if (!destinations.containsKey("queue://"+queueName)) createQueue(queueName);
        send(destinations.get("queue://"+queueName), message);
    }

    public void sendToTopic(String topicName, KoldfishMessage message) throws JMSException {
        if (!destinations.containsKey("topic://"+topicName)) createTopic(topicName);
        send(destinations.get("topic://"+topicName), message);
    }

    public boolean isConnectionActive() {
        return activeConnection;
    }
}
