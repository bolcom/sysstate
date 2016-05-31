package nl.unionsoft.sysstate.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.domain.Instance;

@Service("instanceDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class InstanceDaoImpl implements InstanceDao {

    private final static String[] searchFields = { "name", "homepageUrl", "pluginClass",
            "projectEnvironment.project.name", "projectEnvironment.environment.name",
            "projectEnvironment.homepageUrl" };

    private final static String[] tagFields = { "tags", "projectEnvironment.project.tags",
            "projectEnvironment.environment.tags" };

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public void createOrUpdate(final Instance instance) {
        if (instance.getId() == null) {
            instance.setCreationDate(new Date());
            entityManager.persist(instance);
        } else {
            entityManager.merge(instance);
        }
    }

    public List<Instance> getInstancesForProjectAndEnvironment(final Long projectId, final Long environmentId) {
        //@formatter:off
        return entityManager
                .createQuery( //
                        "FROM Instance ice " + 
                        "WHERE ice.projectEnvironment.environment.id = :environmentId "+ 
                        "AND ice.projectEnvironment.project.id = :projectId", Instance.class)
                .setParameter("projectId", projectId).setParameter("environmentId", environmentId)
                .setHint("org.hibernate.cacheable", true).getResultList();
        //@formatter:on

    }

    public List<Instance> getInstances() {
        return entityManager.createQuery("FROM Instance", Instance.class).setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    public Optional<Instance> getInstance(final Long instanceId) {
        return Optional.ofNullable(entityManager.find(Instance.class, instanceId));

    }
    
    @Override
    public List<Instance> getInstances(FilterDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        CriteriaQuery<Instance> cq = cb.createQuery(Instance.class);
        Root<Instance> rootClass = cq.from(Instance.class);
        // cq.select(cb.tuple(rootClass.get("id")));

        List<Predicate> andPredicates = new ArrayList<>();
        addOptionalPredicateIfPresent(andPredicates, createOrLike(Arrays
                .asList(StringUtils.split(StringUtils.defaultString(filter.getSearch()))), cb, rootClass, searchFields));
        addOptionalPredicateIfPresent(andPredicates, createOrLike(Arrays
                .asList(StringUtils.split(StringUtils.defaultString(filter.getTags()))), cb, rootClass, tagFields));
        addOptionalPredicateIfPresent(andPredicates, createOrEquals(filter.getProjects(), cb, rootClass, "projectEnvironment.project.id"));
        addOptionalPredicateIfPresent(andPredicates, createOrEquals(filter.getEnvironments(), cb, rootClass, "projectEnvironment.environment.id"));
        addOptionalPredicateIfPresent(andPredicates, createOrEquals(filter.getStateResolvers(), cb, rootClass, "pluginClass"));
        cq.where(andPredicates.toArray(new Predicate[] {}));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Instance> getInstances(Long filterId) {
          //@formatter:off
        return entityManager
                .createQuery(
                        "FROM Instance " + 
                        "WHERE id in (" + 
                        "	SELECT fi.instance.id " + 
                        "	FROM FilterInstance fi " + 
                        "	WHERE fi.filter.id = :filterId" + 
                        ")", Instance.class)
                .setParameter("filterId", filterId).getResultList();
        //@formatter:on
    }

    private void addOptionalPredicateIfPresent(List<Predicate> predicates, Optional<Predicate> optPredicate) {
        if (optPredicate.isPresent()) {
            predicates.add(optPredicate.get());
        }
    }

    private Optional<Predicate> createOrLike(List<String> tokens, CriteriaBuilder cb, Root<Instance> rootClass,
            String... fields) {

        if (tokens == null || tokens.isEmpty() || fields == null || fields.length == 0) {
            return Optional.empty();
        }

        List<Predicate> likePredicates = tokens.stream()
                .map(token -> Arrays.stream(fields).map(f -> cb.like(getLeaf(rootClass, f), "%" + token + "%")).collect(Collectors.toList()))
                .flatMap(a -> a.stream()).collect(Collectors.toList());

        return Optional.of(cb.or(likePredicates.toArray(new Predicate[] {})));

    }

    private Optional<Predicate> createOrEquals(List<?> objects, CriteriaBuilder cb, Root<Instance> rootClass,
            String... fields) {
        if (objects == null || objects.isEmpty() || fields == null || fields.length == 0) {
            return Optional.empty();
        }

        List<Predicate> equalsPredicates = objects.stream()
                .map(o -> Arrays.stream(fields).map(f -> cb.equal(getLeaf(rootClass, f), o)).collect(Collectors.toList()))
                .flatMap(a -> a.stream()).collect(Collectors.toList());

        if (equalsPredicates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(cb.or(equalsPredicates.toArray(new Predicate[] {})));
    }

    private <X, Y> Path<Y> getLeaf(Root<X> root, String path) {
        Path<Y> result = null;
        String[] elements = StringUtils.split(path, ".");
        for (int i = 0; i < elements.length; i++) {
            String element = elements[i];
            if (i == 0) {
                result = root.get(element);
            } else {
                result = result.get(element);
            }
        }
        return result;
    }

    public void delete(final Long instanceId) {
        entityManager.remove(entityManager.find(Instance.class, instanceId));

    }

    public List<Instance> getInstancesForProjectAndEnvironment(final String projectName, final String environmentName) {

        return entityManager
                .createQuery( //
                "FROM Instance ice " + "WHERE ice.projectEnvironment.environment.name = :environmentName "
                        + "AND ice.projectEnvironment.project.name = :projectName", Instance.class)
                .setParameter("projectName", StringUtils.upperCase(projectName))
                .setParameter("environmentName", StringUtils.upperCase(environmentName))
                .setHint("org.hibernate.cacheable", true).getResultList();

    }

    public List<Instance> getInstancesForProjectEnvironment(final Long projectEnvironmentId) {

        return entityManager
                .createQuery( //
                "FROM Instance ice " + "WHERE ice.projectEnvironment.id = :projectEnvironmentId ", Instance.class)
                .setParameter("projectEnvironmentId", projectEnvironmentId).setHint("org.hibernate.cacheable", true)
                .getResultList();

    }

    @Override
    public List<Instance> getInstancesForEnvironment(Long environmentId) {

        return entityManager
                .createQuery( //
                "FROM Instance ice " + "WHERE ice.projectEnvironment.environment.id = :environmentId ", Instance.class)
                .setParameter("environmentId", environmentId).setHint("org.hibernate.cacheable", true).getResultList();

    }

    @Override
    public Optional<Instance> getInstanceByReference(String reference) {
        try {

            return Optional.of(entityManager
                    .createQuery( //
                    "FROM Instance ice " + "WHERE ice.reference = :reference", Instance.class)
                    .setParameter("reference", reference).setHint("org.hibernate.cacheable", true).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("More then one result found for reference [" + reference + "]", e);
        }

    }



}
