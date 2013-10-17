package nl.unionsoft.sysstate.logic;

import java.util.Map;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;

public interface StateLogic {

    public void clean();

    public void createOrUpdate(StateDto state);

    public StateDto requestStateForInstance(InstanceDto instance);

    public StateDto requestState(String pluginClass, Map<String, String> configuration);

    public StateDto getLastStateForInstance(Long instanceId);

}
