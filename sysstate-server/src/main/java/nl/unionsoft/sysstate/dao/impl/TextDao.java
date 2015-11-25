package nl.unionsoft.sysstate.dao.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

    public List<Text> getTexts(String[] tags) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Text> cq = cb.createQuery(Text.class);
        Root<Text> text = cq.from(Text.class);
        cq.select(text);
        Predicate[] restrictions = Arrays.stream(tags).map(tag -> cb.like(text.get("tags"), "%" + tag + "%")).collect(Collectors.toList()).toArray(new Predicate[] {});
        cq.where(cb.and(restrictions));
        return entityManager.createQuery(cq).getResultList();
    }
}
