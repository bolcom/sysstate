package nl.unionsoft.sysstate.common.logic;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;

public interface NotifierLogic {

    public void notify(InstanceDto instance, StateDto state, StateDto lastState);

}
