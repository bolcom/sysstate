package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.TaskDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.SchedulerLogic;

@Service("schedulerLogic")
public class SchedulerLogicImpl implements SchedulerLogic {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerLogicImpl.class);

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("scheduler")
    private TaskScheduler scheduler;

    @Override
    public List<TaskDto> retrieveTasks() {
        List<TaskDto> tasks = new ArrayList<TaskDto>();
        return tasks;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public int getLoad() {
        return 0;
    }

}
