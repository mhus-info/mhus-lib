package de.mhus.lib.core.cfg;

import de.mhus.lib.core.config.IConfig;

public interface CfgProvider {

	public IConfig getConfig();
	
	public void doStart();
	public void doStop();
	
}
