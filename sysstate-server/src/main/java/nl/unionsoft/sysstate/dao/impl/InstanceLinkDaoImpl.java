package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.sysstate.dao.InstanceLinkDao;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.InstanceLink;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("instanceLinkDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class InstanceLinkDaoImpl implements InstanceLinkDao{

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;
    
    @Override
    public void create(Long instanceFromId, Long instanceToId, String name) {
        
        List<InstanceLink> instanceLinks = entityManager.createQuery(
                "FROM InstanceLink ilk " +
                "WHERE ilk.from.id = :instanceFromId " +
                "AND ilk.to.id = :instanceToId " +
                "AND ilk.name = :name", InstanceLink.class)
                .setParameter("instanceFromId", instanceFromId)
                .setParameter("instanceToId", instanceToId)
                .setParameter("name", name)
                .getResultList();
        if (instanceLinks.isEmpty()){
            InstanceLink instanceLink = new InstanceLink();
            instanceLink.setFrom(entityManager.find(Instance.class, instanceFromId));
            instanceLink.setTo(entityManager.find(Instance.class, instanceToId));
            instanceLink.setName(name);
            entityManager.persist(instanceLink);
        }
    }

    @Override
    public void delete(Long instanceFromId, Long instanceToId, String name) {
        entityManager.createQuery(
                "DELETE FROM InstanceLink ilk "+ 
                "WHERE ilk.from.id = :instanceFromId " +
                "AND ilk.to.id = :instanceToId " +
                "AND ilk.name = :name", InstanceLink.class)
                .setParameter("instanceFromId", instanceFromId)
                .setParameter("instanceToId", instanceToId)
                .setParameter("name", name).executeUpdate();
    }
    
    
    

}
