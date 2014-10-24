package nl.unionsoft.sysstate.plugins.groovy;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
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

    private static final String[] internalGroovyResources = { "marathonStateResolver.groovy" };

    private File groovyScriptsExt;
    private File groovyScriptsInt;

    @Inject
    public GroovyScriptManager(@Value("#{properties['SYSSTATE_HOME']}") String sysstateHome) {
        File groovyHome = new File(sysstateHome, "groovy");
        groovyScriptsExt = new File(groovyHome, "ext");
        groovyScriptsInt = new File(groovyHome, "int");

    }

    public Set<String> getScriptNames() {
        Set<String> results = new TreeSet<String>();
        results.addAll(getFileNames(getGroovyScriptsFromDir(groovyScriptsExt)));
        results.addAll(getFileNames(getGroovyScriptsFromDir(groovyScriptsInt)));
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

        makeGroovyDir(groovyScriptsExt);
        makeGroovyDir(groovyScriptsInt);

        for (String internalGroovyResource : internalGroovyResources) {
            File targetFile = new File(groovyScriptsInt, internalGroovyResource);
            InputStream is = null;
            try {
                String internalResource = "nl/unionsoft/sysstate/plugins/groovy/" + internalGroovyResource;
                LOG.info("Copying internalResource [{}] to [{}]", internalResource, targetFile);
                is = this.getClass().getClassLoader().getResourceAsStream(internalResource);
                FileUtils.copyInputStreamToFile(is, targetFile);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    public File getScriptFile(String scriptName) {

        File extFile = new File(groovyScriptsExt, scriptName);
        if (extFile.exists()) {
            return extFile;
        }
        File intFile = new File(groovyScriptsInt, scriptName);
        if (intFile.exists()) {
            return intFile;
        }
        throw new IllegalArgumentException("No internal or external script with name [" + scriptName + "] could be found.");
    }
}
