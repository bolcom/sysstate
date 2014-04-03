package nl.unionsoft.sysstate.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_INSTANCE_NOTIFIER")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InstanceNotifier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ICE_ID")
    private Instance instance;

    @ManyToOne
    @JoinColumn(name = "NTR_ID")
    private Notifier notifier;

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public void setNotifier(Notifier notifier) {
        this.notifier = notifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
