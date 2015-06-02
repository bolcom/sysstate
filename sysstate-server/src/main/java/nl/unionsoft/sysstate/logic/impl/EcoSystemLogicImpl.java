package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

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
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.util.SysStateStringUtils;
import nl.unionsoft.sysstate.logic.EcoSystemLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.util.CountUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("ecoSystemLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EcoSystemLogicImpl implements EcoSystemLogic {

    private static final Logger LOG = LoggerFactory.getLogger(EcoSystemLogicImpl.class);
    
    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Cacheable("viewResultCache")
    public ViewResultDto getEcoSystem(final ViewDto view) {

        FilterDto filter = view.getFilter();
        if (filter == null) {
            filter = new FilterDto();
        }
        final ViewResultDto viewResult = new ViewResultDto(view);
        
        
        
        final ListResponse<InstanceDto> instanceListResponse = instanceLogic.getInstances(filter);
        final List<InstanceDto> instances = instanceListResponse.getResults();
        
        Set<ProjectEnvironmentDto> projectEnvironments = getAllProjectEnvironmentsFromInstances(instances);
        enrichProjectEnvironments(projectEnvironments, instances, view.getCommonTags());
        viewResult.getProjectEnvironments().addAll(projectEnvironments);
        
        viewResult.getEnvironments().addAll(getEnvironmentsFromProjectEnvironments(projectEnvironments));
        viewResult.getProjects().addAll(getProjectsFromProjectEnvironments(projectEnvironments));
        
        viewResult.getInstances().addAll(instances);
        countInstances(instances, viewResult.getInstanceCount());

        return viewResult;
    }

    private void enrichProjectEnvironments(Set<ProjectEnvironmentDto> projectEnvironments, List<InstanceDto> instances, String commonDescriptionTags) {
        projectEnvironments.stream().parallel().forEach(projectEnvironment -> {
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
        return projectEnvironments.stream().map(ProjectEnvironmentDto::getEnvironment).sorted((e1, e2) -> Integer.compare(e1.getOrder(), e2.getOrder())).distinct().collect(Collectors.toSet());
    }

    
    private Set<ProjectDto> getProjectsFromProjectEnvironments(Set<ProjectEnvironmentDto> projectEnvironments) {
        return projectEnvironments.stream().map(ProjectEnvironmentDto::getProject).sorted((p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder())).distinct().collect(Collectors.toSet());
    }

    
    private Set<ProjectEnvironmentDto> getAllProjectEnvironmentsFromInstances(List<InstanceDto> instances){
        return instances.stream().map(InstanceDto::getProjectEnvironment).distinct().collect(Collectors.toSet());
    }
  


}
