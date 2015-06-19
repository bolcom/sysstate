package nl.unionsoft.sysstate.common.dto;

import java.io.Serializable;

public class InstanceLinkDto implements Serializable {

    private static final long serialVersionUID = -3245171821584603449L;
    private String name;
    private Long instanceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

}
