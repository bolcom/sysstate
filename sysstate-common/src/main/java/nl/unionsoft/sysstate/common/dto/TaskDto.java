package nl.unionsoft.sysstate.common.dto;

import java.util.Date;

public class TaskDto {

    private String name;
    private String group;
    private String runTime;
    private long runTimeMillis;
    private Date lastRunTime;
    private Date nextRunTime;
    private final String type;

    protected TaskDto(String type) {
        this.type = type;
    }

    public TaskDto() {
        this("generic");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public long getRunTimeMillis() {
        return runTimeMillis;
    }

    public void setRunTimeMillis(long runTimeMillis) {
        this.runTimeMillis = runTimeMillis;
    }

    public String getType() {
        return type;
    }

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public Date getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

}
