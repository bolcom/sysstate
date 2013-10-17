package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nl.unionsoft.sysstate.Constants;
import nl.unionsoft.sysstate.common.dto.PropertyMetaList;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.web.mvc.form.PropertyMetaListsForm;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class ConfigurationController {

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @RequestMapping(value = "/configuration/index", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("list-configuration-manager");

        PropertyMetaListsForm propertyMetaListsForm = new PropertyMetaListsForm();
        List<PropertyMetaList> propertyMetaLists = propertyMetaListsForm.getPropertyMetaLists();
        propertyMetaLists.add(pluginLogic.getPluginPropertyMetaList(Constants.SYSSTATE_PLUGIN_NAME));
        modelAndView.addObject("propertyMetaListsForm", propertyMetaListsForm);
        return modelAndView;
    }

    @RequestMapping(value = "/configuration/index", method = RequestMethod.POST)
    public ModelAndView update(@Valid @ModelAttribute("propertyMetaListsForm") final PropertyMetaListsForm groupConfigurationsForm, final BindingResult bindingResult,
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
