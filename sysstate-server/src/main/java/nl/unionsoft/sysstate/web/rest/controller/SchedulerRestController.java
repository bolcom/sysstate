package nl.unionsoft.sysstate.web.rest.controller;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller()
public class SchedulerRestController {

    private Scheduler quartzScheduler;

    @Inject
    public SchedulerRestController(Scheduler quartzScheduler) {
        this.quartzScheduler = quartzScheduler;
    }
    
    @RequestMapping(value = "/scheduler/", method = RequestMethod.GET)
    public nl.unionsoft.sysstate.sysstate_1_0.Scheduler getScheduler() throws SchedulerException {
        nl.unionsoft.sysstate.sysstate_1_0.Scheduler scheduler = new nl.unionsoft.sysstate.sysstate_1_0.Scheduler();
        SchedulerMetaData schedulerMetaData = quartzScheduler.getMetaData();
        scheduler.setCapacity(schedulerMetaData.getThreadPoolSize());
        scheduler.setLoad(quartzScheduler.getCurrentlyExecutingJobs().size());
        return scheduler;
    }
}
