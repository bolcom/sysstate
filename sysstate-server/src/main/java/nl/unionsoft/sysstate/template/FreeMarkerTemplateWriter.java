package nl.unionsoft.sysstate.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.inject.Inject;

import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.template.freemarker.SysStateTemplateLoader;

import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service("freeMarkerTemplateWriter")
public class FreeMarkerTemplateWriter implements TemplateWriter {

    private Configuration cfg;

    @Inject
    public FreeMarkerTemplateWriter(final SysStateTemplateLoader sysStateTemplateLoader) {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setTemplateLoader(sysStateTemplateLoader);

    }

    @Override
    public void writeTemplate(TemplateDto template, Writer writer, Map<String, Object> context) throws WriterException {
        try {
            Template freemarkerTemplate = cfg.getTemplate(template.getName());
            freemarkerTemplate.process(context, writer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TemplateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
