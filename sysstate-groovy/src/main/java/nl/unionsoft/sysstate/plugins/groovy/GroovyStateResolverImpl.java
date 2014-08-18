package nl.unionsoft.sysstate.plugins.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.extending.TimedStateResolver;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("groovyStateResolver")
public class GroovyStateResolverImpl extends TimedStateResolver {

    @Value("#{properties['SYSSTATE_HOME']}")
    private String sysstateHome;

    public String generateHomePageUrl(final InstanceDto instance) {
        return null;
    }

    @Override
    public void setStateTimed(InstanceDto instance, StateDto state) throws CompilationFailedException, IOException {
        Map<String, String> configuration = instance.getConfiguration();
        Binding binding = new Binding();
        binding.setVariable("state", state);
        binding.setVariable("instance", instance);
        binding.setVariable("properties",PropertiesUtil.stringToProperties(configuration.get("bindingProperties")));
        GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding);
        File groovyHome = new File(sysstateHome, "groovy");
        File groovyScript = new File(groovyHome, configuration.get("groovyScript"));
        shell.evaluate(groovyScript);
    }

}
