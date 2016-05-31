package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceStateDto;
import nl.unionsoft.sysstate.common.enums.StateBehaviour;
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.InstanceStateLogic;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

@Service("instanceStateLogic")
public class InstanceStateLogicImpl implements InstanceStateLogic {

    @Inject
    private InstanceLogic instanceLogic;

    @Inject
    private StateLogic stateLogic;

    @Inject
    private FilterLogic filterLogic;

    @Inject
    private InstanceLinkLogic instanceLinkLogic;

    @Override
    public List<InstanceStateDto> getInstanceStates(FilterDto filter) {
        return toInstanceStates(instanceLogic.getInstances(filter), filter);
    }

    @Override
    public List<InstanceStateDto> getInstanceStates(Long filterId) {
        Optional<FilterDto> optFilter = filterLogic.getFilter(filterId);
        if (!optFilter.isPresent()) {
            throw new IllegalStateException("No filter with filterId [" + filterId + "] can be found.");
        }
        return toInstanceStates(instanceLogic.getInstances(filterId), optFilter.get());
    }

    private List<InstanceStateDto> toInstanceStates(List<InstanceDto> instances, FilterDto filter) {
        //@formatter:off
        return instances.parallelStream()
         .map( instance -> new InstanceStateDto(instance, stateLogic.getLastStateForInstance(instance, StateBehaviour.CACHED), instanceLinkLogic.getInstanceLinks(instance.getId())))
         .filter(instanceState -> filter.getStates() == null || filter.getStates().isEmpty() || filter.getStates().contains(instanceState.getState().getState()))
         .collect(Collectors.toList());
        //@formatter:on     
    }

}
