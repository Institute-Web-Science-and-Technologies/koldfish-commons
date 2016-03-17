package de.uni_koblenz.west.koldfish.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.TemporaryQueue;

import de.uni_koblenz.west.koldfish.messages.KoldfishMessage;

public class TemporaryQueueReceiver extends KoldfishMessagingBase {

	private final TemporaryQueue queue;

	private final MessageConsumer consumer;

	public TemporaryQueueReceiver(String userName, String password,
			String brokerURL) throws JMSException {
		this(userName, password, brokerURL, new String[0]);
	}

	public TemporaryQueueReceiver(String userName, String password,
			String brokerURL, String... trustedPackages) throws JMSException {
		super(userName, password, brokerURL, trustedPackages);
		queue = getSession().createTemporaryQueue();
		consumer = getSession().createConsumer(queue);
	}

	public String getQueueName() throws JMSException {
		return queue.getQueueName();
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
