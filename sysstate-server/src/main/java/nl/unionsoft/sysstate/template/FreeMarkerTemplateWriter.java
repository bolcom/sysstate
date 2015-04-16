package nl.unionsoft.sysstate.template;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.inject.Inject;

import nl.unionsoft.sysstate.common.dto.TemplateDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service("freeMarkerTemplateWriter")
public class FreeMarkerTemplateWriter implements TemplateWriter {

    private Configuration cfg;

    @Inject
    public FreeMarkerTemplateWriter(@Value("#{properties['SYSSTATE_HOME']}") String sysstateHome) throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setTemplateLoader(new FileTemplateLoader(new File(sysstateHome, "templates")));

    }

    @Override
    public void writeTemplate(TemplateDto template, Writer writer, Map<String, Object> context) throws WriterException {
        try {
            Template freemarkerTemplate = cfg.getTemplate(template.getName());
            freemarkerTemplate.process(context, writer);
        } catch (IOException | TemplateException e) {
            throw new WriterException("Caught Exception while writing template", e);
        }
    }

}
