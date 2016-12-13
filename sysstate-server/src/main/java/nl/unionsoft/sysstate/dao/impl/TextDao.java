package nl.unionsoft.sysstate.dao.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.sysstate.domain.Text;

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

    public Optional<Text> getText(final String name) {

        //@formatter:off
        try {
            return Optional.of(entityManager.createQuery(
                    "From Text text WHERE text.name = :name", Text.class)
                    .setParameter("name", name)
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult());
        } catch (NoResultException nre){
            if (StringUtils.isNumeric(name)) {
                return Optional.ofNullable(entityManager.find(Text.class, Long.valueOf(name)));     
            }
            return Optional.empty();
        }
        //@formatter:on
    }

    public void createOrUpdateText(final Text text) {
        
        Optional<Text> optText = getText(text.getName());
        if (optText.isPresent()){
            Text t = optText.get();
            t.setTags(text.getTags());
            t.setText(text.getText());
        } else {
            entityManager.persist(text);
        }
    }

    public void delete(final String name) {
        Optional<Text> optText = getText(name);
        if (optText.isPresent()) {
            entityManager.remove(entityManager.find(Text.class, optText.get().getId()));
        }
    }

    public List<Text> getTexts(String[] tags) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Text> cq = cb.createQuery(Text.class);
        Root<Text> text = cq.from(Text.class);
        cq.select(text);
        Predicate[] restrictions = Arrays.stream(tags)
                .map(tag -> cb.like(text.get("tags"), "%" + tag + "%"))
                .collect(Collectors.toList())
                .toArray(new Predicate[] {});
        cq.where(cb.and(restrictions));
        return entityManager.createQuery(cq).getResultList();
    }
}
