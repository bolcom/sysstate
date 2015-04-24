package nl.unionsoft.sysstate.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.domain.Template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("templateDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TemplateDaoImpl implements TemplateDao {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateDaoImpl.class);
    
    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public Optional<Template> getTemplate(final String name) {

        try {
            //@formatter: off
            return Optional.of(entityManager.createQuery(
                "FROM Template "
                + "WHERE name = :name", Template.class)
                .setParameter("name", name)
                .getSingleResult());
            //@formatter: on            
        } catch (EntityNotFoundException | NoResultException nre){
            LOG.info("No template found for name [{}]", name);
            return Optional.empty();
        }

    }

    public void createOrUpdate(final Template template) {
        template.setLastUpdated(new Date());
        entityManager.merge(template);
    }

    public List<Template> getTemplates() {
        //@formatter:off
        return  entityManager.createQuery("FROM Template", Template.class)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
        //@formatter:on        
    }

    @Override
    public void delete(String name) {
        Optional<Template> optTemplate = getTemplate(name);
        if (optTemplate.isPresent()){
            entityManager.remove(optTemplate.get());    
        }
    }

}
