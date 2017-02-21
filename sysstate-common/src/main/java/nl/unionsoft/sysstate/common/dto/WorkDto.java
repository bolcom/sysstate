package nl.unionsoft.sysstate.common.dto;

import org.joda.time.DateTime;

import nl.unionsoft.sysstate.common.enums.WorkStatus;

public class WorkDto {

    private String reference;

    private String nodeId;

    private WorkStatus state;

    private DateTime initialized;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public WorkStatus getState() {
        return state;
    }

    public void setState(WorkStatus state) {
        this.state = state;
    }

    public DateTime getInitialized() {
        return initialized;
    }

    public void setInitialized(DateTime initialized) {
        this.initialized = initialized;
    }
    
    
}
