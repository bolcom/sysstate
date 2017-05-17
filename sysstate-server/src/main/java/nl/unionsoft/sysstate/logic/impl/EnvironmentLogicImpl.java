package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.commons.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.dao.EnvironmentDao;
import nl.unionsoft.sysstate.dao.ProjectDao;
import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.domain.Environment;
import nl.unionsoft.sysstate.domain.Project;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;
import nl.unionsoft.sysstate.logic.WorkLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("environmentLogic")
public class EnvironmentLogicImpl implements EnvironmentLogic {

    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentLogicImpl.class);

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

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    private WorkLogic workLogic;

    public List<Environment> getEnvironmentEntities() {
        return environmentDao.getEnvironments();
    }

    public Environment getEnvironmentEntity(final Long environmentId) {
        return environmentDao.getEnvironment(environmentId);
    }

    public void delete(final Long environmentId) {
        environmentDao.delete(environmentId);
    }

    public Optional<EnvironmentDto> getEnvironmentByName(final String name) {
        return Optional.ofNullable(environmentConverter.convert(environmentDao.getEnvironmentByName(name)));
    }

    public List<EnvironmentDto> getEnvironments() {
        return ListConverter.convert(environmentConverter, environmentDao.getEnvironments());
    }

    public EnvironmentDto getEnvironment(final Long environmentId) {
        return environmentConverter.convert(environmentDao.getEnvironment(environmentId));
    }

    @Scheduled(cron = "${environmentLogic.deleteEnvironmentsWithoutInstances.cron}")
    public void deleteEnvironmentsWithoutInstances() {
        workLogic.process("environmentLogic.deleteEnvironmentsWithoutInstances", 5 * 60, () -> {
            LOG.info("Deleting Environments without instances...");
            getEnvironments().stream().forEach(environment -> {
                if (instanceLogic.getInstancesForEnvironment(environment.getId()).isEmpty()) {
                    LOG.info("Deleting environment with id [{}] since it is no longer used.", environment.getId());
                    delete(environment.getId());
                }
            });
        });

    }

    public Long createOrUpdate(final EnvironmentDto environmentDto) {

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
        environment.setEnabled(environmentDto.isEnabled());
        environmentDao.createOrUpdate(environment);
        environmentDto.setId(environment.getId());
        for (final Project project : projectDao.getProjects()) {
            ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(project.getId(), environment.getId());
            if (projectEnvironment == null) {
                projectEnvironment = new ProjectEnvironment();
                projectEnvironment.setProject(project);
                projectEnvironment.setEnvironment(environment);
                projectEnvironmentDao.createOrUpdate(projectEnvironment);
            }
        }
        return environment.getId();

    }

    @Override
    public EnvironmentDto findOrCreateEnvironment(String name) {

        Optional<EnvironmentDto> optEnvironment = getEnvironmentByName(name);
        if (optEnvironment.isPresent()) {
            return optEnvironment.get();
        }
        LOG.info("There's no environment defined for environmentName [{}], creating it...", name);
        EnvironmentDto environment = new EnvironmentDto();
        environment.setName(name);
        createOrUpdate(environment);
        return environment;
    }

}
