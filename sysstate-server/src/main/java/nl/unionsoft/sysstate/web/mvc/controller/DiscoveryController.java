package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.logic.DiscoveryLogic;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.web.controller.form.InstanceListSelector;
import nl.unionsoft.sysstate.web.controller.form.InstanceMultiForm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class DiscoveryController {

    @Inject
    @Named("discoveryLogic")
    private DiscoveryLogic discoveryLogic;

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("messageLogic")
    private MessageLogic messageLogic;

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @RequestMapping(value = "/discovery/index", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("discovery-manager");
        commons(modelAndView);
        // modelAndView.addObject("discoveryPlugins", pluginLogic.getPluginInstances(DiscoveryPlugin.class));

        return modelAndView;
    }

    @RequestMapping(value = "/discovery/index", method = RequestMethod.POST)
    public ModelAndView discover(@RequestParam("plugin") final String plugin, @RequestParam("properties") final String properties) {
        discoveryLogic.discover(plugin, PropertiesUtil.stringToProperties(properties));
        return new ModelAndView("redirect:/discovery/index.html");
    }

    private void commons(final ModelAndView modelAndView) {
        modelAndView.addObject("environments", environmentLogic.getEnvironments());
        modelAndView.addObject("projects", projectLogic.getProjects());
        // modelAndView.addObject("stateResolverNames", pluginLogic.getPluginClasses(StateResolverPlugin.class));
        final InstanceMultiForm multiForm = new InstanceMultiForm();
        for (final InstanceDto instanceDto : discoveryLogic.getDiscoveredInstances()) {
            final InstanceListSelector listSelector = new InstanceListSelector();
            listSelector.setInstance(instanceDto);
            multiForm.addItem(listSelector);
        }
        modelAndView.addObject("multiForm", multiForm);
        // modelAndView.addObject("discoveryPlugins", pluginLogic.getPluginInstances(DiscoveryPlugin.class));
    }

    @RequestMapping(value = "/discovery/add", method = RequestMethod.POST)
    public ModelAndView add(@ModelAttribute("instanceForm") final InstanceMultiForm multiForm) {
        for (final InstanceListSelector instanceListSelector : multiForm.getInstanceListSelectors()) {
            if (instanceListSelector.isSelected()) {
                final InstanceDto instance = instanceListSelector.getInstance();
                if (instance.getPluginClass() != null) {
                    final ProjectEnvironmentDto projectEnvironment = instance.getProjectEnvironment();
                    if (projectEnvironment != null) {
                        final ProjectDto project = projectEnvironment.getProject();
                        final EnvironmentDto environment = projectEnvironment.getEnvironment();
                        if (project != null && project.getId() != null && environment != null && environment.getId() != null) {
                            projectEnvironment.setId(projectEnvironmentLogic.createIfNotExists(project.getId(), environment.getId()));
                            instance.setEnabled(true);
                            instanceLogic.createOrUpdateInstance(instance);
                            messageLogic.addUserMessage(new MessageDto("Added instance with name " + instance.getName() + ".", MessageDto.GREEN));
                        }
                    }
                }
            }
        }

        return new ModelAndView("redirect:/discovery/index.html");
    }
}
