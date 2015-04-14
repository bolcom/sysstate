package nl.unionsoft.sysstate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("setupListener")
public class SetupListener implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(SetupListener.class);

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("filterLogic")
    private FilterLogic filterLogic;

    @Inject
    @Named("viewLogic")
    private ViewLogic viewLogic;
    
    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    public static final String RESOURCE_BASE = "/nl/unionsoft/sysstate/templates/";
    public static final String STRING_TEMPLATE_WRITER = "stringTemplateWriter";
    public static final String FREEMARKER_TEMPLATE_WRITER = "freeMarkerTemplateWriter";
    
    public void afterPropertiesSet() throws Exception {

        boolean hasNoProjects = projectLogic.getProjects().isEmpty();
        boolean hasNoEnvironments = environmentLogic.getEnvironments().isEmpty();
        boolean hasNoInstances = instanceLogic.getInstances().isEmpty();
        boolean hasNoTemplates = templateLogic.getTemplates().isEmpty();
        
        
        boolean initialSetup = hasNoProjects && hasNoEnvironments && hasNoInstances && hasNoTemplates;

        if (initialSetup) {
            
            LOG.info("No templates found, creating some default templates...");
            addTemplate("base.css",RESOURCE_BASE + "string/base.css","text/css", STRING_TEMPLATE_WRITER, null);
            addTemplate("ci.css",RESOURCE_BASE + "string/ci.css","text/css", STRING_TEMPLATE_WRITER,null);

            addTemplate("ci.html",RESOURCE_BASE + "freemarker/ci.ftl","text/html", FREEMARKER_TEMPLATE_WRITER, null);
            addTemplate("base.html",RESOURCE_BASE + "freemarker/base.ftl","text/html", FREEMARKER_TEMPLATE_WRITER, null);
            
            addTemplate("fragments.meta-refresh.ftl",RESOURCE_BASE + "freemarker/meta-refresh.ftl","text/html", FREEMARKER_TEMPLATE_WRITER, null);
            addTemplate("fragments.table.ftl",RESOURCE_BASE + "freemarker/table.ftl","text/html", FREEMARKER_TEMPLATE_WRITER, null);
            
            LOG.info("No projects found, creating some default projects...");
            // No projects defined..
            createProject("GOOG");
            createProject("YAHO");
            createProject("BING");
            createProject("ILSE");

            // No environments defined..
            LOG.info("No environments found, creating some default environments...");
            EnvironmentDto prd = new EnvironmentDto();
            prd.setName("PROD");
            prd.setOrder(10);
            environmentLogic.createOrUpdate(prd);

            EnvironmentDto mock = new EnvironmentDto();
            mock.setName("MOCK");
            mock.setOrder(0);
            environmentLogic.createOrUpdate(mock);

            LOG.info("No instances found, creating some default instances...");
//            addTestInstance("google", "GOOG", "PROD", createHttpConfiguration("http://www.google.nl"), "http://www.google.nl", "httpStateResolver");
//            addTestInstance("google", "GOOG", "MOCK", null, "http://www.yahoo.com", "mockStateResolver");
//            addTestInstance("yahoo", "YAHO", "PROD", createHttpConfiguration("http://www.yahoo.com"), "http://www.yahoo.com", "httpStateResolver");
//            addTestInstance("yahoo", "YAHO", "MOCK", null, "http://www.yahoo.com", "mockStateResolver");
//            addTestInstance("bing", "BING", "PROD", createHttpConfiguration("http://www.bing.com"), "http://www.bing.com", "httpStateResolver");
//            addTestInstance("bing", "BING", "MOCK", null, "http://www.bing.com", "mockStateResolver");
//            addTestInstance("ilse", "ILSE", "PROD", createHttpConfiguration("http://www.ilse.nl"), "http://www.ilse.nl", "httpStateResolver");
//            addTestInstance("ilse", "ILSE", "MOCK", null, "http://www.ilse.nl", "mockStateResolver");

            if (filterLogic.getFilters().isEmpty()) {
                LOG.info("No filters found, creating a default filter...");
                FilterDto filterDto = new FilterDto();
                filterDto.getEnvironments().add(prd.getId());
                filterDto.setName("Production");
                filterLogic.createOrUpdate(filterDto);

                if (viewLogic.getViews().isEmpty()) {
                    LOG.info("No views found, creating a default view...");
                    ViewDto viewDto = new ViewDto();
                    viewDto.setFilter(filterDto);
                    viewDto.setName("Production CI View");
                    viewDto.setTemplate(templateLogic.getTemplate("ci.html"));
                    viewLogic.createOrUpdateView(viewDto);
                }
            }
        }
    }

    
    
    private Map<String, String> createHttpConfiguration(final String url) {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("url", url);
        return configuration;
    }

    private TemplateDto addTemplate(String name, String resource, String contentType, String writer, String configuration){
        LOG.info("Adding template [{}] from resource [{}]", name, resource);
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(resource);
            TemplateDto template = new TemplateDto();
            template.setName(name);
            template.setContent(IOUtils.toString(is));
            template.setWriter(writer);
            template.setContentType(contentType);
            templateLogic.createOrUpdate(template);
            return template;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to addTemplate for resource " + resource + ", caught IOException", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
    
    private void addTestInstance(final String name, final String projectName, final String environmentName, final Map<String, String> configuration,
            final String homepageUrl, final String plugin) {
        ProjectEnvironmentDto projectEnvironment = projectEnvironmentLogic.getProjectEnvironment(projectName, environmentName);
        if (projectEnvironment == null) {
            LOG.info("Skipping creating of instance ${}, no projectEnvironment could be found for projectName '{}' and environmentName '{}'", new Object[] {
                    name, projectName, environmentName });
        } else {
            InstanceDto instance = new InstanceDto();
            instance.setName(name);
            instance.setProjectEnvironment(projectEnvironment);
            instance.setEnabled(true);
            instance.setConfiguration(configuration);
            instance.setHomepageUrl(homepageUrl);
            instance.setPluginClass(plugin);
            instance.setRefreshTimeout(10000);
            instance.setTags("application");
            instanceLogic.createOrUpdateInstance(instance);
        }
    }

    private void createProject(final String name) {
        ProjectDto project = new ProjectDto();
        project.setName(name);
        projectLogic.createOrUpdateProject(project);
    }

}
