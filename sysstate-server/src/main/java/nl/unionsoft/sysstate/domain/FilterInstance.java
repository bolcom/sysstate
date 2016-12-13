package nl.unionsoft.sysstate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SSE_FILTER_INSTANCE")
public class FilterInstance {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ICE_ID", foreignKey = @ForeignKey(name = "FK_INSTANCE_FILTER_INSTANCE") )
    private Instance instance;

    @ManyToOne
    @JoinColumn(name = "FTR_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_FILTER_FILTER_INSTANCE") )
    private Filter filter;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Instance getInstance() {
        return instance;
    }

    public void setInstance(final Instance instance) {
        this.instance = instance;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
