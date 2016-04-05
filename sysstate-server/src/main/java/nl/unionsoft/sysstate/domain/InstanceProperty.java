package nl.unionsoft.sysstate.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("INSTANCE")
public class InstanceProperty extends Property {

    @ManyToOne
    @JoinColumn(name = "ICE_ID", foreignKey = @ForeignKey(name = "FK_INSTANCE"))
    private Instance instance;

    /**
     * @return the instance
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * @param instance
     *            the instance to set
     */
    public void setInstance(final Instance instance) {
        this.instance = instance;
    }

}
