package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.domain.Project;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class ProjectController {
    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("stateResolverLogic")
    private StateResolverLogic stateResolverLogic;

    @RequestMapping(value = "/project/index", method = RequestMethod.GET)
    public ModelAndView list() {
        final ModelAndView modelAndView = new ModelAndView("list-project-manager");
        modelAndView.addObject("projects", projectLogic.getProjects());
        return modelAndView;
    }

    @RequestMapping(value = "/project/create", method = RequestMethod.GET)
    public ModelAndView getCreate() {
        final ModelAndView modelAndView = new ModelAndView("create-update-project-manager");
        modelAndView.addObject("project", new Project());
        modelAndView.addObject("stateResolverNames", stateResolverLogic.getStateResolverNames());
        return modelAndView;
    }

    @RequestMapping(value = "/project/{projectId}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("projectId") final Long projectId) {
        final ModelAndView modelAndView = new ModelAndView("create-update-project-manager");
        modelAndView.addObject("project", projectLogic.getProject(projectId));
        modelAndView.addObject("stateResolverNames", stateResolverLogic.getStateResolverNames());
        return modelAndView;
    }

    @RequestMapping(value = "/project/{projectId}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("projectId") final Long projectId) {
        final ModelAndView modelAndView = new ModelAndView("delete-project-manager");
        modelAndView.addObject("project", projectLogic.getProject(projectId));
        return modelAndView;
    }

    @RequestMapping(value = "/project/{projectId}/delete", method = RequestMethod.POST)
    public ModelAndView handleDelete(@Valid @ModelAttribute("project") final ProjectDto project, final BindingResult bindingResult) {
        projectLogic.delete(project.getId());
        return new ModelAndView("redirect:/filter/index.html");
    }

    @RequestMapping(value = "/project/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("project") final ProjectDto project, final BindingResult bindingResult) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-project-manager");
        } else {
            project.setId(Long.valueOf(0).equals(project.getId()) ? null : project.getId());
            projectLogic.createOrUpdateProject(project);
            modelAndView = new ModelAndView("redirect:/filter/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/project/{projectId}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("project") final ProjectDto project, final BindingResult bindingResult) {
        return handleFormCreate(project, bindingResult);
    }
}
