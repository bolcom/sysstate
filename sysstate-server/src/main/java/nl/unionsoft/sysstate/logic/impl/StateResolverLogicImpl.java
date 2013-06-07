package nl.unionsoft.sysstate.logic.impl;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.extending.StateResolver;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

import org.springframework.stereotype.Service;

@Service("stateResolverLogic")
public class StateResolverLogicImpl implements StateResolverLogic {

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @SuppressWarnings("unchecked")
    public StateResolver getStateResolver(final String name) {
        StateResolver result = null;
        try {
            Class<?> beanClass = Class.forName(name);
            result = (StateResolver) pluginLogic.getPluginApplicationContext().getBean(beanClass);
        } catch (ClassNotFoundException e) {
            result = pluginLogic.getPluginApplicationContext().getBean(name, StateResolver.class);
        }
        return result;
    }

    public String[] getStateResolverNames() {
        return pluginLogic.getPluginApplicationContext().getBeanNamesForType(StateResolver.class);
    }

}
