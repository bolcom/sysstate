package nl.unionsoft.sysstate.logic.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.options.getplugin.OptionCapabilities;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.plugins.ConfigurablePlugin;
import nl.unionsoft.sysstate.common.plugins.LifeCyclePlugin;
import nl.unionsoft.sysstate.common.stateresolver.impl.HttpStateResolverPluginImpl;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.plugins.impl.discovery.CrossEnvironmentDiscoveryPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.discovery.JenkinsNodesDiscoveryPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.notifier.MockNotifierPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.rating.ResponseTimeRatingPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.rating.StabilityRatingPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.resolver.JenkinsJobStateResolverPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.resolver.JenkinsServerStateResolverPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.resolver.MockStateResolverPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.resolver.PushRequestsStateResolverPluginImpl;
import nl.unionsoft.sysstate.plugins.impl.resolver.SelfDiagnoseStateResolverPluginImpl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

@Service("pluginLogic")
public class PluginLogicImpl implements PluginLogic, SmartLifecycle, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(PluginLogicImpl.class);

    @Value("${SYSSTATE_HOME}")
    private String homeDir;

    @Inject
    @Named("pluginManager")
    private PluginManager pluginManager;

    @Inject
    @Named("pluginManagerUtil")
    private PluginManagerUtil pluginManagerUtil;

    private File pluginsHomeDir;

    private ApplicationContext applicationContext;

    private final List<PluginInstance<? extends Plugin>> pluginInstances;

    public PluginLogicImpl () {

        pluginInstances = new ArrayList<PluginLogicImpl.PluginInstance<? extends Plugin>>();
    }

    public void start() {
        prepareHomeDir();
        addBasePluginsToManager();
        postProcessPlugins();
    }

    private void postProcessPlugins() {
        final List<Plugin> plugins = getPlugins(Plugin.class);
        LOG.info("Loaded '{}' plugins.", plugins.size());
        for (final Plugin plugin : plugins) {
            postProcessPlugin(plugin, applicationContext.getAutowireCapableBeanFactory());

        }
    }

    private void prepareHomeDir() {
        LOG.info("Homedir set to: {}", homeDir);
        final File homeDirFile = new File(homeDir);
        if (!homeDirFile.exists()) {
            homeDirFile.mkdirs();
        }
        pluginsHomeDir = new File(homeDirFile, "plugins");
        pluginsHomeDir.mkdirs();

        LOG.info("Plugin Homedir is set to:{}", pluginsHomeDir);
    }

    private void addBasePluginsToManager() {
        pluginManager.addPluginsFrom(pluginsHomeDir.toURI());
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(HttpStateResolverPluginImpl.class));
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(MockStateResolverPluginImpl.class));
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(SelfDiagnoseStateResolverPluginImpl.class));
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(JenkinsJobStateResolverPluginImpl.class));
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(PushRequestsStateResolverPluginImpl.class));
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(JenkinsServerStateResolverPluginImpl.class));
        
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(JenkinsNodesDiscoveryPluginImpl.class));
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(CrossEnvironmentDiscoveryPluginImpl.class));

        // pluginManager.addPluginsFrom(ClassURI.PLUGIN(IRCNotifierPluginImpl.class));
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(MockNotifierPluginImpl.class));

        pluginManager.addPluginsFrom(ClassURI.PLUGIN(StabilityRatingPluginImpl.class));
        pluginManager.addPluginsFrom(ClassURI.PLUGIN(ResponseTimeRatingPluginImpl.class));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void postProcessPlugin(Plugin plugin, final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        try {
            autowireCapableBeanFactory.autowireBean(plugin);
            final Class pluginClass = plugin.getClass();
            if (!StringUtils.startsWith(pluginClass.getName(), "net.xeoh.")) {
                LOG.info("Post initialization of plugin '{}' started...", pluginClass);
                final File pluginDir = new File(pluginsHomeDir, pluginClass.getName());
                if (!pluginDir.exists()) {
                    pluginDir.mkdirs();
                }
                final File pluginConfig = new File(pluginDir, "plugin.properties");
                if (!pluginConfig.exists()) {
                    pluginConfig.createNewFile();
                }

                final PluginInstance<Plugin> pluginInstance = new PluginInstance<Plugin>(plugin, pluginClass, pluginConfig);
                postProcessSpecialPlugins(plugin, pluginInstance.getProperties());
                LOG.info("Post initialization of plugin '{}' completed!", pluginClass);
                pluginInstances.add(pluginInstance);
            }
        } catch(final Exception e) {
            LOG.error("Unable to control plugin, caught Exception", e);
        }
    }

    private void postProcessSpecialPlugins(Plugin plugin, Properties pluginProperties) {
        if (plugin instanceof ConfigurablePlugin) {
            LOG.info("Detected LifeCyclePlugin, loading configuration...");
            final ConfigurablePlugin configurablePlugin = (ConfigurablePlugin) plugin;
            configurablePlugin.config(pluginProperties);
        }

        if (plugin instanceof LifeCyclePlugin) {
            LOG.info("Detected LifeCyclePlugin, checking if it's enabled.");
            if (PropertiesUtil.propertyBoolean(pluginProperties, "enabled", "true")) {
                final LifeCyclePlugin lifeCyclePlugin = (LifeCyclePlugin) plugin;
                LOG.info("Starting LifeCyclePlugin...");
                lifeCyclePlugin.start();
            }
        }
    }

    public void stop() {
        for (final LifeCyclePlugin plugin : getPlugins(LifeCyclePlugin.class)) {
            try {
                plugin.stop();
            } catch(final Exception e) {
                LOG.error("Unable to stop plugin, caught Exception", e);
            }
        }
        pluginManager.shutdown();
    }

    public boolean isRunning() {
        return false;
    }

    public int getPhase() {
        return 0;
    }

    public boolean isAutoStartup() {
        return true;
    }

    public void stop(Runnable callback) {
        stop();
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    public LifeCyclePlugin getNotifierController(String name) {
        return pluginManager.getPlugin(LifeCyclePlugin.class, new OptionCapabilities(new String[] { name }));
    }

    @SuppressWarnings("unchecked")
    public <T extends Plugin> T getPlugin(String clazz) {

        final PluginInstance<Plugin> pluginInstance = getPluginInstance(clazz);

        T result = null;
        if (pluginInstance != null) {
            result = (T) pluginInstance.getPlugin();
        }
        return result;

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends Plugin> PluginInstance<T> getPluginInstance(String className) {
        PluginInstance<T> result = null;
        for (final PluginInstance pluginInstance : pluginInstances) {
            if (pluginInstance.getPluginClass().getName().equals(className)) {
                result = pluginInstance;
                break;
            }
        }
        return result;
    }

    public <T extends Plugin> T getPlugin(Class<T> clazz) {
        T result = null;
        if (clazz != null) {
            final PluginInstance<T> pluginInstance = getPluginInstance(clazz);
            if (pluginInstance != null) {
                result = pluginInstance.getPlugin();
            }
        }
        return result;
    }

    public <T extends Plugin> List<T> getPlugins(Class<T> clazz) {
        return (List<T>) pluginManagerUtil.getPlugins(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends Plugin> Set<Class<T>> getPluginClasses(Class<T> clazz) {
        final Set<Class<T>> results = new HashSet<Class<T>>();
        for (final Plugin plugin : pluginManagerUtil.getPlugins(clazz)) {
            results.add((Class<T>) plugin.getClass());
        }
        return results;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends Plugin> PluginInstance<T> getPluginInstance(Class<T> clazz) {
        PluginInstance<T> result = null;
        for (final PluginInstance pluginInstance : pluginInstances) {
            if (pluginInstance.getPluginClass().equals(clazz)) {
                result = pluginInstance;
                break;
            }
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends Plugin> List<PluginInstance<T>> getPluginInstances(Class<T> pluginClass) {
        final List<PluginInstance<T>> result = new ArrayList<PluginLogicImpl.PluginInstance<T>>();
        if (pluginClass != null) {
            for (final PluginInstance pluginInstance : pluginInstances) {
                if (pluginClass.isAssignableFrom((pluginInstance.getPluginClass()))) {
                    result.add(pluginInstance);
                }
            }

        }
        return result;
    }

    public final class PluginInstance<T extends Plugin> {
        private final T plugin;
        private Properties properties;
        private final File propertiesFile;
        private final Class<T> pluginClass;

        public PluginInstance (T plugin, Class<T> pluginClass, File propertiesFile) {
            this.plugin = plugin;
            this.properties = PropertiesUtil.readFromFile(propertiesFile);
            this.propertiesFile = propertiesFile;
            this.pluginClass = pluginClass;
        }

        public T getPlugin() {
            return plugin;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public Class<T> getPluginClass() {
            return pluginClass;
        }

        private void writeConfig() {
            if (properties != null) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(propertiesFile);
                    properties.store(fos, "");
                } catch(final IOException e) {
                    LOG.error("Unable to write properties, caught IOException", e);
                } finally {
                    IOUtils.closeQuietly(fos);
                }

            }
        }

    }

    public void updatePluginConfiguration(String clazz, Properties configuration) {
        final PluginInstance<Plugin> pluginInstance = getPluginInstance(clazz);
        if (pluginInstance != null) {
            pluginInstance.setProperties(configuration);
            final Plugin plugin = pluginInstance.getPlugin();
            if (plugin instanceof LifeCyclePlugin) {
                final LifeCyclePlugin lifeCyclePlugin = (LifeCyclePlugin) plugin;
                lifeCyclePlugin.stop();
            }
            pluginInstance.writeConfig();
            postProcessSpecialPlugins(plugin, configuration);

        }

    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public PluginManagerUtil getPluginManagerUtil() {
        return pluginManagerUtil;
    }

    public void setPluginManagerUtil(PluginManagerUtil pluginManagerUtil) {
        this.pluginManagerUtil = pluginManagerUtil;
    }

}
