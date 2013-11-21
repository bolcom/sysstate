package nl.unionsoft.sysstate.common.dto;

import java.util.Map;
import java.util.Properties;

public class PropertyMetaValue {

    private String id;
    private String value;
    private String title;
    private boolean nullable;

    private Properties properties;
    private Map<String, String> lov;

    public PropertyMetaValue() {
        nullable = false;
        properties = new Properties();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Map<String, String> getLov() {
        return lov;
    }

    public void setLov(final Map<String, String> lov) {
        this.lov = lov;
    }

    /**
     * @return the nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * @param nullable
     *            the nullable to set
     */
    public void setNullable(final boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

}
