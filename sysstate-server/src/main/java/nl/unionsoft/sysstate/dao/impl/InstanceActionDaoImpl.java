package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.common.list.worker.ListRequestWorker;
import nl.unionsoft.sysstate.dao.InstanceActionDao;
import nl.unionsoft.sysstate.domain.InstanceWorkerPluginConfig;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("instanceActionDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class InstanceActionDaoImpl implements InstanceActionDao {
    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    @Inject
    @Named("criteriaListRequestWorker")
    private ListRequestWorker listRequestWorker;

    public ListResponse<InstanceWorkerPluginConfig> getInstanceNotifiers(final ListRequest listRequest) {
        return listRequestWorker.getResults(InstanceWorkerPluginConfig.class, listRequest);
    }

    public InstanceWorkerPluginConfig getInstanceNotifier(Long instanceNotifierId) {
        return entityManager.find(InstanceWorkerPluginConfig.class, instanceNotifierId);
    }

    public void createOrUpdate(InstanceWorkerPluginConfig instanceWorkerPluginConfig) {
        if (instanceWorkerPluginConfig.getId() == null) {
            entityManager.persist(instanceWorkerPluginConfig);
        } else {
            entityManager.merge(instanceWorkerPluginConfig);
        }

    }

    public List<InstanceWorkerPluginConfig> getInstanceNotifiers(Long instanceId, Long actionId) {
        //@formatter:off
        return entityManager.createQuery(
                "FROM InstanceWorkerPluginConfig inf " +
                "WHERE inf.instance.id = :instanceId " +
                "AND inf.action.id = :actionId ", InstanceWorkerPluginConfig.class)
                .setParameter("instanceId", instanceId)
                .setParameter("actionId", actionId)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
        //@formatter:on

    }

    public List<InstanceWorkerPluginConfig> getInstanceWorkerPluginConfigs(Long instanceId) {
        // @formatter: off
        return entityManager.createQuery("FROM InstanceWorkerPluginConfig inf " + "WHERE inf.instance.id = :instanceId ", InstanceWorkerPluginConfig.class)
            .setParameter("instanceId", instanceId).setHint("org.hibernate.cacheable", true).getResultList();
        // @formatter: on
    }

    public void delete(Long id) {
        entityManager.remove(entityManager.find(InstanceWorkerPluginConfig.class, id));
    }

}
