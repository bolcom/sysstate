package nl.unionsoft.sysstate.job;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.logic.StateLogic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class StateCleanupJob extends AutowiringJob {

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Override
    public void execute(final JobExecutionContext context, final ApplicationContext applicationContext) throws JobExecutionException {
        stateLogic.clean();
    }

}
