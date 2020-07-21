package de.mhus.lib.tests.docker;

import java.util.LinkedList;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.api.model.Volume;

import de.mhus.lib.core.M;
import de.mhus.lib.core.MString;

public class ContainerBuilder {
    
    final public DockerScenario scenario;
    final public DockerContainer cont;
    
    final public LinkedList<String> links = new LinkedList<>();
    final public LinkedList<String> cmd = new LinkedList<>();
    final public LinkedList<String> env = new LinkedList<>();
    final public LinkedList<String> entrypoint = new LinkedList<>();
    final public LinkedList<String> volumes = new LinkedList<>();
    final public LinkedList<String> ports = new LinkedList<>();
    final public HostConfig hostBuilder;
    final public CreateContainerCmd builder;
    final public LinkedList<Volume> volumesDef = new LinkedList<>();
    final public LinkedList<Bind> volumesBind = new LinkedList<>();
    final public Ports portConfig  = new Ports();
    final public LinkedList<Link> linksBind = new LinkedList<>();

    public ContainerBuilder(DockerScenario scenario, DockerContainer cont, CreateContainerCmd builder) {
        this.scenario = scenario;
        this.builder = builder;
        this.cont = cont;
        this.hostBuilder = HostConfig.newHostConfig();
    }

    public void build() {

        if (ports.size() > 0) {
            for (String port : ports) {
            	if (port.endsWith("/udp")) {
            		port = MString.beforeIndex(port, '/');
                    if (port.indexOf(':') > 0) {
                        ExposedPort udp = ExposedPort.udp(M.to(MString.beforeIndex(port, ':'), 0));
                        Binding bind = Binding.bindPort(M.to(MString.afterIndex(port, ':'), 0));
                        portConfig.bind(udp, bind);
                    } else {
                        ExposedPort udp = ExposedPort.udp(M.to(port, 0));
                        portConfig.bind(udp, Binding.empty());
                    }
            	} else {
            		if (port.endsWith("/tcp"))
                		port = MString.beforeIndex(port, '/');
	                if (port.indexOf(':') > 0) {
	                    ExposedPort tcp = ExposedPort.tcp(M.to(MString.beforeIndex(port, ':'), 0));
	                    Binding bind = Binding.bindPort(M.to(MString.afterIndex(port, ':'), 0));
	                    portConfig.bind(tcp, bind);
	                } else {
	                    ExposedPort tcp = ExposedPort.tcp(M.to(port, 0));
	                    portConfig.bind(tcp, Binding.empty());
	                }
            	}
            }
            hostBuilder.withPortBindings(portConfig);
        }
        
        if (entrypoint.size() > 0)
            builder.withEntrypoint(entrypoint);
        
        if (volumes.size() > 0) {
            for (String vol : volumes) {
            	AccessMode mode = AccessMode.DEFAULT;
            	String src = MString.beforeIndex(vol, ':');
            	String trg = MString.afterIndex(vol, ':');
            	if (MString.isIndex(trg, ':')) {
            		String modeStr = MString.afterIndex(trg, ':');
            		trg = MString.beforeIndex(trg, ':');
            		if (modeStr.equals("ro"))
            			mode = AccessMode.ro;
            		if (modeStr.equals("rw"))
            			mode = AccessMode.rw;
            	}
                Volume v = new Volume(trg);
                volumesDef.add(v);
                volumesBind.add(new Bind(src, v, mode));
            }
            builder.withVolumes(volumesDef);
            hostBuilder.withBinds(volumesBind);
        }
        if (env.size() > 0)
            builder.withEnv(env);
        
        if (cmd.size() > 0)
            builder.withCmd(cmd);

        if (links.size() > 0) {
            for (String link : links) {
                linksBind.add(new Link(MString.beforeIndex(link, ':'), MString.afterIndex(link, ':')));
            }
            hostBuilder.withLinks(linksBind);
        }
        builder.withHostConfig(hostBuilder);
        
    }

}