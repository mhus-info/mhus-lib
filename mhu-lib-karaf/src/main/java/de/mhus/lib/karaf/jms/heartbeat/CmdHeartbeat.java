package de.mhus.lib.karaf.jms.heartbeat;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import de.mhus.lib.karaf.jms.JmsUtil;

@Command(scope = "jms", name = "heartbeat", description = "Send heardbeat")
@Service
public class CmdHeartbeat implements Action {

	@Argument(index=0, name="cmd", required=false, description="enable / disable, reset", multiValued=false)
    String cmd;
	
	@Override
	public Object execute() throws Exception {

		HeartbeatAdmin service = getService();
		if (service == null) {
			System.out.println("Service not found");
			return null;
		}

		if ("list".equals(cmd)) {
			for (HeartbeatService s : service.getServices())
				System.out.println(s.getName());
		} else
		if ("enable".equals(cmd) || "disbale".equals(cmd)) {
			service.setEnabled("enable".equals(cmd));
			System.out.println("OK " + cmd);
		} else {
			System.out.println("Send Heartbeat ...");
			service.sendHeartbeat(cmd);
			System.out.println("OK " + cmd);
		}
		
		return null;
	}

	private HeartbeatAdmin getService() {
		BundleContext bc = FrameworkUtil.getBundle(JmsUtil.class).getBundleContext();
		if (bc == null) return null;
		ServiceReference<HeartbeatAdmin> ref = bc.getServiceReference(HeartbeatAdmin.class);
		if (ref == null) return null;
		HeartbeatAdmin obj = bc.getService(ref);
		return obj;
	}

	
}
