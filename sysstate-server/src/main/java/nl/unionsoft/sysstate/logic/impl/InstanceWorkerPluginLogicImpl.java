package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.plugins.PostWorkerPlugin;
import nl.unionsoft.sysstate.dao.InstanceActionDao;
import nl.unionsoft.sysstate.domain.InstanceWorkerPluginConfig;
import nl.unionsoft.sysstate.logic.InstanceWorkerPluginLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.impl.PluginLogicImpl.PluginInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("instanceWorkerPluginLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class InstanceWorkerPluginLogicImpl implements InstanceWorkerPluginLogic {
    private static final Logger LOG = LoggerFactory.getLogger(InstanceWorkerPluginLogicImpl.class);
    @Inject
    @Named("instanceActionDao")
    private InstanceActionDao instanceActionDao;
    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public ListResponse<InstanceWorkerPluginConfig> getInstanceWorkerPluginConfigs(ListRequest listRequest) {
        return instanceActionDao.getInstanceNotifiers(listRequest);
    }

    public InstanceWorkerPluginConfig getInstanceWorkerPluginConfig(Long instanceNotifierId) {
        return instanceActionDao.getInstanceNotifier(instanceNotifierId);
    }

    public void createOrUpdate(InstanceWorkerPluginConfig instanceWorkerPluginConfig) {
        instanceActionDao.createOrUpdate(instanceWorkerPluginConfig);

    }

    public List<InstanceWorkerPluginConfig> getInstanceWorkerPluginConfigs(Long instanceId, Long notifierId) {
        return instanceActionDao.getInstanceNotifiers(instanceId, notifierId);
    }

    public void delete(Long id) {
        instanceActionDao.delete(id);

    }

    public List<InstanceWorkerPluginConfig> getInstanceWorkerPluginConfigs(Long instanceId) {
        final List<InstanceWorkerPluginConfig> instanceWorkerPluginConfigs = instanceActionDao.getInstanceWorkerPluginConfigs(instanceId);
        LOG.debug("Checking for additional common plugins..");
        for (final PluginInstance<PostWorkerPlugin> pluginInstance : pluginLogic.getPluginInstances(PostWorkerPlugin.class)) {
            final String postWorkerPluginClass = pluginInstance.getPluginClass().getName();
            LOG.debug("Found Plugin '{}', checking if common...", postWorkerPluginClass);
            final Properties properties = pluginInstance.getProperties();
            if (PropertiesUtil.propertyBoolean(properties, "common", "false")) {
                LOG.debug("Plugin is common, checking if it's not already configured for this Instance...");
                boolean found = false;
                for (final InstanceWorkerPluginConfig instanceWorkerPluginConfig : instanceWorkerPluginConfigs) {
                    if (instanceWorkerPluginConfig.getClass().equals(postWorkerPluginClass)) {
                        found = true;
                        break;

                    }

                }
                if (!found) {
                    LOG.debug("Plugin is not configured for this instance, adding as common plugin...");
                    final InstanceWorkerPluginConfig instanceWorkerPluginConfig = new InstanceWorkerPluginConfig();
                    instanceWorkerPluginConfig.setPluginClass(postWorkerPluginClass);
                    instanceWorkerPluginConfigs.add(instanceWorkerPluginConfig);
                }
            } else {
                LOG.debug("Plugin is not a common plugin.");
            }
        }
        return instanceWorkerPluginConfigs;

    }
}
