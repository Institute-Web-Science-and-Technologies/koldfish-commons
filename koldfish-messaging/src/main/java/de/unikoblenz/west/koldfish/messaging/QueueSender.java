package de.unikoblenz.west.koldfish.messaging;

import javax.jms.JMSException;
import javax.jms.MessageProducer;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

public class QueueSender extends KoldfishMessagingBase {

	private final MessageProducer producer;

	public QueueSender(String userName, String password, String brokerURL,
			String queueName) throws JMSException {
		this(userName, password, brokerURL, queueName, new String[0]);
	}

	public QueueSender(String userName, String password, String brokerURL,
			String queueName, String... trustedPackages) throws JMSException {
		super(userName, password, brokerURL, trustedPackages);
		producer = getSession()
				.createProducer(getSession().createQueue(queueName));
	}

	public void sendMessage(KoldfishMessage message) throws JMSException {
		producer.send(getSession().createObjectMessage(message));
	}

	@Override
	public void close() throws Exception {
		producer.close();
		super.close();
	}

}
