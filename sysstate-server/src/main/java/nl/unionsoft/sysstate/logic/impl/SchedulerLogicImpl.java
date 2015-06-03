package nl.unionsoft.sysstate.logic.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.Task;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.SchedulerLogic;

import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("schedulerLogic")
public class SchedulerLogicImpl implements SchedulerLogic {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerLogicImpl.class);

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("scheduler")
    private Scheduler scheduler;

    @Override
    public List<Task> retrieveTasks() {
        List<Task> tasks = new ArrayList<Task>();
        try {
            List<JobExecutionContext> jobExecutionContexts = scheduler.getCurrentlyExecutingJobs();
            LocalDateTime now = LocalDateTime.now();

            scheduler
                    .getJobKeys(GroupMatcher.anyJobGroup())
                    .stream()
                    .forEach(jobKey -> {

                        try {
                            Task task = new Task();
                            // JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                            task.setName(jobKey.getName());
                            task.setGroup(jobKey.getGroup());
                            // List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

                            Optional<JobExecutionContext> optionalJobExecutionContext = jobExecutionContexts.stream()
                                    .filter(jec -> jec.getJobDetail().getKey().equals(jobKey)).findFirst();

                            if (optionalJobExecutionContext.isPresent()) {
                                JobExecutionContext jobExecutionContext = optionalJobExecutionContext.get();
                                LocalDateTime fireTime = jobExecutionContext.getFireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                                
                                Duration duration = Duration.between(fireTime, now);
                                task.setRunTimeMillis(duration.toMillis());
                                
                                LocalDateTime tempDateTime = LocalDateTime.from( fireTime );
                                long hours = tempDateTime.until( now, ChronoUnit.HOURS);
                                tempDateTime = tempDateTime.plusHours( hours );

                                long minutes = tempDateTime.until( now, ChronoUnit.MINUTES);
                                tempDateTime = tempDateTime.plusMinutes( minutes );

                                long seconds = tempDateTime.until(now, ChronoUnit.SECONDS);
                                
                                task.setRunTime(String.format("%sh %sm %ss", new Object[]{hours, minutes, seconds}));
                            }
                            tasks.add(task);
                        } catch (Exception e) {
                            LOG.error("Unable to get task for jobKey [{}]", jobKey, e);
                        }

                    });

        } catch (SchedulerException e) {
            LOG.info("Unable to add tasks, caught SchedulerException", e);
        }
        return tasks;

    }

    @Override
    public int getCapacity() {
        try {
            SchedulerMetaData schedulerMetaData = scheduler.getMetaData();
            return schedulerMetaData.getThreadPoolSize();
        } catch (SchedulerException e) {
            throw new IllegalStateException("Caught SchedulerException while retrieving schedulerMetaData", e);
        }
    }

    @Override
    public int getLoad() {
        try {
            return scheduler.getCurrentlyExecutingJobs().size();
        } catch (SchedulerException e) {
            throw new IllegalStateException("Caught SchedulerException while retrieving currentlyExecutingJobs", e);
        }
    }

}
