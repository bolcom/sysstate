package nl.unionsoft.sysstate.queue;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.xeoh.plugins.base.Plugin;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.plugins.NotifierPlugin;
import nl.unionsoft.sysstate.common.plugins.RatingPlugin;
import nl.unionsoft.sysstate.common.plugins.RatingPlugin.Rating;
import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;
import nl.unionsoft.sysstate.converter.StateConverter;
import nl.unionsoft.sysstate.domain.InstanceWorkerPluginConfig;
import nl.unionsoft.sysstate.logic.InstanceWorkerPluginLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchStateWorker implements ReferenceRunnable {
    private static final Logger LOG = LoggerFactory.getLogger(FetchStateWorker.class);
    private final InstanceDto instance;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("instanceWorkerPluginLogic")
    private InstanceWorkerPluginLogic instanceWorkerPluginLogic;

    @Inject
    @Named("stateConverter")
    private StateConverter stateConverter;

    public FetchStateWorker (InstanceDto instance) {
        this.instance = instance;
    }

    public void run() {
        LOG.info("Starting job with instance '{}'", instance);
        try {
            final StateDto state = stateLogic.requestStateForInstance(instance);
            finalizeState(state);
            LOG.info("job for instance '{}' completed, state: {}", instance, state);
        } catch(final Exception e) {
            LOG.error("job for instance '{}' failed, caught Exception!", e);
        }

    }

    private void finalizeState(StateDto state) {
        final Long instanceId = instance.getId();
        final List<InstanceWorkerPluginConfig> instanceWorkerPluginConfigs = instanceWorkerPluginLogic.getInstanceWorkerPluginConfigs(instanceId);
        handleRatings(instanceWorkerPluginConfigs, state);
        stateLogic.createOrUpdate(state);
        handleNotifiers(instanceWorkerPluginConfigs, state);
    }

    private void handleNotifiers(List<InstanceWorkerPluginConfig> instanceWorkerPluginConfigs, StateDto state) {
        if (instance.isEnabled()) {
            if (instanceWorkerPluginConfigs != null) {
                for (final InstanceWorkerPluginConfig instanceWorkerPluginConfig : instanceWorkerPluginConfigs) {
                    final Plugin plugin = pluginLogic.getPlugin(instanceWorkerPluginConfig.getPluginClass());
                    if (plugin instanceof NotifierPlugin) {
                        final NotifierPlugin notifierPlugin = (NotifierPlugin) plugin;
                        final String configuration = instanceWorkerPluginConfig.getConfiguration();
                        notifierPlugin.notify(state, instance.getState(), PropertiesUtil.stringToProperties(configuration));
                    }
                }
            }
        }
    }

    private void handleRatings(List<InstanceWorkerPluginConfig> instanceWorkerPluginConfigs, StateDto state) {
        int stateRating = 100;
        int ratingCount = 0;
        if (instance.isEnabled()) {
            boolean first = true;
            if (instanceWorkerPluginConfigs != null) {
                for (final InstanceWorkerPluginConfig instanceWorkerPluginConfig : instanceWorkerPluginConfigs) {
                    final String pluginClass = instanceWorkerPluginConfig.getPluginClass();
                    final Plugin plugin = pluginLogic.getPlugin(pluginClass);
                    if (plugin instanceof RatingPlugin) {
                        final StringBuilder ratingMessageBuilder = new StringBuilder(500);
                        final RatingPlugin ratingPlugin = (RatingPlugin) plugin;
                        final Rating rating = ratingPlugin.rating(state, PropertiesUtil.stringToProperties(instanceWorkerPluginConfig.getConfiguration()));
                        final String message = rating.getMessage();
                        if (StringUtils.isNotBlank(message)) {
                            if (first) {
                                state.appendMessage("\nRatings:\n");
                                first = false;
                            }
                            ratingMessageBuilder.append("Rating for ");
                            ratingMessageBuilder.append(pluginClass);
                            ratingMessageBuilder.append(" reported a rating of '");
                            ratingMessageBuilder.append(rating.getRating());
                            ratingMessageBuilder.append("', message: ");
                            ratingMessageBuilder.append(rating.getMessage());
                            ratingMessageBuilder.append('\n');
                            state.appendMessage(ratingMessageBuilder.toString());
                        }
                        final int theRating = rating.getRating();

                        if (theRating >= 0) {

                            ratingCount++;
                            if (theRating < stateRating) {
                                stateRating = theRating;
                            }
                        }
                    }

                }
            }
            if (ratingCount == 0) {
                state.setRating(-1);
            } else {
                state.setRating(stateRating);
            }
        } else {
            state.setRating(-1);
        }
    }

    public String getReference() {
        return "fetchStateWorker-" + String.valueOf(instance.getId());
    }

    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Fetching state for instance with name:");
        stringBuilder.append(instance.getName());
        stringBuilder.append("\nProject:");
        stringBuilder.append(instance.getProjectEnvironment().getProject().getName());
        stringBuilder.append("\nEnvironment:");
        stringBuilder.append(instance.getProjectEnvironment().getEnvironment().getName());
        stringBuilder.append("\nConfiguration:");
        stringBuilder.append(instance.getConfiguration());

        
        return stringBuilder.toString();
    }

}
