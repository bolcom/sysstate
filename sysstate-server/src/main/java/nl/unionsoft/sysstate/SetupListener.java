package nl.unionsoft.sysstate;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;

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

    public void afterPropertiesSet() throws Exception {

        boolean hasNoProjects = projectLogic.getProjects().isEmpty();
        boolean hasNoEnvironments = environmentLogic.getEnvironments().isEmpty();
        boolean hasNoInstances = instanceLogic.getInstances().isEmpty();

        boolean initialSetup = hasNoProjects && hasNoEnvironments && hasNoInstances;

        if (initialSetup) {

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
            addTestInstance("google", "GOOG", "PROD", createHttpConfiguration("http://www.google.nl"), "http://www.google.nl", "httpStateResolver");
            addTestInstance("google", "GOOG", "MOCK", createMockConfiguration(60000), "http://www.yahoo.com", "mockStateResolver");
            addTestInstance("yahoo", "YAHO", "PROD", createHttpConfiguration("http://www.yahoo.com"), "http://www.yahoo.com", "httpStateResolver");
            addTestInstance("yahoo", "YAHO", "MOCK", createMockConfiguration(30000), "http://www.yahoo.com", "mockStateResolver");
            addTestInstance("bing", "BING", "PROD", createHttpConfiguration("http://www.bing.com"), "http://www.bing.com", "httpStateResolver");
            addTestInstance("bing", "BING", "MOCK", createMockConfiguration(15000), "http://www.bing.com", "mockStateResolver");
            addTestInstance("ilse", "ILSE", "PROD", createHttpConfiguration("http://www.ilse.nl"), "http://www.ilse.nl", "httpStateResolver");
            addTestInstance("ilse", "ILSE", "MOCK", createMockConfiguration(7500), "http://www.ilse.nl", "mockStateResolver");
            //addTestInstance("marathon", "ILSE", "MOCK", createMarathonPatternResolverConfiguration("http://path.to.marathon"), "http://path.to.marathon", "marathonPatternInstanceResolver");    
            

            if (filterLogic.getFilters().isEmpty()) {
                LOG.info("No filters found, creating a default filter...");
                FilterDto filterDto = new FilterDto();
                filterDto.getEnvironments().add(prd.getId());
                filterDto.setName("Production");
                filterLogic.createOrUpdate(filterDto);

            }
            if (viewLogic.getViews().isEmpty()) {
                createView("complete card", "card.html");
            }
        }
    }

    private Map<String, String> createMockConfiguration(int sleep) {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("sleep", String.valueOf(sleep));
        return configuration;
    }
    
    private Map<String, String> createMarathonPatternResolverConfiguration(String serverUrl){
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("serverUrl", serverUrl);
        return configuration;
    }

    private Map<String, String> createHttpConfiguration(final String url) {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("url", url);
        return configuration;
    }

    private void createView(String name, String template) {
        ViewDto view = new ViewDto();
        view.setName(name);
        view.setTemplate(templateLogic.getTemplate(template));
        viewLogic.createOrUpdateView(view);
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
