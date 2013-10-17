package nl.unionsoft.sysstate.common.dto;

import java.util.ArrayList;
import java.util.List;

public class PropertyMetaList {
    private String name;
    private List<PropertyMeta> propertyMetas;

    public PropertyMetaList() {
        propertyMetas = new ArrayList<PropertyMeta>();
    }

    public List<PropertyMeta> getPropertyMetas() {
        return propertyMetas;
    }

    public void setPropertyMetas(List<PropertyMeta> propertyMetas) {
        this.propertyMetas = propertyMetas;
    }

    public void add(PropertyMeta propertyMeta) {
        propertyMetas.add(propertyMeta);
    }

    public PropertyMeta get(String id) {
        PropertyMeta result = null;
        for (PropertyMeta propertyMeta : propertyMetas) {
            if (propertyMeta.getId().equals(id)) {
                result = propertyMeta;
                break;
            }
        }
        return result;
    }

    public String getValue(String id) {
        String result = null;
        PropertyMeta propertyMeta = get(id);
        if (propertyMeta instanceof PropertyMetaValue) {
            PropertyMetaValue propertyMetaValue = (PropertyMetaValue) propertyMeta;
            result = propertyMetaValue.getValue();
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
