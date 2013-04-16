package nl.unionsoft.sysstate.web.controller.form;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

public class DiscoveryListSelector {
    private boolean selected;
    private ReferenceRunnable referenceRunnable;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ReferenceRunnable getReferenceRunnable() {
        return referenceRunnable;
    }

    public void setReferenceRunnable(ReferenceRunnable referenceRunnable) {
        this.referenceRunnable = referenceRunnable;
    }

}
