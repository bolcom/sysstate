package nl.unionsoft.sysstate.job;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
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

        List<InstanceDto> instances = instanceLogic.getInstances();
        instances.forEach(instance -> {
            if (instanceIsApplicableForFilter(instance, filter)) {
                filterLogic.addInstanceToFilter(filter.getId(), instance.getId());
            } else {
                filterLogic.removeInstanceFromFilter(filter.getId(), instance.getId());
            }
        });

    }

    private boolean instanceIsApplicableForFilter(InstanceDto instance, FilterDto filter) {

        ProjectEnvironmentDto projectEnvironment = instance.getProjectEnvironment();
        ProjectDto project = projectEnvironment.getProject();
        EnvironmentDto environment = projectEnvironment.getEnvironment();
        if (filter.getProjects().contains(projectEnvironment.getProject().getId())
                || filter.getEnvironments().contains(projectEnvironment.getEnvironment().getId())) {
            return true;
        }

        String search = filter.getSearch();
        if (StringUtils.containsIgnoreCase(instance.getName(), search) || StringUtils.containsIgnoreCase(instance.getHomepageUrl(), search)
                || StringUtils.containsIgnoreCase(instance.getHomepageUrl(), search) || StringUtils.containsIgnoreCase(instance.getPluginClass(), search)
                || StringUtils.containsIgnoreCase(project.getName(), search) || StringUtils.containsIgnoreCase(environment.getName(), search)
                || StringUtils.containsIgnoreCase(projectEnvironment.getHomepageUrl(), search)) {
            return true;
        }

        Set<String> allTags = new HashSet<>();
        allTags.addAll(Arrays.asList(StringUtils.split(StringUtils.defaultString(instance.getTags()), ' ')));
        allTags.addAll(Arrays.asList(StringUtils.split(StringUtils.defaultString(project.getTags()), ' ')));
        allTags.addAll(Arrays.asList(StringUtils.split(StringUtils.defaultString(environment.getTags()), ' ')));

        for (String tag : StringUtils.split(StringUtils.defaultString(filter.getTags()), ' ')) {
            if (allTags.contains(tag)) {
                return true;
            }
        }

        if (filter.getStateResolvers().contains(instance.getPluginClass())) {
            return true;
        }

        return false;
    }

}
