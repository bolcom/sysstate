package nl.unionsoft.sysstate.web.mvc.form;

import java.util.ArrayList;
import java.util.List;

public class GroupConfigurationsForm {
    private List<GroupConfigurationForm> groupConfigurationForms;

    public GroupConfigurationsForm(){
        groupConfigurationForms = new ArrayList<GroupConfigurationForm>();
    }

    public List<GroupConfigurationForm> getGroupConfigurationForms() {
        return groupConfigurationForms;
    }

    public void setGroupConfigurationForms(List<GroupConfigurationForm> groupConfigurationForms) {
        this.groupConfigurationForms = groupConfigurationForms;
    }
}
