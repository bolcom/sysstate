package nl.unionsoft.sysstate.plugins.impl.discovery;

import javax.inject.Inject;
import javax.inject.Named;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;

@PluginImplementation
public class GapDiscoveryPlugimImpl {

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

}
