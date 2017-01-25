package nl.unionsoft.sysstate.common.dto;

import java.util.ArrayList;
import java.util.List;

public class PropertyMetaList {
    private String name;
    private String id;
    private List<PropertyMetaValue> propertyMetaValues;

    public PropertyMetaList() {
        propertyMetaValues = new ArrayList<PropertyMetaValue>();
    }

    public List<PropertyMetaValue> getPropertyMetaValues() {
        return propertyMetaValues;
    }

    public void setPropertyMetaValues(List<PropertyMetaValue> propertyMetaValues) {
        this.propertyMetaValues = propertyMetaValues;
    }

    public void add(PropertyMetaValue propertyMetaValue) {
        propertyMetaValues.add(propertyMetaValue);
    }

    public PropertyMetaValue get(String id) {
        PropertyMetaValue result = null;
        for (PropertyMetaValue propertyMetaValue : propertyMetaValues) {
            if (propertyMetaValue.getId().equals(id)) {
                result = propertyMetaValue;
                break;
            }
        }
        return result;
    }

    public String getValue(String id) {
        String result = null;
        PropertyMetaValue propertyMeta = get(id);
        if (propertyMeta instanceof PropertyMetaValue) {
            PropertyMetaValue propertyMetaValue = (PropertyMetaValue) propertyMeta;
            result = propertyMetaValue.getDefaultValue();
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
