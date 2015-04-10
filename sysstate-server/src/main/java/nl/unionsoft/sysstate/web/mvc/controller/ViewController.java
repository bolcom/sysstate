package nl.unionsoft.sysstate.web.mvc.controller;

import java.io.Writer;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.Constants;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.logic.EcoSystemLogic;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    public void index(@RequestParam(value = "templateId", required = false) final String templateId) {

//        Properties viewConfiguration = pluginLogic.getPluginProperties(Constants.SYSSTATE_PLUGIN_NAME);
//
//        ModelAndView modelAndView = null;
//        final String defaultView = viewConfiguration.getProperty("defaultView", null);
//        if (StringUtils.isNotEmpty(defaultView)) {
//            modelAndView = index(Long.valueOf(defaultView));
//        } else {
//
//            Template template = getTemplate(templateId, viewConfiguration);
//            if (isMaintenanceMode(viewConfiguration)) {
//                modelAndView = new ModelAndView("maintenance-overview");
//            } else {
//                modelAndView = new ModelAndView(template.getLayout());
//                modelAndView.addObject("viewResults", ecoSystemLogic.getEcoSystem(new ViewDto()));
//            }
//            modelAndView.addObject("properties", PropertiesUtil.stringToProperties(template.getRenderHints()));
//            modelAndView.addObject("template", template);
//        }
//        return modelAndView;
    }

    private Template getTemplate(String templateId, Properties viewConfiguration) {
//        String templateName = StringUtils.defaultIfEmpty(templateId, viewConfiguration.getProperty("defaultTemplate", Constants.DEFAULT_TEMPLATE_VALUE));
//        return templateLogic.getTemplate(templateName);
        return null;
    }

    private boolean isMaintenanceMode(Properties viewConfiguration) {
        return BooleanUtils.toBoolean(viewConfiguration.getProperty("maintenanceMode", "false"));
    }

    @RequestMapping(value = "/view/{viewId}/index.html", method = RequestMethod.GET)
    public void index(@PathVariable("viewId") Long viewId, Writer responseWriter) {
        final ViewDto view = viewLogic.getView(viewId);
        
        
//        Properties viewConfiguration = pluginLogic.getPluginProperties(Constants.SYSSTATE_PLUGIN_NAME);
//        ModelAndView modelAndView = null;
//        
//        if (view != null) {
//          
//            if (isMaintenanceMode(viewConfiguration)) {
//                modelAndView = new ModelAndView("maintenance-overview");
//            } else {
//                modelAndView = new ModelAndView(template.getLayout());
//                modelAndView.addObject("controls", false);
//                modelAndView.addObject("viewResults", ecoSystemLogic.getEcoSystem(view));
//                modelAndView.addObject("properties", PropertiesUtil.stringToProperties(template.getRenderHints()));
//                modelAndView.addObject("view", view);
//            }
//            modelAndView.addObject("template", template);
//        }
//
//        return modelAndView;
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
