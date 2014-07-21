package nl.unionsoft.sysstate.common.extending;

import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.StateDto;

public interface Notifier {
    public void notify(StateDto state, StateDto lastState, Properties properties);

}
