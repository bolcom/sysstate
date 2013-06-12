package nl.unionsoft.sysstate.logic;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public interface PluginLogic {
    public ClassPathXmlApplicationContext getPluginApplicationContext();

    public <T> T getComponent(final String name);

    public String[] getComponentNames(Class<?> type);

}
