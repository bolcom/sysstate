package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nl.unionsoft.common.param.ContextValue;
import nl.unionsoft.common.param.ParamContextLogicImpl;
import nl.unionsoft.sysstate.web.mvc.form.GroupConfigurationForm;
import nl.unionsoft.sysstate.web.mvc.form.GroupConfigurationsForm;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class ConfigurationController {

    @Inject
    @Named("paramContextLogic")
    private ParamContextLogicImpl paramContextLogic;

    @RequestMapping(value = "/configuration/index", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("list-configuration-manager");
        List<GroupConfigurationForm> groupConfigurationForm = new ArrayList<GroupConfigurationForm>();
        // groupConfigurationForm.add(generateGroupConfigurationContextValue(ViewConfiguration.class));
        // GroupConfigurationsForm groupConfigurationsForm = new GroupConfigurationsForm();
        // groupConfigurationsForm.setGroupConfigurationForms(groupConfigurationForm);
        // modelAndView.addObject("groupConfigurationsForm", groupConfigurationsForm);
        return modelAndView;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/configuration/index", method = RequestMethod.POST)
    public ModelAndView update(@Valid @ModelAttribute("groupConfigurationsForm") final GroupConfigurationsForm groupConfigurationsForm, final BindingResult bindingResult,
            final HttpServletRequest httpRequest) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("list-configuration-manager");
            // modelAndView.addObject("groupConfigurationForm", groupConfigurationForm);
        } else {

            // List<GroupConfigurationForm> groupConfigurationForms = groupConfigurationsForm.getGroupConfigurationForms();
            // for (GroupConfigurationForm groupConfigurationContextValue : groupConfigurationForms) {
            // try {
            // Class<? extends GroupConfiguration> groupConfigurationClass = (Class<? extends GroupConfiguration>) Class.forName(groupConfigurationContextValue
            // .getGroupClass());
            // GroupConfiguration groupConfiguration = groupConfigurationClass.newInstance();
            // paramContextLogic.setContextValues(groupConfiguration, groupConfigurationContextValue.getContextValues());
            // configurationLogic.setGroupConfiguration(groupConfiguration);
            // } catch (ClassNotFoundException e) {
            // e.printStackTrace();
            // } catch (InstantiationException e) {
            // e.printStackTrace();
            // } catch (IllegalAccessException e) {
            // e.printStackTrace();
            // }
            // }
            modelAndView = new ModelAndView("redirect:/filter/index.html");
        }
        return modelAndView;
    }

    // private GroupConfigurationForm generateGroupConfigurationContextValue(Class<? extends GroupConfiguration> gcClass) {
    // GroupConfigurationForm configurationContextValue = new GroupConfigurationForm();
    // configurationContextValue.setGroupName(gcClass.getSimpleName());
    // configurationContextValue.setGroupClass(gcClass.getCanonicalName());
    // GroupConfiguration groupConfiguration = configurationLogic.getGroupConfiguration(gcClass);
    // List<ContextValue> contexts = paramContextLogic.getContextValues(groupConfiguration);
    // configurationContextValue.setContextValues(contexts);
    // return configurationContextValue;
    //
    // }

}
