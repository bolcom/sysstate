package nl.unionsoft.sysstate.common.dto;

import java.io.Serializable;

import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.util.SysStateStringUtils;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class StateDto implements Serializable {

    private static final long serialVersionUID = -7907799338579793939L;
    private Long id;
    private String description;
    private StateType state;
    private long responseTime;
    private StringBuilder message;
    private InstanceDto instance;
    private DateTime creationDate;
    private DateTime lastUpdate;
    private int rating;
    public static final int DESCRIPTION_MAX_LENGTH = 15;

    public static final StateDto PENDING = new StateDto(StateType.PENDING, "Pending");

    public StateDto() {
        message = new StringBuilder(4012);
        rating = -1;
        responseTime = 0;
        creationDate = new DateTime();
    }

    public StateDto(StateType stateType, String description) {
        this();
        this.state = stateType;
        this.description = description;
        this.lastUpdate = new DateTime();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.substring(SysStateStringUtils.stripHtml(description), 0, DESCRIPTION_MAX_LENGTH);
    }

    public StateType getState() {
        return state;
    }

    public void setState(StateType state) {
        this.state = state;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public String getMessage() {
        return StringUtils.trimToEmpty(message.toString());
    }

    public void appendMessage(String message) {
        if (StringUtils.isNotEmpty(message)) {
            this.message.append(message);
            if (!StringUtils.endsWith(message, "\n")) {
                this.message.append('\n');
            }
        }

    }

    public InstanceDto getInstance() {
        return instance;
    }

    public void setInstance(InstanceDto instance) {
        this.instance = instance;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "StateDto [state=" + state + ", description=" + description + ", rating=" + rating + "]";
    }

    public DateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(DateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
