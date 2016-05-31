package nl.unionsoft.sysstate.job;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.logic.FilterLogic;

public class InstanceFilterLinkJob extends AutowiringJob {

    private static final Logger log = LoggerFactory.getLogger(UpdateInstanceJob.class);

    @Inject
    private FilterLogic filterLogic;

    @Inject
    private InstanceLogic instanceLogic;

    @Override
    public void execute(JobExecutionContext context, ApplicationContext applicationContext) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        long filterId = jobDataMap.getLong("filterId");
        log.debug("Updating FilterInstances for filter with id [{}]", filterId);
        Optional<FilterDto> optFilter = filterLogic.getFilter(filterId);
        if (!optFilter.isPresent()) {
            log.warn("Filter with id [{}] is no longer present.", filterId);
            return;
        }

        FilterDto filter = optFilter.get();
        DateTime tenMinutesAgo = new DateTime(new Date()).minusMinutes(10);
        if (filter.getLastQueryDate() == null || new DateTime(filter.getLastQueryDate()).isBefore(tenMinutesAgo)) {
            log.debug("Not updating filter with id [{}] as it hasn't been queried for more then 10 minutes.", filterId);
            return;
        }

        List<Long> actualInstanceIds = instanceLogic.getInstances(filter).stream().map(i -> i.getId()).collect(Collectors.toList());
        List<Long> currentIstanceIds = instanceLogic.getInstances(filter.getId()).stream().map(i -> i.getId()).collect(Collectors.toList());

        // Remove all instances that cannot be found in the list of actualInstanceIds (no longer present)
        currentIstanceIds.stream().filter(id -> !actualInstanceIds.contains(id)).forEach(id -> filterLogic.removeInstanceFromFilter(filter.getId(), id));

        // Add all instances that cannot be found in the list of currentInstanceIds (new)
        actualInstanceIds.stream().filter(id -> !currentIstanceIds.contains(id)).forEach(id -> filterLogic.addInstanceToFilter(filter.getId(), id));
    }

}
