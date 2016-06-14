/**
 * 
 */
package de.unikoblenz.west.koldfish.messaging.impl;

import de.unikoblenz.west.koldfish.messaging.Receiver;

/**
 * @author lkastler
 *
 */
public interface CloseableReceiver extends Receiver, AutoCloseable {

}
