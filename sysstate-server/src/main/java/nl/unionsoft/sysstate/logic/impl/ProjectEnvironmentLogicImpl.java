package nl.unionsoft.sysstate.logic.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.commons.converter.ConverterWithConfig;
import nl.unionsoft.commons.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.dao.EnvironmentDao;
import nl.unionsoft.sysstate.dao.ProjectDao;
import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.domain.Environment;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.Project;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;
import nl.unionsoft.sysstate.domain.State;
import nl.unionsoft.sysstate.logic.FilterLogic;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service("projectEnvironmentLogic")
@DependsOn({"projectLogic", "environmentLogic"})
public class ProjectEnvironmentLogicImpl implements ProjectEnvironmentLogic {

    @Inject
    @Named("filterLogic")
    private FilterLogic filterLogic;

    @Inject
    @Named("projectEnvironmentDao")
    private ProjectEnvironmentDao projectEnvironmentDao;

    @Inject
    @Named("projectDao")
    private ProjectDao projectDao;

    @Inject
    @Named("environmentDao")
    private EnvironmentDao environmentDao;


    
    @Inject
    @Named("stateConverter")
    private Converter<StateDto, State> stateConverter;

    @Inject
    @Named("instanceConverter")
    private Converter<InstanceDto, Instance> instanceConverter;

    @Inject
    @Named("projectEnvironmentConverter")
    private ConverterWithConfig<ProjectEnvironmentDto, ProjectEnvironment, Boolean> projectEnvironmentConverter;

    public void createOrUpdate(final ProjectEnvironmentDto dto) {

        Long id = dto.getId();
        ProjectEnvironment projectEnvironment = null;
        if (id == null || id == 0) {
            projectEnvironment = new ProjectEnvironment();
        } else {
            projectEnvironment = projectEnvironmentDao.getProjectEnvironment(id);
        }

        Project project = null;
        if (dto.getProject() != null) {
            project = projectDao.getProject(dto.getProject().getId());
        }
        projectEnvironment.setProject(project);

        Environment environment = null;
        if (dto.getEnvironment() != null) {
            environment = environmentDao.getEnvironment(dto.getEnvironment().getId());
        }

        projectEnvironment.setEnvironment(environment);
        projectEnvironment.setHomepageUrl(dto.getHomepageUrl());
        projectEnvironmentDao.createOrUpdate(projectEnvironment);
    }

    public Long createIfNotExists(final Long projectId, final Long environmentId) {
        ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(projectId, environmentId);
        if (projectEnvironment == null) {
            projectEnvironment = new ProjectEnvironment();
            projectEnvironment.setProject(projectDao.getProject(projectId));
            projectEnvironment.setEnvironment(environmentDao.getEnvironment(environmentId));
            projectEnvironmentDao.createOrUpdate(projectEnvironment);
        }
        return projectEnvironment.getId();
    }

    public ProjectEnvironmentDto getProjectEnvironment(final Long projectId, final Long environmentId) {
        ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(projectId, environmentId);
        if (projectEnvironment == null) {
            projectEnvironment = new ProjectEnvironment();
            projectEnvironment.setProject(projectDao.getProject(projectId));
            projectEnvironment.setEnvironment(environmentDao.getEnvironment(environmentId));
        }
        return projectEnvironmentConverter.convert(projectEnvironment, true);
    }

    public List<ProjectEnvironmentDto> getProjectEnvironments(final boolean resolveNestedProps) {
        return ListConverter.convert(projectEnvironmentConverter, projectEnvironmentDao.getProjectEnvironments(), resolveNestedProps);
    }

    public ProjectEnvironmentDto getProjectEnvironment(final String projectName, final String environmentName) {
        ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(projectName, environmentName);
        return projectEnvironmentConverter.convert(projectEnvironment, true);
    }

    @Override
    public ProjectEnvironmentDto getProjectEnvironment(Long projectEnvironmentId) {        
       return  projectEnvironmentConverter.convert(projectEnvironmentDao.getProjectEnvironment(projectEnvironmentId), true);
    }

}
