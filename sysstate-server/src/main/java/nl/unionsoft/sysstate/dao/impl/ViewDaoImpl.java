package nl.unionsoft.sysstate.dao.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.dao.ViewDao;
import nl.unionsoft.sysstate.domain.Filter;
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

    @Inject
    @Named("viewConverter")
    private Converter<ViewDto, View> viewConverter;

    public void createOrUpdateView(ViewDto viewDto) {

        View view = null;
        final Long viewId = viewDto.getId();
        if (viewId != null) {
            view = entityManager.find(View.class, viewId);
        } else {
            view = new View();
        }
        view.setName(viewDto.getName());
        view.setCommonTags(viewDto.getCommonTags());
        view.setTemplate(entityManager.find(Template.class, viewDto.getTemplate().getId()));

        Filter filter = null;
        if (viewDto.getFilter() != null && viewDto.getFilter().getId() != null) {
            filter = entityManager.find(Filter.class, viewDto.getFilter().getId());
        }
        view.setFilter(filter);
        if (viewId == null) {
            entityManager.persist(view);
        }

    }

    public void delete(Long viewId) {
        entityManager.remove(entityManager.find(View.class, viewId));
    }

    public ViewDto getView(Long viewId) {
        return viewConverter.convert(entityManager.find(View.class, viewId));
    }
}
