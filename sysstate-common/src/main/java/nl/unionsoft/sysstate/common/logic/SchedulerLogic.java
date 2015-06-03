package nl.unionsoft.sysstate.common.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.TaskDto;

public interface SchedulerLogic {

    public List<TaskDto> retrieveTasks();

    public int getCapacity();
    
    public int getLoad();

}
