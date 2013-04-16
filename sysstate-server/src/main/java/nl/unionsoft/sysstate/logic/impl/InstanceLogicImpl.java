package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.common.list.model.GroupRestriction;
import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.common.list.model.ObjectRestriction;
import nl.unionsoft.common.list.model.Restriction;
import nl.unionsoft.common.list.model.Restriction.Rule;
import nl.unionsoft.common.list.worker.impl.BeanListRequestWorkerImpl;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.dao.ListRequestDao;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;
import nl.unionsoft.sysstate.domain.State;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.queue.FetchStateWorker;
import nl.unionsoft.sysstate.queue.ReferenceWorker;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("instanceLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class InstanceLogicImpl implements InstanceLogic, ApplicationContextAware {

    // private static final Logger LOG = LoggerFactory.getLogger(InstanceLogicImpl.class);

    @Inject
    @Named("instanceDao")
    private InstanceDao instanceDao;

    @Inject
    @Named("listRequestDao")
    private ListRequestDao listRequestDao;

    @Inject
    @Named("stateDao")
    private StateDao stateDao;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("referenceWorker")
    private ReferenceWorker referenceWorker;
    @Inject
    @Named("instanceConverter")
    private Converter<InstanceDto, Instance> instanceConverter;

    @Inject
    @Named("stateConverter")
    private Converter<StateDto, State> stateConverter;

    private ApplicationContext applicationContext;

    public void handleUpdates() {
        final List<Instance> instances = instanceDao.getExpiredInstances();
        for (final Instance instance : instances) {

            int refrehTimeout = instance.getRefreshTimeout();
            if (refrehTimeout == 0) {
                refrehTimeout = 60000;
            }
            final DateTime nextUpdate = new DateTime().plusMillis(refrehTimeout);
            instance.setNextUpdate(nextUpdate.toDate());
            instanceDao.createOrUpdate(instance);

            final FetchStateWorker instanceEnvironmentWorker = createInstanceWorker(instance);

            referenceWorker.enqueue(instanceEnvironmentWorker);
        }
    }

    public void queueForUpdate(final Long instanceId) {
        final Instance instance = instanceDao.getInstance(instanceId);
        if (instance != null) {
            int refrehTimeout = instance.getRefreshTimeout();
            if (refrehTimeout == 0) {
                refrehTimeout = 60000;
            }
            final DateTime nextUpdate = new DateTime().plusMillis(refrehTimeout);
            instance.setNextUpdate(nextUpdate.toDate());
            instanceDao.createOrUpdate(instance);

            final FetchStateWorker instanceEnvironmentWorker = createInstanceWorker(instance);
            referenceWorker.enqueue(instanceEnvironmentWorker);
        }
    }

    private FetchStateWorker createInstanceWorker(final Instance instance) {
        InstanceDto instanceDto = instanceConverter.convert(instance);
        setLastStatesForInstance(instanceDto);
        final FetchStateWorker instanceEnvironmentWorker = new FetchStateWorker(instanceDto);
        return instanceEnvironmentWorker;
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    public List<InstanceDto> getInstances() {
        return ListConverter.convert(instanceConverter, instanceDao.getInstances());
    }

    public InstanceDto getInstance(final Long instanceId) {
        return getInstance(instanceId, false);
    }

    public InstanceDto getInstance(Long instanceId, boolean states) {

        InstanceDto result = instanceConverter.convert(instanceDao.getInstance(instanceId));
        if (states) {
            setLastStatesForInstance(result);
        }
        return result;

    }

    private void setLastStatesForInstance(InstanceDto instance) {
        if (instance != null && instance.getId() != null) {
            Long instanceId = instance.getId();
            instance.setState(stateConverter.convert(stateDao.getLastStateForInstance(instanceId)));
            instance.setLastStable(stateConverter.convert(stateDao.getLastStateForInstance(instanceId, StateType.STABLE)));
            instance.setLastUnstable(stateConverter.convert(stateDao.getLastStateForInstance(instanceId, StateType.UNSTABLE)));
            instance.setLastError(stateConverter.convert(stateDao.getLastStateForInstance(instanceId, StateType.ERROR)));
            instance.setLastPending(stateConverter.convert(stateDao.getLastStateForInstance(instanceId, StateType.PENDING)));
            instance.setLastDisabled(stateConverter.convert(stateDao.getLastStateForInstance(instanceId, StateType.DISABLED)));
        }
    }

    public void createOrUpdateInstance(final InstanceDto dto) {
        final Instance instance = new Instance();
        instance.setId(dto.getId());
        instance.setConfiguration(dto.getConfiguration());
        instance.setEnabled(dto.isEnabled());
        instance.setHomepageUrl(dto.getHomepageUrl());
        instance.setName(dto.getName());
        final DateTime nextUpdate = dto.getNextUpdate();
        if (nextUpdate != null) {
            instance.setNextUpdate(nextUpdate.toDate());
        }
        instance.setPluginClass(dto.getPluginClass());
        final ProjectEnvironment projectEnvironment = new ProjectEnvironment();
        projectEnvironment.setId(dto.getProjectEnvironment().getId());
        instance.setProjectEnvironment(projectEnvironment);
        instance.setRefreshTimeout(dto.getRefreshTimeout());
        instance.setTags(dto.getTags());
        instanceDao.createOrUpdate(instance);
    }

    public void delete(final Long instanceId) {
        instanceDao.delete(instanceId);
    }

    public List<InstanceDto> getInstancesForPrefixes(String projectPrefix, String environmentPrefix) {
        return ListConverter.convert(instanceConverter, instanceDao.getInstancesForPrefixes(projectPrefix, environmentPrefix));
    }

    public ListResponse<InstanceDto> getInstances(ListRequest listRequest) {
        final ListResponse<InstanceDto> listResponse = listRequestDao.getResults(Instance.class, listRequest, instanceConverter);
        for (final InstanceDto instance : listResponse.getResults()) {
            instance.setState(stateConverter.convert(stateDao.getLastStateForInstance(instance.getId())));
        }
        return listResponse;
    }

    public ListResponse<InstanceDto> getInstances(FilterDto filter) {
        final ListResponse<InstanceDto> listResponse = handleFilterData(filter);
        handleInstancesFilter(filter, listResponse);
        return listResponse;
    }

    private void handleInstancesFilter(FilterDto filter, ListResponse<InstanceDto> listResponse) {
        final List<?> results = listResponse.getResults();
        final BeanListRequestWorkerImpl beanListRequestWorkerImpl = new BeanListRequestWorkerImpl() {
            @Override
            @SuppressWarnings({ "unchecked", "hiding" })
            public <Object> List<Object> fetchData(Class<Object> dtoClass, ListRequest listRequest) {
                return (List<Object>) results;
            }
        };

        final List<Restriction> restrictions = new ArrayList<Restriction>();
        {
            final List<StateType> states = filter.getStates();
            if (states != null && states.size() > 0) {
                final GroupRestriction groupRestriction = new GroupRestriction(Rule.OR);
                final List<Restriction> orReestrictions = groupRestriction.getRestrictions();
                for (final StateType state : states) {
                    orReestrictions.add(new ObjectRestriction(Rule.EQ, "state.state", state));
                }
                restrictions.add(groupRestriction);
            }
        }
        if (restrictions.size() > 0) {
            beanListRequestWorkerImpl.restrictions(listResponse.getResults(), restrictions);
        }
    }

    private ListResponse<InstanceDto> handleFilterData(FilterDto filter) {
        final ListRequest listRequest = new ListRequest();
        // listRequest.addSort(new Sort("last.state", Direction.ASC));
        listRequest.setFirstResult(0);
        listRequest.setMaxResults(ListRequest.ALL_RESULTS);
        // if (StringUtils.isNotEmpty(sort)) {
        // listRequest.addSort(new Sort(sort));
        // }

        final String search = filter.getSearch();
        if (StringUtils.isNotEmpty(search)) {
            final GroupRestriction groupRestriction = new GroupRestriction(Rule.OR);
            final List<Restriction> restrictions = groupRestriction.getRestrictions();
            for (final String element : StringUtils.split(search, ' ')) {
                restrictions.add(new ObjectRestriction(Rule.LIKE, "tags", element));
                restrictions.add(new ObjectRestriction(Rule.LIKE, "name", element));
                restrictions.add(new ObjectRestriction(Rule.LIKE, "homepageUrl", element));
                restrictions.add(new ObjectRestriction(Rule.LIKE, "configuration", element));
                restrictions.add(new ObjectRestriction(Rule.LIKE, "pluginClass", element));
                restrictions.add(new ObjectRestriction(Rule.LIKE, "projectEnvironment.project.name", element));
                restrictions.add(new ObjectRestriction(Rule.LIKE, "projectEnvironment.environment.name", element));
                restrictions.add(new ObjectRestriction(Rule.LIKE, "projectEnvironment.homepageUrl", element));
            }
            listRequest.addRestriction(groupRestriction);
        }

        final String tags = filter.getTags();
        if (StringUtils.isNotEmpty(tags)) {
            final GroupRestriction groupRestriction = new GroupRestriction(Rule.OR);
            final List<Restriction> restrictions = groupRestriction.getRestrictions();
            for (final String element : StringUtils.split(tags, ' ')) {
                restrictions.add(new ObjectRestriction(Rule.LIKE, "tags", element));
            }
            listRequest.addRestriction(groupRestriction);
        }

        final List<Long> projects = filter.getProjects();
        if (projects != null && projects.size() > 0) {
            final GroupRestriction groupRestriction = new GroupRestriction(Rule.OR);
            final List<Restriction> restrictions = groupRestriction.getRestrictions();
            for (final Long project : projects) {
                restrictions.add(new ObjectRestriction(Rule.EQ, "projectEnvironment.project.id", project));
            }

            listRequest.addRestriction(groupRestriction);

        }

        final List<Long> environments = filter.getEnvironments();
        if (environments != null && environments.size() > 0) {

            final GroupRestriction groupRestriction = new GroupRestriction(Rule.OR);
            final List<Restriction> restrictions = groupRestriction.getRestrictions();
            for (final Long environment : environments) {
                restrictions.add(new ObjectRestriction(Rule.EQ, "projectEnvironment.environment.id", environment));
            }
            listRequest.addRestriction(groupRestriction);

        }

        final List<String> stateResolvers = filter.getStateResolvers();
        if (stateResolvers != null && stateResolvers.size() > 0) {
            final GroupRestriction groupRestriction = new GroupRestriction(Rule.OR);
            final List<Restriction> restrictions = groupRestriction.getRestrictions();
            for (final String stateResolver : stateResolvers) {
                restrictions.add(new ObjectRestriction(Rule.EQ, "pluginClass", stateResolver));
            }
            listRequest.addRestriction(groupRestriction);
        }

        return getInstances(listRequest);
    }

}
