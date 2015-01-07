package de.mhus.lib.core;

import de.mhus.lib.core.logging.Log;

/**
 * This class is currently only a place holder for a smarter strategy. But
 * the interface should be fix.
 * TODO implement strategy
 * @author mikehummel
 *
 */
public class MLog {
	
	private Log log;
	
	protected synchronized Log log() {
		if (log == null) {
			log = MSingleton.get().createLog(this);
		}
		return log;
	}
}