package nl.unionsoft.sysstate.logic;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.template.TemplateWriter;
import nl.unionsoft.sysstate.template.WriterException;

public interface TemplateLogic {
    public TemplateDto getTemplate(String name) throws IOException;

    public void createOrUpdate(TemplateDto template) throws IOException;

    public void delete(String name);
    
    public List<TemplateDto> getTemplates();
    
    public void writeTemplate(TemplateDto template,Map<String, Object> context,  Writer writer) throws WriterException;

    public TemplateDto getBasicTemplate();

    public  Map<String, TemplateWriter> getTemplateWriters();
    
}
