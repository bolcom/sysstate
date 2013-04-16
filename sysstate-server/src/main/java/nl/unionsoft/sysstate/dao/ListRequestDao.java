package nl.unionsoft.sysstate.dao;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ConverterWithConfig;
import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;

public interface ListRequestDao {
    public <TARGET, SOURCE, CONFIG> ListResponse<TARGET> getResults(Class<SOURCE> entityClass, ListRequest listRequest, ConverterWithConfig<TARGET, SOURCE, CONFIG> converter, CONFIG config);

    public <TARGET, SOURCE> ListResponse<TARGET> getResults(Class<SOURCE> entityClass, ListRequest listRequest, Converter<TARGET, SOURCE> converter);

    public <T> ListResponse<T> getResults(Class<T> entityClass, ListRequest listRequest);
}
