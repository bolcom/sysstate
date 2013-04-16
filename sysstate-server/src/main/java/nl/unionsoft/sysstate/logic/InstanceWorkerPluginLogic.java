package nl.unionsoft.sysstate.logic;

import java.util.List;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.domain.InstanceWorkerPluginConfig;

public interface InstanceWorkerPluginLogic {
    public ListResponse<InstanceWorkerPluginConfig> getInstanceWorkerPluginConfigs(final ListRequest listRequest);

    public List<InstanceWorkerPluginConfig> getInstanceWorkerPluginConfigs(Long instanceId, Long notifierId);

    public List<InstanceWorkerPluginConfig> getInstanceWorkerPluginConfigs(Long instanceId);

    public InstanceWorkerPluginConfig getInstanceWorkerPluginConfig(Long instanceNotifierId);

    public void createOrUpdate(InstanceWorkerPluginConfig notifier);

    public void delete(Long id);

}
