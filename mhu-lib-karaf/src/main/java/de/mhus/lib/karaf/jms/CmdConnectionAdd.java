package de.mhus.lib.karaf.jms;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.lib.karaf.TemplateUtils;

@Command(scope = "jms", name = "connection-add", description = "Remove connection")
@Service
public class CmdConnectionAdd implements Action {

	@Argument(index=0, name="name", required=true, description="ID of the connection", multiValued=false)
    String name;

	@Argument(index=1, name="url", required=true, description="URL to the broker", multiValued=false)
    String url;

	@Argument(index=2, name="user", required=true, description="user", multiValued=false)
    String user;

	@Argument(index=3, name="password", required=true, description="password", multiValued=false)
    String password;

    @Option(name = "-o", aliases = { "--online" }, description = "Create the datasource online and not a blueprint", required = false, multiValued = false)
    boolean online;

	@Override
	public Object execute() throws Exception {

		if (online) {
			JmsManagerService service = JmsUtil.getService();
			if (service == null) {
				System.out.println("Service not found");
				return null;
			}
			
			service.addConnection(name, url, user, password);
			System.out.println("OK");
		} else {
			
	        File karafBase = new File(System.getProperty("karaf.base"));
	        File deployFolder = new File(karafBase, "deploy");
	        File outFile = new File(deployFolder, "jms-openwire_" + name + ".xml");

	        HashMap<String, String> properties = new HashMap<String, String>();
	        properties.put("url", url);
	        properties.put("user", user);
	        properties.put("password", password);
	        properties.put("name", name);
	        String templateFile = "jms-openwire.xml";
            InputStream is = this.getClass().getResourceAsStream(templateFile);
            if (is == null) {
                throw new IllegalArgumentException("Template resource " + templateFile + " doesn't exist");
            }
            TemplateUtils.createFromTemplate(outFile, is, properties);
	        
		}
		return null;
	}

}
