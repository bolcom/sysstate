package nl.unionsoft.sysstate.template;

import java.io.Writer;
import java.util.Map;

import nl.unionsoft.sysstate.common.dto.TemplateDto;

public interface TemplateWriter {

    public void writeTemplate(TemplateDto templateName, Writer writer, Map<String, Object> context);
}
