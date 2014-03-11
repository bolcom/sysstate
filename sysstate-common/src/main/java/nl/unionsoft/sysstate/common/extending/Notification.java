package nl.unionsoft.sysstate.common.extending;

import nl.unionsoft.sysstate.common.enums.StateType;

public interface Notification {

    
    public StateType getPreviousState();

    public StateType getCurrentState();
    
    
    
}
