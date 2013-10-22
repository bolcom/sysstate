package nl.unionsoft.sysstate.logic.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaList;
import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.dao.PropertyDao;
import nl.unionsoft.sysstate.domain.GroupProperty;
import nl.unionsoft.sysstate.logic.PluginLogic;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PluginLogicImpl implements PluginLogic, ApplicationContextAware, InitializingBean {

    @Inject
    @Named("propertyDao")
    private PropertyDao propertyDao;

    private ApplicationContext applicationContext;

    private final List<Plugin> plugins;

    private ClassPathXmlApplicationContext pluginApplicationContext;

    private static final Logger LOG = LoggerFactory.getLogger(PluginLogicImpl.class);

    public PluginLogicImpl() {
        plugins = new ArrayList<PluginLogicImpl.Plugin>();
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    public void afterPropertiesSet() throws Exception {
        try {
            List<String> contextFiles = new ArrayList<String>();
            Enumeration<URL> resources = PluginLogicImpl.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                LOG.debug("Found: {}", url);
                InputStream manifestStream = null;
                try {
                    manifestStream = url.openStream();
                    Manifest manifest = new Manifest(manifestStream);

                    final Attributes mainAttributes = manifest.getMainAttributes();
                    String pluginContext = mainAttributes.getValue("plugin-context");
                    String pluginId = mainAttributes.getValue("plugin-id");
                    String pluginVersion = mainAttributes.getValue("plugin-version");
                    if (StringUtils.isNotEmpty(pluginId) && StringUtils.isNotEmpty(pluginVersion)) {
                        String pluginPropertiesResource = mainAttributes.getValue("plugin-properties");

                        if (StringUtils.isNotEmpty(pluginContext)) {
                            LOG.info("PluginContext: {}", pluginContext);
                            contextFiles.add("classpath:" + pluginContext);
                        }
                        Plugin plugin = new Plugin();
                        plugin.setId(pluginId);
                        plugin.setVersion(pluginVersion);
                        if (StringUtils.isNotEmpty(pluginPropertiesResource)) {
                            plugin.setProperties(getPropertiesFromResource(pluginPropertiesResource));
                        }
                        plugins.add(plugin);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(manifestStream);
                }
            }
            LOG.info("Preparing pluginApplicationContext...");
            pluginApplicationContext = new ClassPathXmlApplicationContext(applicationContext);
            pluginApplicationContext.setConfigLocations(contextFiles.toArray(new String[] {}));
            pluginApplicationContext.refresh();
        } catch (Exception e) {
            LOG.error("Caught Exception while initializing plugins", e);
            throw e;
        }
    }

    public ClassPathXmlApplicationContext getPluginApplicationContext() {
        return pluginApplicationContext;
    }

    public void setPluginApplicationContext(final ClassPathXmlApplicationContext pluginApplicationContext) {
        this.pluginApplicationContext = pluginApplicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(final String name) {
        T result = null;
        try {
            Class<?> beanClass = Class.forName(name);
            result = (T) pluginApplicationContext.getBean(beanClass);
        } catch (ClassNotFoundException e) {
            result = (T) pluginApplicationContext.getBean(name);
        }
        return result;
    }

    public String[] getComponentNames(final Class<?> type) {
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(pluginApplicationContext, type);
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public Plugin getPlugin(String id) {
        Plugin result = null;
        for (Plugin plugin : plugins) {
            if (plugin.getId().equals(id)) {
                result = plugin;
                break;
            }
        }
        return result;

    }

    public class Plugin {
        private String id;
        private String version;
        private Properties properties;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(final String version) {
            this.version = version;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

    }

    private Properties getPropertiesFromResource(String propertyResource) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {

            LOG.info("Loading props from class resource at '{}'", propertyResource);
            inputStream = PluginLogicImpl.class.getResourceAsStream(propertyResource);
            if (inputStream != null) {
                properties.load(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return properties;

    }

    @Cacheable("propertiesForClassCache")
    public Properties getPropertiesForClass(Class<?> theClass) {
        String propertyResource = "/" + StringUtils.replace(theClass.getCanonicalName(), ".", "/") + ".properties";
        return getPropertiesFromResource(propertyResource);
    }

    public Properties getPluginProperties(String name) {

        Properties properties = new Properties();
        for (GroupProperty groupProperty : propertyDao.getGroupProperties(name)) {
            properties.put(groupProperty.getKey(), groupProperty.getValue());
        }
        return properties;
    }

    public PropertyMetaList getPluginPropertyMetaList(String id) {
        Plugin plugin = getPlugin(id);
        PropertyMetaList propertyMetaList = new PropertyMetaList();
        if (plugin != null) {
            Properties properties = plugin.getProperties();
            if (properties != null) {
                propertyMetaList.setName(properties.getProperty("plugin.title", id));
                propertyMetaList.setId(id);
                for (String propertyName : properties.stringPropertyNames()) {
                    if (StringUtils.startsWith(propertyName, "global.") && !StringUtils.equals("global.properties", propertyName)) {
                        String[] propertyTokens = StringUtils.split(propertyName, '.');
                        if (propertyTokens.length == 2) {
                            String propertyId = propertyTokens[1];
                            PropertyMetaValue propertyMetaValue = new PropertyMetaValue();
                            propertyMetaValue.setId(propertyId);
                            propertyMetaValue.setTitle(StringUtils.defaultIfEmpty(properties.getProperty(propertyName), propertyId));
                            GroupProperty groupProperty = propertyDao.getGroupProperty(id, propertyId);
                            if (groupProperty != null) {
                                propertyMetaValue.setValue(groupProperty.getValue());
                            }
                            String lovResolver = properties.getProperty(propertyName + ".resolver");
                            if (StringUtils.isNotEmpty(lovResolver)) {
                                ListOfValueResolver listOfValueResolver = pluginApplicationContext.getBean(lovResolver, ListOfValueResolver.class);
                                propertyMetaValue.setLov(listOfValueResolver.getListOfValues());
                            }
                            propertyMetaList.add(propertyMetaValue);
                        }
                    }
                }
            }
        }
        return propertyMetaList;
    }

    public void setPluginPropertyMeta(PropertyMetaList propertyMetaList) {
        String group = propertyMetaList.getId();
        List<PropertyMetaValue> propertyMetaValues = propertyMetaList.getPropertyMetaValues();
        if (propertyMetaValues != null && StringUtils.isNotEmpty(group)) {
            for (PropertyMetaValue propertyMetaValue : propertyMetaValues) {
                propertyDao.setGroupProperty(group, propertyMetaValue.getId(), propertyMetaValue.getValue());
            }
        }
    }
}
