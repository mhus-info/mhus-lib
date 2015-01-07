package de.mhus.lib.core.jmx;

import de.mhus.lib.core.MSystem;

public class MJmx extends JmxObject {

	public MJmx() {
		this(true,MSystem.findSource(4));
	}
	
	public MJmx(boolean weak,String name) {
		jmxRegister(weak, name);
	}
	
	protected void jmxRegister(boolean weak,String name) {
		if (isBase(MRemoteManager.class) && !isJmxRegistered()) {
			try {
				setJmxName(name);
				base(MRemoteManager.class).register(this,weak);
			} catch (Exception e) {
				log().t(e);
			}
		}
	}
}