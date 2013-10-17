package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import nl.unionsoft.common.param.Context;
import nl.unionsoft.common.param.ParamContextLogicImpl;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic.StateResolverMeta;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class InstanceController {
    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @Inject
    @Named("stateResolverLogic")
    private StateResolverLogic stateResolverLogic;

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("paramContextLogic")
    private ParamContextLogicImpl paramContextLogic;

    @RequestMapping(value = "/instance/create", method = RequestMethod.GET)
    public ModelAndView getSelectCreate(final HttpSession session) {
        final ModelAndView modelAndView = new ModelAndView("select-create-instance-manager");
        modelAndView.addObject("stateResolverNames", stateResolverLogic.getStateResolverNames());
        addCommons(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/instance/{type}/create", method = RequestMethod.GET)
    public ModelAndView selectType(@PathVariable("type") final String type, final HttpSession session) {
        final ModelAndView modelAndView = new ModelAndView("create-update-instance-manager");

        InstanceDto instance = instanceLogic.generateInstanceDto(type);

        final FilterDto filter = FilterController.getFilter(session);
        final List<Long> environments = filter.getEnvironments();

        Long environmentId = null;
        if (!environments.isEmpty()) {
            environmentId = environments.get(0);

            final EnvironmentDto environment = environmentLogic.getEnvironment(environmentId);
            if (environment != null) {
                instance.setRefreshTimeout(environment.getDefaultInstanceTimeout());
            }

        }

        final List<Long> projects = filter.getProjects();
        Long projectId = null;
        if (!projects.isEmpty()) {
            projectId = projects.get(0);
            final ProjectDto project = projectLogic.getProject(projectId);
            if (project != null) {
                instance.setPluginClass(project.getDefaultInstancePlugin());

            }
        }

        if (projectId != null && environmentId != null) {
            final ProjectEnvironmentDto projectEnvironment = projectEnvironmentLogic.getProjectEnvironment(projectId, environmentId);
            if (projectEnvironment != null) {
                instance.setProjectEnvironment(projectEnvironment);
            }
        }

        instance.setEnabled(true);
        modelAndView.addObject("instance", instance);
        modelAndView.addObject("propertyMetas", instanceLogic.getPropertyMeta(type));
        addCommons(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/instance/{type}/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("instance") final InstanceDto instance, final BindingResult bindingResult, final HttpServletRequest httpRequest) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-instance-manager");
            modelAndView.addObject("propertyMetas", instanceLogic.getPropertyMeta(instance.getPluginClass()));
            addCommons(modelAndView);
        } else {
            instance.setId(Long.valueOf(0).equals(instance.getId()) ? null : instance.getId());
            instanceLogic.createOrUpdateInstance(instance);
            modelAndView = new ModelAndView("redirect:/filter/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/instance/{instanceId}/configuration", method = RequestMethod.GET)
    public ModelAndView configuration(@PathVariable("instanceId") final Long instanceId) {
        final ModelAndView modelAndView = new ModelAndView("message-clear");
        // final InstanceDto instance = instanceLogic.getInstance(instanceId);
        // modelAndView.addObject("message", instance.getConfiguration());
        return modelAndView;
    }

    @RequestMapping(value = "/instance/{instanceId}/details", method = RequestMethod.GET)
    public ModelAndView details(@PathVariable("instanceId") final Long instanceId) {
        final ModelAndView modelAndView = new ModelAndView("details-instance-clear");
        modelAndView.addObject("instance", instanceLogic.getInstance(instanceId, true));
        return modelAndView;
    }

    @RequestMapping(value = "/instance/{instanceId}/copy", method = RequestMethod.GET)
    public ModelAndView copy(@PathVariable("instanceId") final Long instanceId) {
        final ModelAndView modelAndView = new ModelAndView("copy-update-instance-manager");
        @SuppressWarnings("unchecked")
        final InstanceDto source = instanceLogic.getInstance(instanceId);
        source.setId(null);
        modelAndView.addObject("propertyMetas", instanceLogic.getPropertyMeta(source.getPluginClass()));
        modelAndView.addObject("instance", source);
        addCommons(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/instance/{instanceId}/copy", method = RequestMethod.POST)
    public ModelAndView handleCopy(@Valid @ModelAttribute("instance") final InstanceDto instance, final BindingResult bindingResult, final HttpServletRequest httpRequest) {
        return handleFormCreate(instance, bindingResult, httpRequest);
    }

    @RequestMapping(value = "/instance/{instanceId}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("instanceId") final Long instanceId) {
        final ModelAndView modelAndView = new ModelAndView("create-update-instance-manager");
        InstanceDto instance = instanceLogic.getInstance(instanceId);
        modelAndView.addObject("instance", instance);
        modelAndView.addObject("propertyMetas", instanceLogic.getPropertyMeta(instance.getPluginClass()));
        addCommons(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/instance/{instanceId}/refresh", method = RequestMethod.GET)
    public ModelAndView refresh(@PathVariable(value = "instanceId") final Long instanceId, @RequestParam(value = "redirUrl", required = false) final String redirUrl) {
        instanceLogic.queueForUpdate(instanceId);
        return new ModelAndView("redirect:/filter/index.html");
    }

    @RequestMapping(value = "/instance/{instanceId}/toggle/enabled", method = RequestMethod.GET)
    public ModelAndView toggleEnabled(@PathVariable(value = "instanceId") final Long instanceId, @RequestParam(value = "redirUrl", required = false) final String redirUrl) {
        final InstanceDto instance = instanceLogic.getInstance(instanceId);
        instance.setEnabled(!instance.isEnabled());
        instanceLogic.createOrUpdateInstance(instance);
        instanceLogic.queueForUpdate(instanceId);
        return new ModelAndView("redirect:/filter/index.html");
    }

    @RequestMapping(value = "/instance/{instanceId}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("instanceId") final Long instanceId) {
        final ModelAndView modelAndView = new ModelAndView("delete-instance-manager");
        modelAndView.addObject("instance", instanceLogic.getInstance(instanceId));
        addCommons(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/instance/{instanceId}/delete/confirmed", method = RequestMethod.POST)
    public ModelAndView handleDelete(@PathVariable("instanceId") final Long instanceId, @RequestParam(value = "redirUrl", required = false) final String redirUrl) {
        instanceLogic.delete(instanceId);
        return new ModelAndView("redirect:/filter/index.html");
    }

    private void addCommons(final ModelAndView modelAndView) {
        modelAndView.addObject("projectEnvironments", projectEnvironmentLogic.getProjectEnvironments(true));
        modelAndView.addObject("stateResolverNames", stateResolverLogic.getStateResolverNames());
    }

    @RequestMapping(value = "/instance/{instanceId}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("instance") final InstanceDto instance, final BindingResult bindingResult, final HttpServletRequest httpRequest) {
        return handleFormCreate(instance, bindingResult, httpRequest);
    }


}
