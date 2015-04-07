package nl.unionsoft.sysstate.logic.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.logic.TemplateLogic;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("templateLogic")
public class TemplateLogicImpl implements TemplateLogic, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(TemplateLogicImpl.class);

    @Inject
    @Named("templateDao")
    private TemplateDao templateDao;

    public Template getTemplate(final String templateId) {
        Template result = null;
        if (StringUtils.isNotEmpty(templateId)) {
            result = templateDao.getTemplate(templateId);
        }

        if (result == null) {
            result = SystemTemplate.BASE.asTemplate();
        }
        return result;
    }

    public void createOrUpdate(final Template template) {
        templateDao.createOrUpdate(template);
    }

    public Collection<Template> getTemplates() {

        final Map<String, Template> templates = new HashMap<String, Template>();
        for (final SystemTemplate systemTemplate : SystemTemplate.values()) {
            final Template template = systemTemplate.asTemplate();
            templates.put(template.getId(), template);
        }

        for (final Template template : templateDao.getTemplates()) {
            templates.put(template.getId(), template);
        }
        return templates.values();
    }

    public enum SystemTemplate {
        //@formatter:off
        BASE("base", "nl/unionsoft/sysstate/css/base.css", "table-overview", 30, null),
        CI("ci", "nl/unionsoft/sysstate/css/ci.css", "table-overview", 15,"no_project_col=true\nno_weather=true\nno_popup=true\n"), 
        PRIORITY("priority", "nl/unionsoft/sysstate/css/priority.css", "priority-overview", 15, null),
        DRILLDOWN("drilldown", "nl/unionsoft/sysstate/css/drilldown.css", "drilldown-overview", 15, null),
        NETWORK("network", "nl/unionsoft/sysstate/css/ci.css", "network-overview", 3600, null) ;
        //@formatter:on
        public final String id;
        public final String cssFile;
        public final String layout;
        public final int refresh;
        public final String renderHints;

        private SystemTemplate (final String id, final String cssFile, final String layout, final int refresh, final String renderHints) {
            this.id = id;
            this.cssFile = cssFile;
            this.layout = layout;
            this.refresh = refresh;
            this.renderHints = renderHints;
        }

        public static SystemTemplate getTemplateForId(final String id) {
            SystemTemplate result = null;
            for (final SystemTemplate systemTemplate : values()) {
                if (StringUtils.equalsIgnoreCase(systemTemplate.id, id)) {
                    result = systemTemplate;
                }
            }
            return result;
        }

        public Template asTemplate() {
            Template template = null;
            final InputStream cssInputStream = getClass().getClassLoader().getResourceAsStream(cssFile);
            template = new Template();
            template.setId(id);
            template.setLayout(layout);
            template.setRefresh(refresh);
            template.setRenderHints(renderHints);
            template.setSystemTemplate(true);
            if (cssInputStream != null) {
                try {
                    final String css = IOUtils.toString(cssInputStream);
                    template.setCss(css);
                } catch(final FileNotFoundException e) {
                    LOG.error("Unable to get template, caught FileNotFoundException", e);
                } catch(final IOException e) {
                    LOG.error("Unable to get template, caught IOException", e);
                } finally {
                    IOUtils.closeQuietly(cssInputStream);
                }
            }
            return template;
        }

    }

    public void afterPropertiesSet() throws Exception {
        for (final SystemTemplate systemTemplate : SystemTemplate.values()) {
            final Template template = systemTemplate.asTemplate();
            if (templateDao.getTemplate(template.getId()) == null) {
                templateDao.createOrUpdate(template);
            }
        }
    }

    public void delete(String templateId) {

        final Template template = templateDao.getTemplate(templateId);
        if (template.isSystemTemplate()) {
            throw new IllegalStateException("Cannot delete a system template!");
        }

        templateDao.delete(templateId);

    }

    public void restore(String templateId) {

        final Template template = templateDao.getTemplate(templateId);
        if (!template.isSystemTemplate()) {
            throw new IllegalStateException("Cannot restore  a non system template!");
        }

        final SystemTemplate systemTemplate = SystemTemplate.getTemplateForId(templateId);
        if (systemTemplate == null) {
            // No longer exists as an systemTempalte
            template.setSystemTemplate(false);
            templateDao.createOrUpdate(template);
        } else {
            templateDao.createOrUpdate(systemTemplate.asTemplate());
        }

    }

}
