package nl.unionsoft.sysstate.logic;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;

public interface StateLogic {

    public void clean();

    public void createOrUpdate(StateDto state);

    public StateDto requestStateForInstance(InstanceDto instance);

    public StateDto requestState(String pluginClass, InstanceConfiguration configuration);

    public StateDto getLastStateForInstance(Long instanceId);

}
