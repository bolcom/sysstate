package nl.unionsoft.sysstate.common.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceStateDto;
import nl.unionsoft.sysstate.common.enums.FilterBehaviour;

public interface InstanceStateLogic {

	
	List<InstanceStateDto> getInstanceStates(FilterDto filterDto, FilterBehaviour filterBehaviour);
}
