package de.unikoblenz.west.koldfish.messaging;

import javax.jms.JMSException;
import javax.jms.MessageProducer;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

public class TopicSender extends KoldfishMessagingBase {

	private final MessageProducer producer;

	public TopicSender(String userName, String password, String brokerURL,
			String topicName) throws JMSException {
		this(userName, password, brokerURL, topicName, new String[0]);
	}

	public TopicSender(String userName, String password, String brokerURL,
			String topicName, String... trustedPackages) throws JMSException {
		super(userName, password, brokerURL, trustedPackages);
		producer = getSession()
				.createProducer(getSession().createTopic(topicName));
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
