package nl.unionsoft.sysstate.common.extending;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;

public interface StateResolver {
    public void setState(InstanceDto instance, StateDto state);

    public String generateHomePageUrl(InstanceDto instance);
}
