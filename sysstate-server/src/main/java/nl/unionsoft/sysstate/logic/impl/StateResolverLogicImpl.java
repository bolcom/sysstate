package nl.unionsoft.sysstate.logic.impl;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.extending.ConfiguredBy;
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
        return pluginLogic.getComponent(name);
    }

    public String[] getStateResolverNames() {
        return pluginLogic.getComponentNames(StateResolver.class);
    }

    public StateResolverMeta getStateResolverMeta(final String stateResolverName) {
        StateResolver stateResolver = getStateResolver(stateResolverName);
        StateResolverMeta stateResolverMeta = null;
        if (stateResolver != null) {
            stateResolverMeta = new StateResolverMeta();
            stateResolverMeta.setName(stateResolverName);
            ConfiguredBy configuredBy = stateResolver.getClass().getAnnotation(ConfiguredBy.class);
            if (configuredBy != null) {
                stateResolverMeta.setConfigurationClass(configuredBy.configurationClass());
            }
        }

        return stateResolverMeta;
    }

}
