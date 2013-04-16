package nl.unionsoft.sysstate.common.queue;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.logic.DiscoveryLogic;

public class AddDiscoveredInstancesWorker implements ReferenceRunnable {

    private Collection<? extends InstanceDto> results;

    private static long count = 0;
    private long id;

    public AddDiscoveredInstancesWorker () {
        id = count++;
    }

    @Inject
    @Named("discoveryLogic")
    private DiscoveryLogic discoveryLogic;

    public void run() {
        if (results != null) {
            for (InstanceDto instance : results) {
                discoveryLogic.addDiscoveredInstance(instance);
            }
        }
    }

    public String getReference() {
        return "AddDiscoveredInstancesWorker-" + id;
    }

    public Collection<? extends InstanceDto> getResults() {
        return results;
    }

    public void setResults(Collection<? extends InstanceDto> results) {
        this.results = results;
    }

    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Worker to add discovered instances");
        return stringBuilder.toString();
    }
}
