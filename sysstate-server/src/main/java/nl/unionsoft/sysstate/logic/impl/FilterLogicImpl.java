package nl.unionsoft.sysstate.logic.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.job.InstanceFilterLinkJob;
import nl.unionsoft.sysstate.logic.FilterLogic;

@Service("filterLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FilterLogicImpl implements FilterLogic {

    private static final Logger logger = LoggerFactory.getLogger(FilterLogicImpl.class);
    
    @Inject
    @Named("filterConverter")
    private Converter<FilterDto, Filter> filterConverter;

    @Inject
    @Named("filterDao")
    private FilterDao filterDao;

    @Inject
    private InstanceDao instanceDao;
    
    @Inject
    @Named("scheduler")
    private Scheduler scheduler;

    public List<FilterDto> getFilters() {
        return ListConverter.convert(filterConverter, filterDao.getFilters());
    }

    public Optional<FilterDto> getFilter(final Long filterId) {
        return OptionalConverter.convert(filterDao.getFilter(filterId), filterConverter);
    }

    public void createOrUpdate(final FilterDto dto) {
        final Filter filter = new Filter();
        filter.setId(dto.getId());
        filter.setEnvironments(dto.getEnvironments());
        filter.setName(dto.getName());
        filter.setProjects(dto.getProjects());
        filter.setSearch(dto.getSearch());
        filter.setStateResolvers(dto.getStateResolvers());
        filter.setTags(dto.getTags());
        
        //Reset counters
        filter.setLastQueryDate(null);
        filter.setLastQueryTime(0L);
        filter.setAverageQueryTime(0L);
        filter.setQueryCount(0L);
        
        filterDao.createOrUpdate(filter);
        dto.setId(filter.getId());
        updateTriggerJob(filter.getId());
    }

    public void delete(Long filterId) {
        filterDao.delete(filterId);
        removeTriggerJob(filterId);

    }

    @Override
    public void addInstanceToFilter(Long filterId, Long instanceId) {
        logger.debug("Adding instance with id [{}] from filter with id [{}]", instanceId, filterId);
        filterDao.addInstanceToFilter(filterId, instanceId);

    }

    @Override
    public void removeInstanceFromFilter(Long filterId, Long instanceId) {
        logger.debug("Removing instance with id [{}] from filter with id [{}]", instanceId, filterId);
        filterDao.removeInstanceFromFilter(filterId, instanceId);
    }

    private void addTriggerJob(final long filterId) {

        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setName("filter-" + filterId + "-job");
        jobDetailFactoryBean.setGroup("filters");
        jobDetailFactoryBean.setJobClass(InstanceFilterLinkJob.class);
        Map<String, Object> jobData = new HashMap<String, Object>();
        jobData.put("filterId", filterId);
        jobDetailFactoryBean.setJobDataAsMap(jobData);
        jobDetailFactoryBean.afterPropertiesSet();
        final JobDetail jobDetail = jobDetailFactoryBean.getObject();

        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setName("filter-" + filterId + "-trigger");
        simpleTriggerFactoryBean.setRepeatCount(-1);
        simpleTriggerFactoryBean.setRepeatInterval(30000);
        simpleTriggerFactoryBean.setStartTime(new Date(System.currentTimeMillis() + 5000));
        simpleTriggerFactoryBean.setJobDetail(jobDetail);
        simpleTriggerFactoryBean.afterPropertiesSet();
        final SimpleTrigger trigger = simpleTriggerFactoryBean.getObject();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (final SchedulerException e) {
            e.printStackTrace();
        }
    }
    
    public void removeTriggerJob(final long filterId) {
        try {
            final String jobName = "filter-" + filterId + "-job";
            final String groupName = "filters";
            scheduler.deleteJob(new JobKey(jobName, groupName));
        } catch (final SchedulerException e1) {
            e1.printStackTrace();
        }
    }
    
    private void updateTriggerJob(final Long filterId) {
        logger.info("Creating or updating queue job for filter with id: {}", filterId);
        removeTriggerJob(filterId);
        addTriggerJob(filterId);
    }
    
    @PostConstruct
    public void postConstruct() throws Exception {
        filterDao.getFilters().parallelStream().forEach( f -> addTriggerJob(f.getId()));
    }

    @PreDestroy
    public void preDestroy() throws Exception {
        filterDao.getFilters().parallelStream().forEach( f -> removeTriggerJob(f.getId()));
    }

    @Override
    public void updateFilterSubscriptions(Long filterId) {
        logger.debug("Updating FilterInstances for filter with id [{}]", filterId);
        Optional<FilterDto> optFilter = getFilter(filterId);
        if (!optFilter.isPresent()) {
            logger.warn("Filter with id [{}] is no longer present.", filterId);
            return;
        }

        FilterDto filter = optFilter.get();
        DateTime tenMinutesAgo = new DateTime(new Date()).minusMinutes(10);
        if (filter.getLastQueryDate() == null || new DateTime(filter.getLastQueryDate()).isBefore(tenMinutesAgo)) {
            logger.debug("Not updating filter with id [{}] as it hasn't been queried for more then 10 minutes.", filterId);
            return;
        }

        List<Long> actualInstanceIds = instanceDao.getInstances(filter).stream().map(i -> i.getId()).collect(Collectors.toList());
        List<Long> currentIstanceIds = instanceDao.getInstances(filter.getId()).stream().map(i -> i.getId()).collect(Collectors.toList());

        // Remove all instances that cannot be found in the list of actualInstanceIds (no longer present)
        currentIstanceIds.stream().filter(id -> !actualInstanceIds.contains(id)).forEach(id -> removeInstanceFromFilter(filter.getId(), id));

        // Add all instances that cannot be found in the list of currentInstanceIds (new)
        actualInstanceIds.stream().filter(id -> !currentIstanceIds.contains(id)).forEach(id -> addInstanceToFilter(filter.getId(), id));
        
    }


}
