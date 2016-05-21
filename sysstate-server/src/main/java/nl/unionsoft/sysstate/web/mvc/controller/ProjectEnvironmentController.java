package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.enums.FilterBehaviour;
import nl.unionsoft.sysstate.common.logic.InstanceStateLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;

@Controller()
public class ProjectEnvironmentController {

    @Inject
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @Inject
    private InstanceStateLogic instanceStateLogic;

    @RequestMapping(value = "/projectEnvironment/project/{projectId}/environment/{environmentId}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("projectId") final Long projectId, @PathVariable("environmentId") final Long environmentId) {
        final ModelAndView modelAndView = new ModelAndView("update-project-environment-manager");
        modelAndView.addObject("projectEnvironment", projectEnvironmentLogic.getProjectEnvironment(projectId, environmentId));
        return modelAndView;
    }

    @RequestMapping(value = "/projectEnvironment/project/{projectId}/environment/{environmentId}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("projectEnvironment") final ProjectEnvironmentDto projectEnvironment,
            final BindingResult bindingResult) {
        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("update-project-environment-manager");
        } else {
            projectEnvironment.setId(Long.valueOf(0).equals(projectEnvironment.getId()) ? null : projectEnvironment.getId());
            projectEnvironmentLogic.createOrUpdate(projectEnvironment);
            modelAndView = new ModelAndView("redirect:/filter/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/projectEnvironment/project/{projectId}/environment/{environmentId}/details", method = RequestMethod.GET)
    public ModelAndView details(@PathVariable("projectId") final Long projectId, @PathVariable("environmentId") final Long environmentId) {
        final ModelAndView modelAndView = new ModelAndView("details-project-environment-manager");
        modelAndView.addObject("projectEnvironment", projectEnvironmentLogic.getProjectEnvironment(projectId, environmentId));
        final FilterDto filter = new FilterDto();
        filter.getEnvironments().add(environmentId);
        filter.getProjects().add(projectId);
        modelAndView.addObject("instanceStates", instanceStateLogic.getInstanceStates(filter,FilterBehaviour.DIRECT));
        return modelAndView;
    }

}
