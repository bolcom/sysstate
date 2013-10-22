package nl.unionsoft.sysstate.common.dto;

import java.util.Properties;

public class PropertyMetaValue {

    private String id;
    private String value;
    private String title;

    private Properties lov;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Properties getLov() {
        return lov;
    }

    public void setLov(Properties lov) {
        this.lov = lov;
    }

}
