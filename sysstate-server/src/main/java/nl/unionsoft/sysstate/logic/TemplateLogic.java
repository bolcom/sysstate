package nl.unionsoft.sysstate.logic;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.template.WriterException;

public interface TemplateLogic {
    public TemplateDto getTemplate(String name);

    public void createOrUpdate(TemplateDto template);

    public void delete(String name);
    
    public List<TemplateDto> getTemplates();
    
    public void writeTemplate(TemplateDto template,Map<String, Object> context,  Writer writer) throws WriterException;


}
