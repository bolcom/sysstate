package nl.unionsoft.sysstate.plugins.impl.discovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.extending.Discovery;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;
import nl.unionsoft.sysstate.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.queue.CrossEnvironmentWorker;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @PluginImplementation
public class CrossEnvironmentDiscoveryImpl implements Discovery {

    private static final Logger LOG = LoggerFactory.getLogger(CrossEnvironmentDiscoveryImpl.class);

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    public Collection<ReferenceRunnable> discover(final Properties properties) {

        Collection<ReferenceRunnable> results = new ArrayList<ReferenceRunnable>();
        // Collection<InstanceDto> results = new ArrayList<InstanceDto>();
        List<EnvironmentDto> environments = environmentLogic.getEnvironments();
        for (ProjectDto project : projectLogic.getProjects()) {
            LOG.info("Cross checking project {}", project.getName());
            List<EnvironmentDto> emptyEnvironments = new ArrayList<EnvironmentDto>();
            List<InstanceDto> foundInstances = new ArrayList<InstanceDto>();
            for (EnvironmentDto environment : environments) {
                FilterDto filterDto = new FilterDto();
                filterDto.getProjects().add(project.getId());
                filterDto.getEnvironments().add(environment.getId());
                ListResponse<InstanceDto> listResponse = instanceLogic.getInstances(filterDto);
                if (listResponse.getTotalRecords() == 0) {
                    LOG.info("No results found for project and environment, adding environment {} to list of empty environments...", environment.getName());
                    emptyEnvironments.add(environment);
                } else {
                    LOG.info("Project and environment has one or more instances, adding instances to list of found instances...");
                    foundInstances.addAll(listResponse.getResults());
                }
            }
            if (!emptyEnvironments.isEmpty() && !foundInstances.isEmpty()) {
                for (EnvironmentDto environment : emptyEnvironments) {
                    LOG.info("Generating possible instances for environment: {}", environment.getName());
                    for (InstanceDto foundInstance : foundInstances) {
                        EnvironmentDto foundEnvironment = foundInstance.getProjectEnvironment().getEnvironment();
                        String placeHolderConfiguration = generatePlaceHolders(foundInstance.getConfiguration(), foundEnvironment);
                        if (!StringUtils.equalsIgnoreCase(properties.getProperty("keepProtocol"), "true")) {
                            placeHolderConfiguration = StringUtils.replace(placeHolderConfiguration, "https://", "http://");
                        }

                        LOG.info("Generated placeHolderConfiguration: \n{}", placeHolderConfiguration);

                        String[] possibleNewConfigurations = generateConfigurations(placeHolderConfiguration, environment);
                        LOG.info("Possible new configurations found: {}", possibleNewConfigurations.length);

                        for (String possibleNewConfiguration : possibleNewConfigurations) {
                            LOG.info("Creating instance for configuration: \n{}", possibleNewConfiguration);

                            InstanceDto newInstance = new InstanceDto();
                            ProjectEnvironmentDto projectEnvironment = new ProjectEnvironmentDto();
                            projectEnvironment.setEnvironment(environment);
                            projectEnvironment.setProject(project);
                            newInstance.setProjectEnvironment(projectEnvironment);
                            newInstance.setEnabled(true);
                            newInstance.setName(foundInstance.getName());
                            newInstance.setPluginClass(foundInstance.getPluginClass());
                            newInstance.setTags(foundInstance.getTags());

                            newInstance.setConfiguration(possibleNewConfiguration);
                            newInstance.setRefreshTimeout(foundInstance.getRefreshTimeout());

                            CrossEnvironmentWorker crossEnvironmentWorker = new CrossEnvironmentWorker();
                            crossEnvironmentWorker.setInstance(newInstance);
                            results.add(crossEnvironmentWorker);

                        }
                    }
                }
            }

        }
        // return results;
        return results;
    }

    private String[] generateConfigurations(final String placeHolderConfiguration, final EnvironmentDto environment) {
        Set<String> replaceables = new HashSet<String>();
        replaceables.addAll(Arrays.asList(StringUtils.split(environment.getTags())));
        return replace(placeHolderConfiguration, replaceables).toArray(new String[] {});
    }

    private Set<String> replace(final String originalConfiguration, final Set<String> replaceables) {
        Set<String> results = new LinkedHashSet<String>();

        for (String replaceable : replaceables) {
            String replacedConfig = StringUtils.replaceOnce(originalConfiguration, "${replaceable}", replaceable);
            if (!StringUtils.equals(replacedConfig, originalConfiguration)) {
                if (StringUtils.contains(replacedConfig, "${replaceable}")) {
                    results.addAll(replace(replacedConfig, replaceables));
                } else {
                    results.add(replacedConfig);
                }
            }
        }
        return results;
    }

    private String generatePlaceHolders(final String text, final EnvironmentDto environment) {

        String replaceText = text;
        String[] tags = StringUtils.split(environment.getTags());
        if (tags != null) {
            for (String tag : tags) {
                replaceText = StringUtils.replace(replaceText, tag, "${replaceable}");
            }
        }
        replaceText = StringUtils.replace(replaceText, environment.getName(), "${replaceable}");
        return replaceText;
    }

    public void updatePropertiesTemplate(final Properties properties) {
        properties.put("keepProtocol", "false");
    }

}
