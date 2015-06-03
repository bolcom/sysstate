package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.logic.SchedulerLogic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class SchedulerController {

    @Inject
    @Named("schedulerLogic")
    private SchedulerLogic schedulerLogic;

    @RequestMapping(value = "/scheduler/index", method = RequestMethod.GET)
    public ModelAndView active() {
        final ModelAndView modelAndView = new ModelAndView("scheduler-manager");
        modelAndView.addObject("tasks", schedulerLogic.retrieveTasks());
        return modelAndView;
    }

}
