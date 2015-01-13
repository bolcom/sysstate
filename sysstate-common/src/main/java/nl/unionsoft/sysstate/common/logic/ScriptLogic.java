package nl.unionsoft.sysstate.common.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.extending.ScriptExecutionResult;

public interface ScriptLogic {

    
    public ScriptExecutionResult execute(String script, String scriptExecutor);
    
    public String[] getScriptExecutorNames();
}
