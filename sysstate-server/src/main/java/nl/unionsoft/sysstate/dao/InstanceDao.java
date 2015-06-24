package nl.unionsoft.sysstate.dao;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.domain.Instance;

public interface InstanceDao {

    public void createOrUpdate(Instance instance);

    public List<Instance> getInstancesForProjectAndEnvironment(Long projectId, Long environmentId);
    
    public List<Instance> getInstancesForProjectAndEnvironment(String projectName, String environmentName);

    public List<Instance> getInstancesForProjectEnvironment(Long projectEnvironmentId);

    public List<Instance> getInstances();

    public Optional<Instance> getInstance(Long instanceId);

    public void delete(Long instanceId);


}
