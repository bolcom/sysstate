package nl.unionsoft.sysstate.queue;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.unionsoft.commons.properties.PropertiesUtil;
import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

public class DiscoveryWorker implements ReferenceRunnable {

    // @Inject
    // @Named("pluginLogic")
    // private PluginLogic pluginLogic;

    private String plugin;
    private Properties properties;

    public List<ReferenceRunnable> discovered;

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryWorker.class);

    private static long count = 0;

    private final long id;

    public DiscoveryWorker() {
        id = count++;
    }

    public List<ReferenceRunnable> getDiscovered() {
        return discovered;
    }

    public void setDiscovered(final List<ReferenceRunnable> discovered) {
        this.discovered = discovered;
    }

    public void run() {
        LOG.info("Starting discovery for pluginName '{}' and config '{}'.", plugin, properties);
        // final DiscoveryPlugin discoveryPlugin = pluginLogic.getPlugin(plugin);
        // if (discoveryPlugin == null) {
        // throw new IllegalArgumentException("No plugin found for pluginName: " + plugin);
        // }

        // final Collection<? extends ReferenceRunnable> results = discoveryPlugin.discover(properties);
        // if (results != null) {
        // LOG.info("Discovery returned {} runnables...", results.size());
        // }
        // for (ReferenceRunnable referenceRunnable : results) {
        // // referenceWorker.enqueue(referenceRunnable);
        // }
    }

    public String getReference() {
        return "discoveryWorker-" + id;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(final String plugin) {
        this.plugin = plugin;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Discovery for plugin '");
        stringBuilder.append(plugin);
        stringBuilder.append("' using configuration:\n");
        stringBuilder.append(PropertiesUtil.propertiesToString(properties));
        return stringBuilder.toString();
    }

}
