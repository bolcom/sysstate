package nl.unionsoft.sysstate.logic.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.converter.TemplateConverter;
import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.template.TemplateWriter;
import nl.unionsoft.sysstate.template.WriterException;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service("templateLogic")
public class TemplateLogicImpl implements TemplateLogic {

    private static final String CI_FTL_NAME = "ci.html";
    private static final String BASE_FTL_NAME = "base.html";
    private static final String NETWORK_FTL_NAME = "network.html";

    private static final String BASE_FTL_RESOURCE = "base.ftl";
    private static final String CI_FTL_RESOURCE = "ci.ftl";
    private static final String NETWORK_FTL_RESOURCE = "network.ftl";

    private static final Logger LOG = LoggerFactory.getLogger(TemplateLogicImpl.class);

    private ApplicationContext applicationContext;

    private TemplateDao templateDao;

    private TemplateConverter templateConverter;

    private Path templateHome;

    public static final String RESOURCE_BASE = "/nl/unionsoft/sysstate/templates/";
    public static final String FREEMARKER_TEMPLATE_WRITER = "freeMarkerTemplateWriter";

    @Inject
    public TemplateLogicImpl(TemplateConverter templateConverter, TemplateDao templateDao, ApplicationContext applicationContext,
            @Value("#{properties['SYSSTATE_HOME']}") String sysstateHome) {
        this.templateHome = Paths.get(sysstateHome, "templates");

        this.templateConverter = templateConverter;
        this.templateDao = templateDao;
        this.applicationContext = applicationContext;
    }

    @Override
    public void createOrUpdate(TemplateDto dto) throws IOException {
        Template template = new Template();
        template.setId(dto.getId());
        template.setName(dto.getName());
        template.setWriter(dto.getWriter());
        template.setContentType(dto.getContentType());
        template.setResource(dto.getResource());
        template.setIncludeViewResults(dto.getIncludeViewResults());
        templateDao.createOrUpdate(template);
    }

    @PostConstruct
    public void addTemplatesIfNoneExist() throws IOException {
        LOG.info("Creating templates directory...");
        Files.createDirectories(templateHome);

        LOG.info("No templates found, creating some default templates...");

        addTemplate("base.css", "text/css", FREEMARKER_TEMPLATE_WRITER, "css/base.css", false);
        addTemplate("ci.css", "text/css", FREEMARKER_TEMPLATE_WRITER, "css/ci.css", false);
        addTemplate(CI_FTL_NAME, ContentType.TEXT_HTML.getMimeType(), FREEMARKER_TEMPLATE_WRITER, CI_FTL_RESOURCE, true);
        addTemplate(BASE_FTL_NAME, ContentType.TEXT_HTML.getMimeType(), FREEMARKER_TEMPLATE_WRITER, BASE_FTL_RESOURCE, true);
        addTemplate(NETWORK_FTL_NAME, ContentType.TEXT_HTML.getMimeType(), FREEMARKER_TEMPLATE_WRITER, NETWORK_FTL_RESOURCE, false);
    }

    private void addTemplate(String name, String contentType, String writer, String resource, Boolean includeViewResults) throws IOException {

        LOG.info("Adding template [{}] from resource [{}]", name);

        Template template = new Template();
        template.setName(name);
        template.setWriter(writer);
        template.setContentType(contentType);
        template.setResource(resource);
        template.setIncludeViewResults(includeViewResults);
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
    public void writeTemplate(TemplateDto template, Map<String, Object> context, Writer writer) throws WriterException {
        TemplateWriter templateWriter = applicationContext.getBean(template.getWriter(), TemplateWriter.class);
        templateWriter.writeTemplate(template, writer, context);
    }

    @Override
    public TemplateDto getTemplate(String name) throws IOException {
        return templateConverter.convert(templateDao.getTemplate(name));
    }

    @Override
    public TemplateDto getBasicTemplate() {
        TemplateDto template = new TemplateDto();
        template.setContentType(ContentType.TEXT_HTML.getMimeType());
        template.setName(BASE_FTL_NAME);
        template.setWriter(FREEMARKER_TEMPLATE_WRITER);
        template.setResource(BASE_FTL_RESOURCE);
        template.setIncludeViewResults(true);
        return template;
    }

    @Override
    public Map<String, TemplateWriter> getTemplateWriters() {
        return applicationContext.getBeansOfType(TemplateWriter.class);
    }

}
