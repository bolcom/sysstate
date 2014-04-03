package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.sf.ehcache.CacheManager;
import nl.unionsoft.sysstate.logic.PluginLogic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class PluginController {

    
    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;
    
    @RequestMapping(value = "/plugins/index", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("list-plugin-manager");
//        modelAndView.addObject("plugins", pluginLogic.getPlugins());
        return modelAndView;
    }
}
