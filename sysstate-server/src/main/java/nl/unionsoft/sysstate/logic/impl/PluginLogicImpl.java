package nl.unionsoft.sysstate.logic.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import nl.unionsoft.sysstate.logic.PluginLogic;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PluginLogicImpl implements PluginLogic, ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private ClassPathXmlApplicationContext pluginApplicationContext;

    private static final Logger LOG = LoggerFactory.getLogger(PluginLogicImpl.class);

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
                    manifest.getAttributes("plugin-context");
                    final Attributes mainAttributes = manifest.getMainAttributes();
                    String pluginContext = mainAttributes.getValue("plugin-context");
                    if (StringUtils.isNotEmpty(pluginContext)) {
                        LOG.info("PluginContext: {}", pluginContext);
                        contextFiles.add("classpath:" + pluginContext);
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

}
