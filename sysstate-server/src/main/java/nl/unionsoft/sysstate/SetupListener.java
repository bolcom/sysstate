package nl.unionsoft.sysstate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.common.logic.TemplateLogic;
import nl.unionsoft.sysstate.common.logic.TextLogic;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;

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
    @Named("textLogic")
    private TextLogic textLogic;

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

            LOG.info("Adding default texts...");
            addText("go-selfdiagnose 1.0.2", "xpath xPathStateResolver", "substring-before(substring-after(string(/selfdiagnose/results/result[@task='build information']/@message),': '),',')");
            addText("selfdiagnose-version 1.0", "xpath xPathStateResolver", "normalize-space(string(/selfdiagnose/@version))");

            LOG.info("No instances found, creating some default instances...");
//            addTestInstance("google", "GOOG", "PROD", createHttpConfiguration("http://www.google.nl"), "http://www.google.nl", "httpStateResolver");
//            addTestInstance("google", "GOOG", "MOCK", createMockConfiguration(18000, StateType.STABLE.name()), "http://www.yahoo.com", "mockStateResolver");
//            addTestInstance("yahoo", "YAHO", "PROD", createHttpConfiguration("http://www.yahoo.com"), "http://www.yahoo.com", "httpStateResolver");
//            addTestInstance("yahoo", "YAHO", "MOCK", createMockConfiguration(12000, StateType.UNSTABLE.name()), "http://www.yahoo.com", "mockStateResolver");
            addTestInstance("bing", "BING", "PROD", createHttpConfiguration("http://www.bing.com"), "http://www.bing.com", "httpStateResolver");
            addTestInstance("bing", "BING", "MOCK", createMockConfiguration(6000, StateType.ERROR.name()), "http://www.bing.com", "mockStateResolver");
            addTestInstance("ilse", "ILSE", "PROD", createHttpConfiguration("http://www.ilse.nl"), "http://www.ilse.nl", "httpStateResolver");
            addTestInstance("ilse", "ILSE", "MOCK", createMockConfiguration(3000, StateType.DISABLED.name()), "http://www.ilse.nl", "mockStateResolver");

            if (filterLogic.getFilters().isEmpty()) {
                LOG.info("No filters found, creating a default filter...");
                FilterDto filterDto = new FilterDto();
                filterDto.setName("All");
                filterLogic.createOrUpdate(filterDto);
                if (viewLogic.getViews().isEmpty()) {
                    createView("Network", "network.html", filterDto);
                    createView("Card", "card.html", filterDto);
                    createView("Table", "base.html", filterDto);
                }
            }
        }
    }

    private Map<String, String> createMockConfiguration(int sleep, String state) {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("sleep", String.valueOf(sleep));
        configuration.put("state", state);
        return configuration;
    }

    private Map<String, String> createHttpConfiguration(final String url) {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("url", url);
        return configuration;
    }

    private void createView(String name, String template, FilterDto filter) {
        ViewDto view = new ViewDto();
        view.setName(name);
        Optional<TemplateDto> optTemplate = templateLogic.getTemplate(template);
        if (!optTemplate.isPresent()) {
            throw new IllegalStateException("Template does not exist");
        }
        view.setTemplate(optTemplate.get());
        view.setFilter(filter);
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

    private void addText(String name, String tags, String text) {
        TextDto textDto = new TextDto();
        textDto.setName(name);
        textDto.setTags(tags);
        textDto.setText(text);
        textLogic.createOrUpdateText(textDto);
    }

}
