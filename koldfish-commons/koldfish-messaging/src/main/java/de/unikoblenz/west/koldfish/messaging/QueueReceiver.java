package de.unikoblenz.west.koldfish.messaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

public class QueueReceiver extends KoldfishMessagingBase {

	private final MessageConsumer consumer;

	public QueueReceiver(String userName, String password, String brokerURL,
			String queueName) throws JMSException {
		this(userName, password, brokerURL, queueName, new String[0]);
	}

	public QueueReceiver(String userName, String password, String brokerURL,
			String queueName, String... trustedPackages) throws JMSException {
		super(userName, password, brokerURL, trustedPackages);
		Destination queue = getSession().createQueue(queueName);
		consumer = getSession().createConsumer(queue);
	}

	public KoldfishMessage receiveMessage() throws JMSException {
		Message message = consumer.receive();
		if (message != null) {
			return (KoldfishMessage) ((ObjectMessage) message).getObject();
		} else {
			return null;
		}
	}

	public KoldfishMessage receiveMessage(long timeOut) throws JMSException {
		Message message = consumer.receive(timeOut);
		if (message != null) {
			return (KoldfishMessage) ((ObjectMessage) message).getObject();
		} else {
			return null;
		}
	}

	public KoldfishMessage receiveMessageNoWait() throws JMSException {
		Message message = consumer.receiveNoWait();
		if (message != null) {
			return (KoldfishMessage) ((ObjectMessage) message).getObject();
		} else {
			return null;
		}
	}

	@Override
	public void close() throws Exception {
		consumer.close();
		super.close();
	}

}
