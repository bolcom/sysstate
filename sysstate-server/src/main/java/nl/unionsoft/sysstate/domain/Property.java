package nl.unionsoft.sysstate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_PROPERTY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Property {

    @Id
    @Column(name = "PROP_KEY", nullable = false)
    private String key;

    @Column(name = "PROP_VALUE", nullable = true, length = 20)
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = StringUtils.replace(StringUtils.upperCase(key), " ", "_");
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

}
