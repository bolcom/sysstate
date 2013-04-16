package nl.unionsoft.sysstate.web.controller.form;

import java.util.ArrayList;
import java.util.Collection;

public class DiscoveryMultiForm {
    private Collection<DiscoveryListSelector> discoveryListSelectors;

    public DiscoveryMultiForm() {
        discoveryListSelectors = new ArrayList<DiscoveryListSelector>();
    }

    public Collection<DiscoveryListSelector> getDiscoveryListSelectors() {
        return discoveryListSelectors;
    }

    public void setDiscoveryListSelectors(Collection<DiscoveryListSelector> discoveryListSelectors) {
        this.discoveryListSelectors = discoveryListSelectors;
    }

    public void addItem(DiscoveryListSelector t) {
        discoveryListSelectors.add(t);
    }

}
