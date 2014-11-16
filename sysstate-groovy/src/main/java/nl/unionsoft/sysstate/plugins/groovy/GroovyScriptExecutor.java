package nl.unionsoft.sysstate.plugins.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Map;

import nl.unionsoft.sysstate.common.extending.ScriptExecutor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("groovyScriptExecutor")
public class GroovyScriptExecutor implements ScriptExecutor, ApplicationContextAware {

    private ApplicationContext applicationContext;
    
    public Object executeScript(String script) {
        Binding binding = new Binding();
        binding.setVariable("applicationContext", applicationContext);
        ClassLoader classLoader = getClass().getClassLoader();
        GroovyShell shell = new GroovyShell(classLoader, binding);
        return shell.evaluate(script);        
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
    }

}
