package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import nl.unionsoft.sysstate.common.logic.ResourceLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;

@Controller()
public class ResourceController {

    
    @Inject

    private ResourceLogic resourceLogic;
    
    @RequestMapping(value = "/resources/index", method = RequestMethod.GET)
    public ModelAndView index() {
        
        
        final ModelAndView modelAndView = new ModelAndView("list-resource-manager");
        modelAndView.addObject("resources", resourceLogic.getResources());
        return modelAndView;
    }
}