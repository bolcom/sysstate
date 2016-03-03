package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.dao.ListRequestDao;
import nl.unionsoft.sysstate.dao.TemplateDao;
import nl.unionsoft.sysstate.dao.ViewDao;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.domain.View;
import nl.unionsoft.sysstate.logic.TemplateLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
    private TemplateLogic templateLogic;

    @Inject
    private TemplateDao templateDao;

    @Inject
    private FilterDao filterDao;

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

        View view = new View();
        view.setId(viewDto.getId());
        view.setCommonTags(viewDto.getCommonTags());
        view.setName(viewDto.getName());
        Filter filter = null;
        if (viewDto.getFilter() != null && viewDto.getFilter().getId() != null) {
            filter = filterDao.getFilter(viewDto.getFilter().getId());
        }
        view.setFilter(filter);

        Template template = null;
        if (viewDto.getTemplate() != null) {
            Optional<Template> optTemplate = templateDao.getTemplate(viewDto.getTemplate().getName());
            if (optTemplate.isPresent()) {
                template = optTemplate.get();
            }
        }
        view.setTemplate(template);
        viewDao.createOrUpdateView(view);
    }

    public void delete(Long id) {
        viewDao.delete(id);

    }

    public Optional<ViewDto> getView(Long viewId) {
        return OptionalConverter.convert(viewDao.getView(viewId), viewConverter);
    }

    @Override
    public ViewDto getBasicView() {
        ViewDto view = new ViewDto();
        view.setTemplate(templateLogic.getBasicTemplate());
        return view;
    }

    @Override
    public Optional<ViewDto> getView(String viewId) {
        if (NumberUtils.isDigits(viewId)) {
            return getView(Long.valueOf(viewId));
        }
        //@formatter:off
            return getViews()
                    .stream()
                    .filter(v -> StringUtils.equals(normalizeViewId(v.getName()), normalizeViewId(viewId)))
                    .findFirst();
        //@formatter:on        
    }

    private String normalizeViewId(String input) {
        return StringUtils.lowerCase(StringUtils.replaceEach(input, new String[] { " ", "_" }, new String[] { "-","-"}));
    }

}
