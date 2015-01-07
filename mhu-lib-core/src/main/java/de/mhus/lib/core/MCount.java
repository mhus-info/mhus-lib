package de.mhus.lib.core;

import de.mhus.lib.annotations.jmx.JmxManaged;
import de.mhus.lib.core.jmx.MJmx;
import de.mhus.lib.core.service.UniqueId;

@JmxManaged(descrition = "Simple Counter")
public class MCount extends MJmx {

	protected long cnt;
	private String name;
	private long startTime = 0;
	private long lastTime = 0;
	protected boolean isClosed;
	
	public MCount() {
		cnt = 0;
		name = "Counter " + base(UniqueId.class).nextUniqueId();
	}
	
	public MCount(String name) {
		super(true,name);
		cnt = 0;
		this.name = name;
	}
	
	@JmxManaged(descrition="Reset the counter statistic")
	public void reset() {
		isClosed = false;
		cnt = 0;
		startTime = 0;
		lastTime = 0;
	}
	
	public void inc() {
		if (isClosed) return;
		cnt++;
		lastTime = System.currentTimeMillis();
		if (startTime == 0) startTime = lastTime;
	}

	@JmxManaged(descrition = "Amount of counts")
	public long getValue() {
		return cnt;
	}
	
	public double getHitsPerSecond() {
		if (startTime == 0 || lastTime == 0 || cnt == 0) return 0;
		return (double)cnt / (double)((lastTime - startTime)/1000);
	}
	
	@JmxManaged(descrition = "Name of this value")
	public String getName() {
		return name;
	}
	
	public long getFirstHitTime() {
		return startTime;
	}
	
	public long getLastHitTime() {
		return lastTime;
	}
	
	@JmxManaged(descrition = "Readable status of the counter")
	public String getStatusAsString() {
		if (startTime == 0 || lastTime == 0 || cnt == 0) return "unused";
		return MDate.toIsoDateTime(getFirstHitTime()) + " - " + MDate.toIsoDateTime(getLastHitTime()) + "," + getHitsPerSecond() + " hits/sec," + cnt;
	}
	
	public void close() {
		if (isClosed) return;
		isClosed = true;
		log().i("close",name,cnt,getHitsPerSecond());
	}
	
	protected void finalize() {
		close();
	}
	
	public String toString() {
		return MSystem.toString(this, getStatusAsString());
	}
}
