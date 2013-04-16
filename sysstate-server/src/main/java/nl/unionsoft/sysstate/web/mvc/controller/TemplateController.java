package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class TemplateController {
    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    @Inject
    @Named("messageLogic")
    private MessageLogic messageLogic;

    // .shtml not required
    @RequestMapping(value = "/template/index", method = RequestMethod.GET)
    public ModelAndView list() {
        final ModelAndView modelAndView = new ModelAndView("list-template-manager");
        modelAndView.addObject("templates", templateLogic.getTemplates());
        return modelAndView;
    }

    @RequestMapping(value = "/template/create", method = RequestMethod.GET)
    public ModelAndView getCreate() {
        final ModelAndView modelAndView = new ModelAndView("create-update-template-manager");
        modelAndView.addObject("template", new Template());
        return modelAndView;
    }

    @RequestMapping(value = "/template/{templateId}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("templateId") final String templateId) {
        final ModelAndView modelAndView = new ModelAndView("create-update-template-manager");
        modelAndView.addObject("template", templateLogic.getTemplate(templateId));
        messageLogic.addUserMessage(new MessageDto("Template updates succesfully", MessageDto.GREEN));
        return modelAndView;
    }

    @RequestMapping(value = "/template/{templateId}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("templateId") final String templateId) {
        final ModelAndView modelAndView = new ModelAndView("delete-template-manager");
        modelAndView.addObject("template", templateLogic.getTemplate(templateId));
        return modelAndView;
    }

    @RequestMapping(value = "/template/{templateId}/delete", method = RequestMethod.POST)
    public ModelAndView postDelete(@PathVariable("templateId") final String templateId) {
        templateLogic.delete(templateId);
        return new ModelAndView("redirect:/template/index.html");
    }

    @RequestMapping(value = "/template/{templateId}/restore", method = RequestMethod.GET)
    public ModelAndView getRestore(@PathVariable("templateId") final String templateId) {
        final ModelAndView modelAndView = new ModelAndView("restore-template-manager");
        modelAndView.addObject("template", templateLogic.getTemplate(templateId));
        return modelAndView;
    }

    @RequestMapping(value = "/template/{templateId}/restore", method = RequestMethod.POST)
    public ModelAndView postRestore(@PathVariable("templateId") final String templateId) {
        try {
            templateLogic.restore(templateId);
            messageLogic.addUserMessage(new MessageDto("Template restored succesfully.", MessageDto.GREEN));
        } catch(final RuntimeException e) {
            messageLogic.addUserMessage(new MessageDto("Unable to restore template.", MessageDto.RED));
        }
        return new ModelAndView("redirect:/template/index.html");
    }

    @RequestMapping(value = "/template/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("template") final Template template, final BindingResult bindingResult) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-template-manager");
        } else {
            templateLogic.createOrUpdate(template);
            modelAndView = new ModelAndView("redirect:/template/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/template/{templateId}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("template") final Template template, final BindingResult bindingResult) {
        return handleFormCreate(template, bindingResult);
    }
}
