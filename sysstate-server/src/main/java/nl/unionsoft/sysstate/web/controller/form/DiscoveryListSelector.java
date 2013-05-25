package nl.unionsoft.sysstate.web.controller.form;

import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

public class DiscoveryListSelector {
    private boolean selected;
    private ReferenceRunnable referenceRunnable;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public ReferenceRunnable getReferenceRunnable() {
        return referenceRunnable;
    }

    public void setReferenceRunnable(final ReferenceRunnable referenceRunnable) {
        this.referenceRunnable = referenceRunnable;
    }

}
