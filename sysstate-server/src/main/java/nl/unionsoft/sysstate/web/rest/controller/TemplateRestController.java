package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.common.logic.TemplateLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Template;
import nl.unionsoft.sysstate.sysstate_1_0.TemplateList;
import nl.unionsoft.sysstate.web.rest.converter.TemplateConverter;

@Controller()
public class TemplateRestController {

    @Inject
    private TemplateLogic templateLogic;

    @Inject
    private TemplateConverter templateConverter;

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public TemplateList getList() {
        List<Template> templates = ListConverter.convert(templateConverter, templateLogic.getTemplates());
        TemplateList list = new TemplateList();
        list.getTemplates().addAll(templates);
        return list;
    }

    @RequestMapping(value = "/template/{name}", method = RequestMethod.GET)
    public Template get(@PathVariable("name") final String name) {
        Optional<TemplateDto> optTemplate = templateLogic.getTemplate(name);
        if (!optTemplate.isPresent()) {
            throw new IllegalStateException("Text with name [" + name + "] cannot be found");
        }
        return templateConverter.convert(optTemplate.get());
    }

    @RequestMapping(value = "/template", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Template template) {
        Optional<TemplateDto> optTemplate = templateLogic.getTemplate(template.getName());
        TemplateDto dto = new TemplateDto();
        if (optTemplate.isPresent()) {
            dto.setId(optTemplate.get().getId());
        }
        dto.setName(template.getName());
        dto.setContentType(template.getContentType());
        dto.setResource(template.getResource());
        dto.setIncludeViewResults(template.isIncludeViewResults());
        dto.setWriter(template.getWriter());
        templateLogic.createOrUpdate(dto);
    }

    @RequestMapping(value = "/template/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("name") final String name) {
        templateLogic.delete(name);
    }
}
