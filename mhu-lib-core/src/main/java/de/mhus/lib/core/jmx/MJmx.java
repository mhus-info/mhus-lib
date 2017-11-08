package de.mhus.lib.core.jmx;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MSystem;

public class MJmx extends JmxObject {

	public MJmx() {
		this(true,MSystem.findSource());
	}
	
	public MJmx(boolean weak,String name) {
		jmxRegister(weak, name);
	}
	
	protected void jmxRegister(boolean weak,String name) {
		if (!isJmxRegistered()) {
			try {
				setJmxName(name);
				MApi.lookup(MRemoteManager.class).register(this,weak);
			} catch (Throwable e) {
				log().t(e);
			}
		}
	}
}
