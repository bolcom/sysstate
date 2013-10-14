package nl.unionsoft.sysstate.logic.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.param.Context;
import nl.unionsoft.common.param.ParamContextLogicImpl;
import nl.unionsoft.sysstate.common.extending.GroupConfiguration;
import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.dao.PropertyDao;
import nl.unionsoft.sysstate.domain.GroupProperty;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.InstanceProperty;
import nl.unionsoft.sysstate.logic.ConfigurationLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic.StateResolverMeta;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("configurationLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ConfigurationLogicImpl implements ConfigurationLogic {

    @Inject
    @Named("instanceDao")
    private InstanceDao instanceDao;

    @Inject
    @Named("propertyDao")
    private PropertyDao propertyDao;

    @Inject
    @Named("stateResolverLogic")
    private StateResolverLogic stateResolverLogic;

    @Inject
    @Named("paramContextLogic")
    private ParamContextLogicImpl paramContextLogic;

    public InstanceConfiguration getInstanceConfiguration(final long instanceId) {
        Instance instance = instanceDao.getInstance(instanceId);

        StateResolverMeta stateResolverMeta = stateResolverLogic.getStateResolverMeta(instance.getPluginClass());

        Class<? extends InstanceConfiguration> instanceConfigurationClass = stateResolverMeta.getConfigurationClass();
        InstanceConfiguration instanceConfiguration = null;
        try {
            instanceConfiguration = instanceConfigurationClass.newInstance();
            List<InstanceProperty> instanceProperties = instance.getInstanceProperties();
            Map<String, Object> propertyValues = new HashMap<String, Object>();
            for (InstanceProperty instanceProperty : instanceProperties) {
                propertyValues.put(instanceProperty.getKey(), instanceProperty.getValue());
            }
            paramContextLogic.setBeanValues(instanceConfiguration, propertyValues);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return instanceConfiguration;
    }

    public InstanceConfiguration generateInstanceConfigurationForType(String type) {
        StateResolverMeta stateResolverMeta = stateResolverLogic.getStateResolverMeta(type);

        Class<? extends InstanceConfiguration> instanceConfigurationClass = stateResolverMeta.getConfigurationClass();
        InstanceConfiguration instanceConfiguration = null;
        try {
            instanceConfiguration = instanceConfigurationClass.newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return instanceConfiguration;

    }

    public void setInstanceConfiguration(final long instanceId, final InstanceConfiguration instanceConfiguration) {
        List<Context> contexts = paramContextLogic.getContext(instanceConfiguration.getClass());
        for (Context context : contexts) {
            String key = context.getId();
            try {
                String value = BeanUtils.getProperty(instanceConfiguration, key);
                propertyDao.setInstanceProperty(instanceId, key, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public <T extends GroupConfiguration> T getGroupConfiguration(final Class<T> groupConfigurationClass) {
        T groupConfiguration = null;
        try {
            groupConfiguration = groupConfigurationClass.newInstance();
            Map<String, Object> groupPropertyValues = new HashMap<String, Object>();
            for (GroupProperty groupProperty : propertyDao.getGroupProperties(groupConfigurationClass.getCanonicalName())) {
                groupPropertyValues.put(groupProperty.getKey(), groupProperty.getValue());
            }
            paramContextLogic.setBeanValues(groupConfiguration, groupPropertyValues);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return groupConfiguration;
    }

    public void setGroupConfiguration(GroupConfiguration groupConfiguration) {
        Class<? extends GroupConfiguration> groupConfigurationClass = groupConfiguration.getClass();
        List<Context> contexts = paramContextLogic.getContext(groupConfigurationClass);
        for (Context context : contexts) {
            String key = context.getId();
            try {
                String value = BeanUtils.getProperty(groupConfiguration, key);
                propertyDao.setGroupProperty(groupConfigurationClass.getCanonicalName(), key, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        
    }

}
