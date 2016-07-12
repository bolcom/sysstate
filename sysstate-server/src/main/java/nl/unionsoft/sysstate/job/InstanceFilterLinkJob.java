package nl.unionsoft.sysstate.job;

import java.util.Optional;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.logic.FilterLogic;

public class InstanceFilterLinkJob implements Runnable {

    private final FilterLogic filterLogic;
    private final long filterId;

    public InstanceFilterLinkJob(FilterLogic filterLogic, long filterId) {

        this.filterLogic = filterLogic;
        this.filterId = filterId;
    }

    @Override
    public void run() {

        Optional<FilterDto> optFilter = filterLogic.getFilter(filterId);
        if (optFilter.isPresent()) {
            filterLogic.updateFilterSubscriptions(optFilter.get());
        }

    }

}
