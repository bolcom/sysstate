package nl.unionsoft.sysstate.job;

import javax.inject.Inject;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import nl.unionsoft.sysstate.logic.FilterLogic;

public class InstanceFilterLinkJob extends AutowiringJob {

    @Inject
    private FilterLogic filterLogic;

    @Override
    public void execute(JobExecutionContext context, ApplicationContext applicationContext) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        long filterId = jobDataMap.getLong("filterId");
        filterLogic.updateFilterSubscriptions(filterId);
    }

}
