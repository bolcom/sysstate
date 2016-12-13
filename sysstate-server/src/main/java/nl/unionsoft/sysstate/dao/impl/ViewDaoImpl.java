package nl.unionsoft.sysstate.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.dao.ViewDao;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.domain.View;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("viewDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ViewDaoImpl implements ViewDao {
    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public void createOrUpdateView(View view) {

        if (view.getId() != null && view.getId() != 0) {
            entityManager.merge(view);
        } else {
            entityManager.persist(view);
        }
    }

    public void delete(Long viewId) {
        entityManager.remove(entityManager.find(View.class, viewId));
    }

    public Optional<View> getView(Long viewId) {
        return Optional.ofNullable(entityManager.find(View.class, viewId));
    }

    @Override
    public List<View> getViews() {
        return entityManager.createQuery("FROM View", View.class).setHint("org.hibernate.cacheable", true).getResultList();
    }

    public Optional<View> getView(String name) {
        try {
            return Optional.of(entityManager.createQuery("FROM View view WHERE view.name = :name", View.class)
                    .setHint("org.hibernate.cacheable", true)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }

    }

    @Override
    public void notifyRequested(String name, Long requestTime) {
        Optional<View> optView = getView(name);
        if (!optView.isPresent()){
            return;
        }
        View view = optView.get();
        long averageRequestTime = ((view.getAverageRequestTime() * view.getRequestCount()) + requestTime) / (view.getRequestCount() + 1);
        long queryCount = view.getRequestCount() + 1;

        view.setLastRequestDate(new Date());
        view.setRequestCount(queryCount);
        view.setAverageRequestTime(averageRequestTime);
        view.setLastRequestTime(requestTime);
        entityManager.merge(view);

    }
}
