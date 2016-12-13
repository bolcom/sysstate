package nl.unionsoft.sysstate.converter;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ConverterWithConfig;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.domain.State;

@Service("stateConverter")
public class StateConverter implements Converter<StateDto, State>, ConverterWithConfig<StateDto, State, Boolean> {

    public StateDto convert(State state) {
        return convert(state, true);
    }

    public StateDto convert(State state, Boolean nest) {
        StateDto result = new StateDto();
        if (state == null) {
            result.setState(StateType.PENDING);
            DateTime dateTime = new DateTime();
            result.setCreationDate(dateTime);
            result.setLastUpdate(dateTime);
        } else {
            result = new StateDto();
            result.setId(state.getId());
            result.setDescription(state.getDescription());
            result.appendMessage(StringUtils.trim(state.getMessage()));
            result.setResponseTime(state.getResponseTime());
            result.setState(state.getState());
            result.setRating(state.getRating());
            result.setCreationDate(new DateTime(state.getCreationDate()));
            result.setLastUpdate(new DateTime(state.getLastUpdate()));
        }
        return result;
    }
}
