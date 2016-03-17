package de.uni_koblenz.west.koldfish.messaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import de.uni_koblenz.west.koldfish.messages.KoldfishMessage;

public class QueueReceiver extends KoldfishMessagingBase {

	private final Destination queue;

	private final MessageConsumer consumer;

	public QueueReceiver(String userName, String password, String brokerURL,
			String queueName) throws JMSException {
		this(userName, password, brokerURL, queueName, new String[0]);
	}

	public QueueReceiver(String userName, String password, String brokerURL,
			String queueName, String... trustedPackages) throws JMSException {
		super(userName, password, brokerURL, trustedPackages);
		queue = getSession().createQueue(queueName);
		consumer = getSession().createConsumer(queue);
	}

	public KoldfishMessage receiveMessage() throws JMSException {
		return (KoldfishMessage) consumer.receive();
	}

	public KoldfishMessage receiveMessage(long timeOut) throws JMSException {
		return (KoldfishMessage) consumer.receive(timeOut);
	}

	public KoldfishMessage receiveMessageNoWait() throws JMSException {
		return (KoldfishMessage) consumer.receiveNoWait();
	}

	@Override
	public void close() throws Exception {
		consumer.close();
		super.close();
	}

}
