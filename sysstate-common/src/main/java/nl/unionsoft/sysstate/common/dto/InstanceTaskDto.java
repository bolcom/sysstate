package nl.unionsoft.sysstate.common.dto;

public class InstanceTaskDto extends TaskDto {

    public InstanceTaskDto() {
        super("instance");
    }

    private InstanceDto instance;

    public InstanceDto getInstance() {
        return instance;
    }

    public void setInstance(InstanceDto instance) {
        this.instance = instance;
    }

}
