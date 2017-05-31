package nl.unionsoft.sysstate.plugins.groovy;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import nl.unionsoft.commons.properties.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.extending.TimedStateResolver;


@Service("groovyStateResolver")
public class GroovyStateResolverImpl extends TimedStateResolver implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private GroovyScriptManager groovyScriptManager;

    @Inject
    public GroovyStateResolverImpl(GroovyScriptManager groovyScriptManager, GroovyScriptExecutor groovyScriptExecutor) {
        this.groovyScriptManager = groovyScriptManager;
    }

    public String generateHomePageUrl(final InstanceDto instance) {
        return null;
    }

    @Override
    public void setStateTimed(InstanceDto instance, StateDto state) throws CompilationFailedException, IOException, InstantiationException,
            IllegalAccessException {

        Map<String, String> configuration = instance.getConfiguration();
        ClassLoader classLoader = getClass().getClassLoader();
        File groovyScript = groovyScriptManager.getScriptFile(configuration.get("groovyScript"));

        Binding binding = new Binding();
        binding.setVariable("state", state);
        binding.setVariable("instance", instance);
        binding.setVariable("properties", PropertiesUtil.stringToProperties(configuration.get("bindingProperties")));
        binding.setVariable("applicationContext", applicationContext);
        GroovyShell shell = new GroovyShell(classLoader, binding);
        shell.evaluate(groovyScript);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
