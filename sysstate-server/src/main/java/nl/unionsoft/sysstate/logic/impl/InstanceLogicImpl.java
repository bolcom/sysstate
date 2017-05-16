package nl.unionsoft.sysstate.logic.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
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
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.converter.InstancePropertiesConverter;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.converter.StateConverter;
import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.dao.PropertyDao;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;
import nl.unionsoft.sysstate.job.UpdateInstanceJob;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

@Service("instanceLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class InstanceLogicImpl implements InstanceLogic, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(InstanceLogicImpl.class);

    @Inject
    @Named("instanceDao")
    private InstanceDao instanceDao;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

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
    @Named("scheduler")
    private TaskScheduler scheduler;

    @Inject
    @Named("instanceConverter")
    private Converter<InstanceDto, Instance> instanceConverter;

    @Inject
    @Named("stateResolverLogic")
    private StateResolverLogic stateResolverLogic;

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
    @Named("instancePropertiesConverter")
    private InstancePropertiesConverter instancePropertiesConverter;
    
   

    private Map<Long, ScheduledFuture<?>> instanceTasks;

    public InstanceLogicImpl() {
        instanceTasks = new HashMap<>();
    }

    public void queueForUpdate(final Long instanceId) {

        Optional<Instance> optionalInstance = instanceDao.getInstance(instanceId);
        if (!optionalInstance.isPresent()) {
            throw new IllegalStateException("No instance could be found for the given instanceId [" + instanceId + "]");
        }
        updateTriggerJob(optionalInstance.get());
    }

    private void updateTriggerJob(final Instance instance) {
        if (instance == null) {
            throw new IllegalStateException("Instance is required!");
        }
        logger.info("Creating or updating queue job for instance with id: {}", instance.getId());
        removeTriggerJob(instance.getId());
        addTriggerJob(instance.getId());
    }

    public void addTriggerJob(final long instanceId) {
        Optional<Instance> optionalInstance = instanceDao.getInstance(instanceId);
        if (!optionalInstance.isPresent()) {
            throw new IllegalStateException("No instance could be found for instanceId [" + instanceId + "]");
        }
        Instance instance = optionalInstance.get();

        final long refreshTimeout = instance.getRefreshTimeout();
        long period = refreshTimeout < 30000 ? 30000 : refreshTimeout;
        UpdateInstanceJob updateInstanceJob = new UpdateInstanceJob(this, stateLogic, instanceId);
        Date bitInTheFuture = Date.from(LocalDateTime.now().plusSeconds(10).atZone(ZoneId.systemDefault()).toInstant());
        instanceTasks.put(instanceId, scheduler.scheduleAtFixedRate(updateInstanceJob, bitInTheFuture, period));
    }

    public void removeTriggerJob(final long instanceId) {
        ScheduledFuture<?> scheduledFuture = instanceTasks.get(instanceId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
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
        if (!optProjectEnvironment.isPresent()){
            throw new IllegalStateException("Undefined projectEnvironment [" + dto.getProjectEnvironment() + "] ");
        }
        
        ProjectEnvironment projectEnvironment = optProjectEnvironment.get();
        if (instance.getProjectEnvironment() == null || !instance.getProjectEnvironment().getId().equals(projectEnvironment.getId())) {
            // ProjectEnvironment changed or null
            instance.setProjectEnvironment(projectEnvironment);
        }

        instanceDao.createOrUpdate(instance);
        propertyDao.setInstanceProperties(instance, dto.getConfiguration());
        updateTriggerJob(instance);

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
        removeTriggerJob(instanceId);
    }

    @Scheduled(initialDelay = 10000, fixedRate = 60000)
    public void purgeOldJobs() {
        logger.info("Purging old instance jobs...");
        Set<Long> cancelledJobKeys = new HashSet<Long>();
        instanceTasks.forEach((instanceId, scheduledFuture) -> {
            if (!instanceDao.getInstance(instanceId).isPresent()) {
                logger.info("Cancelling job for non-existing instance who had id [{}]", instanceId);
                scheduledFuture.cancel(true);
                cancelledJobKeys.add(instanceId);
            }
        });
        cancelledJobKeys.forEach(key -> instanceTasks.remove(key));
        logger.info("Purged & cancelled [{}] jobs..", cancelledJobKeys.size());
    }

    public List<InstanceDto> getInstancesForProjectAndEnvironment(final String projectPrefix, final String environmentPrefix) {
        return ListConverter.convert(instanceConverter, instanceDao.getInstancesForProjectAndEnvironment(projectPrefix, environmentPrefix));
    }

    public void afterPropertiesSet() throws Exception {
        List<Instance> instances = instanceDao.getInstances();
        for (Instance instance : instances) {
            addTriggerJob(instance.getId());
        }
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

 

}
