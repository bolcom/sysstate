package nl.unionsoft.sysstate.logic;

import java.util.Map;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;

public interface StateLogic {

    public void clean();

    public StateDto createOrUpdate(StateDto state);

    public StateDto requestStateForInstance(InstanceDto instance);

    public StateDto requestState(String pluginClass, Map<String, String> configuration);

    public StateDto getLastStateForInstance(Long instanceId);

    public StateDto getLastStateForInstance(Long instanceId, StateType stable);

}
