package nl.unionsoft.sysstate.web.mvc.form;

import java.util.ArrayList;
import java.util.List;

import nl.unionsoft.common.param.ContextValue;
import nl.unionsoft.sysstate.web.mvc.controller.ConfigurationController;

public class GroupConfigurationForm {
    private String groupName;
    private String groupClass;
    private List<ContextValue> contextValues;

    public GroupConfigurationForm() {
        contextValues = new ArrayList<ContextValue>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupClass() {
        return groupClass;
    }

    public void setGroupClass(String groupClass) {
        this.groupClass = groupClass;
    }

    public List<ContextValue> getContextValues() {
        return contextValues;
    }

    public void setContextValues(List<ContextValue> contextValues) {
        this.contextValues = contextValues;
    }

}
