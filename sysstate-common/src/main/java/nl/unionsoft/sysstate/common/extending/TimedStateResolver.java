package nl.unionsoft.sysstate.common.extending;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.util.StateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TimedStateResolver implements StateResolver {

    private static final Logger LOG = LoggerFactory.getLogger(TimedStateResolver.class);

    public void setState(InstanceDto instance, StateDto state) {

        final Long startTime = System.currentTimeMillis();

        try {
            setStateTimed(instance, state);
        } catch (final Exception e) {
            LOG.warn("Caught Exception while performing request: {}", e.getMessage(), e);
            handleStateForException(state, e, startTime);
        } finally {
            if (state.getResponseTime() <= 0) {
                state.setResponseTime(System.currentTimeMillis() - startTime);
            }

        }

    }

    public abstract void setStateTimed(InstanceDto instance, StateDto state) throws Exception;

    private void handleStateForException(final StateDto state, final Exception exception, final Long startTime) {
        state.setState(StateType.ERROR);
        state.setDescription(exception.getMessage());
        state.appendMessage(StateUtil.exceptionAsMessage(exception));
    }
}
