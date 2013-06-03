package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.StateResolver;
import nl.unionsoft.sysstate.logic.PushStateLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

// pushRequestsStateResolverPlugin
@Service("pushRequestsStateResolver")
public class PushRequestsStateResolverImpl implements StateResolver {

    @Inject
    @Named("pushStateLogic")
    private PushStateLogic pushStateLogic;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    public void setState(final InstanceDto instance, final StateDto state) {
        Properties properties = PropertiesUtil.stringToProperties(instance.getConfiguration());
        Long timeout = Long.valueOf(PropertiesUtil.getProperty(properties, "timeout", Long.toString(1000 * 60 * 10)));

        final Long instanceId = instance.getId();
        StateDto fetchedState = pushStateLogic.fetch(instanceId);
        if (fetchedState == null) {
            // Get last one instead and check if still valid...
            StateDto lastState = stateLogic.getLastStateForInstance(instanceId);
            if (lastState != null) {
                DateTime creationDate = lastState.getCreationDate();
                if (creationDate != null) {
                    if (timeout == -1 || creationDate.plus(timeout).isAfter(new DateTime())) {
                        fetchedState = lastState;
                    } else {
                        state.setState(StateType.UNSTABLE);
                        state.setMessage("Instance hasn't reported in since " + creationDate);
                        state.setDescription("Missing");
                    }
                }
            }
        }

        if (fetchedState == null) {
            state.setState(StateType.PENDING);
        } else {
            state.setState(fetchedState.getState());
            state.setDescription(fetchedState.getDescription());
            state.setMessage(fetchedState.getMessage());
            state.setResponseTime(fetchedState.getResponseTime());
        }
    }

    public PushStateLogic getPushStateLogic() {
        return pushStateLogic;
    }

    public void setPushStateLogic(final PushStateLogic pushStateLogic) {
        this.pushStateLogic = pushStateLogic;
    }

    public StateLogic getStateLogic() {
        return stateLogic;
    }

    public void setStateLogic(final StateLogic stateLogic) {
        this.stateLogic = stateLogic;
    }

    public String generateHomePageUrl(final InstanceDto instance) {
        return null;
    }

}
