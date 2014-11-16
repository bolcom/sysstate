package nl.unionsoft.sysstate.common.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.EnvironmentDto;

public interface EnvironmentLogic {

    public List<EnvironmentDto> getEnvironments();

    public EnvironmentDto getEnvironment(Long environmentId);

    public void createOrUpdate(EnvironmentDto environment);

    public void delete(Long environmentId);

    public EnvironmentDto getEnvironmentByName(String name);
}
