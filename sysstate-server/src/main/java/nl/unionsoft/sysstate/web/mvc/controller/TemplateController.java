package nl.unionsoft.sysstate.web.mvc.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;
import nl.unionsoft.sysstate.template.WriterException;
import nl.unionsoft.sysstate.web.lov.TemplateWriterLovResolver;

@Controller()
public class TemplateController {

    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    @Inject
    @Named("messageLogic")
    private MessageLogic messageLogic;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("viewLogic")
    private ViewLogic viewLogic;
    
    @Inject
    private TemplateWriterLovResolver templateWriterLovResolver;
    


    @RequestMapping(value = "/template/index", method = RequestMethod.GET)
    public ModelAndView list() {
        final ModelAndView modelAndView = new ModelAndView("list-template-manager");
        modelAndView.addObject("templates", templateLogic.getTemplates());
        return modelAndView;
    }

    @RequestMapping(value = "/template/render/{name:.*}", method = RequestMethod.GET)
    public void renderTemplate(@PathVariable("name") final String name, HttpServletRequest request, HttpServletResponse response) {
        try {
            ViewDto view = viewLogic.getBasicView();
            TemplateDto template = templateLogic.getTemplate(name);
            response.addHeader("Content-Type", template.getContentType());
            Map<String, Object> context = new HashMap<String, Object>();
            if (template.getIncludeViewResults()){
                ViewResultDto viewResult =  viewLogic.getViewResults(view);
                context.put("viewResult",viewResult);
            }
            context.put("view", view);
            context.put("contextPath", request.getContextPath());
            templateLogic.writeTemplate(template, context, response.getWriter());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (WriterException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    @RequestMapping(value = "/template/create", method = RequestMethod.GET)
    public ModelAndView getCreate() {
        final ModelAndView modelAndView = new ModelAndView("create-update-template-manager");
        modelAndView.addObject("template", new TemplateDto());
        addModelObjects(modelAndView);
        return modelAndView;
    }

    private void addModelObjects(final ModelAndView modelAndView) {
        modelAndView.addObject("templateWriters", templateWriterLovResolver.getListOfValues(null));
    }

    @RequestMapping(value = "/template/{name}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("name") final String name) throws IOException {
        final ModelAndView modelAndView = new ModelAndView("create-update-template-manager");
        modelAndView.addObject("template", templateLogic.getTemplate(name));
        addModelObjects(modelAndView);
        messageLogic.addUserMessage(new MessageDto("Template updates succesfully", MessageDto.GREEN));
        return modelAndView;
    }

    @RequestMapping(value = "/template/{name}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("name") final String name) throws IOException {
        final ModelAndView modelAndView = new ModelAndView("delete-template-manager");
        modelAndView.addObject("template", templateLogic.getTemplate(name));
        return modelAndView;

    }

    @RequestMapping(value = "/template/{name}/delete", method = RequestMethod.POST)
    public ModelAndView postDelete(@PathVariable("name") final String name) {
        templateLogic.delete(name);
        return new ModelAndView("redirect:/template/index.html");
    }


    @RequestMapping(value = "/template/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("template") final TemplateDto template, final BindingResult bindingResult) throws IOException {
        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-template-manager");
            addModelObjects(modelAndView);
        } else {
            templateLogic.createOrUpdate(template);
            modelAndView = new ModelAndView("redirect:/template/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/template/{name}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("template") final TemplateDto template, final BindingResult bindingResult) throws IOException {
        return handleFormCreate(template, bindingResult);
    }
}
