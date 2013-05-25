package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.logic.InstanceLogic;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class SchedulerController {

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("scheduler")
    private Scheduler scheduler;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/scheduler/index", method = RequestMethod.GET)
    public ModelAndView active() {
        List<Task> tasks = new ArrayList<Task>();
        List<JobExecutionContext> jobExecutionContexts;
        try {
            jobExecutionContexts = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext jobExecutionContext : jobExecutionContexts) {
                Trigger trigger = jobExecutionContext.getTrigger();
                JobDetail jobDetail = jobExecutionContext.getJobDetail();
                Task task = new Task();
                task.setJobDetail(jobDetail);
                task.setJobExecutionContext(jobExecutionContext);
                task.setTriggers(new Trigger[] { trigger });
                tasks.add(task);
            }
        } catch (SchedulerException e) {
        }
        final ModelAndView modelAndView = new ModelAndView("scheduler-manager");
        modelAndView.addObject("tasks", tasks);
        return modelAndView;
    }

    @RequestMapping(value = "/scheduler/all/index", method = RequestMethod.GET)
    public ModelAndView all() {

        List<Task> tasks = new ArrayList<Task>();

        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (String jobName : scheduler.getJobNames(groupName)) {
                    Task task = new Task();
                    JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
                    task.setJobDetail(jobDetail);
                    task.setTriggers(scheduler.getTriggersOfJob(jobName, groupName));
                    tasks.add(task);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            @SuppressWarnings("unchecked")
            List<JobExecutionContext> jobExecutionContexts = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext jobExecutionContext : jobExecutionContexts) {
                for (Task task : tasks) {
                    JobDetail jobDetail = task.getJobDetail();
                    if (jobDetail.getFullName().equals(jobExecutionContext.getJobDetail().getFullName())) {
                        task.setJobExecutionContext(jobExecutionContext);
                        break;
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        final ModelAndView modelAndView = new ModelAndView("scheduler-manager");
        modelAndView.addObject("tasks", tasks);
        return modelAndView;
    }

    public class Task {
        private JobExecutionContext jobExecutionContext;
        private JobDetail jobDetail;
        private Trigger[] triggers;

        public JobDetail getJobDetail() {
            return jobDetail;
        }

        public void setJobDetail(final JobDetail jobDetail) {
            this.jobDetail = jobDetail;
        }

        public Trigger[] getTriggers() {
            return triggers;
        }

        public void setTriggers(final Trigger[] triggers) {
            this.triggers = triggers;
        }

        public JobExecutionContext getJobExecutionContext() {
            return jobExecutionContext;
        }

        public void setJobExecutionContext(final JobExecutionContext jobExecutionContext) {
            this.jobExecutionContext = jobExecutionContext;
        }

    }

}
