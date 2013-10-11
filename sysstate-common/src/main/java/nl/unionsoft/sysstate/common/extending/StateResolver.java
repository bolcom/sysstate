package nl.unionsoft.sysstate.common.extending;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;

public interface StateResolver<ICT extends InstanceConfiguration> {
    public void setState(InstanceDto<ICT> instance, StateDto state, ConfigurationHolder configuration);

    public String generateHomePageUrl(InstanceDto<ICT> instance);
}
