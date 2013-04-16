package nl.unionsoft.sysstate.queue;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.DiscoveryLogic;
import nl.unionsoft.sysstate.common.plugins.StateResolverPlugin;
import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossEnvironmentWorker implements ReferenceRunnable {

    private static long count = 0;

    private long id;

    private InstanceDto instance;

    private static final Logger LOG = LoggerFactory.getLogger(CrossEnvironmentWorker.class);

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("discoveryLogic")
    private DiscoveryLogic discoveryLogic;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    public CrossEnvironmentWorker () {
        id = count++;
    }

    public void run() {

        String pluginClass = instance.getPluginClass();
        StateDto state = stateLogic.requestState(pluginClass, instance.getConfiguration());
        LOG.info("Validating configuration:\n{}", instance.getConfiguration());
        if (StateType.STABLE.equals(state.getState()) || StateType.UNSTABLE.equals(state.getState())) {
            LOG.info("Instance validated!");
            final StateResolverPlugin stateResolver = pluginLogic.getPlugin(pluginClass);
            instance.setHomepageUrl(stateResolver.generateHomePageUrl(instance));
            discoveryLogic.addDiscoveredInstance(instance);
        } else {
            LOG.info("Configuration invalid, got state: {}", state.getState());
        }
    }

    public String getReference() {
        return "crossEnvironmentWorker-" + id;
    }

    public InstanceDto getInstance() {
        return instance;
    }

    public void setInstance(InstanceDto instance) {
        this.instance = instance;
    }

    public String getDescription() {
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("Job to validate if new instance is viable.");
        descriptionBuilder.append("\nProject:");
        descriptionBuilder.append(instance.getProjectEnvironment().getProject().getName());
        descriptionBuilder.append("\nEnvironment:");
        descriptionBuilder.append(instance.getProjectEnvironment().getEnvironment().getName());
        descriptionBuilder.append("\nConfiguration:");
        descriptionBuilder.append(instance.getConfiguration());

        return descriptionBuilder.toString();
    }

}
