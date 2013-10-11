package nl.unionsoft.sysstate.plugins.example;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.ConfigurationHolder;
import nl.unionsoft.sysstate.common.extending.StateResolver;

import org.springframework.stereotype.Service;

@Service("exampleStateResolver")
public class ExampleStateResolverImpl implements StateResolver {

    public void setState(final InstanceDto instance, final StateDto state,final ConfigurationHolder configurationHolder) {
        state.setState(StateType.STABLE);
    }

    public String generateHomePageUrl(final InstanceDto instance) {
        return "http://www.unionsoft.nl/";
    }

}
