package nl.unionsoft.sysstate.logic.impl;

import java.util.HashMap;
import java.util.Map;

import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.logic.PushStateLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("pushStateLogic")
public class PushStateLogicImpl implements PushStateLogic {

    private static final Logger LOG = LoggerFactory.getLogger(PushStateLogicImpl.class);
    private final Map<Long, StateDto> states;

    public PushStateLogicImpl () {
        states = new HashMap<Long, StateDto>();
    }

    public void push(Long instanceId, StateDto state) {
        if (instanceId != null) {
            LOG.info("Pushing state for instanceId '{}': {}", instanceId, state);
            states.put(instanceId, state);
        }

    }

    public StateDto fetch(Long instanceId) {
        final StateDto state = states.get(instanceId);
        LOG.info("Fetched state for instanceId '{}': {}", instanceId, state);
        return state;
    }
}
