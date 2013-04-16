package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.logic.DiscoveryLogic;
import nl.unionsoft.sysstate.common.plugins.DiscoveryPlugin;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.queue.DiscoveryWorker;
import nl.unionsoft.sysstate.queue.ReferenceWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("discoveryLogic")
public class DiscoveryLogicImpl implements DiscoveryLogic {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryLogicImpl.class);
    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("referenceWorker")
    private ReferenceWorker referenceWorker;

    private List<InstanceDto> discoveredInstances;

    public DiscoveryLogicImpl () {
        discoveredInstances = new ArrayList<InstanceDto>();
    }

    public void discover(String plugin, Properties properties) {
        LOG.info("Starting discovery for pluginName '{}' and config '{}'.", plugin, properties);
        discoveredInstances.clear();
        final DiscoveryPlugin discoveryPlugin = pluginLogic.getPlugin(plugin);
        if (discoveryPlugin == null) {
            throw new IllegalArgumentException("No plugin found for pluginName: " + plugin);
        }
        DiscoveryWorker discovery = new DiscoveryWorker();
        discovery.setPlugin(plugin);
        discovery.setProperties(properties);
        referenceWorker.enqueue(discovery);

    }

    public List<InstanceDto> getDiscoveredInstances() {
        return discoveredInstances;
    }

    public void setDiscoveredInstances(List<InstanceDto> discoveredInstances) {
        this.discoveredInstances = discoveredInstances;
    }

    public void addDiscoveredInstance(InstanceDto instanceDto) {
        discoveredInstances.add(instanceDto);
    }

}
