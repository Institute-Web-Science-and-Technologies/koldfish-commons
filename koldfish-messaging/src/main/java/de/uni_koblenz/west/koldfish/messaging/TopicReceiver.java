package de.uni_koblenz.west.koldfish.messaging;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import de.uni_koblenz.west.koldfish.messages.KoldfishMessage;

public class TopicReceiver extends KoldfishMessagingBase {

	private final MessageConsumer consumer;

	public TopicReceiver(String userName, String password, String brokerURL,
			String topicName) throws JMSException {
		this(userName, password, brokerURL, topicName, new String[0]);
	}

	public TopicReceiver(String userName, String password, String brokerURL,
			String topicName, String... trustedPackages) throws JMSException {
		super(userName, password, brokerURL, trustedPackages);
		consumer = getSession()
				.createConsumer(getSession().createTopic(topicName));
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
