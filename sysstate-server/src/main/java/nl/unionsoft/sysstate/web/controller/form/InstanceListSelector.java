package nl.unionsoft.sysstate.web.controller.form;

import nl.unionsoft.sysstate.common.dto.InstanceDto;

public class InstanceListSelector {

    private boolean selected;
    private InstanceDto instance;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public InstanceDto getInstance() {
        return instance;
    }

    public void setInstance(InstanceDto instance) {
        this.instance = instance;
    }

}
