package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;
import nl.unionsoft.sysstate.logic.ProjectEnvironmentLogic;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class ProjectEnvironmentController {

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @RequestMapping(value = "/projectEnvironment/project/{projectId}/environment/{environmentId}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("projectId") final Long projectId, @PathVariable("environmentId") final Long environmentId) {
        final ModelAndView modelAndView = new ModelAndView("update-project-environment-manager");
        modelAndView.addObject("projectEnvironment", projectEnvironmentLogic.getProjectEnvironment(projectId, environmentId));
        return modelAndView;
    }

    @RequestMapping(value = "/projectEnvironment/project/{projectId}/environment/{environmentId}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("projectEnvironment") final ProjectEnvironment projectEnvironment, final BindingResult bindingResult) {
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
        modelAndView.addObject("instances", instanceLogic.getInstances(filter).getResults());
        return modelAndView;
    }

}
