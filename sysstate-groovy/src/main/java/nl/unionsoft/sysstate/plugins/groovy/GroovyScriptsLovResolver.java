package nl.unionsoft.sysstate.plugins.groovy;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("groovyScriptsLovResolver")
public class GroovyScriptsLovResolver implements ListOfValueResolver {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyScriptsLovResolver.class);

    private GroovyScriptManager groovyScriptManager;

    @Inject
    public GroovyScriptsLovResolver(GroovyScriptManager groovyScriptManager) {
        this.groovyScriptManager = groovyScriptManager;
    }

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        Map<String, String> results = new LinkedHashMap<String, String>();
        for (String scriptName : groovyScriptManager.getScriptNames()) {
            results.put(scriptName, scriptName);
        }
        return results;
    }

}
