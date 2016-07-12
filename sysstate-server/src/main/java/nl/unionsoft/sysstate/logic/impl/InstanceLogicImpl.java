package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.common.param.ParamContextLogicImpl;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.common.util.PropertyGroupUtil;
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
    @Named("paramContextLogic")
    private ParamContextLogicImpl paramContextLogic;

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
        instanceTasks.put(instanceId, scheduler.scheduleAtFixedRate(updateInstanceJob, period));
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

        final ProjectEnvironment projectEnvironment = getProjectEnvironmentFromDto(dto.getProjectEnvironment());
        if (instance.getProjectEnvironment() == null || !instance.getProjectEnvironment().getId().equals(projectEnvironment.getId())) {
            // ProjectEnvironment changed or null
            instance.setProjectEnvironment(projectEnvironment);
        }

        instanceDao.createOrUpdate(instance);

        Map<String, String> configuration = dto.getConfiguration();
        if (configuration != null) {
            for (Entry<String, String> entry : configuration.entrySet()) {
                propertyDao.setInstanceProperty(instance, entry.getKey(), entry.getValue());
            }
        }
        updateTriggerJob(instance);

        dto.setId(instance.getId());
        return instance.getId();
    }

    private ProjectEnvironment getProjectEnvironmentFromDto(ProjectEnvironmentDto dto) {
        if (dto.getId() != null) {
            return projectEnvironmentDao.getProjectEnvironment(dto.getId());
        }
        if (dto.getProject().getId() != null && dto.getEnvironment().getId() != null) {
            return projectEnvironmentDao.getProjectEnvironment(dto.getProject().getId(), dto.getEnvironment().getId());
        }
        if (StringUtils.isNotEmpty(dto.getProject().getName()) && StringUtils.isNotEmpty(dto.getEnvironment().getName())) {
            return projectEnvironmentDao.getProjectEnvironment(dto.getProject().getName(), dto.getEnvironment().getName());
        }
        throw new IllegalArgumentException("Unable to determine find existing projectEnvironment for  [" + dto + "]");

    }

    private Instance getInstanceForDto(InstanceDto dto) {
        Long instanceId = dto.getId();
        if (instanceId == null) {
            // Id is not set, try to fetch based on reference.
            String reference = dto.getReference();
            if (StringUtils.isNotEmpty(reference)) {
                logger.info("No ID is set, but reference [{}] is. Trying to resolve instance using reference.", reference);
                Optional<Instance> optInstance = instanceDao.getInstanceByReference(reference);
                if (optInstance.isPresent()) {
                    logger.info("Returning found for reference [{}]", reference);
                    return optInstance.get();
                }
            }
            logger.info("Returning new instance");
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
        instance.setRefreshTimeout(10000);
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

    @Cacheable("propertyMetaTypeCache")
    public List<PropertyMetaValue> getPropertyMeta(String type) {

        Object component = pluginLogic.getComponent(type);

        Class<?> componentClass = component.getClass();

        Stack<Class<?>> classStack = new Stack<Class<?>>();
        Class<?> superClass = componentClass;
        while (!Object.class.equals(superClass)) {
            classStack.push(superClass);
            superClass = superClass.getSuperclass();
        }

        List<PropertyMetaValue> propertyMetas = new ArrayList<PropertyMetaValue>();
        while (!classStack.empty()) {
            Class<?> stackClass = classStack.pop();
            addPropertyMetasFromPropertyFiles(propertyMetas, stackClass);

        }

        return propertyMetas;
    }

    private void addPropertyMetasFromPropertyFiles(List<PropertyMetaValue> propertyMetas, Class<?> stackClass) {
        Map<String, Properties> instanceGroupProperties = PropertyGroupUtil.getGroupProperties(pluginLogic.getPropertiesForClass(stackClass), "instance");
        for (Entry<String, Properties> entry : instanceGroupProperties.entrySet()) {
            String id = entry.getKey();
            Properties properties = entry.getValue();
            PropertyMetaValue propertyMetaValue = new PropertyMetaValue();
            propertyMetaValue.setId(id);
            propertyMetaValue.setTitle(properties.getProperty("title", id));
            String lovResolver = properties.getProperty("resolver");
            if (StringUtils.isNotEmpty(lovResolver)) {
                ListOfValueResolver listOfValueResolver = pluginLogic.getListOfValueResolver(lovResolver);
                propertyMetaValue.setLov(listOfValueResolver.getListOfValues(propertyMetaValue));
            }
            propertyMetas.add(propertyMetaValue);
        }
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

}
