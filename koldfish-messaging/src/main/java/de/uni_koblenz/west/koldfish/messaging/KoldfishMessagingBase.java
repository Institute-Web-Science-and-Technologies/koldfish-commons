package de.uni_koblenz.west.koldfish.messaging;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

public abstract class KoldfishMessagingBase implements AutoCloseable {

	private final Connection connection;

	private final Session session;

	public KoldfishMessagingBase(String userName, String password,
			String brokerURL) throws JMSException {
		this(userName, password, brokerURL, new String[0]);
	}

	public KoldfishMessagingBase(String userName, String password,
			String brokerURL, String... trustedPackages) throws JMSException {
		connection = ConnectionManager.getConnection(userName, password,
				brokerURL, trustedPackages);
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	protected Session getSession() {
		return session;
	}

	@Override
	public void close() throws Exception {
		session.close();
		ConnectionManager.closeConnection(connection);
	}

}
