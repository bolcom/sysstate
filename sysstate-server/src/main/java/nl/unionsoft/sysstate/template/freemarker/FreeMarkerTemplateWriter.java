package nl.unionsoft.sysstate.template.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;

import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.template.TemplateWriter;
import nl.unionsoft.sysstate.template.WriterException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service("freeMarkerTemplateWriter")
public class FreeMarkerTemplateWriter implements TemplateWriter {

    private Configuration cfg;

    @Inject
    public FreeMarkerTemplateWriter(@Value("#{properties['SYSSTATE_HOME']}") String sysstateHome) throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        Path templatePath = Paths.get(sysstateHome,"templates");
        Files.createDirectories(templatePath);
        FileTemplateLoader ftl = new FileTemplateLoader(templatePath.toFile());
        ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "");
        TemplateLoader[] loaders = new TemplateLoader[] { ftl, ctl };
        MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
        cfg.setTemplateLoader(mtl);
    }

    @Override
    public void writeTemplate(TemplateDto template, Writer writer, Map<String, Object> context) throws WriterException {
        try {
            Template freemarkerTemplate = cfg.getTemplate(template.getResource());
            freemarkerTemplate.process(context, writer);
        } catch (IOException | TemplateException e) {
            throw new WriterException("Caught Exception while writing template", e);
        }
    }
}
