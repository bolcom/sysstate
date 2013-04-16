package nl.unionsoft.sysstate.logic;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.xeoh.plugins.base.Plugin;
import nl.unionsoft.sysstate.logic.impl.PluginLogicImpl.PluginInstance;

public interface PluginLogic {

    public <T extends Plugin> T getPlugin(String clazz);

    public <T extends Plugin> T getPlugin(Class<T> clazz);

    public <T extends Plugin> List<T> getPlugins(Class<T> clazz);

    public <T extends Plugin> PluginInstance<T> getPluginInstance(String clazz);

    public <T extends Plugin> PluginInstance<T> getPluginInstance(Class<T> clazz);

    public <T extends Plugin> List<PluginInstance<T>> getPluginInstances(Class<T> clazz);

    public <T extends Plugin> Set<Class<T>> getPluginClasses(Class<T> clazz);

    public void updatePluginConfiguration(String clazz, Properties configuration);

}
