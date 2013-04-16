package nl.unionsoft.sysstate.logic.factory;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.util.JSPFProperties;

import org.springframework.beans.factory.FactoryBean;

public class JSPFPropertyFactory implements FactoryBean<JSPFProperties> {

    private String cacheEnabled;
    private String cacheMode;
    private String cacheFile;

    public JSPFProperties getObject() throws Exception {
        final JSPFProperties props = new JSPFProperties();
        props.setProperty(PluginManager.class, "cache.enabled", "true");
        props.setProperty(PluginManager.class, "cache.mode", "weak"); // optional
        props.setProperty(PluginManager.class, "cache.file", "jspf.cache");
        return props;
    }

    public Class<?> getObjectType() {

        return JSPFProperties.class;
    }

    public boolean isSingleton() {

        return false;
    }

    public String getCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(String cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public String getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(String cacheMode) {
        this.cacheMode = cacheMode;
    }

    public String getCacheFile() {
        return cacheFile;
    }

    public void setCacheFile(String cacheFile) {
        this.cacheFile = cacheFile;
    }

}
