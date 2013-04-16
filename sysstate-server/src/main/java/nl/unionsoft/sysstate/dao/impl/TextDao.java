package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.sysstate.domain.Text;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("textDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TextDao {
    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public List<Text> getTexts() {
        //@formatter:off
        return entityManager.createQuery(
            "From Text text " +
            "ORDER BY tags ASC", Text.class)
            .setHint("org.hibernate.cacheable", true)
            .getResultList();
        //@formatter:on
    }

    public Text getText(final Long textId) {
        return entityManager.find(Text.class, textId);
    }

    public void createOrUpdateText(final Text text) {
        if (text.getId() == null) {
            entityManager.persist(text);
        } else {
            entityManager.merge(text);
        }
    }

    public void delete(final Long textId) {
        entityManager.remove(entityManager.find(Text.class, textId));
    }
}
