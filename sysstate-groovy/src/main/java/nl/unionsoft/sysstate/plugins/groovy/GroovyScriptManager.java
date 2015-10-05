package nl.unionsoft.sysstate.plugins.groovy;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("groovyScriptManager")
public class GroovyScriptManager implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyScriptManager.class);

    private File groovyHome;

    @Inject
    public GroovyScriptManager(@Value("${SYSSTATE_HOME}") String sysstateHome) {
        LOG.info("Constructing GroovyScriptManager with sysstateHome [{}]", sysstateHome);
        groovyHome = new File(sysstateHome, "groovy");
    }

    public Set<String> getScriptNames() {
        Set<String> results = new TreeSet<String>();
        results.addAll(getFileNames(getGroovyScriptsFromDir(groovyHome)));
        return results;
    }

    private List<String> getFileNames(File[] files) {
        List<String> fileNames = new ArrayList<String>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;

    }

    private File[] getGroovyScriptsFromDir(File dir) {
        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".groovy");
            }
        });
    }

    private void makeGroovyDir(File groovyDir) {
        LOG.info("Checking if groovySourceFolder [{}] exists...", groovyDir);
        if (groovyDir.exists()) {
            LOG.info("groovySourceFolder [{}] exists!", groovyDir);
        } else {
            LOG.info("Attempting to create directory [{}]", groovyDir);
            boolean result = groovyDir.mkdirs();
            LOG.info("Creation of [{}] status: {}", groovyDir, result);
        }
    }

    public void afterPropertiesSet() throws Exception {

        makeGroovyDir(groovyHome);
    }

    public File getScriptFile(String scriptName) {

        File extFile = new File(groovyHome, scriptName);
        if (extFile.exists()) {
            return extFile;
        }
        throw new IllegalArgumentException("No internal or external script with name [" + scriptName + "] could be found.");
    }

}
