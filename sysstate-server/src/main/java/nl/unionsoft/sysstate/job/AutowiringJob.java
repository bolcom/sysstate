package nl.unionsoft.sysstate.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.springframework.context.ApplicationContext;

public abstract class AutowiringJob implements StatefulJob {

    public void execute(final JobExecutionContext context) throws JobExecutionException {
        try {
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get("applicationContext");
            applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
            execute(context, applicationContext);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }

    }

    public abstract void execute(final JobExecutionContext context, ApplicationContext applicationContext) throws JobExecutionException;

}
