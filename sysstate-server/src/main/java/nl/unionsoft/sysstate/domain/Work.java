package nl.unionsoft.sysstate.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.unionsoft.sysstate.common.enums.WorkStatus;

@Entity
@Table(name = "SSE_WORK", indexes = {
        @Index(columnList = "REFERENCE"),
        @Index(columnList = "NODEID"),
        @Index(columnList = "STATUS"),
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "REFERENCE", nullable = false, length = 255, unique = true)
    private String reference;

    @Column(name = "NODEID", nullable = false, length = 255)
    private String nodeId;

    @Column(name = "STATUS", nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private WorkStatus state;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INITIALIZED", nullable = false)
    private Date initialized;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public WorkStatus getState() {
        return state;
    }

    public void setState(WorkStatus state) {
        this.state = state;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Date getInitialized() {
        return initialized;
    }

    public void setInitialized(Date initialized) {
        this.initialized = initialized;
    }

}
