package nl.unionsoft.sysstate.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.domain.Template;
@Service("templateConverter")
public class TemplateConverter implements Converter<TemplateDto, Template> {

    @Override
    public TemplateDto convert(Template template) {
       if (template == null){
           return null;
       }
       TemplateDto dto = new TemplateDto();
       dto.setContent(template.getContent());
       dto.setName(template.getName());
       dto.setWriter(template.getWriter());
       dto.setContentType(template.getContentType());
       dto.setLastUpdated(template.getLastUpdated());
       dto.setConfiguration(PropertiesUtil.stringToProperties(template.getConfiguration()));
       return dto;
    }

}
