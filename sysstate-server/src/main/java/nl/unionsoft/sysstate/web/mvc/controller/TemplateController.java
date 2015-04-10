package nl.unionsoft.sysstate.web.mvc.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.template.WriterException;

import org.springframework.http.HttpHeaders;
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
    
    @RequestMapping(value = "/template/render/{name:.*}", method = RequestMethod.GET)
    public void renderTemplate(@PathVariable("name") final String name, HttpServletResponse response) {
        try {
            TemplateDto template = templateLogic.getTemplate(name);
            response.addHeader("Content-Type", template.getContentType());
            templateLogic.writeTemplate(template, new HashMap<String, Object>(), response.getWriter());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @RequestMapping(value = "/template/create", method = RequestMethod.GET)
    public ModelAndView getCreate() {
        final ModelAndView modelAndView = new ModelAndView("create-update-template-manager");
        modelAndView.addObject("template", new TemplateDto());
        return modelAndView;
    }

    @RequestMapping(value = "/template/{name}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("name") final String name) {
        final ModelAndView modelAndView = new ModelAndView("create-update-template-manager");
        modelAndView.addObject("template", templateLogic.getTemplate(name));
        messageLogic.addUserMessage(new MessageDto("Template updates succesfully", MessageDto.GREEN));
        return modelAndView;
    }

    @RequestMapping(value = "/template/{name}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("name") final String name) {
        final ModelAndView modelAndView = new ModelAndView("delete-template-manager");
        modelAndView.addObject("template", templateLogic.getTemplate(name));
        return modelAndView;

    }

    @RequestMapping(value = "/template/{name}/delete", method = RequestMethod.POST)
    public ModelAndView postDelete(@PathVariable("name") final String name) {
        templateLogic.delete(name);
        return new ModelAndView("redirect:/template/index.html");
    }

    @RequestMapping(value = "/template/{name}/restore", method = RequestMethod.GET)
    public ModelAndView getRestore(@PathVariable("name") final String name) {
        final ModelAndView modelAndView = new ModelAndView("restore-template-manager");
        modelAndView.addObject("template", templateLogic.getTemplate(name));
        return modelAndView;
    }

    @RequestMapping(value = "/template/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("template") final TemplateDto template, final BindingResult bindingResult) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-template-manager");
        } else {
            templateLogic.createOrUpdate(template);
            modelAndView = new ModelAndView("redirect:/template/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/template/{name}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("template") final TemplateDto template, final BindingResult bindingResult) {
        return handleFormCreate(template, bindingResult);
    }
}
