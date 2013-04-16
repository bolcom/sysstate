package nl.unionsoft.sysstate.logic;

import nl.unionsoft.sysstate.common.dto.StateDto;

public interface PushStateLogic {

    public void push(Long instanceId, StateDto state);

    public StateDto fetch(Long instanceId);
}
