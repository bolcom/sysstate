package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.logic.SchedulerLogic;
import nl.unionsoft.sysstate.logic.WorkLogic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class WorkController {

    @Inject
    @Named("workLogic")
    private WorkLogic workLogic;

    @RequestMapping(value = "/work/index", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("work-manager");
        modelAndView.addObject("myWork", workLogic.getMyWork());
        return modelAndView;
    }

}
