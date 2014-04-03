package nl.unionsoft.sysstate.common.extending;

import java.util.Map;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;

public interface StateResolver {
    public void setState(InstanceDto instance, StateDto state, Map<String, String> config);

    public String generateHomePageUrl(InstanceDto instance, Map<String, String> config);
}
