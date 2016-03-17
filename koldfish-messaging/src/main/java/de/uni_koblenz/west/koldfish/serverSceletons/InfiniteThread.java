package de.uni_koblenz.west.koldfish.serverSceletons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class InfiniteThread extends Thread implements AutoCloseable {

	protected final Logger log = LogManager.getLogger(getClass());

	private volatile boolean isInterrupted;

	public InfiniteThread(boolean addShutdownHook) {
		isInterrupted = false;
		if (addShutdownHook) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					isInterrupted = false;
					if (isAlive()) {
						interrupt();
					}
					try {
						InfiniteThread.this.join();
					} catch (InterruptedException e) {
						log.error(e);
					}
					try {
						close();
					} catch (Exception e) {
						log.error(e);
					}
				}
			}));
		}
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			performOneIterationStep();
		}
	}

	protected abstract void performOneIterationStep();

	@Override
	public boolean isInterrupted() {
		return super.isInterrupted() || isInterrupted;
	}

	@Override
	public void interrupt() {
		isInterrupted = true;
		super.interrupt();
	}

	@Override
	public void close() throws Exception {
		if (isAlive()) {
			interrupt();
		} else {
			isInterrupted = true;
		}
	}

}
