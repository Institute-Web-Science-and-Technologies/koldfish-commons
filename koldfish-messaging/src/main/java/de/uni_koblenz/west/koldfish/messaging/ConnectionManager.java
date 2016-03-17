package de.uni_koblenz.west.koldfish.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ConnectionManager {

	private static Map<Set<String>, Connection> connections = new HashMap<>();

	private static Map<Set<String>, Integer> usageFrequency = new HashMap<>();

	public static synchronized Connection getConnection(String userName,
			String password, String brokerURL) throws JMSException {
		return getConnectionUnsynchronized(userName, password, brokerURL,
				new String[0]);
	}

	public static synchronized Connection getConnection(String userName,
			String password, String brokerURL, String... trustedPackages)
			throws JMSException {
		return getConnectionUnsynchronized(userName, password, brokerURL,
				trustedPackages);
	}

	private static Connection getConnectionUnsynchronized(String userName,
			String password, String brokerURL, String... trustedPackages)
			throws JMSException {
		Set<String> key = getKeySet(userName, password, brokerURL);
		Connection connection = connections.get(key);
		if (connection == null) {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
					userName, password, brokerURL);
			if (trustedPackages == null || trustedPackages.length == 0) {
				// FIXME hardcore hack:
				// http://activemq.apache.org/objectmessage.html
				factory.setTrustAllPackages(true);

			} else {
				List<String> trustedPackagesList = new ArrayList<>();
				trustedPackagesList.add("java.lang");
				trustedPackagesList.add("java.util");
				trustedPackagesList.add("org.apache.activemq");
				trustedPackagesList.add("org.fusesource.hawtbuf");
				trustedPackagesList.add("com.thoughtworks.xstream.mapper");
				trustedPackagesList
						.add("de.uni_koblenz.west.koldfish.messages");
				for (String trustedPackage : trustedPackages) {
					trustedPackagesList.add(trustedPackage);
				}
				factory.setTrustedPackages(trustedPackagesList);
			}
			connection = factory.createConnection();
			connections.put(key, connection);
			usageFrequency.put(key, 1);
		} else {
			usageFrequency.put(key, usageFrequency.get(key).intValue() + 1);
		}
		return connection;
	}

	private static Set<String> getKeySet(String... strings) {
		Set<String> set = new HashSet<>();
		for (String s : strings) {
			set.add(s);
		}
		return set;
	}

	public static synchronized void closeConnection(Connection connection)
			throws JMSException {
		Set<String> key = null;
		for (Entry<Set<String>, Connection> entry : connections.entrySet()) {
			if (entry.getValue() == connection) {
				key = entry.getKey();
				break;
			}
		}
		if (key != null) {
			int usage = usageFrequency.get(key);
			usage--;
			if (usage == 0) {
				connections.remove(key);
				usageFrequency.remove(key);
			} else {
				usageFrequency.put(key, usage);
			}
		}
		connection.close();
	}

}
