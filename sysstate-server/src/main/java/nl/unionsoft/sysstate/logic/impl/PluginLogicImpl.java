package nl.unionsoft.sysstate.logic.impl;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import nl.unionsoft.sysstate.logic.PluginLogic;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sun.misc.JarFilter;

public class PluginLogicImpl implements PluginLogic, ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private ClassPathXmlApplicationContext pluginApplicationContext;

    private ClassLoader pluginClassLoader;

    private String home;

    private static final Logger LOG = LoggerFactory.getLogger(PluginLogicImpl.class);

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    public void afterPropertiesSet() throws Exception {
        File plugins = new File(home, "plugins");
        LOG.info("Loading jars from plugins directory @ {}...", plugins);

        File[] files = plugins.listFiles(new JarFilter());

        List<String> contextFiles = new ArrayList<String>();
        List<URL> uris = new ArrayList<URL>();

        for (File file : files) {
            LOG.info("Found {}", file);
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(file);
                Manifest manifest = jarFile.getManifest();
                manifest.getAttributes("plugin-context");
                final Attributes mainAttributes = manifest.getMainAttributes();
                String pluginContext = mainAttributes.getValue("plugin-context");
                contextFiles.add("classpath:" + pluginContext);
            } finally {
                IOUtils.closeQuietly(jarFile);
            }
            uris.add(file.toURI().toURL());
        }
        pluginClassLoader = new URLClassLoader(uris.toArray(new URL[] {}));
        pluginApplicationContext = new ClassPathXmlApplicationContext(applicationContext);
        pluginApplicationContext.setClassLoader(pluginClassLoader);
        pluginApplicationContext.setConfigLocations(contextFiles.toArray(new String[] {}));
        pluginApplicationContext.refresh();
        // pluginApplicationContext.getBean(Class.forName("com.bol.sysstate.DatabaseDiscovery", true, pluginClassLoader));
    }

    public String getHome() {
        return home;
    }

    public void setHome(final String home) {
        this.home = home;
    }

    public ClassPathXmlApplicationContext getPluginApplicationContext() {
        return pluginApplicationContext;
    }

    public void setPluginApplicationContext(final ClassPathXmlApplicationContext pluginApplicationContext) {
        this.pluginApplicationContext = pluginApplicationContext;
    }

}
