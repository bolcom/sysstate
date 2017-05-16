package nl.unionsoft.sysstate.logic.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.commons.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.converter.StateConverter;
import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.dao.PropertyDao;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.logic.WorkLogic;

@Service("instanceLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class InstanceLogicImpl implements InstanceLogic {

    private static final Logger logger = LoggerFactory.getLogger(InstanceLogicImpl.class);

    @Inject
    @Named("instanceDao")
    private InstanceDao instanceDao;

    @Inject
    @Named("propertyDao")
    private PropertyDao propertyDao;

    @Inject
    @Named("filterDao")
    private FilterDao filterDao;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("instanceConverter")
    private Converter<InstanceDto, Instance> instanceConverter;

    @Inject
    @Named("stateConverter")
    private StateConverter stateConverter;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @Inject
    @Named("projectEnvironmentDao")
    private ProjectEnvironmentDao projectEnvironmentDao;


    @Inject
    private WorkLogic workLogic;


    public InstanceLogicImpl() {
    }

    public List<InstanceDto> getInstances() {
        return ListConverter.convert(instanceConverter, instanceDao.getInstances());
    }

    public Optional<InstanceDto> getInstance(final Long instanceId) {
        return OptionalConverter.convert(instanceDao.getInstance(instanceId), instanceConverter);
    }

    public Long createOrUpdateInstance(final InstanceDto dto) {
        Instance instance = getInstanceForDto(dto);

        instance.setEnabled(dto.isEnabled());
        instance.setHomepageUrl(dto.getHomepageUrl());
        instance.setName(dto.getName());
        instance.setPluginClass(dto.getPluginClass());
        instance.setTags(dto.getTags());
        instance.setReference(dto.getReference());
        instance.setRefreshTimeout(dto.getRefreshTimeout());

        final Optional<ProjectEnvironment> optProjectEnvironment = getProjectEnvironmentFromDto(dto.getProjectEnvironment());
        if (!optProjectEnvironment.isPresent()) {
            throw new IllegalStateException("Undefined projectEnvironment [" + dto.getProjectEnvironment() + "] ");
        }

        ProjectEnvironment projectEnvironment = optProjectEnvironment.get();
        if (instance.getProjectEnvironment() == null || !instance.getProjectEnvironment().getId().equals(projectEnvironment.getId())) {
            // ProjectEnvironment changed or null
            instance.setProjectEnvironment(projectEnvironment);
        }

        instanceDao.createOrUpdate(instance);
        propertyDao.setInstanceProperties(instance, dto.getConfiguration());
        dto.setId(instance.getId());
        return instance.getId();
    }

    private Optional<ProjectEnvironment> getProjectEnvironmentFromDto(ProjectEnvironmentDto dto) {
        if (dto.getId() != null) {
            return Optional.ofNullable(projectEnvironmentDao.getProjectEnvironment(dto.getId()));
        }
        if (dto.getProject().getId() != null && dto.getEnvironment().getId() != null) {
            return Optional.ofNullable(projectEnvironmentDao.getProjectEnvironment(dto.getProject().getId(), dto.getEnvironment().getId()));
        }
        if (StringUtils.isNotEmpty(dto.getProject().getName()) && StringUtils.isNotEmpty(dto.getEnvironment().getName())) {
            return Optional.ofNullable(projectEnvironmentDao.getProjectEnvironment(dto.getProject().getName(), dto.getEnvironment().getName()));
        }
        return Optional.empty();

    }

    private Instance getInstanceForDto(InstanceDto dto) {
        Long instanceId = dto.getId();
        if (instanceId == null) {
            return new Instance();
        } else {
            logger.info("Instance [{}] has an Id, try to look instance up.", dto);
            Optional<Instance> optInstance = instanceDao.getInstance(instanceId);
            if (!optInstance.isPresent()) {
                throw new IllegalStateException("Instance with id [" + instanceId + "] cannot be found.");
            }
            return optInstance.get();
        }
    }

    public void delete(final Long instanceId) {
        instanceDao.delete(instanceId);
    }

    public List<InstanceDto> getInstancesForProjectAndEnvironment(final String projectPrefix, final String environmentPrefix) {
        return ListConverter.convert(instanceConverter, instanceDao.getInstancesForProjectAndEnvironment(projectPrefix, environmentPrefix));
    }

    public InstanceDto generateInstanceDto(String type) {
        return generateInstanceDto(type, null, null);
    }

    public InstanceDto generateInstanceDto(String type, Long projectId, Long environmentId) {

        final InstanceDto instance = new InstanceDto();
        instance.setPluginClass(type);
        instance.setEnabled(true);
        instance.setRefreshTimeout(60000);
        if (environmentId != null) {
            final EnvironmentDto environment = environmentLogic.getEnvironment(environmentId);
            if (environment != null) {
                instance.setRefreshTimeout(environment.getDefaultInstanceTimeout());
            }
            if (projectId != null) {
                final ProjectEnvironmentDto projectEnvironment = projectEnvironmentLogic.getProjectEnvironment(projectId, environmentId);
                if (projectEnvironment != null) {
                    instance.setProjectEnvironment(projectEnvironment);
                }
            }
        }
        return instance;
    }

    @Override
    public List<InstanceDto> getInstancesForEnvironment(Long environmentId) {
        return ListConverter.convert(instanceConverter, instanceDao.getInstancesForEnvironment(environmentId));
    }

    @Override
    public List<InstanceDto> getInstancesForProjectEnvironment(Long projectEnvironmentId) {
        return ListConverter.convert(instanceConverter, instanceDao.getInstancesForProjectEnvironment(projectEnvironmentId));
    }

    @Override
    public List<InstanceDto> getInstances(FilterDto filter) {
        return instanceDao.getInstances(filter).parallelStream().map(i -> instanceConverter.convert(i)).collect(Collectors.toList());
    }

    @Override
    public List<InstanceDto> getInstances(Long filterId) {
        Long now = System.currentTimeMillis();
        List<Instance> instances = instanceDao.getInstances(filterId);
        filterDao.notifyFilterQueried(filterId, System.currentTimeMillis() - now);
        return instances.parallelStream().map(i -> instanceConverter.convert(i)).collect(Collectors.toList());
    }

    @Override
    public Optional<InstanceDto> getInstance(String reference) {
        return OptionalConverter.convert(instanceDao.getInstanceByReference(reference), instanceConverter);
    }

    @Override
    public void refreshInstance(Long instanceId) {
        logger.info("Forcing refresh of instance with id [{}]", instanceId);
        getInstance(instanceId).ifPresent(this::refreshInstance);
    }

    @Override
    @Scheduled(cron = "${instanceLogic.refreshInstances.cron}")
    public void refreshInstances() {
        logger.info("Refreshing instances...");
        instanceDao.getInstances().stream()
                .map(instance -> instanceConverter.convert(instance))
                .filter(instance -> needsToBeUpdated(instance))
                .forEach(instance -> refreshInstance(instance));

    }

    private void refreshInstance(InstanceDto instance) {
        String reference = String.format("instance-%s", instance.getId());
        workLogic.aquireWorkLock(reference)
                .map(workId -> stateLogic.requestStateForInstance(instance)
                        .thenApply(state -> stateLogic.createOrUpdate(state))
                        .whenComplete((it, err) -> workLogic.release(workId)));

    }

    private boolean needsToBeUpdated(InstanceDto instance) {
        logger.trace("Checking if instance [{}]  needs to be updated...", instance);
        StateDto state = stateLogic.getLastStateForInstance(instance);
        // Returns true if the lastUpdate + the refreshTimeout is before the current time (aka expired)
        boolean expired = state.getLastUpdate().plusMillis(instance.getRefreshTimeout()).isBefore(new DateTime());
        logger.trace("Instance [{}] expired: [{}]", instance, expired);
        return expired;
    }

}
