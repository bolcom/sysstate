package nl.unionsoft.sysstate.plugins.example;

import java.util.Map;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.StateResolver;

import org.springframework.stereotype.Service;

@Service("exampleStateResolver")
public class ExampleStateResolverImpl implements StateResolver {

    public void setState(final InstanceDto instance, final StateDto state, Map<String, String> configuration) {
        state.setState(StateType.STABLE);
    }

    public String generateHomePageUrl(final InstanceDto instance, Map<String, String> configuration) {
        return "http://www.unionsoft.nl/";
    }

}
