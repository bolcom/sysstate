package nl.unionsoft.sysstate.common.dto;

import java.io.Serializable;

public class InstanceLinkDto implements Serializable {

    private static final long serialVersionUID = -3245171821584603449L;
    private String name;
    private Long instanceToId;
    private Long instanceFromId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInstanceToId() {
        return instanceToId;
    }

    public void setInstanceToId(Long instanceToId) {
        this.instanceToId = instanceToId;
    }

    public Long getInstanceFromId() {
        return instanceFromId;
    }

    public void setInstanceFromId(Long instanceFromId) {
        this.instanceFromId = instanceFromId;
    }

}
