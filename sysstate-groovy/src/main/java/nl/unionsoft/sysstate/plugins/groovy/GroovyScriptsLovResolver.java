package nl.unionsoft.sysstate.plugins.groovy;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.logic.HttpClientLogic;
import nl.unionsoft.sysstate.plugins.http.HttpStateResolverImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("groovyScriptsLovResolver")
public class GroovyScriptsLovResolver implements ListOfValueResolver {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyScriptsLovResolver.class);
    
    @Value("#{properties['SYSSTATE_HOME']}")
    private String sysstateHome;
    

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {

        File groovyHome = new File(sysstateHome, "groovy");
        LOG.info("Fetching groovy files from [{}]", groovyHome);
        File[] groovyFiles = groovyHome.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".groovy");
            }
        });
        
        LOG.info("Found '.groovy' files:", groovyFiles);
        Map<String, String> results = new LinkedHashMap<String, String>();
        if (groovyFiles != null) {
            for (File file : groovyFiles) {
                results.put(file.getName(), file.getName());
            }
        }
        return results;
    }

}
