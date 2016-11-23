package nl.unionsoft.sysstate.common.logic;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import nl.unionsoft.sysstate.common.dto.TemplateDto;

public interface TemplateLogic {
    public Optional<TemplateDto> getTemplate(String name);

    public void createOrUpdate(TemplateDto template);

    public void delete(String name);

    public List<TemplateDto> getTemplates();

    public void writeTemplate(String templateName, Map<String, Object> context, Writer writer);

    public void writeTemplate(TemplateDto template, Map<String, Object> context, Writer writer);

    public TemplateDto getBasicTemplate();

    public Set<String> getTemplateWriters();

}
