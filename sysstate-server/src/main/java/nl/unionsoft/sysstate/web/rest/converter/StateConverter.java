package nl.unionsoft.sysstate.web.rest.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.State;

@Service("restStateConverter")
public class StateConverter implements Converter<State, StateDto>{

    @Override
    public State convert(StateDto dto) {
        if (dto == null){
            return null;
        }
        
        State state = new State();
        state.setDescription(dto.getDescription());
        state.setId(dto.getId());
        state.setMessage(dto.getMessage());
        state.setResponseTime(dto.getResponseTime());
        state.setState(dto.getState() == null ? null : dto.getState().toString());
        return state;
    }



}
