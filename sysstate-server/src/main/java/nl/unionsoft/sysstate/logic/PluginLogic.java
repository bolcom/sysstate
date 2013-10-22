package nl.unionsoft.sysstate.logic;

import java.util.List;
import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.PropertyMetaList;
import nl.unionsoft.sysstate.logic.impl.PluginLogicImpl.Plugin;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public interface PluginLogic {
    public ClassPathXmlApplicationContext getPluginApplicationContext();

    public <T> T getComponent(final String name);

    public String[] getComponentNames(Class<?> type);

    public List<Plugin> getPlugins();

    public Properties getPropertiesForClass(Class<?> theClass);

    public PropertyMetaList getPluginPropertyMetaList(String name);

    public void setPluginPropertyMeta(PropertyMetaList propertyMetaList);

    public Properties getPluginProperties(String name);

}
