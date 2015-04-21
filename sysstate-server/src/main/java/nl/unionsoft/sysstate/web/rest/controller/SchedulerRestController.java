package nl.unionsoft.sysstate.web.rest.controller;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.sysstate_1_0.EcoSystem;

import org.quartz.Scheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller()
public class SchedulerRestController {
    @Inject
    @Named("scheduler")
    private Scheduler scheduler;
    
    @RequestMapping(value = "/scheduler/", method = RequestMethod.GET)
    public Scheduler ecosystemForView(@PathVariable("viewId") Long viewId) {
        final ViewDto view = viewLogic.getView(viewId);
        return ecoSystemConverter.convert(ecoSystemLogic.getEcoSystem(view));
    }
}
