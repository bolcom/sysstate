package nl.unionsoft.sysstate.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_RESOURCE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MANAGER", nullable = true, length = 512)
    private String manager;

    @Column(name = "NAME", nullable = true, length = 200)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "SSE_RESOURCE_CONFIGURATION", joinColumns = @JoinColumn(name = "ID") )
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    private Map<String, String> configuration;

    public Resource() {
        configuration = new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }

   

}
