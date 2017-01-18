package nl.unionsoft.sysstate.logic.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaList;
import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.util.PropertyGroupUtil;
import nl.unionsoft.sysstate.dao.PropertyDao;
import nl.unionsoft.sysstate.domain.GroupProperty;
import nl.unionsoft.sysstate.logic.PluginLogic;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
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
            Enumeration<URL> resources = PluginLogicImpl.class.getClassLoader().getResources(JarFile.MANIFEST_NAME);
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
                        LOG.info("Adding plugin: '{} ({}')", pluginId, pluginVersion);
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
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
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

    public Plugin getPlugin(final String id) {
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

        public void setProperties(final Properties properties) {
            this.properties = properties;
        }

    }

    private Properties getPropertiesFromResource(final String propertyResource) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = PluginLogicImpl.class.getResourceAsStream(propertyResource);
            if (inputStream == null) {
                LOG.warn("No properties found for resource '{}'!", propertyResource);
            } else {
                LOG.info("Loading props from class resource at '{}'", propertyResource);
                properties.load(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        LOG.info("Result for getPropertiesFromResource: {}", properties);
        return properties;

    }

    @Cacheable("propertiesForClassCache")
    public Properties getPropertiesForClass(final Class<?> theClass) {
        LOG.debug("Getting properties for class '{}'.", theClass);
        String propertyResource = "/" + StringUtils.replace(theClass.getCanonicalName(), ".", "/") + ".properties";
        return getPropertiesFromResource(propertyResource);
    }

    public Properties getPluginProperties(final String name) {
        PropertyMetaList propertyMetaList = getPluginPropertyMetaList(name);
        Properties properties = new Properties();
        for (PropertyMetaValue propertyMetaValue : propertyMetaList.getPropertyMetaValues()) {
            if (StringUtils.isNotEmpty(propertyMetaValue.getDefaultValue())) {
                properties.put(propertyMetaValue.getId(), propertyMetaValue.getDefaultValue());
            }
        }
        return properties;
    }

    public PropertyMetaList getPluginPropertyMetaList(final String id) {
        Plugin plugin = getPlugin(id);
        PropertyMetaList propertyMetaList = new PropertyMetaList();
        if (plugin != null) {
            Properties pluginProperties = plugin.getProperties();
            if (pluginProperties != null) {

                propertyMetaList.setName(pluginProperties.getProperty("plugin.title", id));
                propertyMetaList.setId(id);

                Map<String, Properties> globalGroupProperties = PropertyGroupUtil.getGroupProperties(pluginProperties, "global");

                for (Entry<String, Properties> entry : globalGroupProperties.entrySet()) {

                    String propertyId = entry.getKey();
                    Properties properties = entry.getValue();
                    String title = StringUtils.defaultIfEmpty(properties.getProperty("title"), propertyId);
                    boolean nullable = BooleanUtils.toBoolean(properties.getProperty("nullable"));

                    GroupProperty groupProperty = propertyDao.getGroupProperty(id, propertyId);
                    String value = getValueForPropMeta(properties, groupProperty);
                    PropertyMetaValue propertyMetaValue = new PropertyMetaValue(propertyId, title, nullable, value, 0);

                    Properties innerProps = propertyMetaValue.getProperties();
                    for (String propKey : properties.stringPropertyNames()) {
                        if (StringUtils.startsWith(propKey, "property.")) {
                            innerProps.setProperty(StringUtils.substringAfter(propKey, "property."), properties.getProperty(propKey));
                        }
                    }

                    String lovResolver = properties.getProperty("resolver");
                    if (StringUtils.isNotEmpty(lovResolver)) {
                        ListOfValueResolver listOfValueResolver = getListOfValueResolver(lovResolver);
                        propertyMetaValue.setLov(listOfValueResolver.getListOfValues(propertyMetaValue));
                    }
                    propertyMetaList.add(propertyMetaValue);
                }
            }
        }
        return propertyMetaList;
    }

    private String getValueForPropMeta(final Properties properties, final GroupProperty groupProperty) {
        String value = null;
        if (groupProperty != null) {
            value = groupProperty.getValue();
        }
        if (StringUtils.isEmpty(value)) {
            value = properties.getProperty("default");
        }
        return value;
    }

    public void setPluginPropertyMeta(final PropertyMetaList propertyMetaList) {
        String group = propertyMetaList.getId();
        List<PropertyMetaValue> propertyMetaValues = propertyMetaList.getPropertyMetaValues();
        if (propertyMetaValues != null && StringUtils.isNotEmpty(group)) {
            for (PropertyMetaValue propertyMetaValue : propertyMetaValues) {
                propertyDao.setGroupProperty(group, propertyMetaValue.getId(), propertyMetaValue.getDefaultValue());
            }
        }
    }

    public ListOfValueResolver getListOfValueResolver(String name) {
        return pluginApplicationContext.getBean(name, ListOfValueResolver.class);
    }

    @Override
    public ListOfValueResolver getListOfValueResolver(Class<? extends ListOfValueResolver> lovResolverClass) {
        return pluginApplicationContext.getBean(lovResolverClass);
    }
}
