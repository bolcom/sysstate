package nl.unionsoft.sysstate.job;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.logic.HttpClientLogic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class HttpClientCleanUpJob extends AutowiringJob {

    @Inject
    @Named("httpClientLogic")
    private HttpClientLogic httpClientLogic;

    @Override
    public void execute(final JobExecutionContext context, final ApplicationContext applicationContext) throws JobExecutionException {
        httpClientLogic.closeIdleConnections();
    }
}
