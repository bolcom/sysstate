package nl.unionsoft.sysstate.logic;

import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.PropertyMetaList;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public interface PluginLogic {
    public ClassPathXmlApplicationContext getPluginApplicationContext();

    public <T> T getComponent(final String name);

    public String[] getComponentNames(Class<?> type);

//    public List<Plugin> getPlugins();

//    public Plugin getPlugin(String id);

    public Properties getPropertiesForClass(Class<?> theClass);

    public PropertyMetaList getPluginPropertyMetaList(String name);

    public void setPluginPropertyMeta(PropertyMetaList propertyMetaList);

    public Properties getPluginProperties(String name);

    public ListOfValueResolver getListOfValueResolver(String name);

}
