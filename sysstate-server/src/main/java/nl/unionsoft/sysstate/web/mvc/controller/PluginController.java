package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;

import net.xeoh.plugins.base.Plugin;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.impl.PluginLogicImpl.PluginInstance;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class PluginController {

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @RequestMapping(value = "/plugin/index", method = RequestMethod.GET)
    public ModelAndView list() {
        final ModelAndView modelAndView = new ModelAndView("list-plugin-manager");
        modelAndView.addObject("plugins", pluginLogic.getPluginInstances(Plugin.class));
        return modelAndView;
    }

    @RequestMapping(value = "/plugin/{pluginClass}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("pluginClass") final String pluginClass) {
        final ModelAndView modelAndView = new ModelAndView("update-plugin-manager");
        final PluginInstance<Plugin> pluginInstance = pluginLogic.getPluginInstance(pluginClass);
        modelAndView.addObject("plugin", pluginInstance);
        modelAndView.addObject("configuration", PropertiesUtil.propertiesToString(pluginInstance.getProperties()));
        return modelAndView;
    }

    @RequestMapping(value = "/plugin/{pluginClass}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@PathVariable("pluginClass") final String pluginClass, @RequestParam("configuration") String configuration) {
        final ModelAndView modelAndView = new ModelAndView("redirect:/plugin/index.html");
        pluginLogic.updatePluginConfiguration(pluginClass, PropertiesUtil.stringToProperties(configuration));
        return modelAndView;
    }

}
