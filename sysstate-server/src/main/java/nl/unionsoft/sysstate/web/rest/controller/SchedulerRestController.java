package nl.unionsoft.sysstate.web.rest.controller;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import nl.unionsoft.sysstate.common.logic.SchedulerLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Scheduler;

@Controller()
public class SchedulerRestController {

    private SchedulerLogic schedulerLogic;

    @Inject
    public SchedulerRestController(SchedulerLogic schedulerLogic) {
        this.schedulerLogic = schedulerLogic;
    }

    @RequestMapping(value = "/scheduler/", method = RequestMethod.GET)
    public nl.unionsoft.sysstate.sysstate_1_0.Scheduler getScheduler() {
        Scheduler scheduler = new Scheduler();
        scheduler.setCapacity(schedulerLogic.getCapacity());
        scheduler.setLoad(schedulerLogic.getLoad());
        return scheduler;
    }
}
