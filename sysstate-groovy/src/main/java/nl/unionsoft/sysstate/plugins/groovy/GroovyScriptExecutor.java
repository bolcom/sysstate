package nl.unionsoft.sysstate.plugins.groovy;

import java.io.PrintStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import nl.unionsoft.sysstate.common.extending.ScriptExecutionResult;
import nl.unionsoft.sysstate.common.extending.ScriptExecutor;

@Service("groovyScriptExecutor")
public class GroovyScriptExecutor implements ScriptExecutor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public ScriptExecutionResult executeScript(String script) {
        Binding binding = new Binding();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        binding.setProperty("out", new PrintStream(output));
        binding.setVariable("applicationContext", applicationContext);

        ClassLoader classLoader = getClass().getClassLoader();
        GroovyShell shell = new GroovyShell(classLoader, binding);

        try {
            return new ScriptExecutionResult(shell.evaluate(script), new String(output.toByteArray()));
        } catch (Exception e) {
            return new ScriptExecutionResult(e, new String(output.toByteArray()));
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
