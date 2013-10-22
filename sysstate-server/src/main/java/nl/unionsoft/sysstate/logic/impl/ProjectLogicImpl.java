package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.dao.EnvironmentDao;
import nl.unionsoft.sysstate.dao.ProjectDao;
import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.domain.Environment;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.Project;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("projectLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProjectLogicImpl implements ProjectLogic {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectLogicImpl.class);
    @Inject
    @Named("projectDao")
    private ProjectDao projectDao;

    @Inject
    @Named("environmentDao")
    private EnvironmentDao environmentDao;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("projectEnvironmentDao")
    private ProjectEnvironmentDao projectEnvironmentDao;

    @Inject
    @Named("projectConverter")
    private Converter<ProjectDto, Project> projectConverter;

    public ProjectDto getProject(final Long projectId) {
        return projectConverter.convert(projectDao.getProject(projectId));
    }

    public void createOrUpdateProject(final ProjectDto project) {
        Long projectId = project.getId();
        Project theProject = null;
        if (projectId == null || projectId == 0) {
            theProject = new Project();

        } else {
            theProject = projectDao.getProject(projectId);
        }
        theProject.setDefaultInstancePlugin(project.getDefaultInstancePlugin());
        theProject.setName(project.getName());
        theProject.setOrder(project.getOrder());
        projectDao.createOrUpdateProject(theProject);
        for (final Environment environment : environmentDao.getEnvironments()) {
            ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(project.getId(), environment.getId());
            if (projectEnvironment == null) {
                projectEnvironment = new ProjectEnvironment();
                projectEnvironment.setProject(theProject);
                projectEnvironment.setEnvironment(environment);
                projectEnvironmentDao.createOrUpdate(projectEnvironment);
            }
        }
    }

    public void delete(final Long projectId) {
        Project project = projectDao.getProject(projectId);
        List<ProjectEnvironment> projectEnvironments = project.getProjectEnvironments();
        List<Long> instanceIds = new ArrayList<Long>();
        if (projectEnvironments != null) {
            for (ProjectEnvironment projectEnvironment : projectEnvironments) {
                List<Instance> instances = projectEnvironment.getInstances();
                for (Instance instance : instances) {
                    instanceIds.add(instance.getId());
                }
            }
        }
        projectDao.delete(projectId);
        for (Long instanceId : instanceIds) {
            instanceLogic.removeTriggerJob(instanceId);
        }
    }

    public ProjectDto findProject(final String name) {
        return projectConverter.convert(projectDao.findProject(name));
    }

    public List<ProjectDto> getProjects() {
        return ListConverter.convert(projectConverter, projectDao.getProjects());
    }




}
