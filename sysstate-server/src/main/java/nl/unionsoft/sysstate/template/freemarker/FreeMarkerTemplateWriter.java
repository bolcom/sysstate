package nl.unionsoft.sysstate.template.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.template.TemplateWriter;

@Service("freeMarkerTemplateWriter")
public class FreeMarkerTemplateWriter implements TemplateWriter {

    private static final Logger LOG = LoggerFactory.getLogger(FreeMarkerTemplateWriter.class);
    private final Configuration cfg;
    
    @Inject
    public FreeMarkerTemplateWriter(@Value("${SYSSTATE_HOME}") String sysstateHome,  TextTemplateLoader textTemplateLoader) throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        Path templatePath = Paths.get(sysstateHome, "templates");
        LOG.info("Creating directories for templatePath [{}]...", templatePath);
        Files.createDirectories(templatePath);
        FileTemplateLoader ftl = new FileTemplateLoader(templatePath.toFile());
        ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "");
        TemplateLoader[] loaders = new TemplateLoader[] { ftl, ctl , textTemplateLoader};
        MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
        cfg.setTemplateLoader(mtl);
    }

    @Override
    public void writeTemplate(TemplateDto template, Writer writer, Map<String, Object> context){
        try {
            Template freemarkerTemplate = cfg.getTemplate(template.getResource());
            freemarkerTemplate.process(context, writer);
        } catch (IOException | TemplateException e) {
            throw new IllegalStateException("Caught Exception while writing template", e);
        }
    }
}
