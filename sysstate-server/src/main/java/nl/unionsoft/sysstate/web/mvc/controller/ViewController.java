package nl.unionsoft.sysstate.web.mvc.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import nl.unionsoft.sysstate.Constants;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.logic.EcoSystemLogic;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;
import nl.unionsoft.sysstate.template.WriterException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class ViewController {

    @Inject
    @Named("viewLogic")
    private ViewLogic viewLogic;

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("filterLogic")
    private FilterLogic filterLogic;

    @Inject
    @Named("templateLogic")
    private TemplateLogic templateLogic;

    @Inject
    @Named("ecoSystemLogic")
    private EcoSystemLogic ecoSystemLogic;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public void renderIndex(HttpServletResponse response, HttpServletRequest request) {

        Properties viewConfiguration = pluginLogic.getPluginProperties(Constants.SYSSTATE_PLUGIN_NAME);

        String defaultViewProperty = viewConfiguration.getProperty("defaultView");
        ViewDto view = viewLogic.getBasicView();
        if (StringUtils.isNotEmpty(defaultViewProperty)) {
            view = viewLogic.getView(Long.valueOf(defaultViewProperty));
        }
        writeTemplateForView(response, request, view);
    }

    @RequestMapping(value = "/view/{viewId}/index.html", method = RequestMethod.GET)
    public void renderIndexView(@PathVariable("viewId") Long viewId, HttpServletRequest request, HttpServletResponse response) {
        final ViewDto view = viewLogic.getView(viewId);
        writeTemplateForView(response, request, view);

    }

    private void writeTemplateForView(HttpServletResponse response, HttpServletRequest request, final ViewDto view) {
        TemplateDto template = view.getTemplate();
        try {

            response.addHeader("Content-Type", template.getContentType());
            Map<String, Object> context = new HashMap<String, Object>();
            if (template.getIncludeViewResults()){
                context.put("viewResult", ecoSystemLogic.getEcoSystem(view));    
            }
            context.put("request", request);
            context.put("view", view);
            templateLogic.writeTemplate(template, context, response.getWriter());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/view/index", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("list-view-manager");
        modelAndView.addObject("views", viewLogic.getViews());
        return modelAndView;
    }

    @RequestMapping(value = "/view/create", method = RequestMethod.GET)
    public ModelAndView getCreate() {
        final ModelAndView modelAndView = new ModelAndView("create-update-view-manager");
        final ViewDto view = new ViewDto();
        view.setFilter(new FilterDto());
        modelAndView.addObject("view", view);
        addCommons(modelAndView);
        return modelAndView;
    }

    private void addCommons(final ModelAndView modelAndView) {
        modelAndView.addObject("templates", templateLogic.getTemplates());
        modelAndView.addObject("filters", filterLogic.getFilters());
    }

    @RequestMapping(value = "/view/{viewId}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("viewId") final Long viewId) {
        final ModelAndView modelAndView = new ModelAndView("create-update-view-manager");
        modelAndView.addObject("view", viewLogic.getView(viewId));
        addCommons(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/view/{viewId}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("viewId") final Long viewId) {
        final ModelAndView modelAndView = new ModelAndView("delete-view-manager");
        modelAndView.addObject("view", viewLogic.getView(viewId));
        return modelAndView;
    }

    @RequestMapping(value = "/view/{viewId}/delete", method = RequestMethod.POST)
    public ModelAndView handleDelete(@Valid @ModelAttribute("view") final ViewDto view, final BindingResult bindingResult) {
        viewLogic.delete(view.getId());
        return new ModelAndView("redirect:/view/index.html");
    }

    @RequestMapping(value = "/view/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("view") final ViewDto view, final BindingResult bindingResult) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-view-manager");
            addCommons(modelAndView);
        } else {
            view.setId(Long.valueOf(0).equals(view.getId()) ? null : view.getId());
            viewLogic.createOrUpdateView(view);
            modelAndView = new ModelAndView("redirect:/view/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/view/{viewId}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("view") final ViewDto view, final BindingResult bindingResult) {
        return handleFormCreate(view, bindingResult);
    }

    @RequestMapping(value = "/view/{viewId}/details", method = RequestMethod.GET)
    public ModelAndView dashboard(@PathVariable("viewId") Long viewId) {
        final ModelAndView modelAndView = new ModelAndView("details-view-manager");
        final ViewDto view = viewLogic.getView(viewId);
        modelAndView.addObject("viewResults", ecoSystemLogic.getEcoSystem(view));
        return modelAndView;

    }

}
