package nl.unionsoft.sysstate.logic.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ConverterWithConfig;
import nl.unionsoft.sysstate.common.dto.CountDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.dao.EnvironmentDao;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.dao.ListRequestDao;
import nl.unionsoft.sysstate.dao.ProjectDao;
import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;
import nl.unionsoft.sysstate.domain.State;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.ProjectEnvironmentLogic;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service("projectEnvironmentLogic")
public class ProjectEnvironmentLogicImpl implements ProjectEnvironmentLogic {

    @Inject
    @Named("listRequestDao")
    private ListRequestDao listRequestDao;

    @Inject
    @Named("filterLogic")
    private FilterLogic filterLogic;

    @Inject
    @Named("projectEnvironmentDao")
    private ProjectEnvironmentDao projectEnvironmentDao;

    @Inject
    @Named("projectDao")
    private ProjectDao projectDao;

    @Inject
    @Named("environmentDao")
    private EnvironmentDao environmentDao;

    @Inject
    @Named("instanceDao")
    private InstanceDao instanceDao;

    @Inject
    @Named("stateDao")
    private StateDao stateDao;

    @Inject
    @Named("stateConverter")
    private Converter<StateDto, State> stateConverter;

    @Inject
    @Named("instanceConverter")
    private Converter<InstanceDto, Instance> instanceConverter;

    @Inject
    @Named("projectEnvironmentConverter")
    private ConverterWithConfig<ProjectEnvironmentDto, ProjectEnvironment, Boolean> projectEnvironmentConverter;

    public void createOrUpdate(ProjectEnvironment projectEnvironment) {
        projectEnvironmentDao.createOrUpdate(projectEnvironment);
    }

    public Long createIfNotExists(Long projectId, Long environmentId) {
        ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(projectId, environmentId);
        if (projectEnvironment == null) {
            projectEnvironment = new ProjectEnvironment();
            projectEnvironment.setProject(projectDao.getProject(projectId));
            projectEnvironment.setEnvironment(environmentDao.getEnvironment(environmentId));
            projectEnvironmentDao.createOrUpdate(projectEnvironment);
        }
        return projectEnvironment.getId();
    }

    public ProjectEnvironment getProjectEnvironment(Long projectId, Long environmentId) {
        ProjectEnvironment projectEnvironment = projectEnvironmentDao.getProjectEnvironment(projectId, environmentId);
        if (projectEnvironment == null) {
            projectEnvironment = new ProjectEnvironment();
            projectEnvironment.setProject(projectDao.getProject(projectId));
            projectEnvironment.setEnvironment(environmentDao.getEnvironment(environmentId));
        }
        return projectEnvironment;
    }

    public List<ProjectEnvironment> getProjectEnvironments() {
        return projectEnvironmentDao.getProjectEnvironments();
    }

    // private void enrichProjectEnvironment(ProjectEnvironmentDto projectEnvironment,
    // ViewDto view) {
    // final CountDto count = projectEnvironment.getCount();
    //
    // final List<InstanceDto> instances = projectEnvironment.getInstances();
    // final List<String> commonDescriptions = new ArrayList<String>();
    // int prjEnvRating = 100;
    // final List<InstanceDto> sourceInstances = new ArrayList<InstanceDto>();
    // String searchTags = null;
    //
    // if (view != null) {
    // FilterDto filter = view.getFilter();
    // if (filter != null) {
    // searchTags = filter.getSearch();
    // }
    //
    // }
    // for (InstanceDto instance : ListConverter.convert(instanceConverter,
    // instanceDao.getInstancesForProjectEnvironment(projectEnvironment.getId()))) {
    // if (StringUtils.isEmpty(searchTags) ||
    // SysStateStringUtils.isTagMatch(instance.getTags(), searchTags)) {
    // sourceInstances.add(instance);
    // }
    // }
    //
    // int instanceCount = 0;
    // for (final InstanceDto instance : sourceInstances) {
    // final StateDto state = getStateForInstance(instance);
    //
    // if (StringUtils.isBlank(projectEnvironment.getHomepageUrl()) ||
    // (!StateType.STABLE.equals(state.getState()) &&
    // !StateType.PENDING.equals(state.getState()))) {
    // projectEnvironment.setHomepageUrl(instance.getHomepageUrl());
    // }
    //
    // instance.setProjectEnvironment(projectEnvironment);
    //
    // final String description = state.getDescription();
    // if (instance.isEnabled()) {
    // if (view != null && SysStateStringUtils.isTagMatch(instance.getTags(),
    // view.getCommonTags()) && !commonDescriptions.contains(description)) {
    // commonDescriptions.add(description);
    // }
    // CountUtil.add(count, state.getState());
    // }
    // final int instanceRating = state.getRating();
    // if (instanceRating >= 0) {
    // instanceCount++;
    // if (instanceRating < prjEnvRating) {
    // prjEnvRating = instanceRating;
    // }
    // }
    // instance.setState(state);
    // instances.add(instance);
    // }
    //
    // if (instanceCount > 0) {
    // projectEnvironment.setRating(prjEnvRating);
    // } else {
    // // No (enabled) instances
    // projectEnvironment.setRating(-1);
    // }
    //
    // if (commonDescriptions.size() == 1) {
    // projectEnvironment.setDescription(commonDescriptions.get(0));
    // }
    // projectEnvironment.setState(stateTypeForCount(count));
    // }

    private StateDto getStateForInstance(InstanceDto instance) {
        StateDto state = stateConverter.convert(stateDao.getLastStateForInstance(instance.getId()));
        if (state == null) {
            state = new StateDto();
            state.setState(StateType.PENDING);
            state.setCreationDate(new DateTime());
        }
        return state;
    }

    private StateType stateTypeForCount(CountDto count) {
        StateType result = StateType.DISABLED;
        if (count.getError() > 0) {
            result = StateType.ERROR;
        } else if (count.getUnstable() > 0) {
            result = StateType.UNSTABLE;
        } else if (count.getDisabled() > 0) {
            result = StateType.DISABLED;
        } else if (count.getStable() > 0) {
            result = StateType.STABLE;
        } else if (count.getPending() > 0) {
            result = StateType.PENDING;
        }
        return result;
    }

    // @formatter:off
    // @Cacheable(cacheName="projectEnvironmentCache", keyGenerator = @KeyGenerator (
    // name = "ListCacheKeyGenerator",
    // properties = {@Property( name="useReflection", value="true" ),@Property(
    // name="checkforCycles", value="true" ),@Property( name="includeMethod",
    // value="false" )}
    // ))
    // @formatter:on
    // public ProjectEnvironmentDto getProjectEnvironmentDto(Long projectId, Long
    // environmentId, ViewDto view) {
    // final ProjectEnvironmentDto projectEnvironment =
    // projectEnvironmentConverter.convert(getProjectEnvironment(projectId,
    // environmentId), true);
    // enrichProjectEnvironment(projectEnvironment, view);
    // return projectEnvironment;
    // }
    //
    // public ListResponse<ProjectEnvironmentDto> getProjectEnvironments(ListRequest
    // listRequest) {
    // final ListResponse<ProjectEnvironmentDto> listResponse =
    // listRequestDao.getResults(ProjectEnvironment.class, listRequest,
    // projectEnvironmentConverter, true);
    // for (final ProjectEnvironmentDto projectEnvironment : listResponse.getResults()) {
    // enrichProjectEnvironment(projectEnvironment, null);
    // }
    // return listResponse;
    // }

}
