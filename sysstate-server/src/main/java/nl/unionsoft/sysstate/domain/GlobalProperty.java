package nl.unionsoft.sysstate.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("GLOBAL")
public class GlobalProperty extends Property {

    @Column(name = "PROP_GROUP", nullable = true, length = 512)
    private String group;

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(final String group) {
        this.group = group;
    }

}
