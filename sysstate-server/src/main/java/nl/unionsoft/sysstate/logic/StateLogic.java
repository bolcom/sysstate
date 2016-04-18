package nl.unionsoft.sysstate.logic;

import java.util.List;
import java.util.Map;

import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateBehaviour;
import nl.unionsoft.sysstate.common.enums.StateType;

public interface StateLogic {

    public void clean();

    public StateDto createOrUpdate(StateDto state);

    public StateDto requestStateForInstance(InstanceDto instance);

    public StateDto requestState(String pluginClass, Map<String, String> configuration);

    public StateDto getLastStateForInstance(InstanceDto instance);
    
    public StateDto getLastStateForInstance(InstanceDto instance, StateBehaviour stateBehaviour);

    public StateDto getLastStateForInstance(InstanceDto instance, StateType stable);
    
}
