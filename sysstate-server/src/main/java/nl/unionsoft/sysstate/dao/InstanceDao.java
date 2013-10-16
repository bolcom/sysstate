package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.Instance;

public interface InstanceDao {

    public void createOrUpdate(Instance instance);

    public List<Instance> getInstancesForProjectAndEnvironment(Long projectId, Long environmentId);

    public List<Instance> getInstancesForProjectEnvironment(Long projectEnvironmentId);

    public List<Instance> getInstances();

    public Instance getInstance(Long instanceId);

    public void delete(Long instanceId);

    public List<Instance> getInstancesForPrefixes(String projectPrefix, String environmentPrefix);

}
