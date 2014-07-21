package nl.unionsoft.sysstate.job;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class UpdateInstanceJob extends AutowiringJob {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateInstanceJob.class);

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("stateResolverLogic")
    private StateResolverLogic stateResolverLogic;

    @Override
    public void execute(final JobExecutionContext context, final ApplicationContext applicationContext) throws JobExecutionException {

        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        long instanceId = jobDataMap.getLong("instanceId");

        InstanceDto instance = instanceLogic.getInstance(instanceId);
        LOG.info("Starting job with instance '{}'", instance);
        try {

            final StateDto state = stateLogic.requestStateForInstance(instance);
            finalizeState(instance, state);
            LOG.info("job for instance '{}' completed, state: {}", instance, state);
        } catch (final Exception e) {
            LOG.error("job for instance '{}' failed, caught Exception!", e);
        }

    }

    private void finalizeState(final InstanceDto instance, final StateDto state) {
        // final Long instanceId = instance.getId();
        // final List<InstanceWorkerPluginConfig> instanceWorkerPluginConfigs = instanceWorkerPluginLogic.getInstanceWorkerPluginConfigs(instanceId);
        // handleRatings(instanceWorkerPluginConfigs, state, instance);
        stateLogic.createOrUpdate(state);
        // handleNotifiers(instanceWorkerPluginConfigs, state, instance);
    }

    // private void handleNotifiers(final List<InstanceWorkerPluginConfig> instanceWorkerPluginConfigs, final StateDto state, final InstanceDto instance) {
    // if (instance.isEnabled()) {
    // if (instanceWorkerPluginConfigs != null) {
    // // for (final InstanceWorkerPluginConfig instanceWorkerPluginConfig : instanceWorkerPluginConfigs) {
    // // final Plugin plugin = pluginLogic.getPlugin(instanceWorkerPluginConfig.getPluginClass());
    // // if (plugin instanceof NotifierPlugin) {
    // // final NotifierPlugin notifierPlugin = (NotifierPlugin) plugin;
    // // final String configuration = instanceWorkerPluginConfig.getConfiguration();
    // // notifierPlugin.notify(state, instance.getState(), PropertiesUtil.stringToProperties(configuration));
    // // }
    // // }
    // }
    // }
    // }

    // private void handleRatings(final List<InstanceWorkerPluginConfig> instanceWorkerPluginConfigs, final StateDto state, final InstanceDto instance) {
    // int stateRating = 100;
    // int ratingCount = 0;
    // if (instance.isEnabled()) {
    // boolean first = true;
    // if (instanceWorkerPluginConfigs != null) {
    // for (final InstanceWorkerPluginConfig instanceWorkerPluginConfig : instanceWorkerPluginConfigs) {
    // final String pluginClass = instanceWorkerPluginConfig.getPluginClass();
    // final StateResolver plugin = stateResolverLogic.getStateResolver(pluginClass);
    // if (plugin instanceof RatingResolver) {
    // final StringBuilder ratingMessageBuilder = new StringBuilder(500);
    // final RatingResolver ratingPlugin = (RatingResolver) plugin;
    // final Rating rating = ratingPlugin.rating(state, PropertiesUtil.stringToProperties(instanceWorkerPluginConfig.getConfiguration()));
    // final String message = rating.getMessage();
    // if (StringUtils.isNotBlank(message)) {
    // if (first) {
    // state.appendMessage("\nRatings:\n");
    // first = false;
    // }
    // ratingMessageBuilder.append("Rating for ");
    // ratingMessageBuilder.append(pluginClass);
    // ratingMessageBuilder.append(" reported a rating of '");
    // ratingMessageBuilder.append(rating.getRating());
    // ratingMessageBuilder.append("', message: ");
    // ratingMessageBuilder.append(rating.getMessage());
    // ratingMessageBuilder.append('\n');
    // state.appendMessage(ratingMessageBuilder.toString());
    // }
    // final int theRating = rating.getRating();
    //
    // if (theRating >= 0) {
    //
    // ratingCount++;
    // if (theRating < stateRating) {
    // stateRating = theRating;
    // }
    // }
    // }
    //
    // }
    // }
    // if (ratingCount == 0) {
    // state.setRating(-1);
    // } else {
    // state.setRating(stateRating);
    // }
    // } else {
    // state.setRating(-1);
    // }
    //
    // }

}
