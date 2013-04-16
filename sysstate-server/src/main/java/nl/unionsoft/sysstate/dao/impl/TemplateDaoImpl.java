package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.domain.Template;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("templateDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TemplateDaoImpl implements TemplateDao {

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public Template getTemplate(final String templateId) {
        return entityManager.find(Template.class, templateId);
    }

    public void createOrUpdate(final Template template) {

        final String templateId = template.getId();
        if (StringUtils.isEmpty(templateId)) {
            entityManager.persist(template);
        } else {

            final Template existingTemplate = entityManager.find(Template.class, templateId);
            if (existingTemplate != null) {
                existingTemplate.setCss(template.getCss());
                existingTemplate.setLayout(template.getLayout());
                existingTemplate.setRefresh(template.getRefresh());
                existingTemplate.setRenderHints(template.getRenderHints());
                entityManager.merge(existingTemplate);
            } else {
                entityManager.persist(template);
            }
        }
    }

    public List<Template> getTemplates() {
        final List<Template> results = entityManager.createQuery("From Template", Template.class).setHint("org.hibernate.cacheable", true).getResultList();
        return results;
    }

    public void delete(String templateId) {
        entityManager.remove(entityManager.find(Template.class, templateId));
    }

}
