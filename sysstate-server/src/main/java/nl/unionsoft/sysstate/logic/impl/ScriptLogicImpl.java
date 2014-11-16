package nl.unionsoft.sysstate.logic.impl;

import nl.unionsoft.sysstate.common.extending.ScriptExecutor;
import nl.unionsoft.sysstate.common.logic.ScriptLogic;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("scriptLogicImpl")
public class ScriptLogicImpl implements ScriptLogic, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Object execute(String script, String scriptExecutorName) {
        ScriptExecutor scriptExecutor = applicationContext.getBean(scriptExecutorName, ScriptExecutor.class);
        return scriptExecutor.executeScript(script);

    }

    public String[] getScriptExecutorNames() {
        return applicationContext.getBeanNamesForType(ScriptExecutor.class);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
