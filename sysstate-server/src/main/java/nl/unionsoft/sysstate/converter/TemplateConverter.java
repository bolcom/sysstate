package nl.unionsoft.sysstate.converter;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.domain.Template;

import org.springframework.stereotype.Service;
@Service("templateConverter")
public class TemplateConverter implements Converter<TemplateDto, Template> {

    @Override
    public TemplateDto convert(Template template) {
       if (template == null){
           return null;
       }
       TemplateDto dto = new TemplateDto();
       dto.setId(template.getId());
       dto.setName(template.getName());
       dto.setWriter(template.getWriter());
       dto.setContentType(template.getContentType());
       dto.setLastUpdated(template.getLastUpdated());
       dto.setResource(template.getResource());
       dto.setIncludeViewResults(template.getIncludeViewResults());
       return dto;
    }

}
