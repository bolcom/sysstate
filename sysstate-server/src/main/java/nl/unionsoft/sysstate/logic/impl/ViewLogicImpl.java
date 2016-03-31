package nl.unionsoft.sysstate.logic.impl;

import java.util.Collections;
import java.util.Comparator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.CountDto;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.common.enums.StateBehaviour;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.util.SysStateStringUtils;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.dao.ListRequestDao;
import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.dao.ViewDao;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.domain.View;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;
import nl.unionsoft.sysstate.util.CountUtil;

@Service("viewLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ViewLogicImpl implements ViewLogic {

    private static final Logger LOG = LoggerFactory.getLogger(ViewLogicImpl.class);
    
    @Inject
    @Named("listRequestDao")
    private ListRequestDao listRequestDao;

    @Inject
    @Named("viewDao")
    private ViewDao viewDao;

    @Inject
    private TemplateLogic templateLogic;

    @Inject
    private StateLogic stateLogic;
    
    @Inject
    private InstanceLogic instanceLogic;

    @Inject
    private TemplateDao templateDao;

    @Inject
    private FilterDao filterDao;

    @Inject
    @Named("viewConverter")
    private Converter<ViewDto, View> viewConverter;

    public ListResponse<ViewDto> getViews(ListRequest listRequest) {
        return listRequestDao.getResults(View.class, listRequest, viewConverter);
    }

    public List<ViewDto> getViews() {
        final ListRequest listRequest = new ListRequest();
        return listRequestDao.getResults(View.class, listRequest, viewConverter).getResults();
    }

    public void createOrUpdateView(ViewDto viewDto) {

        View view = new View();
        view.setId(viewDto.getId());
        view.setCommonTags(viewDto.getCommonTags());
        view.setName(viewDto.getName());
        Filter filter = null;
        if (viewDto.getFilter() != null && viewDto.getFilter().getId() != null) {
            filter = filterDao.getFilter(viewDto.getFilter().getId());
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
        return StringUtils.lowerCase(StringUtils.replaceEach(input, new String[] { " ", "_" }, new String[] { "-","-"}));
    }

    @Override
    public ViewResultDto getViewResults(ViewDto view) {
        FilterDto filter = view.getFilter();
        if (filter == null) {
            filter = new FilterDto();
        }
        final ViewResultDto viewResult = new ViewResultDto(view);
        
        final ListResponse<InstanceDto> instanceListResponse = instanceLogic.getInstances(filter);

        final List<InstanceDto> instances = instanceListResponse.getResults();
        stateLogic.addStates(instances, StateBehaviour.CACHED);
        
        Set<ProjectEnvironmentDto> projectEnvironments = getAllProjectEnvironmentsFromInstances(instances);
        enrichProjectEnvironments(projectEnvironments, instances, view.getCommonTags());
        viewResult.getProjectEnvironments().addAll(projectEnvironments);
        
        viewResult.getEnvironments().addAll(getEnvironmentsFromProjectEnvironments(projectEnvironments));
        viewResult.getProjects().addAll(getProjectsFromProjectEnvironments(projectEnvironments));
        
        viewResult.getInstances().addAll(instances);
        countInstances(instances, viewResult.getInstanceCount());
        
        sortViewResult(viewResult);

        return viewResult;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void sortViewResult(final ViewResultDto viewResult) {
        final Comparator orderComparator = new BeanComparator("order", new NullComparator(ComparableComparator.getInstance()));
        Collections.sort(viewResult.getProjects(), orderComparator);
        Collections.sort(viewResult.getEnvironments(), orderComparator);
    }

    private void enrichProjectEnvironments(Set<ProjectEnvironmentDto> projectEnvironments, List<InstanceDto> instances, String commonDescriptionTags) {
        projectEnvironments.parallelStream().forEach(projectEnvironment -> {
            LOG.debug("Enriching projectEnvironment [{}]...", projectEnvironment);
            CountDto count = projectEnvironment.getCount();
            
            List<InstanceDto> projectEnvironmentInstances = instances.stream().filter(i -> i.getProjectEnvironment().equals(projectEnvironment)).collect(Collectors.toList());
            if (projectEnvironmentInstances.size() == 1){
                InstanceDto instance = projectEnvironmentInstances.get(0);
                StateDto state = instance.getState();
                
                CountUtil.add(count, state.getState());
                projectEnvironment.setDescription(state.getDescription());
                projectEnvironment.setState(state.getState());
                projectEnvironment.getInstances().add(instance);
            } else {
                TreeSet<String> descriptions = new TreeSet<String>();
                projectEnvironmentInstances.forEach(instance -> {
                    StateDto state = instance.getState();
                    if (StringUtils.isNotEmpty(state.getDescription()) && SysStateStringUtils.isTagMatch(instance.getTags(), commonDescriptionTags)){
                        descriptions.add(state.getDescription());    
                    }
                    LOG.debug("Adding instance [{}] to projectEnvironment [{}]", instance, projectEnvironment);
                    projectEnvironment.getInstances().add(instance);
                    projectEnvironment.setState(StateType.transfer(projectEnvironment.getState(), state.getState()));
                    CountUtil.add(count, state.getState());
                });
                if (descriptions.size() == 1){
                    projectEnvironment.setDescription(descriptions.first());
                }
            }
        });
    }

    private void countInstances(List<InstanceDto> instances, CountDto count) {
        instances.stream().forEach(instance -> CountUtil.add(count, instance.getState().getState()));
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

    
    private Set<ProjectEnvironmentDto> getAllProjectEnvironmentsFromInstances(List<InstanceDto> instances){
        return instances.stream().map(InstanceDto::getProjectEnvironment).distinct().collect(Collectors.toSet());
    }
  

}
