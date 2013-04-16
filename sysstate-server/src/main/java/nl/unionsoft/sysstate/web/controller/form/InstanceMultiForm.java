package nl.unionsoft.sysstate.web.controller.form;

import java.util.ArrayList;
import java.util.Collection;

public class InstanceMultiForm {
    private Collection<InstanceListSelector> instanceListSelectors;

    public InstanceMultiForm () {
        instanceListSelectors = new ArrayList<InstanceListSelector>();
    }

    public Collection<InstanceListSelector> getInstanceListSelectors() {
        return instanceListSelectors;
    }

    public void setInstanceListSelectors(Collection<InstanceListSelector> instanceListSelectors) {
        this.instanceListSelectors = instanceListSelectors;
    }

    public void addItem(InstanceListSelector t) {
        instanceListSelectors.add(t);
    }

}
