package nl.unionsoft.sysstate.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.inject.Inject;

import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.logic.TemplateLogic;

import org.springframework.stereotype.Service;

@Service("stringTemplateWriter")
public class StringTemplateWriter implements TemplateWriter {
    @Override
    public void writeTemplate(TemplateDto template, Writer writer, Map<String, Object> context) throws WriterException {
        try {
            writer.write(template.getContent());
        } catch (IOException e) {
            throw new WriterException("Unable to write content", e);
        }

    }

}
