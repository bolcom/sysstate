package nl.unionsoft.sysstate.web.mvc.form;

import java.util.ArrayList;
import java.util.List;

import nl.unionsoft.sysstate.common.dto.PropertyMetaList;

public class PropertyMetaListsForm {
    private List<PropertyMetaList> propertyMetaLists;

    public PropertyMetaListsForm() {
        propertyMetaLists = new ArrayList<PropertyMetaList>();
    }

    public List<PropertyMetaList> getPropertyMetaLists() {
        return propertyMetaLists;
    }

    public void setPropertyMetaLists(List<PropertyMetaList> propertyMetaLists) {
        this.propertyMetaLists = propertyMetaLists;
    }

}
