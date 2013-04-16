package nl.unionsoft.sysstate.common.plugins;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;

public interface StateResolverPlugin extends WorkerPlugin {
    public void setState(InstanceDto instance, StateDto state);

    public String generateHomePageUrl(InstanceDto instance);
}
