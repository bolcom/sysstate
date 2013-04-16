package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.domain.Environment;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class EnvironmentController {

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @RequestMapping(value = "/environment/index", method = RequestMethod.GET)
    public ModelAndView list() {
        final ModelAndView modelAndView = new ModelAndView("list-environment-manager");
        modelAndView.addObject("environments", environmentLogic.getEnvironments());
        return modelAndView;
    }

    @RequestMapping(value = "/environment/create", method = RequestMethod.GET)
    public ModelAndView getCreate() {
        final ModelAndView modelAndView = new ModelAndView("create-update-environment-manager"); // no
        // .jsp
        // here
        modelAndView.addObject("environment", new EnvironmentDto());
        return modelAndView;
    }

    @RequestMapping(value = "/environment/{environmentId}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("environmentId") final Long environmentId) {
        final ModelAndView modelAndView = new ModelAndView("create-update-environment-manager"); // no
        modelAndView.addObject("environment", environmentLogic.getEnvironment(environmentId));
        return modelAndView;
    }

    @RequestMapping(value = "/environment/{environmentId}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("environmentId") final Long environmentId) {
        final ModelAndView modelAndView = new ModelAndView("delete-environment-manager"); // no
        modelAndView.addObject("environment", environmentLogic.getEnvironment(environmentId));
        return modelAndView;
    }

    @RequestMapping(value = "/environment/{environmentId}/delete", method = RequestMethod.POST)
    public ModelAndView handleDelete(@Valid @ModelAttribute("environment") final EnvironmentDto environment, final BindingResult bindingResult) {
        environmentLogic.delete(environment.getId());
        return new ModelAndView("redirect:/filter/index.html");
    }

    @RequestMapping(value = "/environment/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("environment") final EnvironmentDto environment, final BindingResult bindingResult) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-environment-manager");
        } else {
            environment.setId(Long.valueOf(0).equals(environment.getId()) ? null : environment.getId());
            environmentLogic.createOrUpdate(environment);
            modelAndView = new ModelAndView("redirect:/filter/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/environment/{environmentId}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("environment") final EnvironmentDto environment, final BindingResult bindingResult) {
        return handleFormCreate(environment, bindingResult);
    }
}
