package nl.unionsoft.sysstate.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_NOTIFIER")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Notifier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PLUGIN", nullable = false, length = 512)
    private String plugin;

    @OneToMany(mappedBy = "notifier", cascade = CascadeType.ALL)
    private List<InstanceNotifier> instanceNotifiers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public List<InstanceNotifier> getInstanceNotifiers() {
        return instanceNotifiers;
    }

    public void setInstanceNotifiers(List<InstanceNotifier> instanceNotifiers) {
        this.instanceNotifiers = instanceNotifiers;
    }

}
