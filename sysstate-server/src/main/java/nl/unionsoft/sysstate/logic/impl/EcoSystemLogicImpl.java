package nl.unionsoft.sysstate.logic.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.CountDto;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.logic.EcoSystemLogic;
import nl.unionsoft.sysstate.util.CountUtil;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("ecoSystemLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EcoSystemLogicImpl implements EcoSystemLogic {

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @SuppressWarnings("unchecked")
    @Cacheable("viewResultCache")
    public ViewResultDto getEcoSystem(ViewDto view) {

        FilterDto filter = view.getFilter();
        if (filter == null) {
            filter = new FilterDto();
        }
        final ListResponse<InstanceDto> result = instanceLogic.getInstances(filter);
        final ViewResultDto viewResult = new ViewResultDto(view);
        final CountDto instanceCount = viewResult.getInstanceCount();
        final List<EnvironmentDto> environments = viewResult.getEnvironments();
        final List<ProjectDto> projects = viewResult.getProjects();

        final List<InstanceDto> instances = result.getResults();
        viewResult.getInstances().addAll(instances);
        for (final InstanceDto instance : instances) {
            final ProjectEnvironmentDto projectEnvironment = instance.getProjectEnvironment();
            final ProjectDto project = projectEnvironment.getProject();
            addProjectIfNotExists(projects, project);
            final EnvironmentDto environment = projectEnvironment.getEnvironment();
            addEnvironmentsIfNotExists(environments, environment);
            CountUtil.add(instanceCount, instance.getState().getState());
        }
        // Comparator<Object> orderComparator = new BeanComparator("order", new
        // NullComparator(new ReverseComparator()));
        final Comparator<Object> orderComparator = new BeanComparator("order", new NullComparator(ComparableComparator.getInstance()));
        Collections.sort(projects, orderComparator);
        Collections.sort(environments, orderComparator);
        return viewResult;
    }

    private void addProjectIfNotExists(final List<ProjectDto> projects, ProjectDto project) {
        boolean projectAlreadyInList = false;
        for (final ProjectDto searchProject : projects) {
            if (searchProject.getId().equals(project.getId())) {
                projectAlreadyInList = true;
                break;
            }
        }
        if (!projectAlreadyInList) {
            projects.add(project);
        }
    }

    private void addEnvironmentsIfNotExists(final List<EnvironmentDto> environments, EnvironmentDto environment) {
        boolean environmentAlreadyInList = false;
        for (final EnvironmentDto searcEnvironment : environments) {
            if (searcEnvironment.getId().equals(environment.getId())) {
                environmentAlreadyInList = true;
                break;
            }
        }
        if (!environmentAlreadyInList) {
            environments.add(environment);
        }
    }

}