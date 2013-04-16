package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.domain.InstanceWorkerPluginConfig;

public interface InstanceActionDao {
    public ListResponse<InstanceWorkerPluginConfig> getInstanceNotifiers(final ListRequest listRequest);

    public InstanceWorkerPluginConfig getInstanceNotifier(Long instanceNotifierId);

    public void createOrUpdate(InstanceWorkerPluginConfig notifier);

    public List<InstanceWorkerPluginConfig> getInstanceNotifiers(Long instanceId, Long notifierId);

    public List<InstanceWorkerPluginConfig> getInstanceWorkerPluginConfigs(Long instanceId);

    public void delete(Long id);
}
