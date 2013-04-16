package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.plugins.StateResolverPlugin;
import nl.unionsoft.sysstate.logic.PushStateLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

import org.joda.time.DateTime;

// pushRequestsStateResolverPlugin
@PluginImplementation
public class PushRequestsStateResolverPluginImpl implements StateResolverPlugin {

    @Inject
    @Named("pushStateLogic")
    private PushStateLogic pushStateLogic;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    public void setState(InstanceDto instance, StateDto state) {
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

    public void setPushStateLogic(PushStateLogic pushStateLogic) {
        this.pushStateLogic = pushStateLogic;
    }

    public StateLogic getStateLogic() {
        return stateLogic;
    }

    public void setStateLogic(StateLogic stateLogic) {
        this.stateLogic = stateLogic;
    }

    public String generateHomePageUrl(InstanceDto instance) {
        return null;
    }

}
