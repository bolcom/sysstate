package nl.unionsoft.sysstate.logic.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.dao.EnvironmentDao;
import nl.unionsoft.sysstate.dao.ProjectDao;
import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.domain.Environment;
import nl.unionsoft.sysstate.domain.Project;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("environmentLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EnvironmentLogicImpl implements EnvironmentLogic {

    @Inject
    @Named("environmentDao")
    private EnvironmentDao environmentDao;

    @Inject
    @Named("projectDao")
    private ProjectDao projectDao;

    @Inject
    @Named("projectEnvironmentDao")
    private ProjectEnvironmentDao projectEnvironmentDao;

    @Inject
    @Named("environmentConverter")
    private Converter<EnvironmentDto, Environment> environmentConverter;

    public List<Environment> getEnvironmentEntities() {
        return environmentDao.getEnvironments();
    }

    public Environment getEnvironmentEntity(final Long environmentId) {
        return environmentDao.getEnvironment(environmentId);
    }

    public void createOrUpdateEntity(final Environment environment) {

        environmentDao.createOrUpdate(environment);
        for (final Project project : projectDao.getProjects()) {
            ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(project.getId(), environment.getId());
            if (projectEnvironment == null) {
                projectEnvironment = new ProjectEnvironment();
                projectEnvironment.setProject(project);
                projectEnvironment.setEnvironment(environment);
                projectEnvironmentDao.createOrUpdate(projectEnvironment);
            }
        }

    }

    public void delete(final Long environmentId) {
        environmentDao.delete(environmentId);

    }

    public EnvironmentDto findEnvironment(final String name) {
        return environmentConverter.convert(environmentDao.findEnvironment(name));
    }

    public List<EnvironmentDto> getEnvironments() {
        return ListConverter.convert(environmentConverter, environmentDao.getEnvironments());
    }

    public EnvironmentDto getEnvironment(final Long environmentId) {
        return environmentConverter.convert(environmentDao.getEnvironment(environmentId));
    }

    public void createOrUpdate(final EnvironmentDto environmentDto) {

        Environment environment = null;
        Long givenId = environmentDto.getId();
        if (givenId == null || givenId == 0) {
            environment = new Environment();
        } else {
            environment = environmentDao.getEnvironment(givenId);
        }
        environment.setDefaultInstanceTimeout(environmentDto.getDefaultInstanceTimeout());
        environment.setName(environmentDto.getName());
        environment.setOrder(environmentDto.getOrder());
        environment.setTags(environmentDto.getTags());
        environmentDao.createOrUpdate(environment);

        for (final Project project : projectDao.getProjects()) {
            ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(project.getId(), environment.getId());
            if (projectEnvironment == null) {
                projectEnvironment = new ProjectEnvironment();
                projectEnvironment.setProject(project);
                projectEnvironment.setEnvironment(environment);
                projectEnvironmentDao.createOrUpdate(projectEnvironment);
            }
        }

    }

}
