package nl.unionsoft.sysstate.common.logic;

import java.util.List;

public interface ScriptLogic {

    
    public Object execute(String script, String scriptExecutor);
    
    public String[] getScriptExecutorNames();
}
