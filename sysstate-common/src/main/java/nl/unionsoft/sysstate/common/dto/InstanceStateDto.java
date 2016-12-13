package nl.unionsoft.sysstate.common.dto;

import java.util.List;

public class InstanceStateDto {

	private final InstanceDto instance;
	private final StateDto state;
	private final List<InstanceLinkDto> instanceLinks;

	public InstanceStateDto(InstanceDto instance, StateDto state, List<InstanceLinkDto> instanceLinks) {
		this.instance = instance;
		this.state = state;
		this.instanceLinks = instanceLinks;
	}

	public InstanceDto getInstance() {
		return instance;
	}

	public StateDto getState() {
		return state;
	}

	public List<InstanceLinkDto> getInstanceLinks() {
		return instanceLinks;
	}

}
