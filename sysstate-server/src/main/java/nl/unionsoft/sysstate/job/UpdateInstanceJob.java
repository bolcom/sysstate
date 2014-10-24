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
        stateLogic.createOrUpdate(state);
    }




}
