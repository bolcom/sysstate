package nl.unionsoft.sysstate.plugins.impl.discovery;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.logic.ProjectLogic;

// @PluginImplementation
public class GapDiscoveryImpl {

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

}
