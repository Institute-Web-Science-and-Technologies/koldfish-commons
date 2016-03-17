package de.uni_koblenz.west.koldfish.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;

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
