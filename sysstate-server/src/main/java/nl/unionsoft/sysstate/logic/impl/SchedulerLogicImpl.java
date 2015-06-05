package nl.unionsoft.sysstate.logic.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceTaskDto;
import nl.unionsoft.sysstate.common.dto.TaskDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.SchedulerLogic;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
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
    public List<TaskDto> retrieveTasks() {
        List<TaskDto> tasks = new ArrayList<TaskDto>();
        try {
            List<JobExecutionContext> jobExecutionContexts = scheduler.getCurrentlyExecutingJobs();
            LocalDateTime now = LocalDateTime.now();

            scheduler
                    .getJobKeys(GroupMatcher.anyJobGroup())
                    .stream()
                    .forEach(jobKey -> {

                        try {
                            TaskDto task = createTask(jobKey);

                            task.setName(jobKey.getName());
                            task.setGroup(jobKey.getGroup());
                            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

                            task.setNextRunTime(triggers.stream().map(t -> t.getNextFireTime()).min(Date::compareTo).get());
                            task.setLastRunTime(triggers.stream().map(t -> t.getPreviousFireTime()).max(Date::compareTo).get());

                            Optional<JobExecutionContext> optionalJobExecutionContext = jobExecutionContexts.stream()
                                    .filter(jec -> jec.getJobDetail().getKey().equals(jobKey)).findFirst();

                            if (optionalJobExecutionContext.isPresent()) {
                                JobExecutionContext jobExecutionContext = optionalJobExecutionContext.get();
                                LocalDateTime fireTime = jobExecutionContext.getFireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                                Duration duration = Duration.between(fireTime, now);
                                task.setRunTimeMillis(duration.toMillis());

                                LocalDateTime tempDateTime = LocalDateTime.from(fireTime);
                                long hours = tempDateTime.until(now, ChronoUnit.HOURS);
                                tempDateTime = tempDateTime.plusHours(hours);

                                long minutes = tempDateTime.until(now, ChronoUnit.MINUTES);
                                tempDateTime = tempDateTime.plusMinutes(minutes);

                                long seconds = tempDateTime.until(now, ChronoUnit.SECONDS);

                                task.setRunTime(String.format("%sh %sm %ss", new Object[] { hours, minutes, seconds }));
                            }
                            tasks.add(task);
                        } catch (Exception e) {
                            LOG.error("Unable to get task for jobKey [{}]", jobKey, e);
                        }
                    });

        } catch (SchedulerException e) {
            LOG.info("Unable to add tasks, caught SchedulerException", e);
        }
        sortTasks(tasks);
        return tasks;

    }

    private void sortTasks(List<TaskDto> tasks) {
        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(new BeanComparator("runTimeMillis", new NullComparator(new ReverseComparator(ComparableComparator.getInstance()))));
        comparatorChain.addComparator(new BeanComparator("nextRunTime", new NullComparator(ComparableComparator.getInstance())));
        Collections.sort(tasks, comparatorChain);
    }

    private TaskDto createTask(JobKey jobKey) throws SchedulerException {
        if (jobKey.getGroup().equalsIgnoreCase("instances")) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            Long instanceId = jobDataMap.getLong("instanceId");
            InstanceTaskDto instanceTask = new InstanceTaskDto();
            if (instanceId != null && instanceId > 0) {
                instanceTask.setInstance(instanceLogic.getInstance(instanceId));
            }
            return instanceTask;
        } else {
            return new TaskDto();
        }
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
