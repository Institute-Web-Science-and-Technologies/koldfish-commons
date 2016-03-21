package de.uni_koblenz.west.koldfish.serverSkeletons;

import javax.jms.JMSException;

import de.uni_koblenz.west.koldfish.messages.KoldfishMessage;
import de.uni_koblenz.west.koldfish.messaging.TopicReceiver;

public abstract class RequestHandler extends InfiniteThread {

	private final TopicReceiver receiver;

	public RequestHandler(String userName, String password, String brokerURL,
			String topicName) throws JMSException {
		this(userName, password, brokerURL, topicName, new String[0]);
	}

	public RequestHandler(String userName, String password, String brokerURL,
			String topicName, String... trustedPackages) throws JMSException {
		super(true);
		receiver = new TopicReceiver(userName, password, brokerURL, topicName,
				trustedPackages);
	}

	@Override
	protected void performOneIterationStep() {
		try {
			KoldfishMessage message = receiver.receiveMessage();
			if (message != null) {
				processMessage(message);
			}
		} catch (JMSException e) {
			log.catching(e);
		}
	}

	protected abstract void processMessage(KoldfishMessage message);

	@Override
	public void close() throws Exception {
		super.close();
		// TODO close logger if not done by shutdown hook
		receiver.close();
	}

}
