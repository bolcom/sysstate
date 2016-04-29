package nl.unionsoft.sysstate.common.dto;

public class InstanceStateDto {

	private final InstanceDto instance;
	private final StateDto state;

	public InstanceStateDto(InstanceDto instance, StateDto state) {
		this.instance = instance;
		this.state = state;
	}

	public InstanceDto getInstance() {
		return instance;
	}

	public StateDto getState() {
		return state;
	}

}
