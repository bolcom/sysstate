package nl.unionsoft.sysstate.logic.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.dao.ListRequestDao;
import nl.unionsoft.sysstate.dao.ViewDao;
import nl.unionsoft.sysstate.domain.View;
import nl.unionsoft.sysstate.logic.ViewLogic;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("viewLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ViewLogicImpl implements ViewLogic {

    @Inject
    @Named("listRequestDao")
    private ListRequestDao listRequestDao;

    @Inject
    @Named("viewDao")
    private ViewDao viewDao;

    @Inject
    @Named("viewConverter")
    private Converter<ViewDto, View> viewConverter;

    public ListResponse<ViewDto> getViews(ListRequest listRequest) {
        return listRequestDao.getResults(View.class, listRequest, viewConverter);
    }

    public List<ViewDto> getViews() {
        final ListRequest listRequest = new ListRequest();
        return listRequestDao.getResults(View.class, listRequest, viewConverter).getResults();
    }

    public void createOrUpdateView(ViewDto viewDto) {
        viewDao.createOrUpdateView(viewDto);
    }

    public void delete(Long id) {
        viewDao.delete(id);

    }

    public ViewDto getView(Long viewId) {
        return viewDao.getView(viewId);
    }

}
