package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.logic.impl.InstanceLogicImpl;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class SchedulerController {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerController.class);

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("scheduler")
    private Scheduler scheduler;

    @RequestMapping(value = "/scheduler/index", method = RequestMethod.GET)
    public ModelAndView active() {
        List<Task> tasks = new ArrayList<Task>();
        try {
            List<JobExecutionContext> jobExecutionContexts = scheduler.getCurrentlyExecutingJobs();
            scheduler
                    .getJobKeys(GroupMatcher.anyJobGroup())
                    .stream()
                    .forEach(jobKey -> {
                        Optional<JobExecutionContext> optionalJobExecutionContext = jobExecutionContexts.stream()
                                .filter(jec -> jec.getJobDetail().getKey().equals(jobKey)).findFirst();
                        try {
                            if (optionalJobExecutionContext.isPresent()){
                                tasks.add(new Task(scheduler.getJobDetail(jobKey), scheduler.getTriggersOfJob(jobKey), optionalJobExecutionContext));    
                            }
                        } catch (Exception e) {
                            LOG.info("Unable to add task, caught Exception", e);
                        }
                    });

        } catch (SchedulerException e) {
            LOG.info("Unable to add tasks, caught SchedulerException", e);
        }
        final ModelAndView modelAndView = new ModelAndView("scheduler-manager");
        modelAndView.addObject("tasks", tasks);
        return modelAndView;
    }

    @RequestMapping(value = "/scheduler/all/index", method = RequestMethod.GET)
    public ModelAndView all() {

        List<Task> tasks = new ArrayList<Task>();

        try {
            List<JobExecutionContext> jobExecutionContexts = scheduler.getCurrentlyExecutingJobs();
            scheduler
                    .getJobKeys(GroupMatcher.anyJobGroup())
                    .stream()
                    .forEach(jobKey -> {
                        Optional<JobExecutionContext> jobExecutionContext = jobExecutionContexts.stream()
                                .filter(jec -> jec.getJobDetail().getKey().equals(jobKey)).findFirst();
                        try {
                            tasks.add(new Task(scheduler.getJobDetail(jobKey), scheduler.getTriggersOfJob(jobKey), jobExecutionContext));
                        } catch (Exception e) {
                            LOG.info("Unable to add task, caught Exception", e);
                        }
                    });

        } catch (SchedulerException e) {
            LOG.info("Unable to add tasks, caught SchedulerException", e);
        }

        final ModelAndView modelAndView = new ModelAndView("scheduler-manager");
        modelAndView.addObject("tasks", tasks);
        return modelAndView;
    }

    public class Task {
        private final Optional<JobExecutionContext> jobExecutionContext;
        private final JobDetail jobDetail;
        private final List<? extends Trigger> triggers;

        public Task(JobDetail jobDetail, List<? extends Trigger> triggers, Optional<JobExecutionContext> jobExecutionContext) {
            this.jobExecutionContext = jobExecutionContext;
            this.jobDetail = jobDetail;
            this.triggers = triggers;
        }

        public JobDetail getJobDetail() {
            return jobDetail;
        }

        public List<? extends Trigger> getTriggers() {
            return triggers;
        }

        public Optional<JobExecutionContext> getJobExecutionContext() {
            return jobExecutionContext;
        }

    }

}
