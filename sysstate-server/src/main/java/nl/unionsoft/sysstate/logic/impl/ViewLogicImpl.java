package nl.unionsoft.sysstate.logic.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.commons.converter.ListConverter;
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
import nl.unionsoft.sysstate.common.logic.TemplateLogic;
import nl.unionsoft.sysstate.common.util.SysStateStringUtils;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.dao.ViewDao;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.domain.View;
import nl.unionsoft.sysstate.logic.FilterLogic;
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
        Optional<View> optView = viewDao.getView(viewDto.getName());
        if (optView.isPresent()){
            view = optView.get();
        }
        
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

    public void delete(String name) {
        Optional<View> optView = viewDao.getView(name);
        if (optView.isPresent()) {
            viewDao.delete(optView.get().getId());
        }
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
    public Optional<ViewDto> getView(String name) {
        if (NumberUtils.isDigits(name)) {
            return getView(Long.valueOf(name));
        }
        return OptionalConverter.convert(viewDao.getView(name), viewConverter);
    }

    @Override
    public ViewResultDto getViewResults(ViewDto view) {
        Long now = System.currentTimeMillis();
        Map<Long, Long> timings = new LinkedHashMap<>();
        final ViewResultDto viewResult = new ViewResultDto(view);
        final List<InstanceStateDto> instanceStates = getInstanceStatesForView(view);
        timings.put(1L, System.currentTimeMillis() - now);
        Set<ProjectEnvironmentDto> projectEnvironments = getAllProjectEnvironmentsFromInstances(instanceStates);
        timings.put(2L, System.currentTimeMillis() - now);
        enrichProjectEnvironments(projectEnvironments, instanceStates, view.getCommonTags());
        timings.put(3L, System.currentTimeMillis() - now);
        viewResult.getProjectEnvironments().addAll(projectEnvironments);
        timings.put(4L, System.currentTimeMillis() - now);
        viewResult.getEnvironments().addAll(getEnvironmentsFromProjectEnvironments(projectEnvironments));
        timings.put(5L, System.currentTimeMillis() - now);
        viewResult.getProjects().addAll(getProjectsFromProjectEnvironments(projectEnvironments));
        timings.put(6L, System.currentTimeMillis() - now);
        viewResult.getInstanceStates().addAll(instanceStates);
        timings.put(7L, System.currentTimeMillis() - now);
        countInstances(instanceStates, viewResult.getInstanceCount());
        timings.put(8L, System.currentTimeMillis() - now);
        sortViewResult(viewResult);
        timings.put(9L, System.currentTimeMillis() - now);
        if (StringUtils.isNotEmpty(view.getName())) {
            viewDao.notifyRequested(view.getName(), System.currentTimeMillis() - now);
        }
        timings.put(10L, System.currentTimeMillis() - now);
        log.debug("Timings for view [{}] are [{}]", view, timings);
        return viewResult;
    }

    private List<InstanceStateDto> getInstanceStatesForView(ViewDto view) {
        FilterDto filter = view.getFilter();
        if (filter == null) {
            log.debug("No filter defined for view [{}], returning results for new Filter", view);
            return instanceStateLogic.getInstanceStates(new FilterDto());
        }

        if (filter.getId() == null || filter.getId() == 0) {
            log.debug("Filter is defined for view [{}], but has no id. Returning results for given filter", view);
            return instanceStateLogic.getInstanceStates(filter);
        }

        log.debug("Returning results based on subscribed instances to filter for view [{}].", view);
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
            log.trace("Enriching projectEnvironment [{}]...", projectEnvironment);
            CountDto count = projectEnvironment.getCount();
            TreeSet<String> descriptions = new TreeSet<String>();

            instanceStates.stream()
                    .filter(i -> i.getInstance().getProjectEnvironment().equals(projectEnvironment))
                    .forEach(instanceState -> {
                InstanceDto instance = instanceState.getInstance();
                StateDto state = instanceState.getState();
                if (StringUtils.isNotEmpty(state.getDescription())
                        && (SysStateStringUtils.isTagMatch(instance.getTags(), commonDescriptionTags) || StringUtils.isEmpty(commonDescriptionTags))) {
                    descriptions.add(state.getDescription());
                }
                log.trace("Adding instance [{}] to projectEnvironment [{}]", instance, projectEnvironment);
                projectEnvironment.getInstances().add(instance);
                projectEnvironment.setState(StateType.transfer(projectEnvironment.getState(), state.getState()));
                CountUtil.add(count, state.getState());
            });
            if (descriptions.size() == 1) {
                projectEnvironment.setDescription(descriptions.first());
            }
        });
    }

    private void countInstances(List<InstanceStateDto> instancesStates, CountDto count) {
        instancesStates.parallelStream()
                .forEach(instanceState -> CountUtil.add(count, instanceState.getState().getState()));
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

}
