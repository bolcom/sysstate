package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.logic.DiscoveryLogic;

import org.springframework.stereotype.Service;

@Service("discoveryLogic")
public class DiscoveryLogicImpl implements DiscoveryLogic {

    //    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryLogicImpl.class);

    private List<InstanceDto> discoveredInstances;

    public DiscoveryLogicImpl() {
        discoveredInstances = new ArrayList<InstanceDto>();
    }

    public void discover(final String plugin, final Properties properties) {
        // LOG.info("Starting discovery for pluginName '{}' and config '{}'.", plugin, properties);
        // discoveredInstances.clear();
        // final DiscoveryPlugin discoveryPlugin = pluginLogic.getPlugin(plugin);
        // if (discoveryPlugin == null) {
        // throw new IllegalArgumentException("No plugin found for pluginName: " + plugin);
        // }
        // DiscoveryWorker discovery = new DiscoveryWorker();
        // discovery.setPlugin(plugin);
        // discovery.setProperties(properties);
        // referenceWorker.enqueue(discovery);

    }

    public List<InstanceDto> getDiscoveredInstances() {
        return discoveredInstances;
    }

    public void setDiscoveredInstances(final List<InstanceDto> discoveredInstances) {
        this.discoveredInstances = discoveredInstances;
    }

    public void addDiscoveredInstance(final InstanceDto instanceDto) {
        discoveredInstances.add(instanceDto);
    }

}
