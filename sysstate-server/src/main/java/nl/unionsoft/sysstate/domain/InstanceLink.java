package nl.unionsoft.sysstate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
//@formatter:off
@Table(name = "SSE_INSTANCE_LINK", indexes = { 
        @Index(columnList = "FROM_ICE_ID"),
        @Index(columnList = "TO_ICE_ID")
        }
)
//@formatter:on
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InstanceLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true, length = 200)
    private String name;

    @ManyToOne
    @JoinColumn(name = "FROM_ICE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_INSTANCE_LINK_FROM"))
    private Instance from;

    @ManyToOne
    @JoinColumn(name = "TO_ICE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_INSTANCE_LINK_TO"))
    private Instance to;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instance getFrom() {
        return from;
    }

    public void setFrom(Instance from) {
        this.from = from;
    }

    public Instance getTo() {
        return to;
    }

    public void setTo(Instance to) {
        this.to = to;
    }

}
