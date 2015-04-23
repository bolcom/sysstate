package nl.unionsoft.sysstate.dao.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.domain.Template;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("templateDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TemplateDaoImpl implements TemplateDao {

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public Template getTemplate(final String name) {
        //@formatter: off
        return entityManager.createQuery(
                "FROM Template "
                + "WHERE name = :name", Template.class)
                .setParameter("name", name)
                .getSingleResult();
        //@formatter: on
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
        entityManager.remove(getTemplate(name));
    }

}
