package nl.unionsoft.sysstate.common.extending;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.enums.StateType;

public class InstanceNotification implements Notification {

    private InstanceDto instance;
    private StateType previousState;
    private StateType currentState;

    public InstanceDto getInstance() {
        return instance;
    }

    public void setInstance(InstanceDto instance) {
        this.instance = instance;
    }

    public StateType getPreviousState() {
        return previousState;
    }

    public void setPreviousState(StateType previousState) {
        this.previousState = previousState;
    }

    public StateType getCurrentState() {
        return currentState;
    }

    public void setCurrentState(StateType currentState) {
        this.currentState = currentState;
    }

}
