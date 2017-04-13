package nl.unionsoft.sysstate.web.rest.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.sysstate_1_0.Template;

@Service("restTemplateConverter")
public class TemplateConverter implements Converter<Template, TemplateDto> {

    @Override
    public Template convert(TemplateDto dto) {
        if (dto == null) {
            return null;
        }

        Template template = new Template();
        template.setName(dto.getName());
        template.setContentType(dto.getContentType());
        template.setResource(dto.getResource());
        template.setWriter(dto.getWriter());
        template.setIncludeViewResults(dto.getIncludeViewResults());
        return template;
    }

}
