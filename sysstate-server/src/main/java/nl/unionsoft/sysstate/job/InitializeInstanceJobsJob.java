package nl.unionsoft.sysstate.job;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class InitializeInstanceJobsJob extends AutowiringJob {

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Override
    public void execute(final JobExecutionContext context, final ApplicationContext applicationContext) throws JobExecutionException {
        for (final InstanceDto instance : instanceLogic.getInstances()) {
            instanceLogic.addTriggerJob(instance.getId());
        }
    }

}
