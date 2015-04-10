package nl.unionsoft.sysstate.logic.impl;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.converter.TemplateConverter;
import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.template.TemplateWriter;
import nl.unionsoft.sysstate.template.WriterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("templateLogic")
public class TemplateLogicImpl implements TemplateLogic, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Inject
    @Named("templateDao")
    private TemplateDao templateDao;

    @Inject
    @Named("templateConverter")
    private TemplateConverter templateConverter;

  
    @Override
    public void createOrUpdate(TemplateDto dto) {
        Template template = new Template();
        template.setContent(dto.getContent());
        template.setName(dto.getName());
        template.setWriter(dto.getWriter());
        template.setContentType(dto.getContentType());
        templateDao.createOrUpdate(template);
    }

    @Override
    public void delete(String name) {
        templateDao.delete(name);
    }

    @Override
    public List<TemplateDto> getTemplates() {
        return ListConverter.convert(templateConverter, templateDao.getTemplates());

    }

    @Override
    public void writeTemplate(TemplateDto template,  Map<String, Object> context, Writer writer) throws WriterException {
        TemplateWriter templateWriter = applicationContext.getBean(template.getWriter(), TemplateWriter.class);
        templateWriter.writeTemplate(template, writer, context);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public TemplateDto getTemplate(String name) {
        return templateConverter.convert(templateDao.getTemplate(name));
    }

}
