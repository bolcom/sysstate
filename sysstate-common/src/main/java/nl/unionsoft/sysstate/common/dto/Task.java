package nl.unionsoft.sysstate.common.dto;

import java.util.Date;

public class Task {

    private String name;
    private String group;
    private String runTime;
    private long runTimeMillis;

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

}
