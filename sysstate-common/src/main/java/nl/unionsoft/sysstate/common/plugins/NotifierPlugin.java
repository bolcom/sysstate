package nl.unionsoft.sysstate.common.plugins;

import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.StateDto;

public interface NotifierPlugin extends PostWorkerPlugin {
    public void notify(StateDto state, StateDto lastState, Properties properties);

}
