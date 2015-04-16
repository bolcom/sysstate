package nl.unionsoft.sysstate.logic.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.SetupListener;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.converter.TemplateConverter;
import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.template.TemplateWriter;
import nl.unionsoft.sysstate.template.WriterException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("templateLogic")
public class TemplateLogicImpl implements TemplateLogic {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateLogicImpl.class);

    private ApplicationContext applicationContext;

    private TemplateDao templateDao;

    private TemplateConverter templateConverter;

    private Path templateHome;

    public static final String RESOURCE_BASE = "/nl/unionsoft/sysstate/templates/";
    public static final String STRING_TEMPLATE_WRITER = "stringTemplateWriter";
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
        template.setName(dto.getName());
        template.setWriter(dto.getWriter());
        template.setContentType(dto.getContentType());
        setContent(dto.getName(), dto.getContent());
        templateDao.createOrUpdate(template);
    }

    private String getContent(String name) throws IOException {
        Path template = templateHome.resolve(name);
        return new String(Files.readAllBytes(template));
    }

    private void setContent(String name, String content) throws IOException {
        Path template = templateHome.resolve(name);
        Files.write(template, content.getBytes(), StandardOpenOption.CREATE);
    }

    @PostConstruct
    public void addTemplatesIfNoneExist() throws IOException {
        LOG.info("Creating templates directory...");
        Files.createDirectories(templateHome);

        LOG.info("No templates found, creating some default templates...");
        addFile("base.css", RESOURCE_BASE + "string/base.css");
        addFile("ci.css", RESOURCE_BASE + "string/ci.css");

        addTemplate("ci.html", "text/html", FREEMARKER_TEMPLATE_WRITER, null);
        addFile("ci.html", RESOURCE_BASE + "freemarker/ci.ftl");
        addTemplate("base.html", "text/html", FREEMARKER_TEMPLATE_WRITER, null);
        addFile("base.html", RESOURCE_BASE + "freemarker/base.ftl");

        addFile("fragments.meta-refresh.ftl", RESOURCE_BASE + "freemarker/meta-refresh.ftl");
        addFile("fragments.table.ftl", RESOURCE_BASE + "freemarker/table.ftl");
    }

    private void addTemplate(String name, String contentType, String writer, String configuration) throws IOException {

        LOG.info("Adding template [{}] from resource [{}]", name);

        Template template = new Template();
        template.setName(name);

        template.setWriter(writer);
        template.setContentType(contentType);

        templateDao.createOrUpdate(template);

    }

    private void addFile(String name, String resource) throws IOException {
        LOG.info("Adding file [{}] from resource [{}]", name, resource);

        try (InputStream is = getClass().getResourceAsStream(resource)) {

            if (!Files.exists(templateHome.resolve(name))) {
                setContent(name, IOUtils.toString(is));
            }

        }
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
        TemplateDto template = templateConverter.convert(templateDao.getTemplate(name));
        template.setContent(getContent(name));
        return template;
    }

}
