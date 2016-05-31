package nl.unionsoft.sysstate.common.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceStateDto;

public interface InstanceStateLogic {

	
	List<InstanceStateDto> getInstanceStates(FilterDto filterDto);
	
	List<InstanceStateDto> getInstanceStates(Long filterId);
}
