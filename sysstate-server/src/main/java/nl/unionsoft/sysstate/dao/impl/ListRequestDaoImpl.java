package nl.unionsoft.sysstate.dao.impl;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ConverterWithConfig;
import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.common.list.worker.ListRequestWorker;
import nl.unionsoft.sysstate.dao.ListRequestDao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("listRequestDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ListRequestDaoImpl implements ListRequestDao {

    @Inject
    @Named("criteriaListRequestWorker")
    private ListRequestWorker listRequestWorker;

    public <TARGET, SOURCE, CONFIG> ListResponse<TARGET> getResults(Class<SOURCE> entityClass, ListRequest listRequest, ConverterWithConfig<TARGET, SOURCE, CONFIG> converter, CONFIG config) {
        return listRequestWorker.getResults(entityClass, listRequest, converter, config);
    }

    public <TARGET, SOURCE> ListResponse<TARGET> getResults(Class<SOURCE> entityClass, ListRequest listRequest, Converter<TARGET, SOURCE> converter) {
        return listRequestWorker.getResults(entityClass, listRequest, converter);
    }

    public <T> ListResponse<T> getResults(Class<T> entityClass, ListRequest listRequest) {
        return listRequestWorker.getResults(entityClass, listRequest);
    }

}
