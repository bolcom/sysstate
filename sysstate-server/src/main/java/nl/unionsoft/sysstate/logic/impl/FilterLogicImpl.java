package nl.unionsoft.sysstate.logic.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
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
    private TaskScheduler scheduler;

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

        // Reset counters
        filter.setLastQueryDate(null);
        filter.setLastQueryTime(0L);
        filter.setAverageQueryTime(0L);
        filter.setQueryCount(0L);
        filterDao.createOrUpdate(filter);
        dto.setId(filter.getId());
    }

    public void delete(Long filterId) {
        filterDao.delete(filterId);
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

    @Override
    public void updateFilterSubscriptions(FilterDto filter) {
        logger.debug("Updating FilterInstances for filter with id [{}]", filter);

        List<Long> actualInstanceIds = instanceDao.getInstances(filter).stream().map(i -> i.getId()).collect(Collectors.toList());
        List<Long> currentIstanceIds = instanceDao.getInstances(filter.getId()).stream().map(i -> i.getId()).collect(Collectors.toList());

        // Remove all instances that cannot be found in the list of actualInstanceIds (no longer present)
        currentIstanceIds.stream().filter(id -> !actualInstanceIds.contains(id)).forEach(id -> removeInstanceFromFilter(filter.getId(), id));

        // Add all instances that cannot be found in the list of currentInstanceIds (new)
        actualInstanceIds.stream().filter(id -> !currentIstanceIds.contains(id)).forEach(id -> addInstanceToFilter(filter.getId(), id));

        updateLastSyncDate(filter.getId());
    }

    private void updateLastSyncDate(Long filterId) {
        Optional<Filter> optfilter = filterDao.getFilter(filterId);
        if (optfilter.isPresent()) {
            Filter filter = optfilter.get();
            filter.setLastSyncDate(new Date());
            filterDao.createOrUpdate(filter);
        }
    }

    public void scheduleUpdate(final long filterId) {
        InstanceFilterLinkJob instanceFilterLinkJob = new InstanceFilterLinkJob(this, filterId);
        scheduler.schedule(instanceFilterLinkJob, new Date());
    }

}
