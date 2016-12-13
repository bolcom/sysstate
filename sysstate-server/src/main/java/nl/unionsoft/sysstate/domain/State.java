package nl.unionsoft.sysstate.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
//@formatter:off
@Table(name = "SSE_STATE",  indexes = { 
        @Index(columnList = "lastUpdate"), 
        @Index(columnList = "STATE"),
        })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
    @NamedQuery(name = "findLastStateForInstance", query = "FROM State WHERE instance.id = :instanceId ORDER BY lastUpdate DESC"),
    @NamedQuery(name = "findLastStateForInstanceWithStateType", query = "FROM State WHERE instance.id = :instanceId AND state = :stateType ORDER BY lastUpdate DESC")
    }
)
//@formatter:on
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DESCRIPTION", nullable = true, length = 20)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creationDate", nullable = true)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdate", nullable = true)
    private Date lastUpdate;

    @Column(name = "STATE", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private StateType state;

    @Column(name = "RESPONSE_TIME", nullable = true)
    private long responseTime;

    @Column(name = "MESSAGE", nullable = true, length = 4012)
    private String message;

    @Column(name = "RATING", nullable = false)
    private int rating;

    @ManyToOne
    @JoinColumn(name = "ICE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_INSTANCE_STATE"))
    private Instance instance;
    
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = StringUtils.substring(description, 0, StateDto.DESCRIPTION_MAX_LENGTH);
    }

    public StateType getState() {
        return state;
    }

    public void setState(final StateType state) {
        this.state = state;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(final long responseTime) {
        this.responseTime = responseTime;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(final Instance instance) {
        this.instance = instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(final Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = StringUtils.substring(message, 0, 4012);
    }

    public void appendMessage(final String append) {
        if (StringUtils.isNotEmpty(append)) {
            final StringBuilder appender = new StringBuilder(4012);
            appender.append(getMessage());
            appender.append(StringUtils.trim(append));
            setMessage(appender.toString());
        }
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "State [id=" + id + ", description=" + description + ", state=" + state + "]";
    }

}
