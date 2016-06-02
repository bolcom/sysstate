package nl.unionsoft.sysstate.logic.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.CountDto;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceStateDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceStateLogic;
import nl.unionsoft.sysstate.common.util.SysStateStringUtils;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.dao.ViewDao;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.domain.View;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;
import nl.unionsoft.sysstate.util.CountUtil;

@Service("viewLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ViewLogicImpl implements ViewLogic {

    private static final Logger log = LoggerFactory.getLogger(ViewLogicImpl.class);

    @Inject
    @Named("viewDao")
    private ViewDao viewDao;

    @Inject
    private TemplateLogic templateLogic;

    @Inject
    private InstanceStateLogic instanceStateLogic;

    @Inject
    private StateLogic stateLogic;

    @Inject
    private TemplateDao templateDao;

    @Inject
    private FilterDao filterDao;
    
    @Inject
    private FilterLogic filterLogic;

    @Inject
    @Named("viewConverter")
    private Converter<ViewDto, View> viewConverter;

    public List<ViewDto> getViews() {
        return ListConverter.convert(viewConverter, viewDao.getViews());
    }

    public void createOrUpdateView(ViewDto viewDto) {

        View view = new View();
        view.setId(viewDto.getId());
        view.setCommonTags(viewDto.getCommonTags());
        view.setName(viewDto.getName());
        Filter filter = null;
        if (viewDto.getFilter() != null && viewDto.getFilter().getId() != null) {
            Optional<Filter> optFilter = filterDao.getFilter(viewDto.getFilter().getId());
            if (!optFilter.isPresent()) {
                throw new IllegalStateException("Filter cannot be found.");
            }
            filter = optFilter.get();

        }
        view.setFilter(filter);
        Template template = null;
        if (viewDto.getTemplate() != null) {
            Optional<Template> optTemplate = templateDao.getTemplate(viewDto.getTemplate().getName());
            if (optTemplate.isPresent()) {
                template = optTemplate.get();
            }
        }
        view.setTemplate(template);
        // Reset counters
        view.setLastRequestDate(null);
        view.setLastRequestTime(0L);
        view.setAverageRequestTime(0L);
        view.setRequestCount(0L);
        
        viewDao.createOrUpdateView(view);
    }

    public void delete(Long id) {
        viewDao.delete(id);

    }

    public Optional<ViewDto> getView(Long viewId) {
        return OptionalConverter.convert(viewDao.getView(viewId), viewConverter);
    }

    @Override
    public ViewDto getBasicView() {
        ViewDto view = new ViewDto();
        view.setTemplate(templateLogic.getBasicTemplate());
        return view;
    }

    @Override
    public Optional<ViewDto> getView(String viewId) {
        if (NumberUtils.isDigits(viewId)) {
            return getView(Long.valueOf(viewId));
        }
        //@formatter:off
            return getViews()
                    .stream()
                    .filter(v -> StringUtils.equals(normalizeViewId(v.getName()), normalizeViewId(viewId)))
                    .findFirst();
        //@formatter:on        
    }

    private String normalizeViewId(String input) {
        return StringUtils.lowerCase(StringUtils.replaceEach(input, new String[] { " ", "_" }, new String[] { "-", "-" }));
    }

    @Override
    public ViewResultDto getViewResults(ViewDto view) {
        Long now = System.currentTimeMillis();
        final ViewResultDto viewResult = new ViewResultDto(view);
        final List<InstanceStateDto> instanceStates = getInstanceStatesForView(view);
        Set<ProjectEnvironmentDto> projectEnvironments = getAllProjectEnvironmentsFromInstances(instanceStates);
        enrichProjectEnvironments(projectEnvironments, instanceStates, view.getCommonTags());
        viewResult.getProjectEnvironments().addAll(projectEnvironments);
        viewResult.getEnvironments().addAll(getEnvironmentsFromProjectEnvironments(projectEnvironments));
        viewResult.getProjects().addAll(getProjectsFromProjectEnvironments(projectEnvironments));
        viewResult.getInstanceStates().addAll(instanceStates);
        countInstances(instanceStates, viewResult.getInstanceCount());
        sortViewResult(viewResult);
        if (view.getId() != null && view.getId() > 0){
            viewDao.notifyRequested(view.getId(), System.currentTimeMillis() - now);    
        }        
        return viewResult;
    }

    private List<InstanceStateDto> getInstanceStatesForView(ViewDto view) {
        FilterDto filter = view.getFilter();
        if (filter == null) {
            log.debug("No filter defined, returning results for new Filter");
            return instanceStateLogic.getInstanceStates(new FilterDto());
        }

        if (filter.getId() == null || filter.getId() == 0) {
            log.debug("Filter is defined, but has no id. Returning results for given filter");
            return instanceStateLogic.getInstanceStates(filter);
        }
        log.debug("Validating if filterdata is still up to date");
        if (isFilterDataExpired(filter)) {
            log.debug("FilterDate needs to be updated, scheduling update...");
            filterLogic.scheduleUpdate(filter.getId());
        }
        
        log.debug("Returning results based on subscribed instances to filter.");
        return instanceStateLogic.getInstanceStates(filter.getId());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void sortViewResult(final ViewResultDto viewResult) {
        final Comparator orderComparator = new BeanComparator("order", new NullComparator(ComparableComparator.getInstance()));
        Collections.sort(viewResult.getProjects(), orderComparator);
        Collections.sort(viewResult.getEnvironments(), orderComparator);
    }

    private void enrichProjectEnvironments(Set<ProjectEnvironmentDto> projectEnvironments, List<InstanceStateDto> instanceStates,
            String commonDescriptionTags) {
        projectEnvironments.parallelStream().forEach(projectEnvironment -> {
            log.debug("Enriching projectEnvironment [{}]...", projectEnvironment);
            CountDto count = projectEnvironment.getCount();

            List<InstanceDto> projectEnvironmentInstances = instanceStates
                    .stream()
                    .filter(i -> i.getInstance().getProjectEnvironment().equals(projectEnvironment))
                    .map(InstanceStateDto::getInstance)
                    .collect(Collectors.toList());
            if (projectEnvironmentInstances.size() == 1) {
                InstanceDto instance = projectEnvironmentInstances.get(0);
                StateDto state = stateLogic.getLastStateForInstance(instance);

                CountUtil.add(count, state.getState());
                projectEnvironment.setDescription(state.getDescription());
                projectEnvironment.setState(state.getState());
                projectEnvironment.getInstances().add(instance);
            } else {
                TreeSet<String> descriptions = new TreeSet<String>();
                projectEnvironmentInstances.forEach(instance -> {
                    StateDto state = stateLogic.getLastStateForInstance(instance);
                    if (StringUtils.isNotEmpty(state.getDescription()) && SysStateStringUtils.isTagMatch(instance.getTags(), commonDescriptionTags)) {
                        descriptions.add(state.getDescription());
                    }
                    log.debug("Adding instance [{}] to projectEnvironment [{}]", instance, projectEnvironment);
                    projectEnvironment.getInstances().add(instance);
                    projectEnvironment.setState(StateType.transfer(projectEnvironment.getState(), state.getState()));
                    CountUtil.add(count, state.getState());
                });
                if (descriptions.size() == 1) {
                    projectEnvironment.setDescription(descriptions.first());
                }
            }
        });
    }

    private void countInstances(List<InstanceStateDto> instancesStates, CountDto count) {
        instancesStates.parallelStream()
                .forEach(instancesState -> CountUtil.add(count, stateLogic.getLastStateForInstance(instancesState.getInstance()).getState()));
    }

    private Set<EnvironmentDto> getEnvironmentsFromProjectEnvironments(Set<ProjectEnvironmentDto> projectEnvironments) {
        //@formatter:off
        return projectEnvironments.parallelStream()
                .map(ProjectEnvironmentDto::getEnvironment)
                .sorted((e1, e2) -> Integer.compare(e1.getOrder(), e2.getOrder()))
                .distinct()
                .collect(Collectors.toSet());
        //@formatter:on
    }

    private Set<ProjectDto> getProjectsFromProjectEnvironments(Set<ProjectEnvironmentDto> projectEnvironments) {
        //@formatter:off
        return projectEnvironments.parallelStream()
                .map(ProjectEnvironmentDto::getProject)
                .sorted((p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()))
                .distinct()
                .collect(Collectors.toSet());
        //@formatter:on
    }

    private Set<ProjectEnvironmentDto> getAllProjectEnvironmentsFromInstances(List<InstanceStateDto> instanceStates) {
        return instanceStates.stream().map(instanceState -> instanceState.getInstance().getProjectEnvironment()).distinct().collect(Collectors.toSet());
    }
    
    private boolean isFilterDataExpired(FilterDto filter) {
        DateTime threeMinutesAgo = new DateTime(new Date()).minusMinutes(3);
        //Filter has never been synced, or has been synced more then 3 minutes ago.
        return filter.getLastSyncDate() == null || new DateTime(filter.getLastSyncDate()).isBefore(threeMinutesAgo);
    }

}
